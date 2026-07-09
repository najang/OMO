package com.omo.interfaces.api.user;

import com.omo.interfaces.api.ApiResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;

@Tag(name = "User V1 API", description = "유저 API")
public interface UserV1ApiSpec {

    @Operation(
        summary = "온보딩 닉네임 설정",
        description = "신규 유저의 닉네임을 설정하고 온보딩을 완료합니다. 닉네임은 한글 또는 영어 2~6자만 허용합니다."
    )
    ApiResponse<Object> completeOnboarding(Long userId, UserV1Dto.OnboardingRequest request);

    @Operation(
        summary = "내 정보 조회",
        description = "로그인한 유저의 이메일과 닉네임을 조회합니다."
    )
    ApiResponse<UserV1Dto.MeResponse> getMyInfo(Long userId);
}
