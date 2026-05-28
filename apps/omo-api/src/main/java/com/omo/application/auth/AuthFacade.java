package com.omo.application.auth;

import com.omo.domain.user.SocialProvider;
import com.omo.domain.user.User;
import com.omo.domain.user.UserService;
import com.omo.infrastructure.auth.jwt.JwtProvider;
import com.omo.support.error.CoreException;
import com.omo.support.error.ErrorType;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;

@RequiredArgsConstructor
@Component
public class AuthFacade {

    private final List<SocialAuthClient> socialAuthClients;
    private final UserService userService;
    private final JwtProvider jwtProvider;

    public AuthInfo login(SocialProvider provider, String token) {
        SocialAuthClient client = socialAuthClients.stream()
            .filter(c -> c.provider() == provider)
            .findFirst()
            .orElseThrow(() -> new CoreException(ErrorType.INVALID_INPUT, "지원하지 않는 소셜 프로바이더입니다: " + provider));

        SocialUserInfo userInfo = client.fetchUserInfo(token);
        User user = userService.getOrCreateUser(
            userInfo.email(), userInfo.nickname(), provider, userInfo.providerId()
        );

        return new AuthInfo(
            jwtProvider.createAccessToken(user.getId()),
            jwtProvider.createRefreshToken(user.getId()),
            user.getId()
        );
    }
}
