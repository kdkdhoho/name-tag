# MCP·플러그인 점검

`node scripts/build-plugin.mjs`는 `.agents/skills`의 9개 스킬을 `plugins/nametag/skills`로 동기화하고, fixture 모드 MCP 설정을 생성한다.

MCP 프로필은 stdio에서 JSON-RPC만 반환한다. 도구는 `search_animals`, `match`, `shelter_questions`, `settle_plan`, `evaluate_match`다.

플러그인 설치 전에는 `./gradlew bootJar`로 jar를 먼저 만든다.
