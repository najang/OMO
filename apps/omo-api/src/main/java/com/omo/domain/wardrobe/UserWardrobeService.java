package com.omo.domain.wardrobe;

import com.omo.domain.user.User;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import java.util.Set;

@RequiredArgsConstructor
@Component
public class UserWardrobeService {

    private final UserWardrobeRepository userWardrobeRepository;

    @Transactional
    public void setupWardrobe(User user, Set<ClothingItem> items) {
        userWardrobeRepository.findByUser(user)
                .ifPresentOrElse(
                        wardrobe -> wardrobe.updateItems(items),
                        () -> userWardrobeRepository.save(UserWardrobe.create(user, items))
                );
    }
}