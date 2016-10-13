/**
 *用户表增加“vip_status”字段
*/
alter table `user` 
   add column `vip_status` enum('LOW','NORMAL','VIP1','VIP2','VIP3','SPECIAL') DEFAULT 'NORMAL' NULL after `type`;
   
/**
 *管理员操作表中“操作类型”列增加“CHANGE_USER”枚举
 */
 alter table `admin_operator_log` 
   change `log_type` `log_type` enum('BUYER_INGOT','SELLER_INGOT','SELLER_PLEDGE','AUDIT_SELLER','AUDIT_SELLER_FAIL','AUDIT_TASK','AUDIT_TASK_FAIL','CANCEL_TASK','CANCEL_SUB_TASK','CHANGE_USER') character set utf8 collate utf8_bin NULL;
   
/**
 *用户状态增加冻结用户状态
 */
alter table `user` 
   change `status` `status` enum('INACTIVE','ACTIVE','INVALID','VALID','LOCKED') character set utf8 collate utf8_general_ci NULL;  

/**
 *任务增加针对VIP用户延时发布任务的“延时发布任务时间”列
 */
alter table `task` 
   add column `delay_span` int(11) DEFAULT '0' NULL after `sys_express`;
/*
 * 增加京东，蘑菇街，等平台的支持。
 */
alter table `task` 
   change `platform` `platform` enum('TAOBAO','TMALL','JD','MOGUJIE','YHD','JUMEI','AMAZON','DANGDANG','QQ','ALIBABA','MEILISHUO','GUOMEI','SUNING') character set utf8 collate utf8_bin NULL;

alter table `shop` 
   change `platform` `platform` enum('TAOBAO','TMALL','JD','MOGUJIE','YHD','JUMEI','AMAZON','DANGDANG','QQ','ALIBABA','MEILISHUO','GUOMEI','SUNING') character set utf8 collate utf8_bin NULL;

alter table `buyer_account` 
   change `platform` `platform` enum('TAOBAO','TMALL','JD','MOGUJIE','YHD','JUMEI','AMAZON','DANGDANG','QQ','ALIBABA','MEILISHUO','GUOMEI','SUNING') character set utf8 collate utf8_bin NULL;
