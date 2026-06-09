CREATE TABLE `user_wardrobe`
(
    `id`         BIGINT      NOT NULL AUTO_INCREMENT,
    `user_id`    BIGINT      NOT NULL,
    `created_at` DATETIME(6) NOT NULL,
    `updated_at` DATETIME(6) NOT NULL,
    `deleted_at` DATETIME(6) NULL,
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_user_wardrobe_user_id` (`user_id`),
    CONSTRAINT `fk_user_wardrobe_user_id`
        FOREIGN KEY (`user_id`) REFERENCES `user` (`id`)
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4
  COLLATE = utf8mb4_general_ci;

CREATE TABLE `user_wardrobe_category`
(
    `wardrobe_id` BIGINT      NOT NULL,
    `category`    VARCHAR(50) NOT NULL,
    CONSTRAINT `fk_uwc_wardrobe_id`
        FOREIGN KEY (`wardrobe_id`) REFERENCES `user_wardrobe` (`id`)
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4
  COLLATE = utf8mb4_general_ci;
