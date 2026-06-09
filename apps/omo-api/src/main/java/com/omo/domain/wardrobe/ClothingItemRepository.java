package com.omo.domain.wardrobe;

import java.util.Collection;
import java.util.List;

public interface ClothingItemRepository {
    List<ClothingItem> findAllBySystemKeyIn(Collection<String> systemKeys);
    List<ClothingItem> saveAll(Iterable<ClothingItem> items);
}
