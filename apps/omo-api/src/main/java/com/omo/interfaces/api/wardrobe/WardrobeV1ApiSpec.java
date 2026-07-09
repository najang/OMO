package com.omo.interfaces.api.wardrobe;

import com.omo.interfaces.api.ApiResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;

@Tag(name = "Wardrobe V1 API", description = "옷장 API")
public interface WardrobeV1ApiSpec {

    @Operation(
        summary = "온보딩 보유 의류 설정",
        description = "온보딩 시 보유 의류 카테고리를 설정합니다. 이미 설정된 경우 카테고리를 덮어씁니다."
    )
    ApiResponse<Object> setupWardrobe(Long userId, WardrobeV1Dto.WardrobeSetupRequest request);

    @Operation(
        summary = "온보딩 체감 온도 설정",
        description = "온보딩 시 초기 체감 민감도를 설정합니다. 이미 설정된 경우 무시합니다."
    )
    ApiResponse<Object> initTempProfile(Long userId, WardrobeV1Dto.TempProfileSetupRequest request);

    @Operation(
        summary = "내 옷장 조회",
        description = "로그인한 사용자의 보유 의류 목록을 반환합니다. 옷장이 설정되지 않은 경우 404를 반환합니다."
    )
    ApiResponse<WardrobeV1Dto.WardrobeResponse> getWardrobe(Long userId);

    @Operation(
        summary = "내 옷장 수정",
        description = "로그인한 사용자의 보유 의류 목록을 교체합니다. 최소 1개 이상의 유효한 아이템 키가 필요합니다."
    )
    ApiResponse<Object> updateWardrobe(Long userId, WardrobeV1Dto.WardrobeSetupRequest request);
}
