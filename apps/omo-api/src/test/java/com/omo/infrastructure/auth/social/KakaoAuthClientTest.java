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
import static org.springframework.test.web.client.match.MockRestRequestMatchers.header;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.method;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.requestTo;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withServerError;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withSuccess;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withUnauthorizedRequest;

@DisplayName("KakaoAuthClient —")
class KakaoAuthClientTest {

    private static final String USER_ME_URL = "https://kapi.kakao.com/v2/user/me";

    private MockRestServiceServer server;
    private KakaoAuthClient kakaoAuthClient;

    @BeforeEach
    void setUp() {
        RestClient.Builder builder = RestClient.builder();
        server = MockRestServiceServer.bindTo(builder).build();
        kakaoAuthClient = new KakaoAuthClient(builder);
    }

    @AfterEach
    void verify() {
        server.verify();
    }

    @DisplayName("Kakao accessToken으로 유저 정보를 조회할 때,")
    @Nested
    class FetchUserInfo {

        @DisplayName("Kakao가 유효한 응답을 반환하면, SocialUserInfo로 파싱하여 반환한다.")
        @Test
        void returnsSocialUserInfo_whenKakaoRespondsSuccessfully() {
            // arrange
            server.expect(requestTo(USER_ME_URL))
                .andExpect(method(HttpMethod.GET))
                .andExpect(header("Authorization", "Bearer kakao-access-token"))
                .andRespond(withSuccess("""
                    {
                        "id": 123456789,
                        "kakao_account": {
                            "email": "kakao@omo.com",
                            "profile": {
                                "nickname": "카카오유저"
                            }
                        }
                    }
                    """, MediaType.APPLICATION_JSON));

            // act
            SocialUserInfo result = kakaoAuthClient.fetchUserInfo("kakao-access-token");

            // assert
            assertAll(
                () -> assertThat(result.providerId()).isEqualTo("123456789"),
                () -> assertThat(result.email()).isEqualTo("kakao@omo.com"),
                () -> assertThat(result.nickname()).isEqualTo("카카오유저")
            );
        }

        @DisplayName("이메일 미동의 유저면, providerId 기반 임시 이메일을 사용하고 닉네임은 그대로 가져온다.")
        @Test
        void usesProviderIdEmail_whenEmailNotConsented() {
            // arrange
            server.expect(requestTo(USER_ME_URL))
                .andRespond(withSuccess("""
                    {
                        "id": 987654321,
                        "kakao_account": {
                            "profile": {
                                "nickname": "이메일없는유저"
                            }
                        }
                    }
                    """, MediaType.APPLICATION_JSON));

            // act
            SocialUserInfo result = kakaoAuthClient.fetchUserInfo("token");

            // assert
            assertAll(
                () -> assertThat(result.email()).isEqualTo("987654321@kakao.com"),
                () -> assertThat(result.nickname()).isEqualTo("이메일없는유저"),
                () -> assertThat(result.providerId()).isEqualTo("987654321")
            );
        }

        @DisplayName("kakao_account가 없으면, providerId 기반 임시 이메일과 기본 닉네임을 사용한다.")
        @Test
        void usesDefaults_whenKakaoAccountIsNull() {
            // arrange
            server.expect(requestTo(USER_ME_URL))
                .andRespond(withSuccess("""
                    {
                        "id": 111222333
                    }
                    """, MediaType.APPLICATION_JSON));

            // act
            SocialUserInfo result = kakaoAuthClient.fetchUserInfo("token");

            // assert
            assertAll(
                () -> assertThat(result.email()).isEqualTo("111222333@kakao.com"),
                () -> assertThat(result.nickname()).isEqualTo("카카오유저")
            );
        }

        @DisplayName("Kakao 서버가 401을 반환하면, UNAUTHENTICATED 예외가 발생한다.")
        @Test
        void throwsUnauthenticated_whenKakaoReturns401() {
            // arrange
            server.expect(requestTo(USER_ME_URL))
                .andRespond(withUnauthorizedRequest());

            // act
            CoreException result = assertThrows(CoreException.class, () ->
                kakaoAuthClient.fetchUserInfo("invalid-token")
            );

            // assert
            assertThat(result.getErrorType()).isEqualTo(ErrorType.UNAUTHENTICATED);
        }

        @DisplayName("Kakao 서버가 5xx를 반환하면, UNAUTHENTICATED 예외가 발생한다.")
        @Test
        void throwsUnauthenticated_whenKakaoServerError() {
            // arrange
            server.expect(requestTo(USER_ME_URL))
                .andRespond(withServerError());

            // act
            CoreException result = assertThrows(CoreException.class, () ->
                kakaoAuthClient.fetchUserInfo("bad-token")
            );

            // assert
            assertThat(result.getErrorType()).isEqualTo(ErrorType.UNAUTHENTICATED);
        }
    }
}
