# 소셜 로그인 API 구현 노트

> Google / Kakao / Apple 소셜 토큰을 받아 자체 JWT를 발급하는 로그인 플로우 (OMO-20)

## 구현 파일

**Application Layer**
- `apps/omo-api/src/main/java/com/omo/application/auth/SocialAuthClient.java`
- `apps/omo-api/src/main/java/com/omo/application/auth/SocialUserInfo.java`
- `apps/omo-api/src/main/java/com/omo/application/auth/AuthFacade.java`
- `apps/omo-api/src/main/java/com/omo/application/auth/AuthInfo.java`

**Infrastructure Layer**
- `apps/omo-api/src/main/java/com/omo/infrastructure/auth/social/GoogleAuthClient.java`
- `apps/omo-api/src/main/java/com/omo/infrastructure/auth/social/KakaoAuthClient.java`
- `apps/omo-api/src/main/java/com/omo/infrastructure/auth/social/AppleAuthClient.java`

**Interface Layer**
- `apps/omo-api/src/main/java/com/omo/interfaces/api/auth/AuthV1Controller.java`
- `apps/omo-api/src/main/java/com/omo/interfaces/api/auth/AuthV1Dto.java`
- `apps/omo-api/src/main/java/com/omo/interfaces/api/auth/AuthV1ApiSpec.java`

**테스트**
- `apps/omo-api/src/test/java/com/omo/application/auth/AuthFacadeTest.java` — Unit
- `apps/omo-api/src/test/java/com/omo/infrastructure/auth/social/GoogleAuthClientIntegrationTest.java` — Integration
- `apps/omo-api/src/test/java/com/omo/infrastructure/auth/social/KakaoAuthClientIntegrationTest.java` — Integration
- `apps/omo-api/src/test/java/com/omo/infrastructure/auth/social/AppleAuthClientIntegrationTest.java` — Integration
- `apps/omo-api/src/test/java/com/omo/interfaces/api/auth/AuthV1ApiE2ETest.java` — E2E

---

## 전체 플로우

```
모바일 앱
  │ POST /api/v1/auth/login
  │ { provider: "GOOGLE", token: "..." }
  ▼
AuthV1Controller
  └─ AuthFacade.login(provider, token)
        ├─ SocialAuthClient 탐색 (provider 일치)
        ├─ client.fetchUserInfo(token)  →  소셜 서버 HTTP 호출
        ├─ UserService.getOrCreateUser(...)  →  DB upsert
        └─ JwtProvider.createAccessToken / createRefreshToken
  ▼
{ accessToken, refreshToken, userId }
```

모바일이 소셜 인증을 먼저 수행하고 토큰만 넘기는 **OAuth 위임 방식**이다.  
백엔드는 소셜 서버에 토큰 유효성을 재검증한 뒤 자체 JWT를 발급한다.

---

## 레이어별 설명

### Application — SocialAuthClient (Port Interface)

**역할**  
소셜 클라이언트의 추상화. 두 메서드만 강제한다.

```java
public interface SocialAuthClient {
    SocialProvider provider();
    SocialUserInfo fetchUserInfo(String token);
}
```

**왜 이렇게 작성했나**  
`AuthFacade`가 HTTP 구현(Google/Kakao/Apple)에 직접 의존하지 않도록 포트 인터페이스를 application 레이어에 두었다.  
이로써 `AuthFacade` 단위 테스트에서 HTTP 없이 mock 클라이언트로 대체 가능하고, 새 소셜 프로바이더를 추가할 때 Facade 코드를 건드리지 않아도 된다.

**트레이드오프**  
- 파일이 늘어난다 (인터페이스 + 구현체 분리).
- `provider()` 메서드가 enum을 반환하므로 새 provider 추가 시 `SocialProvider` enum도 함께 수정해야 한다.

---

### Application — AuthFacade

**역할**  
소셜 클라이언트 탐색 → 유저 조회/생성 → JWT 발급까지 한 번에 오케스트레이션.

```java
public AuthInfo login(SocialProvider provider, String token) {
    SocialAuthClient client = socialAuthClients.stream()
        .filter(c -> c.provider() == provider)
        .findFirst()
        .orElseThrow(() -> new CoreException(ErrorType.INVALID_INPUT, "..."));

    SocialUserInfo userInfo = client.fetchUserInfo(token);
    User user = userService.getOrCreateUser(
        userInfo.email(), userInfo.nickname(), provider, userInfo.providerId()
    );

    return new AuthInfo(
        jwtProvider.createAccessToken(user.getId()),
        jwtProvider.createRefreshToken(user.getId()),
        user.getId()
    );
}
```

**왜 이렇게 작성했나**  
Spring이 `List<SocialAuthClient>`를 자동으로 주입한다. 새 구현체(`@Component`)를 등록하면 Facade 코드 수정 없이 자동으로 탐색 대상에 추가된다.  
`@Service` 대신 `@Component`를 쓴 것은 이 클래스가 도메인 서비스가 아니라 여러 서비스를 조합하는 오케스트레이터에 가깝기 때문이다.

**트레이드오프**  
`JwtProvider`를 인터페이스 없이 직접 주입한다. 추후 JWT 발급 전략이 변경될 경우(예: 비대칭키 전환) 인터페이스 도출이 필요할 수 있다.

---

### Infrastructure — GoogleAuthClient

**역할**  
Google tokeninfo API(`https://oauth2.googleapis.com/tokeninfo?id_token=...`)를 호출해 ID 토큰을 검증하고 유저 정보를 파싱한다.

**왜 이렇게 작성했나**  
Google은 별도 SDK 없이 HTTP GET 한 번으로 토큰 검증이 가능하다.  
응답 body에 `error` 필드가 있으면 200 OK여도 실패로 처리한다 — Google tokeninfo의 특이한 동작 방식.

```java
if (body == null || body.containsKey("error")) {
    throw new CoreException(ErrorType.UNAUTHENTICATED, "유효하지 않은 Google 토큰입니다.");
}
```

`name` 필드가 없을 때 `email`을 nickname으로 fallback한다 (Google 계정 설정에 따라 생략될 수 있음).

---

### Infrastructure — KakaoAuthClient

**역할**  
Kakao User API(`https://kapi.kakao.com/v2/user/me`)를 Bearer 토큰으로 호출해 유저 정보를 파싱한다.

**왜 이렇게 작성했나**  
Kakao는 이메일 제공 동의가 선택 사항이므로 미동의 시 `{providerId}@kakao.com` 형식의 임시 이메일을 생성한다.  
`kakao_account` 자체가 null인 케이스도 처리하여 닉네임 기본값(`"카카오유저"`)을 제공한다.

**트레이드오프**  
임시 이메일 생성은 실제 이메일이 아니므로, 나중에 이메일 인증이나 알림 발송 로직이 생기면 이 케이스를 별도 처리해야 한다.

---

### Infrastructure — AppleAuthClient

**역할**  
Apple JWKS 공개키를 내려받아 identityToken(RS256 JWT)을 직접 검증한다.

**왜 이렇게 작성했나**  
Apple은 Google/Kakao와 달리 "토큰 검증용 API 엔드포인트"를 제공하지 않는다.  
대신 공개키 집합(JWKS)을 제공하고 클라이언트가 JWT를 직접 서명 검증하도록 설계되어 있다.  
JJWT의 `keyLocator`를 사용해 토큰 헤더의 `kid`와 일치하는 공개키를 JWKS에서 찾아 검증한다.

```java
Claims claims = Jwts.parser()
    .keyLocator(header -> {
        String kid = (String) header.get("kid");
        return jwkSet.getKeys().stream()
            .filter(jwk -> kid.equals(jwk.getId()))
            .findFirst()
            .map(jwk -> (Key) ((PublicJwk<?>) jwk).toKey())
            .orElseThrow(() -> new CoreException(UNAUTHENTICATED, "Apple 공개키를 찾을 수 없습니다."));
    })
    .requireIssuer("https://appleid.apple.com")
    .requireAudience(clientId)   // aud = Apple 앱 번들 ID
    .build()
    .parseSignedClaims(identityToken)
    .getPayload();
```

Apple은 최초 로그인 시에만 이메일을 제공한다. 이후 로그인에서 email 클레임이 없으면 `{sub}@privaterelay.appleid.com` 형식으로 fallback한다.

**트레이드오프**  
JWKS를 요청마다 새로 fetch한다. Apple의 공개키는 자주 바뀌지 않으므로 캐싱하면 성능을 개선할 수 있다. 현재는 단순함을 우선했다.

---

### Interface — AuthV1Controller / AuthV1ApiSpec

**역할**  
`POST /api/v1/auth/login` 엔드포인트 구현. Swagger 스펙(`AuthV1ApiSpec`)과 실제 구현(`AuthV1Controller`)을 분리했다.

**왜 이렇게 작성했나**  
`@Operation`, `@Tag` 같은 Swagger 어노테이션을 컨트롤러 본문에서 분리하면 비즈니스 코드가 더 읽기 쉽다.  
`AuthV1ApiSpec` 인터페이스가 API 계약 명세서 역할을 한다.

**트레이드오프**  
컨트롤러 코드가 인터페이스와 구현 두 파일로 나뉘어 파악해야 할 파일이 늘어난다.

---

## 핵심 설계 결정

### 1. SocialAuthClient를 application 레이어에 둔 이유

```
application/auth/SocialAuthClient.java  ← 포트 (인터페이스)
infrastructure/auth/social/*Client.java ← 어댑터 (구현)
```

포트-어댑터(헥사고날) 패턴. application 레이어가 infrastructure에 의존하지 않도록 방향을 역전시킨다.  
덕분에 `AuthFacadeTest`에서 HTTP 없이 mock 클라이언트만으로 오케스트레이션 로직을 검증할 수 있다.

### 2. RestClient를 생성자에서 builder로 받는 이유

```java
public GoogleAuthClient(RestClient.Builder builder) {
    this.restClient = builder.build();
}
```

`RestClient.create()`를 쓰면 통합 테스트에서 `MockRestServiceServer`를 바인딩할 수 없다.  
`RestClient.Builder`를 주입받으면 `MockRestServiceServer.bindTo(builder)`로 HTTP를 가로챌 수 있어 Spring 컨텍스트 없이도 클라이언트를 격리 테스트할 수 있다.

### 3. 예외 처리 패턴

모든 소셜 클라이언트가 동일한 패턴을 따른다:

```java
try {
    // 외부 API 호출 및 파싱
} catch (CoreException e) {
    throw e;              // 이미 우리 예외면 그대로 전파
} catch (Exception e) {
    throw new CoreException(UNAUTHENTICATED, "...토큰 검증에 실패했습니다.");
}
```

외부 HTTP 오류(4xx/5xx)는 모두 `UNAUTHENTICATED`로 변환한다. 소셜 서버의 5xx도 클라이언트 입장에서는 "인증 불가" 상황으로 간주한 설계 결정이다.

### 4. 신규 유저 자동 가입

별도 회원가입 API 없이 최초 로그인 시 자동 가입된다. `UserService.getOrCreateUser`가 DB에 유저가 없으면 생성하고, 있으면 기존 레코드를 반환한다. provider + providerId 조합이 UK이므로 중복 가입이 불가능하다.

---

## 테스트 전략

| 레이어 | 방식 | 이유 |
|-------|------|------|
| AuthFacade | Unit (Mockito) | HTTP·DB 없이 오케스트레이션 로직만 검증 |
| 소셜 클라이언트 3종 | Integration (MockRestServiceServer) | Spring 컨텍스트 없이 HTTP 어댑터만 격리 |
| AuthV1Controller | E2E (`@SpringBootTest` + 실제 DB) | 전체 레이어 관통 — 소셜 클라이언트만 `@MockitoBean`으로 대체 |

Spring Boot 4.x에서 `@RestClientTest`가 제거됐기 때문에, 소셜 클라이언트 테스트는 `MockRestServiceServer.bindTo(RestClient.Builder)` 패턴으로 Spring 컨텍스트 없이 구성했다.


