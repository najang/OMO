import 'package:flutter/material.dart';
import 'package:flutter_riverpod/flutter_riverpod.dart';
import 'package:flutter_svg/flutter_svg.dart';

import '../../data/repositories/wardrobe_repository_impl.dart';
import '../../domain/entities/clothing_category.dart';
import '../widgets/onboarding_widgets.dart';
import 'temp_sensitivity_page.dart';

class _ClothingItem {
  const _ClothingItem({required this.id, required this.label, required this.category});
  final String id;
  final String label;
  final ClothingCategory category;
}

class _CategoryData {
  const _CategoryData({
    required this.label,
    required this.iconAsset,
    required this.color,
    required this.bgColor,
    required this.items,
  });
  final String label;
  final String iconAsset;
  final Color color;
  final Color bgColor;
  final List<_ClothingItem> items;
}

const _categories = [
  _CategoryData(
    label: '상의',
    iconAsset: 'assets/icons/clothes/shirt.svg',
    color: Color(0xFF93C5FD),
    bgColor: Color(0xFFEFF6FF),
    items: [
      _ClothingItem(id: 'short-tee', label: '반팔 티셔츠', category: ClothingCategory.top),
      _ClothingItem(id: 'long-tee', label: '긴팔 티셔츠', category: ClothingCategory.top),
      _ClothingItem(id: 'shirt', label: '셔츠', category: ClothingCategory.top),
      _ClothingItem(id: 'blouse', label: '블라우스', category: ClothingCategory.top),
      _ClothingItem(id: 'sweatshirt', label: '맨투맨', category: ClothingCategory.top),
      _ClothingItem(id: 'hoodie', label: '후드티', category: ClothingCategory.top),
      _ClothingItem(id: 'knit', label: '니트', category: ClothingCategory.top),
      _ClothingItem(id: 'cardigan', label: '가디건', category: ClothingCategory.top),
      _ClothingItem(id: 'sleeveless', label: '민소매/나시', category: ClothingCategory.top),
      _ClothingItem(id: 'turtleneck', label: '폴라티', category: ClothingCategory.top),
    ],
  ),
  _CategoryData(
    label: '하의',
    iconAsset: 'assets/icons/clothes/pants.svg',
    color: Color(0xFFC4B5FD),
    bgColor: Color(0xFFF5F3FF),
    items: [
      _ClothingItem(id: 'shorts', label: '반바지', category: ClothingCategory.pants),
      _ClothingItem(id: 'jeans', label: '청바지', category: ClothingCategory.pants),
      _ClothingItem(id: 'slacks', label: '슬랙스', category: ClothingCategory.pants),
      _ClothingItem(id: 'cotton-pants', label: '면바지', category: ClothingCategory.pants),
      _ClothingItem(id: 'skirt', label: '치마', category: ClothingCategory.skirt),
      _ClothingItem(id: 'leggings', label: '레깅스', category: ClothingCategory.pants),
    ],
  ),
  _CategoryData(
    label: '아우터',
    iconAsset: 'assets/icons/clothes/jacket.svg',
    color: Color(0xFF6EE7B7),
    bgColor: Color(0xFFF0FDF4),
    items: [
      _ClothingItem(id: 'windbreaker', label: '바람막이', category: ClothingCategory.outer),
      _ClothingItem(id: 'denim-jacket', label: '청자켓', category: ClothingCategory.outer),
      _ClothingItem(id: 'leather-jacket', label: '가죽자켓', category: ClothingCategory.outer),
      _ClothingItem(id: 'trench-coat', label: '트렌치코트', category: ClothingCategory.outer),
      _ClothingItem(id: 'padding', label: '패딩', category: ClothingCategory.outer),
      _ClothingItem(id: 'long-coat', label: '롱코트', category: ClothingCategory.outer),
      _ClothingItem(id: 'parka', label: '파카', category: ClothingCategory.outer),
      _ClothingItem(id: 'raincoat', label: '레인코트', category: ClothingCategory.outer),
      _ClothingItem(id: 'blazer', label: '블레이저', category: ClothingCategory.outer),
    ],
  ),
  _CategoryData(
    label: '원피스',
    iconAsset: 'assets/icons/clothes/dress.svg',
    color: Color(0xFFFCD34D),
    bgColor: Color(0xFFFFFBEB),
    items: [
      _ClothingItem(id: 'mini-dress', label: '미니 원피스', category: ClothingCategory.dress),
      _ClothingItem(id: 'midi-dress', label: '미디 원피스', category: ClothingCategory.dress),
      _ClothingItem(id: 'long-dress', label: '롱 원피스', category: ClothingCategory.dress),
      _ClothingItem(id: 'shirt-dress', label: '셔츠 원피스', category: ClothingCategory.dress),
      _ClothingItem(id: 'jumpsuit', label: '점프수트', category: ClothingCategory.dress),
    ],
  ),
  _CategoryData(
    label: '신발',
    iconAsset: 'assets/icons/clothes/sneaker.svg',
    color: Color(0xFF86EFAC),
    bgColor: Color(0xFFF0FDF4),
    items: [
      _ClothingItem(id: 'sneakers', label: '운동화', category: ClothingCategory.shoes),
      _ClothingItem(id: 'loafers', label: '로퍼', category: ClothingCategory.shoes),
      _ClothingItem(id: 'boots', label: '부츠', category: ClothingCategory.shoes),
      _ClothingItem(id: 'sandals', label: '샌들', category: ClothingCategory.shoes),
      _ClothingItem(id: 'slippers', label: '슬리퍼', category: ClothingCategory.shoes),
      _ClothingItem(id: 'rain-boots', label: '장화', category: ClothingCategory.shoes),
      _ClothingItem(id: 'heels', label: '힐/구두', category: ClothingCategory.shoes),
    ],
  ),
  _CategoryData(
    label: '모자',
    iconAsset: 'assets/icons/clothes/hat.svg',
    color: Color(0xFFFCA5A5),
    bgColor: Color(0xFFFEF2F2),
    items: [
      _ClothingItem(id: 'cap', label: '볼캡', category: ClothingCategory.accessory),
      _ClothingItem(id: 'bucket-hat', label: '버킷햇', category: ClothingCategory.accessory),
      _ClothingItem(id: 'beanie', label: '비니', category: ClothingCategory.accessory),
      _ClothingItem(id: 'beret', label: '베레모', category: ClothingCategory.accessory),
      _ClothingItem(id: 'sun-hat', label: '썬햇', category: ClothingCategory.accessory),
    ],
  ),
  _CategoryData(
    label: '스카프/목도리',
    iconAsset: 'assets/icons/clothes/scarf.svg',
    color: Color(0xFFF9A8D4),
    bgColor: Color(0xFFFDF2F8),
    items: [
      _ClothingItem(id: 'scarf', label: '스카프', category: ClothingCategory.accessory),
      _ClothingItem(id: 'muffler', label: '머플러', category: ClothingCategory.accessory),
      _ClothingItem(id: 'neck-warmer', label: '넥워머', category: ClothingCategory.accessory),
      _ClothingItem(id: 'shawl', label: '숄', category: ClothingCategory.accessory),
    ],
  ),
];

class WardrobeSetupPage extends ConsumerStatefulWidget {
  const WardrobeSetupPage({super.key});

  @override
  ConsumerState<WardrobeSetupPage> createState() => _WardrobeSetupPageState();
}

class _WardrobeSetupPageState extends ConsumerState<WardrobeSetupPage>
    with SingleTickerProviderStateMixin {
  final _selectedIds = <String>{};
  bool _isLoading = false;
  late final AnimationController _fadeController;
  late final Animation<double> _fadeAnimation;

  @override
  void initState() {
    super.initState();
    _fadeController = AnimationController(
      vsync: this,
      duration: const Duration(milliseconds: 400),
    )..forward();
    _fadeAnimation = CurvedAnimation(parent: _fadeController, curve: Curves.easeOut);
  }

  @override
  void dispose() {
    _fadeController.dispose();
    super.dispose();
  }

  void _toggleItem(String id) {
    setState(() {
      if (_selectedIds.contains(id)) {
        _selectedIds.remove(id);
      } else {
        _selectedIds.add(id);
      }
    });
  }

  Future<void> _submit() async {
    final itemKeys = _selectedIds.toList();
    if (itemKeys.isEmpty || _isLoading) return;
    setState(() => _isLoading = true);
    try {
      await ref.read(wardrobeRepositoryProvider).setupWardrobe(itemKeys);
      if (mounted) {
        Navigator.of(context).push(
          MaterialPageRoute<void>(builder: (_) => const TempSensitivityPage()),
        );
      }
    } catch (e) {
      if (mounted) {
        ScaffoldMessenger.of(context).showSnackBar(
          SnackBar(content: Text('저장에 실패했습니다: $e')),
        );
      }
    } finally {
      if (mounted) setState(() => _isLoading = false);
    }
  }

  @override
  Widget build(BuildContext context) {
    final canProceed = _selectedIds.isNotEmpty;

    return Scaffold(
      backgroundColor: Colors.white,
      body: SafeArea(
        child: Column(
          children: [
            const OnboardingProgressBar(step: 2),
            Expanded(
              child: FadeTransition(
                opacity: _fadeAnimation,
                child: CustomScrollView(
                  slivers: [
                    SliverToBoxAdapter(
                      child: Padding(
                        padding: const EdgeInsets.fromLTRB(20, 4, 20, 16),
                        child: Column(
                          crossAxisAlignment: CrossAxisAlignment.start,
                          children: const [
                            Text(
                              '어떤 옷을 가지고 계신가요?',
                              style: TextStyle(
                                fontSize: 24,
                                fontWeight: FontWeight.w800,
                                color: Color(0xFF111827),
                              ),
                            ),
                            SizedBox(height: 6),
                            Text(
                              '보유하신 의류를 모두 선택해주세요',
                              style: TextStyle(fontSize: 14, color: Color(0xFF6B7280)),
                            ),
                          ],
                        ),
                      ),
                    ),
                    SliverList(
                      delegate: SliverChildBuilderDelegate(
                        (context, index) => _CategorySection(
                          category: _categories[index],
                          selectedIds: _selectedIds,
                          onToggle: _toggleItem,
                        ),
                        childCount: _categories.length,
                      ),
                    ),
                    const SliverToBoxAdapter(child: SizedBox(height: 8)),
                  ],
                ),
              ),
            ),
            OnboardingNextButton(
              label: '다음',
              enabled: canProceed,
              isLoading: _isLoading,
              onTap: _submit,
            ),
          ],
        ),
      ),
    );
  }
}

class _CategorySection extends StatelessWidget {
  const _CategorySection({
    required this.category,
    required this.selectedIds,
    required this.onToggle,
  });

  final _CategoryData category;
  final Set<String> selectedIds;
  final ValueChanged<String> onToggle;

  @override
  Widget build(BuildContext context) {
    return Padding(
      padding: const EdgeInsets.fromLTRB(20, 0, 20, 20),
      child: Column(
        crossAxisAlignment: CrossAxisAlignment.start,
        children: [
          Row(
            children: [
              Container(
                width: 32,
                height: 32,
                decoration: BoxDecoration(
                  color: category.bgColor,
                  borderRadius: BorderRadius.circular(10),
                ),
                padding: const EdgeInsets.all(6),
                child: SvgPicture.asset(
                  category.iconAsset,
                  colorFilter: ColorFilter.mode(category.color, BlendMode.srcIn),
                ),
              ),
              const SizedBox(width: 8),
              Text(
                category.label,
                style: const TextStyle(
                  fontSize: 14,
                  fontWeight: FontWeight.w700,
                  color: Color(0xFF111827),
                ),
              ),
            ],
          ),
          const SizedBox(height: 10),
          Wrap(
            spacing: 8,
            runSpacing: 8,
            children: category.items.map((item) {
              final isSelected = selectedIds.contains(item.id);
              return _ItemChip(
                label: item.label,
                isSelected: isSelected,
                selectedColor: category.color,
                onTap: () => onToggle(item.id),
              );
            }).toList(),
          ),
        ],
      ),
    );
  }
}

class _ItemChip extends StatelessWidget {
  const _ItemChip({
    required this.label,
    required this.isSelected,
    required this.selectedColor,
    required this.onTap,
  });

  final String label;
  final bool isSelected;
  final Color selectedColor;
  final VoidCallback onTap;

  @override
  Widget build(BuildContext context) {
    return GestureDetector(
      onTap: onTap,
      child: AnimatedContainer(
        duration: const Duration(milliseconds: 180),
        padding: const EdgeInsets.symmetric(horizontal: 12, vertical: 6),
        decoration: BoxDecoration(
          color: isSelected ? selectedColor : const Color(0xFFF3F4F6),
          borderRadius: BorderRadius.circular(20),
        ),
        child: Text(
          label,
          style: TextStyle(
            fontSize: 13,
            fontWeight: isSelected ? FontWeight.w600 : FontWeight.w500,
            color: isSelected ? Colors.white : const Color(0xFF6B7280),
          ),
        ),
      ),
    );
  }
}
