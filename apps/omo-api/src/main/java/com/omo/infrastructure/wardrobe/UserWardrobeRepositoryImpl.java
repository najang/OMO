package com.omo.infrastructure.wardrobe;

import com.omo.domain.user.User;
import com.omo.domain.wardrobe.UserWardrobe;
import com.omo.domain.wardrobe.UserWardrobeRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import java.util.Optional;

@RequiredArgsConstructor
@Repository
public class UserWardrobeRepositoryImpl implements UserWardrobeRepository {

    private final UserWardrobeJpaRepository userWardrobeJpaRepository;

    @Override
    public Optional<UserWardrobe> findByUser(User user) {
        return userWardrobeJpaRepository.findByUser(user);
    }

    @Override
    public UserWardrobe save(UserWardrobe wardrobe) {
        return userWardrobeJpaRepository.save(wardrobe);
    }
}
