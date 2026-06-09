package com.omo.interfaces.api.wardrobe;

import tools.jackson.databind.ObjectMapper;
import com.omo.application.auth.SocialUserInfo;
import com.omo.domain.user.SocialProvider;
import com.omo.domain.user.UserRepository;
import com.omo.domain.user.UserTempProfileRepository;
import com.omo.domain.wardrobe.ClothingCategory;
import com.omo.domain.wardrobe.ClothingItem;
import com.omo.domain.wardrobe.ClothingItemRepository;
import com.omo.domain.wardrobe.TempSensitivity;
import com.omo.domain.wardrobe.UserWardrobeRepository;
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
import org.springframework.transaction.support.TransactionTemplate;
import org.springframework.web.context.WebApplicationContext;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
class WardrobeV1ApiE2ETest {

    private static final String WARDROBE_ENDPOINT = "/api/v1/users/me/wardrobe";
    private static final String TEMP_PROFILE_ENDPOINT = "/api/v1/users/me/temp-profile";

    private static final String GOOGLE_UID = "g-uid-001";
    private static final String GOOGLE_TOKEN = "google-token";

    private MockMvc mockMvc;

    @Autowired private ObjectMapper objectMapper;
    @Autowired private WebApplicationContext webApplicationContext;
    @Autowired private DatabaseCleanUp databaseCleanUp;
    @Autowired private UserRepository userRepository;
    @Autowired private UserWardrobeRepository userWardrobeRepository;
    @Autowired private UserTempProfileRepository userTempProfileRepository;
    @Autowired private ClothingItemRepository clothingItemRepository;
    @Autowired private TransactionTemplate transactionTemplate;

    @MockitoBean private GoogleAuthClient googleAuthClient;
    @MockitoBean private KakaoAuthClient kakaoAuthClient;
    @MockitoBean private AppleAuthClient appleAuthClient;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext).build();
        when(googleAuthClient.provider()).thenReturn(SocialProvider.GOOGLE);
        when(kakaoAuthClient.provider()).thenReturn(SocialProvider.KAKAO);
        when(appleAuthClient.provider()).thenReturn(SocialProvider.APPLE);
        seedClothingItems();
    }

    @AfterEach
    void tearDown() {
        databaseCleanUp.truncateAllTables();
    }

    private void seedClothingItems() {
        clothingItemRepository.saveAll(List.of(
            ClothingItem.of("short-tee",  ClothingCategory.TOP,   "반팔 티셔츠"),
            ClothingItem.of("long-tee",   ClothingCategory.TOP,   "긴팔 티셔츠"),
            ClothingItem.of("padding",    ClothingCategory.OUTER, "패딩"),
            ClothingItem.of("jeans",      ClothingCategory.PANTS, "청바지"),
            ClothingItem.of("slacks",     ClothingCategory.PANTS, "슬랙스")
        ));
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

    @DisplayName("POST /api/v1/users/me/wardrobe —")
    @Nested
    class SetupWardrobe {

        @DisplayName("유효한 아이템 키 목록이면, 200을 반환한다.")
        @Test
        void returns200_whenItemKeysAreValid() throws Exception {
            String token = loginAndGetToken();

            mockMvc.perform(post(WARDROBE_ENDPOINT)
                    .header("Authorization", "Bearer " + token)
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(toJson(new WardrobeV1Dto.WardrobeSetupRequest(List.of("short-tee", "padding")))))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.meta.result").value("SUCCESS"));
        }

        @DisplayName("동일 유저가 두 번 호출하면, 아이템이 두 번째 값으로 교체된다.")
        @Test
        void replacesItems_whenCalledTwice() throws Exception {
            String token = loginAndGetToken();

            mockMvc.perform(post(WARDROBE_ENDPOINT)
                    .header("Authorization", "Bearer " + token)
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(toJson(new WardrobeV1Dto.WardrobeSetupRequest(List.of("short-tee")))))
                .andExpect(status().isOk());

            mockMvc.perform(post(WARDROBE_ENDPOINT)
                    .header("Authorization", "Bearer " + token)
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(toJson(new WardrobeV1Dto.WardrobeSetupRequest(List.of("padding", "jeans")))))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.meta.result").value("SUCCESS"));

            Set<String> itemKeys = transactionTemplate.execute(status -> {
                var user = userRepository.findByProviderInfo(SocialProvider.GOOGLE, GOOGLE_UID).orElseThrow();
                var wardrobe = userWardrobeRepository.findByUser(user).orElseThrow();
                return wardrobe.getItems().stream()
                    .map(ClothingItem::getSystemKey)
                    .collect(Collectors.toSet());
            });
            assertThat(itemKeys)
                .containsExactlyInAnyOrder("padding", "jeans")
                .doesNotContain("short-tee");
        }

        @DisplayName("아이템 목록이 빈 목록이면, 400을 반환한다.")
        @Test
        void returns400_whenItemKeysIsEmpty() throws Exception {
            String token = loginAndGetToken();

            mockMvc.perform(post(WARDROBE_ENDPOINT)
                    .header("Authorization", "Bearer " + token)
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(toJson(new WardrobeV1Dto.WardrobeSetupRequest(List.of()))))
                .andExpect(status().isBadRequest());
        }

        @DisplayName("존재하지 않는 아이템 키가 포함되면, 400을 반환한다.")
        @Test
        void returns400_whenItemKeyIsUnknown() throws Exception {
            String token = loginAndGetToken();

            mockMvc.perform(post(WARDROBE_ENDPOINT)
                    .header("Authorization", "Bearer " + token)
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(toJson(new WardrobeV1Dto.WardrobeSetupRequest(List.of("invalid-key")))))
                .andExpect(status().isBadRequest());
        }

        @DisplayName("Authorization 헤더가 없으면, 401을 반환한다.")
        @Test
        void returns401_whenNoAuthorizationHeader() throws Exception {
            mockMvc.perform(post(WARDROBE_ENDPOINT)
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(toJson(new WardrobeV1Dto.WardrobeSetupRequest(List.of("short-tee")))))
                .andExpect(status().isUnauthorized());
        }

        @DisplayName("유효하지 않은 JWT 토큰이면, 401을 반환한다.")
        @Test
        void returns401_whenTokenIsInvalid() throws Exception {
            mockMvc.perform(post(WARDROBE_ENDPOINT)
                    .header("Authorization", "Bearer invalid.jwt.token")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(toJson(new WardrobeV1Dto.WardrobeSetupRequest(List.of("short-tee")))))
                .andExpect(status().isUnauthorized());
        }

        @DisplayName("itemKeys가 null이면, 400을 반환한다.")
        @Test
        void returns400_whenItemKeysIsNull() throws Exception {
            String token = loginAndGetToken();

            mockMvc.perform(post(WARDROBE_ENDPOINT)
                    .header("Authorization", "Bearer " + token)
                    .contentType(MediaType.APPLICATION_JSON)
                    .content("{\"itemKeys\": null}"))
                .andExpect(status().isBadRequest());
        }

        @DisplayName("요청 body가 없으면, 400을 반환한다.")
        @Test
        void returns400_whenRequestBodyIsMissing() throws Exception {
            String token = loginAndGetToken();

            mockMvc.perform(post(WARDROBE_ENDPOINT)
                    .header("Authorization", "Bearer " + token)
                    .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());
        }
    }

    @DisplayName("POST /api/v1/users/me/temp-profile —")
    @Nested
    class InitTempProfile {

        @DisplayName("유효한 체감 민감도이면, 200을 반환한다.")
        @Test
        void returns200_whenTempSensitivityIsValid() throws Exception {
            String token = loginAndGetToken();

            mockMvc.perform(post(TEMP_PROFILE_ENDPOINT)
                    .header("Authorization", "Bearer " + token)
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(toJson(new WardrobeV1Dto.TempProfileSetupRequest(TempSensitivity.COLD))))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.meta.result").value("SUCCESS"));
        }

        @DisplayName("두 번 호출해도 200을 반환하고, 기존 temp_offset이 유지된다.")
        @Test
        void preservesOriginalTempOffset_whenCalledTwice() throws Exception {
            String token = loginAndGetToken();

            mockMvc.perform(post(TEMP_PROFILE_ENDPOINT)
                    .header("Authorization", "Bearer " + token)
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(toJson(new WardrobeV1Dto.TempProfileSetupRequest(TempSensitivity.COLD))))
                .andExpect(status().isOk());

            mockMvc.perform(post(TEMP_PROFILE_ENDPOINT)
                    .header("Authorization", "Bearer " + token)
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(toJson(new WardrobeV1Dto.TempProfileSetupRequest(TempSensitivity.VERY_HEAT))))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.meta.result").value("SUCCESS"));

            var user = userRepository.findByProviderInfo(SocialProvider.GOOGLE, GOOGLE_UID).orElseThrow();
            var profile = userTempProfileRepository.findByUser(user).orElseThrow();
            assertThat(profile.getTempOffset()).isEqualTo(TempSensitivity.COLD.toTempOffset());
        }

        @DisplayName("Authorization 헤더가 없으면, 401을 반환한다.")
        @Test
        void returns401_whenNoAuthorizationHeader() throws Exception {
            mockMvc.perform(post(TEMP_PROFILE_ENDPOINT)
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(toJson(new WardrobeV1Dto.TempProfileSetupRequest(TempSensitivity.NORMAL))))
                .andExpect(status().isUnauthorized());
        }

        @DisplayName("유효하지 않은 JWT 토큰이면, 401을 반환한다.")
        @Test
        void returns401_whenTokenIsInvalid() throws Exception {
            mockMvc.perform(post(TEMP_PROFILE_ENDPOINT)
                    .header("Authorization", "Bearer invalid.jwt.token")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(toJson(new WardrobeV1Dto.TempProfileSetupRequest(TempSensitivity.NORMAL))))
                .andExpect(status().isUnauthorized());
        }

        @DisplayName("tempSensitivity가 null이면, 400을 반환한다.")
        @Test
        void returns400_whenTempSensitivityIsNull() throws Exception {
            String token = loginAndGetToken();

            mockMvc.perform(post(TEMP_PROFILE_ENDPOINT)
                    .header("Authorization", "Bearer " + token)
                    .contentType(MediaType.APPLICATION_JSON)
                    .content("{\"tempSensitivity\": null}"))
                .andExpect(status().isBadRequest());
        }

        @DisplayName("요청 body가 없으면, 400을 반환한다.")
        @Test
        void returns400_whenRequestBodyIsMissing() throws Exception {
            String token = loginAndGetToken();

            mockMvc.perform(post(TEMP_PROFILE_ENDPOINT)
                    .header("Authorization", "Bearer " + token)
                    .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());
        }

        @DisplayName("알 수 없는 tempSensitivity 값이면, 400을 반환한다.")
        @Test
        void returns400_whenTempSensitivityValueIsUnknown() throws Exception {
            String token = loginAndGetToken();

            mockMvc.perform(post(TEMP_PROFILE_ENDPOINT)
                    .header("Authorization", "Bearer " + token)
                    .contentType(MediaType.APPLICATION_JSON)
                    .content("{\"tempSensitivity\": \"UNKNOWN_VALUE\"}"))
                .andExpect(status().isBadRequest());
        }
    }

    private String toJson(Object obj) throws Exception {
        return objectMapper.writeValueAsString(obj);
    }
}
