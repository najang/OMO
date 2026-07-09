package com.omo.interfaces.api.wardrobe;

import com.omo.application.wardrobe.ClothingItemCatalogInfo;
import io.swagger.v3.oas.annotations.media.Schema;
import java.util.List;

public class ClothingItemV1Dto {

    public record CatalogResponse(List<ItemResponse> items) {
        public record ItemResponse(
            @Schema(description = "아이템 시스템 키", example = "short-tee") String systemKey,
            @Schema(description = "카테고리 (추천 로직용)", example = "TOP") String category,
            @Schema(description = "화면 표시 그룹", example = "TOP") String displayGroup,
            @Schema(description = "한글 이름", example = "반팔 티셔츠") String nameKo
        ) {}

        public static CatalogResponse from(ClothingItemCatalogInfo info) {
            return new CatalogResponse(
                info.items().stream()
                    .map(item -> new ItemResponse(
                        item.systemKey(),
                        item.category(),
                        item.displayGroup(),
                        item.nameKo()))
                    .toList()
            );
        }
    }
}
