package com.omo.domain.user;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.time.ZonedDateTime;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;

class DeviceTokenModelTest {

    private static final User USER = new User("test@omo.com", "테스터", SocialProvider.GOOGLE, "uid-123");

    @DisplayName("register로 기기 토큰을 생성할 때,")
    @Nested
    class Register {

        @DisplayName("User, fcmToken, deviceType이 주어지면, 각 필드가 그대로 세팅된다.")
        @Test
        void setsFields_whenArgumentsProvided() {
            // act
            DeviceToken token = DeviceToken.register(USER, "fcm-token-1", DeviceType.IOS);

            // assert
            assertAll(
                () -> assertThat(token.getUser()).isSameAs(USER),
                () -> assertThat(token.getFcmToken()).isEqualTo("fcm-token-1"),
                () -> assertThat(token.getDeviceType()).isEqualTo(DeviceType.IOS)
            );
        }

        @DisplayName("register로 생성하면, lastUsedAt이 호출 시각(now) 범위 내로 채워진다.")
        @Test
        void fillsLastUsedAtWithNow_whenRegistered() {
            // arrange
            ZonedDateTime before = ZonedDateTime.now();

            // act
            DeviceToken token = DeviceToken.register(USER, "fcm-token-2", DeviceType.ANDROID);

            // assert
            ZonedDateTime after = ZonedDateTime.now();
            assertAll(
                () -> assertThat(token.getLastUsedAt()).isNotNull(),
                () -> assertThat(token.getLastUsedAt()).isBetween(before, after)
            );
        }
    }

    @DisplayName("of로 기기 토큰을 복원할 때,")
    @Nested
    class Of {

        @DisplayName("모든 값이 주어지면, lastUsedAt까지 전달한 값 그대로 세팅된다.")
        @Test
        void setsAllFields_whenAllValuesProvided() {
            // arrange
            ZonedDateTime lastUsedAt = ZonedDateTime.now().minusDays(3);

            // act
            DeviceToken token = DeviceToken.of(USER, "fcm-token-3", DeviceType.ANDROID, lastUsedAt);

            // assert
            assertAll(
                () -> assertThat(token.getUser()).isSameAs(USER),
                () -> assertThat(token.getFcmToken()).isEqualTo("fcm-token-3"),
                () -> assertThat(token.getDeviceType()).isEqualTo(DeviceType.ANDROID),
                () -> assertThat(token.getLastUsedAt()).isEqualTo(lastUsedAt)
            );
        }
    }
}
