# 데모 리허설 기록

## 2026-09-04 fixture 리허설

- `NAMETAG_USE_FIXTURE=1 java -jar build/libs/jeongchak-0.0.1-SNAPSHOT.jar --server.port=8080`로 서버를 기동했다.
- `POST /api/v1/matches`의 기본 요청은 추천 카드 4장을 반환했다.
- 같은 요청의 `tenure: RENT_DENIED`는 `landlordDenied: true`를 반환했다.
- 첫 추천 카드에는 보호소 전화번호 `02-111-1111`이 포함됐다.
- `POST /api/v1/settle-plans`는 5개 일자별 항목을 반환했다.
- `./gradlew test smoke`, `node scripts/check.mjs`, `web/npm run build`를 통과했다.

## 현장 재현

터미널 A에서 fixture 서버를, 터미널 B에서 `cd web && npm run dev`를 실행한다. 브라우저에서 질문 제출, 임대인 반대 선택, 정착 플랜 버튼, `tel:` 링크를 차례대로 확인한다.
