/**
 *用户表增加“admin_id”和"docking_message"字段,用于客服对接客户信息的存储
*/
alter table `user` add column `admin_id` bigint(20) DEFAULT 0 NULL after `id`;
alter table `user` add column `docking_message` varchar(200) NULL after `admin_id`;