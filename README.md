# 꾸무 (ggumu) — Backend

나만의 루틴을 만들고, 공유하고, 성장하는 루틴 공유 플랫폼 **꾸무**의 백엔드 프로젝트입니다.

---

## 기술 스택

| 분류 | 기술 |
|------|------|
| 언어 | Java 21 |
| 프레임워크 | Spring Boot 4.0.4 |
| 빌드 | Gradle |
| DB | MySQL 8 (Docker) |
| ORM | JPA / Hibernate |
| 마이그레이션 | Flyway |
| 인증 | Spring Security + JWT (jjwt 0.12.6) |
| 소셜 로그인 | 카카오 OAuth2 |
| 캐시/세션 | Redis |
| 스토리지 | AWS S3 |
| 기타 | Lombok, Spring Validation |

---

## 시작하기

### 사전 조건

- Java 21
- Docker & Docker Compose

### 환경변수 설정

`.env.example`을 복사해 `.env` 파일을 생성하고 값을 채워주세요:

```bash
cp .env.example .env
```

### 실행

```bash
# MySQL + Redis 컨테이너 실행
docker compose up -d

# 앱 실행 (active profile: local)
./gradlew bootRun
```

API 서버: `http://localhost:8080`
Swagger UI: `http://localhost:8080/swagger-ui.html`

---

## 프로젝트 구조

```
src/main/java/com/jjh/ggumu/
├── common/
│   ├── response/         # ApiResponse<T> 공통 응답 래퍼
│   └── exception/        # GlobalExceptionHandler
└── domain/
    ├── user/             # 유저 엔티티, 온보딩 API
    ├── auth/             # 카카오 OAuth2, JWT 발급/재발급/로그아웃
    ├── routine/          # 루틴 CRUD API
    └── follow/           # 팔로우 시스템 + 피드 API
```

---

## API 응답 형식

모든 응답은 `ApiResponse<T>` 형태로 반환됩니다.

```json
{ "success": true, "data": { ... } }
{ "success": false, "message": "에러 메시지" }
```

---

## 주요 API

| 메서드 | 경로 | 설명 |
|--------|------|------|
| GET | `/oauth2/authorization/kakao` | 카카오 로그인 시작 |
| POST | `/api/auth/reissue` | 액세스 토큰 재발급 |
| POST | `/api/auth/logout` | 로그아웃 |
| POST | `/api/users/onboarding` | 온보딩 설문 저장 |
| GET | `/api/routines/me` | 내 루틴 목록 |
| POST | `/api/routines` | 루틴 생성 |
| GET | `/api/routines/:id` | 루틴 상세 |
| PUT | `/api/routines/:id` | 루틴 수정 |
| DELETE | `/api/routines/:id` | 루틴 삭제 |
| GET | `/api/routines/feed` | 팔로잉 피드 |
| POST | `/api/follows/:userId` | 팔로우 |
| DELETE | `/api/follows/:userId` | 언팔로우 |

---

## DB 마이그레이션

Flyway를 사용합니다. 마이그레이션 파일 위치: `src/main/resources/db/migration/`

| 버전 | 내용 |
|------|------|
| V1 | users 테이블 생성 |
| V2 | routines / follows 테이블 생성 |
| V3 | routine_items 테이블 생성 |

---

## 인증 흐름

1. 프론트에서 `/oauth2/authorization/kakao` 로 리다이렉트
2. 카카오 인증 완료 → 백엔드가 유저 저장 또는 조회
3. JWT 액세스/리프레시 토큰 발급
4. 프론트 콜백 URL로 토큰 전달 (`?access_token=...&refresh_token=...&is_new_user=...`)
5. 이후 요청은 `Authorization: Bearer {access_token}` 헤더 사용
6. 리프레시 토큰은 Redis에 저장, 만료 시 재발급 불가

---

## 라이선스

[MIT](LICENSE)
