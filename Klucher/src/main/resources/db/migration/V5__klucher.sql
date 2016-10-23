ALTER TABLE user
ADD COLUMN profile_description varchar(140);

UPDATE user
SET kluchs_per_request = 30
WHERE kluchs_per_request IS NULL;

ALTER TABLE user
MODIFY kluchs_per_request int(11) DEFAULT 30 NOT NULL;

ALTER TABLE user
ADD COLUMN deleted bit(1) DEFAULT 0 NOT NULL;