package com.omo.application.wardrobe;

import com.omo.domain.user.User;
import com.omo.domain.user.UserService;
import com.omo.domain.wardrobe.ClothingItem;
import com.omo.domain.wardrobe.ClothingItemRepository;
import com.omo.domain.wardrobe.TempSensitivity;
import com.omo.domain.wardrobe.UserWardrobeService;
import com.omo.support.error.CoreException;
import com.omo.support.error.ErrorType;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

@RequiredArgsConstructor
@Component
public class WardrobeFacade {

    private final UserService userService;
    private final UserWardrobeService userWardrobeService;
    private final ClothingItemRepository clothingItemRepository;

    public void setupWardrobe(Long userId, List<String> itemKeys) {
        if (itemKeys == null || itemKeys.isEmpty()) {
            throw new CoreException(ErrorType.INVALID_INPUT, "아이템을 최소 1개 이상 선택해야 합니다.");
        }
        Set<String> uniqueKeys = new HashSet<>(itemKeys);
        List<ClothingItem> found = clothingItemRepository.findAllBySystemKeyIn(uniqueKeys);
        if (found.size() != uniqueKeys.size()) {
            throw new CoreException(ErrorType.INVALID_INPUT, "유효하지 않은 아이템 키가 포함되어 있습니다.");
        }
        User user = userService.getUser(userId);
        userWardrobeService.setupWardrobe(user, new HashSet<>(found));
    }

    public void initTempProfile(Long userId, TempSensitivity tempSensitivity) {
        if (tempSensitivity == null) {
            throw new CoreException(ErrorType.INVALID_INPUT, "체감 민감도를 선택해야 합니다.");
        }
        User user = userService.getUser(userId);
        userService.initTempProfile(user, tempSensitivity.toTempOffset());
    }
}