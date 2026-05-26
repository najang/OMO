package com.omo.domain.user;

import java.util.Optional;

public interface UserRepository {
    Optional<User> find(Long id);
    Optional<User> findByProviderInfo(SocialProvider provider, String providerId);
    User save(User user);
}
