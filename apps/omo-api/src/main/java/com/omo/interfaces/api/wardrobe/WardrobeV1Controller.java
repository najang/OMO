package com.omo.interfaces.api.wardrobe;

import com.omo.application.wardrobe.WardrobeFacade;
import com.omo.interfaces.api.ApiResponse;
import com.omo.interfaces.api.auth.AuthUser;
import lombok.RequiredArgsConstructor;
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
}
