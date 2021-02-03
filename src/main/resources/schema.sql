CREATE TABLE `cocuser`
(
    `id`    int(11) NOT NULL AUTO_INCREMENT,
    `userName`  varchar(50)  DEFAULT NULL,
    `cocToken`  varchar(50)  DEFAULT NULL,
    `level` int(11) DEFAULT NULL,
    PRIMARY KEY (`id`)
);