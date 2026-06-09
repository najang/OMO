package com.omo.domain.wardrobe;

import com.omo.domain.user.User;
import java.util.Optional;

public interface UserWardrobeRepository {
    Optional<UserWardrobe> findByUser(User user);
    UserWardrobe save(UserWardrobe wardrobe);
}
