package com.omo.infrastructure.user;

import com.omo.domain.user.User;
import com.omo.domain.user.UserTempProfile;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface UserTempProfileJpaRepository extends JpaRepository<UserTempProfile, Long> {
    Optional<UserTempProfile> findByUser(User user);
}
