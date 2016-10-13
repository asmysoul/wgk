
/*买手配置表*/
CREATE TABLE `buyer_config`( 
   `id` BIGINT(20) NOT NULL  AUTO_INCREMENT , 
   `buyer_id` BIGINT(20) , 
   `is_clear_view` TINYINT(4) DEFAULT '0' , 
   PRIMARY KEY (`id`)
) ENGINE=INNODB DEFAULT CHARSET=utf8 COLLATE=utf8_bin;

/*添加快递字段*/
ALTER TABLE `task`
ADD COLUMN `express_type`  enum('SELLERKD','KJKD','YDKD') NULL DEFAULT 'KJKD' AFTER `good_comment_img`;

ALTER TABLE `task`
MODIFY COLUMN `express_weight`  decimal(20,2) NULL DEFAULT NULL AFTER `delay_span`;

/*在shop表中添加店铺发货人姓名、电话、发货详细地址和发货邮编字段*/
alter table `shop` 
   add column `seller_name` varchar(20) NULL after `name`, 
   add column `mobile` varchar(20) NULL after `nick`, 
   add column `street` varchar(200) NULL after `address`, 
   add column `branch` varchar(10) NULL after `status`;
   
CREATE TABLE `fabaoguo` (
  `id` bigint(20) NOT NULL auto_increment,
  `order_sn` varchar(50) collate utf8_bin default NULL,
  `send_name` varchar(180) collate utf8_bin default NULL,
  `send_tel` varchar(50) collate utf8_bin default NULL,
  `send_addr` varchar(400) collate utf8_bin default NULL,
  `receive_name` varchar(180) collate utf8_bin default NULL,
  `receive_tel` varchar(60) collate utf8_bin default NULL,
  `receive_addr` varchar(400) collate utf8_bin default NULL,
  `weight` decimal(10,2) default NULL,
  `goods_name` varchar(100) collate utf8_bin default NULL,
  `net_no` char(6) collate utf8_bin default NULL,
  `comment` varchar(200) collate utf8_bin default NULL,
  `send_time` date default NULL,
  `status` enum('EXPRESS_PRINT','WAIT_EXPRESS_PRINT','WAIT_SEND_GOODS') collate utf8_bin default 'WAIT_SEND_GOODS',
  `batch_num` varchar(100) collate utf8_bin default NULL,
  `express_no` varchar(100) collate utf8_bin default NULL,
  PRIMARY KEY  (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_bin;
