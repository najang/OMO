import 'package:dio/dio.dart';
import 'package:flutter_riverpod/flutter_riverpod.dart';
import 'package:riverpod_annotation/riverpod_annotation.dart';

import '../../../../core/network/dio_client.dart';
import '../../domain/entities/clothing_item_catalog.dart';
import '../../domain/entities/temp_sensitivity.dart';

part 'wardrobe_remote_datasource.g.dart';

@riverpod
WardrobeRemoteDataSource wardrobeRemoteDataSource(Ref ref) =>
    WardrobeRemoteDataSource(ref.watch(dioClientProvider));

class WardrobeRemoteDataSource {
  const WardrobeRemoteDataSource(this._dio);
  final Dio _dio;

  Future<List<ClothingItemCatalog>> getClothingItemCatalog() async {
    final response = await _dio.get<Map<String, dynamic>>('/api/v1/clothing-items');
    final data = response.data!['data'] as Map<String, dynamic>;
    final items = data['items'] as List<dynamic>;
    return items
        .cast<Map<String, dynamic>>()
        .map(ClothingItemCatalog.fromJson)
        .toList();
  }

  Future<List<String>> getWardrobe() async {
    final response = await _dio.get<Map<String, dynamic>>('/api/v1/users/me/wardrobe');
    final data = response.data!['data'] as Map<String, dynamic>;
    final items = data['items'] as List<dynamic>;
    return items
        .cast<Map<String, dynamic>>()
        .map((item) => item['systemKey'] as String)
        .toList();
  }

  Future<void> setupWardrobe(List<String> itemKeys) => _dio.post<void>(
        '/api/v1/users/me/wardrobe',
        data: {'itemKeys': itemKeys},
      );

  Future<void> initTempProfile(TempSensitivity tempSensitivity) => _dio.post<void>(
        '/api/v1/users/me/temp-profile',
        data: {'tempSensitivity': tempSensitivity.apiValue},
      );
}
