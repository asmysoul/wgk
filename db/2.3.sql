ALTER TABLE `notice` 
   ADD COLUMN `url` VARCHAR(1000) NULL AFTER `title`;
ALTER TABLE `notice` ADD COLUMN `top_time` DATETIME NULL AFTER `modify_time`;
ALTER TABLE `admin` CHANGE `id` `id` BIGINT NOT NULL AUTO_INCREMENT;

/**
 *创建后台管理员操作表，记录管理员的后台操作
 */
create table `admin_operator_log`( 
   `id` bigint NOT NULL AUTO_INCREMENT , 
   `admin_account` varchar(50) ,
   `log_type` enum('BUYER_INGOT','SELLER_INGOT','SELLER_PLEDGE','AUDIT_SELLER','AUDIT_SELLER_FAIL','AUDIT_TASK','AUDIT_TASK_FAIL','CANCEL_TASK','CANCEL_SUB_TASK') , 
   `message` varchar(200) , 
   `operator_time` datetime , 
   PRIMARY KEY (`id`)
 ) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_bin;
 
 /**
 * 添加快递费用
 */
alter table `task` 
   add column `express_ingot` int(10) DEFAULT '0' NULL after `modify_time`;
alter table `task` 
   add column `sys_express` tinyint(1) DEFAULT '0' NULL after `express_ingot`;
UPDATE task SET express_ingot=500,sys_express=1; 