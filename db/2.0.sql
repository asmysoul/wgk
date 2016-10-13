/*
 * user_withdraw_record 表status 添加枚举类型  PROCESSING
 */
alter table `user_withdraw_record` 
   change `status` `status` enum('WAIT','FINISHED','PROCESSING') character set utf8 collate utf8_bin NULL; 
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
