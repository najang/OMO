package com.omo.infrastructure.user;

import com.omo.domain.user.DeviceToken;
import com.omo.domain.user.DeviceTokenRepository;
import com.omo.domain.user.DeviceType;
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
import org.springframework.test.context.bean.override.mockito.MockitoBean;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;

@SpringBootTest
class DeviceTokenRepositoryImplTest {

    @MockitoBean
    private GoogleAuthClient googleAuthClient;

    @MockitoBean
    private KakaoAuthClient kakaoAuthClient;

    @MockitoBean
    private AppleAuthClient appleAuthClient;

    @Autowired
    private DeviceTokenRepository deviceTokenRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private DatabaseCleanUp databaseCleanUp;

    @AfterEach
    void tearDown() {
        databaseCleanUp.truncateAllTables();
    }

    private User saveUser(String providerId) {
        return userRepository.save(new User("device@omo.com", "기기유저", SocialProvider.GOOGLE, providerId));
    }

    @DisplayName("기기 토큰을 저장할 때,")
    @Nested
    class Save {

        @DisplayName("유효한 토큰을 저장하면, fcmToken으로 다시 조회된다.")
        @Test
        void returnsToken_afterSave() {
            // arrange
            User user = saveUser("uid-save");

            // act
            deviceTokenRepository.save(DeviceToken.register(user, "fcm-save-1", DeviceType.IOS));

            // assert
            assertThat(deviceTokenRepository.findByFcmToken("fcm-save-1")).isPresent();
        }

        // fcm_token 유니크 제약(uk_device_token_fcm_token)은 V7 Flyway 마이그레이션에만 정의되어 있고
        // 엔티티 매핑(@Column(name="fcm_token", nullable=false))에는 unique=true 나
        // @Table(uniqueConstraints=...) 가 없다. 테스트 프로파일은 ddl-auto=create 로 엔티티 매핑에서
        // 스키마를 생성하므로, 이 환경에서는 제약이 존재하지 않아 유니크 위반을 검증할 수 없다.
        // (OMO-24 NotificationSetting 통합 테스트에서 관측된 엔티티 매핑 vs Flyway 불일치와 동일.)
    }

    @DisplayName("fcmToken으로 기기 토큰을 조회할 때,")
    @Nested
    class FindByFcmToken {

        @DisplayName("토큰이 존재하면, 저장한 값이 그대로 왕복 조회된다.")
        @Test
        void returnsToken_whenTokenExists() {
            // arrange
            User user = saveUser("uid-roundtrip");
            deviceTokenRepository.save(DeviceToken.register(user, "fcm-roundtrip", DeviceType.ANDROID));

            // act
            Optional<DeviceToken> result = deviceTokenRepository.findByFcmToken("fcm-roundtrip");

            // assert
            assertThat(result).isPresent();
            DeviceToken found = result.get();
            assertAll(
                () -> assertThat(found.getUser().getId()).isEqualTo(user.getId()),
                () -> assertThat(found.getFcmToken()).isEqualTo("fcm-roundtrip"),
                () -> assertThat(found.getDeviceType()).isEqualTo(DeviceType.ANDROID),
                () -> assertThat(found.getLastUsedAt()).isNotNull()
            );
        }

        @DisplayName("존재하지 않는 fcmToken이면, 빈 Optional을 반환한다.")
        @Test
        void returnsEmpty_whenTokenDoesNotExist() {
            // act
            Optional<DeviceToken> result = deviceTokenRepository.findByFcmToken("no-such-token");

            // assert
            assertThat(result).isEmpty();
        }
    }

    @DisplayName("User로 기기 토큰 목록을 조회할 때,")
    @Nested
    class FindByUser {

        @DisplayName("한 User가 서로 다른 fcmToken으로 기기 2개를 등록하면, 2건이 모두 조회된다.")
        @Test
        void returnsAllTokens_whenUserHasMultipleDevices() {
            // arrange
            User user = saveUser("uid-multi");
            deviceTokenRepository.save(DeviceToken.register(user, "fcm-phone", DeviceType.IOS));
            deviceTokenRepository.save(DeviceToken.register(user, "fcm-tablet", DeviceType.ANDROID));

            // act
            List<DeviceToken> result = deviceTokenRepository.findByUser(user);

            // assert
            assertAll(
                () -> assertThat(result).hasSize(2),
                () -> assertThat(result).extracting(DeviceToken::getFcmToken)
                    .containsExactlyInAnyOrder("fcm-phone", "fcm-tablet")
            );
        }

        @DisplayName("등록한 기기가 없는 User면, 빈 목록을 반환한다.")
        @Test
        void returnsEmptyList_whenUserHasNoDevice() {
            // arrange
            User user = saveUser("uid-none");

            // act
            List<DeviceToken> result = deviceTokenRepository.findByUser(user);

            // assert
            assertThat(result).isEmpty();
        }

        @DisplayName("다른 User의 기기만 존재하면, 빈 목록을 반환한다.")
        @Test
        void returnsEmptyList_whenOnlyOtherUserHasDevice() {
            // arrange
            User owner = saveUser("uid-owner");
            User other = userRepository.save(new User("other@omo.com", "다른유저", SocialProvider.KAKAO, "uid-other"));
            deviceTokenRepository.save(DeviceToken.register(owner, "fcm-owner", DeviceType.IOS));

            // act
            List<DeviceToken> result = deviceTokenRepository.findByUser(other);

            // assert
            assertThat(result).isEmpty();
        }
    }
}
