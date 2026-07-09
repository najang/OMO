import 'package:dio/dio.dart';
import 'package:flutter_riverpod/flutter_riverpod.dart';
import 'package:riverpod_annotation/riverpod_annotation.dart';

import '../../../../core/network/dio_client.dart';
import '../../domain/repositories/auth_repository.dart';
import '../models/auth_token_model.dart';

part 'auth_remote_datasource.g.dart';

@riverpod
AuthRemoteDataSource authRemoteDataSource(Ref ref) =>
    AuthRemoteDataSource(ref.watch(dioClientProvider));

class AuthRemoteDataSource {
  const AuthRemoteDataSource(this._dio);
  final Dio _dio;

  Future<AuthTokenModel> login({
    required SocialProvider provider,
    required String token,
  }) async {
    final response = await _dio.post<Map<String, dynamic>>(
      '/api/v1/auth/login',
      data: {
        'provider': provider.name.toUpperCase(),
        'token': token,
      },
    );
    final data = response.data!['data'] as Map<String, dynamic>;
    return AuthTokenModel.fromJson(data);
  }

  Future<void> completeOnboarding(String nickname) => _dio.put<void>(
        '/api/v1/users/me/onboarding',
        data: {'nickname': nickname},
      );

  Future<Map<String, dynamic>> getMyInfo() async {
    final response = await _dio.get<Map<String, dynamic>>('/api/v1/users/me');
    return response.data!['data'] as Map<String, dynamic>;
  }
}
