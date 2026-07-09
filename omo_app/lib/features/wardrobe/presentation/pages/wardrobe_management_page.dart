import 'package:flutter/material.dart';
import 'package:flutter_riverpod/flutter_riverpod.dart';
import 'package:flutter_svg/flutter_svg.dart';
import 'package:go_router/go_router.dart';

import '../../data/repositories/wardrobe_repository_impl.dart';
import '../../domain/entities/clothing_display_group.dart';
import '../../domain/entities/clothing_item_catalog.dart';
import '../providers/clothing_catalog_provider.dart';

class WardrobeManagementPage extends ConsumerStatefulWidget {
  const WardrobeManagementPage({super.key});

  @override
  ConsumerState<WardrobeManagementPage> createState() => _WardrobeManagementPageState();
}

class _WardrobeManagementPageState extends ConsumerState<WardrobeManagementPage>
    with SingleTickerProviderStateMixin {
  Set<String> _selectedIds = {};
  bool _isLoadingData = true;
  bool _isSaving = false;
  late final AnimationController _fadeController;
  late final Animation<double> _fadeAnimation;

  @override
  void initState() {
    super.initState();
    _fadeController = AnimationController(
      vsync: this,
      duration: const Duration(milliseconds: 400),
    );
    _fadeAnimation = CurvedAnimation(parent: _fadeController, curve: Curves.easeOut);
    _loadWardrobe();
  }

  @override
  void dispose() {
    _fadeController.dispose();
    super.dispose();
  }

  Future<void> _loadWardrobe() async {
    try {
      final items = await ref.read(wardrobeRepositoryProvider).getWardrobe();
      if (mounted) {
        setState(() {
          _selectedIds = Set.from(items);
          _isLoadingData = false;
        });
        _fadeController.forward();
      }
    } catch (_) {
      if (mounted) {
        setState(() => _isLoadingData = false);
        _fadeController.forward();
      }
    }
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

  Future<void> _save() async {
    if (_isSaving) return;
    if (_selectedIds.isEmpty) {
      ScaffoldMessenger.of(context).showSnackBar(
        const SnackBar(content: Text('최소 1개 이상의 아이템을 선택해주세요')),
      );
      return;
    }
    setState(() => _isSaving = true);
    try {
      await ref.read(wardrobeRepositoryProvider).setupWardrobe(_selectedIds.toList());
      if (mounted) context.pop();
    } catch (e) {
      if (mounted) {
        ScaffoldMessenger.of(context).showSnackBar(
          SnackBar(content: Text('저장에 실패했습니다: $e')),
        );
      }
    } finally {
      if (mounted) setState(() => _isSaving = false);
    }
  }

  @override
  Widget build(BuildContext context) {
    final catalogAsync = ref.watch(clothingItemCatalogProvider);

    return Scaffold(
      backgroundColor: const Color(0xFFF9FAFB),
      body: SafeArea(
        child: Column(
          children: [
            _buildHeader(context),
            Expanded(
              child: catalogAsync.when(
                loading: () => const Center(child: CircularProgressIndicator()),
                error: (_, _) => _buildError(),
                data: (catalog) {
                  if (_isLoadingData) {
                    return const Center(child: CircularProgressIndicator());
                  }
                  return _buildContent(catalog);
                },
              ),
            ),
            _buildSaveButton(),
          ],
        ),
      ),
    );
  }

  Widget _buildContent(List<ClothingItemCatalog> catalog) {
    final groups = groupByDisplayGroup(catalog);

    return FadeTransition(
      opacity: _fadeAnimation,
      child: CustomScrollView(
        slivers: [
          SliverToBoxAdapter(
            child: Padding(
              padding: const EdgeInsets.fromLTRB(20, 4, 20, 16),
              child: Text(
                '보유하신 의류를 관리하세요. 선택된 의류를 기반으로 날씨에 맞는 옷차림을 추천해드려요.',
                style: const TextStyle(fontSize: 13, color: Color(0xFF6B7280)),
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
      ),
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
            '옷장 관리',
            style: TextStyle(fontSize: 18, fontWeight: FontWeight.w700, color: Color(0xFF111827)),
          ),
        ],
      ),
    );
  }

  Widget _buildSaveButton() {
    final canSave = _selectedIds.isNotEmpty;

    return Padding(
      padding: const EdgeInsets.fromLTRB(20, 12, 20, 24),
      child: GestureDetector(
        onTap: _isSaving ? null : _save,
        child: Container(
          width: double.infinity,
          height: 52,
          decoration: BoxDecoration(
            gradient: canSave
                ? const LinearGradient(
                    begin: Alignment.topLeft,
                    end: Alignment.bottomRight,
                    colors: [Color(0xFF38BDF8), Color(0xFF818CF8), Color(0xFFF472B6)],
                    stops: [0.0, 0.5, 1.0],
                  )
                : null,
            color: canSave ? null : const Color(0xFFE5E7EB),
            borderRadius: BorderRadius.circular(16),
          ),
          child: Center(
            child: _isSaving
                ? const SizedBox(
                    width: 22,
                    height: 22,
                    child: CircularProgressIndicator(color: Colors.white, strokeWidth: 2),
                  )
                : Text(
                    '저장',
                    style: TextStyle(
                      color: canSave ? Colors.white : const Color(0xFF9CA3AF),
                      fontSize: 16,
                      fontWeight: FontWeight.w700,
                    ),
                  ),
          ),
        ),
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
    final selectedCount = items.where((i) => selectedIds.contains(i.systemKey)).length;

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
              const Spacer(),
              Text(
                '$selectedCount/${items.length}',
                style: const TextStyle(fontSize: 12, color: Color(0xFF9CA3AF)),
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
          color: isSelected ? selectedColor : Colors.white,
          borderRadius: BorderRadius.circular(20),
          border: isSelected ? null : Border.all(color: const Color(0xFFE5E7EB)),
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
