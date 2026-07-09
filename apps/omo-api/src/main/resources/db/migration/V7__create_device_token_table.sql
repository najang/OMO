CREATE TABLE `device_token`
(
    `id`           BIGINT       NOT NULL AUTO_INCREMENT,
    `user_id`      BIGINT       NOT NULL,
    `fcm_token`    VARCHAR(512) NOT NULL COMMENT 'FCM 등록 토큰',
    `device_type`  VARCHAR(16)  NOT NULL COMMENT 'IOS / ANDROID',
    `last_used_at` DATETIME(6)  NOT NULL COMMENT '토큰 마지막 사용 시각',
    `created_at`   DATETIME(6)  NOT NULL,
    `updated_at`   DATETIME(6)  NOT NULL,
    `deleted_at`   DATETIME(6)  NULL,
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_device_token_fcm_token` (`fcm_token`),
    KEY `idx_device_token_user_id` (`user_id`),
    CONSTRAINT `fk_device_token_user_id`
        FOREIGN KEY (`user_id`) REFERENCES `user` (`id`)
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4
  COLLATE = utf8mb4_general_ci;
