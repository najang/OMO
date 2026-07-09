import 'clothing_category.dart';
import 'clothing_display_group.dart';

/// 백엔드 `GET /api/v1/clothing-items` 응답의 단일 의류 아이템.
///
/// `systemKey`가 옷장 선택/저장의 식별자이고, `displayGroup`이 화면 그룹핑을 결정한다.
class ClothingItemCatalog {
  const ClothingItemCatalog({
    required this.systemKey,
    required this.nameKo,
    required this.category,
    required this.displayGroup,
  });

  final String systemKey;
  final String nameKo;
  final ClothingCategory category;
  final ClothingDisplayGroup displayGroup;

  factory ClothingItemCatalog.fromJson(Map<String, dynamic> json) {
    return ClothingItemCatalog(
      systemKey: json['systemKey'] as String,
      nameKo: json['nameKo'] as String,
      category: ClothingCategory.values.byName((json['category'] as String).toLowerCase()),
      displayGroup: ClothingDisplayGroup.fromApiValue(json['displayGroup'] as String),
    );
  }
}
