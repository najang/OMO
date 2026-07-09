package com.omo.domain.user;

import com.omo.domain.BaseEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import java.time.LocalTime;

@Entity
@Table(name = "notification_setting")
public class NotificationSetting extends BaseEntity {

    private static final String DEFAULT_TIMEZONE = "Asia/Seoul";

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Column(name = "notification_time")
    private LocalTime notificationTime;

    @Column(name = "timezone", nullable = false)
    private String timezone;

    @Column(name = "location_lat")
    private String locationLat;

    @Column(name = "location_lon")
    private String locationLon;

    @Column(name = "location_name")
    private String locationName;

    @Column(name = "enabled", nullable = false)
    private boolean enabled;

    protected NotificationSetting() {}

    public static NotificationSetting init(User user) {
        return of(user, null, DEFAULT_TIMEZONE, null, null, null, true);
    }

    // 테스트 및 복원 시 사용
    public static NotificationSetting of(
            User user,
            LocalTime notificationTime,
            String timezone,
            String locationLat,
            String locationLon,
            String locationName,
            boolean enabled) {
        NotificationSetting setting = new NotificationSetting();
        setting.user = user;
        setting.notificationTime = notificationTime;
        setting.timezone = timezone;
        setting.locationLat = locationLat;
        setting.locationLon = locationLon;
        setting.locationName = locationName;
        setting.enabled = enabled;
        return setting;
    }

    public User getUser() { return user; }
    public LocalTime getNotificationTime() { return notificationTime; }
    public String getTimezone() { return timezone; }
    public String getLocationLat() { return locationLat; }
    public String getLocationLon() { return locationLon; }
    public String getLocationName() { return locationName; }
    public boolean isEnabled() { return enabled; }
}
