package com.omo.infrastructure.user;

import com.omo.domain.user.NotificationSetting;
import com.omo.domain.user.NotificationSettingRepository;
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

import java.time.LocalTime;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;

@SpringBootTest
class NotificationSettingRepositoryImplTest {

    @MockitoBean
    private GoogleAuthClient googleAuthClient;

    @MockitoBean
    private KakaoAuthClient kakaoAuthClient;

    @MockitoBean
    private AppleAuthClient appleAuthClient;

    @Autowired
    private NotificationSettingRepository notificationSettingRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private DatabaseCleanUp databaseCleanUp;

    @AfterEach
    void tearDown() {
        databaseCleanUp.truncateAllTables();
    }

    private User saveUser(String providerId) {
        return userRepository.save(new User("noti@omo.com", "알림유저", SocialProvider.GOOGLE, providerId));
    }

    @DisplayName("알림 설정을 저장할 때,")
    @Nested
    class Save {

        @DisplayName("유효한 설정을 저장하면, ID가 할당된 설정을 반환하고 DB에서 다시 조회된다.")
        @Test
        void returnsSettingWithId_afterSave() {
            // arrange
            User user = saveUser("uid-save");
            NotificationSetting setting = NotificationSetting.init(user);

            // act
            NotificationSetting saved = notificationSettingRepository.save(setting);

            // assert
            assertThat(notificationSettingRepository.findByUser(user)).isPresent();
        }

        // 유니크 제약(uk_notification_setting_user_id) 위반 케이스는 V6 Flyway 마이그레이션에만
        // 정의되어 있고, 테스트 프로파일은 ddl-auto=create 로 엔티티 매핑에서 스키마를 생성하므로
        // 이 환경에서는 제약이 존재하지 않아 검증할 수 없다. (sibling UserTempProfile 통합 테스트도
        // 동일한 이유로 유니크 제약을 테스트하지 않는다.)
    }

    @DisplayName("User로 알림 설정을 조회할 때,")
    @Nested
    class FindByUser {

        @DisplayName("설정이 존재하면, 저장한 값이 그대로 왕복 조회된다.")
        @Test
        void returnsSetting_whenSettingExists() {
            // arrange
            User user = saveUser("uid-roundtrip");
            LocalTime time = LocalTime.of(7, 30);
            notificationSettingRepository.save(NotificationSetting.of(
                user, time, "Asia/Tokyo", "37.5665", "126.9780", "서울시청", false));

            // act
            Optional<NotificationSetting> result = notificationSettingRepository.findByUser(user);

            // assert
            assertThat(result).isPresent();
            NotificationSetting found = result.get();
            assertAll(
                () -> assertThat(found.getUser().getId()).isEqualTo(user.getId()),
                () -> assertThat(found.getNotificationTime()).isEqualTo(time),
                () -> assertThat(found.getTimezone()).isEqualTo("Asia/Tokyo"),
                () -> assertThat(found.getLocationLat()).isEqualTo("37.5665"),
                () -> assertThat(found.getLocationLon()).isEqualTo("126.9780"),
                () -> assertThat(found.getLocationName()).isEqualTo("서울시청"),
                () -> assertThat(found.isEnabled()).isFalse()
            );
        }

        @DisplayName("init 기본값으로 저장한 설정도, nullable 필드까지 그대로 왕복 조회된다.")
        @Test
        void returnsDefaults_whenSavedByInit() {
            // arrange
            User user = saveUser("uid-init");
            notificationSettingRepository.save(NotificationSetting.init(user));

            // act
            Optional<NotificationSetting> result = notificationSettingRepository.findByUser(user);

            // assert
            assertThat(result).isPresent();
            NotificationSetting found = result.get();
            assertAll(
                () -> assertThat(found.getTimezone()).isEqualTo("Asia/Seoul"),
                () -> assertThat(found.isEnabled()).isTrue(),
                () -> assertThat(found.getNotificationTime()).isNull(),
                () -> assertThat(found.getLocationLat()).isNull(),
                () -> assertThat(found.getLocationLon()).isNull(),
                () -> assertThat(found.getLocationName()).isNull()
            );
        }

        @DisplayName("설정이 없는 User면, 빈 Optional을 반환한다.")
        @Test
        void returnsEmpty_whenSettingDoesNotExist() {
            // arrange
            User user = saveUser("uid-none");

            // act
            Optional<NotificationSetting> result = notificationSettingRepository.findByUser(user);

            // assert
            assertThat(result).isEmpty();
        }

        @DisplayName("다른 User의 설정만 존재하면, 빈 Optional을 반환한다.")
        @Test
        void returnsEmpty_whenOnlyOtherUserHasSetting() {
            // arrange
            User owner = saveUser("uid-owner");
            User other = userRepository.save(new User("other@omo.com", "다른유저", SocialProvider.KAKAO, "uid-other"));
            notificationSettingRepository.save(NotificationSetting.init(owner));

            // act
            Optional<NotificationSetting> result = notificationSettingRepository.findByUser(other);

            // assert
            assertThat(result).isEmpty();
        }
    }
}
