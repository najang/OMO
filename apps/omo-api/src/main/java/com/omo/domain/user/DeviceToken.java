package com.omo.domain.user;

import com.omo.domain.BaseEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import java.time.ZonedDateTime;

@Entity
@Table(name = "device_token")
public class DeviceToken extends BaseEntity {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Column(name = "fcm_token", nullable = false)
    private String fcmToken;

    @Enumerated(EnumType.STRING)
    @Column(name = "device_type", nullable = false)
    private DeviceType deviceType;

    @Column(name = "last_used_at", nullable = false)
    private ZonedDateTime lastUsedAt;

    protected DeviceToken() {}

    public static DeviceToken register(User user, String fcmToken, DeviceType deviceType) {
        return of(user, fcmToken, deviceType, ZonedDateTime.now());
    }

    // 테스트 및 복원 시 사용
    public static DeviceToken of(
            User user,
            String fcmToken,
            DeviceType deviceType,
            ZonedDateTime lastUsedAt) {
        DeviceToken token = new DeviceToken();
        token.user = user;
        token.fcmToken = fcmToken;
        token.deviceType = deviceType;
        token.lastUsedAt = lastUsedAt;
        return token;
    }

    public User getUser() { return user; }
    public String getFcmToken() { return fcmToken; }
    public DeviceType getDeviceType() { return deviceType; }
    public ZonedDateTime getLastUsedAt() { return lastUsedAt; }
}
