import 'package:flutter/material.dart';

class OnboardingProgressBar extends StatelessWidget {
  const OnboardingProgressBar({super.key, required this.step});

  /// 채워진 진행 바 수 (1~3)
  final int step;

  @override
  Widget build(BuildContext context) {
    return Padding(
      padding: const EdgeInsets.fromLTRB(20, 24, 20, 16),
      child: Row(
        children: List.generate(3, (i) {
          final filled = i < step;
          return Expanded(
            child: Container(
              height: 4,
              margin: EdgeInsets.only(right: i < 2 ? 6 : 0),
              decoration: BoxDecoration(
                borderRadius: BorderRadius.circular(4),
                gradient: filled ? LinearGradient(colors: _gradientFor(i)) : null,
                color: filled ? null : const Color(0xFFF3F4F6),
              ),
            ),
          );
        }),
      ),
    );
  }

  List<Color> _gradientFor(int index) {
    if (index == 0) return [const Color(0xFF38BDF8), const Color(0xFF818CF8)];
    if (index == 1) return [const Color(0xFF38BDF8), const Color(0xFFF472B6)];
    return [const Color(0xFF818CF8), const Color(0xFFF472B6)];
  }
}

class OnboardingNextButton extends StatelessWidget {
  const OnboardingNextButton({
    super.key,
    required this.label,
    required this.enabled,
    required this.isLoading,
    required this.onTap,
  });

  final String label;
  final bool enabled;
  final bool isLoading;
  final VoidCallback onTap;

  @override
  Widget build(BuildContext context) {
    return Padding(
      padding: const EdgeInsets.fromLTRB(20, 8, 20, 24),
      child: AnimatedContainer(
        duration: const Duration(milliseconds: 200),
        width: double.infinity,
        height: 56,
        decoration: BoxDecoration(
          borderRadius: BorderRadius.circular(16),
          gradient: enabled
              ? const LinearGradient(
                  colors: [Color(0xFF38BDF8), Color(0xFF818CF8), Color(0xFFF472B6)],
                )
              : null,
          color: enabled ? null : const Color(0xFFD1D5DB),
        ),
        child: Material(
          color: Colors.transparent,
          child: InkWell(
            onTap: enabled ? onTap : null,
            borderRadius: BorderRadius.circular(16),
            child: Center(
              child: isLoading
                  ? const SizedBox(
                      width: 24,
                      height: 24,
                      child: CircularProgressIndicator(strokeWidth: 2, color: Colors.white),
                    )
                  : Text(
                      label,
                      style: TextStyle(
                        fontSize: 17,
                        fontWeight: FontWeight.w700,
                        color: enabled ? Colors.white : const Color(0xFF9CA3AF),
                      ),
                    ),
            ),
          ),
        ),
      ),
    );
  }
}
