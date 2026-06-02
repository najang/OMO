# OMO · 시퀀스 다이어그램

## 1. 일일 알림 발송 흐름

```mermaid
sequenceDiagram
    autonumber
    participant CRON as Scheduler (1분 cron)
    participant DB as MySQL
    participant W as Weather API
    participant R as Recommender
    participant FCM as FCM
    participant APP as Mobile App

    activate CRON
    CRON->>+DB: 현재 시각에 알림 받을 유저 조회<br/>(notification_time = now & enabled = true)
    DB-->>-CRON: 대상 유저 리스트

    loop 각 유저별
        CRON->>+W: 유저 위치 기반 날씨 조회
        W-->>-CRON: 기온, 날씨 상태

        CRON->>+R: 추천 생성 요청<br/>(날씨 + user_temp_profile)
        R-->>-CRON: 추천 결과<br/>(level, description)

        CRON->>+DB: DAILY_RECOMMENDATION 저장
        DB-->>-CRON: 저장 완료

        CRON->>+FCM: 푸시 메시지 발송<br/>(FCM 토큰 기준)
        FCM-->>APP: 푸시 알림 전달
        FCM-->>-CRON: 발송 결과
    end

    deactivate CRON
```

## 2. 소셜 로그인 흐름

```mermaid
sequenceDiagram
    autonumber
    participant APP as Mobile App
    participant API as Backend API
    participant Social as 소셜 서버<br/>(Google·Kakao·Apple)
    participant DB as MySQL

    APP->>+Social: 소셜 로그인 - 앱 SDK
    Social-->>-APP: 소셜 토큰 발급

    APP->>+API: POST /api/v1/auth/login<br/>{ provider, token }

    API->>+Social: 토큰 검증 요청<br/>(Google: tokeninfo / Kakao: user/me / Apple: JWKS)
    Social-->>-API: 유저 정보 - email, providerId, nickname

    API->>DB: provider, providerId로 유저 조회

    alt 기존 유저
        DB-->>API: User
    else 신규 유저
        DB-->>API: null
        API->>+DB: 유저 생성 - 자동 가입
        DB-->>-API: User
    end

    API-->>-APP: 200 OK<br/>{ accessToken, refreshToken, userId, isNewUser }
```

- `isNewUser = true`이면 앱이 닉네임 설정 화면(`/nickname`)으로 이동한다.
- `isNewUser`는 `onboarding_completed`의 반전값이다. 온보딩 완료 후 재로그인하면 `false`를 반환한다.

> Apple은 SDK에서 받은 identityToken(RS256 JWT)을 Apple JWKS 공개키로 직접 검증한다. Google·Kakao와 달리 별도 검증 API 엔드포인트가 없다.

## 3. 온보딩 — 닉네임 설정

```mermaid
sequenceDiagram
    autonumber
    participant APP as Mobile App
    participant API as Backend API
    participant DB as MySQL

    APP->>APP: isNewUser = true 확인<br/>→ /nickname 화면 이동

    APP->>+API: PUT /api/v1/users/me/onboarding<br/>{ nickname } + Authorization: Bearer {accessToken}
    API->>API: JWT에서 userId 추출
    API->>+DB: User 조회 (userId)
    DB-->>-API: User
    API->>API: nickname 유효성 검사<br/>(한글/영어 2~6자)

    alt 유효한 닉네임
        API->>+DB: nickname 업데이트, onboarding_completed = true
        DB-->>-API: 완료
        API-->>-APP: 200 OK { meta: { result: SUCCESS } }
        APP->>APP: AuthState.isNewUser = false<br/>→ 홈 화면으로 이동
    else 유효하지 않은 닉네임
        API-->>APP: 400 INVALID_INPUT
    end
```

## 3. 앱 진입 → 피드백 토스트 흐름

```mermaid
sequenceDiagram
    autonumber
    participant U as User
    participant APP as Mobile App
    participant API as Backend API
    participant DB as MySQL

    U->>+APP: 앱 실행
    APP->>+API: GET /feedbacks/pending - 어제 피드백 존재 여부 확인
    API->>+DB: 어제 추천 조회 및 피드백 미입력 확인
    DB-->>-API: 미입력 추천 정보
    API-->>-APP: { hasPending: true, recommendation: {...} }

    alt 피드백 미입력 상태
        APP-->>U: 토스트 노출 - "어제 옷차림 어떠셨어요?"
        U->>APP: 추웠음 / 적당 / 더웠음 / 실내 선택
        APP->>+API: POST /feedbacks { type, recommendation_id }
        API->>+DB: DAILY_FEEDBACK 저장
        DB-->>-API: 저장 완료

        opt type이 INDOOR가 아닌 경우
            API->>+DB: temp_offset 갱신
            DB-->>-API: 갱신 완료
        end

        API-->>-APP: 200 OK
        APP-->>U: 토스트 닫힘
    else 이미 입력했거나 어제 추천 없음
        APP-->>U: 토스트 노출 안 함
    end

    APP-->>-U: 앱 실행 완료
```

## 4. 옷차림 추천 조회 + 개인화 보정

```mermaid
sequenceDiagram
    autonumber
    participant APP as Mobile App
    participant API as Backend API
    participant DB as MySQL
    participant R as Recommender
    participant W as Weather API
    participant Cache as Redis

    APP->>+API: GET /recommendations/today
    API->>DB: 오늘자 추천 조회

    alt 추천 존재
        DB-->>API: DAILY_RECOMMENDATION
    else 추천 없음 - 앱 직접 요청 케이스
        DB-->>API: null

        API->>Cache: 동일 지역 날씨 캐시 조회

        alt 캐시 hit
            Cache-->>API: 날씨 데이터
        else 캐시 miss
            Cache-->>API: null
            API->>+W: 날씨 API 호출
            W-->>-API: 날씨 데이터
            API->>+Cache: 30분 TTL로 저장
            Cache-->>-API: 저장 완료
        end

        API->>+DB: USER_TEMP_PROFILE 조회
        DB-->>-API: temp_offset

        API->>+R: 추천 계산 - 날씨 + offset
        R-->>-API: 추천 결과

        API->>+DB: DAILY_RECOMMENDATION 저장
        DB-->>-API: 저장 완료
    end

    API-->>-APP: 추천 응답 - 옷차림 레벨 + 설명
```
