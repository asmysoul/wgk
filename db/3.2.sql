CREATE TABLE `sys_config` (
  `id` bigint(20) NOT NULL auto_increment,
  `key` enum('BUYER_TASK_MONTH_COUNT','BUYER_TASK_WEEK_COUNT','BUYER_TASK_DAY_COUNT','BUYER_ACOUNT_AND_ITEM_TIME','BUYER_ACOUNT_AND_SHOP_TIME','BUYER_AND_SELLER_TIME','BUYER_AND_SHOP_TIME') collate utf8_bin default NULL,
  `value` varchar(100) collate utf8_bin default NULL,
  `record` varchar(500) collate utf8_bin default NULL,
  `modify_time` date default NULL,
  PRIMARY KEY  (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_bin;

INSERT INTO `sys_config` VALUES ('1', 'BUYER_TASK_MONTH_COUNT', '90', '平台限制每个【买号】接单数量:90单/月 ', '2015-03-27');
INSERT INTO `sys_config` VALUES ('2', 'BUYER_TASK_WEEK_COUNT', '30', '平台限制每个【买号】接单数量:30单/周', '2015-03-27');
INSERT INTO `sys_config` VALUES ('3', 'BUYER_TASK_DAY_COUNT', '5', '平台限制每个【买号】接单数量:5单/天', '2015-03-27');
INSERT INTO `sys_config` VALUES ('4', 'BUYER_ACOUNT_AND_ITEM_TIME', '60', '同一个买号（旺旺号）接同一个商品（根据商品ID判断）：需要60天', '2015-03-27');
INSERT INTO `sys_config` VALUES ('5', 'BUYER_ACOUNT_AND_SHOP_TIME', '20', '同一个买号（旺旺号）接同一个店铺（根据店铺ID判断）：需要20天', '2015-03-27');
INSERT INTO `sys_config` VALUES ('6', 'BUYER_AND_SHOP_TIME', '10', '同一个账号（挖顾客账号），接同一个店铺（根据店铺ID判断）：需要间隔10天。', '2015-03-27');
INSERT INTO `sys_config` VALUES ('7', 'BUYER_AND_SELLER_TIME', '7', '同一个账号（挖顾客账号），接同一个商家（根据商家ID判断）：需要间隔7天。', '2015-03-27');
/*task添加字段*/
ALTER TABLE `task`
ADD COLUMN `good_comment_img`  varchar(500) NULL AFTER `task_request`;
