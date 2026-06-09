# CLAUDE.md

This file provides guidance to Claude Code (claude.ai/code) when working with code in this repository.

## 빌드 / 테스트 명령어

```bash
# 전체 빌드
./gradlew build

# omo-api만 빌드
./gradlew :apps:omo-api:build

# 전체 테스트 실행
./gradlew test

# 특정 모듈 테스트
./gradlew :apps:omo-api:test

# 단일 테스트 클래스 실행
./gradlew :apps:omo-api:test --tests "com.omo.domain.user.UserServiceTest"

# 단일 테스트 메서드 실행
./gradlew :apps:omo-api:test --tests "com.omo.domain.user.UserServiceTest.메서드명"

# 컴파일만 확인 (테스트 포함)
./gradlew :apps:omo-api:compileTestJava
```

테스트 중 DB가 필요한 테스트는 Docker를 통해 Testcontainers(MySQL 8.0)를 자동으로 띄운다. Docker가 실행 중이어야 한다.

## 멀티모듈 구조

```
apps/
  omo-api/       # Spring Boot REST API 서버 (메인)
  omo-batch/     # 배치 처리
  omo-streamer/  # 스트리밍 처리
modules/
  jpa/           # DataSource, JPA/QueryDSL 설정, BaseEntity, Testcontainers fixture
  redis/         # Redis 설정
  kafka/         # Kafka 설정
supports/
  jackson/       # ObjectMapper 설정
  logging/       # 로깅 설정
  monitoring/    # 모니터링(Actuator) 설정
```

`apps/`는 Spring Boot 플러그인이 적용된 실행 가능한 애플리케이션이다. `modules/`는 `api()`로 의존성을 전파하는 공유 라이브러리다. `supports/`는 설정만 담당하는 얇은 모듈이다.

## omo-api 레이어 구조

헥사고날 아키텍처를 따른다. 의존 방향은 항상 `interfaces → application → domain ← infrastructure`다.

| 레이어 | 패키지 | 역할 |
|--------|--------|------|
| Interface | `interfaces/api/` | Controller, Dto, ApiSpec(Swagger), ControllerAdvice |
| Application | `application/` | Facade (오케스트레이션), Port 인터페이스, 결과 record |
| Domain | `domain/` | 엔티티, 도메인 서비스, Repository 인터페이스 |
| Infrastructure | `infrastructure/` | Repository 구현체, 외부 HTTP 클라이언트, JWT |

**포트-어댑터 패턴**: 외부 시스템과의 계약은 `application/` 레이어에 인터페이스로 정의하고, 구현은 `infrastructure/`에 둔다. 예) `SocialAuthClient` (application) ← `GoogleAuthClient` (infrastructure).

## 핵심 규칙

**BaseEntity** (`modules/jpa`): 모든 엔티티의 부모. `id`는 `final Long id = 0L`로 선언되어 있어 **DB 없이 생성한 엔티티의 id는 항상 0L**이다. 단위 테스트에서 getId()에 의존하는 assertion은 mock을 사용해야 한다.

**CoreException**: 모든 도메인 예외는 `CoreException(ErrorType, message)`로 던진다. `ApiControllerAdvice`가 `ErrorType`의 HTTP 상태코드와 에러코드로 변환한다.

**ApiResponse**: 모든 API 응답은 `{ "meta": { "result", "errorCode", "message" }, "data": ... }` 형식이다. 성공 시 `ApiResponse.success(data)`, 실패 시 ControllerAdvice가 자동 처리한다.

**설정 파일**: `application.yml`이 `jpa.yml`, `redis.yml`, `logging.yml`, `monitoring.yml`을 import한다. 프로파일은 `local`(기본), `dev`, `qa`, `prd`가 있다.

## 테스트 전략

| 종류 | 어노테이션/방식 | DB | Spring |
|------|---------------|-----|--------|
| Unit | `@ExtendWith(MockitoExtension.class)` | X | X |
| Integration (Repository) | `@SpringBootTest` + `MySqlTestContainersConfig` | Testcontainers | O |
| Integration (HTTP Client) | `MockRestServiceServer.bindTo(RestClient.Builder)` | X | X |
| E2E | `@SpringBootTest` + 실제 DB + `@MockitoBean` (외부 클라이언트만) | Testcontainers | O |

Spring Boot 4.x에서 `@RestClientTest`와 `@MockBean`이 제거됐다. HTTP 클라이언트 테스트는 Spring 없이 `MockRestServiceServer.bindTo(builder)`를 사용하고, Bean mock은 `@MockitoBean`을 사용한다.

**RestClient 테스트 가능 조건**: 클라이언트가 `RestClient.create()` 대신 생성자에서 `RestClient.Builder`를 주입받아야 `MockRestServiceServer.bindTo(builder)`로 바인딩할 수 있다.

## Git / 커밋 컨벤션

브랜치: `{prefix}/OMO-{number}-{slug}` (예: `feature/OMO-20-social-login-api`)

커밋: `OMO-{number} {type}: {설명(한국어)}` (예: `OMO-20 feat: 소셜 로그인 API 엔드포인트 추가`)

type: `feat` / `fix` / `refactor` / `test` / `chore` / `docs` / `style`

## 문서 위치

- `docs/` — 설계 문서 (아키텍처, ERD, 시퀀스 다이어그램, API 명세 등)
- `docs/implementation/` — 기능별 구현 노트 (왜 이렇게 구현했는지)