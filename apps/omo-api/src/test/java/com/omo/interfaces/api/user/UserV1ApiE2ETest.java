package com.omo.interfaces.api.user;

import tools.jackson.databind.ObjectMapper;
import com.omo.application.auth.SocialUserInfo;
import com.omo.domain.user.SocialProvider;
import com.omo.infrastructure.auth.social.AppleAuthClient;
import com.omo.infrastructure.auth.social.GoogleAuthClient;
import com.omo.infrastructure.auth.social.KakaoAuthClient;
import com.omo.interfaces.api.auth.AuthV1Dto;
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

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@DisplayName("PUT /api/v1/users/me/onboarding —")
class UserV1ApiE2ETest {

    private static final String ENDPOINT = "/api/v1/users/me/onboarding";

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

    private String loginAndGetToken() throws Exception {
        when(googleAuthClient.fetchUserInfo("google-token"))
            .thenReturn(new SocialUserInfo("google@omo.com", "구글유저", "g-uid-001"));

        String response = mockMvc.perform(post("/api/v1/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(
                    new AuthV1Dto.LoginRequest(SocialProvider.GOOGLE, "google-token"))))
            .andReturn().getResponse().getContentAsString();

        return objectMapper.readTree(response).at("/data/accessToken").asText();
    }

    @DisplayName("정상 케이스")
    @Nested
    class SuccessCases {

        @DisplayName("유효한 한글 닉네임이면, 200을 반환한다.")
        @Test
        void returns200_whenKoreanNicknameIsValid() throws Exception {
            String token = loginAndGetToken();

            mockMvc.perform(put(ENDPOINT)
                    .header("Authorization", "Bearer " + token)
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(toJson(new UserV1Dto.OnboardingRequest("햇살곰"))))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.meta.result").value("SUCCESS"));
        }

        @DisplayName("유효한 영어 닉네임이면, 200과 SUCCESS를 반환한다.")
        @Test
        void returns200_whenEnglishNicknameIsValid() throws Exception {
            String token = loginAndGetToken();

            mockMvc.perform(put(ENDPOINT)
                    .header("Authorization", "Bearer " + token)
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(toJson(new UserV1Dto.OnboardingRequest("sunny"))))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.meta.result").value("SUCCESS"));
        }

        @DisplayName("온보딩을 두 번 호출하면, 닉네임이 덮어씌워진다.")
        @Test
        void returns200_whenOnboardingCalledTwice() throws Exception {
            String token = loginAndGetToken();

            mockMvc.perform(put(ENDPOINT)
                    .header("Authorization", "Bearer " + token)
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(toJson(new UserV1Dto.OnboardingRequest("첫번째"))))
                .andExpect(status().isOk());

            mockMvc.perform(put(ENDPOINT)
                    .header("Authorization", "Bearer " + token)
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(toJson(new UserV1Dto.OnboardingRequest("두번째"))))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.meta.result").value("SUCCESS"));
        }

        @DisplayName("온보딩 완료 후 재로그인하면, isNewUser가 false다.")
        @Test
        void returnsIsNewUserFalse_afterOnboardingCompleted() throws Exception {
            String token = loginAndGetToken();

            mockMvc.perform(put(ENDPOINT)
                    .header("Authorization", "Bearer " + token)
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(toJson(new UserV1Dto.OnboardingRequest("햇살곰"))))
                .andExpect(status().isOk());

            // 재로그인
            when(googleAuthClient.fetchUserInfo("google-token"))
                .thenReturn(new SocialUserInfo("google@omo.com", "구글유저", "g-uid-001"));

            mockMvc.perform(post("/api/v1/auth/login")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(
                        new AuthV1Dto.LoginRequest(SocialProvider.GOOGLE, "google-token"))))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.isNewUser").value(false));
        }
    }

    @DisplayName("에러 케이스")
    @Nested
    class ErrorCases {

        @DisplayName("Authorization 헤더가 없으면, 401을 반환한다.")
        @Test
        void returns401_whenNoAuthorizationHeader() throws Exception {
            mockMvc.perform(put(ENDPOINT)
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(toJson(new UserV1Dto.OnboardingRequest("햇살곰"))))
                .andExpect(status().isUnauthorized());
        }

        @DisplayName("닉네임이 1자면, 400을 반환한다.")
        @Test
        void returns400_whenNicknameIsTooShort() throws Exception {
            String token = loginAndGetToken();

            mockMvc.perform(put(ENDPOINT)
                    .header("Authorization", "Bearer " + token)
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(toJson(new UserV1Dto.OnboardingRequest("곰"))))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.meta.errorCode").value("INVALID_INPUT"));
        }

        @DisplayName("닉네임이 7자면, 400을 반환한다.")
        @Test
        void returns400_whenNicknameIsTooLong() throws Exception {
            String token = loginAndGetToken();

            mockMvc.perform(put(ENDPOINT)
                    .header("Authorization", "Bearer " + token)
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(toJson(new UserV1Dto.OnboardingRequest("일이삼사오육칠"))))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.meta.errorCode").value("INVALID_INPUT"));
        }

        @DisplayName("숫자가 포함된 닉네임이면, 400을 반환한다.")
        @Test
        void returns400_whenNicknameContainsNumber() throws Exception {
            String token = loginAndGetToken();

            mockMvc.perform(put(ENDPOINT)
                    .header("Authorization", "Bearer " + token)
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(toJson(new UserV1Dto.OnboardingRequest("햇살1"))))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.meta.errorCode").value("INVALID_INPUT"));
        }

        @DisplayName("닉네임이 null이면, 400을 반환한다.")
        @Test
        void returns400_whenNicknameIsNull() throws Exception {
            String token = loginAndGetToken();

            mockMvc.perform(put(ENDPOINT)
                    .header("Authorization", "Bearer " + token)
                    .contentType(MediaType.APPLICATION_JSON)
                    .content("{\"nickname\": null}"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.meta.errorCode").value("INVALID_INPUT"));
        }

        @DisplayName("닉네임이 빈 문자열이면, 400을 반환한다.")
        @Test
        void returns400_whenNicknameIsEmpty() throws Exception {
            String token = loginAndGetToken();

            mockMvc.perform(put(ENDPOINT)
                    .header("Authorization", "Bearer " + token)
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(toJson(new UserV1Dto.OnboardingRequest(""))))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.meta.errorCode").value("INVALID_INPUT"));
        }

        @DisplayName("요청 body가 없으면, 400을 반환한다.")
        @Test
        void returns400_whenRequestBodyIsMissing() throws Exception {
            String token = loginAndGetToken();

            mockMvc.perform(put(ENDPOINT)
                    .header("Authorization", "Bearer " + token)
                    .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());
        }
    }

    private String toJson(Object obj) throws Exception {
        return objectMapper.writeValueAsString(obj);
    }
}
