
/*
 * 【任务】增加“平台直接返款”标记
 */
alter table `task`
   add column `sys_refund` tinyint(1) DEFAULT '0' NULL after `is_paid`;

/*
 * 【买手任务】增加“平台返款”相关状态 、本金提现申请流水号
 */
alter table `buyer_task` 
  change `status` `status` enum (
    'RECIEVED',
    'WAIT_PAY',
    'ITEM_COMPARE',
    'VIEW_AND_INQUIRY',
    'ORDER_AND_PAY',
    'WAIT_SEND_GOODS',
    'EXPRESS_PRINT',
    'WAIT_CONFIRM',
    'CONFIRM_AND_COMMENT',
    'WAIT_REFUND',
    'REFUNDING',
    'FINISHED',
    'CANCLED',
    'WAIT_EXPRESS_PRINT',
    'WAIT_PRINT',
    'WAIT_SELLER_CONFIRM_SYS_REFUND',
    'WAIT_BUYER_CONFIRM_EDITED_SYS_REFUND',
    'WAIT_BUYER_CONFIRM_SYS_REFUND',
    'BUYER_REJECT_SYS_REFUND',
    'WAIT_SYS_REFUND'
  ) character set utf8 collate utf8_bin null comment '任务状态，参看TaskStatus',
  add column `user_withdraw_record_sn` bigint NULL after `status`;

/**
 * 【买手任务进度】增加是否“有效”的标记
 */
alter table `buyer_task_step` 
   add column `is_valid` tinyint(1) DEFAULT '1' null after `content`, 
   add column `modify_time` datetime null after `create_time`;
   
/*
 * 【买手本金记录】用于平台返款任务的转账及提现
 */
CREATE TABLE `buyer_deposit_record` (
  `id` bigint(20) unsigned NOT NULL AUTO_INCREMENT,
  `user_id` bigint(20) DEFAULT NULL,
  `buyer_task_id` bigint(20) DEFAULT NULL,
  `task_id` bigint(20) DEFAULT NULL,
  `amount` bigint(20) DEFAULT NULL,
  `sign` enum('PLUS','MINUS') COLLATE utf8_bin DEFAULT NULL,
  `balance` bigint(20) DEFAULT NULL,
  `memo` varchar(50) COLLATE utf8_bin DEFAULT NULL,
  `create_time` datetime DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=3 DEFAULT CHARSET=utf8 COLLATE=utf8_bin;


/*
 * 【卖家押金记录】增加“平台返款”任务的冻结、解冻、扣款动作，以及扣款标记
 */
alter table `seller_pledge_record` 
   add column `is_deduct` tinyint(1) DEFAULT '0' NULL after `is_withdraw`,
   change `action` `action` enum('RECHARGE','LOCK','UNLOCK','EXCHANGE_INGOT','PAY_TASK','MEMBER','WITHDRAW','DEDUCT','LOCK_SYS_REFUND','UNLOCK_SYS_REFUND','DEDUCT_SYS_REFUND')
   character set utf8 collate utf8_bin NULL;
   
/*
 * 【提现记录】增加“买手提现垫付本金”的标记字段
 */
alter table `user_withdraw_record` 
   add column `sn` bigint NULL after `id`, 
   add column `is_buyer_deposit` tinyint(1) NULL after `memo`,
   change `user_id` `user_id` bigint(20) NULL;
