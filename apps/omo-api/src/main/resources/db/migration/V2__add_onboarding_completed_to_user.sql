ALTER TABLE `user`
    ADD COLUMN `onboarding_completed` BOOLEAN NOT NULL DEFAULT FALSE AFTER `provider_id`;
