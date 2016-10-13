/*商家个性配置表*/
CREATE TABLE `seller_config`( 
   `id` BIGINT(20) NOT NULL AUTO_INCREMENT , 
   `seller_id` BIGINT(20) , 
   `buyer_and_seller_time` BIGINT(20) , 
   `buyer_and_shop_time` BIGINT(20) , 
   `buyer_acount_and_shop_time` BIGINT(20) , 
   `buyer_acount_and_item_time` BIGINT(20) , 
   PRIMARY KEY (`id`)
) ENGINE=INNODB DEFAULT CHARSET=utf8 COLLATE=utf8_bin;

/*管理员操作金币表*/
create table `admin_trade_log`( 
   `id` bigint(20) NOT NULL AUTO_INCREMENT , 
   `type` enum('BUYER_INGOT','SELLER_INGOT','SELLER_PLEDGE') , 
   `sign` enum('PLUS','MINUS') , 
   `amount` bigint(20) , 
   `user_id` bigint(20) , 
   `result` varchar(200) DEFAULT 'OK' , 
   `memo` varchar(400) , 
   `create_time` datetime , 
   `admin_id` bigint(20) , 
   PRIMARY KEY (`id`)
) ENGINE=INNODB DEFAULT CHARSET=utf8 COLLATE=utf8_bin;

/*买手买号表里增加排序字段*/
alter table `buyer_account` 
   add column `order_number` bigint(20) DEFAULT '0' NULL after `modify_time`;
