package com.omo.application.auth;

public record AuthInfo(String accessToken, String refreshToken, Long userId, boolean isNewUser) {}
