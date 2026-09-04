#!/usr/bin/env python3
"""2026-09-04-발표-덱-claude.html 정적 검수 (표준 라이브러리만)."""
import io, re, sys
from html.parser import HTMLParser

PATH = "/Users/dongho/team-wiki/raw/personal/Codex-Impact-Workshop/2026-09-04-발표-덱-claude.html"
VOID = {"area","base","br","col","embed","hr","img","input","link","meta","param","source","track","wbr"}
TOKENS = {"#F4EFE6","#FFFDF9","#1E1A16","#6B6259","#8C8478","#D9D0C2","#C8371F","#F6E3DE"}  # --tag/--tag-ink는 중복값

src = io.open(PATH, encoding="utf-8").read()
ok = True
def rep(name, passed, detail=""):
    global ok
    ok = ok and passed
    print(f"[{'PASS' if passed else 'FAIL'}] {name}" + (f" : {detail}" if detail else ""))


class Deck(HTMLParser):
    def __init__(self):
        super().__init__(convert_charrefs=True)
        self.stack, self.errs = [], []
        self.skip = 0            # script/style 깊이
        self.slides = 0
        self.asides = 0
        self.in_aside = 0
        self.screen, self.notes = [], []
        self.cur_slide = -1
        self.slide_depth = None
        self.vermilion = {}      # slide idx -> count
        self.tag_slides = []     # .tag 가 쓰인 슬라이드 (HUD 는 -1)
        self.parts = {}          # slide idx -> {s-head, s-title, s-body, s-foot} 개수
        self.eyebrow = 0
    def handle_starttag(self, tag, attrs):
        a = dict(attrs)
        cls = a.get("class", "").split()
        if tag not in VOID and not self.get_starttag_text().endswith("/>"):
            self.stack.append((tag, self.getpos()))
        if tag in ("script", "style"):
            self.skip += 1
        if "slide" in cls:
            self.slides += 1
            self.cur_slide += 1
            self.slide_depth = len(self.stack)
        if tag == "aside":
            self.asides += 1
            self.in_aside += 1
        if "tag" in cls:
            self.tag_slides.append(self.cur_slide if self.slide_depth is not None and self.cur_slide >= 0 else -1)
        if "eyebrow" in cls:
            self.eyebrow += 1
        for part in ("s-head", "s-title", "s-body", "s-foot"):
            if part in cls and self.cur_slide >= 0:
                self.parts.setdefault(self.cur_slide, {})
                self.parts[self.cur_slide][part] = self.parts[self.cur_slide].get(part, 0) + 1
        v = ({"red","stamp","bad","back","alert"} & set(cls)) or ("num-sub" in cls) or ("ratio" in cls)
        if v and self.cur_slide >= 0:
            n = 2 if ("num-sub" in cls or "ratio" in cls) else 1  # num-sub 2개, ratio 표 인주 셀(자연사·안락사) 2개
            self.vermilion[self.cur_slide] = self.vermilion.get(self.cur_slide, 0) + n
    def handle_endtag(self, tag):
        if tag in ("script", "style"):
            self.skip = max(0, self.skip - 1)
        if tag == "aside":
            self.in_aside = max(0, self.in_aside - 1)
        if self.stack and self.stack[-1][0] == tag:
            self.stack.pop()
        elif tag not in VOID:
            self.errs.append(f"{tag} @line {self.getpos()[0]} (열림: {self.stack[-1][0] if self.stack else '없음'})")
    def handle_data(self, d):
        if self.skip:
            return
        (self.notes if self.in_aside else self.screen).append(d)


p = Deck()
p.feed(src)

print("=" * 68)
print("1. 구조")
print("=" * 68)
rep("슬라이드 요소 16개", p.slides == 16, f"{p.slides}개")
rep("발표자 노트(aside) 16개", p.asides == 16, f"{p.asides}개")

# design.md 6장 슬라이드 공통 구조: 덱 순서 2~15(idx 1~14)는 Header·구분선·Body·Footer 4단
STD = list(range(1, 15))
need = {"s-head": 1, "s-title": 1, "s-body": 1, "s-foot": 1}
missing = {i: p.parts.get(i, {}) for i in STD if p.parts.get(i, {}) != need}
rep("대상 14장에 4단 구조(.s-head/.s-title/.s-body/.s-foot) 각 1개", not missing, f"어긋남: {missing}")
extra = {i: p.parts[i] for i in (0, 15) if i in p.parts}
rep("표지·클로징(덱 1·16)은 4단 구조 없음", not extra, f"발견: {extra}")
rep(".eyebrow 0건(Header 로 대체됨)", p.eyebrow == 0, f"{p.eyebrow}건")

print()
print("=" * 68)
print("2. 화면 텍스트 · 노트")
print("=" * 68)
screen = "".join(p.screen)
notes = "".join(p.notes)
em_s, em_n = screen.count("\u2014"), notes.count("\u2014")
rep("화면 텍스트 em dash 0건", em_s == 0, f"{em_s}건")
rep("발표자 노트 em dash 0건", em_n == 0, f"{em_n}건")
en_all = (screen + notes).count("\u2013")
rep("en dash(–) 0건", en_all == 0, f"{en_all}건")
fill_s, fill_n = screen.count("(채움)"), notes.count("(채움)")
print(f"[INFO] \"(채움)\" 화면 {fill_s}건(12번 4 + 13번 2 = 6 예상) · 노트 {fill_n}건(12·13번 = 2 예상)")

print()
print("=" * 68)
print("3. 수치 존재")
print("=" * 68)
NUMS = ["95,685","262","25.7%","17.3%","25.6%","34.6%","7.1%","479","50만 1천","14.5%","41%",
        "51.6%","30.1%","3.8%","17.9%","42.7%","16.3%","55.9%","85%","24%","78%","054-552-2233"]
missing = [n for n in NUMS if n not in screen]
rep(f"필수 수치 {len(NUMS)}종 전부 화면에 존재", not missing, "누락: " + ", ".join(missing) if missing else "누락 없음")

print()
print("=" * 68)
print("4. 카운터 분모 자동 계산")
print("=" * 68)
auto = "slides.length" in src and re.search(r"counter\.textContent\s*=\s*\(idx\+1\)\+' / '\+slides\.length", src)
rep("counter 는 slides.length 로 계산", bool(auto))
hard = re.findall(r"[>\"']\s*\d+\s*/\s*16\s*[<\"']", src)
rep("하드코딩된 '/ 16' 없음", not hard, str(hard))
rep("삭제 대상 countUp/#count 없음", "countUp" not in src and 'id="count"' not in src)

print()
print("=" * 68)
print("5. 태그 균형 · --dogphoto")
print("=" * 68)
rep("닫히지 않은 태그 없음", not p.stack, str([t for t, _ in p.stack][:8]))
rep("짝 없는 종료 태그 없음", not p.errs, "; ".join(p.errs[:5]))
uses = re.findall(r"var\(--dogphoto\)", src)
where = re.findall(r"\.([a-z-]+)\{[^}]*var\(--dogphoto\)", src)
rep("var(--dogphoto) 사용 2곳", len(uses) == 2, f"{len(uses)}곳 {where}")
rep("--dogphoto 선언 1건(v1 base64 그대로)", src.count("--dogphoto:url(") == 1)
rep("1번 슬라이드 .gov-photo 존재", 'class="gov-photo"' in src)
rep("11번 슬라이드 .ba-photo 존재", 'class="ba-photo"' in src)

print()
print("=" * 68)
print("6. design.md 9장 추가 검수")
print("=" * 68)
css = re.search(r"<style>(.*?)</style>", src, re.S).group(1)
for bad in ["#E2A24A", "box-shadow", "transition", "animation", "linear-gradient"]:
    rep(f"스타일시트에 '{bad}' 0건", bad not in css, f"{css.count(bad)}건")
rep("스타일시트에 '.eyebrow' 규칙 0건", ".eyebrow" not in css)
label = [x for x in ("1부", "2부", "3부", "4부") if x in screen]
rep("화면에 부 라벨 0건", not label, f"발견: {label}")
hexes = {h.upper() for h in re.findall(r"#[0-9A-Fa-f]{6}\b", css)}
rep("색 hex 가 토큰 안에만", hexes <= TOKENS, f"토큰 밖: {sorted(hexes - TOKENS)}")
over = {i: c for i, c in p.vermilion.items() if c > 3}
rep("인주 요소 슬라이드당 3개 이하", not over, f"초과: {over}")
print(f"[INFO] 슬라이드별 인주 요소 추정: {dict(sorted(p.vermilion.items()))}")
rep(".tag 는 11번·14번(덱 순서 12·15)만", sorted(set(p.tag_slides)) == [12, 15], f"사용 위치: {p.tag_slides}")
rep("외부 의존성은 폰트 CDN 링크 하나", len(re.findall(r'<link[^>]+href="https?://', src)) == 1
    and not re.findall(r'<script[^>]+src=', src))
rep("폰트 fallback 지정", css.count('"Apple SD Gothic Neo",sans-serif;') >= 3)
rep("애니메이션 없음(@keyframes)", "@keyframes" not in css)
rep("슬라이드별 시간 표기 없음", not re.search(r"\d:\d\d[^\"]*[–~-]\s*\d:\d\d", screen + notes))

print()
print("=" * 68)
print("7. 기능 키 바인딩")
print("=" * 68)
for k, pat in [("← →", "ArrowLeft"), ("N 노트", "case 'n':"), ("T 타이머", "case 't':"), ("F 전체화면", "requestFullscreen")]:
    rep(f"{k} 바인딩", pat in src)
rep("좌우 가장자리 클릭(.nav 버튼)", 'id="prev"' in src and 'id="next"' in src and ".nav button{position:absolute" in css)

print()
print("=" * 68)
print("결과:", "전부 통과" if ok else "실패 항목 있음")
print("=" * 68)
sys.exit(0 if ok else 1)
