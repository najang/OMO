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
public class GoogleAuthClient implements SocialAuthClient {

    private final RestClient restClient;

    public GoogleAuthClient(RestClient.Builder builder) {
        this.restClient = builder.build();
    }

    @Override
    public SocialProvider provider() {
        return SocialProvider.GOOGLE;
    }

    @Override
    public SocialUserInfo fetchUserInfo(String idToken) {
        try {
            Map<?, ?> body = restClient.get()
                .uri("https://oauth2.googleapis.com/tokeninfo?id_token=" + idToken)
                .retrieve()
                .body(Map.class);

            if (body == null || body.containsKey("error")) {
                throw new CoreException(ErrorType.UNAUTHENTICATED, "유효하지 않은 Google 토큰입니다.");
            }

            String email = (String) body.get("email");
            String sub = (String) body.get("sub");
            String name = body.containsKey("name") ? (String) body.get("name") : email;

            return new SocialUserInfo(email, name, sub);
        } catch (CoreException e) {
            throw e;
        } catch (Exception e) {
            throw new CoreException(ErrorType.UNAUTHENTICATED, "Google 토큰 검증에 실패했습니다.");
        }
    }
}
