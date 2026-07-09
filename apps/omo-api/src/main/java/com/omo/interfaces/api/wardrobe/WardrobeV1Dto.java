package com.omo.interfaces.api.wardrobe;

import com.omo.application.wardrobe.WardrobeInfo;
import com.omo.domain.wardrobe.TempSensitivity;
import io.swagger.v3.oas.annotations.media.Schema;
import java.util.List;

public class WardrobeV1Dto {

    public record WardrobeSetupRequest(
        @Schema(description = "보유 의류 아이템 시스템 키 목록 (최소 1개)", example = "[\"short-tee\", \"padding\", \"jeans\"]")
        List<String> itemKeys
    ) {}

    public record TempProfileSetupRequest(
        @Schema(description = "체감 민감도 (VERY_COLD / COLD / NORMAL / HEAT / VERY_HEAT)", example = "COLD")
        TempSensitivity tempSensitivity
    ) {}

    public record WardrobeResponse(List<ItemResponse> items) {
        public record ItemResponse(
            @Schema(description = "아이템 시스템 키", example = "short-tee") String systemKey,
            @Schema(description = "카테고리", example = "TOP") String category,
            @Schema(description = "한글 이름", example = "반팔 티셔츠") String nameKo
        ) {}

        public static WardrobeResponse from(WardrobeInfo info) {
            return new WardrobeResponse(
                info.items().stream()
                    .map(item -> new ItemResponse(item.systemKey(), item.category(), item.nameKo()))
                    .toList()
            );
        }
    }
}