package com.omo;

import com.omo.infrastructure.auth.social.AppleAuthClient;
import com.omo.infrastructure.auth.social.GoogleAuthClient;
import com.omo.infrastructure.auth.social.KakaoAuthClient;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;

@SpringBootTest
class OmoApiContextTest {

    @MockitoBean
    private GoogleAuthClient googleAuthClient;

    @MockitoBean
    private KakaoAuthClient kakaoAuthClient;

    @MockitoBean
    private AppleAuthClient appleAuthClient;

    @Test
    void contextLoads() {
    }
}
