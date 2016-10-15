CREATE TABLE user_likes (
	user_id bigint(20) NOT NULL,
	likes bigint(20) DEFAULT NULL,
	KEY `FKuserlikekey` (`user_id`),
  	CONSTRAINT `FKuserlikekey` FOREIGN KEY (`user_id`) REFERENCES `user` (`id`)
)