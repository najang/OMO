import 'package:flutter_riverpod/flutter_riverpod.dart';
import 'package:riverpod_annotation/riverpod_annotation.dart';

import '../../data/repositories/wardrobe_repository_impl.dart';
import '../../domain/entities/clothing_display_group.dart';
import '../../domain/entities/clothing_item_catalog.dart';

part 'clothing_catalog_provider.g.dart';

/// 전체 의류 카탈로그를 백엔드에서 조회한다.
/// autoDispose(기본값)이므로 화면 진입 시마다 최신 카탈로그를 가져온다.
@riverpod
Future<List<ClothingItemCatalog>> clothingItemCatalog(Ref ref) {
  return ref.watch(wardrobeRepositoryProvider).getClothingItemCatalog();
}

/// 카탈로그를 화면 그룹 순서(ClothingDisplayGroup.values)대로 그룹핑한다.
/// 빈 그룹은 제외한다.
List<MapEntry<ClothingDisplayGroup, List<ClothingItemCatalog>>> groupByDisplayGroup(
  List<ClothingItemCatalog> items,
) {
  return ClothingDisplayGroup.values
      .map((group) => MapEntry(
            group,
            items.where((item) => item.displayGroup == group).toList(),
          ))
      .where((entry) => entry.value.isNotEmpty)
      .toList();
}
