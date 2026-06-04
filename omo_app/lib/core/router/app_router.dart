import 'package:flutter/material.dart';
import 'package:flutter_riverpod/flutter_riverpod.dart';
import 'package:go_router/go_router.dart';
import 'package:riverpod_annotation/riverpod_annotation.dart';

import '../../features/auth/presentation/pages/login_page.dart';
import '../../features/auth/presentation/pages/nickname_setup_page.dart';
import '../../features/auth/presentation/providers/auth_provider.dart';
import '../../features/home/presentation/pages/home_page.dart';
import '../../features/mypage/presentation/pages/mypage_page.dart';
import '../../features/sample/presentation/pages/sample_page.dart';

part 'app_router.g.dart';

@riverpod
GoRouter appRouter(Ref ref) {
  final listenable = _AuthListenable(ref);
  ref.onDispose(listenable.dispose);

  return GoRouter(
    initialLocation: Routes.login,
    refreshListenable: listenable,
    redirect: (context, state) {
      final authState = ref.read(authNotifierProvider);
      final location = state.uri.toString();

      if (authState is AuthInitial || authState is AuthLoading) return null;

      if (authState is AuthUnauthenticated || authState is AuthError) {
        return location == Routes.login ? null : Routes.login;
      }

      if (authState is AuthAuthenticated) {
        if (authState.token.isNewUser) {
          return location == Routes.nickname ? null : Routes.nickname;
        }
        if (location == Routes.login || location == Routes.nickname) {
          return Routes.home;
        }
      }

      return null;
    },
    routes: [
      GoRoute(
        path: Routes.login,
        builder: (context, state) => const LoginPage(),
      ),
      GoRoute(
        path: Routes.nickname,
        builder: (context, state) => const NicknameSetupPage(),
      ),
      GoRoute(
        path: Routes.home,
        builder: (context, state) => const HomePage(),
      ),
      GoRoute(
        path: Routes.mypage,
        builder: (context, state) => const MyPagePage(),
      ),
      GoRoute(
        path: Routes.sample,
        builder: (context, state) => const SamplePage(),
      ),
    ],
    errorBuilder: (context, state) => Scaffold(
      body: Center(child: Text('Page not found: ${state.error}')),
    ),
  );
}

abstract class Routes {
  static const String login = '/login';
  static const String nickname = '/nickname';
  static const String home = '/';
  static const String mypage = '/mypage';
  static const String sample = '/sample';
}

class _AuthListenable extends ChangeNotifier {
  _AuthListenable(Ref ref) {
    _sub = ref.listen<AuthState>(authNotifierProvider, (prev, next) {
      notifyListeners();
    });
  }

  late final ProviderSubscription<AuthState> _sub;

  @override
  void dispose() {
    _sub.close();
    super.dispose();
  }
}
