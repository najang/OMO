import 'package:dio/dio.dart';

import '../../services/token_storage.dart';

class AuthInterceptor extends Interceptor {
  const AuthInterceptor(this._tokenStorage);
  final TokenStorage _tokenStorage;

  @override
  void onRequest(
    RequestOptions options,
    RequestInterceptorHandler handler,
  ) async {
    final token = await _tokenStorage.getAccessToken();
    if (token != null) {
      options.headers['Authorization'] = 'Bearer $token';
    }
    handler.next(options);
  }

  @override
  void onError(DioException err, ErrorInterceptorHandler handler) {
    // TODO: 401 수신 시 refreshToken으로 재발급 후 원래 요청 재시도
    handler.next(err);
  }
}
