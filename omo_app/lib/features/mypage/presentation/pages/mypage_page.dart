import 'package:flutter/material.dart';
import 'package:flutter_riverpod/flutter_riverpod.dart';
import 'package:go_router/go_router.dart';

import '../../../../core/router/app_router.dart';
import '../../../../features/auth/presentation/providers/auth_provider.dart';

// ── 설정 메뉴 데이터 ─────────────────────────────────────────────────────────

class _MenuItem {
  const _MenuItem({required this.icon, required this.label, required this.sub, this.route});

  final IconData icon;
  final String label;
  final String sub;
  final String? route;
}

const _menuItems = [
  _MenuItem(icon: Icons.checkroom_outlined,      label: '옷장 관리',  sub: '보유 의류 수정하기', route: Routes.wardrobeManagement),
  _MenuItem(icon: Icons.notifications_outlined,  label: '알림 설정',  sub: '매일 오전 7시'),
  _MenuItem(icon: Icons.location_on_outlined,    label: '위치 설정',  sub: '서울특별시'),
  _MenuItem(icon: Icons.dark_mode_outlined,      label: '다크 모드',  sub: '사용 안 함'),
];

// ── 마이페이지 ───────────────────────────────────────────────────────────────

class MyPagePage extends ConsumerWidget {
  const MyPagePage({super.key});

  @override
  Widget build(BuildContext context, WidgetRef ref) {
    return Scaffold(
      backgroundColor: const Color(0xFFF9FAFB),
      body: SafeArea(
        child: Column(
          children: [
            _buildHeader(context),
            Expanded(
              child: SingleChildScrollView(
                padding: const EdgeInsets.fromLTRB(20, 0, 20, 32),
                child: Column(
                  children: [
                    const _ProfileCard(),
                    const SizedBox(height: 12),
                    const _MenuCard(),
                    const SizedBox(height: 12),
                    _LogoutButton(onTap: () => _handleLogout(context, ref)),
                  ],
                ),
              ),
            ),
          ],
        ),
      ),
    );
  }

  Widget _buildHeader(BuildContext context) {
    return Padding(
      padding: const EdgeInsets.fromLTRB(20, 16, 20, 16),
      child: Row(
        children: [
          GestureDetector(
            onTap: () => context.pop(),
            child: Container(
              width: 36,
              height: 36,
              decoration: BoxDecoration(
                color: Colors.white,
                shape: BoxShape.circle,
                border: Border.all(color: const Color(0xFFE5E7EB)),
                boxShadow: const [
                  BoxShadow(color: Color(0x14000000), blurRadius: 4, offset: Offset(0, 1)),
                ],
              ),
              child: const Icon(Icons.chevron_left, size: 18, color: Color(0xFF6B7280)),
            ),
          ),
          const SizedBox(width: 12),
          const Text(
            '마이페이지',
            style: TextStyle(fontSize: 18, fontWeight: FontWeight.w700, color: Color(0xFF111827)),
          ),
        ],
      ),
    );
  }

  Future<void> _handleLogout(BuildContext context, WidgetRef ref) async {
    await ref.read(authNotifierProvider.notifier).logout();
  }
}

// ── 프로필 카드 ──────────────────────────────────────────────────────────────

class _ProfileCard extends ConsumerWidget {
  const _ProfileCard();

  @override
  Widget build(BuildContext context, WidgetRef ref) {
    final myInfo = ref.watch(myInfoProvider);

    return Container(
      padding: const EdgeInsets.all(20),
      decoration: BoxDecoration(
        color: Colors.white,
        borderRadius: BorderRadius.circular(24),
        boxShadow: const [
          BoxShadow(color: Color(0x0A000000), blurRadius: 8, offset: Offset(0, 2)),
        ],
      ),
      child: Row(
        children: [
          Container(
            width: 56,
            height: 56,
            decoration: const BoxDecoration(
              shape: BoxShape.circle,
              gradient: LinearGradient(
                begin: Alignment.topLeft,
                end: Alignment.bottomRight,
                colors: [Color(0xFF38BDF8), Color(0xFF818CF8), Color(0xFFF472B6)],
                stops: [0.0, 0.5, 1.0],
              ),
            ),
            child: Center(
              child: Text(
                myInfo.maybeWhen(
                  data: (profile) => profile.nickname.isNotEmpty ? profile.nickname[0] : 'O',
                  orElse: () => 'O',
                ),
                style: const TextStyle(
                  color: Colors.white,
                  fontSize: 22,
                  fontWeight: FontWeight.w800,
                ),
              ),
            ),
          ),
          const SizedBox(width: 14),
          Expanded(
            child: Column(
              crossAxisAlignment: CrossAxisAlignment.start,
              children: [
                Text(
                  myInfo.maybeWhen(
                    data: (profile) => profile.nickname,
                    orElse: () => '...',
                  ),
                  style: const TextStyle(fontSize: 16, fontWeight: FontWeight.w700, color: Color(0xFF111827)),
                ),
                const SizedBox(height: 2),
                Text(
                  myInfo.maybeWhen(
                    data: (profile) => profile.email,
                    orElse: () => '',
                  ),
                  style: const TextStyle(fontSize: 12, color: Color(0xFF9CA3AF)),
                ),
              ],
            ),
          ),
        ],
      ),
    );
  }
}

// ── 설정 메뉴 카드 ───────────────────────────────────────────────────────────

class _MenuCard extends StatelessWidget {
  const _MenuCard();

  @override
  Widget build(BuildContext context) {
    return Container(
      decoration: BoxDecoration(
        color: Colors.white,
        borderRadius: BorderRadius.circular(24),
        boxShadow: const [
          BoxShadow(color: Color(0x0A000000), blurRadius: 8, offset: Offset(0, 2)),
        ],
      ),
      child: Column(
        children: [
          for (int i = 0; i < _menuItems.length; i++)
            _MenuRow(
              item: _menuItems[i],
              showDivider: i < _menuItems.length - 1,
            ),
        ],
      ),
    );
  }
}

class _MenuRow extends StatelessWidget {
  const _MenuRow({required this.item, required this.showDivider});

  final _MenuItem item;
  final bool showDivider;

  @override
  Widget build(BuildContext context) {
    return Column(
      children: [
        InkWell(
          onTap: item.route != null ? () => context.push(item.route!) : null,
          borderRadius: BorderRadius.circular(24),
          child: Padding(
            padding: const EdgeInsets.symmetric(horizontal: 20, vertical: 16),
            child: Row(
              children: [
                Container(
                  width: 32,
                  height: 32,
                  decoration: BoxDecoration(
                    color: const Color(0xFFF3F4F6),
                    borderRadius: BorderRadius.circular(10),
                  ),
                  child: Icon(item.icon, size: 15, color: const Color(0xFF6B7280)),
                ),
                const SizedBox(width: 14),
                Expanded(
                  child: Column(
                    crossAxisAlignment: CrossAxisAlignment.start,
                    children: [
                      Text(
                        item.label,
                        style: const TextStyle(
                          fontSize: 14,
                          fontWeight: FontWeight.w500,
                          color: Color(0xFF111827),
                        ),
                      ),
                      Text(
                        item.sub,
                        style: const TextStyle(fontSize: 12, color: Color(0xFF9CA3AF)),
                      ),
                    ],
                  ),
                ),
                const Icon(Icons.chevron_right, size: 15, color: Color(0xFFD1D5DB)),
              ],
            ),
          ),
        ),
        if (showDivider)
          const Divider(height: 1, indent: 20, endIndent: 20, color: Color(0xFFF3F4F6)),
      ],
    );
  }
}

// ── 로그아웃 버튼 ────────────────────────────────────────────────────────────

class _LogoutButton extends StatelessWidget {
  const _LogoutButton({required this.onTap});

  final VoidCallback onTap;

  @override
  Widget build(BuildContext context) {
    return InkWell(
      onTap: onTap,
      borderRadius: BorderRadius.circular(16),
      child: Container(
        padding: const EdgeInsets.all(16),
        decoration: BoxDecoration(
          color: Colors.white,
          borderRadius: BorderRadius.circular(16),
          boxShadow: const [
            BoxShadow(color: Color(0x0A000000), blurRadius: 8, offset: Offset(0, 2)),
          ],
        ),
        child: Row(
          children: [
            Container(
              width: 32,
              height: 32,
              decoration: BoxDecoration(
                color: const Color(0xFFFEF2F2),
                borderRadius: BorderRadius.circular(10),
              ),
              child: const Icon(Icons.logout, size: 15, color: Color(0xFFF87171)),
            ),
            const SizedBox(width: 14),
            const Text(
              '로그아웃',
              style: TextStyle(
                fontSize: 14,
                fontWeight: FontWeight.w500,
                color: Color(0xFFF87171),
              ),
            ),
          ],
        ),
      ),
    );
  }
}
