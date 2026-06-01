package com.omo.interfaces.api.auth;

import com.omo.infrastructure.auth.jwt.JwtProvider;
import com.omo.support.error.CoreException;
import com.omo.support.error.ErrorType;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.JwtException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

@RequiredArgsConstructor
@Component
public class AuthInterceptor implements HandlerInterceptor {

    static final String USER_ID_ATTRIBUTE = "userId";
    private static final String AUTHORIZATION_HEADER = "Authorization";
    private static final String BEARER_PREFIX = "Bearer ";

    private final JwtProvider jwtProvider;

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) {
        String authHeader = request.getHeader(AUTHORIZATION_HEADER);
        if (authHeader == null || !authHeader.startsWith(BEARER_PREFIX)) {
            throw new CoreException(ErrorType.UNAUTHENTICATED, "인증 토큰이 필요합니다.");
        }

        String token = authHeader.substring(BEARER_PREFIX.length());
        try {
            Long userId = jwtProvider.parseUserId(token);
            request.setAttribute(USER_ID_ATTRIBUTE, userId);
            return true;
        } catch (ExpiredJwtException e) {
            throw new CoreException(ErrorType.TOKEN_EXPIRED, "만료된 토큰입니다.");
        } catch (JwtException e) {
            throw new CoreException(ErrorType.UNAUTHENTICATED, "유효하지 않은 토큰입니다.");
        }
    }
}
