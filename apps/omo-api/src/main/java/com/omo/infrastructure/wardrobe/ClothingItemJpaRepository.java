package com.omo.infrastructure.wardrobe;

import com.omo.domain.wardrobe.ClothingItem;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Collection;
import java.util.List;

public interface ClothingItemJpaRepository extends JpaRepository<ClothingItem, Long> {
    List<ClothingItem> findAllBySystemKeyIn(Collection<String> systemKeys);
}