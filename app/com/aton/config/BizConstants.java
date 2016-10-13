package com.aton.config;

import java.util.Map;

import com.google.common.collect.Maps;

/**
 * 
 * 业务常量类.
 * 
 * @author youblade
 * @since v0.3.3
 * @created 2013-10-31 下午5:04:10
 */
public class BizConstants {

    /** -------------------- 任务相关---------------------------------- **/
    /**
     * 淘宝、天猫平台每单任务服务费用起步价格：6元
     */
	public static final int TASK_STARTING_INGOT = 6;
	  /**
     * 非淘宝、天猫平台每单任务服务费用起步价格：4元
     */
	public static final int TASK_OTHER_PLATFORM_STARTING_INGOT = 4;
    /**
     * 每单任务服务快递费用：5元
     */
    public static final Integer TASK_EXPRESS_INGOT = 500;
    /**
     * 商家快递每单任务服务快递费用：2元
     */
    public static final Integer TASK_SELLER_EXPRESS_INGOT = 200;
   
    /**
     * 每单任务非包邮商品运费押金：10元
     */
    public static final int TASK_EXPRESS_PLEDGE = 10;
    
    /**
	 * 手机价格
	 */
	public static final int TASK_MOBILE_INGOTS = 1;
	
	/**
	 * PC价格
	 */
	public static final double TASK_PC_INGOTS = 0.8;
	
	/**
	 * 平台服务费
	 */
	public static final double TASK_SERVICE_INGOTS = 0.2;
	
	/**
	 * 性别金币金额
	 */
	public static final double TASK_GNDER_INGOT = 0.1;
	
	/**
	 * 区域金币金额
	 */
	public static final double TASK_BUYERLOCATION_INGOT = 0.1;
	
    /**
     * 每额外增加一个搜索关键词方案的费用：1元
     */
    public static final int TASK_SEARCH_PLAN_INGOT = 1;
    /**
     * 每额外增加关键词评价的费用：1元
     */
    public static final int TASK_GOOD_COMM_KWD_INGOT = 1;
    /**
     * 每额外增加一个图片评价的费用：1元
     */
    public static final int TASK_GOOD_COMM_PIC_INGOT = 1;
    /**
     * 移动端每单任务费用：1元
     */
    public static final float TASK_MOBILE_INGOT = 1f;
    
    
    /**
     * 移动端每单推广费用：0.5元
     */
    public static final float TASK_MOBILE_INGOT2 = 0.5f;
    /**
     * 平台快速返款给买手服务费
     */
    public static final float TASK_SYS_REFUND = 0.006f;//0.6%
    /**
     * 任务佣金的买手分成比例：90%
     */
    public static final float TASK_REWARD_BUYER_RATE = 0.9f;
    /**
     * 任务加赏佣金的买手分成比例：80%
     */
    public static final float TASK_EXT_REWARD_BUYER_RATE = 0.8f;
    /**
     * 加速任务审核费用：5元
     */
    public static final int TASK_SPEED_EXAMINE_INGOT = 5;
    
    /**
     * 优先审核费用：1元
     */
    public static final int TASK_SPEED_EXAMINE_INGOT2 = 1;
    
    /**
     * 加粉费用：1元
     */
    public static final int TASK_ADD_FEN_INGOT2 = 1;
    
    /**
     * 定时发布任务费用：5元
     */
    public static final int TASK_TIMING_PUBLISH_INGOT = 5;
    
    /**
     * 领取（接手）任务押金：1个金币
     */
    public static final int TASK_TAKING_INGOT = 1;
    
    /**
     * 买手接取任务后自动取消的时长	单位（h）
     */
    public static final int BUYER_TASK_AUTO_CANCEL_TIME = 3;
    
    /**
     * 任务退款保证金比例：5%(20150302暂时取消退款保证金)
     */
    public static final float TASK_INSURANCE_RATE = 0.0f;
    
    /**
     *  同一个账号（挖顾客账号），接同一个商家（根据商家ID判断）：需要间隔7天。
     *  同一个账号（挖顾客账号），接同一个店铺（根据店铺ID判断）：需要间隔10天。
     *  同一个买号（旺旺号）接同一个店铺（根据店铺ID判断）：需要20天
     *  同一个买号（旺旺号）接同一个商品（根据商品ID判断）：需要60天。
     */
    public static final int BUYER_AND_SELLER_TIME = 7;
    public static final int BUYER_AND_SHOP_TIME = 10;
    public static final int BUYER_ACOUNT_AND_SHOP_TIME = 20;
    public static final int BUYER_ACOUNT_AND_ITEM_TIME = 60;
    
    /**
     *  平台限制每个【买号】接单数量:5单/天
     *  平台限制每个【买号】接单数量:30单/周
     *  平台限制每个【买号】接单数量:90单/月 
     */
    public static final int BUYER_TASK_DAY_COUNT = 5;
    public static final int BUYER_TASK_WEEK_COUNT = 30;
    public static final int BUYER_TASK_MONTH_COUNT = 90;
    
    /** --------------------任务经验值------------------ **/
    public static final int TASK_EXPERIENCE_COMMON = 1;
    public static final int TASK_EXPERIENCE_NEW_SHOP = 1;
    public static final int TASK_EXPERIENCE_SYS_RECOMMEND = 3;
    
    /** --------------------提现费用---------------------------------- **/
    public static final float WITHDRAW_PERCENT_SELLER = 0.003f;//0.3%
    public static final float WITHDRAW_PERCENT_BUYER = 0.05f;//5%
    /**
     * 每月最多可提现次数：按记录条数计算
     */
    public static final int WITHDRAW_MONTH_COUNT = 3;
    /**商家流量费用*/
    public static final Map<Long,Integer> SELLER_FLOW_FEE = Maps.newHashMapWithExpectedSize(4);
    static{
    	/**100金币500流量*/
    	SELLER_FLOW_FEE.put(100*100L, 500);
    	/**200金币1000流量*/
    	SELLER_FLOW_FEE.put(200*100L, 1000);
    	/**500金币3333流量*/
    	SELLER_FLOW_FEE.put(500*100L, 3333);
    	/**1000金币10000流量*/
    	SELLER_FLOW_FEE.put(1000*100L, 10000);
    	/**5000金币62500流量*/
    	SELLER_FLOW_FEE.put(5000*100L, 62500);
    }
    
    /** ----------------------自动升级VIP所需的充值一次性会员时长------------------------- */
    public static final int UPGRADE_VIP1_COUNT = 6;
    public static final int UPGRADE_VIP2_COUNT = 12;
    public static final int UPGRADE_VIP3_COUNT = 24;
    public static final int UPGRADE_NORMAL_COUNT = UPGRADE_VIP1_COUNT-1;
    
    /**
     * 是否赠送会员(默认打开)
     * */
    public static final boolean IS_GIVE_MEMBER = true;
    
    /**
     * 是否打开被邀请人（商家）做任务奖励邀请人（默认打开）
     * */
    public static final boolean IS_BUYER_TASK_REWARD = true;
    
    /**
     * 是否打开被邀请人（买手）做任务奖励邀请人（默认打开）
     */
    public static final boolean IS_SELLER_TASK_REWARD = true;
    
    /** --------------------会员费用设置---------------------------------- **/
    /**
     * 【商家】会员费用
     */
    public static final Map<Integer,Long> SELLER_MEMBER_FEE = Maps.newHashMapWithExpectedSize(4);
    /**
     * 【买手】会员费用
     */
    public static final Map<Integer,Long> BUYER_MEMBER_FEE = Maps.newHashMapWithExpectedSize(5);
    static{
        /** 1个月60元 */
        SELLER_MEMBER_FEE.put(1, 60*100L);
        /** 3个月 180元 */
        SELLER_MEMBER_FEE.put(3, 180*100L);
        /** 6个月 300元 */
        SELLER_MEMBER_FEE.put(6, 300*100L);
        /** 12个月 600元 */
        SELLER_MEMBER_FEE.put(12, 600*100L);
        /** 24个月 1100元 */
        SELLER_MEMBER_FEE.put(24, 1100*100L);
        
        /** 1个月 20元 */
        BUYER_MEMBER_FEE.put(1, 10*100L);
        /** 3个月 50元 */
        BUYER_MEMBER_FEE.put(3, 20*100L);
        /** 6个月 100元 */
        BUYER_MEMBER_FEE.put(6, 40*100L);
        /** 9个月 150元 */
        BUYER_MEMBER_FEE.put(9, 50*100L);
        /** 12个月 200元 */
        BUYER_MEMBER_FEE.put(12, 70*100L);
        /** 24个月 400元 */
        BUYER_MEMBER_FEE.put(24, 120*100L);
    }
    /**
     * 管理员手动为超时返款子任务返款，扣除RMB5元
     */
    public static final long SELLER_REFUND_SUBTRACTION = 5;
    
    /**
     * 买手第一次充值赠送4金币
     */
    public static final long BUYER_FIRST_TIME_ACTIVE_MEMBER = 400;
    
    /**
     * 新注册买手会员免费赠送20金币的基础资金 
     */
    public static final long BUYER_RRGIST_MEMBER = 2000;
    
    /**
     * 被邀请注册的【商家】第一次充值会员，奖励邀请者50%会员充值费
     */
    public static final float SELLER_INVITED_REWARD = 0.5f; //50%
    /**
     * 被邀请注册的【买手】第一次充值会员，奖励邀请者50%会员充值费
     */
    public static final float BUYER_INVITED_REWARD = 0.5f; //50%
    
    /**
     * 如果是合作用户，反现比为80%
     */
    public static final float SPECIAL_INVITED_REWARD = 0.8f;
    
    /**
     * 如果是普通用户，返现比为50%
     */
    public static final float INVITED_REWARD = 0.5f;
    
    /** --------------------View相关：页面渲染、前后端交互---------------------------------- **/
    public static final String MSG = "msg";
    /** 提示消息时的跳转连接 */
    public static final String URL = "url";

    public static final String PLATFORMS = "platforms";
    public static final String DEVICES = "devices";
    public static final String TASK_TYPES = "taskTypes";
    public static final String SHOPS = "shops";
    public static final String TASK_STATUS = "taskStatus";
    public static final String EXPRESS_TYPE = "expressType";
    
    @Deprecated
    public static final String TENPAY_PLATFORMS = "tenpayPlatforms";
    public static final String PAY_PLATFORMS = "payPlatforms";
    
    /**
     *  从map中获取后端封装的审核状态 的任务统计信息的Key
     */
    public static final String EXAMINE_STATUS_LIST = "EXAMINE_STATUS_LIST";
    
    /**
     * 买手当前正在做的任务
     */
    public static final String BUYER_TASK_ID = "buyerTaskId";
    public static final String BUYER_TASK_STEP = "buyerTaskStep";
    
    
    /**
     * 买手当前正在做的浏览
     */
    public static final String BUYER_TASK_ID3 = "buyerTaskId3";
    public static final String BUYER_TASK_STEP3 = "buyerTaskStep3";
    
    /**
     * 买手当前正在做的推广
     */
    public static final String BUYER_TASK_ID2 = "buyerTaskId2";
    public static final String BUYER_TASK_STEP2 = "buyerTaskStep2";

}
