package com.omo.interfaces.api;

import tools.jackson.databind.ObjectMapper;
import com.omo.application.auth.SocialUserInfo;
import com.omo.domain.example.ExampleModel;
import com.omo.domain.user.SocialProvider;
import com.omo.infrastructure.auth.social.AppleAuthClient;
import com.omo.infrastructure.auth.social.GoogleAuthClient;
import com.omo.infrastructure.auth.social.KakaoAuthClient;
import com.omo.infrastructure.example.ExampleJpaRepository;
import com.omo.interfaces.api.auth.AuthV1Dto;
import com.omo.utils.DatabaseCleanUp;
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
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
class ExampleV1ApiE2ETest {

    private static final String EXAMPLE_ENDPOINT = "/api/v1/examples/";

    private static final String GOOGLE_UID = "g-uid-001";
    private static final String GOOGLE_TOKEN = "google-token";

    private MockMvc mockMvc;

    @Autowired private ObjectMapper objectMapper;
    @Autowired private WebApplicationContext webApplicationContext;
    @Autowired private ExampleJpaRepository exampleJpaRepository;
    @Autowired private DatabaseCleanUp databaseCleanUp;

    @MockitoBean private GoogleAuthClient googleAuthClient;
    @MockitoBean private KakaoAuthClient kakaoAuthClient;
    @MockitoBean private AppleAuthClient appleAuthClient;

    @BeforeEach
    void setUp() {
        databaseCleanUp.truncateAllTables();
        mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext).build();
        when(googleAuthClient.provider()).thenReturn(SocialProvider.GOOGLE);
        when(kakaoAuthClient.provider()).thenReturn(SocialProvider.KAKAO);
        when(appleAuthClient.provider()).thenReturn(SocialProvider.APPLE);
    }

    private String loginAndGetToken() throws Exception {
        when(googleAuthClient.fetchUserInfo(GOOGLE_TOKEN))
            .thenReturn(new SocialUserInfo("google@omo.com", "구글유저", GOOGLE_UID));

        String response = mockMvc.perform(post("/api/v1/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(
                    new AuthV1Dto.LoginRequest(SocialProvider.GOOGLE, GOOGLE_TOKEN))))
            .andReturn().getResponse().getContentAsString();

        return objectMapper.readTree(response).at("/data/accessToken").asText();
    }

    @DisplayName("GET /api/v1/examples/{id}")
    @Nested
    class Get {
        @DisplayName("존재하는 예시 ID를 주면, 해당 예시 정보를 반환한다.")
        @Test
        void returnsExampleInfo_whenValidIdIsProvided() throws Exception {
            // arrange
            String token = loginAndGetToken();
            ExampleModel exampleModel = exampleJpaRepository.save(
                new ExampleModel("예시 제목", "예시 설명")
            );

            // act & assert
            mockMvc.perform(get(EXAMPLE_ENDPOINT + exampleModel.getId())
                    .header("Authorization", "Bearer " + token))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.meta.result").value("SUCCESS"))
                .andExpect(jsonPath("$.data.id").value(exampleModel.getId()))
                .andExpect(jsonPath("$.data.name").value(exampleModel.getName()))
                .andExpect(jsonPath("$.data.description").value(exampleModel.getDescription()));
        }

        @DisplayName("숫자가 아닌 ID 로 요청하면, 400 BAD_REQUEST 응답을 받는다.")
        @Test
        void throwsBadRequest_whenIdIsNotProvided() throws Exception {
            // arrange
            String token = loginAndGetToken();

            // act & assert
            mockMvc.perform(get(EXAMPLE_ENDPOINT + "나나")
                    .header("Authorization", "Bearer " + token))
                .andExpect(status().isBadRequest());
        }

        @DisplayName("존재하지 않는 예시 ID를 주면, 404 NOT_FOUND 응답을 받는다.")
        @Test
        void throwsException_whenInvalidIdIsProvided() throws Exception {
            // arrange
            String token = loginAndGetToken();
            Long invalidId = -1L;

            // act & assert
            mockMvc.perform(get(EXAMPLE_ENDPOINT + invalidId)
                    .header("Authorization", "Bearer " + token))
                .andExpect(status().isNotFound());
        }
    }
}
