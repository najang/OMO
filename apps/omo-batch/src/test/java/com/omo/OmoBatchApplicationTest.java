package com.omo;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;

@SpringBootTest
@TestPropertySource(properties = "spring.batch.job.enabled=false")
public class OmoBatchApplicationTest {
    @Test
    void contextLoads() {}
}
