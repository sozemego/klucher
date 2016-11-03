CREATE TABLE chat_rooms (
	id bigint(20) NOT NULL AUTO_INCREMENT,
	name varchar(255) NOT NULL,
	created_at datetime NOT NULL,
	closed_at datetime,
	unique_users bigint(11),
	max_concurrent_users bigint(11),
	PRIMARY KEY (id)
);