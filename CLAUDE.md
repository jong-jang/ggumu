# CLAUDE.md — ggumu 프로젝트 지침

## 기술 스택
- Java 21, Spring Boot 4.0.4
- MySQL 8 (Docker), Flyway (마이그레이션)
- JPA/Hibernate, Spring Security, Spring Validation
- Lombok

## 브랜치 전략
- `main` — 프로덕션 브랜치 (직접 커밋 금지)
- `dev` — 개발 통합 브랜치
- 기능 개발은 `dev`에서 작업 후 PR로 `main`에 머지

## 커밋 컨벤션
Conventional Commits 규칙을 따른다.

```
feat: 새로운 기능
fix: 버그 수정
refactor: 리팩토링 (기능 변경 없음)
chore: 빌드/설정/의존성 변경
docs: 문서 수정
test: 테스트 코드
```

커밋 메시지에 `Co-Authored-By` 등 출처 표기는 하지 않는다.

## 패키지 구조
```
com.ggumu.server
├── common
│   ├── response    # ApiResponse
│   └── exception   # GlobalExceptionHandler
└── (도메인별 패키지)
```
> 기존 `com.jjh.ggumu`는 GgumuApplication 엔트리포인트만 위치. 신규 코드는 모두 `com.ggumu.server` 하위에 작성한다.

## API 응답 형식
모든 API 응답은 `ApiResponse<T>`를 사용한다.

```java
ApiResponse.ok(data);     // 성공 + 데이터
ApiResponse.ok();          // 성공 (데이터 없음)
ApiResponse.error("msg");  // 실패
```

## DB 마이그레이션
- Flyway 사용, 파일 위치: `src/main/resources/db/migration/`
- 네이밍: `V{버전}__{설명}.sql` (예: `V2__add_user_profile.sql`)
- 스키마 변경은 반드시 마이그레이션 파일로 관리, JPA `ddl-auto` 직접 수정 금지

## 민감정보 관리
- 비밀번호 등 민감정보는 `.env`에 정의
- `.env`와 `application-local.yml`은 `.gitignore`에 등록되어 있으므로 커밋하지 않는다
- 새 환경변수 추가 시 `.env.example`에도 키만 추가한다

## 로컬 실행
```bash
# MySQL 컨테이너 실행
docker compose up -d

# 앱 실행 (active profile: local)
./gradlew bootRun
```
