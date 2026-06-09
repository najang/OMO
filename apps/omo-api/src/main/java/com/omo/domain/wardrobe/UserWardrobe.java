package com.omo.domain.wardrobe;

import com.omo.domain.BaseEntity;
import com.omo.domain.user.User;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinTable;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "user_wardrobe")
public class UserWardrobe extends BaseEntity {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(
            name = "user_wardrobe_item",
            joinColumns = @JoinColumn(name = "wardrobe_id"),
            inverseJoinColumns = @JoinColumn(name = "item_id")
    )
    private Set<ClothingItem> items = new HashSet<>();

    protected UserWardrobe() {}

    public static UserWardrobe create(User user, Set<ClothingItem> items) {
        UserWardrobe wardrobe = new UserWardrobe();
        wardrobe.user = user;
        wardrobe.items = new HashSet<>(items);
        return wardrobe;
    }

    public void updateItems(Set<ClothingItem> items) {
        this.items.clear();
        this.items.addAll(items);
    }

    public User getUser() { return user; }
    public Set<ClothingItem> getItems() { return Set.copyOf(items); }
}