package com.omo.domain.user;

import java.util.List;
import java.util.Optional;

public interface DeviceTokenRepository {
    List<DeviceToken> findByUser(User user);
    Optional<DeviceToken> findByFcmToken(String fcmToken);
    DeviceToken save(DeviceToken deviceToken);
}
