import 'package:flutter_riverpod/flutter_riverpod.dart';
import 'package:google_sign_in/google_sign_in.dart';
import 'package:kakao_flutter_sdk_user/kakao_flutter_sdk_user.dart';
import 'package:riverpod_annotation/riverpod_annotation.dart';
import 'package:sign_in_with_apple/sign_in_with_apple.dart';

import '../../../../core/services/token_storage.dart';
import '../../data/repositories/auth_repository_impl.dart';
import '../../domain/entities/auth_token.dart';
import '../../domain/entities/user_profile.dart';
import '../../domain/repositories/auth_repository.dart';

part 'auth_provider.g.dart';

@riverpod
Future<UserProfile> myInfo(Ref ref) =>
    ref.watch(authRepositoryProvider).getMyInfo();

sealed class AuthState {
  const AuthState();
}

class AuthInitial extends AuthState {
  const AuthInitial();
}

class AuthLoading extends AuthState {
  const AuthLoading();
}

class AuthAuthenticated extends AuthState {
  const AuthAuthenticated(this.token);
  final AuthToken token;
}

class AuthUnauthenticated extends AuthState {
  const AuthUnauthenticated();
}

class AuthError extends AuthState {
  const AuthError(this.message);
  final String message;
}

@riverpod
class AuthNotifier extends _$AuthNotifier {
  @override
  AuthState build() => const AuthInitial();

  Future<void> initialize() async {
    final tokenStorage = ref.read(tokenStorageProvider);
    final accessToken = await tokenStorage.getAccessToken();
    if (accessToken != null) {
      state = AuthAuthenticated(
        AuthToken(accessToken: accessToken, refreshToken: '', userId: 0, isNewUser: false),
      );
    } else {
      state = const AuthUnauthenticated();
    }
  }

  Future<void> setupNickname(String nickname) async {
    if (state is! AuthAuthenticated) return;
    await ref.read(authRepositoryProvider).completeOnboarding(nickname);
  }

  Future<void> finalizeOnboarding() async {
    final current = state;
    if (current is! AuthAuthenticated) return;
    state = AuthAuthenticated(
      AuthToken(
        accessToken: current.token.accessToken,
        refreshToken: current.token.refreshToken,
        userId: current.token.userId,
        isNewUser: false,
      ),
    );
  }

  Future<void> loginWithGoogle() async {
    state = const AuthLoading();
    try {
      final googleSignIn = GoogleSignIn();
      final account = await googleSignIn.signIn();
      if (account == null) {
        state = const AuthUnauthenticated();
        return;
      }
      final auth = await account.authentication;
      final idToken = auth.idToken;
      if (idToken == null) throw Exception('Google idToken을 가져올 수 없습니다.');

      await _loginWithBackend(SocialProvider.google, idToken);
    } catch (e) {
      state = AuthError(e.toString());
    }
  }

  Future<void> loginWithApple() async {
    state = const AuthLoading();
    try {
      final credential = await SignInWithApple.getAppleIDCredential(
        scopes: [
          AppleIDAuthorizationScopes.email,
          AppleIDAuthorizationScopes.fullName,
        ],
      );
      final identityToken = credential.identityToken;
      if (identityToken == null) throw Exception('Apple identityToken을 가져올 수 없습니다.');

      await _loginWithBackend(SocialProvider.apple, identityToken);
    } catch (e) {
      state = AuthError(e.toString());
    }
  }

  Future<void> loginWithKakao() async {
    state = const AuthLoading();
    try {
      final OAuthToken token;
      if (await isKakaoTalkInstalled()) {
        token = await UserApi.instance.loginWithKakaoTalk();
      } else {
        token = await UserApi.instance.loginWithKakaoAccount();
      }
      await _loginWithBackend(SocialProvider.kakao, token.accessToken);
    } catch (e) {
      state = AuthError(e.toString());
    }
  }

  Future<void> logout() async {
    await ref.read(authRepositoryProvider).logout();
    state = const AuthUnauthenticated();
  }

  Future<void> _loginWithBackend(SocialProvider provider, String token) async {
    final authToken = await ref.read(authRepositoryProvider).login(
          provider: provider,
          token: token,
        );
    state = AuthAuthenticated(authToken);
  }
}
