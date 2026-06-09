package com.omo.domain.wardrobe;

public enum TempSensitivity {
    VERY_COLD(-2.0),
    COLD(-1.0),
    NORMAL(0.0),
    HEAT(1.0),
    VERY_HEAT(2.0);

    private final double tempOffset;

    TempSensitivity(double tempOffset) {
        this.tempOffset = tempOffset;
    }

    public double toTempOffset() {
        return tempOffset;
    }
}
