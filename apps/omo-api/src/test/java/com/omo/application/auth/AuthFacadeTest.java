package com.omo.application.auth;

import com.omo.domain.user.SocialProvider;
import com.omo.domain.user.User;
import com.omo.domain.user.UserService;
import com.omo.infrastructure.auth.jwt.JwtProvider;
import com.omo.support.error.CoreException;
import com.omo.support.error.ErrorType;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@DisplayName("AuthFacade —")
class AuthFacadeTest {

    @Mock
    private SocialAuthClient googleClient;

    @Mock
    private UserService userService;

    @Mock
    private JwtProvider jwtProvider;

    private AuthFacade authFacade;

    @BeforeEach
    void setUp() {
        when(googleClient.provider()).thenReturn(SocialProvider.GOOGLE);
        authFacade = new AuthFacade(List.of(googleClient), userService, jwtProvider);
    }

    @DisplayName("소셜 로그인을 할 때,")
    @Nested
    class Login {

        @DisplayName("Google 토큰이 유효하면, 유저를 조회/생성하고 JWT 토큰을 반환한다.")
        @Test
        void returnsAuthInfo_whenGoogleTokenIsValid() {
            // arrange
            String token = "google-id-token";
            User mockUser = mock(User.class);
            when(mockUser.getId()).thenReturn(42L);
            when(googleClient.fetchUserInfo(token))
                .thenReturn(new SocialUserInfo("google@omo.com", "구글유저", "g-uid-001"));
            when(userService.getOrCreateUser("google@omo.com", "구글유저", SocialProvider.GOOGLE, "g-uid-001"))
                .thenReturn(mockUser);
            when(jwtProvider.createAccessToken(42L)).thenReturn("access-token");
            when(jwtProvider.createRefreshToken(42L)).thenReturn("refresh-token");

            // act
            AuthInfo result = authFacade.login(SocialProvider.GOOGLE, token);

            // assert
            assertAll(
                () -> assertThat(result.accessToken()).isEqualTo("access-token"),
                () -> assertThat(result.refreshToken()).isEqualTo("refresh-token"),
                () -> assertThat(result.userId()).isEqualTo(42L)
            );
        }

        @DisplayName("소셜 검증 후, UserService.getOrCreateUser를 정확한 인자로 호출한다.")
        @Test
        void callsGetOrCreateUser_withFetchedUserInfo() {
            // arrange
            when(googleClient.fetchUserInfo("token"))
                .thenReturn(new SocialUserInfo("test@omo.com", "테스터", "g-uid-999"));
            when(userService.getOrCreateUser(any(), any(), any(), any()))
                .thenReturn(new User("test@omo.com", "테스터", SocialProvider.GOOGLE, "g-uid-999"));
            when(jwtProvider.createAccessToken(any())).thenReturn("a");
            when(jwtProvider.createRefreshToken(any())).thenReturn("r");

            // act
            authFacade.login(SocialProvider.GOOGLE, "token");

            // assert
            verify(userService).getOrCreateUser(
                eq("test@omo.com"), eq("테스터"), eq(SocialProvider.GOOGLE), eq("g-uid-999")
            );
        }

        @DisplayName("지원하지 않는 프로바이더면, INVALID_INPUT 예외가 발생한다.")
        @Test
        void throwsInvalidInput_whenProviderNotSupported() {
            // act - KAKAO 클라이언트는 등록되지 않음
            CoreException result = assertThrows(CoreException.class, () ->
                authFacade.login(SocialProvider.KAKAO, "kakao-token")
            );

            // assert
            assertThat(result.getErrorType()).isEqualTo(ErrorType.INVALID_INPUT);
        }

        @DisplayName("소셜 클라이언트가 UNAUTHENTICATED 예외를 던지면, 그대로 전파된다.")
        @Test
        void propagatesUnauthenticated_whenClientThrows() {
            // arrange
            when(googleClient.fetchUserInfo(any()))
                .thenThrow(new CoreException(ErrorType.UNAUTHENTICATED, "유효하지 않은 Google 토큰입니다."));

            // act
            CoreException result = assertThrows(CoreException.class, () ->
                authFacade.login(SocialProvider.GOOGLE, "invalid-token")
            );

            // assert
            assertThat(result.getErrorType()).isEqualTo(ErrorType.UNAUTHENTICATED);
        }
    }
}
