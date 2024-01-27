CREATE TABLE `user` (
	`id` int unsigned NOT NULL AUTO_INCREMENT,
	`name` varchar(45) NOT NULL,
	`email` varchar(320) NOT NULL,
	`password` varchar(45) NOT NULL,
	PRIMARY KEY (`id`),
	CONSTRAINT `user_email_un` UNIQUE (`email`)
);

CREATE TABLE `song` (
	`id` int unsigned NOT NULL AUTO_INCREMENT,
	`title` varchar(45) NOT NULL,
	`cover_path` varchar(255) NOT NULL, -- Path of the image cover
	`album` varchar(45) NOT NULL,
	`artist` varchar(45) NOT NULL,
	`album_year` int unsigned NOT NULL,
	`genre` varchar(45) NOT NULL,
	`file_path` varchar(255) NOT NULL, -- Path of the song file
	`uploader_id` int unsigned NOT NULL,
	PRIMARY KEY (`id`),
	CONSTRAINT `song_uploader_id_fk` FOREIGN KEY (`uploader_id`) REFERENCES `user` (`id`)
);

CREATE TABLE `playlist` (
	`id` int unsigned NOT NULL AUTO_INCREMENT,
	`title` varchar(45) NOT NULL,
	`owner_id` int unsigned NOT NULL,
	`creation_date` datetime NOT NULL,
	PRIMARY KEY (`id`),
	CONSTRAINT `playlist_owner_id_fk` FOREIGN KEY (`owner_id`) REFERENCES `user` (`id`)
);

CREATE TABLE `binder` (
	`id` int unsigned NOT NULL AUTO_INCREMENT,
	`playlist_id` int unsigned NOT NULL,
	`song_id` int unsigned NOT NULL,
    `song_position` int unsigned NOT NULL,
	PRIMARY KEY (`id`),
	CONSTRAINT `binder_playlist_id_fk` FOREIGN KEY (`playlist_id`) REFERENCES `playlist` (`id`),
	CONSTRAINT `binder_song_id_fk` FOREIGN KEY (`song_id`) REFERENCES `song` (`id`)
)