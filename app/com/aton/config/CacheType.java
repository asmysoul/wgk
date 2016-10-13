package com.aton.config;

import com.aton.util.StringUtils;

/**
 * 
 * 缓存数据类型：汇总所有缓存对象的Key及过期时间.<br>
 * 【注意】：所有加入缓存的对象都要实现Serializable接口
 * 
 * @author youblade
 * @since v0.1
 */
public enum CacheType {
	// ===========================用户相关========================================
	/** 用户状态:USER_{userId} */
	USER_INFO("USER_", "3d"),

	/** 用户注册短信验证码:SMS_VALID_CODE_{nick} */
	SMS_VALID_CODE("SMS_VALID_CODE_", "1min"),
	
	/** 
	 * 七牛文件上传凭证:FILE_UPTOKEN,
	 * 失效之后需要重新获取
	 */
	FILE_UPTOKEN("FILE_UPTOKEN_", "3h"),
	
	// ===========================其他业务数据========================================
	/** 买手【买号】任务数统计:BUYER_TASK_STATS_{buyerAccountId} */
	BUYER_TASK_STATS("BUYER_TASK_STATS_", "1t"),
	
	/** 买手【买号】推广数统计:BUYER_TASK_STATS_{buyerAccountId} */
	BUYER_TASK_STATS2("BUYER_TASK_STATS2_", "1t"),
	
	/** 检测到的宝贝信息：TASK_ITEM_INFO_{platform}{id} */
	TASK_ITEM_INFO("TASK_ITEM_INFO_", "1d"),
	
	/** 已发布的任务总数：TASK_NUM */
	TASK_NUM("TASK_NUM","1d"), 
	
	
	// ===========================运营相关========================================
	/** 活动规则：ACTIVITY_RULE_{ActivityBizType} */
	ACTIVITY_RULE_("ACIVITY_RULE_","30d"), 
	
	/** 首页数据：INDEX_DATA */
	INDEX_DATA("INDEX_DATA","5m"), 
	
	//===============================================================
	SYS_CONFIG("SYS_CONFIG","30d"),
	;
	
	private String prefix;
	public String expiredTime;

	CacheType(String prefix, String expiredTime) {
		this.prefix = prefix;
		this.expiredTime = expiredTime;
	}

	/**
	 * 
	 * 获取CacheType对应的key.
	 * 【注意】若key有自定义的后缀部分，使用{@link #getKey(Object)}
	 * 
	 * @return
	 * @since v0.1
	 */
	public String getKey() {
		return this.prefix;
	}

	/**
	 * 
	 * 获取CacheType对应的key.
	 * 
	 * @param keySuffixs 多个附加对象
	 * @return
	 * @since v0.1
	 */
	public String getKey(Object... keySuffixs) {
		return this.prefix + StringUtils.join(keySuffixs, StringUtils.UNDERLINE);
	}
}
