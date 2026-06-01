class AuthToken {
  const AuthToken({
    required this.accessToken,
    required this.refreshToken,
    required this.userId,
    required this.isNewUser,
  });

  final String accessToken;
  final String refreshToken;
  final int userId;
  final bool isNewUser;
}
