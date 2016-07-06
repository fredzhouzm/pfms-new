DROP TABLE IF EXISTS `pfmsdb_form`;
CREATE TABLE `pfmsdb_form` (
  `ID` varchar(20) NOT NULL COMMENT '单据号',
  `VALUE_DATE` date NOT NULL COMMENT '单据日期',
  `TIME_NO` char(2) NOT NULL COMMENT '时段',
  `AMOUNT` decimal(15,2) DEFAULT '0.00' COMMENT '金额',
  `USAGE_LEVEL_ONE` varchar(20) NOT NULL COMMENT '一级科目',
  `USAGE_LEVEL_TWO` varchar(25) NOT NULL COMMENT '二级科目',
  `CREATOR_ID` int(15) NOT NULL COMMENT '创建者ID',
  `CREATE_TIME` datetime NOT NULL COMMENT '创建时间',
  `MODIFIER_ID` int(15) DEFAULT NULL COMMENT '修改者ID',
  `MODIFY_TIME` datetime DEFAULT NULL COMMENT '修改时间',
  `REMARK` varchar(256) DEFAULT NULL COMMENT '备注',
  `TYPE` char(1) NOT NULL COMMENT '种类',
  PRIMARY KEY (`ID`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

DROP TABLE IF EXISTS `pfmsdb_realstatistics`;
CREATE TABLE `pfmsdb_realstatistics` (
  `ID` varchar(25) NOT NULL,
  `MONTH` varchar(8) NOT NULL,
  `BUDGET` decimal(15,2) NOT NULL,
  `REALAMOUNT` decimal(15,2) NOT NULL DEFAULT '0.00',
  PRIMARY KEY (`ID`,`MONTH`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

DROP TABLE IF EXISTS `pfmsdb_sys`;
CREATE TABLE `pfmsdb_sys` (
  `SYS_ID` char(10) NOT NULL COMMENT '系统参数ID',
  `SYS_NAME` varchar(40) DEFAULT NULL COMMENT '系统参数名称',
  `SYS_VALUE` varchar(40) NOT NULL COMMENT '系统参数值',
  `SYS_DESC` varchar(1024) DEFAULT NULL COMMENT '系统参数描述',
  PRIMARY KEY (`SYS_ID`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

DROP TABLE IF EXISTS `pfmsdb_usage_level_one`;
CREATE TABLE `pfmsdb_usage_level_one` (
  `ID` varchar(20) NOT NULL COMMENT '科目ID',
  `TYPE` char(1) NOT NULL COMMENT '科目类型',
  `NAME` varchar(40) NOT NULL COMMENT '科目名称',
  `CREATOR_ID` int(15) NOT NULL COMMENT '创建者ID',
  `CREATE_TIME` datetime NOT NULL COMMENT '创建时间',
  `DESCRIPT` varchar(256) DEFAULT NULL COMMENT '描述',
  `MONTHBUDGET` decimal(15,2) DEFAULT NULL,
  PRIMARY KEY (`ID`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

DROP TABLE IF EXISTS `pfmsdb_usage_level_two`;
CREATE TABLE `pfmsdb_usage_level_two` (
  `ID` varchar(25) NOT NULL COMMENT '科目ID',
  `TYPE` char(1) NOT NULL COMMENT '科目类型',
  `FATHER_ID` varchar(20) NOT NULL COMMENT '父科目ID',
  `NAME` varchar(40) NOT NULL COMMENT '科目名称',
  `CREATOR_ID` int(15) NOT NULL COMMENT '创建者ID',
  `CREATE_TIME` datetime NOT NULL COMMENT '创建时间',
  `DESCRIPT` varchar(256) DEFAULT NULL COMMENT '描述',
  `MONTHBUDGET` decimal(15,2) DEFAULT NULL,
  PRIMARY KEY (`ID`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

DROP TABLE IF EXISTS `pfmsdb_user`;
CREATE TABLE `pfmsdb_user` (
  `ID` int(15) unsigned zerofill NOT NULL AUTO_INCREMENT COMMENT '用户ID',
  `LOGIN_NAME` varchar(32) NOT NULL COMMENT '登录账号名称',
  `PASSWORD` varchar(256) NOT NULL COMMENT '登录账号密码',
  `NAME` varchar(40) NOT NULL COMMENT '昵称',
  `FIRST_LOGIN` char(1) NOT NULL COMMENT '是否第一次登陆',
  `GENDER` char(1) NOT NULL COMMENT '性别',
  `BIRTH_DATE` date NOT NULL COMMENT '出生日期',
  `MAIL_ADDRESS` varchar(128) DEFAULT NULL COMMENT 'E-mail地址',
  `USER_TYPE` char(1) NOT NULL COMMENT '用户类型',
  `TIMESTAMP` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '时间戳',
  PRIMARY KEY (`ID`)
) ENGINE=InnoDB AUTO_INCREMENT=6 DEFAULT CHARSET=utf8;

DROP TABLE IF EXISTS `sequence`;
CREATE TABLE `sequence` (
  `SEQ_NAME` varchar(50) NOT NULL COMMENT '序列号名称',
  `DATE` char(8) NOT NULL,
  `CURRENT_VAL` int(11) NOT NULL COMMENT '当前值',
  `INCREMENT_VAL` int(11) NOT NULL DEFAULT '1' COMMENT '递增值',
  PRIMARY KEY (`SEQ_NAME`,`DATE`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

DROP VIEW IF EXISTS `pfmsvm_form`;
CREATE VIEW `pfmsvm_form` AS select `form`.`TYPE` AS `TYPE`,`form`.`ID` AS `ID`,`form`.`VALUE_DATE` AS `VALUE_DATE`,`form`.`TIME_NO` AS `TIME_NO`,`form`.`AMOUNT` AS `AMOUNT`,`form`.`USAGE_LEVEL_ONE` AS `USAGE_LEVEL_ONE`,`u1`.`NAME` AS `USAGE_LEVEL_ONE_NAME`,`form`.`USAGE_LEVEL_TWO` AS `USAGE_LEVEL_TWO`,`u2`.`NAME` AS `USAGE_LEVEL_TWO_NAME`,`form`.`CREATOR_ID` AS `CREATOR_ID`,`form`.`CREATE_TIME` AS `CREATE_TIME`,`form`.`MODIFIER_ID` AS `MODIFIER_ID`,`form`.`MODIFY_TIME` AS `MODIFY_TIME`,`form`.`REMARK` AS `REMARK` from ((`pfmsdb_form` `form` join `pfmsdb_usage_level_one` `u1`) join `pfmsdb_usage_level_two` `u2`) where ((`form`.`USAGE_LEVEL_ONE` = `u1`.`ID`) and (`form`.`USAGE_LEVEL_TWO` = `u2`.`ID`)) ;

DROP VIEW IF EXISTS `pfmsvm_proset`;
CREATE VIEW `pfmsvm_proset` AS select `re`.`ID` AS `ID`,`re`.`MONTH` AS `MONTH`,`u1`.`NAME` AS `NAME`,`u1`.`TYPE` AS `TYPE`,'level1' AS `LEVEL`,`re`.`BUDGET` AS `MONTHBUDGET`,`re`.`REALAMOUNT` AS `REALAMOUNT` from (`pfmsdb_realstatistics` `re` join `pfmsdb_usage_level_one` `u1`) where (`re`.`ID` = `u1`.`ID`) union select `re`.`ID` AS `ID`,`re`.`MONTH` AS `MONTH`,`u2`.`NAME` AS `NAME`,`u2`.`TYPE` AS `TYPE`,'level2' AS `LEVEL`,`re`.`BUDGET` AS `MONTHBUDGET`,`re`.`REALAMOUNT` AS `REALAMOUNT` from (`pfmsdb_realstatistics` `re` join `pfmsdb_usage_level_two` `u2`) where (`re`.`ID` = `u2`.`ID`) ;
commit;
