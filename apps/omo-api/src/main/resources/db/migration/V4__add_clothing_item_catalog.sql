CREATE TABLE `clothing_item`
(
    `id`         BIGINT       NOT NULL AUTO_INCREMENT,
    `system_key` VARCHAR(50)  NOT NULL,
    `category`   VARCHAR(50)  NOT NULL,
    `name_ko`    VARCHAR(100) NOT NULL,
    `created_at` DATETIME(6)  NOT NULL,
    `updated_at` DATETIME(6)  NOT NULL,
    `deleted_at` DATETIME(6)  NULL,
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_clothing_item_system_key` (`system_key`)
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4
  COLLATE = utf8mb4_general_ci;

INSERT INTO `clothing_item` (`system_key`, `category`, `name_ko`, `created_at`, `updated_at`) VALUES
-- 상의
('short-tee',    'TOP',       '반팔 티셔츠', NOW(), NOW()),
('long-tee',     'TOP',       '긴팔 티셔츠', NOW(), NOW()),
('shirt',        'TOP',       '셔츠',        NOW(), NOW()),
('blouse',       'TOP',       '블라우스',    NOW(), NOW()),
('sweatshirt',   'TOP',       '맨투맨',      NOW(), NOW()),
('hoodie',       'TOP',       '후드티',      NOW(), NOW()),
('knit',         'TOP',       '니트',        NOW(), NOW()),
('cardigan',     'TOP',       '가디건',      NOW(), NOW()),
-- 하의
('shorts',       'PANTS',     '반바지',      NOW(), NOW()),
('jeans',        'PANTS',     '청바지',      NOW(), NOW()),
('slacks',       'PANTS',     '슬랙스',      NOW(), NOW()),
('cotton-pants', 'PANTS',     '면바지',      NOW(), NOW()),
('leggings',     'PANTS',     '레깅스',      NOW(), NOW()),
-- 치마
('skirt',        'SKIRT',     '치마',        NOW(), NOW()),
-- 아우터
('windbreaker',  'OUTER',     '바람막이',    NOW(), NOW()),
('denim-jacket', 'OUTER',     '청자켓',      NOW(), NOW()),
('leather-jacket','OUTER',    '가죽자켓',    NOW(), NOW()),
('trench-coat',  'OUTER',     '트렌치코트',  NOW(), NOW()),
('padding',      'OUTER',     '패딩',        NOW(), NOW()),
('long-coat',    'OUTER',     '롱코트',      NOW(), NOW()),
('parka',        'OUTER',     '파카',        NOW(), NOW()),
-- 원피스
('mini-dress',   'DRESS',     '미니 원피스', NOW(), NOW()),
('midi-dress',   'DRESS',     '미디 원피스', NOW(), NOW()),
('long-dress',   'DRESS',     '롱 원피스',   NOW(), NOW()),
('shirt-dress',  'DRESS',     '셔츠 원피스', NOW(), NOW()),
-- 신발
('sneakers',     'SHOES',     '운동화',      NOW(), NOW()),
('loafers',      'SHOES',     '로퍼',        NOW(), NOW()),
('boots',        'SHOES',     '부츠',        NOW(), NOW()),
('sandals',      'SHOES',     '샌들',        NOW(), NOW()),
('slippers',     'SHOES',     '슬리퍼',      NOW(), NOW()),
-- 모자 / 스카프
('cap',          'ACCESSORY', '볼캡',        NOW(), NOW()),
('bucket-hat',   'ACCESSORY', '버킷햇',      NOW(), NOW()),
('beanie',       'ACCESSORY', '비니',        NOW(), NOW()),
('beret',        'ACCESSORY', '베레모',      NOW(), NOW()),
('sun-hat',      'ACCESSORY', '썬햇',        NOW(), NOW()),
('scarf',        'ACCESSORY', '스카프',      NOW(), NOW()),
('muffler',      'ACCESSORY', '머플러',      NOW(), NOW()),
('neck-warmer',  'ACCESSORY', '넥워머',      NOW(), NOW()),
('shawl',        'ACCESSORY', '숄',          NOW(), NOW()),
-- 상의 추가
('sleeveless',   'TOP',       '민소매/나시', NOW(), NOW()),
('turtleneck',   'TOP',       '폴라티',      NOW(), NOW()),
-- 아우터 추가
('raincoat',     'OUTER',     '레인코트',    NOW(), NOW()),
('blazer',       'OUTER',     '블레이저',    NOW(), NOW()),
-- 원피스 추가
('jumpsuit',     'DRESS',     '점프수트',    NOW(), NOW()),
-- 신발 추가
('rain-boots',   'SHOES',     '장화',        NOW(), NOW()),
('heels',        'SHOES',     '힐/구두',     NOW(), NOW());

DROP TABLE IF EXISTS `user_wardrobe_category`;

CREATE TABLE `user_wardrobe_item`
(
    `wardrobe_id` BIGINT NOT NULL,
    `item_id`     BIGINT NOT NULL,
    PRIMARY KEY (`wardrobe_id`, `item_id`),
    CONSTRAINT `fk_uwi_wardrobe_id`
        FOREIGN KEY (`wardrobe_id`) REFERENCES `user_wardrobe` (`id`),
    CONSTRAINT `fk_uwi_item_id`
        FOREIGN KEY (`item_id`) REFERENCES `clothing_item` (`id`)
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4
  COLLATE = utf8mb4_general_ci;
