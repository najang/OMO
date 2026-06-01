package com.omo.interfaces.api.auth;

import com.omo.application.auth.AuthInfo;
import com.omo.domain.user.SocialProvider;
import io.swagger.v3.oas.annotations.media.Schema;

public class AuthV1Dto {

    public record LoginRequest(
        @Schema(description = "소셜 프로바이더", example = "GOOGLE") SocialProvider provider,
        @Schema(description = "소셜 토큰 (Google: idToken, Kakao: accessToken, Apple: identityToken)") String token
    ) {}

    public record LoginResponse(
        @Schema(description = "액세스 토큰") String accessToken,
        @Schema(description = "리프레시 토큰") String refreshToken,
        @Schema(description = "유저 ID") Long userId,
        @Schema(description = "신규 유저 여부 — true면 닉네임 설정 화면으로 이동") boolean isNewUser
    ) {
        public static LoginResponse from(AuthInfo info) {
            return new LoginResponse(info.accessToken(), info.refreshToken(), info.userId(), info.isNewUser());
        }
    }
}
