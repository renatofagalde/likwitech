-- #1 create table
CREATE TABLE `student` (
	`id` BIGINT(22) NOT NULL AUTO_INCREMENT,
	`name` VARCHAR(255) NOT NULL,
	`email` VARCHAR(45) NOT NULL,
	`gender` VARCHAR(10) NULL,
	PRIMARY KEY (`id`),
	UNIQUE INDEX `email_UNIQUE` (`email` ASC));
