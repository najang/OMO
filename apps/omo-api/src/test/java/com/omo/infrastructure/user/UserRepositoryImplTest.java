package com.omo.infrastructure.user;

import com.omo.domain.user.SocialProvider;
import com.omo.domain.user.User;
import com.omo.domain.user.UserRepository;
import com.omo.infrastructure.auth.social.AppleAuthClient;
import com.omo.infrastructure.auth.social.GoogleAuthClient;
import com.omo.infrastructure.auth.social.KakaoAuthClient;
import com.omo.utils.DatabaseCleanUp;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.test.context.bean.override.mockito.MockitoBean;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertThrows;

@SpringBootTest
class UserRepositoryImplTest {

    @MockitoBean
    private GoogleAuthClient googleAuthClient;

    @MockitoBean
    private KakaoAuthClient kakaoAuthClient;

    @MockitoBean
    private AppleAuthClient appleAuthClient;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private DatabaseCleanUp databaseCleanUp;

    @AfterEach
    void tearDown() {
        databaseCleanUp.truncateAllTables();
    }

    @DisplayName("ID로 유저를 조회할 때,")
    @Nested
    class Find {

        @DisplayName("존재하는 ID면, 유저를 반환한다.")
        @Test
        void returnsUser_whenUserExists() {
            // arrange
            User saved = userRepository.save(new User("find@omo.com", "조회유저", SocialProvider.GOOGLE, "uid-find"));

            // act
            Optional<User> result = userRepository.find(saved.getId());

            // assert
            assertThat(result).isPresent();
            assertThat(result.get().getId()).isEqualTo(saved.getId());
        }

        @DisplayName("존재하지 않는 ID면, 빈 Optional을 반환한다.")
        @Test
        void returnsEmpty_whenUserDoesNotExist() {
            // act
            Optional<User> result = userRepository.find(999L);

            // assert
            assertThat(result).isEmpty();
        }
    }

    @DisplayName("소셜 계정 정보로 유저를 조회할 때,")
    @Nested
    class FindByProviderInfo {

        @DisplayName("일치하는 provider/providerId 쌍이 있으면, 해당 유저를 반환한다.")
        @Test
        void returnsUser_whenProviderInfoMatches() {
            // arrange
            userRepository.save(new User("match@omo.com", "일치유저", SocialProvider.KAKAO, "kakao-uid-123"));

            // act
            Optional<User> result = userRepository.findByProviderInfo(SocialProvider.KAKAO, "kakao-uid-123");

            // assert
            assertAll(
                () -> assertThat(result).isPresent(),
                () -> assertThat(result.get().getProvider()).isEqualTo(SocialProvider.KAKAO),
                () -> assertThat(result.get().getProviderId()).isEqualTo("kakao-uid-123")
            );
        }

        @DisplayName("provider는 같지만 providerId가 다르면, 빈 Optional을 반환한다.")
        @Test
        void returnsEmpty_whenProviderIdDoesNotMatch() {
            // arrange
            userRepository.save(new User("test@omo.com", "유저", SocialProvider.GOOGLE, "google-uid-abc"));

            // act
            Optional<User> result = userRepository.findByProviderInfo(SocialProvider.GOOGLE, "google-uid-xyz");

            // assert
            assertThat(result).isEmpty();
        }

        @DisplayName("providerId는 같지만 provider가 다르면, 빈 Optional을 반환한다.")
        @Test
        void returnsEmpty_whenProviderDoesNotMatch() {
            // arrange
            userRepository.save(new User("test@omo.com", "유저", SocialProvider.GOOGLE, "uid-shared"));

            // act
            Optional<User> result = userRepository.findByProviderInfo(SocialProvider.APPLE, "uid-shared");

            // assert
            assertThat(result).isEmpty();
        }
    }

    @DisplayName("유저를 저장할 때,")
    @Nested
    class Save {

        @DisplayName("유효한 유저를 저장하면, ID가 할당된 유저를 반환한다.")
        @Test
        void returnsUserWithId_afterSave() {
            // arrange
            User user = new User("save@omo.com", "저장유저", SocialProvider.GOOGLE, "uid-save");

            // act
            User saved = userRepository.save(user);

            // assert
            assertAll(
                () -> assertThat(saved.getId()).isNotNull(),
                () -> assertThat(saved.getEmail()).isEqualTo("save@omo.com"),
                () -> assertThat(saved.getProvider()).isEqualTo(SocialProvider.GOOGLE)
            );
        }

        @DisplayName("동일한 provider/providerId로 두 번 저장하면, 유니크 제약 위반 예외가 발생한다.")
        @Test
        void throwsException_whenDuplicateProviderInfo() {
            // arrange
            userRepository.save(new User("first@omo.com", "첫번째유저", SocialProvider.GOOGLE, "uid-dup"));

            // act & assert
            assertThrows(DataIntegrityViolationException.class, () ->
                userRepository.save(new User("second@omo.com", "두번째유저", SocialProvider.GOOGLE, "uid-dup"))
            );
        }

        @DisplayName("같은 이메일이라도 provider가 다르면, 별개 유저로 저장된다.")
        @Test
        void savesBothUsers_whenSameEmailButDifferentProvider() {
            // act
            User google = userRepository.save(new User("same@omo.com", "구글유저", SocialProvider.GOOGLE, "google-uid"));
            User apple  = userRepository.save(new User("same@omo.com", "애플유저", SocialProvider.APPLE,  "apple-uid"));

            // assert
            assertThat(google.getId()).isNotEqualTo(apple.getId());
        }
    }

    @DisplayName("onboarding_completed 컬럼을 저장할 때,")
    @Nested
    class OnboardingPersistence {

        @DisplayName("신규 유저의 기본값은 false다.")
        @Test
        void defaultOnboardingCompleted_isFalse() {
            User saved = userRepository.save(new User("default@omo.com", "기본유저", SocialProvider.GOOGLE, "uid-default-ob"));

            Optional<User> found = userRepository.find(saved.getId());

            assertThat(found.get().isOnboardingCompleted()).isFalse();
        }

        @DisplayName("completeOnboarding 후 저장하면, onboarding_completed가 true로 영속된다.")
        @Test
        void persistsOnboardingCompleted_afterSave() {
            User saved = userRepository.save(new User("onboard@omo.com", "온보딩유저", SocialProvider.GOOGLE, "uid-ob-persist"));
            saved.completeOnboarding("햇살곰");
            userRepository.save(saved);

            Optional<User> reloaded = userRepository.find(saved.getId());

            assertAll(
                () -> assertThat(reloaded.get().getNickname()).isEqualTo("햇살곰"),
                () -> assertThat(reloaded.get().isOnboardingCompleted()).isTrue()
            );
        }
    }
}
