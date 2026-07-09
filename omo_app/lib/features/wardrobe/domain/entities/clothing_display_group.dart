import 'package:flutter/material.dart';

/// 백엔드 `ClothingDisplayGroup` enum과 1:1로 매칭되는 화면 표시 그룹.
///
/// "어떤 아이템이 어떤 그룹에 속하는가"는 DB(`display_group` 컬럼)가 결정하고,
/// 그룹의 라벨·아이콘·색상은 순수 프레젠테이션 정보이므로 여기서만 관리한다.
enum ClothingDisplayGroup {
  top,
  bottom,
  outer,
  dress,
  shoes,
  hat,
  scarf;

  String get apiValue => switch (this) {
        ClothingDisplayGroup.top => 'TOP',
        ClothingDisplayGroup.bottom => 'BOTTOM',
        ClothingDisplayGroup.outer => 'OUTER',
        ClothingDisplayGroup.dress => 'DRESS',
        ClothingDisplayGroup.shoes => 'SHOES',
        ClothingDisplayGroup.hat => 'HAT',
        ClothingDisplayGroup.scarf => 'SCARF',
      };

  static ClothingDisplayGroup fromApiValue(String value) => switch (value) {
        'TOP' => ClothingDisplayGroup.top,
        'BOTTOM' => ClothingDisplayGroup.bottom,
        'OUTER' => ClothingDisplayGroup.outer,
        'DRESS' => ClothingDisplayGroup.dress,
        'SHOES' => ClothingDisplayGroup.shoes,
        'HAT' => ClothingDisplayGroup.hat,
        'SCARF' => ClothingDisplayGroup.scarf,
        _ => throw ArgumentError('Unknown ClothingDisplayGroup: $value'),
      };

  String get label => switch (this) {
        ClothingDisplayGroup.top => '상의',
        ClothingDisplayGroup.bottom => '하의',
        ClothingDisplayGroup.outer => '아우터',
        ClothingDisplayGroup.dress => '원피스',
        ClothingDisplayGroup.shoes => '신발',
        ClothingDisplayGroup.hat => '모자',
        ClothingDisplayGroup.scarf => '스카프/목도리',
      };

  String get iconAsset => switch (this) {
        ClothingDisplayGroup.top => 'assets/icons/clothes/shirt.svg',
        ClothingDisplayGroup.bottom => 'assets/icons/clothes/pants.svg',
        ClothingDisplayGroup.outer => 'assets/icons/clothes/jacket.svg',
        ClothingDisplayGroup.dress => 'assets/icons/clothes/dress.svg',
        ClothingDisplayGroup.shoes => 'assets/icons/clothes/sneaker.svg',
        ClothingDisplayGroup.hat => 'assets/icons/clothes/hat.svg',
        ClothingDisplayGroup.scarf => 'assets/icons/clothes/scarf.svg',
      };

  Color get color => switch (this) {
        ClothingDisplayGroup.top => const Color(0xFF93C5FD),
        ClothingDisplayGroup.bottom => const Color(0xFFC4B5FD),
        ClothingDisplayGroup.outer => const Color(0xFF6EE7B7),
        ClothingDisplayGroup.dress => const Color(0xFFFCD34D),
        ClothingDisplayGroup.shoes => const Color(0xFF86EFAC),
        ClothingDisplayGroup.hat => const Color(0xFFFCA5A5),
        ClothingDisplayGroup.scarf => const Color(0xFFF9A8D4),
      };

  Color get bgColor => switch (this) {
        ClothingDisplayGroup.top => const Color(0xFFEFF6FF),
        ClothingDisplayGroup.bottom => const Color(0xFFF5F3FF),
        ClothingDisplayGroup.outer => const Color(0xFFF0FDF4),
        ClothingDisplayGroup.dress => const Color(0xFFFFFBEB),
        ClothingDisplayGroup.shoes => const Color(0xFFF0FDF4),
        ClothingDisplayGroup.hat => const Color(0xFFFEF2F2),
        ClothingDisplayGroup.scarf => const Color(0xFFFDF2F8),
      };
}
