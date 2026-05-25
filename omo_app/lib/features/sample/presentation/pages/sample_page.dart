import 'package:flutter/material.dart';
import 'package:flutter_riverpod/flutter_riverpod.dart';

import '../providers/sample_provider.dart';
import '../widgets/sample_list_item.dart';

class SamplePage extends ConsumerWidget {
  const SamplePage({super.key});

  @override
  Widget build(BuildContext context, WidgetRef ref) {
    final samplesAsync = ref.watch(samplesProvider);

    return Scaffold(
      appBar: AppBar(title: const Text('Sample')),
      body: samplesAsync.when(
        data: (samples) => ListView.separated(
          padding: const EdgeInsets.all(16),
          itemCount: samples.length,
          separatorBuilder: (_, _) => const SizedBox(height: 8),
          itemBuilder: (context, index) =>
              SampleListItem(entity: samples[index]),
        ),
        loading: () => const Center(child: CircularProgressIndicator()),
        error: (e, _) => Center(child: Text('Error: $e')),
      ),
    );
  }
}
