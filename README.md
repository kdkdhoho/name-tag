# 정착

보호 중인 개와 입양 희망자의 생활 조건을 함께 살펴보는 입양 매칭 데모입니다. 주거 조건, 부재 시간, 동거인, 돌봄 경험과 기대 기간을 입력하면 조건을 통과한 동물을 추천하고, 확인이 필요한 조건과 보호소에 물어볼 질문을 함께 보여 줍니다. 추천 뒤에는 첫 2주 정착 계획도 확인할 수 있습니다.

기본 실행은 저장소의 fixture 데이터만 사용합니다. fixture 모드에서는 외부 네트워크를 호출하지 않아, 서비스 키 없이도 데모와 테스트를 재현할 수 있습니다.

## 구성

- **백엔드**: Java 17, Spring Boot
- **웹**: React, Vite
- **데이터**: 국가동물보호정보시스템 API v2 또는 로컬 fixture
- **연동**: REST API와 stdio 기반 MCP 서버

매칭의 판정과 점수 규칙은 `src/main/java/com/nametag/domain/match/model/Rules.java`의 `Rules.ALL`에만 정의합니다. 이유 문장은 규칙의 템플릿을 우선 사용하며, 외부 LLM 키가 없어도 동작합니다.

## 빠른 시작

필수 환경은 Java 17과 Node.js입니다.

터미널 1에서 백엔드를 실행합니다. 환경 변수를 생략하면 fixture 모드로 기동합니다.

```bash
./gradlew bootRun
```

터미널 2에서 웹을 실행합니다.

```bash
cd web
npm install
npm run dev
```

브라우저에서 Vite가 출력한 주소(기본 `http://localhost:5173`)를 열면 됩니다. 개발 서버는 `/api` 요청을 백엔드 `http://localhost:8080`으로 프록시합니다.

## 실제 공공 API 사용

실제 데이터를 조회하려면 국가동물보호정보시스템 API 서비스 키를 설정하고 fixture 모드를 끕니다. 공공 API는 v2 엔드포인트만 사용합니다.

```bash
DATA_GO_KR_SERVICE_KEY=발급받은_서비스_키 \
NAMETAG_USE_FIXTURE=false \
./gradlew bootRun
```

설정값은 환경 변수 또는 저장소 루트의 `.env`에서 지정할 수 있습니다. 예시는 [.env.example](.env.example)에 있습니다.

| 변수 | 기본값 | 설명 |
| --- | --- | --- |
| `NAMETAG_USE_FIXTURE` | `true` | `true`면 로컬 fixture만 사용합니다. |
| `NAMETAG_FIXTURE_DIR` | `./fixtures` | fixture 파일이 있는 디렉터리입니다. |
| `DATA_GO_KR_SERVICE_KEY` | 빈 값 | 공공 API 호출에 사용할 서비스 키입니다. |
| `NAMETAG_PUBLIC_API_BASE_URL` | 국가동물보호정보시스템 v2 주소 | API 기본 주소를 바꿀 때 사용합니다. |

## REST API

모든 응답은 `success`, `message`, `data`, `timestamp`를 포함하는 공통 형식입니다. 백엔드 기본 주소는 `http://localhost:8080`입니다.

| 메서드 | 경로 | 설명 |
| --- | --- | --- |
| `GET` | `/api/v1/health` | 현재 fixture 모드와 로드된 동물 수를 확인합니다. |
| `GET` | `/api/v1/regions/sido` | 시·도 목록을 조회합니다. |
| `GET` | `/api/v1/regions/sigungu?uprCd={코드}` | 선택한 시·도의 시·군·구 목록을 조회합니다. |
| `POST` | `/api/v1/matches` | 입양 희망자 프로필에 맞는 추천을 조회합니다. |
| `POST` | `/api/v1/settle-plans` | 프로필 기준 첫 2주 정착 계획을 조회합니다. |
| `GET` | `/api/v1/evaluations` | fixture 기반 매칭 평가 결과를 조회합니다. |

매칭 요청 예시:

```bash
curl -X POST http://localhost:8080/api/v1/matches \
  -H 'Content-Type: application/json' \
  --data @fixtures/match.request.json
```

`POST /api/v1/matches`와 `POST /api/v1/settle-plans`은 아래 필드를 사용합니다. 각 값의 가능한 열거형은 웹의 [`web/src/api.ts`](web/src/api.ts)에 정의되어 있습니다.

```json
{
  "home": "STUDIO",
  "sizeLimit": "UNDER_10",
  "tenure": "OWN",
  "absence": "UNDER_4",
  "housemates": ["ALONE"],
  "activeDays": 3,
  "experience": "NONE",
  "expectation": "WEEKS",
  "sido": "서울",
  "sigungu": "강남구"
}
```

## MCP 서버

MCP 프로필은 표준 입출력에서 JSON-RPC를 처리합니다. 먼저 실행 가능한 JAR를 만듭니다.

```bash
./gradlew bootJar
NAMETAG_USE_FIXTURE=true \
java -jar build/libs/jeongchak-0.0.1-SNAPSHOT.jar --spring.profiles.active=mcp
```

제공 도구는 다음 5개입니다.

| 도구 | 설명 |
| --- | --- |
| `search_animals` | 현재 조회 가능한 보호 동물을 반환합니다. |
| `match` | `profile`로 매칭 결과를 반환합니다. |
| `shelter_questions` | 공고 번호에 따른 보호소 확인 질문을 반환합니다. |
| `settle_plan` | `profile`로 첫 2주 정착 계획을 반환합니다. |
| `evaluate_match` | fixture 기반 평가 결과를 반환합니다. |

Codex 플러그인용 설정과 스킬은 아래 명령으로 동기화할 수 있습니다.

```bash
node scripts/build-plugin.mjs
```

자세한 연결 방법은 [MCP 플러그인 문서](docs/MCP-PLUGIN.md)를 참고하세요.

## 검증

저장소 루트에서 백엔드 테스트와 fixture 기반 빠른 점검을 실행합니다.

```bash
./gradlew test smoke
node scripts/check.mjs --quick
```

REST 응답과 MCP 도구 목록까지 확인하려면 `node scripts/check.mjs`를 실행합니다.

웹 빌드는 별도로 확인합니다.

```bash
cd web
npm run build
```

전체 검증과 수동 데모 절차는 [테스트 문서](docs/TESTING.md)에 정리되어 있습니다.

## 프로젝트 구조

```text
src/main/java/com/nametag/
├── api/          REST API, 공공 API 클라이언트, MCP 실행기
└── domain/       동물 정규화, 매칭, 정착 계획, 평가 로직
fixtures/         네트워크 없이 실행하는 데모·테스트 데이터
web/              React 사용자 화면
docs/             API, 테스트, 발표 및 근거 문서
scripts/          빠른 점검과 MCP 플러그인 생성 스크립트
```

## 참고 문서

- [API 개요](docs/api.md)
- [테스트 방법](docs/TESTING.md)
- [MCP·플러그인 점검](docs/MCP-PLUGIN.md)
- [근거 자료](docs/evidence.md)
