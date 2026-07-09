package com.omo.infrastructure.user;

import com.omo.domain.user.DeviceToken;
import com.omo.domain.user.DeviceTokenRepository;
import com.omo.domain.user.User;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;

@RequiredArgsConstructor
@Repository
public class DeviceTokenRepositoryImpl implements DeviceTokenRepository {

    private final DeviceTokenJpaRepository deviceTokenJpaRepository;

    @Override
    public List<DeviceToken> findByUser(User user) {
        return deviceTokenJpaRepository.findByUser(user);
    }

    @Override
    public Optional<DeviceToken> findByFcmToken(String fcmToken) {
        return deviceTokenJpaRepository.findByFcmToken(fcmToken);
    }

    @Override
    public DeviceToken save(DeviceToken deviceToken) {
        return deviceTokenJpaRepository.save(deviceToken);
    }
}
