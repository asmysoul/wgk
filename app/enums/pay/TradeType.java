package enums.pay;

/**
 * 
 * 第三方支付平台交易记录中的业务类型标记.
 * 
 * @author youblade
 * @since v0.2.4
 * @created 2014年10月31日 下午3:34:42
 */
public enum TradeType {
    /**
     * 会员开通、续费
     */
    MEMBER,
    /**
     * 【卖家】充值押金
     */
    PLEDGE,
    /**
     * 充值购买金币
     */
    INGOT,
    /**
     * 本金
     */
    DEPOSIT,
    
    /**
     * 任务发布等
     */
    TASK;
    
}
