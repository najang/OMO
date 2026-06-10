package com.omo.domain.user;

import com.omo.infrastructure.auth.social.AppleAuthClient;
import com.omo.infrastructure.auth.social.GoogleAuthClient;
import com.omo.infrastructure.auth.social.KakaoAuthClient;
import com.omo.support.error.CoreException;
import com.omo.support.error.ErrorType;
import com.omo.utils.DatabaseCleanUp;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertThrows;


@SpringBootTest
class UserServiceIntegrationTest {

    @MockitoBean
    private GoogleAuthClient googleAuthClient;

    @MockitoBean
    private KakaoAuthClient kakaoAuthClient;

    @MockitoBean
    private AppleAuthClient appleAuthClient;

    @Autowired
    private UserService userService;

    @Autowired
    private DatabaseCleanUp databaseCleanUp;

    @AfterEach
    void tearDown() {
        databaseCleanUp.truncateAllTables();
    }

    @DisplayName("소셜 로그인으로 유저를 조회/생성할 때,")
    @Nested
    class GetOrCreate {

        @DisplayName("처음 로그인하는 유저면, 새로 생성하여 반환한다.")
        @Test
        void createsAndReturnsNewUser_whenFirstLogin() {
            // arrange
            String email = "new@omo.com";
            String nickname = "신규유저";
            SocialProvider provider = SocialProvider.GOOGLE;
            String providerId = "google-uid-new";

            // act
            User result = userService.getOrCreateUser(email, nickname, provider, providerId);

            // assert
            assertAll(
                () -> assertThat(result.getId()).isNotNull(),
                () -> assertThat(result.getEmail()).isEqualTo(email),
                () -> assertThat(result.getNickname()).isEqualTo(nickname),
                () -> assertThat(result.getProvider()).isEqualTo(provider),
                () -> assertThat(result.getProviderId()).isEqualTo(providerId)
            );
        }

        @DisplayName("이미 가입된 유저면, 기존 유저를 그대로 반환한다.")
        @Test
        void returnsExistingUser_whenAlreadyRegistered() {
            // arrange
            User existing = createUser("existing@omo.com", "기존유저", SocialProvider.KAKAO, "kakao-uid-001");

            // act
            User result = userService.getOrCreateUser(
                "existing@omo.com", "닉네임변경시도", SocialProvider.KAKAO, "kakao-uid-001"
            );

            // assert
            assertAll(
                () -> assertThat(result.getId()).isEqualTo(existing.getId()),
                () -> assertThat(result.getNickname()).isEqualTo("기존유저") // 기존 닉네임 유지
            );
        }

        @DisplayName("동일한 provider/providerId 쌍이 없으면, 같은 이메일이어도 새 유저로 생성한다.")
        @Test
        void createsNewUser_whenProviderInfoIsDifferent() {
            // arrange
            User google = createUser("same@omo.com", "구글유저", SocialProvider.GOOGLE, "google-uid-abc");

            // act — 같은 이메일이지만 provider가 다름 (APPLE)
            User apple = userService.getOrCreateUser(
                "same@omo.com", "애플유저", SocialProvider.APPLE, "apple-uid-abc"
            );

            // assert
            assertAll(
                () -> assertThat(apple.getId()).isNotEqualTo(google.getId()),
                () -> assertThat(apple.getProvider()).isEqualTo(SocialProvider.APPLE)
            );
        }
    }

    @DisplayName("온보딩 닉네임을 설정할 때,")
    @Nested
    class CompleteOnboarding {

        @DisplayName("유효한 닉네임이면, 닉네임과 onboardingCompleted가 저장된다.")
        @Test
        void completesOnboarding_whenNicknameIsValid() {
            // arrange
            User user = createUser("onboard@omo.com", "임시닉네임", SocialProvider.GOOGLE, "uid-onboard");

            // act
            userService.completeOnboarding(user.getId(), "햇살곰");

            // assert
            User updated = userService.getUser(user.getId());
            assertAll(
                () -> assertThat(updated.getNickname()).isEqualTo("햇살곰"),
                () -> assertThat(updated.isOnboardingCompleted()).isTrue()
            );
        }

        @DisplayName("존재하지 않는 ID를 주면, USER_NOT_FOUND 예외가 발생한다.")
        @Test
        void throwsUserNotFound_whenUserDoesNotExist() {
            CoreException result = assertThrows(CoreException.class, () ->
                userService.completeOnboarding(999L, "햇살곰")
            );
            assertThat(result.getErrorType()).isEqualTo(ErrorType.USER_NOT_FOUND);
        }

        @DisplayName("유효하지 않은 닉네임이면, INVALID_INPUT 예외가 발생한다.")
        @Test
        void throwsInvalidInput_whenNicknameIsInvalid() {
            // arrange
            User user = createUser("invalid@omo.com", "임시닉네임", SocialProvider.GOOGLE, "uid-invalid");

            // act
            CoreException result = assertThrows(CoreException.class, () ->
                userService.completeOnboarding(user.getId(), "a")  // 1자 — 너무 짧음
            );
            assertThat(result.getErrorType()).isEqualTo(ErrorType.INVALID_INPUT);
        }
    }

    private User createUser(String email, String nickname, SocialProvider provider, String providerId) {
        return userService.getOrCreateUser(email, nickname, provider, providerId);
    }
}
