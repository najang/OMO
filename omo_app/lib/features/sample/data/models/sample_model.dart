import 'package:freezed_annotation/freezed_annotation.dart';

import '../../domain/entities/sample_entity.dart';

part 'sample_model.freezed.dart';
part 'sample_model.g.dart';

@freezed
sealed class SampleModel with _$SampleModel {
  const factory SampleModel({
    required int id,
    required String title,
    required String body,
  }) = _SampleModel;

  factory SampleModel.fromJson(Map<String, dynamic> json) =>
      _$SampleModelFromJson(json);
}

extension SampleModelMapper on SampleModel {
  SampleEntity toEntity() => SampleEntity(id: id, title: title, body: body);
}
