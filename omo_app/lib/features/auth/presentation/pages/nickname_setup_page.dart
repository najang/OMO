import 'package:flutter/material.dart';
import 'package:flutter_riverpod/flutter_riverpod.dart';

import '../providers/auth_provider.dart';

class NicknameSetupPage extends ConsumerStatefulWidget {
  const NicknameSetupPage({super.key});

  @override
  ConsumerState<NicknameSetupPage> createState() => _NicknameSetupPageState();
}

class _NicknameSetupPageState extends ConsumerState<NicknameSetupPage> {
  final _controller = TextEditingController();
  final _focusNode = FocusNode();
  bool _isLoading = false;

  static const _minLength = 2;
  static const _maxLength = 6;
  static final _validPattern = RegExp(r'^[가-힣a-zA-Z]+$');

  @override
  void dispose() {
    _controller.dispose();
    _focusNode.dispose();
    super.dispose();
  }

  String? _errorText(String text) {
    if (text.isEmpty) return null;
    if (!_validPattern.hasMatch(text)) return '한글 또는 영어만 입력 가능해요';
    if (text.length < _minLength) return '$_minLength자 이상 입력해주세요';
    return null;
  }

  bool get _isValid {
    final text = _controller.text;
    return text.length >= _minLength &&
        text.length <= _maxLength &&
        _validPattern.hasMatch(text);
  }

  Future<void> _submit() async {
    if (!_isValid || _isLoading) return;
    setState(() => _isLoading = true);
    try {
      // TODO: API 호출로 닉네임 저장
      ref.read(authNotifierProvider.notifier).completeOnboarding();
    } finally {
      if (mounted) setState(() => _isLoading = false);
    }
  }

  @override
  Widget build(BuildContext context) {
    return Scaffold(
      backgroundColor: Colors.white,
      body: SafeArea(
        child: Padding(
          padding: const EdgeInsets.symmetric(horizontal: 32),
          child: Column(
            crossAxisAlignment: CrossAxisAlignment.start,
            children: [
              const SizedBox(height: 64),
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
                    fontSize: 28,
                    fontWeight: FontWeight.w900,
                    letterSpacing: -1.12,
                    color: Colors.white,
                  ),
                ),
              ),
              const SizedBox(height: 16),
              const Text(
                '닉네임을 설정해주세요',
                style: TextStyle(
                  fontSize: 24,
                  fontWeight: FontWeight.w700,
                  color: Color(0xFF111827),
                  letterSpacing: -0.5,
                ),
              ),
              const SizedBox(height: 8),
              const Text(
                '한글 또는 영어, $_minLength~$_maxLength자',
                style: TextStyle(fontSize: 14, color: Color(0xFF9CA3AF)),
              ),
              const SizedBox(height: 40),
              ValueListenableBuilder(
                valueListenable: _controller,
                builder: (context, value, _) {
                  final error = _errorText(value.text);
                  return TextField(
                    controller: _controller,
                    focusNode: _focusNode,
                    maxLength: _maxLength,
                    autofocus: true,
                    textInputAction: TextInputAction.done,
                    onSubmitted: (_) => _submit(),
                    style: const TextStyle(
                      fontSize: 18,
                      fontWeight: FontWeight.w500,
                      color: Color(0xFF111827),
                    ),
                    decoration: InputDecoration(
                      hintText: '닉네임 입력',
                      hintStyle: const TextStyle(color: Color(0xFFD1D5DB)),
                      counterText: '',
                      errorText: error,
                      errorStyle: const TextStyle(fontSize: 12, color: Color(0xFFEF4444)),
                      enabledBorder: UnderlineInputBorder(
                        borderSide: BorderSide(
                          color: error != null ? const Color(0xFFEF4444) : const Color(0xFFE5E7EB),
                          width: 1.5,
                        ),
                      ),
                      focusedBorder: UnderlineInputBorder(
                        borderSide: BorderSide(
                          color: error != null ? const Color(0xFFEF4444) : const Color(0xFF818CF8),
                          width: 2,
                        ),
                      ),
                      errorBorder: const UnderlineInputBorder(
                        borderSide: BorderSide(color: Color(0xFFEF4444), width: 1.5),
                      ),
                      focusedErrorBorder: const UnderlineInputBorder(
                        borderSide: BorderSide(color: Color(0xFFEF4444), width: 2),
                      ),
                      suffixText: '${value.text.length}/$_maxLength',
                      suffixStyle: const TextStyle(
                        fontSize: 13,
                        color: Color(0xFF9CA3AF),
                      ),
                    ),
                  );
                },
              ),
              const Spacer(),
              ValueListenableBuilder(
                valueListenable: _controller,
                builder: (context, value, _) {
                  final valid = _isValid;
                  return AnimatedContainer(
                    duration: const Duration(milliseconds: 200),
                    width: double.infinity,
                    height: 56,
                    decoration: BoxDecoration(
                      borderRadius: BorderRadius.circular(16),
                      gradient: valid
                          ? const LinearGradient(
                              colors: [Color(0xFF38BDF8), Color(0xFF818CF8), Color(0xFFF472B6)],
                            )
                          : null,
                      color: valid ? null : const Color(0xFFF3F4F6),
                    ),
                    child: Material(
                      color: Colors.transparent,
                      child: InkWell(
                        onTap: valid ? _submit : null,
                        borderRadius: BorderRadius.circular(16),
                        child: Center(
                          child: _isLoading
                              ? const SizedBox(
                                  width: 24,
                                  height: 24,
                                  child: CircularProgressIndicator(
                                    strokeWidth: 2,
                                    color: Colors.white,
                                  ),
                                )
                              : Text(
                                  '시작하기',
                                  style: TextStyle(
                                    fontSize: 17,
                                    fontWeight: FontWeight.w700,
                                    color: valid ? Colors.white : const Color(0xFFD1D5DB),
                                  ),
                                ),
                        ),
                      ),
                    ),
                  );
                },
              ),
              const SizedBox(height: 32),
            ],
          ),
        ),
      ),
    );
  }
}
