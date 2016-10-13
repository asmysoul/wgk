
/*
 * 【任务】增加‘待支付’状态
 */
alter table `task` 
   change `status` `status` enum('WAIT_EDIT','WAITING_PAY','WAIT_EXAMINE','EXAMINING','WAIT_PUBLISH','PUBLISHED','NOT_PASS','FINISHED','CANCLED') character set utf8 collate utf8_bin NULL;