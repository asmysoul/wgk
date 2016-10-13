
/*
 * [Fix bug]交通银行、浙商银行代码错误
 */
alter table `pay_trade_log` 
   change `bank` `bank` enum('CMB','ICBC','ABC','CCB','BOC','SPDB','BCOM','CMBC','SDB','GDB','CITIC','HXB','CIB','GZCB','SRCB','BOB','CBHB','BJRCB','NJCB','CEB','BEA','NBCB','HZB','PAB','HSB','CZB','SHB','POST','JSB') character set utf8 collate utf8_bin NULL;