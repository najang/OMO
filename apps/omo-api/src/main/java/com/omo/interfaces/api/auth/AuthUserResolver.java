package com.omo.interfaces.api.auth;

import com.omo.support.error.CoreException;
import com.omo.support.error.ErrorType;
import org.springframework.core.MethodParameter;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.support.WebDataBinderFactory;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.method.support.ModelAndViewContainer;

import jakarta.servlet.http.HttpServletRequest;

@Component
public class AuthUserResolver implements HandlerMethodArgumentResolver {

    @Override
    public boolean supportsParameter(MethodParameter parameter) {
        return parameter.hasParameterAnnotation(AuthUser.class)
            && Long.class.isAssignableFrom(parameter.getParameterType());
    }

    @Override
    public Object resolveArgument(MethodParameter parameter, ModelAndViewContainer mavContainer,
                                  NativeWebRequest webRequest, WebDataBinderFactory binderFactory) {
        HttpServletRequest request = webRequest.getNativeRequest(HttpServletRequest.class);
        if (request == null) {
            throw new CoreException(ErrorType.UNAUTHENTICATED, "인증 정보를 찾을 수 없습니다.");
        }
        Object userId = request.getAttribute(AuthInterceptor.USER_ID_ATTRIBUTE);
        if (userId == null) {
            throw new CoreException(ErrorType.UNAUTHENTICATED, "인증 정보를 찾을 수 없습니다.");
        }
        return userId;
    }
}
