import 'package:flutter/material.dart';

import '../../../../../core/constants/app_constants.dart';

class HomePage extends StatelessWidget {
  const HomePage({super.key});

  @override
  Widget build(BuildContext context) {
    return Scaffold(
      appBar: AppBar(title: const Text(AppConstants.appName)),
      body: const Center(
        child: Column(
          mainAxisAlignment: MainAxisAlignment.center,
          children: [
            Icon(Icons.home_outlined, size: 64),
            SizedBox(height: 16),
            Text('OMO', style: TextStyle(fontSize: 24, fontWeight: FontWeight.bold)),
            SizedBox(height: 8),
            Text('v${AppConstants.appVersion}', style: TextStyle(color: Colors.grey)),
          ],
        ),
      ),
    );
  }
}
