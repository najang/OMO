import 'dart:math' as math;

import 'package:flutter/material.dart';
import 'package:go_router/go_router.dart';

import '../../../../../core/router/app_router.dart';

// ── 더미 날씨 데이터 ─────────────────────────────────────────────────────────

const _weatherCity = '서울';
const _weatherTemp = 23;
const _weatherFeels = 21;
const _weatherCondition = '맑음';
const _weatherHumidity = 48;
const _weatherWind = 3.2;
const _weatherVisibility = 10;

// ── 코디 추천 데이터 ─────────────────────────────────────────────────────────

class _OutfitItem {
  const _OutfitItem({
    required this.category,
    required this.item,
    required this.reason,
    required this.bgColor,
    required this.accentColor,
    required this.iconPath,
  });

  final String category;
  final String item;
  final String reason;
  final Color bgColor;
  final Color accentColor;
  final String iconPath;
}

const _outfits = [
  _OutfitItem(
    category: '상의',
    item: '얇은 셔츠 또는 티셔츠',
    reason: '23°C의 쾌적한 날씨에 딱 맞아요',
    bgColor: Color(0xFFEFF6FF),
    accentColor: Color(0xFF93C5FD),
    iconPath: 'assets/icons/clothes/shirt.svg',
  ),
  _OutfitItem(
    category: '하의',
    item: '청바지 또는 면바지',
    reason: '일교차에 대비한 긴 바지 추천',
    bgColor: Color(0xFFF5F3FF),
    accentColor: Color(0xFFC4B5FD),
    iconPath: 'assets/icons/clothes/pants.svg',
  ),
  _OutfitItem(
    category: '아우터',
    item: '가벼운 재킷',
    reason: '저녁엔 선선해져서 걸치기 좋아요',
    bgColor: Color(0xFFF0FDF4),
    accentColor: Color(0xFF6EE7B7),
    iconPath: 'assets/icons/clothes/jacket.svg',
  ),
  _OutfitItem(
    category: '신발',
    item: '스니커즈',
    reason: '활동하기 편하고 스타일도 UP',
    bgColor: Color(0xFFF0FDF4),
    accentColor: Color(0xFF86EFAC),
    iconPath: 'assets/icons/clothes/sneaker.svg',
  ),
];

// ── 홈 페이지 ────────────────────────────────────────────────────────────────

class HomePage extends StatelessWidget {
  const HomePage({super.key});

  @override
  Widget build(BuildContext context) {
    return Scaffold(
      backgroundColor: const Color(0xFFF9FAFB),
      body: SafeArea(
        child: CustomScrollView(
          slivers: [
            SliverToBoxAdapter(child: _buildHeader(context)),
            SliverPadding(
              padding: const EdgeInsets.fromLTRB(20, 0, 20, 32),
              sliver: SliverList(
                delegate: SliverChildListDelegate([
                  const _WeatherCard(),
                  const SizedBox(height: 16),
                  const _SectionHeader(),
                  const SizedBox(height: 12),
                  ..._outfits.map((o) => Padding(
                        padding: const EdgeInsets.only(bottom: 10),
                        child: _OutfitCard(outfit: o),
                      )),
                  const SizedBox(height: 4),
                  const _TipCard(),
                ]),
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
        mainAxisAlignment: MainAxisAlignment.spaceBetween,
        children: [
          Column(
            crossAxisAlignment: CrossAxisAlignment.start,
            children: [
              const Text(
                '오늘의 날씨',
                style: TextStyle(fontSize: 12, color: Color(0xFF9CA3AF)),
              ),
              ShaderMask(
                shaderCallback: (bounds) => const LinearGradient(
                  begin: Alignment.topLeft,
                  end: Alignment.bottomRight,
                  colors: [Color(0xFF38BDF8), Color(0xFF818CF8), Color(0xFFF472B6)],
                  stops: [0.0, 0.5, 1.0],
                ).createShader(bounds),
                child: const Text(
                  'OMO',
                  style: TextStyle(
                    fontSize: 22,
                    fontWeight: FontWeight.w900,
                    letterSpacing: -0.88,
                    color: Colors.white,
                  ),
                ),
              ),
            ],
          ),
          GestureDetector(
            onTap: () => context.push(Routes.mypage),
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
              child: const Icon(Icons.person_outline, size: 20, color: Color(0xFF9CA3AF)),
            ),
          ),
        ],
      ),
    );
  }
}

// ── 날씨 카드 ────────────────────────────────────────────────────────────────

class _WeatherCard extends StatelessWidget {
  const _WeatherCard();

  @override
  Widget build(BuildContext context) {
    return Container(
      padding: const EdgeInsets.all(24),
      decoration: BoxDecoration(
        gradient: const LinearGradient(
          begin: Alignment.topLeft,
          end: Alignment.bottomRight,
          colors: [Color(0xFF38BDF8), Color(0xFF818CF8)],
        ),
        borderRadius: BorderRadius.circular(24),
      ),
      child: Row(
        crossAxisAlignment: CrossAxisAlignment.center,
        children: [
          Expanded(
            child: Column(
              crossAxisAlignment: CrossAxisAlignment.start,
              children: [
                Text(
                  '$_weatherCity · $_weatherCondition',
                  style: const TextStyle(color: Color(0xCCFFFFFF), fontSize: 14),
                ),
                const SizedBox(height: 4),
                Text(
                  '$_weatherTemp°',
                  style: const TextStyle(
                    color: Colors.white,
                    fontSize: 60,
                    fontWeight: FontWeight.w100,
                    letterSpacing: -2,
                    height: 1.1,
                  ),
                ),
                Text(
                  '체감 $_weatherFeels°',
                  style: const TextStyle(color: Color(0xB3FFFFFF), fontSize: 12),
                ),
                const SizedBox(height: 12),
                Row(
                  children: [
                    _WeatherStat(icon: Icons.water_drop_outlined, label: '$_weatherHumidity%'),
                    const SizedBox(width: 12),
                    _WeatherStat(icon: Icons.air, label: '${_weatherWind}m/s'),
                    const SizedBox(width: 12),
                    _WeatherStat(icon: Icons.visibility_outlined, label: '${_weatherVisibility}km'),
                  ],
                ),
              ],
            ),
          ),
          const _SunIcon(),
        ],
      ),
    );
  }
}

class _WeatherStat extends StatelessWidget {
  const _WeatherStat({required this.icon, required this.label});

  final IconData icon;
  final String label;

  @override
  Widget build(BuildContext context) {
    return Row(
      children: [
        Icon(icon, size: 12, color: const Color(0xCCFFFFFF)),
        const SizedBox(width: 3),
        Text(label, style: const TextStyle(color: Color(0xCCFFFFFF), fontSize: 11)),
      ],
    );
  }
}

class _SunIcon extends StatefulWidget {
  const _SunIcon();

  @override
  State<_SunIcon> createState() => _SunIconState();
}

class _SunIconState extends State<_SunIcon> with SingleTickerProviderStateMixin {
  late final AnimationController _controller;
  late final Animation<double> _angle;

  @override
  void initState() {
    super.initState();
    _controller = AnimationController(
      vsync: this,
      duration: const Duration(seconds: 4),
    )..repeat(reverse: true);
    _angle = Tween<double>(begin: -0.175, end: 0.175).animate(
      CurvedAnimation(parent: _controller, curve: Curves.easeInOut),
    );
  }

  @override
  void dispose() {
    _controller.dispose();
    super.dispose();
  }

  @override
  Widget build(BuildContext context) {
    return AnimatedBuilder(
      animation: _angle,
      builder: (_, child) => Transform.rotate(
        angle: _angle.value,
        child: CustomPaint(
          size: const Size(56, 56),
          painter: _SunPainter(),
        ),
      ),
    );
  }
}

class _SunPainter extends CustomPainter {
  @override
  void paint(Canvas canvas, Size size) {
    final cx = size.width / 2;
    final cy = size.height / 2;

    final corePaint = Paint()..color = const Color(0xFFFBBF24);
    final glowPaint = Paint()..color = const Color(0xFFFDE68A);
    final rayPaint = Paint()
      ..color = const Color(0xFFFBBF24)
      ..strokeWidth = 2.5
      ..strokeCap = StrokeCap.round;

    canvas.drawCircle(Offset(cx, cy), 9, glowPaint);
    canvas.drawCircle(Offset(cx, cy), 7, corePaint);

    for (int i = 0; i < 8; i++) {
      final rad = i * math.pi / 4;
      final inner = 14.0;
      final outer = 18.0;
      canvas.drawLine(
        Offset(cx + inner * math.cos(rad), cy + inner * math.sin(rad)),
        Offset(cx + outer * math.cos(rad), cy + outer * math.sin(rad)),
        rayPaint,
      );
    }
  }

  @override
  bool shouldRepaint(covariant CustomPainter oldDelegate) => false;
}

// ── 코디 추천 섹션 헤더 ──────────────────────────────────────────────────────

class _SectionHeader extends StatelessWidget {
  const _SectionHeader();

  @override
  Widget build(BuildContext context) {
    return Row(
      mainAxisAlignment: MainAxisAlignment.spaceBetween,
      children: [
        Column(
          crossAxisAlignment: CrossAxisAlignment.start,
          children: const [
            Text(
              '오늘의 추천 코디',
              style: TextStyle(fontSize: 16, fontWeight: FontWeight.w700, color: Color(0xFF111827)),
            ),
            SizedBox(height: 2),
            Text(
              '현재 날씨 기준으로 골라드렸어요',
              style: TextStyle(fontSize: 12, color: Color(0xFF9CA3AF)),
            ),
          ],
        ),
        const Icon(Icons.thermostat_outlined, size: 16, color: Color(0xFFD1D5DB)),
      ],
    );
  }
}

// ── 코디 카드 ────────────────────────────────────────────────────────────────

class _OutfitCard extends StatelessWidget {
  const _OutfitCard({required this.outfit});

  final _OutfitItem outfit;

  @override
  Widget build(BuildContext context) {
    return Container(
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
            width: 48,
            height: 48,
            decoration: BoxDecoration(
              color: outfit.bgColor,
              borderRadius: BorderRadius.circular(14),
            ),
            padding: const EdgeInsets.all(10),
            child: _ClothingIcon(category: outfit.category, color: outfit.accentColor),
          ),
          const SizedBox(width: 14),
          Expanded(
            child: Column(
              crossAxisAlignment: CrossAxisAlignment.start,
              children: [
                Text(
                  outfit.category,
                  style: const TextStyle(fontSize: 11, color: Color(0xFF9CA3AF)),
                ),
                const SizedBox(height: 2),
                Text(
                  outfit.item,
                  style: const TextStyle(
                    fontSize: 14,
                    fontWeight: FontWeight.w600,
                    color: Color(0xFF111827),
                  ),
                ),
                const SizedBox(height: 2),
                Text(
                  outfit.reason,
                  style: const TextStyle(fontSize: 11, color: Color(0xFF9CA3AF)),
                  maxLines: 1,
                  overflow: TextOverflow.ellipsis,
                ),
              ],
            ),
          ),
          const Icon(Icons.chevron_right, size: 16, color: Color(0xFFD1D5DB)),
        ],
      ),
    );
  }
}

class _ClothingIcon extends StatelessWidget {
  const _ClothingIcon({required this.category, required this.color});

  final String category;
  final Color color;

  @override
  Widget build(BuildContext context) {
    return CustomPaint(
      painter: _ClothingPainter(category: category, color: color),
    );
  }
}

class _ClothingPainter extends CustomPainter {
  _ClothingPainter({required this.category, required this.color});

  final String category;
  final Color color;

  @override
  void paint(Canvas canvas, Size size) {
    final paint = Paint()
      ..color = color
      ..style = PaintingStyle.fill;

    final s = size.width;

    switch (category) {
      case '상의':
        final path = Path()
          ..moveTo(s * 0.35, s * 0.10)
          ..lineTo(s * 0.15, s * 0.25)
          ..lineTo(s * 0.25, s * 0.35)
          ..lineTo(s * 0.25, s * 0.90)
          ..lineTo(s * 0.75, s * 0.90)
          ..lineTo(s * 0.75, s * 0.35)
          ..lineTo(s * 0.85, s * 0.25)
          ..lineTo(s * 0.65, s * 0.10)
          ..cubicTo(s * 0.65, s * 0.10, s * 0.575, s * 0.20, s * 0.50, s * 0.20)
          ..cubicTo(s * 0.425, s * 0.20, s * 0.35, s * 0.10, s * 0.35, s * 0.10)
          ..close();
        canvas.drawPath(path, paint);
        break;
      case '하의':
        final path = Path()
          ..moveTo(s * 0.15, s * 0.15)
          ..lineTo(s * 0.25, s * 0.90)
          ..lineTo(s * 0.45, s * 0.90)
          ..lineTo(s * 0.50, s * 0.50)
          ..lineTo(s * 0.55, s * 0.90)
          ..lineTo(s * 0.75, s * 0.90)
          ..lineTo(s * 0.85, s * 0.15)
          ..close();
        canvas.drawPath(path, paint);
        break;
      case '아우터':
        final leftPath = Path()
          ..moveTo(s * 0.325, s * 0.075)
          ..lineTo(s * 0.125, s * 0.225)
          ..lineTo(s * 0.20, s * 0.325)
          ..lineTo(s * 0.20, s * 0.925)
          ..lineTo(s * 0.50, s * 0.925)
          ..lineTo(s * 0.50, s * 0.075)
          ..close();
        final rightPath = Path()
          ..moveTo(s * 0.675, s * 0.075)
          ..lineTo(s * 0.875, s * 0.225)
          ..lineTo(s * 0.80, s * 0.325)
          ..lineTo(s * 0.80, s * 0.925)
          ..lineTo(s * 0.50, s * 0.925)
          ..lineTo(s * 0.50, s * 0.075)
          ..close();
        final collarPath = Path()
          ..moveTo(s * 0.325, s * 0.075)
          ..cubicTo(s * 0.375, s * 0.175, s * 0.425, s * 0.225, s * 0.50, s * 0.225)
          ..cubicTo(s * 0.575, s * 0.225, s * 0.625, s * 0.175, s * 0.675, s * 0.075)
          ..close();
        canvas.drawPath(leftPath, paint);
        canvas.drawPath(rightPath, paint);
        canvas.drawPath(collarPath, paint);
        break;
      case '신발':
        final solePaint = Paint()
          ..color = color.withAlpha(128)
          ..style = PaintingStyle.fill;
        final upperPath = Path()
          ..moveTo(s * 0.10, s * 0.70)
          ..cubicTo(s * 0.10, s * 0.70, s * 0.20, s * 0.45, s * 0.40, s * 0.45)
          ..lineTo(s * 0.60, s * 0.45)
          ..lineTo(s * 0.70, s * 0.55)
          ..lineTo(s * 0.90, s * 0.60)
          ..lineTo(s * 0.90, s * 0.75)
          ..lineTo(s * 0.10, s * 0.75)
          ..close();
        final soleRect = RRect.fromRectAndRadius(
          Rect.fromLTWH(s * 0.10, s * 0.70, s * 0.80, s * 0.10),
          const Radius.circular(4),
        );
        canvas.drawPath(upperPath, paint);
        canvas.drawRRect(soleRect, solePaint);
        break;
    }
  }

  @override
  bool shouldRepaint(covariant _ClothingPainter oldDelegate) =>
      oldDelegate.category != category || oldDelegate.color != color;
}

// ── 팁 카드 ──────────────────────────────────────────────────────────────────

class _TipCard extends StatelessWidget {
  const _TipCard();

  @override
  Widget build(BuildContext context) {
    return Container(
      padding: const EdgeInsets.all(16),
      decoration: BoxDecoration(
        color: const Color(0xFFFFF7ED),
        borderRadius: BorderRadius.circular(16),
      ),
      child: Row(
        crossAxisAlignment: CrossAxisAlignment.start,
        children: const [
          Text('💡', style: TextStyle(fontSize: 18)),
          SizedBox(width: 10),
          Expanded(
            child: Column(
              crossAxisAlignment: CrossAxisAlignment.start,
              children: [
                Text(
                  '오늘의 한 마디',
                  style: TextStyle(
                    fontSize: 12,
                    fontWeight: FontWeight.w600,
                    color: Color(0xFFB45309),
                  ),
                ),
                SizedBox(height: 4),
                Text(
                  '오늘 저녁 기온이 15°C까지 내려가요. 가벼운 재킷을 꼭 챙기세요!',
                  style: TextStyle(fontSize: 12, color: Color(0xFFD97706), height: 1.5),
                ),
              ],
            ),
          ),
        ],
      ),
    );
  }
}
