package com.omo.infrastructure.user;

import com.omo.domain.user.DeviceToken;
import com.omo.domain.user.User;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
import java.util.Optional;

public interface DeviceTokenJpaRepository extends JpaRepository<DeviceToken, Long> {
    List<DeviceToken> findByUser(User user);
    Optional<DeviceToken> findByFcmToken(String fcmToken);
}
