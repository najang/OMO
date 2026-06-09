import '../entities/temp_sensitivity.dart';

abstract interface class WardrobeRepository {
  Future<void> setupWardrobe(List<String> itemKeys);
  Future<void> initTempProfile(TempSensitivity tempSensitivity);
}
