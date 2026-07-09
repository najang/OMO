package com.omo.domain.wardrobe;

import com.omo.domain.user.SocialProvider;
import com.omo.domain.user.User;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class UserWardrobeServiceTest {

    @Mock
    private UserWardrobeRepository userWardrobeRepository;

    @InjectMocks
    private UserWardrobeService userWardrobeService;

    private static final User USER = new User("test@omo.com", "테스터", SocialProvider.GOOGLE, "uid-1");
    private static final ClothingItem TOP = ClothingItem.of("short-tee", ClothingCategory.TOP, ClothingDisplayGroup.TOP, "반팔 티셔츠");
    private static final ClothingItem OUTER = ClothingItem.of("padding", ClothingCategory.OUTER, ClothingDisplayGroup.OUTER, "패딩");
    private static final ClothingItem PANTS = ClothingItem.of("jeans", ClothingCategory.PANTS, ClothingDisplayGroup.BOTTOM, "청바지");

    @DisplayName("옷장을 설정할 때,")
    @Nested
    class SetupWardrobe {

        @DisplayName("옷장이 없으면, 새로 생성하고 저장한다.")
        @Test
        void savesNewWardrobe_whenNotExists() {
            when(userWardrobeRepository.findByUser(USER)).thenReturn(Optional.empty());
            Set<ClothingItem> items = Set.of(TOP, OUTER);

            userWardrobeService.setupWardrobe(USER, items);

            ArgumentCaptor<UserWardrobe> captor = ArgumentCaptor.forClass(UserWardrobe.class);
            verify(userWardrobeRepository).save(captor.capture());
            assertThat(captor.getValue().getItems()).containsExactlyInAnyOrderElementsOf(items);
        }

        @DisplayName("옷장이 이미 있으면, 아이템을 업데이트하고 save를 호출하지 않는다.")
        @Test
        void updatesItems_whenWardrobeExists() {
            UserWardrobe existing = UserWardrobe.create(USER, Set.of(TOP));
            when(userWardrobeRepository.findByUser(USER)).thenReturn(Optional.of(existing));
            Set<ClothingItem> newItems = Set.of(OUTER, PANTS);

            userWardrobeService.setupWardrobe(USER, newItems);

            verify(userWardrobeRepository, never()).save(any());
            assertThat(existing.getItems()).containsExactlyInAnyOrderElementsOf(newItems);
        }

    }
}
