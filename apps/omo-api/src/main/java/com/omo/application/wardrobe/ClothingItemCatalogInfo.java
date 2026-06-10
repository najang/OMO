package com.omo.application.wardrobe;

import java.util.List;

public record ClothingItemCatalogInfo(List<ItemInfo> items) {
    public record ItemInfo(String systemKey, String category, String displayGroup, String nameKo) {}
}
