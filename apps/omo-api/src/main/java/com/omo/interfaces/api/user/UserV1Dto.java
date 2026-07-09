package com.omo.interfaces.api.user;

import com.omo.application.user.UserInfo;
import io.swagger.v3.oas.annotations.media.Schema;

public class UserV1Dto {

    public record OnboardingRequest(
        @Schema(description = "닉네임 (한글/영어 2~6자)", example = "햇살곰") String nickname
    ) {}

    public record MeResponse(
        @Schema(description = "이메일", example = "user@example.com") String email,
        @Schema(description = "닉네임", example = "햇살곰") String nickname
    ) {
        public static MeResponse from(UserInfo info) {
            return new MeResponse(info.email(), info.nickname());
        }
    }
}
