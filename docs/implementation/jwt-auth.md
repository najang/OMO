# JWT 발급/검증 구현 노트

> JJWT 0.12.6 기반 HS256 JWT 발급·파싱 — 소셜 로그인 후 자체 인증 토큰 관리 (OMO-21)

## 구현 파일

- `apps/omo-api/src/main/java/com/omo/infrastructure/auth/jwt/JwtProperties.java`
- `apps/omo-api/src/main/java/com/omo/infrastructure/auth/jwt/JwtProvider.java`
- `apps/omo-api/src/main/resources/application.yml` (jwt 섹션)
- `apps/omo-api/src/test/java/com/omo/infrastructure/auth/jwt/JwtProviderTest.java` — Unit

---

## 레이어별 설명

### Infrastructure — JwtProperties

**역할**  
`application.yml`의 `jwt.*` 설정을 타입 안전하게 바인딩한다.

```java
@ConfigurationProperties(prefix = "jwt")
public record JwtProperties(String secret, long accessExpiry, long refreshExpiry) {}
```

```yaml
jwt:
  secret: ${JWT_SECRET:local-dev-secret-key-must-be-at-least-32-chars-long}
  access-expiry: 3600000      # 1시간 (ms)
  refresh-expiry: 2592000000  # 30일 (ms)
```

**왜 이렇게 작성했나**  
`@Value`로 개별 필드를 주입하는 대신 `@ConfigurationProperties` record를 쓰면:

- 타입 변환(`long`)이 자동으로 처리된다
- 설정 키가 하나의 클래스에 모여 누락을 컴파일 타임에 잡을 수 있다
- 단위 테스트에서 `new JwtProperties(secret, expiry, expiry)` 로 간단히 생성 가능하다

**트레이드오프**  
`@ConfigurationPropertiesScan`이 메인 클래스(`OmoApiApplication`)에 이미 있어야 한다. 빠뜨리면 빈이 등록되지 않는다.

---

### Infrastructure — JwtProvider

**역할**  
액세스 토큰·리프레시 토큰 발급과 파싱을 담당한다.

```java
@PostConstruct
public void init() {
    this.secretKey = Keys.hmacShaKeyFor(
        properties.secret().getBytes(StandardCharsets.UTF_8)
    );
}
```

```java
private String buildToken(Long userId, long expiryMs) {
    Date now = new Date();
    return Jwts.builder()
        .subject(String.valueOf(userId))  // sub 클레임에 userId 저장
        .issuedAt(now)
        .expiration(new Date(now.getTime() + expiryMs))
        .signWith(secretKey)              // JJWT가 HS256 자동 선택
        .compact();
}
```

```java
public Long parseUserId(String token) {
    Claims claims = Jwts.parser()
        .verifyWith(secretKey)
        .build()
        .parseSignedClaims(token)
        .getPayload();
    return Long.parseLong(claims.getSubject());
}
```

**왜 이렇게 작성했나**  

*HS256 선택*: 발급·검증 모두 서버에서만 하므로 비대칭키(RS256)가 불필요하다. HS256이 더 간단하고 키 관리 부담이 적다.

*subject에 userId 저장*: JWT 표준 클레임 `sub`를 활용해 별도 커스텀 클레임을 쓰지 않는다. `parseUserId`에서 `Long.parseLong(claims.getSubject())`으로 꺼낸다. userId가 문자열로 저장되는 점을 주의해야 한다.

*`@PostConstruct` init*: `SecretKey` 생성은 무거운 작업이 아니지만, 빈 생성 시 한 번만 만들어두어 요청마다 재생성하지 않는다. 생성자에서 하지 않은 이유는 `JwtProperties`가 생성자 주입 시점에 완전히 초기화되어 있음을 보장하기 위해서다.

**트레이드오프**  

- 액세스·리프레시 토큰이 `sub` + 만료 시간만 다르고 내부 구조가 동일하다. 추후 토큰 타입 구분(예: `type=access`)이 필요하면 커스텀 클레임을 추가해야 한다.
- 현재 `parseUserId`는 만료·변조·서명 오류를 모두 `JwtException`(JJWT 예외)으로 던진다. 상위 레이어에서 이를 `CoreException`으로 변환하는 로직이 없으면 클라이언트에게 500 응답이 갈 수 있다. 추후 필터/미들웨어 구현 시 반드시 처리해야 한다.
- 토큰 무효화(로그아웃, 강제 만료)를 지원하지 않는다. Redis 기반 블랙리스트나 리프레시 토큰 저장이 필요해지면 구조 변경이 필요하다.

---

## 핵심 설계 결정

### 1. JwtProvider에 인터페이스를 두지 않은 이유

현재 JWT 발급 전략이 하나(HS256)뿐이고 테스트에서도 Mockito가 구체 클래스를 직접 mock할 수 있다.  
"추후 비대칭키로 전환할 수도 있다"는 이유만으로 미리 인터페이스를 만드는 것은 YAGNI에 해당한다고 판단했다. 실제 전략이 분기될 때 도출하면 된다.

### 2. 액세스/리프레시 토큰을 한 Provider에서 관리

발급 로직은 `expiryMs`만 다르고 동일하다. 별도 클래스로 분리하면 오히려 추적이 어려워진다.  
`createAccessToken` / `createRefreshToken`을 명시적으로 구분해 호출 쪽에서 의도를 명확히 표현하게 했다.

### 3. 만료 시간을 밀리초(ms)로 관리

Java `Date`가 ms 단위이므로 변환 없이 직접 사용 가능하다.  
`application.yml`의 주석으로 단위를 명시(`# 1시간 (ms)`)해 혼동을 방지한다.

---

## 테스트 전략

Spring 컨텍스트 없이 `new JwtProvider(new JwtProperties(...))` + `init()`으로 직접 생성해 테스트한다.

```java
@BeforeEach
void setUp() {
    jwtProvider = new JwtProvider(new JwtProperties(SECRET, 3_600_000L, 2_592_000_000L));
    jwtProvider.init();
}
```

커버하는 케이스:

| 케이스 | 검증 내용 |
|-------|---------|
| 액세스 토큰 발급 | 발급 후 파싱 → 동일 userId |
| 리프레시 토큰 발급 | 발급 후 파싱 → 동일 userId |
| 만료 토큰 | `JwtException` 발생 |
| 변조 토큰 | `JwtException` 발생 |
| 다른 시크릿으로 서명된 토큰 | `JwtException` 발생 |
| 액세스 ≠ 리프레시 | 두 토큰 값이 다름을 보장 |


