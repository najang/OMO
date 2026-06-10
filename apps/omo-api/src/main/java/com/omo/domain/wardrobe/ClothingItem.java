package com.omo.domain.wardrobe;

import com.omo.domain.BaseEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Table;

@Entity
@Table(name = "clothing_item")
public class ClothingItem extends BaseEntity {

    @Column(name = "system_key", nullable = false, unique = true)
    private String systemKey;

    @Column(name = "category", nullable = false)
    @Enumerated(EnumType.STRING)
    private ClothingCategory category;

    @Column(name = "display_group", nullable = false)
    @Enumerated(EnumType.STRING)
    private ClothingDisplayGroup displayGroup;

    @Column(name = "name_ko", nullable = false)
    private String nameKo;

    protected ClothingItem() {}

    public static ClothingItem of(String systemKey, ClothingCategory category, ClothingDisplayGroup displayGroup, String nameKo) {
        ClothingItem item = new ClothingItem();
        item.systemKey = systemKey;
        item.category = category;
        item.displayGroup = displayGroup;
        item.nameKo = nameKo;
        return item;
    }

    public String getSystemKey() { return systemKey; }
    public ClothingCategory getCategory() { return category; }
    public ClothingDisplayGroup getDisplayGroup() { return displayGroup; }
    public String getNameKo() { return nameKo; }
}
