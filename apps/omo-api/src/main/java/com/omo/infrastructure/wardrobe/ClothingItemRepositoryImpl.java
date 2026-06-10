package com.omo.infrastructure.wardrobe;

import com.omo.domain.wardrobe.ClothingItem;
import com.omo.domain.wardrobe.ClothingItemRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.Collection;
import java.util.List;

@RequiredArgsConstructor
@Repository
public class ClothingItemRepositoryImpl implements ClothingItemRepository {

    private final ClothingItemJpaRepository clothingItemJpaRepository;

    @Override
    public List<ClothingItem> findAll() {
        return clothingItemJpaRepository.findAll();
    }

    @Override
    public List<ClothingItem> findAllBySystemKeyIn(Collection<String> systemKeys) {
        return clothingItemJpaRepository.findAllBySystemKeyIn(systemKeys);
    }

    @Override
    public List<ClothingItem> saveAll(Iterable<ClothingItem> items) {
        return clothingItemJpaRepository.saveAll(items);
    }
}