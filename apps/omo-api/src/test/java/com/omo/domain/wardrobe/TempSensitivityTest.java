package com.omo.domain.wardrobe;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("TempSensitivity가 tempOffset으로 변환될 때,")
class TempSensitivityTest {

    @DisplayName("각 enum 값이 올바른 offset으로 변환된다.")
    @Test
    void mapsToCorrectOffset() {
        assertAll(
            () -> assertThat(TempSensitivity.VERY_COLD.toTempOffset()).isEqualTo(-2.0),
            () -> assertThat(TempSensitivity.COLD.toTempOffset()).isEqualTo(-1.0),
            () -> assertThat(TempSensitivity.NORMAL.toTempOffset()).isEqualTo(0.0),
            () -> assertThat(TempSensitivity.HEAT.toTempOffset()).isEqualTo(1.0),
            () -> assertThat(TempSensitivity.VERY_HEAT.toTempOffset()).isEqualTo(2.0)
        );
    }
}
