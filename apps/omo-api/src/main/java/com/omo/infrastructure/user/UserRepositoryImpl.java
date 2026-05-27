package com.omo.infrastructure.user;

import com.omo.domain.user.SocialProvider;
import com.omo.domain.user.User;
import com.omo.domain.user.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.Optional;

@RequiredArgsConstructor
@Component
public class UserRepositoryImpl implements UserRepository {

    private final UserJpaRepository userJpaRepository;

    @Override
    public Optional<User> find(Long id) {
        return userJpaRepository.findById(id);
    }

    @Override
    public Optional<User> findByProviderInfo(SocialProvider provider, String providerId) {
        return userJpaRepository.findByProviderAndProviderId(provider, providerId);
    }

    @Override
    public User save(User user) {
        return userJpaRepository.save(user);
    }
}
