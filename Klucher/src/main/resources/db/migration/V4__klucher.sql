ALTER TABLE user
ADD COLUMN kluchs_per_request int(11);

ALTER TABLE user
ADD COLUMN admin bit(1) DEFAULT 0,
ADD COLUMN user bit(1) DEFAULT 1;

UPDATE user, user_roles
SET user.admin=user_roles.admin
WHERE user.user_roles_id=user_roles.id;

UPDATE user, user_roles
SET user.user=user_roles.user
WHERE user.user_roles_id=user_roles.id;

ALTER TABLE user
DROP FOREIGN KEY FKhmd3yu14haupqtsyri0gjpehi;

ALTER TABLE user
DROP COLUMN user_roles_id;

DROP TABLE user_roles;