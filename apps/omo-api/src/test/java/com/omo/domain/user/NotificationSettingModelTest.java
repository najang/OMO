package com.omo.domain.user;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.time.LocalTime;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;

class NotificationSettingModelTest {

    private static final User USER = new User("test@omo.com", "테스터", SocialProvider.GOOGLE, "uid-123");

    @DisplayName("init으로 알림 설정을 생성할 때,")
    @Nested
    class Init {

        @DisplayName("User가 주어지면, timezone=Asia/Seoul, enabled=true 기본값으로 초기화된다.")
        @Test
        void initializesWithDefaults_whenUserIsProvided() {
            // act
            NotificationSetting setting = NotificationSetting.init(USER);

            // assert
            assertAll(
                () -> assertThat(setting.getUser()).isSameAs(USER),
                () -> assertThat(setting.getTimezone()).isEqualTo("Asia/Seoul"),
                () -> assertThat(setting.isEnabled()).isTrue()
            );
        }

        @DisplayName("User가 주어지면, notificationTime과 location 3종은 null로 초기화된다.")
        @Test
        void initializesNullableFieldsAsNull_whenUserIsProvided() {
            // act
            NotificationSetting setting = NotificationSetting.init(USER);

            // assert
            assertAll(
                () -> assertThat(setting.getNotificationTime()).isNull(),
                () -> assertThat(setting.getLocationLat()).isNull(),
                () -> assertThat(setting.getLocationLon()).isNull(),
                () -> assertThat(setting.getLocationName()).isNull()
            );
        }
    }

    @DisplayName("of로 알림 설정을 생성할 때,")
    @Nested
    class Of {

        @DisplayName("모든 값이 주어지면, 각 필드가 전달한 값 그대로 세팅된다.")
        @Test
        void setsAllFields_whenAllValuesProvided() {
            // arrange
            LocalTime time = LocalTime.of(7, 30);

            // act
            NotificationSetting setting = NotificationSetting.of(
                USER, time, "Asia/Tokyo", "37.5665", "126.9780", "서울시청", false);

            // assert
            assertAll(
                () -> assertThat(setting.getUser()).isSameAs(USER),
                () -> assertThat(setting.getNotificationTime()).isEqualTo(time),
                () -> assertThat(setting.getTimezone()).isEqualTo("Asia/Tokyo"),
                () -> assertThat(setting.getLocationLat()).isEqualTo("37.5665"),
                () -> assertThat(setting.getLocationLon()).isEqualTo("126.9780"),
                () -> assertThat(setting.getLocationName()).isEqualTo("서울시청"),
                () -> assertThat(setting.isEnabled()).isFalse()
            );
        }

        @DisplayName("enabled=true를 전달하면, isEnabled가 true를 반환한다.")
        @Test
        void isEnabledTrue_whenEnabledIsTrue() {
            // act
            NotificationSetting setting = NotificationSetting.of(
                USER, null, "Asia/Seoul", null, null, null, true);

            // assert
            assertThat(setting.isEnabled()).isTrue();
        }
    }
}
