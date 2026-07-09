package com.omo.domain.user;

import java.util.Optional;

public interface NotificationSettingRepository {
    Optional<NotificationSetting> findByUser(User user);
    NotificationSetting save(NotificationSetting setting);
}
