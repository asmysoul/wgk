package enums;

import com.aton.util.EnumUtil;

/**
 * 
 * 买手任务步骤.
 * 
 * @author youblade
 * @since  0.1
 * @created 2014年7月18日 上午10:12:25
 */
public enum BuyerTaskStepType {
    //@formatter:off
    
    CHOOSE_ITEM("货比三家"), 
    VIEW_AND_INQUIRY("浏览店铺，客服询盘"),
    ORDER_AND_PAY("下单支付"),
    SEND_GOODS("商家发货"),
    CONFIRM_AND_COMMENT("收货并好评"),
    
    REFUND("商家退款"),
    CONFIRM_REFUND("买手确认退款"),
    ;
    //@formatter:on
    
    public String title;

    private BuyerTaskStepType(String title) {
        this.title = title;
    }
    
    public int getOrder() {
        return this.ordinal() + 1;
    }

    public BuyerTaskStepType getNext() {
        return EnumUtil.valueOf(BuyerTaskStepType.class, this.getOrder() + 1);
    }
  
}
