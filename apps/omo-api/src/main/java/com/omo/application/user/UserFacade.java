package com.omo.application.user;

import com.omo.domain.user.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@RequiredArgsConstructor
@Component
public class UserFacade {

    private final UserService userService;

    public void completeOnboarding(Long userId, String nickname) {
        userService.completeOnboarding(userId, nickname);
    }
}
