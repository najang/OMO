import 'package:flutter/material.dart';

import '../../domain/entities/sample_entity.dart';

class SampleListItem extends StatelessWidget {
  final SampleEntity entity;

  const SampleListItem({super.key, required this.entity});

  @override
  Widget build(BuildContext context) {
    return Card(
      child: Padding(
        padding: const EdgeInsets.all(16),
        child: Column(
          crossAxisAlignment: CrossAxisAlignment.start,
          children: [
            Text(entity.title, style: Theme.of(context).textTheme.bodyLarge),
            const SizedBox(height: 4),
            Text(entity.body, style: Theme.of(context).textTheme.bodyMedium),
          ],
        ),
      ),
    );
  }
}
