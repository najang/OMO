package com.omo.interfaces.api.wardrobe;

import com.omo.interfaces.api.ApiResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;

@Tag(name = "Clothing Item V1 API", description = "의류 카탈로그 API")
public interface ClothingItemV1ApiSpec {

    @Operation(
        summary = "의류 카탈로그 전체 조회",
        description = "선택 가능한 전체 의류 아이템 목록을 반환합니다. 인증이 필요 없습니다."
    )
    ApiResponse<ClothingItemV1Dto.CatalogResponse> getClothingItemCatalog();
}
