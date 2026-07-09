package com.omo.infrastructure.user;

import com.omo.domain.user.NotificationSetting;
import com.omo.domain.user.User;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface NotificationSettingJpaRepository extends JpaRepository<NotificationSetting, Long> {
    Optional<NotificationSetting> findByUser(User user);
}
