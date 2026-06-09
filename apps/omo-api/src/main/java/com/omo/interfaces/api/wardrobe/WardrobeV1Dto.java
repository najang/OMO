package com.omo.interfaces.api.wardrobe;

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
}