# 정착

유기동물 입양 매칭 데모입니다. 백엔드는 기본적으로 로컬 픽스처 데이터를 사용하며, 프론트 개발 서버는 `/api` 요청을 백엔드(`http://localhost:8080`)로 전달합니다.

## 백엔드 실행

Java 17이 필요합니다. 실제 국가동물보호정보시스템 API를 사용하려면 `DATA_GO_KR_SERVICE_KEY`를 설정한 뒤, 저장소 루트에서 픽스처 모드를 끄고 실행합니다.

```bash
NAMETAG_USE_FIXTURE=false ./gradlew bootRun
```

예를 들어 현재 터미널에 서비스 키를 설정해 실행할 수 있습니다.

```bash
DATA_GO_KR_SERVICE_KEY=발급받은_서비스_키 NAMETAG_USE_FIXTURE=false ./gradlew bootRun
```

개발용 픽스처 모드는 환경 변수를 생략하고 실행합니다.

```bash
./gradlew bootRun
```

## 프론트 실행

Node.js를 준비한 뒤 별도 터미널에서 실행합니다.

```bash
cd web
npm install
npm run dev
```

Vite 개발 서버 주소는 터미널에 출력됩니다. 기본 주소는 `http://localhost:5173`이며, API 요청은 `http://localhost:8080`의 백엔드로 프록시됩니다.
