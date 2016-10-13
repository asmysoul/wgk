/*
 * 【押金记录】增加memo字段长度
 */
alter table `seller_pledge_record` 
   change `memo` `memo` varchar(500) character set utf8 collate utf8_bin NULL;