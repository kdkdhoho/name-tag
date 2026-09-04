# 정착 검증 체크리스트

- [x] 규칙 엔진: `./gradlew test smoke`
- [x] 페르소나 20/20 및 하드 필터·플래그·보호종료 위반 0건
- [x] `node scripts/check.mjs --quick`
- [ ] REST fixture 모드 수동 확인
- [ ] MCP 도구 5개 수동 확인
- [ ] 웹 네 화면 확인

성공 조건: 단위 테스트, 20명 스모크, 근거 없는 이유 0, 하드 필터 위반 0, GOOD 플래그 0, 보호 종료 추천 0, 매칭 REST 성공, MCP 5개 도구 응답.
