package com.omo.interfaces.api.auth;

import com.omo.interfaces.api.ApiResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;

@Tag(name = "Auth V1 API", description = "인증 API")
public interface AuthV1ApiSpec {

    @Operation(
        summary = "소셜 로그인",
        description = "Google/Kakao/Apple 소셜 토큰으로 로그인합니다. 신규 유저는 자동 가입됩니다."
    )
    ApiResponse<AuthV1Dto.LoginResponse> login(AuthV1Dto.LoginRequest request);
}
