
/*
 * 运营【活动】表
 */
CREATE TABLE `activity` (
  `id` bigint(20) unsigned NOT NULL AUTO_INCREMENT,
  `title` varchar(80) COLLATE utf8_bin DEFAULT NULL,
  `page_url` varchar(200) COLLATE utf8_bin DEFAULT NULL,
  `banner_pic` varchar(300) COLLATE utf8_bin DEFAULT NULL,
  `start_time` datetime DEFAULT NULL,
  `end_time` datetime DEFAULT NULL,
  `status` enum('VALID','INVALID') COLLATE utf8_bin DEFAULT NULL,
  `biz_type` enum('INVITE_REG') COLLATE utf8_bin DEFAULT NULL,
  `rule_content` varchar(500) COLLATE utf8_bin DEFAULT NULL,
  `create_time` datetime DEFAULT NULL,
  `modify_time` datetime DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_bin;

/*
 * 【邀请注册】用户记录 
 */
CREATE TABLE `user_invited_record` (
  `id` bigint(20) unsigned NOT NULL AUTO_INCREMENT,
  `invite_user_id` bigint(20) DEFAULT NULL,
  `user_id` bigint(20) DEFAULT NULL,
  `member_open_time` datetime DEFAULT NULL,
  `reward_ingot` bigint(20) DEFAULT NULL,
  `create_time` datetime DEFAULT NULL,
  `modify_time` datetime DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_bin;

/*
 * 流量任务表
 *
 */
CREATE TABLE `traffic_record` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `type` enum('JDPC','TAOBAOMOBILE','TBAD','TAOBAOPC') COLLATE utf8_bin DEFAULT 'TAOBAOPC',
  `kwd` varchar(50) COLLATE utf8_bin DEFAULT NULL,
  `nid` bigint(20) DEFAULT NULL,
  `shop_type` varchar(20) COLLATE utf8_bin DEFAULT NULL,
  `times` bigint(20) DEFAULT NULL,
  `path1` int(5) DEFAULT NULL,
  `path2` int(5) DEFAULT NULL,
  `path3` int(5) DEFAULT NULL,
  `sleep_time` bigint(20) DEFAULT NULL,
  `click_start` int(5) DEFAULT NULL,
  `click_end` int(5) DEFAULT NULL,
  `begin_time` varchar(50) COLLATE utf8_bin DEFAULT NULL,
  `end_time` varchar(50) COLLATE utf8_bin DEFAULT NULL,
  `kid` bigint(20) DEFAULT NULL,
  `taskId` bigint(20) DEFAULT NULL,
  `status` enum('FINISHED','PROCESSING','WAIT') COLLATE utf8_bin DEFAULT 'WAIT',
  `return_times` bigint(20) DEFAULT NULL,
  `create_time` datetime DEFAULT NULL,
  `modify_time` datetime DEFAULT NULL,
  `user_id` bigint(20) DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_bin;


create table admin (
	id double primary key auto_increment,
	name varchar (150),
	salt varchar (30),
	password varchar (96),
	type enum('SUPERADMIN','ADMIN','SERVICE','FINANCE'),
	qq varchar (90),
	email varchar (600),
	mobile varchar (60),
	status enum('VALID','INVALID'),
	message varchar (600)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_bin; 

insert into admin ( name, salt, password, type, qq, email, mobile, status, message) values('admin','23952965','66c107ef8c1d820ea784bf675472ec68','SUPERADMIN','2581850121','a2581850121@live.com','13735534659','VALID','管理员唯一账号');
