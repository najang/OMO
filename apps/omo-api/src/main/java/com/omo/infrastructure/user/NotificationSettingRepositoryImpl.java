package com.omo.infrastructure.user;

import com.omo.domain.user.NotificationSetting;
import com.omo.domain.user.NotificationSettingRepository;
import com.omo.domain.user.User;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import java.util.Optional;

@RequiredArgsConstructor
@Repository
public class NotificationSettingRepositoryImpl implements NotificationSettingRepository {

    private final NotificationSettingJpaRepository notificationSettingJpaRepository;

    @Override
    public Optional<NotificationSetting> findByUser(User user) {
        return notificationSettingJpaRepository.findByUser(user);
    }

    @Override
    public NotificationSetting save(NotificationSetting setting) {
        return notificationSettingJpaRepository.save(setting);
    }
}
