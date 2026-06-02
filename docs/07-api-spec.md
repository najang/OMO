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
| PUT | `/users/me/onboarding` | 닉네임 설정 (신규 유저 온보딩) | 필요 |
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
    "userId": 1,
    "isNewUser": true
  }
}
```

| 필드 | 타입 | 설명 |
|------|------|------|
| `accessToken` | String | HS256 JWT, 유효기간 1시간 |
| `refreshToken` | String | HS256 JWT, 유효기간 30일 |
| `userId` | Long | 서버 내부 유저 ID |
| `isNewUser` | Boolean | 온보딩 미완료 여부. `true`이면 클라이언트가 닉네임 설정 화면으로 이동 |

**에러**

| 상태 코드 | errorCode | 원인 |
|-----------|-----------|------|
| 400 | `BAD_REQUEST` | 지원하지 않는 provider / 요청 body 누락 |
| 401 | `UNAUTHENTICATED` | 유효하지 않거나 만료된 소셜 토큰 |

---

## PUT /users/me/onboarding

신규 유저의 닉네임을 설정하고 온보딩을 완료한다. 멱등하게 반복 호출 가능하다 (닉네임 재설정에도 사용).

**Request Header**
```
Authorization: Bearer {accessToken}
```

**Request Body**
```json
{
  "nickname": "햇살곰"
}
```

| 필드 | 타입 | 설명 |
|------|------|------|
| `nickname` | String | 한글 또는 영어, 2~6자 (`^[가-힣a-zA-Z]{2,6}$`) |

**Response**
```json
{
  "meta": { "result": "SUCCESS", "errorCode": null },
  "data": null
}
```

**에러**

| 상태 코드 | errorCode | 원인 |
|-----------|-----------|------|
| 400 | `INVALID_INPUT` | 닉네임 형식 오류 (null, 빈 문자열, 길이 초과, 숫자/특수문자 포함) |
| 401 | `UNAUTHENTICATED` | Authorization 헤더 없음 또는 유효하지 않은 토큰 |
