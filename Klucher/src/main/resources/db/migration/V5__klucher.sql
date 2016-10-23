ALTER TABLE user
ADD COLUMN profile_description varchar(140);

ALTER TABLE user
MODIFY kluchs_per_request int(11) DEFAULT 30 NOT NULL;

ALTER TABLE user
ADD COLUMN deleted bit(1) NOT NULL DEFAULT 0;