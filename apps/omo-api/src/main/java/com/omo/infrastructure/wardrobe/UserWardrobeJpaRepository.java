package com.omo.infrastructure.wardrobe;

import com.omo.domain.user.User;
import com.omo.domain.wardrobe.UserWardrobe;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface UserWardrobeJpaRepository extends JpaRepository<UserWardrobe, Long> {
    Optional<UserWardrobe> findByUser(User user);
}
