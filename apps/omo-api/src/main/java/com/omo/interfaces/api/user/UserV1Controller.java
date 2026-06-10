package com.omo.interfaces.api.user;

import com.omo.application.user.UserFacade;
import com.omo.interfaces.api.ApiResponse;
import com.omo.interfaces.api.auth.AuthUser;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/v1/users")
public class UserV1Controller implements UserV1ApiSpec {

    private final UserFacade userFacade;

    @PutMapping("/me/onboarding")
    @Override
    public ApiResponse<Object> completeOnboarding(@AuthUser Long userId, @RequestBody UserV1Dto.OnboardingRequest request) {
        userFacade.completeOnboarding(userId, request.nickname());
        return ApiResponse.success();
    }

    @GetMapping("/me")
    @Override
    public ApiResponse<UserV1Dto.MeResponse> getMyInfo(@AuthUser Long userId) {
        return ApiResponse.success(UserV1Dto.MeResponse.from(userFacade.getMyInfo(userId)));
    }
}
