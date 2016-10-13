
alter table `pay_trade_log` 
   change `bank` `bank` enum('CMB','ICBC','ABC','CCB','BOC','SPDB','COMM','CMBC','SDB','GDB','CITIC','HXB','CIB','GZCB','SRCB','BOB','CBHB','BJRCB','NJCB','CEB','BEA','NBCB','HZB','PAB','HSB','ZSB','SHB','POST','JSB') collate utf8_bin default null;