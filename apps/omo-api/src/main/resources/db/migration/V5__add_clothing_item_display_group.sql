ALTER TABLE `clothing_item`
    ADD COLUMN `display_group` VARCHAR(20) NOT NULL DEFAULT 'TOP' AFTER `category`;

-- 상의
UPDATE `clothing_item` SET `display_group` = 'TOP' WHERE `system_key` IN (
    'short-tee', 'long-tee', 'shirt', 'blouse', 'sweatshirt',
    'hoodie', 'knit', 'cardigan', 'sleeveless', 'turtleneck'
);

-- 하의
UPDATE `clothing_item` SET `display_group` = 'BOTTOM' WHERE `system_key` IN (
    'shorts', 'jeans', 'slacks', 'cotton-pants', 'skirt', 'leggings'
);

-- 아우터
UPDATE `clothing_item` SET `display_group` = 'OUTER' WHERE `system_key` IN (
    'windbreaker', 'denim-jacket', 'leather-jacket', 'trench-coat', 'padding',
    'long-coat', 'parka', 'raincoat', 'blazer'
);

-- 원피스
UPDATE `clothing_item` SET `display_group` = 'DRESS' WHERE `system_key` IN (
    'mini-dress', 'midi-dress', 'long-dress', 'shirt-dress', 'jumpsuit'
);

-- 신발
UPDATE `clothing_item` SET `display_group` = 'SHOES' WHERE `system_key` IN (
    'sneakers', 'loafers', 'boots', 'sandals', 'slippers', 'rain-boots', 'heels'
);

-- 모자
UPDATE `clothing_item` SET `display_group` = 'HAT' WHERE `system_key` IN (
    'cap', 'bucket-hat', 'beanie', 'beret', 'sun-hat'
);

-- 스카프/목도리
UPDATE `clothing_item` SET `display_group` = 'SCARF' WHERE `system_key` IN (
    'scarf', 'muffler', 'neck-warmer', 'shawl'
);

ALTER TABLE `clothing_item`
    ALTER COLUMN `display_group` DROP DEFAULT;
