package com.omo.infrastructure.user;

import com.omo.domain.user.User;
import com.omo.domain.user.UserTempProfile;
import com.omo.domain.user.UserTempProfileRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import java.util.Optional;

@RequiredArgsConstructor
@Repository
public class UserTempProfileRepositoryImpl implements UserTempProfileRepository {

    private final UserTempProfileJpaRepository userTempProfileJpaRepository;

    @Override
    public Optional<UserTempProfile> findByUser(User user) {
        return userTempProfileJpaRepository.findByUser(user);
    }

    @Override
    public UserTempProfile save(UserTempProfile profile) {
        return userTempProfileJpaRepository.save(profile);
    }
}
