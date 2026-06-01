package com.omo.interfaces.api.auth;

import com.omo.infrastructure.auth.jwt.JwtProvider;
import com.omo.support.error.CoreException;
import com.omo.support.error.ErrorType;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.JwtException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@DisplayName("AuthInterceptor")
class AuthInterceptorTest {

    private JwtProvider jwtProvider;
    private AuthInterceptor interceptor;
    private HttpServletRequest request;
    private HttpServletResponse response;

    @BeforeEach
    void setUp() {
        jwtProvider = mock(JwtProvider.class);
        interceptor = new AuthInterceptor(jwtProvider);
        request = mock(HttpServletRequest.class);
        response = mock(HttpServletResponse.class);
    }

    @Nested
    @DisplayName("preHandle")
    class PreHandle {

        @Test
        @DisplayName("Authorization 헤더가 없으면, UNAUTHENTICATED 예외가 발생한다.")
        void throwsUnauthenticated_whenNoAuthorizationHeader() {
            when(request.getHeader("Authorization")).thenReturn(null);

            assertThatThrownBy(() -> interceptor.preHandle(request, response, new Object()))
                .isInstanceOf(CoreException.class)
                .satisfies(e -> assertThat(((CoreException) e).getErrorType()).isEqualTo(ErrorType.UNAUTHENTICATED));
        }

        @Test
        @DisplayName("Bearer 접두사가 없으면, UNAUTHENTICATED 예외가 발생한다.")
        void throwsUnauthenticated_whenNoBearerPrefix() {
            when(request.getHeader("Authorization")).thenReturn("Basic dXNlcjpwYXNz");

            assertThatThrownBy(() -> interceptor.preHandle(request, response, new Object()))
                .isInstanceOf(CoreException.class)
                .satisfies(e -> assertThat(((CoreException) e).getErrorType()).isEqualTo(ErrorType.UNAUTHENTICATED));
        }

        @Test
        @DisplayName("유효한 토큰이면, request에 userId를 저장하고 true를 반환한다.")
        void setsUserIdAndReturnsTrue_whenValidToken() throws Exception {
            when(request.getHeader("Authorization")).thenReturn("Bearer valid-token");
            when(jwtProvider.parseUserId("valid-token")).thenReturn(42L);

            boolean result = interceptor.preHandle(request, response, new Object());

            assertThat(result).isTrue();
            verify(request).setAttribute(AuthInterceptor.USER_ID_ATTRIBUTE, 42L);
        }

        @Test
        @DisplayName("Bearer 다음에 토큰이 없으면, UNAUTHENTICATED 예외가 발생한다.")
        void throwsUnauthenticated_whenTokenIsEmpty() {
            when(request.getHeader("Authorization")).thenReturn("Bearer ");
            when(jwtProvider.parseUserId("")).thenThrow(new JwtException("empty token"));

            assertThatThrownBy(() -> interceptor.preHandle(request, response, new Object()))
                .isInstanceOf(CoreException.class)
                .satisfies(e -> assertThat(((CoreException) e).getErrorType()).isEqualTo(ErrorType.UNAUTHENTICATED));
        }

        @Test
        @DisplayName("토큰이 만료됐으면, TOKEN_EXPIRED 예외가 발생한다.")
        void throwsTokenExpired_whenExpiredToken() {
            when(request.getHeader("Authorization")).thenReturn("Bearer expired-token");
            when(jwtProvider.parseUserId("expired-token"))
                .thenThrow(new ExpiredJwtException(null, null, "expired", null));

            assertThatThrownBy(() -> interceptor.preHandle(request, response, new Object()))
                .isInstanceOf(CoreException.class)
                .satisfies(e -> assertThat(((CoreException) e).getErrorType()).isEqualTo(ErrorType.TOKEN_EXPIRED));
        }

        @Test
        @DisplayName("토큰이 변조됐으면, UNAUTHENTICATED 예외가 발생한다.")
        void throwsUnauthenticated_whenTamperedToken() {
            when(request.getHeader("Authorization")).thenReturn("Bearer tampered-token");
            when(jwtProvider.parseUserId("tampered-token")).thenThrow(new JwtException("tampered"));

            assertThatThrownBy(() -> interceptor.preHandle(request, response, new Object()))
                .isInstanceOf(CoreException.class)
                .satisfies(e -> assertThat(((CoreException) e).getErrorType()).isEqualTo(ErrorType.UNAUTHENTICATED));
        }
    }
}
