package com.omo.infrastructure.auth.social;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.omo.application.auth.SocialUserInfo;
import com.omo.support.error.CoreException;
import com.omo.support.error.ErrorType;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Jwks;
import io.jsonwebtoken.security.RsaPublicJwk;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import org.springframework.test.web.client.MockRestServiceServer;
import org.springframework.web.client.RestClient;

import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.interfaces.RSAPublicKey;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.requestTo;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withSuccess;

@DisplayName("AppleAuthClient —")
class AppleAuthClientIntegrationTest {

    private static final String APPLE_JWKS_URL = "https://appleid.apple.com/auth/keys";
    private static final String TEST_CLIENT_ID = "com.omo.test";
    private static final String KID = "test-kid-001";

    private static KeyPair keyPair;
    private final ObjectMapper objectMapper = new ObjectMapper();

    private MockRestServiceServer server;
    private AppleAuthClient appleAuthClient;

    @BeforeAll
    static void generateKeyPair() throws Exception {
        KeyPairGenerator kpg = KeyPairGenerator.getInstance("RSA");
        kpg.initialize(2048);
        keyPair = kpg.generateKeyPair();
    }

    @BeforeEach
    void setUp() {
        RestClient.Builder builder = RestClient.builder();
        server = MockRestServiceServer.bindTo(builder).build();
        appleAuthClient = new AppleAuthClient(builder, TEST_CLIENT_ID);
    }

    @AfterEach
    void verify() {
        server.verify();
    }

    private String buildIdentityToken(String sub, String email, Date expiration) {
        var builder = Jwts.builder()
            .header().keyId(KID).and()
            .subject(sub)
            .issuer("https://appleid.apple.com")
            .audience().add(TEST_CLIENT_ID).and()
            .issuedAt(new Date())
            .expiration(expiration)
            .signWith(keyPair.getPrivate(), Jwts.SIG.RS256);

        if (email != null) {
            builder.claim("email", email);
        }
        return builder.compact();
    }

    private String buildJwksJson() throws Exception {
        RsaPublicJwk publicJwk = Jwks.builder()
            .key((RSAPublicKey) keyPair.getPublic())
            .id(KID)
            .build();
        return objectMapper.writeValueAsString(
            Map.of("keys", List.of(new LinkedHashMap<>(publicJwk)))
        );
    }

    @DisplayName("Apple identityToken으로 유저 정보를 조회할 때,")
    @Nested
    class FetchUserInfo {

        @DisplayName("유효한 identityToken이면, SocialUserInfo를 반환한다.")
        @Test
        void returnsSocialUserInfo_whenAppleTokenIsValid() throws Exception {
            // arrange
            String identityToken = buildIdentityToken(
                "apple-sub-001", "apple@privaterelay.appleid.com",
                new Date(System.currentTimeMillis() + 3_600_000)
            );
            server.expect(requestTo(APPLE_JWKS_URL))
                .andRespond(withSuccess(buildJwksJson(), MediaType.APPLICATION_JSON));

            // act
            SocialUserInfo result = appleAuthClient.fetchUserInfo(identityToken);

            // assert
            assertAll(
                () -> assertThat(result.providerId()).isEqualTo("apple-sub-001"),
                () -> assertThat(result.email()).isEqualTo("apple@privaterelay.appleid.com")
            );
        }

        @DisplayName("email 클레임이 없으면, sub 기반 임시 이메일을 사용한다.")
        @Test
        void usesSubEmail_whenEmailClaimIsMissing() throws Exception {
            // arrange
            String identityToken = buildIdentityToken(
                "apple-sub-002", null,
                new Date(System.currentTimeMillis() + 3_600_000)
            );
            server.expect(requestTo(APPLE_JWKS_URL))
                .andRespond(withSuccess(buildJwksJson(), MediaType.APPLICATION_JSON));

            // act
            SocialUserInfo result = appleAuthClient.fetchUserInfo(identityToken);

            // assert
            assertThat(result.email()).isEqualTo("apple-sub-002@privaterelay.appleid.com");
        }

        @DisplayName("만료된 identityToken이면, UNAUTHENTICATED 예외가 발생한다.")
        @Test
        void throwsUnauthenticated_whenTokenExpired() throws Exception {
            // arrange
            String expiredToken = buildIdentityToken(
                "apple-sub-001", "apple@privaterelay.appleid.com",
                new Date(System.currentTimeMillis() - 1)
            );
            server.expect(requestTo(APPLE_JWKS_URL))
                .andRespond(withSuccess(buildJwksJson(), MediaType.APPLICATION_JSON));

            // act
            CoreException result = assertThrows(CoreException.class, () ->
                appleAuthClient.fetchUserInfo(expiredToken)
            );

            // assert
            assertThat(result.getErrorType()).isEqualTo(ErrorType.UNAUTHENTICATED);
        }

        @DisplayName("JWKS에 일치하는 kid가 없으면, UNAUTHENTICATED 예외가 발생한다.")
        @Test
        void throwsUnauthenticated_whenKidNotFoundInJwks() throws Exception {
            // arrange
            String identityToken = buildIdentityToken(
                "apple-sub-001", "apple@omo.com",
                new Date(System.currentTimeMillis() + 3_600_000)
            );
            server.expect(requestTo(APPLE_JWKS_URL))
                .andRespond(withSuccess("{\"keys\":[]}", MediaType.APPLICATION_JSON));

            // act
            CoreException result = assertThrows(CoreException.class, () ->
                appleAuthClient.fetchUserInfo(identityToken)
            );

            // assert
            assertThat(result.getErrorType()).isEqualTo(ErrorType.UNAUTHENTICATED);
        }

        @DisplayName("aud 클레임이 clientId와 다르면, UNAUTHENTICATED 예외가 발생한다.")
        @Test
        void throwsUnauthenticated_whenAudienceMismatch() throws Exception {
            // arrange
            String identityToken = Jwts.builder()
                .header().keyId(KID).and()
                .subject("apple-sub-001")
                .issuer("https://appleid.apple.com")
                .audience().add("com.wrong.app").and()
                .issuedAt(new Date())
                .expiration(new Date(System.currentTimeMillis() + 3_600_000))
                .signWith(keyPair.getPrivate(), Jwts.SIG.RS256)
                .compact();
            server.expect(requestTo(APPLE_JWKS_URL))
                .andRespond(withSuccess(buildJwksJson(), MediaType.APPLICATION_JSON));

            // act
            CoreException result = assertThrows(CoreException.class, () ->
                appleAuthClient.fetchUserInfo(identityToken)
            );

            // assert
            assertThat(result.getErrorType()).isEqualTo(ErrorType.UNAUTHENTICATED);
        }

        @DisplayName("iss 클레임이 Apple 도메인이 아니면, UNAUTHENTICATED 예외가 발생한다.")
        @Test
        void throwsUnauthenticated_whenIssuerInvalid() throws Exception {
            // arrange
            String identityToken = Jwts.builder()
                .header().keyId(KID).and()
                .subject("apple-sub-001")
                .issuer("https://evil.com")
                .audience().add(TEST_CLIENT_ID).and()
                .issuedAt(new Date())
                .expiration(new Date(System.currentTimeMillis() + 3_600_000))
                .signWith(keyPair.getPrivate(), Jwts.SIG.RS256)
                .compact();
            server.expect(requestTo(APPLE_JWKS_URL))
                .andRespond(withSuccess(buildJwksJson(), MediaType.APPLICATION_JSON));

            // act
            CoreException result = assertThrows(CoreException.class, () ->
                appleAuthClient.fetchUserInfo(identityToken)
            );

            // assert
            assertThat(result.getErrorType()).isEqualTo(ErrorType.UNAUTHENTICATED);
        }

        @DisplayName("다른 RSA 키로 서명된 토큰이면, UNAUTHENTICATED 예외가 발생한다.")
        @Test
        void throwsUnauthenticated_whenTokenSignedWithDifferentKey() throws Exception {
            // arrange
            KeyPairGenerator kpg = KeyPairGenerator.getInstance("RSA");
            kpg.initialize(2048);
            KeyPair otherKeyPair = kpg.generateKeyPair();

            String identityToken = buildIdentityToken(
                "apple-sub-001", "apple@omo.com",
                new Date(System.currentTimeMillis() + 3_600_000)
            );

            // 다른 공개키를 JWKS로 반환 (서명 불일치)
            RsaPublicJwk otherJwk = Jwks.builder()
                .key((RSAPublicKey) otherKeyPair.getPublic())
                .id(KID)
                .build();
            String otherJwksJson = objectMapper.writeValueAsString(
                Map.of("keys", List.of(new LinkedHashMap<>(otherJwk)))
            );
            server.expect(requestTo(APPLE_JWKS_URL))
                .andRespond(withSuccess(otherJwksJson, MediaType.APPLICATION_JSON));

            // act
            CoreException result = assertThrows(CoreException.class, () ->
                appleAuthClient.fetchUserInfo(identityToken)
            );

            // assert
            assertThat(result.getErrorType()).isEqualTo(ErrorType.UNAUTHENTICATED);
        }
    }
}
