import 'package:dio/dio.dart';
import 'package:flutter_riverpod/flutter_riverpod.dart';
import 'package:riverpod_annotation/riverpod_annotation.dart';

import '../constants/api_constants.dart';
import '../services/token_storage.dart';
import 'interceptors/auth_interceptor.dart';
import 'interceptors/logging_interceptor.dart';

part 'dio_client.g.dart';

@riverpod
Dio dioClient(Ref ref) {
  final tokenStorage = ref.watch(tokenStorageProvider);

  final dio = Dio(
    BaseOptions(
      baseUrl: ApiConstants.baseUrl,
      connectTimeout: ApiConstants.connectTimeout,
      receiveTimeout: ApiConstants.receiveTimeout,
      headers: {'Content-Type': 'application/json'},
    ),
  );

  dio.interceptors.addAll([
    AuthInterceptor(tokenStorage),
    LoggingInterceptor(),
  ]);

  return dio;
}
