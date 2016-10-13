ALTER TABLE `buyer_task`
ADD COLUMN `search_plan_id`  bigint(20) NULL AFTER `modify_time`;

ALTER TABLE `task_item_search_plan`
ADD COLUMN `taken_num`  bigint(10) NULL AFTER `in_tmall`,
ADD COLUMN `total_num`  bigint(10) NULL AFTER `taken_num`,
ADD COLUMN `flow_num`  bigint(20) NULL AFTER `total_num`;

ALTER TABLE `task`
ADD COLUMN `express_weight`  bigint(20) NULL AFTER `delay_span`,
ADD COLUMN `task_request`  varchar(500) NULL AFTER `express_weight`;

CREATE TABLE `user_flow_record` (
  `id` bigint(20) NOT NULL auto_increment,
  `user_id` bigint(20) default NULL,
  `task_id` bigint(20) default NULL,
  `amount` bigint(20) default NULL,
  `sign` enum('PLUS','MINUS') collate utf8_bin default NULL,
  `balance` bigint(20) default NULL,
  `memo` varchar(200) collate utf8_bin default NULL,
  `create_time` datetime default NULL,
  PRIMARY KEY  (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_bin;


/**
 *创建邀请用户的金币奖励记录表
 */
create table `task_reward_log`( 
   `id` bigint NOT NULL AUTO_INCREMENT , 
   `invite_user_id` bigint(20) ,
   `user_id` bigint(20) , 
   `task_id` bigint(20) , 
   `task_finished_time` datetime ,
   `memo` varchar(200) ,
   `reward_ingot` bigint(20) , 
   PRIMARY KEY (`id`)
 ) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_bin;