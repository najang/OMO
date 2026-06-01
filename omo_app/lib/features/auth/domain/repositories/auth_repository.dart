import '../entities/auth_token.dart';

enum SocialProvider { google, kakao, apple }

abstract interface class AuthRepository {
  Future<AuthToken> login({
    required SocialProvider provider,
    required String token,
  });
  Future<void> logout();
}
