package com.omo.interfaces.api.user;

import io.swagger.v3.oas.annotations.media.Schema;

public class UserV1Dto {

    public record OnboardingRequest(
        @Schema(description = "닉네임 (한글/영어 2~6자)", example = "햇살곰") String nickname
    ) {}
}
