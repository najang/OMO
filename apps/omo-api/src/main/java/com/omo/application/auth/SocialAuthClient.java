package com.omo.application.auth;

import com.omo.domain.user.SocialProvider;

public interface SocialAuthClient {
    SocialProvider provider();
    SocialUserInfo fetchUserInfo(String token);
}
