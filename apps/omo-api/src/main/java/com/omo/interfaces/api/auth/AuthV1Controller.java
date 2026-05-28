package com.omo.interfaces.api.auth;

import com.omo.application.auth.AuthFacade;
import com.omo.application.auth.AuthInfo;
import com.omo.interfaces.api.ApiResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/v1/auth")
public class AuthV1Controller implements AuthV1ApiSpec {

    private final AuthFacade authFacade;

    @PostMapping("/login")
    @Override
    public ApiResponse<AuthV1Dto.LoginResponse> login(@RequestBody AuthV1Dto.LoginRequest request) {
        AuthInfo info = authFacade.login(request.provider(), request.token());
        return ApiResponse.success(AuthV1Dto.LoginResponse.from(info));
    }
}
