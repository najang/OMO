# OMO · API 명세

Base URL: `/api/v1`

공통 응답 형식:
```json
{ "meta": { "errorCode": null }, "data": { ... } }
```
오류 시: `data`는 `null`, `meta.errorCode`에 에러 코드 반환.

| Method | Endpoint | 설명 | 인증 |
|--------|----------|------|------|
| POST | `/auth/login` | 소셜 로그인 (JWT 발급) | 불필요 |
| POST | `/devices` | FCM 토큰 등록 | 필요 |
| GET | `/recommendations/today` | 오늘의 옷차림 추천 조회 | 필요 |
| GET | `/recommendations?from=&to=` | 기간별 추천 + 피드백 조회 (마이페이지) | 필요 |
| GET | `/feedbacks/pending` | 미입력 피드백 확인 (토스트용) | 필요 |
| POST | `/feedbacks` | 피드백 등록 | 필요 |
| GET | `/users/me/notification` | 알림 설정 조회 | 필요 |
| PUT | `/users/me/notification` | 알림 시간 / 위치 변경 | 필요 |

---

## POST /auth/login

소셜 토큰으로 로그인한다. 신규 유저는 자동 가입된다.

**Request**
```json
{
  "provider": "GOOGLE",
  "token": "eyJhbGci..."
}
```

| 필드 | 타입 | 설명 |
|------|------|------|
| `provider` | `GOOGLE` \| `KAKAO` \| `APPLE` | 소셜 프로바이더 |
| `token` | String | Google: idToken / Kakao: accessToken / Apple: identityToken |

**Response**
```json
{
  "meta": { "errorCode": null },
  "data": {
    "accessToken": "eyJhbGci...",
    "refreshToken": "eyJhbGci...",
    "userId": 1
  }
}
```

| 필드 | 타입 | 설명 |
|------|------|------|
| `accessToken` | String | HS256 JWT, 유효기간 1시간 |
| `refreshToken` | String | HS256 JWT, 유효기간 30일 |
| `userId` | Long | 서버 내부 유저 ID |

**에러**

| 상태 코드 | errorCode | 원인 |
|-----------|-----------|------|
| 400 | `BAD_REQUEST` | 지원하지 않는 provider / 요청 body 누락 |
| 401 | `UNAUTHENTICATED` | 유효하지 않거나 만료된 소셜 토큰 |
