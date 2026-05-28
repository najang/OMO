package com.omo.infrastructure.auth.social;

import com.omo.application.auth.SocialAuthClient;
import com.omo.application.auth.SocialUserInfo;
import com.omo.domain.user.SocialProvider;
import com.omo.support.error.CoreException;
import com.omo.support.error.ErrorType;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

import java.util.Map;

@Component
public class KakaoAuthClient implements SocialAuthClient {

    private final RestClient restClient;

    public KakaoAuthClient(RestClient.Builder builder) {
        this.restClient = builder.build();
    }

    @Override
    public SocialProvider provider() {
        return SocialProvider.KAKAO;
    }

    @Override
    @SuppressWarnings("unchecked")
    public SocialUserInfo fetchUserInfo(String accessToken) {
        try {
            Map<String, Object> body = restClient.get()
                .uri("https://kapi.kakao.com/v2/user/me")
                .header("Authorization", "Bearer " + accessToken)
                .retrieve()
                .body(Map.class);

            if (body == null) {
                throw new CoreException(ErrorType.UNAUTHENTICATED, "유효하지 않은 Kakao 토큰입니다.");
            }

            String providerId = String.valueOf(body.get("id"));
            Map<String, Object> kakaoAccount = (Map<String, Object>) body.get("kakao_account");
            Map<String, Object> profile = kakaoAccount != null
                ? (Map<String, Object>) kakaoAccount.get("profile")
                : null;

            String email = (kakaoAccount != null && kakaoAccount.containsKey("email"))
                ? (String) kakaoAccount.get("email")
                : providerId + "@kakao.com";
            String nickname = (profile != null && profile.containsKey("nickname"))
                ? (String) profile.get("nickname")
                : "카카오유저";

            return new SocialUserInfo(email, nickname, providerId);
        } catch (CoreException e) {
            throw e;
        } catch (Exception e) {
            throw new CoreException(ErrorType.UNAUTHENTICATED, "Kakao 토큰 검증에 실패했습니다.");
        }
    }
}
