package com.omo.infrastructure.user;

import com.omo.domain.user.SocialProvider;
import com.omo.domain.user.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserJpaRepository extends JpaRepository<User, Long> {
    Optional<User> findByProviderAndProviderId(SocialProvider provider, String providerId);
}
