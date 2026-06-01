package com.omo.domain.user;

import com.omo.support.error.CoreException;
import com.omo.support.error.ErrorType;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertThrows;

class UserModelTest {

    @DisplayName("User를 생성할 때,")
    @Nested
    class Create {

        @DisplayName("유효한 값이 모두 주어지면, 정상적으로 생성된다.")
        @Test
        void createsUser_whenAllValidInputsAreProvided() {
            // arrange
            String email = "test@omo.com";
            String nickname = "테스터";
            SocialProvider provider = SocialProvider.GOOGLE;
            String providerId = "google-uid-123";

            // act
            User user = new User(email, nickname, provider, providerId);

            // assert
            assertAll(
                () -> assertThat(user.getEmail()).isEqualTo(email),
                () -> assertThat(user.getNickname()).isEqualTo(nickname),
                () -> assertThat(user.getProvider()).isEqualTo(provider),
                () -> assertThat(user.getProviderId()).isEqualTo(providerId)
            );
        }

        @DisplayName("이메일이 null이면, INVALID_INPUT 예외가 발생한다.")
        @Test
        void throwsBadRequest_whenEmailIsNull() {
            CoreException result = assertThrows(CoreException.class, () ->
                new User(null, "테스터", SocialProvider.GOOGLE, "uid-123")
            );
            assertThat(result.getErrorType()).isEqualTo(ErrorType.INVALID_INPUT);
        }

        @DisplayName("이메일이 비어있으면, INVALID_INPUT 예외가 발생한다.")
        @Test
        void throwsBadRequest_whenEmailIsBlank() {
            CoreException result = assertThrows(CoreException.class, () ->
                new User("  ", "테스터", SocialProvider.GOOGLE, "uid-123")
            );
            assertThat(result.getErrorType()).isEqualTo(ErrorType.INVALID_INPUT);
        }

        @DisplayName("닉네임이 null이면, INVALID_INPUT 예외가 발생한다.")
        @Test
        void throwsBadRequest_whenNicknameIsNull() {
            CoreException result = assertThrows(CoreException.class, () ->
                new User("test@omo.com", null, SocialProvider.GOOGLE, "uid-123")
            );
            assertThat(result.getErrorType()).isEqualTo(ErrorType.INVALID_INPUT);
        }

        @DisplayName("닉네임이 비어있으면, INVALID_INPUT 예외가 발생한다.")
        @Test
        void throwsBadRequest_whenNicknameIsBlank() {
            CoreException result = assertThrows(CoreException.class, () ->
                new User("test@omo.com", "", SocialProvider.GOOGLE, "uid-123")
            );
            assertThat(result.getErrorType()).isEqualTo(ErrorType.INVALID_INPUT);
        }

        @DisplayName("소셜 프로바이더가 null이면, INVALID_INPUT 예외가 발생한다.")
        @Test
        void throwsBadRequest_whenProviderIsNull() {
            CoreException result = assertThrows(CoreException.class, () ->
                new User("test@omo.com", "테스터", null, "uid-123")
            );
            assertThat(result.getErrorType()).isEqualTo(ErrorType.INVALID_INPUT);
        }

        @DisplayName("프로바이더 ID가 null이면, INVALID_INPUT 예외가 발생한다.")
        @Test
        void throwsBadRequest_whenProviderIdIsNull() {
            CoreException result = assertThrows(CoreException.class, () ->
                new User("test@omo.com", "테스터", SocialProvider.GOOGLE, null)
            );
            assertThat(result.getErrorType()).isEqualTo(ErrorType.INVALID_INPUT);
        }

        @DisplayName("프로바이더 ID가 비어있으면, INVALID_INPUT 예외가 발생한다.")
        @Test
        void throwsBadRequest_whenProviderIdIsBlank() {
            CoreException result = assertThrows(CoreException.class, () ->
                new User("test@omo.com", "테스터", SocialProvider.GOOGLE, "  ")
            );
            assertThat(result.getErrorType()).isEqualTo(ErrorType.INVALID_INPUT);
        }
    }

    @DisplayName("온보딩 닉네임을 설정할 때,")
    @Nested
    class CompleteOnboarding {

        @DisplayName("새로 생성한 유저는 onboardingCompleted가 false다.")
        @Test
        void defaultOnboardingCompleted_isFalse() {
            User user = new User("test@omo.com", "소셜닉네임", SocialProvider.GOOGLE, "uid-1");

            assertThat(user.isOnboardingCompleted()).isFalse();
        }

        @DisplayName("한글 닉네임이면, 닉네임과 onboardingCompleted가 업데이트된다.")
        @Test
        void completesOnboarding_withKoreanNickname() {
            User user = new User("test@omo.com", "소셜닉네임", SocialProvider.GOOGLE, "uid-1");

            user.completeOnboarding("햇살곰");

            assertAll(
                () -> assertThat(user.getNickname()).isEqualTo("햇살곰"),
                () -> assertThat(user.isOnboardingCompleted()).isTrue()
            );
        }

        @DisplayName("영어 닉네임이면, 닉네임과 onboardingCompleted가 업데이트된다.")
        @Test
        void completesOnboarding_withEnglishNickname() {
            User user = new User("test@omo.com", "소셜닉네임", SocialProvider.GOOGLE, "uid-1");

            user.completeOnboarding("sunny");

            assertAll(
                () -> assertThat(user.getNickname()).isEqualTo("sunny"),
                () -> assertThat(user.isOnboardingCompleted()).isTrue()
            );
        }

        @DisplayName("한글+영어 혼합 닉네임이면, 닉네임과 onboardingCompleted가 업데이트된다.")
        @Test
        void completesOnboarding_withMixedNickname() {
            User user = new User("test@omo.com", "소셜닉네임", SocialProvider.GOOGLE, "uid-1");

            user.completeOnboarding("곰a");

            assertAll(
                () -> assertThat(user.getNickname()).isEqualTo("곰a"),
                () -> assertThat(user.isOnboardingCompleted()).isTrue()
            );
        }

        @DisplayName("2자(최소 경계) 닉네임이면, 정상 설정된다.")
        @Test
        void completesOnboarding_withMinLengthNickname() {
            User user = new User("test@omo.com", "소셜닉네임", SocialProvider.GOOGLE, "uid-1");

            user.completeOnboarding("곰곰");

            assertAll(
                () -> assertThat(user.getNickname()).isEqualTo("곰곰"),
                () -> assertThat(user.isOnboardingCompleted()).isTrue()
            );
        }

        @DisplayName("6자(최대 경계) 닉네임이면, 정상 설정된다.")
        @Test
        void completesOnboarding_withMaxLengthNickname() {
            User user = new User("test@omo.com", "소셜닉네임", SocialProvider.GOOGLE, "uid-1");

            user.completeOnboarding("일이삼사오육");

            assertAll(
                () -> assertThat(user.getNickname()).isEqualTo("일이삼사오육"),
                () -> assertThat(user.isOnboardingCompleted()).isTrue()
            );
        }

        @DisplayName("이미 완료된 온보딩을 다시 호출하면, 닉네임이 덮어씌워진다.")
        @Test
        void overwritesNickname_whenCalledAgain() {
            User user = new User("test@omo.com", "소셜닉네임", SocialProvider.GOOGLE, "uid-1");
            user.completeOnboarding("첫번째");

            user.completeOnboarding("두번째");

            assertThat(user.getNickname()).isEqualTo("두번째");
            assertThat(user.isOnboardingCompleted()).isTrue();
        }

        @DisplayName("닉네임이 null이면, INVALID_INPUT 예외가 발생한다.")
        @Test
        void throwsInvalidInput_whenNicknameIsNull() {
            User user = new User("test@omo.com", "소셜닉네임", SocialProvider.GOOGLE, "uid-1");

            CoreException result = assertThrows(CoreException.class, () -> user.completeOnboarding(null));

            assertThat(result.getErrorType()).isEqualTo(ErrorType.INVALID_INPUT);
        }

        @DisplayName("닉네임이 1자면, INVALID_INPUT 예외가 발생한다.")
        @Test
        void throwsInvalidInput_whenNicknameIsTooShort() {
            User user = new User("test@omo.com", "소셜닉네임", SocialProvider.GOOGLE, "uid-1");

            CoreException result = assertThrows(CoreException.class, () -> user.completeOnboarding("곰"));

            assertThat(result.getErrorType()).isEqualTo(ErrorType.INVALID_INPUT);
        }

        @DisplayName("닉네임이 7자면, INVALID_INPUT 예외가 발생한다.")
        @Test
        void throwsInvalidInput_whenNicknameIsTooLong() {
            User user = new User("test@omo.com", "소셜닉네임", SocialProvider.GOOGLE, "uid-1");

            CoreException result = assertThrows(CoreException.class, () -> user.completeOnboarding("일이삼사오육칠"));

            assertThat(result.getErrorType()).isEqualTo(ErrorType.INVALID_INPUT);
        }

        @DisplayName("숫자가 포함된 닉네임이면, INVALID_INPUT 예외가 발생한다.")
        @Test
        void throwsInvalidInput_whenNicknameContainsNumber() {
            User user = new User("test@omo.com", "소셜닉네임", SocialProvider.GOOGLE, "uid-1");

            CoreException result = assertThrows(CoreException.class, () -> user.completeOnboarding("햇살1"));

            assertThat(result.getErrorType()).isEqualTo(ErrorType.INVALID_INPUT);
        }

        @DisplayName("특수문자가 포함된 닉네임이면, INVALID_INPUT 예외가 발생한다.")
        @Test
        void throwsInvalidInput_whenNicknameContainsSpecialChar() {
            User user = new User("test@omo.com", "소셜닉네임", SocialProvider.GOOGLE, "uid-1");

            CoreException result = assertThrows(CoreException.class, () -> user.completeOnboarding("햇살!"));

            assertThat(result.getErrorType()).isEqualTo(ErrorType.INVALID_INPUT);
        }
    }
}
