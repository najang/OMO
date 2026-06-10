package com.omo.interfaces.api.wardrobe;

import tools.jackson.databind.ObjectMapper;
import com.omo.application.auth.SocialUserInfo;
import com.omo.domain.user.SocialProvider;
import com.omo.domain.wardrobe.ClothingCategory;
import com.omo.domain.wardrobe.ClothingDisplayGroup;
import com.omo.domain.wardrobe.ClothingItem;
import com.omo.domain.wardrobe.ClothingItemRepository;
import com.omo.infrastructure.auth.social.AppleAuthClient;
import com.omo.infrastructure.auth.social.GoogleAuthClient;
import com.omo.infrastructure.auth.social.KakaoAuthClient;
import com.omo.interfaces.api.auth.AuthV1Dto;
import com.omo.utils.DatabaseCleanUp;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import java.util.List;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
class ClothingItemV1ApiE2ETest {

    private static final String CATALOG_ENDPOINT = "/api/v1/clothing-items";

    private static final String GOOGLE_UID = "g-uid-catalog";
    private static final String GOOGLE_TOKEN = "google-token-catalog";

    private MockMvc mockMvc;

    @Autowired private ObjectMapper objectMapper;
    @Autowired private WebApplicationContext webApplicationContext;
    @Autowired private DatabaseCleanUp databaseCleanUp;
    @Autowired private ClothingItemRepository clothingItemRepository;

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
        seedClothingItems();
    }

    private void seedClothingItems() {
        clothingItemRepository.saveAll(List.of(
            ClothingItem.of("short-tee", ClothingCategory.TOP,       ClothingDisplayGroup.TOP,   "반팔 티셔츠"),
            ClothingItem.of("skirt",     ClothingCategory.SKIRT,     ClothingDisplayGroup.BOTTOM, "치마"),
            ClothingItem.of("cap",       ClothingCategory.ACCESSORY, ClothingDisplayGroup.HAT,   "볼캡"),
            ClothingItem.of("scarf",     ClothingCategory.ACCESSORY, ClothingDisplayGroup.SCARF, "스카프")
        ));
    }

    private String loginAndGetToken() throws Exception {
        when(googleAuthClient.fetchUserInfo(GOOGLE_TOKEN))
            .thenReturn(new SocialUserInfo("catalog@omo.com", "카탈로그유저", GOOGLE_UID));

        String response = mockMvc.perform(post("/api/v1/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(
                    new AuthV1Dto.LoginRequest(SocialProvider.GOOGLE, GOOGLE_TOKEN))))
            .andReturn().getResponse().getContentAsString();

        return objectMapper.readTree(response).at("/data/accessToken").asText();
    }

    @DisplayName("GET /api/v1/clothing-items —")
    @Test
    void returns200_withFullCatalog() throws Exception {
        String token = loginAndGetToken();

        mockMvc.perform(get(CATALOG_ENDPOINT)
                .header("Authorization", "Bearer " + token))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.meta.result").value("SUCCESS"))
            .andExpect(jsonPath("$.data.items").isArray())
            .andExpect(jsonPath("$.data.items.length()").value(4))
            .andExpect(jsonPath("$.data.items[0].systemKey").isNotEmpty())
            .andExpect(jsonPath("$.data.items[0].category").isNotEmpty())
            .andExpect(jsonPath("$.data.items[0].displayGroup").isNotEmpty())
            .andExpect(jsonPath("$.data.items[0].nameKo").isNotEmpty());
    }

    @DisplayName("Authorization 헤더가 없으면, 401을 반환한다.")
    @Test
    void returns401_whenNoAuthorizationHeader() throws Exception {
        mockMvc.perform(get(CATALOG_ENDPOINT))
            .andExpect(status().isUnauthorized());
    }
}
