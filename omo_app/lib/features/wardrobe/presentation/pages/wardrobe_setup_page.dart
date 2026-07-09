import 'package:flutter/material.dart';
import 'package:flutter_riverpod/flutter_riverpod.dart';
import 'package:flutter_svg/flutter_svg.dart';

import '../../data/repositories/wardrobe_repository_impl.dart';
import '../../domain/entities/clothing_display_group.dart';
import '../../domain/entities/clothing_item_catalog.dart';
import '../providers/clothing_catalog_provider.dart';
import '../widgets/onboarding_widgets.dart';
import 'temp_sensitivity_page.dart';

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
    final catalogAsync = ref.watch(clothingItemCatalogProvider);

    return Scaffold(
      backgroundColor: Colors.white,
      body: SafeArea(
        child: Column(
          children: [
            const OnboardingProgressBar(step: 2),
            Expanded(
              child: FadeTransition(
                opacity: _fadeAnimation,
                child: catalogAsync.when(
                  loading: () => const Center(child: CircularProgressIndicator()),
                  error: (_, _) => _buildError(),
                  data: _buildList,
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

  Widget _buildList(List<ClothingItemCatalog> catalog) {
    final groups = groupByDisplayGroup(catalog);

    return CustomScrollView(
      slivers: [
        const SliverToBoxAdapter(
          child: Padding(
            padding: EdgeInsets.fromLTRB(20, 4, 20, 16),
            child: Column(
              crossAxisAlignment: CrossAxisAlignment.start,
              children: [
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
              group: groups[index].key,
              items: groups[index].value,
              selectedIds: _selectedIds,
              onToggle: _toggleItem,
            ),
            childCount: groups.length,
          ),
        ),
        const SliverToBoxAdapter(child: SizedBox(height: 8)),
      ],
    );
  }

  Widget _buildError() {
    return Center(
      child: Column(
        mainAxisSize: MainAxisSize.min,
        children: [
          const Text(
            '의류 목록을 불러오지 못했어요.',
            style: TextStyle(fontSize: 14, color: Color(0xFF6B7280)),
          ),
          const SizedBox(height: 12),
          TextButton(
            onPressed: () => ref.invalidate(clothingItemCatalogProvider),
            child: const Text('다시 시도'),
          ),
        ],
      ),
    );
  }
}

class _CategorySection extends StatelessWidget {
  const _CategorySection({
    required this.group,
    required this.items,
    required this.selectedIds,
    required this.onToggle,
  });

  final ClothingDisplayGroup group;
  final List<ClothingItemCatalog> items;
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
                  color: group.bgColor,
                  borderRadius: BorderRadius.circular(10),
                ),
                padding: const EdgeInsets.all(6),
                child: SvgPicture.asset(
                  group.iconAsset,
                  colorFilter: ColorFilter.mode(group.color, BlendMode.srcIn),
                ),
              ),
              const SizedBox(width: 8),
              Text(
                group.label,
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
            children: items.map((item) {
              final isSelected = selectedIds.contains(item.systemKey);
              return _ItemChip(
                label: item.nameKo,
                isSelected: isSelected,
                selectedColor: group.color,
                onTap: () => onToggle(item.systemKey),
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
