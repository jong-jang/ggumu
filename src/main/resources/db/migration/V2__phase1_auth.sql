-- Phase 1: users 테이블 재설계 (UUID PK, 카카오 소셜 로그인 기반)

SET FOREIGN_KEY_CHECKS = 0;
DROP TABLE IF EXISTS follows;
DROP TABLE IF EXISTS routine_items;
DROP TABLE IF EXISTS routines;
DROP TABLE IF EXISTS users;
SET FOREIGN_KEY_CHECKS = 1;

CREATE TABLE users
(
    id                   CHAR(36)     NOT NULL PRIMARY KEY,
    provider             VARCHAR(20)  NOT NULL DEFAULT 'kakao',
    provider_id          VARCHAR(100) NOT NULL,
    nickname             VARCHAR(50),
    profile_image_url    TEXT,
    bio                  VARCHAR(200),
    survey_result        JSON,
    onboarding_completed BOOLEAN      NOT NULL DEFAULT FALSE,
    role                 VARCHAR(20)  NOT NULL DEFAULT 'USER',
    deleted_at           DATETIME,
    created_at           DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at           DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    UNIQUE KEY uk_provider_provider_id (provider, provider_id)
);

CREATE TABLE routines
(
    id         CHAR(36)     NOT NULL PRIMARY KEY,
    user_id    CHAR(36)     NOT NULL,
    title      VARCHAR(255) NOT NULL,
    created_at DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_routines_user FOREIGN KEY (user_id) REFERENCES users (id)
);

CREATE TABLE routine_items
(
    id         CHAR(36)     NOT NULL PRIMARY KEY,
    routine_id CHAR(36)     NOT NULL,
    title      VARCHAR(255) NOT NULL,
    order_num  INT          NOT NULL,
    CONSTRAINT fk_routine_items_routine FOREIGN KEY (routine_id) REFERENCES routines (id)
);

CREATE TABLE follows
(
    follower_id  CHAR(36) NOT NULL,
    following_id CHAR(36) NOT NULL,
    created_at   DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    PRIMARY KEY (follower_id, following_id),
    CONSTRAINT fk_follows_follower FOREIGN KEY (follower_id) REFERENCES users (id),
    CONSTRAINT fk_follows_following FOREIGN KEY (following_id) REFERENCES users (id)
);
