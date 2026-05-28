package com.omo.infrastructure.auth.social;

import com.omo.application.auth.SocialUserInfo;
import com.omo.support.error.CoreException;
import com.omo.support.error.ErrorType;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.test.web.client.MockRestServiceServer;
import org.springframework.web.client.RestClient;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.method;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.requestTo;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withServerError;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withSuccess;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withUnauthorizedRequest;

@DisplayName("GoogleAuthClient —")
class GoogleAuthClientIntegrationTest {

    private static final String TOKENINFO_URL = "https://oauth2.googleapis.com/tokeninfo";

    private MockRestServiceServer server;
    private GoogleAuthClient googleAuthClient;

    @BeforeEach
    void setUp() {
        RestClient.Builder builder = RestClient.builder();
        server = MockRestServiceServer.bindTo(builder).build();
        googleAuthClient = new GoogleAuthClient(builder);
    }

    @AfterEach
    void verify() {
        server.verify();
    }

    @DisplayName("Google idToken으로 유저 정보를 조회할 때,")
    @Nested
    class FetchUserInfo {

        @DisplayName("Google이 유효한 응답을 반환하면, SocialUserInfo로 파싱하여 반환한다.")
        @Test
        void returnsSocialUserInfo_whenGoogleRespondsSuccessfully() {
            // arrange
            server.expect(requestTo(TOKENINFO_URL + "?id_token=valid-token"))
                .andExpect(method(HttpMethod.GET))
                .andRespond(withSuccess("""
                    {
                        "sub": "google-uid-001",
                        "email": "test@gmail.com",
                        "name": "테스트유저"
                    }
                    """, MediaType.APPLICATION_JSON));

            // act
            SocialUserInfo result = googleAuthClient.fetchUserInfo("valid-token");

            // assert
            assertAll(
                () -> assertThat(result.providerId()).isEqualTo("google-uid-001"),
                () -> assertThat(result.email()).isEqualTo("test@gmail.com"),
                () -> assertThat(result.nickname()).isEqualTo("테스트유저")
            );
        }

        @DisplayName("name 필드가 없으면, email을 nickname으로 사용한다.")
        @Test
        void usesEmailAsNickname_whenNameIsMissing() {
            // arrange
            server.expect(requestTo(TOKENINFO_URL + "?id_token=no-name-token"))
                .andExpect(method(HttpMethod.GET))
                .andRespond(withSuccess("""
                    {
                        "sub": "google-uid-002",
                        "email": "noname@gmail.com"
                    }
                    """, MediaType.APPLICATION_JSON));

            // act
            SocialUserInfo result = googleAuthClient.fetchUserInfo("no-name-token");

            // assert
            assertThat(result.nickname()).isEqualTo("noname@gmail.com");
        }

        @DisplayName("Google이 error 필드를 포함한 응답을 반환하면, UNAUTHENTICATED 예외가 발생한다.")
        @Test
        void throwsUnauthenticated_whenGoogleReturnsErrorBody() {
            // arrange
            server.expect(requestTo(TOKENINFO_URL + "?id_token=invalid-token"))
                .andExpect(method(HttpMethod.GET))
                .andRespond(withSuccess("""
                    {
                        "error": "invalid_token",
                        "error_description": "Invalid Value"
                    }
                    """, MediaType.APPLICATION_JSON));

            // act
            CoreException result = assertThrows(CoreException.class, () ->
                googleAuthClient.fetchUserInfo("invalid-token")
            );

            // assert
            assertThat(result.getErrorType()).isEqualTo(ErrorType.UNAUTHENTICATED);
        }

        @DisplayName("Google 서버가 401을 반환하면, UNAUTHENTICATED 예외가 발생한다.")
        @Test
        void throwsUnauthenticated_whenGoogleReturns401() {
            // arrange
            server.expect(requestTo(TOKENINFO_URL + "?id_token=expired-token"))
                .andExpect(method(HttpMethod.GET))
                .andRespond(withUnauthorizedRequest());

            // act
            CoreException result = assertThrows(CoreException.class, () ->
                googleAuthClient.fetchUserInfo("expired-token")
            );

            // assert
            assertThat(result.getErrorType()).isEqualTo(ErrorType.UNAUTHENTICATED);
        }

        @DisplayName("Google 서버가 5xx를 반환하면, UNAUTHENTICATED 예외가 발생한다.")
        @Test
        void throwsUnauthenticated_whenGoogleServerError() {
            // arrange
            server.expect(requestTo(TOKENINFO_URL + "?id_token=bad-token"))
                .andExpect(method(HttpMethod.GET))
                .andRespond(withServerError());

            // act
            CoreException result = assertThrows(CoreException.class, () ->
                googleAuthClient.fetchUserInfo("bad-token")
            );

            // assert
            assertThat(result.getErrorType()).isEqualTo(ErrorType.UNAUTHENTICATED);
        }
    }
}
