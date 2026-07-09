package com.omo.application.wardrobe;

import com.omo.domain.user.User;
import com.omo.domain.user.UserService;
import com.omo.domain.wardrobe.ClothingItem;
import com.omo.domain.wardrobe.ClothingItemRepository;
import com.omo.domain.wardrobe.TempSensitivity;
import com.omo.domain.wardrobe.UserWardrobeRepository;
import com.omo.domain.wardrobe.UserWardrobeService;
import com.omo.support.error.CoreException;
import com.omo.support.error.ErrorType;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

@RequiredArgsConstructor
@Component
public class WardrobeFacade {

    private final UserService userService;
    private final UserWardrobeService userWardrobeService;
    private final UserWardrobeRepository userWardrobeRepository;
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

    @Transactional(readOnly = true)
    public WardrobeInfo getWardrobe(Long userId) {
        User user = userService.getUser(userId);
        var wardrobe = userWardrobeRepository.findByUser(user)
                .orElseThrow(() -> new CoreException(ErrorType.WARDROBE_NOT_FOUND, "옷장을 찾을 수 없습니다."));
        List<WardrobeInfo.ItemInfo> items = wardrobe.getItems().stream()
                .map(item -> new WardrobeInfo.ItemInfo(item.getSystemKey(), item.getCategory().name(), item.getNameKo()))
                .toList();
        return new WardrobeInfo(items);
    }

    public void updateWardrobe(Long userId, List<String> itemKeys) {
        setupWardrobe(userId, itemKeys);
    }

    public ClothingItemCatalogInfo getClothingItemCatalog() {
        List<ClothingItemCatalogInfo.ItemInfo> items = clothingItemRepository.findAll().stream()
                .map(item -> new ClothingItemCatalogInfo.ItemInfo(
                        item.getSystemKey(),
                        item.getCategory().name(),
                        item.getDisplayGroup().name(),
                        item.getNameKo()))
                .toList();
        return new ClothingItemCatalogInfo(items);
    }
}