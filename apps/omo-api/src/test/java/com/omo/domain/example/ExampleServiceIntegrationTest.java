package com.omo.domain.example;

import com.omo.infrastructure.auth.social.AppleAuthClient;
import com.omo.infrastructure.auth.social.GoogleAuthClient;
import com.omo.infrastructure.auth.social.KakaoAuthClient;
import com.omo.infrastructure.example.ExampleJpaRepository;
import com.omo.support.error.CoreException;
import com.omo.support.error.ErrorType;
import com.omo.utils.DatabaseCleanUp;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertThrows;

@SpringBootTest
class ExampleServiceIntegrationTest {

    @MockitoBean
    private GoogleAuthClient googleAuthClient;

    @MockitoBean
    private KakaoAuthClient kakaoAuthClient;

    @MockitoBean
    private AppleAuthClient appleAuthClient;

    @Autowired
    private ExampleService exampleService;

    @Autowired
    private ExampleJpaRepository exampleJpaRepository;

    @Autowired
    private DatabaseCleanUp databaseCleanUp;

    @AfterEach
    void tearDown() {
        databaseCleanUp.truncateAllTables();
    }

    @DisplayName("예시를 조회할 때,")
    @Nested
    class Get {
        @DisplayName("존재하는 예시 ID를 주면, 해당 예시 정보를 반환한다.")
        @Test
        void returnsExampleInfo_whenValidIdIsProvided() {
            // arrange
            ExampleModel exampleModel = exampleJpaRepository.save(
                new ExampleModel("예시 제목", "예시 설명")
            );

            // act
            ExampleModel result = exampleService.getExample(exampleModel.getId());

            // assert
            assertAll(
                () -> assertThat(result).isNotNull(),
                () -> assertThat(result.getId()).isEqualTo(exampleModel.getId()),
                () -> assertThat(result.getName()).isEqualTo(exampleModel.getName()),
                () -> assertThat(result.getDescription()).isEqualTo(exampleModel.getDescription())
            );
        }

        @DisplayName("존재하지 않는 예시 ID를 주면, NOT_FOUND 예외가 발생한다.")
        @Test
        void throwsException_whenInvalidIdIsProvided() {
            // arrange
            Long invalidId = 999L; // Assuming this ID does not exist

            // act
            CoreException exception = assertThrows(CoreException.class, () -> {
                exampleService.getExample(invalidId);
            });

            // assert
            assertThat(exception.getErrorType()).isEqualTo(ErrorType.NOT_FOUND);
        }
    }
}
