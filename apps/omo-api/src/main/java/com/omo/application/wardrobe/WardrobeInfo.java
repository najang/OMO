package com.omo.application.wardrobe;

import java.util.List;

public record WardrobeInfo(List<ItemInfo> items) {
    public record ItemInfo(String systemKey, String category, String nameKo) {}
}
