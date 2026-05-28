package com.omo.infrastructure.auth.jwt;

import io.jsonwebtoken.JwtException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertAll;

@DisplayName("JwtProvider —")
class JwtProviderTest {

    private static final String SECRET = "test-secret-key-must-be-32-chars-or-longer!!";

    private JwtProvider jwtProvider;

    @BeforeEach
    void setUp() {
        jwtProvider = new JwtProvider(new JwtProperties(SECRET, 3_600_000L, 2_592_000_000L));
        jwtProvider.init();
    }

    @DisplayName("액세스 토큰을 발급할 때,")
    @Nested
    class CreateAccessToken {

        @DisplayName("userId를 포함한 유효한 JWT가 발급되고, 파싱하면 동일한 userId가 반환된다.")
        @Test
        void createsToken_andParsesBackSameUserId() {
            // arrange
            Long userId = 42L;

            // act
            String token = jwtProvider.createAccessToken(userId);
            Long parsed = jwtProvider.parseUserId(token);

            // assert
            assertAll(
                () -> assertThat(token).isNotBlank(),
                () -> assertThat(parsed).isEqualTo(userId)
            );
        }
    }

    @DisplayName("리프레시 토큰을 발급할 때,")
    @Nested
    class CreateRefreshToken {

        @DisplayName("userId를 포함한 유효한 JWT가 발급되고, 파싱하면 동일한 userId가 반환된다.")
        @Test
        void createsToken_andParsesBackSameUserId() {
            // arrange
            Long userId = 99L;

            // act
            String token = jwtProvider.createRefreshToken(userId);
            Long parsed = jwtProvider.parseUserId(token);

            // assert
            assertAll(
                () -> assertThat(token).isNotBlank(),
                () -> assertThat(parsed).isEqualTo(userId)
            );
        }
    }

    @DisplayName("토큰을 파싱할 때,")
    @Nested
    class ParseUserId {

        @DisplayName("만료된 토큰이면 JwtException이 발생한다.")
        @Test
        void throwsJwtException_whenTokenExpired() {
            // arrange - expiry를 음수로 설정하면 즉시 만료
            JwtProvider expiredProvider = new JwtProvider(new JwtProperties(SECRET, -1L, -1L));
            expiredProvider.init();
            String expiredToken = expiredProvider.createAccessToken(1L);

            // act & assert
            assertThatThrownBy(() -> jwtProvider.parseUserId(expiredToken))
                .isInstanceOf(JwtException.class);
        }

        @DisplayName("변조된 토큰이면 JwtException이 발생한다.")
        @Test
        void throwsJwtException_whenTokenTampered() {
            // arrange
            String token = jwtProvider.createAccessToken(1L);
            String tampered = token + "x";

            // act & assert
            assertThatThrownBy(() -> jwtProvider.parseUserId(tampered))
                .isInstanceOf(JwtException.class);
        }

        @DisplayName("다른 시크릿으로 서명된 토큰이면 JwtException이 발생한다.")
        @Test
        void throwsJwtException_whenSignedWithDifferentSecret() {
            // arrange
            JwtProvider otherProvider = new JwtProvider(
                new JwtProperties("other-secret-key-must-be-32-chars-or-longer!!", 3_600_000L, 2_592_000_000L)
            );
            otherProvider.init();
            String tokenFromOther = otherProvider.createAccessToken(1L);

            // act & assert
            assertThatThrownBy(() -> jwtProvider.parseUserId(tokenFromOther))
                .isInstanceOf(JwtException.class);
        }

    }

    @DisplayName("액세스·리프레시 토큰을 비교할 때,")
    @Nested
    class TokenDistinction {

        @DisplayName("동일한 userId로 발급해도 액세스·리프레시 토큰은 서로 다른 값이다.")
        @Test
        void accessAndRefreshTokensAreDifferent() {
            // arrange
            Long userId = 7L;

            // act
            String access = jwtProvider.createAccessToken(userId);
            String refresh = jwtProvider.createRefreshToken(userId);

            // assert
            assertThat(access).isNotEqualTo(refresh);
        }
    }
}
