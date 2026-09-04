# 테스트 방법

## 자동 테스트

```bash
cd /Users/hylee/swm/codex-impact-workshop/nametag
./gradlew test smoke
node scripts/check.mjs
```

기대 결과는 `REST match: N cards`, `MCP tools: 5/5`, 마지막 줄 `ALL CHECKS PASSED`다. 전체 검사는 fixture 서버를 임시 포트에 직접 기동해 `/api/v1/matches`까지 확인한 후 종료한다.

## 웹 빌드

```bash
cd /Users/hylee/swm/codex-impact-workshop/nametag/web
npm run build
```

기대 결과는 `✓ built`다.

## 수동 데모

터미널 A:

```bash
cd /Users/hylee/swm/codex-impact-workshop/nametag
NAMETAG_USE_FIXTURE=1 java -jar build/libs/nametag-0.0.1-SNAPSHOT.jar
```

터미널 B:

```bash
cd /Users/hylee/swm/codex-impact-workshop/nametag/web
npm run dev
```

`http://localhost:5173`에서 추천 보기, 정착 플랜, 임대인 반대 안내, 카드의 `tel:` 링크를 차례대로 확인한다.
