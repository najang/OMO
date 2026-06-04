import 'package:flutter_riverpod/flutter_riverpod.dart';
import 'package:riverpod_annotation/riverpod_annotation.dart';

import '../../../../core/services/token_storage.dart';
import '../../domain/entities/auth_token.dart';
import '../../domain/repositories/auth_repository.dart';
import '../datasources/auth_remote_datasource.dart';
import '../models/auth_token_model.dart';

part 'auth_repository_impl.g.dart';

@riverpod
AuthRepository authRepository(Ref ref) => AuthRepositoryImpl(
      remoteDataSource: ref.watch(authRemoteDataSourceProvider),
      tokenStorage: ref.watch(tokenStorageProvider),
    );

class AuthRepositoryImpl implements AuthRepository {
  const AuthRepositoryImpl({
    required this._remoteDataSource,
    required this._tokenStorage,
  });

  final AuthRemoteDataSource _remoteDataSource;
  final TokenStorage _tokenStorage;

  @override
  Future<AuthToken> login({
    required SocialProvider provider,
    required String token,
  }) async {
    final model = await _remoteDataSource.login(
      provider: provider,
      token: token,
    );
    await _tokenStorage.saveTokens(
      accessToken: model.accessToken,
      refreshToken: model.refreshToken,
    );
    return model.toEntity();
  }

  @override
  Future<void> completeOnboarding(String nickname) =>
      _remoteDataSource.completeOnboarding(nickname);

  @override
  Future<void> logout() => _tokenStorage.clearTokens();
}
