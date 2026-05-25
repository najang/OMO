extension DateTimeExtensions on DateTime {
  String get toDisplayDate => '$year-${month.toString().padLeft(2, '0')}-${day.toString().padLeft(2, '0')}';

  bool get isToday {
    final now = DateTime.now();
    return year == now.year && month == now.month && day == now.day;
  }
}
