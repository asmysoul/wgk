/*
 * 部分用户无法绑定银行卡
 */
alter table `fund_account` 
   change `type` `type` enum('ALIPAY','TENPAY','ICBC','ABC','BOC','CCB','CMB','BCM','CMBC','COMM','SDB','CITIC','GDB','SPDB','CEB','PAB','CIB','BOB','HXB','GZCB','SRCB','CBHB','BJRCB','NJCB','BEA','NBCB','HZB','HSB','ZSB','SHB','POST','JSB') character set utf8 collate utf8_bin NULL;