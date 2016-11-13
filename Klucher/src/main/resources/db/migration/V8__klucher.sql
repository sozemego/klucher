ALTER DATABASE db
CHARACTER SET = utf8mb4
COLLATE = utf8mb4_unicode_ci;

ALTER TABLE
chat_rooms
CONVERT TO CHARACTER SET utf8mb4
COLLATE utf8mb4_unicode_ci;

ALTER TABLE
follows
CONVERT TO CHARACTER SET utf8mb4
COLLATE utf8mb4_unicode_ci;

ALTER TABLE
kluch_hashtags
CONVERT TO CHARACTER SET utf8mb4
COLLATE utf8mb4_unicode_ci;

ALTER TABLE
kluch_likes
CONVERT TO CHARACTER SET utf8mb4
COLLATE utf8mb4_unicode_ci;

ALTER TABLE
kluch_mentions
CONVERT TO CHARACTER SET utf8mb4
COLLATE utf8mb4_unicode_ci;

ALTER TABLE
kluchs
CONVERT TO CHARACTER SET utf8mb4
COLLATE utf8mb4_unicode_ci;

ALTER TABLE
user
CONVERT TO CHARACTER SET utf8mb4
COLLATE utf8mb4_unicode_ci;

ALTER TABLE
user_likes
CONVERT TO CHARACTER SET utf8mb4
COLLATE utf8mb4_unicode_ci;

ALTER TABLE chat_rooms
CHANGE name name VARCHAR(255)
CHARACTER SET utf8mb4
COLLATE utf8mb4_unicode_ci;

ALTER TABLE kluch_hashtags
CHANGE hashtags hashtags VARCHAR(255)
CHARACTER SET utf8mb4
COLLATE utf8mb4_unicode_ci;

ALTER TABLE kluch_mentions
CHANGE mentions mentions VARCHAR(255)
CHARACTER SET utf8mb4
COLLATE utf8mb4_unicode_ci;

ALTER TABLE kluchs
CHANGE text text VARCHAR(255)
CHARACTER SET utf8mb4
COLLATE utf8mb4_unicode_ci;

ALTER TABLE user
CHANGE avatar_path avatar_path VARCHAR(255)
CHARACTER SET utf8mb4
COLLATE utf8mb4_unicode_ci;

ALTER TABLE user
CHANGE hashed_password hashed_password VARCHAR(255)
CHARACTER SET utf8mb4
COLLATE utf8mb4_unicode_ci;

ALTER TABLE user
CHANGE username username VARCHAR(32)
CHARACTER SET utf8mb4
COLLATE utf8mb4_unicode_ci;

ALTER TABLE user
CHANGE profile_description profile_description VARCHAR(140)
CHARACTER SET utf8mb4
COLLATE utf8mb4_unicode_ci;