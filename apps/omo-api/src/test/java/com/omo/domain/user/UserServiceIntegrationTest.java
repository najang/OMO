package com.omo.domain.user;

import com.omo.infrastructure.user.UserJpaRepository;
import com.omo.support.error.CoreException;
import com.omo.support.error.ErrorType;
import com.omo.utils.DatabaseCleanUp;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertThrows;

@SpringBootTest
class UserServiceIntegrationTest {

    @Autowired
    private UserService userService;

    @Autowired
    private UserJpaRepository userJpaRepository;

    @Autowired
    private DatabaseCleanUp databaseCleanUp;

    @AfterEach
    void tearDown() {
        databaseCleanUp.truncateAllTables();
    }

    @DisplayName("ID로 유저를 조회할 때,")
    @Nested
    class FindById {

        @DisplayName("존재하는 ID를 주면, 해당 유저를 반환한다.")
        @Test
        void returnsUser_whenUserExists() {
            // arrange
            User saved = createUser("find@omo.com", "조회유저", SocialProvider.GOOGLE, "google-uid-find");

            // act
            User result = userService.getUser(saved.getId());

            // assert
            assertAll(
                () -> assertThat(result.getId()).isEqualTo(saved.getId()),
                () -> assertThat(result.getEmail()).isEqualTo(saved.getEmail()),
                () -> assertThat(result.getNickname()).isEqualTo(saved.getNickname())
            );
        }

        @DisplayName("존재하지 않는 ID를 주면, NOT_FOUND 예외가 발생한다.")
        @Test
        void throwsNotFound_whenUserDoesNotExist() {
            // arrange
            Long nonExistentId = 999L;

            // act
            CoreException result = assertThrows(CoreException.class, () ->
                userService.getUser(nonExistentId)
            );

            // assert
            assertThat(result.getErrorType()).isEqualTo(ErrorType.USER_NOT_FOUND);
        }
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
            createUser("same@omo.com", "구글유저", SocialProvider.GOOGLE, "google-uid-abc");

            // act — 같은 이메일이지만 provider가 다름 (APPLE)
            User result = userService.getOrCreateUser(
                "same@omo.com", "애플유저", SocialProvider.APPLE, "apple-uid-abc"
            );

            // assert
            assertThat(userJpaRepository.count()).isEqualTo(2);
            assertThat(result.getProvider()).isEqualTo(SocialProvider.APPLE);
        }
    }

    private User createUser(String email, String nickname, SocialProvider provider, String providerId) {
        return userService.getOrCreateUser(email, nickname, provider, providerId);
    }
}
