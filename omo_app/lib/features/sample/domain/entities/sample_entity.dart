import 'package:freezed_annotation/freezed_annotation.dart';

part 'sample_entity.freezed.dart';

@freezed
sealed class SampleEntity with _$SampleEntity {
  const factory SampleEntity({
    required int id,
    required String title,
    required String body,
  }) = _SampleEntity;
}
