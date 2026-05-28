package com.omo.interfaces.api.auth;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.omo.application.auth.SocialUserInfo;
import com.omo.domain.user.SocialProvider;
import com.omo.infrastructure.auth.social.AppleAuthClient;
import com.omo.infrastructure.auth.social.GoogleAuthClient;
import com.omo.infrastructure.auth.social.KakaoAuthClient;
import com.omo.support.error.CoreException;
import com.omo.support.error.ErrorType;
import com.omo.utils.DatabaseCleanUp;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.notNullValue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@DisplayName("POST /api/v1/auth/login —")
class AuthV1ApiE2ETest {

    private static final String ENDPOINT = "/api/v1/auth/login";

    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private WebApplicationContext webApplicationContext;

    @Autowired
    private DatabaseCleanUp databaseCleanUp;

    @MockitoBean
    private GoogleAuthClient googleAuthClient;

    @MockitoBean
    private KakaoAuthClient kakaoAuthClient;

    @MockitoBean
    private AppleAuthClient appleAuthClient;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext).build();
        when(googleAuthClient.provider()).thenReturn(SocialProvider.GOOGLE);
        when(kakaoAuthClient.provider()).thenReturn(SocialProvider.KAKAO);
        when(appleAuthClient.provider()).thenReturn(SocialProvider.APPLE);
    }

    @AfterEach
    void tearDown() {
        databaseCleanUp.truncateAllTables();
    }

    @DisplayName("정상 로그인 케이스")
    @Nested
    class SuccessCases {

        @DisplayName("Google 토큰이 유효하면, 200과 JWT 토큰을 반환한다.")
        @Test
        void returnsJwt_whenGoogleTokenIsValid() throws Exception {
            // arrange
            when(googleAuthClient.fetchUserInfo("google-token"))
                .thenReturn(new SocialUserInfo("google@omo.com", "구글유저", "g-uid-001"));

            // act & assert
            mockMvc.perform(post(ENDPOINT)
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(toJson(new AuthV1Dto.LoginRequest(SocialProvider.GOOGLE, "google-token"))))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.accessToken", notNullValue()))
                .andExpect(jsonPath("$.data.refreshToken", notNullValue()))
                .andExpect(jsonPath("$.data.userId", notNullValue()));
        }

        @DisplayName("Kakao 토큰이 유효하면, 200과 JWT 토큰을 반환한다.")
        @Test
        void returnsJwt_whenKakaoTokenIsValid() throws Exception {
            // arrange
            when(kakaoAuthClient.fetchUserInfo("kakao-token"))
                .thenReturn(new SocialUserInfo("kakao@omo.com", "카카오유저", "k-uid-001"));

            // act & assert
            mockMvc.perform(post(ENDPOINT)
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(toJson(new AuthV1Dto.LoginRequest(SocialProvider.KAKAO, "kakao-token"))))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.accessToken", notNullValue()));
        }

        @DisplayName("Apple 토큰이 유효하면, 200과 JWT 토큰을 반환한다.")
        @Test
        void returnsJwt_whenAppleTokenIsValid() throws Exception {
            // arrange
            when(appleAuthClient.fetchUserInfo("apple-token"))
                .thenReturn(new SocialUserInfo("apple@privaterelay.appleid.com", "애플유저", "a-uid-001"));

            // act & assert
            mockMvc.perform(post(ENDPOINT)
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(toJson(new AuthV1Dto.LoginRequest(SocialProvider.APPLE, "apple-token"))))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.accessToken", notNullValue()));
        }

        @DisplayName("같은 소셜 계정으로 두 번 로그인하면, 동일한 userId를 반환한다.")
        @Test
        void returnsSameUserId_onRepeatedLogin() throws Exception {
            // arrange
            when(googleAuthClient.fetchUserInfo("google-token"))
                .thenReturn(new SocialUserInfo("google@omo.com", "구글유저", "g-uid-001"));

            String body = toJson(new AuthV1Dto.LoginRequest(SocialProvider.GOOGLE, "google-token"));

            // act
            String first = mockMvc.perform(post(ENDPOINT)
                    .contentType(MediaType.APPLICATION_JSON).content(body))
                .andReturn().getResponse().getContentAsString();
            String second = mockMvc.perform(post(ENDPOINT)
                    .contentType(MediaType.APPLICATION_JSON).content(body))
                .andReturn().getResponse().getContentAsString();

            // assert - userId 동일
            Long firstUserId = objectMapper.readTree(first).at("/data/userId").asLong();
            Long secondUserId = objectMapper.readTree(second).at("/data/userId").asLong();
            assertThat(firstUserId).isEqualTo(secondUserId);
        }
    }

    @DisplayName("에러 케이스")
    @Nested
    class ErrorCases {

        @DisplayName("소셜 클라이언트가 UNAUTHENTICATED 예외를 던지면, 401을 반환한다.")
        @Test
        void returns401_whenSocialClientThrowsUnauthenticated() throws Exception {
            // arrange
            when(googleAuthClient.fetchUserInfo(any()))
                .thenThrow(new CoreException(ErrorType.UNAUTHENTICATED, "유효하지 않은 Google 토큰입니다."));

            // act & assert
            mockMvc.perform(post(ENDPOINT)
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(toJson(new AuthV1Dto.LoginRequest(SocialProvider.GOOGLE, "bad-token"))))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.meta.errorCode").value("UNAUTHENTICATED"));
        }

        @DisplayName("지원하지 않는 provider 값을 넘기면, 400을 반환한다.")
        @Test
        void returns400_whenProviderIsUnsupported() throws Exception {
            // act & assert
            mockMvc.perform(post(ENDPOINT)
                    .contentType(MediaType.APPLICATION_JSON)
                    .content("""
                        {"provider": "NAVER", "token": "naver-token"}
                        """))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.meta.errorCode").value("BAD_REQUEST"));
        }

        @DisplayName("요청 body가 없으면, 400을 반환한다.")
        @Test
        void returns400_whenRequestBodyIsMissing() throws Exception {
            // act & assert
            mockMvc.perform(post(ENDPOINT)
                    .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.meta.errorCode").value("BAD_REQUEST"));
        }
    }

    private String toJson(Object obj) throws Exception {
        return objectMapper.writeValueAsString(obj);
    }
}
