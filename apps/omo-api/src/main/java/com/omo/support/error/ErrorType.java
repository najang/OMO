package com.omo.support.error;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@Getter
@RequiredArgsConstructor
public enum ErrorType {

    // ── 서버 ──────────────────────────────────────────────────────────────────
    INTERNAL_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, "INTERNAL_ERROR", "일시적인 오류가 발생했습니다."),

    // ── 요청 검증 ─────────────────────────────────────────────────────────────
    /** 파라미터 타입 불일치, JSON 파싱 오류 등 HTTP 요청 수준 오류 */
    BAD_REQUEST(HttpStatus.BAD_REQUEST, "BAD_REQUEST", "잘못된 요청입니다."),
    /** 도메인 모델·비즈니스 규칙 검증 실패 (필드 값 오류 등) */
    INVALID_INPUT(HttpStatus.BAD_REQUEST, "INVALID_INPUT", "입력 값을 확인해주세요."),

    // ── 인증 / 권한 ───────────────────────────────────────────────────────────
    UNAUTHENTICATED(HttpStatus.UNAUTHORIZED, "UNAUTHENTICATED", "로그인이 필요합니다."),
    TOKEN_EXPIRED(HttpStatus.UNAUTHORIZED, "TOKEN_EXPIRED", "로그인이 만료되었습니다. 다시 로그인해주세요."),
    UNAUTHORIZED(HttpStatus.FORBIDDEN, "UNAUTHORIZED", "접근 권한이 없습니다."),

    // ── 공통 조회 / 충돌 ──────────────────────────────────────────────────────
    NOT_FOUND(HttpStatus.NOT_FOUND, "NOT_FOUND", "존재하지 않는 리소스입니다."),
    CONFLICT(HttpStatus.CONFLICT, "CONFLICT", "이미 존재하는 리소스입니다."),

    // ── User ──────────────────────────────────────────────────────────────────
    USER_NOT_FOUND(HttpStatus.NOT_FOUND, "USER_NOT_FOUND", "사용자를 찾을 수 없습니다."),

    // ── Wardrobe ──────────────────────────────────────────────────────────────
    WARDROBE_NOT_FOUND(HttpStatus.NOT_FOUND, "WARDROBE_NOT_FOUND", "옷장을 찾을 수 없습니다."),

    // ── 날씨 ──────────────────────────────────────────────────────────────────
    WEATHER_UNAVAILABLE(HttpStatus.SERVICE_UNAVAILABLE, "WEATHER_UNAVAILABLE", "날씨 정보를 가져올 수 없습니다. 잠시 후 다시 시도해주세요.");

    private final HttpStatus status;
    private final String code;
    private final String message;
}
