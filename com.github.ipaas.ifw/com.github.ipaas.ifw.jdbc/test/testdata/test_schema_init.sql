CREATE DATABASE `fw_demo` /*!40100 DEFAULT CHARACTER SET utf8 */;


CREATE TABLE `demo_user` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `name` varchar(45) DEFAULT NULL,
  `content` varchar(45) DEFAULT NULL,
  `loginTime` timestamp NULL DEFAULT NULL,
  `address` varchar(45) DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;


CREATE TABLE `test_decim_tb` (
  `id` int(11) NOT NULL,
  `decim1` varchar(45) DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;


CREATE TABLE `user` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `name` varchar(45) DEFAULT NULL,
  `address` varchar(45) DEFAULT NULL,
  `content` varchar(45) DEFAULT NULL,
  `longin_time` timestamp NULL DEFAULT NULL,
  `last_action_time` timestamp NULL DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=3 DEFAULT CHARSET=utf8;



CREATE DATABASE `fw_demo2` /*!40100 DEFAULT CHARACTER SET utf8 */;


CREATE TABLE `demo_user` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `name` varchar(45) DEFAULT NULL,
  `content` varchar(45) DEFAULT NULL,
  `loginTime` timestamp NULL DEFAULT NULL,
  `address` varchar(45) DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;


CREATE TABLE `test_decim_tb` (
  `id` int(11) NOT NULL,
  `decim1` varchar(45) DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;


CREATE TABLE `user` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `name` varchar(45) DEFAULT NULL,
  `address` varchar(45) DEFAULT NULL,
  `content` varchar(45) DEFAULT NULL,
  `longin_time` timestamp NULL DEFAULT NULL,
  `last_action_time` timestamp NULL DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=3 DEFAULT CHARSET=utf8;
