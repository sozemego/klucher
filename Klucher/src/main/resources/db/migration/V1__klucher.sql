SET @OLD_FOREIGN_KEY_CHECKS=@@FOREIGN_KEY_CHECKS, FOREIGN_KEY_CHECKS=0;

CREATE TABLE `user` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `avatar_path` varchar(255) DEFAULT NULL,
  `hashed_password` varchar(255) NOT NULL,
  `notifications` int(11) NOT NULL,
  `username` varchar(32) NOT NULL UNIQUE,
  `user_roles_id` bigint(20) NOT NULL,
  created_at datetime NOT NULL,
  PRIMARY KEY (`id`),
  KEY `FKhmd3yu14haupqtsyri0gjpehi` (`user_roles_id`),
  CONSTRAINT `FKhmd3yu14haupqtsyri0gjpehi` FOREIGN KEY (`user_roles_id`) REFERENCES `user_roles` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=455 DEFAULT CHARSET=latin1;

CREATE TABLE `user_roles` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `admin` bit(1) NOT NULL,
  `user` bit(1) NOT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=455 DEFAULT CHARSET=latin1;

CREATE TABLE `follows` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `followee_id` bigint(20) NOT NULL,
  `follower_id` bigint(20) NOT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

CREATE TABLE `kluch_hashtags` (
  `kluch_id` bigint(20) NOT NULL,
  `hashtags` varchar(255) DEFAULT NULL,
  KEY `FKljcusnc294dscxo4kdt749l53` (`kluch_id`),
  CONSTRAINT `FKljcusnc294dscxo4kdt749l53` FOREIGN KEY (`kluch_id`) REFERENCES `kluchs` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

CREATE TABLE `kluch_mentions` (
  `kluch_id` bigint(20) NOT NULL,
  `mentions` varchar(255) DEFAULT NULL,
  KEY `FK1d81kdd29idhobarfio3ks5aw` (`kluch_id`),
  CONSTRAINT `FK1d81kdd29idhobarfio3ks5aw` FOREIGN KEY (`kluch_id`) REFERENCES `kluchs` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

CREATE TABLE `kluchs` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `author_id` bigint(20) NOT NULL,
  `text` varchar(255) NOT NULL,
  `timestamp` datetime NOT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=2 DEFAULT CHARSET=latin1;

CREATE TABLE `persistent_logins` (
  `series` varchar(255) NOT NULL,
  `last_used` datetime NOT NULL,
  `token` varchar(255) NOT NULL,
  `username` varchar(255) NOT NULL,
  PRIMARY KEY (`series`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;
