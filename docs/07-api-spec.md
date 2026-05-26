# OMO · API 명세

Base URL: `/api/v1`

| Method | Endpoint | 설명 |
|--------|----------|------|
| POST | `/auth/login` | 소셜 로그인 (JWT 발급) |
| POST | `/devices` | FCM 토큰 등록 |
| GET | `/recommendations/today` | 오늘의 옷차림 추천 조회 |
| GET | `/recommendations?from=&to=` | 기간별 추천 + 피드백 조회 (마이페이지) |
| GET | `/feedbacks/pending` | 미입력 피드백 확인 (토스트용) |
| POST | `/feedbacks` | 피드백 등록 |
| GET | `/users/me/notification` | 알림 설정 조회 |
| PUT | `/users/me/notification` | 알림 시간 / 위치 변경 |
