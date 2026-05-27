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

        @DisplayName("이메일이 null이면, BAD_REQUEST 예외가 발생한다.")
        @Test
        void throwsBadRequest_whenEmailIsNull() {
            CoreException result = assertThrows(CoreException.class, () ->
                new User(null, "테스터", SocialProvider.GOOGLE, "uid-123")
            );
            assertThat(result.getErrorType()).isEqualTo(ErrorType.INVALID_INPUT);
        }

        @DisplayName("이메일이 비어있으면, BAD_REQUEST 예외가 발생한다.")
        @Test
        void throwsBadRequest_whenEmailIsBlank() {
            CoreException result = assertThrows(CoreException.class, () ->
                new User("  ", "테스터", SocialProvider.GOOGLE, "uid-123")
            );
            assertThat(result.getErrorType()).isEqualTo(ErrorType.INVALID_INPUT);
        }

        @DisplayName("닉네임이 null이면, BAD_REQUEST 예외가 발생한다.")
        @Test
        void throwsBadRequest_whenNicknameIsNull() {
            CoreException result = assertThrows(CoreException.class, () ->
                new User("test@omo.com", null, SocialProvider.GOOGLE, "uid-123")
            );
            assertThat(result.getErrorType()).isEqualTo(ErrorType.INVALID_INPUT);
        }

        @DisplayName("닉네임이 비어있으면, BAD_REQUEST 예외가 발생한다.")
        @Test
        void throwsBadRequest_whenNicknameIsBlank() {
            CoreException result = assertThrows(CoreException.class, () ->
                new User("test@omo.com", "", SocialProvider.GOOGLE, "uid-123")
            );
            assertThat(result.getErrorType()).isEqualTo(ErrorType.INVALID_INPUT);
        }

        @DisplayName("소셜 프로바이더가 null이면, BAD_REQUEST 예외가 발생한다.")
        @Test
        void throwsBadRequest_whenProviderIsNull() {
            CoreException result = assertThrows(CoreException.class, () ->
                new User("test@omo.com", "테스터", null, "uid-123")
            );
            assertThat(result.getErrorType()).isEqualTo(ErrorType.INVALID_INPUT);
        }

        @DisplayName("프로바이더 ID가 null이면, BAD_REQUEST 예외가 발생한다.")
        @Test
        void throwsBadRequest_whenProviderIdIsNull() {
            CoreException result = assertThrows(CoreException.class, () ->
                new User("test@omo.com", "테스터", SocialProvider.GOOGLE, null)
            );
            assertThat(result.getErrorType()).isEqualTo(ErrorType.INVALID_INPUT);
        }

        @DisplayName("프로바이더 ID가 비어있으면, BAD_REQUEST 예외가 발생한다.")
        @Test
        void throwsBadRequest_whenProviderIdIsBlank() {
            CoreException result = assertThrows(CoreException.class, () ->
                new User("test@omo.com", "테스터", SocialProvider.GOOGLE, "  ")
            );
            assertThat(result.getErrorType()).isEqualTo(ErrorType.INVALID_INPUT);
        }
    }

    @DisplayName("닉네임을 변경할 때,")
    @Nested
    class UpdateNickname {

        @DisplayName("유효한 닉네임이 주어지면, 닉네임만 변경되고 다른 필드는 그대로다.")
        @Test
        void updatesNickname_whenValidNicknameIsProvided() {
            // arrange
            User user = new User("test@omo.com", "기존닉네임", SocialProvider.KAKAO, "uid-456");

            // act
            user.updateNickname("새닉네임");

            // assert
            assertAll(
                () -> assertThat(user.getNickname()).isEqualTo("새닉네임"),
                () -> assertThat(user.getEmail()).isEqualTo("test@omo.com"),
                () -> assertThat(user.getProvider()).isEqualTo(SocialProvider.KAKAO),
                () -> assertThat(user.getProviderId()).isEqualTo("uid-456")
            );
        }

        @DisplayName("닉네임이 null이면, BAD_REQUEST 예외가 발생한다.")
        @Test
        void throwsBadRequest_whenNicknameIsNull() {
            // arrange
            User user = new User("test@omo.com", "기존닉네임", SocialProvider.KAKAO, "uid-456");

            // act
            CoreException result = assertThrows(CoreException.class, () ->
                user.updateNickname(null)
            );

            // assert
            assertThat(result.getErrorType()).isEqualTo(ErrorType.INVALID_INPUT);
        }

        @DisplayName("닉네임이 비어있으면, BAD_REQUEST 예외가 발생한다.")
        @Test
        void throwsBadRequest_whenNicknameIsBlank() {
            // arrange
            User user = new User("test@omo.com", "기존닉네임", SocialProvider.KAKAO, "uid-456");

            // act
            CoreException result = assertThrows(CoreException.class, () ->
                user.updateNickname("   ")
            );

            // assert
            assertThat(result.getErrorType()).isEqualTo(ErrorType.INVALID_INPUT);
        }
    }
}
