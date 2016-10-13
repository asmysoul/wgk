package com.aton.config;

import java.io.File;

import org.apache.commons.lang.StringUtils;

import play.Play;

public class Config {

	// --------------------本地应用相关配置---------------------------------------
	public static File confDir = new File(Play.applicationPath, "conf");
	public static File mockDir = new File(Play.applicationPath, "conf/mock");
	public static File testDataDir = new File(Play.applicationPath, "test/data");
	
	public static String APP_MODE = getProperty("app.mode", "DEV");
	public static String APP_MODE_YML = getProperty("app.mode.yml", "init-appmode.yml");
	
	/** 应用域名（以/结尾）:默认为http://localhost/ */
	public static String APP_URL = getProperty("application.baseUrl", "http://localhost/");
	
	/**
	 * 导入快递单时读取Excel的配置 文件
	 */
	public static File orderExpressJxlsConfig = new File(Config.confDir, "excel/orderExpressConfig.xml");
	public static File orderExpressYdJxlsConfig = new File(Config.confDir, "excel/orderExpressYdConfig.xml");
	public static File fabaoguoOrderExpressConfig = new File(Config.confDir, "excel/fabaoguoOrderExpressConfig.xml");
	public static File buyerDepositConfig = new File(Config.confDir, "excel/buyerDepositConfig.xml");
	/**
	 * 导出订单的Excel模板文件
	 */
	public static File orderExpressXlsTemplate = new File(Config.confDir, "excel/orderExpressTpl_yd.xls");
	
	 /**
     * 导出订单的Excel模板文件
     */
    public static File faBaoGuoOrderExpressXlsTemplate = new File(Config.confDir, "excel/faBaoGuoOrderExpressTpl_yd.xls");
	/**
	 * 导出任务EXCEL模板文件
	 */
	public static File tasksXlsTemplate = new File(Config.confDir, "excel/tasks.xls");
	/**
	 * 导出买手任务的Excel模板文件
	 */
	public static File buyerTaskXlsTemplate = new File(Config.confDir, "excel/buyerTaskTpl.xls");
	/**
	 * 导出“超过48小时未返款”的买手任务的Excel模板文件
	 */
	public static File buyerTaskRefundOverdueXlsTemplate = new File(Config.confDir, "excel/buyerTaskRefundOverdueTpl.xls");
	
	/**
     * 导出买手垫付本金提现的Excel模板文件
     */
    public static File allUntradeBuyerDepositXlsTemplate = new File(Config.confDir, "excel/buyerdepositTpl.xls");
    /**
	 * 导出卖家押金记录EXCEL模板文件
	 */
	public static File pledgeXlsTemplate = new File(Config.confDir, "excel/pledge.xls");
	/**
	 * 导出商家放单统计Excel模板文件
	 */
	public static File sellerPutTimeTemplate = new File(Config.confDir, "excel/sellerPutTime.xls");
	/**
	 * 导出买手接单统计Excel模板文件
	 */
	public static File buyerTakeTaskTemplate = new File(Config.confDir, "excel/buyerTakeTask.xls");
	/**
	 *商家子任务对账
	 */
	public static File sellerBlanceXlsTemplate = new File(Config.confDir, "excel/seller-blance.xls");
	/**
	 *商家父任务对账
	 */
	public static File sellerTaskBlanceXlsTemplate = new File(Config.confDir, "excel/seller-task-blance.xls");

	// --------------------其他服务相关配置---------------------------------------
	/*
	 * 后端业务服务器
	 */
	public static String BIZ_HOST = getProperty("biz.host");
	public static String BIZ_POST = getProperty("biz.port");
	
	/* 
	 * 七牛云存储
	 */
    public static String QN_ACCESS_KEY = getProperty("qiniu.access_key");
    public static String QN_SECRET_KEY = getProperty("qiniu.secret_key");
    
    /* 
     * 易宝支付商家账号
     */
    public static String YEEPAY_MERCHANT_ID = getProperty("yeepay.merchant_id");
    public static String YEEPAY_SECRET_KEY = getProperty("yeepay.secret_key");
    /** 支付接口 */
    public static String YEEPAY_PAYMENT_URL = "https://www.yeepay.com/app-merchant-proxy/node";
    /** 返款接口 */
    public static String YEEPAY_REFUND_URL = "https://www.yeepay.com/app-merchant-proxy/command";
    
    /* 
     * 财付通商家账号
     */
    public static String TENPAY_PARTNER = getProperty("tenpay.partner");
    public static String TENPAY_KEY = getProperty("tenpay.key");
    /** 支付接口 */
    public static String TENPAY_URL = "https://gw.tenpay.com/gateway/pay.htm";
    /** 验证支付通知ID接口 */
    public static String TENPAY_VERIFY_URL = "https://gw.tenpay.com/gateway/simpleverifynotifyid.xml";
    
    /* 
     * 快钱支付商家账号
     */
    public static String KQPAY_MERCHANT_ID = getProperty("kqpay.merchant_id");
    public static String KQPAY_SECRET_PASS = getProperty("kqpay.secret.pass");
    /** 商户私钥证书：用于加密提交参数 */
    public static File KQPAY_SECRET_PFX = new File(Config.confDir, getProperty("kqpay.secret.pfx"));
    /** 快钱公钥证书：用于校验返回参数（从快钱后台下载） */
    public static File KQPAY_SECRET_CER = new File(Config.confDir, getProperty("kqpay.secret.cer"));
    /** 支付接口 */
    public static String KQPAY_URL = "https://www.99bill.com/gateway/recvMerchantInfoAction.htm";
	
	/* 
	 * ElasticSearch Server
	 */
	public static String ES_HOST = getProperty("es.host");
	public static String ES_PORT = getProperty("es.port");
	
	/*
	 * Remote MongoDB Server
	 */
	public static String MDB_HOST = getProperty("mdb.host");
	public static String MDB_PORT = getProperty("mdb.port");
	public static String MDB_DB = getProperty("mdb.db");
	public static String MDB_USER = getProperty("mdb.db.user");
	public static String MDB_PASS = getProperty("mdb.db.pass");

	/** 其他服务IP白名单：多个IP以半角逗号隔开 */
	public static String IP_WHITE_LIST = getProperty("ip.white");

	/** 短信提醒的接收号码 */
	public static String SMS_ALERT_PHONES = getProperty("sms.alert.phones");
	
	//流量任务接口配置
	public static String FLOW_APP_KEY=getProperty("flow.appkey");
	public static String FLOW_APP_SECRET=getProperty("flow.appsecret");
	public static String FLOW_API_URL="http://api.aymoo.com/api/";
	
	
	//系统配置
	/* 同一个账号（挖顾客账号），接同一个店铺（根据店铺ID判断）：需要间隔7天。
	同一个买号（旺旺号）接同一个店铺（根据店铺ID判断）：需要20天 同一个买号（旺旺号）接同一个商品（根据商品ID判断）：需要60天。
	*/
	public static final String BUYER_AND_SHOP_TIME = getProperty("BUYER_AND_SHOP_TIME");
	public static final String BUYER_AND_SELLER_TIME = getProperty("BUYER_AND_SELLER_TIME");
	public static final String BUYER_ACOUNT_AND_SHOP_TIME = getProperty("BUYER_ACOUNT_AND_SHOP_TIME");
	public static final String BUYER_ACOUNT_AND_ITEM_TIME = getProperty("BUYER_ACOUNT_AND_ITEM_TIME");

	/**
	 *  平台限制每个【买号】接单数量:5单/天
	 *  平台限制每个【买号】接单数量:30单/周
	 *  平台限制每个【买号】接单数量:90单/月 
	 */
	public static final String BUYER_TASK_DAY_COUNT = getProperty("BUYER_TASK_DAY_COUNT");
	public static final String BUYER_TASK_WEEK_COUNT = getProperty("BUYER_TASK_WEEK_COUNT");
	public static final String BUYER_TASK_MONTH_COUNT = getProperty("BUYER_TASK_MONTH_COUNT");
	
	
	
	/**
	 *  平台限制每个【买号】接单数量:5单/天
	 *  平台限制每个【买号】接单数量:30单/周
	 *  平台限制每个【买号】接单数量:90单/月 
	 */
	public static final String BUYER_TASK_DAY_COUNT3 = getProperty("BUYER_TASK_DAY_COUNT3");
	public static final String BUYER_TASK_WEEK_COUNT3 = getProperty("BUYER_TASK_WEEK_COUNT3");
	public static final String BUYER_TASK_MONTH_COUNT3 = getProperty("BUYER_TASK_MONTH_COUNT3");
	
	
	
	/**
	 *  平台限制每个【买号】接单数量:30单/天
	 *  平台限制每个【买号】接单数量:210单/周
	 *  平台限制每个【买号】接单数量:300单/月 
	 */
	public static final String BUYER_TASK_DAY_COUNT2 = getProperty("BUYER_TASK_DAY_COUNT2");
	public static final String BUYER_TASK_WEEK_COUNT2 = getProperty("BUYER_TASK_WEEK_COUNT2");
	public static final String BUYER_TASK_MONTH_COUNT2 = getProperty("BUYER_TASK_MONTH_COUNT2");
	
	public static String getProperty(String key) {
		return getProperty(key, StringUtils.EMPTY);
	}

	public static String getProperty(String key, String defaultValue) {
		return Play.configuration.getProperty(key, defaultValue);
	}

}
