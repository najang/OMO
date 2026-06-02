import 'dart:io';

abstract class ApiConstants {
  static String get baseUrl {
    // 로컬 테스트용 — 배포 전 https://api.omo.com 으로 교체
    if (Platform.isAndroid) return 'http://10.0.2.2:8080';
    return 'http://localhost:8080';
  }

  static const Duration connectTimeout = Duration(seconds: 30);
  static const Duration receiveTimeout = Duration(seconds: 30);
}
