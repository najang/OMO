import '../entities/auth_token.dart';
import '../entities/user_profile.dart';

enum SocialProvider { google, kakao, apple }

abstract interface class AuthRepository {
  Future<AuthToken> login({
    required SocialProvider provider,
    required String token,
  });
  Future<void> completeOnboarding(String nickname);
  Future<UserProfile> getMyInfo();
  Future<void> logout();
}
