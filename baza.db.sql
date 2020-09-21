BEGIN TRANSACTION;
CREATE TABLE IF NOT EXISTS `grad` (
	`id`	INTEGER,
	`naziv`	TEXT,
	`broj_stanovnika`	INTEGER,
	`drzava`	INTEGER,
	`gradonacelnik` INTEGER,
	FOREIGN KEY(`drzava`) REFERENCES `drzava`,
	FOREIGN KEY(`gradonacelnik`) REFERENCES `osoba`,
	PRIMARY KEY(`id`)
);
INSERT INTO `grad` VALUES (1,'Pariz',2206488,1,0);
INSERT INTO `grad` VALUES (2,'London',8825000,2,0);
INSERT INTO `grad` VALUES (3,'Beč',1899055,3,0);
INSERT INTO `grad` VALUES (4,'Mančester',545500,2,0);
INSERT INTO `grad` VALUES (5,'Grac',280200,3,0);
CREATE TABLE IF NOT EXISTS `drzava` (
	`id`	INTEGER,
	`naziv`	TEXT,
	`glavni_grad`	INTEGER,
	PRIMARY KEY(`id`)
);
INSERT INTO `drzava` VALUES (1,'Francuska',1);
INSERT INTO `drzava` VALUES (2,'Velika Britanija',2);
INSERT INTO `drzava` VALUES (3,'Austrija',3);
CREATE TABLE IF NOT EXISTS `osoba` (
	`id`	INTEGER,
	`ime`	TEXT,
	`prezime`	TEXT,
	PRIMARY KEY(`id`)
);
COMMIT;
