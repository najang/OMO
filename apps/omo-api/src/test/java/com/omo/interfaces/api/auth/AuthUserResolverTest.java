package com.omo.interfaces.api.auth;

import com.omo.support.error.CoreException;
import com.omo.support.error.ErrorType;
import jakarta.servlet.http.HttpServletRequest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.core.MethodParameter;
import org.springframework.web.context.request.NativeWebRequest;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@DisplayName("AuthUserResolver")
class AuthUserResolverTest {

    private AuthUserResolver resolver;
    private NativeWebRequest webRequest;
    private HttpServletRequest httpRequest;

    @BeforeEach
    void setUp() {
        resolver = new AuthUserResolver();
        webRequest = mock(NativeWebRequest.class);
        httpRequest = mock(HttpServletRequest.class);
        when(webRequest.getNativeRequest(HttpServletRequest.class)).thenReturn(httpRequest);
    }

    @Nested
    @DisplayName("supportsParameter")
    class SupportsParameter {

        @Test
        @DisplayName("@AuthUser Long 파라미터면 true를 반환한다.")
        void returnsTrue_whenAuthUserLongParameter() throws Exception {
            MethodParameter parameter = methodParameter("authenticated");

            assertThat(resolver.supportsParameter(parameter)).isTrue();
        }

        @Test
        @DisplayName("@AuthUser가 없는 파라미터면 false를 반환한다.")
        void returnsFalse_whenNoAnnotation() throws Exception {
            MethodParameter parameter = methodParameter("noAnnotation");

            assertThat(resolver.supportsParameter(parameter)).isFalse();
        }

        @Test
        @DisplayName("@AuthUser String 파라미터면 false를 반환한다.")
        void returnsFalse_whenAuthUserStringParameter() throws Exception {
            MethodParameter parameter = methodParameter("wrongType");

            assertThat(resolver.supportsParameter(parameter)).isFalse();
        }
    }

    @Nested
    @DisplayName("resolveArgument")
    class ResolveArgument {

        @Test
        @DisplayName("request attribute에 userId가 있으면, userId를 반환한다.")
        void returnsUserId_whenAttributeExists() throws Exception {
            when(httpRequest.getAttribute(AuthInterceptor.USER_ID_ATTRIBUTE)).thenReturn(42L);

            Object result = resolver.resolveArgument(null, null, webRequest, null);

            assertThat(result).isEqualTo(42L);
        }

        @Test
        @DisplayName("request attribute에 userId가 없으면, UNAUTHENTICATED 예외가 발생한다.")
        void throwsUnauthenticated_whenAttributeMissing() {
            when(httpRequest.getAttribute(AuthInterceptor.USER_ID_ATTRIBUTE)).thenReturn(null);

            assertThatThrownBy(() -> resolver.resolveArgument(null, null, webRequest, null))
                .isInstanceOf(CoreException.class)
                .satisfies(e -> assertThat(((CoreException) e).getErrorType()).isEqualTo(ErrorType.UNAUTHENTICATED));
        }

        @Test
        @DisplayName("getNativeRequest가 null을 반환하면, UNAUTHENTICATED 예외가 발생한다.")
        void throwsUnauthenticated_whenNativeRequestIsNull() {
            NativeWebRequest nullNativeReq = mock(NativeWebRequest.class);
            when(nullNativeReq.getNativeRequest(HttpServletRequest.class)).thenReturn(null);

            assertThatThrownBy(() -> resolver.resolveArgument(null, null, nullNativeReq, null))
                .isInstanceOf(CoreException.class)
                .satisfies(e -> assertThat(((CoreException) e).getErrorType()).isEqualTo(ErrorType.UNAUTHENTICATED));
        }
    }

    private MethodParameter methodParameter(String methodName) throws Exception {
        return new MethodParameter(SampleController.class.getMethod(methodName, getMethodParamType(methodName)), 0);
    }

    private Class<?> getMethodParamType(String methodName) throws Exception {
        return switch (methodName) {
            case "authenticated" -> Long.class;
            case "noAnnotation" -> Long.class;
            case "wrongType" -> String.class;
            default -> throw new IllegalArgumentException(methodName);
        };
    }

    @SuppressWarnings("unused")
    static class SampleController {
        public void authenticated(@AuthUser Long userId) {}
        public void noAnnotation(Long userId) {}
        public void wrongType(@AuthUser String userId) {}
    }
}
