package com.omo.interfaces.api.wardrobe;

import com.omo.application.wardrobe.WardrobeFacade;
import com.omo.interfaces.api.ApiResponse;
import com.omo.interfaces.api.auth.AuthUser;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/v1/users")
public class WardrobeV1Controller implements WardrobeV1ApiSpec {

    private final WardrobeFacade wardrobeFacade;

    @PostMapping("/me/wardrobe")
    @Override
    public ApiResponse<Object> setupWardrobe(@AuthUser Long userId, @RequestBody WardrobeV1Dto.WardrobeSetupRequest request) {
        wardrobeFacade.setupWardrobe(userId, request.itemKeys());
        return ApiResponse.success();
    }

    @PostMapping("/me/temp-profile")
    @Override
    public ApiResponse<Object> initTempProfile(@AuthUser Long userId, @RequestBody WardrobeV1Dto.TempProfileSetupRequest request) {
        wardrobeFacade.initTempProfile(userId, request.tempSensitivity());
        return ApiResponse.success();
    }

    @GetMapping("/me/wardrobe")
    @Override
    public ApiResponse<WardrobeV1Dto.WardrobeResponse> getWardrobe(@AuthUser Long userId) {
        return ApiResponse.success(WardrobeV1Dto.WardrobeResponse.from(wardrobeFacade.getWardrobe(userId)));
    }

    @PatchMapping("/me/wardrobe")
    @Override
    public ApiResponse<Object> updateWardrobe(@AuthUser Long userId, @RequestBody WardrobeV1Dto.WardrobeSetupRequest request) {
        wardrobeFacade.updateWardrobe(userId, request.itemKeys());
        return ApiResponse.success();
    }
}
