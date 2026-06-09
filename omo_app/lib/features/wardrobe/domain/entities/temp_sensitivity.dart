enum TempSensitivity {
  veryHeat,
  heat,
  normal,
  cold,
  veryCold;

  String get apiValue => switch (this) {
        TempSensitivity.veryHeat => 'VERY_HEAT',
        TempSensitivity.heat => 'HEAT',
        TempSensitivity.normal => 'NORMAL',
        TempSensitivity.cold => 'COLD',
        TempSensitivity.veryCold => 'VERY_COLD',
      };

  String get label => switch (this) {
        TempSensitivity.veryHeat => '더위를 많이 탐',
        TempSensitivity.heat => '더위를 조금 탐',
        TempSensitivity.normal => '보통',
        TempSensitivity.cold => '추위를 조금 탐',
        TempSensitivity.veryCold => '추위를 많이 탐',
      };

  String get emoji => switch (this) {
        TempSensitivity.veryHeat => '🥵',
        TempSensitivity.heat => '😓',
        TempSensitivity.normal => '😊',
        TempSensitivity.cold => '🥶',
        TempSensitivity.veryCold => '🧊',
      };

  String get description => switch (this) {
        TempSensitivity.veryHeat => '여름엔 민소매, 겨울도 얇게 입어요',
        TempSensitivity.heat => '다른 사람보다 더위를 타는 편이에요',
        TempSensitivity.normal => '날씨에 딱 맞게 입어요',
        TempSensitivity.cold => '다른 사람보다 추위를 타는 편이에요',
        TempSensitivity.veryCold => '여름에도 가디건, 겨울엔 두껍게 입어요',
      };
}
