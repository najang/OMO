package com.omo.application.wardrobe;

import com.omo.domain.user.SocialProvider;
import com.omo.domain.user.User;
import com.omo.domain.user.UserService;
import com.omo.domain.wardrobe.ClothingCategory;
import com.omo.domain.wardrobe.ClothingDisplayGroup;
import com.omo.domain.wardrobe.ClothingItem;
import com.omo.domain.wardrobe.ClothingItemRepository;
import com.omo.domain.wardrobe.UserWardrobe;
import com.omo.domain.wardrobe.UserWardrobeRepository;
import com.omo.domain.wardrobe.UserWardrobeService;
import com.omo.support.error.CoreException;
import com.omo.support.error.ErrorType;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anySet;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class WardrobeFacadeTest {

    @Mock private UserService userService;
    @Mock private UserWardrobeService userWardrobeService;
    @Mock private UserWardrobeRepository userWardrobeRepository;
    @Mock private ClothingItemRepository clothingItemRepository;

    @InjectMocks private WardrobeFacade wardrobeFacade;

    private static final User USER = new User("test@omo.com", "테스터", SocialProvider.GOOGLE, "uid-1");
    private static final ClothingItem SHORT_TEE = ClothingItem.of("short-tee", ClothingCategory.TOP, ClothingDisplayGroup.TOP, "반팔 티셔츠");
    private static final ClothingItem PADDING = ClothingItem.of("padding", ClothingCategory.OUTER, ClothingDisplayGroup.OUTER, "패딩");

    @DisplayName("옷장을 설정할 때,")
    @Nested
    class SetupWardrobe {

        @DisplayName("itemKeys가 null이면, INVALID_INPUT 예외가 발생하고 유저 조회를 하지 않는다.")
        @Test
        void throwsInvalidInput_whenItemKeysIsNull() {
            assertThatThrownBy(() -> wardrobeFacade.setupWardrobe(1L, null))
                .isInstanceOf(CoreException.class)
                .satisfies(e -> assertThat(((CoreException) e).getErrorType())
                    .isEqualTo(ErrorType.INVALID_INPUT));
            verify(userService, never()).getUser(any());
        }

        @DisplayName("itemKeys가 빈 목록이면, INVALID_INPUT 예외가 발생하고 유저 조회를 하지 않는다.")
        @Test
        void throwsInvalidInput_whenItemKeysIsEmpty() {
            assertThatThrownBy(() -> wardrobeFacade.setupWardrobe(1L, List.of()))
                .isInstanceOf(CoreException.class)
                .satisfies(e -> assertThat(((CoreException) e).getErrorType())
                    .isEqualTo(ErrorType.INVALID_INPUT));
            verify(userService, never()).getUser(any());
        }

        @DisplayName("일부 키가 DB에 없으면, INVALID_INPUT 예외가 발생한다.")
        @Test
        void throwsInvalidInput_whenSomeKeysDoNotExist() {
            when(clothingItemRepository.findAllBySystemKeyIn(anySet()))
                .thenReturn(List.of(SHORT_TEE)); // 2개 요청에 1개만 반환

            assertThatThrownBy(() -> wardrobeFacade.setupWardrobe(1L, List.of("short-tee", "invalid-key")))
                .isInstanceOf(CoreException.class)
                .satisfies(e -> assertThat(((CoreException) e).getErrorType())
                    .isEqualTo(ErrorType.INVALID_INPUT));
        }

        @DisplayName("유효한 키 목록이면, 옷장 설정을 위임한다.")
        @Test
        void delegatesToUserWardrobeService_whenItemKeysAreValid() {
            when(clothingItemRepository.findAllBySystemKeyIn(anySet()))
                .thenReturn(List.of(SHORT_TEE, PADDING));
            when(userService.getUser(1L)).thenReturn(USER);

            wardrobeFacade.setupWardrobe(1L, List.of("short-tee", "padding"));

            verify(userWardrobeService).setupWardrobe(any(User.class), anySet());
        }

        @DisplayName("중복 키는 하나로 처리되어 유효성 검사를 통과한다.")
        @Test
        void deduplicatesKeys_beforeValidating() {
            when(clothingItemRepository.findAllBySystemKeyIn(anySet()))
                .thenReturn(List.of(SHORT_TEE));
            when(userService.getUser(1L)).thenReturn(USER);

            wardrobeFacade.setupWardrobe(1L, List.of("short-tee", "short-tee"));

            verify(userWardrobeService).setupWardrobe(any(User.class), anySet());
        }
    }

    @DisplayName("옷장을 조회할 때,")
    @Nested
    class GetWardrobe {

        @DisplayName("옷장이 없으면, WARDROBE_NOT_FOUND 예외가 발생한다.")
        @Test
        void throwsWardrobeNotFound_whenWardrobeDoesNotExist() {
            when(userService.getUser(1L)).thenReturn(USER);
            when(userWardrobeRepository.findByUser(USER)).thenReturn(Optional.empty());

            assertThatThrownBy(() -> wardrobeFacade.getWardrobe(1L))
                .isInstanceOf(CoreException.class)
                .satisfies(e -> assertThat(((CoreException) e).getErrorType())
                    .isEqualTo(ErrorType.WARDROBE_NOT_FOUND));
        }

        @DisplayName("옷장이 있으면, 아이템 목록을 담은 WardrobeInfo를 반환한다.")
        @Test
        void returnsWardrobeInfo_whenWardrobeExists() {
            UserWardrobe wardrobe = UserWardrobe.create(USER, Set.of(SHORT_TEE, PADDING));
            when(userService.getUser(1L)).thenReturn(USER);
            when(userWardrobeRepository.findByUser(USER)).thenReturn(Optional.of(wardrobe));

            WardrobeInfo result = wardrobeFacade.getWardrobe(1L);

            assertThat(result.items()).hasSize(2);
            assertThat(result.items())
                .extracting(WardrobeInfo.ItemInfo::systemKey)
                .containsExactlyInAnyOrder("short-tee", "padding");
        }
    }

    @DisplayName("옷장을 수정할 때,")
    @Nested
    class UpdateWardrobe {

        @DisplayName("updateWardrobe는 setupWardrobe와 동일하게 동작한다.")
        @Test
        void delegatesToSetupWardrobe_whenItemKeysAreValid() {
            when(clothingItemRepository.findAllBySystemKeyIn(anySet()))
                .thenReturn(List.of(SHORT_TEE));
            when(userService.getUser(1L)).thenReturn(USER);

            wardrobeFacade.updateWardrobe(1L, List.of("short-tee"));

            verify(userWardrobeService).setupWardrobe(any(User.class), anySet());
        }

        @DisplayName("itemKeys가 null이면, INVALID_INPUT 예외가 발생한다.")
        @Test
        void throwsInvalidInput_whenItemKeysIsNull() {
            assertThatThrownBy(() -> wardrobeFacade.updateWardrobe(1L, null))
                .isInstanceOf(CoreException.class)
                .satisfies(e -> assertThat(((CoreException) e).getErrorType())
                    .isEqualTo(ErrorType.INVALID_INPUT));
        }
    }

    @DisplayName("의류 카탈로그를 조회할 때,")
    @Nested
    class GetClothingItemCatalog {

        @DisplayName("전체 아이템을 ClothingItemCatalogInfo로 매핑하여 반환한다.")
        @Test
        void returnsCatalogInfo_mappedFromAllItems() {
            when(clothingItemRepository.findAll()).thenReturn(List.of(SHORT_TEE, PADDING));

            ClothingItemCatalogInfo result = wardrobeFacade.getClothingItemCatalog();

            assertThat(result.items()).hasSize(2);
            assertThat(result.items())
                .extracting(ClothingItemCatalogInfo.ItemInfo::systemKey)
                .containsExactlyInAnyOrder("short-tee", "padding");
            assertThat(result.items())
                .extracting(ClothingItemCatalogInfo.ItemInfo::displayGroup)
                .containsExactlyInAnyOrder("TOP", "OUTER");
        }

        @DisplayName("아이템이 없으면, 빈 목록을 반환한다.")
        @Test
        void returnsEmptyList_whenNoItems() {
            when(clothingItemRepository.findAll()).thenReturn(List.of());

            ClothingItemCatalogInfo result = wardrobeFacade.getClothingItemCatalog();

            assertThat(result.items()).isEmpty();
        }
    }
}
