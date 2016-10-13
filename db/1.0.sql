
CREATE TABLE `admin_refund_record` (
  `id` bigint(20) unsigned NOT NULL AUTO_INCREMENT,
  `buyer_task_id` bigint(20) NOT NULL,
  `task_id` bigint(20) DEFAULT NULL,
  `buyer_id` bigint(20) DEFAULT NULL,
  `seller_id` bigint(20) DEFAULT NULL,
  `trans_no` varchar(32) COLLATE utf8_bin NOT NULL,
  `create_time` datetime DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_bin;


CREATE TABLE `buyer_account` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `nick` varchar(50) COLLATE utf8_bin DEFAULT NULL,
  `platform` enum('TAOBAO','TMALL') COLLATE utf8_bin DEFAULT NULL,
  `consignee` varchar(20) COLLATE utf8_bin DEFAULT NULL COMMENT '收货人',
  `state` varchar(20) COLLATE utf8_bin DEFAULT NULL COMMENT '省',
  `city` varchar(20) COLLATE utf8_bin DEFAULT NULL COMMENT '市',
  `region` varchar(20) COLLATE utf8_bin DEFAULT NULL COMMENT '区',
  `address` varchar(100) COLLATE utf8_bin DEFAULT NULL COMMENT '街道地址',
  `mobile` varchar(20) COLLATE utf8_bin DEFAULT NULL,
  `user_id` bigint(20) DEFAULT NULL,
  `status` enum('WAIT_EXAMINE','EXAMINING','EXAMINED','NOT_PASS') COLLATE utf8_bin DEFAULT NULL,
  `memo` varchar(5000) COLLATE utf8_bin DEFAULT NULL COMMENT '备注（审核不通过的理由）json格式',
  `create_time` datetime DEFAULT NULL,
  `modify_time` datetime DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_bin;


CREATE TABLE `buyer_task` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `task_id` bigint(20) DEFAULT NULL COMMENT '卖家发布的任务（父任务ID）',
  `seller_id` bigint(20) DEFAULT NULL,
  `buyer_id` bigint(20) DEFAULT NULL,
  `buyer_account_id` bigint(20) DEFAULT NULL,
  `buyer_account_nick` varchar(50) COLLATE utf8_bin DEFAULT NULL COMMENT '买号',
  `message_id` bigint(20) DEFAULT NULL,
  `pledge_ingot` bigint(20) DEFAULT NULL COMMENT '接手任务的押金',
  `reward_ingot` bigint(20) DEFAULT NULL COMMENT '任务佣金',
  `experience` int(11) DEFAULT NULL COMMENT '任务经验值',
  `order_id` varchar(50) COLLATE utf8_bin DEFAULT NULL,
  `express_no` varchar(50) COLLATE utf8_bin DEFAULT NULL,
  `express_company` varchar(50) COLLATE utf8_bin DEFAULT NULL,
  `paid_fee` bigint(20) DEFAULT NULL,
  `trans_no` varchar(50) COLLATE utf8_bin DEFAULT NULL,
  `device` enum('PC','MOBILE') COLLATE utf8_bin DEFAULT NULL,
  `status` enum('RECIEVED','WAIT_PAY','ITEM_COMPARE','VIEW_AND_INQUIRY','ORDER_AND_PAY','WAIT_SEND_GOODS','EXPRESS_PRINT','WAIT_CONFIRM','CONFIRM_AND_COMMENT','WAIT_REFUND','REFUNDING','FINISHED','CANCLED','WAIT_EXPRESS_PRINT','WAIT_PRINT') COLLATE utf8_bin DEFAULT NULL COMMENT '任务状态，参看TaskStatus',
  `take_time` datetime DEFAULT NULL COMMENT '接手时间',
  `start_time` datetime DEFAULT NULL,
  `finish_time` datetime DEFAULT NULL,
  `modify_time` datetime DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_bin;

CREATE TABLE `buyer_task_step` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `buyer_task_id` bigint(20) DEFAULT NULL,
  `buyer_id` bigint(20) DEFAULT NULL,
  `type` enum('CHOOSE_ITEM','VIEW_AND_INQUIRY','ORDER_AND_PAY','SEND_GOODS','CONFIRM_AND_COMMENT','REFUND','UNLOCK_PLEGE','CONFIRM_REFUND') COLLATE utf8_bin DEFAULT NULL COMMENT '完成任务的步骤',
  `no` int(11) DEFAULT NULL COMMENT '步骤序号，页面展示时排序用',
  `content` varchar(5000) COLLATE utf8_bin DEFAULT NULL COMMENT '该步骤执行结果：json格式保存',
  `create_time` datetime DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_bin;

CREATE TABLE `buyer_experience_record` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `user_id` bigint(20) DEFAULT NULL,
  `amount` bigint(20) DEFAULT NULL,
  `balance` bigint(20) DEFAULT NULL,
  `sign` enum('MINUS','PLUS') COLLATE utf8_bin DEFAULT NULL,
  `memo` varchar(300) COLLATE utf8_bin DEFAULT NULL,
  `create_time` datetime DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_bin;


CREATE TABLE `fund_account` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `user_id` bigint(20) DEFAULT NULL,
  `name` varchar(30) COLLATE utf8_bin DEFAULT NULL,
  `no` varchar(50) COLLATE utf8_bin DEFAULT NULL,
  `type` enum('ALIPAY','TENPAY','ICBC','ABC','BOC','CCB','CMB','BCM','CMBC','COMM','SDB','CITIC','GDB','SPDB','CEB','PAB','CIB','BOB') COLLATE utf8_bin DEFAULT NULL,
  `opening_bank` varchar(50) COLLATE utf8_bin DEFAULT NULL,
  `address` varchar(50) COLLATE utf8_bin DEFAULT NULL,
  `create_time` datetime DEFAULT NULL,
  `modify_time` datetime DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_bin;


CREATE TABLE `member_charge_record` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `user_id` bigint(20) DEFAULT NULL,
  `user_type` enum('BUYER','SELLER') COLLATE utf8_bin DEFAULT NULL,
  `amount` bigint(20) DEFAULT NULL,
  `ingot` bigint(20) DEFAULT NULL,
  `month` int(11) DEFAULT NULL,
  `start_time` datetime DEFAULT NULL,
  `end_time` datetime DEFAULT NULL,
  `memo` varchar(100) COLLATE utf8_bin DEFAULT NULL,
  `create_time` datetime DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB  DEFAULT CHARSET=utf8 COLLATE=utf8_bin;


CREATE TABLE `notice` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `type` enum('COURSE','DEFAULT') COLLATE utf8_bin DEFAULT NULL,
  `title` varchar(1000) COLLATE utf8_bin DEFAULT NULL,
  `content` varchar(5000) COLLATE utf8_bin DEFAULT NULL,
  `is_display` tinyint(4) DEFAULT NULL,
  `role` enum('ALL','SELLER','BUYER') COLLATE utf8_bin DEFAULT NULL,
  `sort_num` int(11) DEFAULT NULL,
  `admin_id` bigint(20) DEFAULT NULL,
  `create_time` datetime DEFAULT NULL,
  `modify_time` datetime DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_bin;


create table `pay_trade_log` (
  `id` bigint(20) unsigned not null auto_increment,
  `trade_no` varchar(32) collate utf8_bin default null comment '格式：yyyyMMddHHmmss+id组成',
  `type` enum('TASK','PLEDGE','MEMBER','INGOT') collate utf8_bin default null,
  `biz_id` bigint(20) default null,
  `biz_member_month` int(11) default null,
  `amount` bigint(20) default null,
  `fee` bigint(20) default null comment '商户手续费',
  `user_id` bigint(20) default null,
  `deal_id` varchar(30) collate utf8_bin default null,
  `bank_deal_id` varchar(30) collate utf8_bin default null,
  `bank` enum('ICBC','ABC','BOC','CCB','CMB','BCM','CMBC','COMM','SDB','CITIC','GDB','SPDB','CEB','PAB','CIB','BOB') collate utf8_bin default null,
  `result` enum('OK','FAIL','UNTREATED') collate utf8_bin not null comment '交易结果',
  `memo` varchar(100) collate utf8_bin default null,
  `withdraw_amount` bigint(20) default null,
  `create_time` datetime default null,
  `modify_time` datetime default null,
  primary key (`id`)
) engine=innodb default charset=utf8 collate=utf8_bin;


CREATE TABLE `region` (
  `id` int(11) unsigned NOT NULL,
  `name` varchar(30) COLLATE utf8_bin DEFAULT NULL COMMENT '地区名',
  `parent_id` int(11) DEFAULT NULL COMMENT '父地区ID',
  `type` tinyint(4) DEFAULT NULL,
  `zip` varchar(15) COLLATE utf8_bin DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_bin;


CREATE TABLE `seller_pledge_record` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `seller_id` bigint(20) DEFAULT NULL,
  `task_id` bigint(20) DEFAULT NULL,
  `amount` bigint(20) DEFAULT NULL,
  `sign` enum('PLUS','MINUS') COLLATE utf8_bin DEFAULT NULL COMMENT '符号：加、减',
  `action` enum('RECHARGE','LOCK','UNLOCK','EXCHANGE_INGOT','PAY_TASK','MEMBER','WITHDRAW','DEDUCT') COLLATE utf8_bin DEFAULT NULL,
  `is_withdraw` tinyint(4) DEFAULT NULL,
  `balance` bigint(1) DEFAULT NULL COMMENT '账户结余',
  `memo` varchar(50) COLLATE utf8_bin DEFAULT NULL,
  `create_time` datetime DEFAULT NULL,
  `modify_time` datetime DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_bin;


CREATE TABLE `shop` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `seller_id` bigint(20) DEFAULT NULL,
  `platform` enum('TAOBAO','TMALL') COLLATE utf8_bin DEFAULT NULL,
  `url` varchar(200) COLLATE utf8_bin DEFAULT NULL,
  `name` varchar(50) COLLATE utf8_bin DEFAULT NULL,
  `nick` varchar(50) COLLATE utf8_bin DEFAULT NULL,
  `address` varchar(50) COLLATE utf8_bin DEFAULT NULL,
  `status` enum('VALID','INVALID','LOCKED') COLLATE utf8_bin DEFAULT NULL,
  `create_time` datetime DEFAULT NULL,
  `modify_time` datetime DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_bin;


CREATE TABLE `task` (
  `id` bigint(20) unsigned NOT NULL COMMENT '任务编号',
  `platform` enum('TAOBAO','TMALL') COLLATE utf8_bin DEFAULT NULL,
  `type` enum('ORDER','JHS','SUBWAY') COLLATE utf8_bin DEFAULT NULL,
  `shop_id` bigint(20) DEFAULT NULL,
  `shop_name` varchar(50) COLLATE utf8_bin DEFAULT NULL,
  `item_id` varchar(50) COLLATE utf8_bin DEFAULT NULL,
  `item_title` varchar(100) COLLATE utf8_bin DEFAULT NULL,
  `item_url` varchar(300) COLLATE utf8_bin DEFAULT NULL,
  `item_props` varchar(100) COLLATE utf8_bin DEFAULT NULL,
  `item_price` int(11) DEFAULT NULL,
  `item_display_price` int(11) DEFAULT NULL,
  `item_buy_num` int(11) NOT NULL DEFAULT '0' COMMENT '每单购买件数',
  `item_pic_url` varchar(500) COLLATE utf8_bin DEFAULT NULL,
  `item_pic` varchar(200) COLLATE utf8_bin DEFAULT NULL,
  `item_subway_pic_url` varchar(500) COLLATE utf8_bin DEFAULT NULL,
  `item_subway_pic` varchar(200) COLLATE utf8_bin DEFAULT NULL,
  `item_search_min_price` int(11) DEFAULT NULL,
  `item_search_max_price` int(11) DEFAULT NULL,
  `item_search_location` varchar(20) COLLATE utf8_bin DEFAULT NULL,
  `additional_item_title` varchar(100) COLLATE utf8_bin DEFAULT NULL,
  `additional_item_url` varchar(200) COLLATE utf8_bin DEFAULT NULL,
  `additional_item_props` varchar(50) COLLATE utf8_bin DEFAULT NULL,
  `additional_item_price` int(11) DEFAULT NULL,
  `additional_item_buy_num` int(11) DEFAULT NULL,
  `additional_item_search_keyword` varchar(50) COLLATE utf8_bin DEFAULT NULL,
  `additional_item_pic_url` varchar(200) COLLATE utf8_bin DEFAULT NULL,
  `additional_item_pic` varchar(200) COLLATE utf8_bin DEFAULT NULL,
  `is_free_shipping` tinyint(4) DEFAULT NULL COMMENT '是否包邮',
  `order_messages` varchar(200) COLLATE utf8_bin DEFAULT NULL COMMENT '已废弃：订单留言：多个以,分隔',
  `total_order_num` int(11) unsigned NOT NULL DEFAULT '0' COMMENT '订单总数（买家任务数）',
  `pc_order_num` int(11) unsigned NOT NULL DEFAULT '0',
  `mobile_order_num` int(11) unsigned NOT NULL DEFAULT '0' COMMENT '移动端的订单数量',
  `extra_reward_ingot` int(11) unsigned NOT NULL DEFAULT '0' COMMENT '加赏佣金',
  `speed_task_ingot` int(11) unsigned NOT NULL DEFAULT '0' COMMENT '加速任务的佣金',
  `speed_examine` int(11) unsigned NOT NULL DEFAULT '0' COMMENT '是否加速审核：0,1',
  `publish_timer_interval` int(11) DEFAULT NULL COMMENT '单位：分钟',
  `publish_timer_value` int(11) DEFAULT NULL,
  `last_batch_publish_time` datetime DEFAULT NULL,
  `buy_time_interval` int(11) DEFAULT NULL COMMENT '单位：月',
  `good_comment_words` varchar(500) COLLATE utf8_bin DEFAULT NULL,
  `pledge` bigint(20) unsigned NOT NULL DEFAULT '0' COMMENT '每单押金:商品价格+退款保证金+运费押金',
  `total_pledge` bigint(20) unsigned NOT NULL DEFAULT '0' COMMENT '本次任务押金',
  `base_order_ingot` bigint(20) unsigned NOT NULL DEFAULT '0' COMMENT '每单服务费用:该值的90%为买手佣金',
  `vas_ingot` bigint(20) DEFAULT NULL,
  `total_ingot` bigint(20) unsigned NOT NULL DEFAULT '0' COMMENT '平台服务费总计',
  `status` enum('WAIT_EDIT','WAIT_EXAMINE','EXAMINING','WAIT_PUBLISH','PUBLISHED','NOT_PASS','FINISHED','CANCLED') COLLATE utf8_bin DEFAULT NULL,
  `seller_id` bigint(20) DEFAULT NULL,
  `seller_nick` varchar(60) COLLATE utf8_bin DEFAULT NULL,
  `pc_taken_count` int(11) unsigned NOT NULL DEFAULT '0',
  `mobile_taken_count` int(11) unsigned NOT NULL DEFAULT '0',
  `finished_count` int(11) unsigned NOT NULL DEFAULT '0' COMMENT '已完成的任务数量',
  `is_paid` tinyint(4) DEFAULT NULL,
  `publish_time` datetime DEFAULT NULL,
  `examine_time` datetime DEFAULT NULL COMMENT '审核时间',
  `create_time` datetime DEFAULT NULL,
  `modify_time` datetime DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_bin;

CREATE TABLE `task_examine_log` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `admin_id` bigint(20) DEFAULT NULL,
  `task_id` bigint(20) DEFAULT NULL,
  `is_pass` tinyint(4) DEFAULT NULL,
  `memo` varchar(800) COLLATE utf8_bin DEFAULT NULL,
  `start_time` datetime DEFAULT NULL,
  `finish_time` datetime DEFAULT NULL,
  `create_time` datetime DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_bin;

CREATE TABLE `task_item_search_plan` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `task_id` bigint(20) DEFAULT NULL,
  `buyer_task_id` bigint(20) DEFAULT NULL,
  `word` varchar(50) COLLATE utf8_bin DEFAULT NULL,
  `skus` varchar(100) COLLATE utf8_bin DEFAULT NULL COMMENT '筛选分类，多个以,分隔',
  `in_tmall` tinyint(1) DEFAULT NULL COMMENT '是否天猫搜索',
  `create_time` datetime DEFAULT NULL,
  `modify_time` datetime DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_bin;

CREATE TABLE `task_order_message` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `task_id` bigint(20) DEFAULT NULL,
  `message` varchar(500) COLLATE utf8_bin DEFAULT NULL,
  `used_num` int(12) DEFAULT NULL,
  `create_time` datetime DEFAULT NULL,
  `modify_time` datetime DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_bin;


CREATE TABLE `user` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `nick` varchar(50) DEFAULT NULL,
  `active_code` varchar(50) DEFAULT NULL COMMENT '注册激活码',
  `password` varchar(32) DEFAULT NULL,
  `salt` varchar(10) DEFAULT NULL,
  `last_login_time` datetime DEFAULT NULL,
  `status` enum('INACTIVE','ACTIVE','INVALID','VALID') DEFAULT NULL,
  `type` enum('BUYER','SELLER') DEFAULT NULL,
  `avatar` varchar(200) DEFAULT NULL,
  `qq` varchar(30) DEFAULT NULL,
  `email` varchar(60) DEFAULT NULL,
  `mobile` varchar(20) DEFAULT NULL,
  `level` int(11) DEFAULT NULL COMMENT '会员等级',
  `ingot` bigint(20) DEFAULT NULL COMMENT '已废弃，元宝',
  `pledge` bigint(20) DEFAULT NULL COMMENT '已废弃，【卖家】账户押金',
  `experience` bigint(20) DEFAULT NULL COMMENT '已废弃，【买手】经验值',
  `pay_password` varchar(32) DEFAULT NULL COMMENT '支付密码',
  `due_time` date DEFAULT NULL COMMENT '会员到期时间',
  `regist_time` datetime DEFAULT NULL,
  `active_code_create_time` datetime DEFAULT NULL,
  `modify_time` datetime DEFAULT NULL,
  `create_time` datetime DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_bin;

CREATE TABLE `user_ingot_record` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `user_id` bigint(20) DEFAULT NULL,
  `task_id` bigint(20) DEFAULT NULL,
  `amount` bigint(20) DEFAULT NULL,
  `is_reward` tinyint(4) DEFAULT NULL,
  `sign` enum('PLUS','MINUS') COLLATE utf8_bin DEFAULT NULL COMMENT '符号：加、减',
  `balance` bigint(1) DEFAULT NULL COMMENT '账户结余',
  `memo` varchar(300) COLLATE utf8_bin DEFAULT NULL,
  `create_time` datetime DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_bin;

CREATE TABLE `user_login_log` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `user_id` bigint(20) DEFAULT NULL COMMENT '用户id',
  `nick` varchar(50) DEFAULT NULL COMMENT '用户昵称',
  `ip` varchar(40) DEFAULT NULL,
  `hardware` varchar(200) DEFAULT NULL COMMENT '硬件信息',
  `create_time` datetime DEFAULT NULL COMMENT '登陆时间',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_bin;

CREATE TABLE `user_withdraw_record` (
  `id` bigint(20) unsigned NOT NULL AUTO_INCREMENT,
  `user_id` bigint(20) DEFAULT NULL,
  `fund_account_id` bigint(20) DEFAULT NULL,
  `apply_amount` bigint(20) DEFAULT NULL,
  `amount` bigint(20) DEFAULT NULL,
  `trade_no` varchar(50) COLLATE utf8_bin DEFAULT NULL,
  `status` enum('WAIT','FINISHED') COLLATE utf8_bin DEFAULT NULL,
  `memo` varchar(50) COLLATE utf8_bin DEFAULT NULL,
  `apply_time` datetime DEFAULT NULL,
  `modify_time` datetime DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_bin;
