# OMO · ERD

```mermaid
erDiagram
    USER ||--o| NOTIFICATION_SETTING : has
    USER ||--o{ DEVICE_TOKEN : registers
    USER ||--o{ DAILY_RECOMMENDATION : receives
    USER ||--o{ DAILY_FEEDBACK : submits
    USER ||--o| USER_TEMP_PROFILE : has
    DAILY_RECOMMENDATION ||--o| DAILY_FEEDBACK : "feedback_for"

    USER {
        bigint id PK
        varchar email "INDEX (non-unique)"
        varchar nickname
        varchar provider "GOOGLE/APPLE/KAKAO"
        varchar provider_id "UK: (provider, provider_id)"
        datetime created_at
        datetime updated_at
    }

    NOTIFICATION_SETTING {
        bigint id PK
        bigint user_id FK
        time notification_time "예: 07:30"
        varchar timezone "예: Asia/Seoul"
        varchar location_lat
        varchar location_lon
        varchar location_name
        boolean enabled
        datetime updated_at
    }

    DEVICE_TOKEN {
        bigint id PK
        bigint user_id FK
        varchar fcm_token
        varchar device_type "IOS/ANDROID"
        datetime last_used_at
    }

    DAILY_RECOMMENDATION {
        bigint id PK
        bigint user_id FK
        date target_date
        decimal temp_min
        decimal temp_max
        decimal feels_like
        varchar weather_condition "CLEAR/RAIN/SNOW..."
        varchar outfit_level "1~7 단계"
        text outfit_description
        datetime created_at
    }

    DAILY_FEEDBACK {
        bigint id PK
        bigint user_id FK
        bigint recommendation_id FK "nullable"
        date target_date
        varchar feedback_type "HOT/MODERATE/COLD/INDOOR"
        datetime created_at
    }

    USER_TEMP_PROFILE {
        bigint id PK
        bigint user_id FK
        decimal temp_offset "체감 보정값(°C)"
        int feedback_count "누적 피드백 수"
        datetime updated_at
    }
```

## 핵심 설계 포인트

- `USER.email`은 unique key가 **아님** — 동일 이메일로 Google/Apple/Kakao 각각 가입 시 별개 계정으로 처리. unique key는 `(provider, provider_id)` 쌍
- `DAILY_RECOMMENDATION`은 알림 발송 시점에 미리 생성 → 앱에서 조회만 (응답 속도 ↑)
- `DAILY_FEEDBACK`은 추천과 1:1 연결되지만 `INDOOR`인 경우 추천과 무관하게 기록 가능
- `USER_TEMP_PROFILE.temp_offset`: 피드백 누적 시 점진적으로 업데이트되는 개인화 보정값
  - 양수 → "더위를 잘 타는 유저" → 추천 옷차림 한 단계 가볍게
  - 음수 → "추위를 잘 타는 유저" → 추천 옷차림 한 단계 두껍게
