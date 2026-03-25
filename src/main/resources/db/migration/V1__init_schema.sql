CREATE TABLE users
(
    id         BIGINT       NOT NULL AUTO_INCREMENT PRIMARY KEY,
    email      VARCHAR(255) NOT NULL UNIQUE,
    nickname   VARCHAR(100) NOT NULL,
    created_at DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE routines
(
    id         BIGINT       NOT NULL AUTO_INCREMENT PRIMARY KEY,
    user_id    BIGINT       NOT NULL,
    title      VARCHAR(255) NOT NULL,
    created_at DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_routines_user FOREIGN KEY (user_id) REFERENCES users (id)
);

CREATE TABLE routine_items
(
    id         BIGINT       NOT NULL AUTO_INCREMENT PRIMARY KEY,
    routine_id BIGINT       NOT NULL,
    title      VARCHAR(255) NOT NULL,
    order_num  INT          NOT NULL,
    CONSTRAINT fk_routine_items_routine FOREIGN KEY (routine_id) REFERENCES routines (id)
);

CREATE TABLE follows
(
    follower_id  BIGINT   NOT NULL,
    following_id BIGINT   NOT NULL,
    created_at   DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    PRIMARY KEY (follower_id, following_id),
    CONSTRAINT fk_follows_follower  FOREIGN KEY (follower_id)  REFERENCES users (id),
    CONSTRAINT fk_follows_following FOREIGN KEY (following_id) REFERENCES users (id)
);
