package com.omo.interfaces.api.wardrobe;

import com.omo.application.wardrobe.WardrobeFacade;
import com.omo.interfaces.api.ApiResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/v1/clothing-items")
public class ClothingItemV1Controller implements ClothingItemV1ApiSpec {

    private final WardrobeFacade wardrobeFacade;

    @GetMapping
    @Override
    public ApiResponse<ClothingItemV1Dto.CatalogResponse> getClothingItemCatalog() {
        return ApiResponse.success(
            ClothingItemV1Dto.CatalogResponse.from(wardrobeFacade.getClothingItemCatalog())
        );
    }
}
