package com.omo.domain.user;

import com.omo.support.error.CoreException;
import com.omo.support.error.ErrorType;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@RequiredArgsConstructor
@Component
public class UserService {

    private final UserRepository userRepository;

    @Transactional(readOnly = true)
    public User getUser(Long id) {
        return userRepository.find(id)
                .orElseThrow(() -> new CoreException(ErrorType.USER_NOT_FOUND, "[id = " + id + "] 유저를 찾을 수 없습니다."));
    }

    /**
     * provider + providerId 기준으로 유저를 조회하고, 없으면 신규 생성한다.
     * 동일 이메일이더라도 provider가 다르면 별개의 계정으로 처리한다.
     */
    @Transactional
    public User getOrCreateUser(String email, String nickname, SocialProvider provider, String providerId) {
        return userRepository.findByProviderInfo(provider, providerId)
                .orElseGet(() -> userRepository.save(new User(email, nickname, provider, providerId)));
    }

    @Transactional
    public void completeOnboarding(Long userId, String nickname) {
        User user = getUser(userId);
        user.completeOnboarding(nickname);
    }
}
