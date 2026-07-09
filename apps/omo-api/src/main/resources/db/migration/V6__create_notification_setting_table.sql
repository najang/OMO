CREATE TABLE `notification_setting`
(
    `id`                BIGINT       NOT NULL AUTO_INCREMENT,
    `user_id`           BIGINT       NOT NULL,
    `notification_time` TIME         NULL COMMENT '알림 시각, 예: 07:30',
    `timezone`          VARCHAR(64)  NOT NULL COMMENT '예: Asia/Seoul',
    `location_lat`      VARCHAR(32)  NULL,
    `location_lon`      VARCHAR(32)  NULL,
    `location_name`     VARCHAR(255) NULL,
    `enabled`           BOOLEAN      NOT NULL,
    `created_at`        DATETIME(6)  NOT NULL,
    `updated_at`        DATETIME(6)  NOT NULL,
    `deleted_at`        DATETIME(6)  NULL,
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_notification_setting_user_id` (`user_id`),
    CONSTRAINT `fk_notification_setting_user_id`
        FOREIGN KEY (`user_id`) REFERENCES `user` (`id`)
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4
  COLLATE = utf8mb4_general_ci;
