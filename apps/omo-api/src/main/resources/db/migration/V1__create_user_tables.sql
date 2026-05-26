CREATE TABLE `user`
(
    `id`          BIGINT       NOT NULL AUTO_INCREMENT,
    `email`       VARCHAR(255) NOT NULL,
    `nickname`    VARCHAR(50)  NOT NULL,
    `provider`    VARCHAR(20)  NOT NULL COMMENT 'GOOGLE | APPLE | KAKAO',
    `provider_id` VARCHAR(255) NOT NULL,
    `created_at`  DATETIME(6)  NOT NULL,
    `updated_at`  DATETIME(6)  NOT NULL,
    `deleted_at`  DATETIME(6)  NULL,
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_user_provider` (`provider`, `provider_id`),
    INDEX `idx_user_email` (`email`)
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4
  COLLATE = utf8mb4_general_ci;

CREATE TABLE `user_temp_profile`
(
    `id`             BIGINT      NOT NULL AUTO_INCREMENT,
    `user_id`        BIGINT      NOT NULL,
    `temp_offset`    DECIMAL(4, 1) NOT NULL DEFAULT 0.0 COMMENT '체감 보정값(°C), 범위: -3.0 ~ +3.0',
    `feedback_count` INT         NOT NULL DEFAULT 0,
    `created_at`     DATETIME(6) NOT NULL,
    `updated_at`     DATETIME(6) NOT NULL,
    `deleted_at`     DATETIME(6) NULL,
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_user_temp_profile_user_id` (`user_id`),
    CONSTRAINT `fk_user_temp_profile_user_id`
        FOREIGN KEY (`user_id`) REFERENCES `user` (`id`)
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4
  COLLATE = utf8mb4_general_ci;
