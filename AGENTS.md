# 이름표

- 공공 API는 v2만 사용한다.
- 규칙은 `domain/match/model/Rules.ALL`에만 둔다.
- LLM은 이유 문장만 만들며, 키가 없으면 템플릿을 사용한다.
- fixture 모드에서는 네트워크를 호출하지 않는다.
- Java 17과 테스트 우선 규칙을 지킨다.
- 출처 없는 수치와 품종 성격 단정은 금지한다.

완료: `./gradlew test smoke`, `node scripts/check.mjs --quick`, REST와 MCP 도구 목록, 웹 빌드가 통과해야 한다.
