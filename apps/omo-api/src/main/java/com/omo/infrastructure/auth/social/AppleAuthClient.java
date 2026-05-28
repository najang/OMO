package com.omo.infrastructure.auth.social;

import com.omo.application.auth.SocialAuthClient;
import com.omo.application.auth.SocialUserInfo;
import com.omo.domain.user.SocialProvider;
import com.omo.support.error.CoreException;
import com.omo.support.error.ErrorType;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.JwkSet;
import io.jsonwebtoken.security.Jwks;
import io.jsonwebtoken.security.PublicJwk;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

import java.security.Key;
import java.util.Optional;

@Component
public class AppleAuthClient implements SocialAuthClient {

    private static final String APPLE_JWKS_URL = "https://appleid.apple.com/auth/keys";
    private static final String APPLE_ISSUER = "https://appleid.apple.com";

    private final RestClient restClient;
    private final String clientId;

    public AppleAuthClient(RestClient.Builder builder, @Value("${social.apple.client-id}") String clientId) {
        this.restClient = builder.build();
        this.clientId = clientId;
    }

    @Override
    public SocialProvider provider() {
        return SocialProvider.APPLE;
    }

    @Override
    public SocialUserInfo fetchUserInfo(String identityToken) {
        try {
            String jwksJson = restClient.get()
                .uri(APPLE_JWKS_URL)
                .retrieve()
                .body(String.class);

            JwkSet jwkSet = Jwks.setParser().build().parse(jwksJson);

            Claims claims = Jwts.parser()
                .keyLocator(header -> {
                    String kid = (String) header.get("kid");
                    return jwkSet.getKeys().stream()
                        .filter(jwk -> kid.equals(jwk.getId()))
                        .findFirst()
                        .map(jwk -> (Key) ((PublicJwk<?>) jwk).toKey())
                        .orElseThrow(() -> new CoreException(
                            ErrorType.UNAUTHENTICATED, "Apple 공개키를 찾을 수 없습니다."));
                })
                .requireIssuer(APPLE_ISSUER)
                .requireAudience(clientId)
                .build()
                .parseSignedClaims(identityToken)
                .getPayload();

            String sub = claims.getSubject();
            String email = Optional.ofNullable(claims.get("email", String.class))
                .orElse(sub + "@privaterelay.appleid.com");

            return new SocialUserInfo(email, "애플유저", sub);
        } catch (CoreException e) {
            throw e;
        } catch (Exception e) {
            throw new CoreException(ErrorType.UNAUTHENTICATED, "Apple 토큰 검증에 실패했습니다.");
        }
    }
}
