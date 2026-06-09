package com.omo.domain.wardrobe;

import com.omo.domain.user.SocialProvider;
import com.omo.domain.user.User;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.util.HashSet;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertAll;

class UserWardrobeModelTest {

    private static final User USER = new User("test@omo.com", "테스터", SocialProvider.GOOGLE, "uid-1");
    private static final ClothingItem TOP_ITEM = ClothingItem.of("short-tee", ClothingCategory.TOP, "반팔 티셔츠");
    private static final ClothingItem OUTER_ITEM = ClothingItem.of("padding", ClothingCategory.OUTER, "패딩");
    private static final ClothingItem PANTS_ITEM = ClothingItem.of("jeans", ClothingCategory.PANTS, "청바지");

    @DisplayName("옷장을 생성할 때,")
    @Nested
    class Create {

        @DisplayName("유저와 아이템 목록이 주어지면, 정상 생성된다.")
        @Test
        void createsWardrobe_withUserAndItems() {
            Set<ClothingItem> items = Set.of(TOP_ITEM, OUTER_ITEM);

            UserWardrobe wardrobe = UserWardrobe.create(USER, items);

            assertAll(
                () -> assertThat(wardrobe.getUser()).isSameAs(USER),
                () -> assertThat(wardrobe.getItems()).containsExactlyInAnyOrderElementsOf(items)
            );
        }

        @DisplayName("원본 아이템 Set을 변경해도, 옷장의 아이템은 영향을 받지 않는다.")
        @Test
        void items_areImmutableFromOriginalSet() {
            Set<ClothingItem> original = new HashSet<>(Set.of(TOP_ITEM));
            UserWardrobe wardrobe = UserWardrobe.create(USER, original);

            original.add(PANTS_ITEM);

            assertThat(wardrobe.getItems()).doesNotContain(PANTS_ITEM);
        }

        @DisplayName("getItems()가 반환한 Set은 수정할 수 없다.")
        @Test
        void getItems_returnsUnmodifiableSet() {
            UserWardrobe wardrobe = UserWardrobe.create(USER, Set.of(TOP_ITEM));

            assertThatThrownBy(() -> wardrobe.getItems().add(PANTS_ITEM))
                .isInstanceOf(UnsupportedOperationException.class);
        }
    }

    @DisplayName("아이템을 업데이트할 때,")
    @Nested
    class UpdateItems {

        @DisplayName("새로운 아이템 목록으로 교체된다.")
        @Test
        void replacesItems_withNewSet() {
            UserWardrobe wardrobe = UserWardrobe.create(USER, Set.of(TOP_ITEM));

            wardrobe.updateItems(Set.of(OUTER_ITEM, PANTS_ITEM));

            assertThat(wardrobe.getItems())
                .containsExactlyInAnyOrder(OUTER_ITEM, PANTS_ITEM)
                .doesNotContain(TOP_ITEM);
        }
    }
}
