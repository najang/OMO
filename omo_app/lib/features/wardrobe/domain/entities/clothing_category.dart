enum ClothingCategory {
  outer,
  top,
  pants,
  skirt,
  dress,
  shoes,
  accessory;

  String get apiValue => name.toUpperCase();
}
