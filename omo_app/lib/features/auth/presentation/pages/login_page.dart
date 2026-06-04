import 'package:flutter/material.dart';
import 'package:flutter_riverpod/flutter_riverpod.dart';
import 'package:flutter_svg/flutter_svg.dart';

import '../providers/auth_provider.dart';

// ── 옷 아이콘 데이터 ────────────────────────────────────────────────────────

const _clothes = [
  (path: 'assets/icons/clothes/shirt.svg',   color: Color(0xFF93C5FD)),
  (path: 'assets/icons/clothes/pants.svg',   color: Color(0xFFC4B5FD)),
  (path: 'assets/icons/clothes/jacket.svg',  color: Color(0xFF6EE7B7)),
  (path: 'assets/icons/clothes/hat.svg',     color: Color(0xFFFCA5A5)),
  (path: 'assets/icons/clothes/dress.svg',   color: Color(0xFFFCD34D)),
  (path: 'assets/icons/clothes/sneaker.svg', color: Color(0xFF86EFAC)),
  (path: 'assets/icons/clothes/scarf.svg',   color: Color(0xFFF9A8D4)),
];

// ── 페이지 ──────────────────────────────────────────────────────────────────

class LoginPage extends ConsumerWidget {
  const LoginPage({super.key});

  @override
  Widget build(BuildContext context, WidgetRef ref) {
    final authState = ref.watch(authNotifierProvider);

    ref.listen<AuthState>(authNotifierProvider, (_, state) {
      if (state is AuthError) {
        ScaffoldMessenger.of(context).showSnackBar(
          SnackBar(
            content: Text(state.message),
            backgroundColor: const Color(0xFFD32F2F),
          ),
        );
      }
    });

    return Scaffold(
      backgroundColor: Colors.white,
      body: SafeArea(
        child: Center(
          child: Padding(
            padding: const EdgeInsets.symmetric(horizontal: 32),
            child: Column(
              mainAxisSize: MainAxisSize.min,
              children: [
                const _OmoLogo(),
                const SizedBox(height: 32),
                const _BouncingIconsRow(),
                const SizedBox(height: 32),
                const _Subtitle(),
                const SizedBox(height: 32),
                if (authState is AuthLoading || authState is AuthInitial)
                  const CircularProgressIndicator(color: Color(0xFF818CF8))
                else
                  Row(
                    mainAxisAlignment: MainAxisAlignment.center,
                    children: [
                      _CircleButton(
                        backgroundColor: Colors.white,
                        assetPath: 'assets/icons/social/google.svg',
                        border: Border.all(color: const Color(0xFFE5E7EB)),
                        shadow: const BoxShadow(
                          color: Color(0x14000000),
                          blurRadius: 4,
                          offset: Offset(0, 1),
                        ),
                        onPressed: () => ref
                            .read(authNotifierProvider.notifier)
                            .loginWithGoogle(),
                      ),
                      const SizedBox(width: 20),
                      _CircleButton(
                        backgroundColor: const Color(0xFFFEE500),
                        assetPath: 'assets/icons/social/kakao.svg',
                        onPressed: () => ref
                            .read(authNotifierProvider.notifier)
                            .loginWithKakao(),
                      ),
                      const SizedBox(width: 20),
                      _CircleButton(
                        backgroundColor: Colors.black,
                        assetPath: 'assets/icons/social/apple.svg',
                        onPressed: () => ref
                            .read(authNotifierProvider.notifier)
                            .loginWithApple(),
                      ),
                    ],
                  ),
              ],
            ),
          ),
        ),
      ),
    );
  }
}

// ── OMO 로고 ────────────────────────────────────────────────────────────────

class _OmoLogo extends StatelessWidget {
  const _OmoLogo();

  @override
  Widget build(BuildContext context) {
    return Container(
      width: 96,
      height: 96,
      decoration: BoxDecoration(
        color: Colors.white,
        borderRadius: BorderRadius.circular(24),
        boxShadow: const [
          BoxShadow(color: Color(0x1A000000), blurRadius: 25, offset: Offset(0, 20)),
          BoxShadow(color: Color(0x1A000000), blurRadius: 10, offset: Offset(0, 8)),
        ],
      ),
      child: Center(
        child: ShaderMask(
          shaderCallback: (bounds) => const LinearGradient(
            begin: Alignment.topLeft,
            end: Alignment.bottomRight,
            colors: [Color(0xFF38BDF8), Color(0xFF818CF8), Color(0xFFF472B6)],
            stops: [0.0, 0.5, 1.0],
          ).createShader(bounds),
          child: const Text(
            'OMO',
            style: TextStyle(
              fontSize: 32,
              fontWeight: FontWeight.w900,
              letterSpacing: -1.28,
              color: Colors.white,
            ),
          ),
        ),
      ),
    );
  }
}

// ── 바운싱 옷 아이콘 행 ─────────────────────────────────────────────────────

class _BouncingIconsRow extends StatefulWidget {
  const _BouncingIconsRow();

  @override
  State<_BouncingIconsRow> createState() => _BouncingIconsRowState();
}

class _BouncingIconsRowState extends State<_BouncingIconsRow>
    with SingleTickerProviderStateMixin {
  late final AnimationController _controller;

  static const int _totalMs = 1950;
  static const int _staggerMs = 150;
  static const int _bounceMs = 500;
  static const double _bounceHeight = 14.0;

  @override
  void initState() {
    super.initState();
    _controller = AnimationController(
      vsync: this,
      duration: const Duration(milliseconds: _totalMs),
    )..repeat();
  }

  @override
  void dispose() {
    _controller.dispose();
    super.dispose();
  }

  double _computeY(int index) {
    final phase = index * _staggerMs / _totalMs;
    final t = (_controller.value - phase + 1.0) % 1.0;
    final halfBounce = (_bounceMs / 2) / _totalMs;

    if (t < halfBounce) {
      return -_bounceHeight * Curves.easeIn.transform(t / halfBounce);
    } else if (t < halfBounce * 2) {
      return -_bounceHeight *
          (1.0 - Curves.easeOut.transform((t - halfBounce) / halfBounce));
    }
    return 0.0;
  }

  @override
  Widget build(BuildContext context) {
    return AnimatedBuilder(
      animation: _controller,
      builder: (context, _) => Row(
        mainAxisAlignment: MainAxisAlignment.center,
        crossAxisAlignment: CrossAxisAlignment.end,
        children: [
          for (int i = 0; i < _clothes.length; i++) ...[
            if (i > 0) const SizedBox(width: 12),
            Transform.translate(
              offset: Offset(0, _computeY(i)),
              child: SvgPicture.asset(
                _clothes[i].path,
                width: 32,
                height: 32,
                colorFilter: ColorFilter.mode(_clothes[i].color, BlendMode.srcIn),
              ),
            ),
          ],
        ],
      ),
    );
  }
}

// ── 서브타이틀 ──────────────────────────────────────────────────────────────

class _Subtitle extends StatelessWidget {
  const _Subtitle();

  @override
  Widget build(BuildContext context) {
    return RichText(
      text: const TextSpan(
        style: TextStyle(fontSize: 18, color: Color(0xFF374151)),
        children: [
          TextSpan(
            text: '오',
            style: TextStyle(color: Color(0xFF38BDF8), fontWeight: FontWeight.w700),
          ),
          TextSpan(text: '늘 '),
          TextSpan(
            text: '모',
            style: TextStyle(color: Color(0xFFF472B6), fontWeight: FontWeight.w700),
          ),
          TextSpan(text: '입지?'),
        ],
      ),
    );
  }
}

// ── 소셜 로그인 원형 버튼 ───────────────────────────────────────────────────

class _CircleButton extends StatelessWidget {
  const _CircleButton({
    required this.backgroundColor,
    required this.assetPath,
    required this.onPressed,
    this.border,
    this.shadow,
  });

  final Color backgroundColor;
  final String assetPath;
  final VoidCallback onPressed;
  final BoxBorder? border;
  final BoxShadow? shadow;

  @override
  Widget build(BuildContext context) {
    return GestureDetector(
      onTap: onPressed,
      child: Container(
        width: 64,
        height: 64,
        decoration: BoxDecoration(
          color: backgroundColor,
          shape: BoxShape.circle,
          border: border,
          boxShadow: shadow != null ? [shadow!] : null,
        ),
        child: Center(
          child: SvgPicture.asset(assetPath),
        ),
      ),
    );
  }
}
