import 'package:freezed_annotation/freezed_annotation.dart';

import '../../domain/entities/auth_token.dart';

part 'auth_token_model.freezed.dart';
part 'auth_token_model.g.dart';

@freezed
sealed class AuthTokenModel with _$AuthTokenModel {
  const factory AuthTokenModel({
    required String accessToken,
    required String refreshToken,
    required int userId,
    required bool isNewUser,
  }) = _AuthTokenModel;

  factory AuthTokenModel.fromJson(Map<String, dynamic> json) =>
      _$AuthTokenModelFromJson(json);
}

extension AuthTokenModelMapper on AuthTokenModel {
  AuthToken toEntity() => AuthToken(
        accessToken: accessToken,
        refreshToken: refreshToken,
        userId: userId,
        isNewUser: isNewUser,
      );
}
