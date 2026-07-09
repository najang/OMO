import '../entities/clothing_item_catalog.dart';
import '../entities/temp_sensitivity.dart';

abstract interface class WardrobeRepository {
  Future<List<ClothingItemCatalog>> getClothingItemCatalog();
  Future<List<String>> getWardrobe();
  Future<void> setupWardrobe(List<String> itemKeys);
  Future<void> initTempProfile(TempSensitivity tempSensitivity);
}
