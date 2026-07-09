// GENERATED CODE - DO NOT MODIFY BY HAND

part of 'clothing_catalog_provider.dart';

// **************************************************************************
// RiverpodGenerator
// **************************************************************************

String _$clothingItemCatalogHash() =>
    r'60fedcb17161bcf8880d61d8eb607c628d4ff933';

/// 전체 의류 카탈로그를 백엔드에서 조회한다.
/// autoDispose(기본값)이므로 화면 진입 시마다 최신 카탈로그를 가져온다.
///
/// Copied from [clothingItemCatalog].
@ProviderFor(clothingItemCatalog)
final clothingItemCatalogProvider =
    AutoDisposeFutureProvider<List<ClothingItemCatalog>>.internal(
      clothingItemCatalog,
      name: r'clothingItemCatalogProvider',
      debugGetCreateSourceHash: const bool.fromEnvironment('dart.vm.product')
          ? null
          : _$clothingItemCatalogHash,
      dependencies: null,
      allTransitiveDependencies: null,
    );

@Deprecated('Will be removed in 3.0. Use Ref instead')
// ignore: unused_element
typedef ClothingItemCatalogRef =
    AutoDisposeFutureProviderRef<List<ClothingItemCatalog>>;
// ignore_for_file: type=lint
// ignore_for_file: subtype_of_sealed_class, invalid_use_of_internal_member, invalid_use_of_visible_for_testing_member, deprecated_member_use_from_same_package
