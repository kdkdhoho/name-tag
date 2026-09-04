# 현장 순서

| 시간 | 작업 | 사람이 확인할 결과 |
| --- | --- | --- |
| 0–10분 | `.env`와 fixture 모드 확인, `git pull --rebase` | 원격 최신, 의존성 준비 |
| 10–40분 | 규칙 엔진 | `./gradlew test smoke`, `CHECK 1 PASSED` |
| 10–60분 | 웹 화면 병렬 작업 | 질문·결과·임대인 안내·정착 플랜 |
| 40–60분 | REST 연결 | fixture 매칭 성공 응답 |
| 60–70분 | MCP 연결 | 도구 5개와 `ALL CHECKS PASSED` |
| 70–90분 | 데모 리허설 | REHEARSAL.md 순서 완료 |
| 90–120분 | 발표·Q&A·푸시 | 최신 main, 발표 1회 완주 |

각 코드 변경 뒤에는 `git status`를 보고, 커밋 직전 및 푸시 직전에 반드시 `git pull --rebase origin main`을 실행한다.
