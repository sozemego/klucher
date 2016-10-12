CREATE TABLE kluch_likes (
	kluch_id bigint(20) NOT NULL,
	likes bigint(20) DEFAULT NULL,
	KEY `FKkluchlikekey` (`kluch_id`),
  	CONSTRAINT `FKkluchlikekey` FOREIGN KEY (`kluch_id`) REFERENCES `kluchs` (`id`)
)