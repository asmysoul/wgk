package models;

import java.util.Date;

import org.apache.commons.lang.Validate;

/**
 * 
 * 【平台退款记录】.<br>
 * 
 * 当买手“确认收货并好评”之后，若24小时之内商家没有为买手进行退款，则平台介入操作，
 * 扣除商家的该单任务押金及退款保障金，并代替商家未买手退款。
 * 
 * @author youblade
 * @since v0.2
 * @created 2014年10月22日 下午1:51:15
 */
public class AdminRefundRecord {

    public static final String TABLE_NAME = "admin_refund_record";

    public long id;
    public long buyerTaskId;
    public long taskId;
    public long buyerId;
    public long sellerId;
    public String transNo;
    public Date createTime;

    public static AdminRefundRecord newInstance(BuyerTask bt) {
        Validate.notNull(bt);
        Validate.notNull(bt.id);
        Validate.notNull(bt.taskId);
        Validate.notNull(bt.buyerId);
        Validate.notNull(bt.sellerId);
        Validate.notNull(bt.transNo);
        
        AdminRefundRecord r = new AdminRefundRecord();
        r.buyerTaskId = bt.id;
        r.taskId = bt.taskId;
        r.buyerId = bt.buyerId;
        r.sellerId = bt.sellerId;
        r.transNo = bt.transNo.trim();
        return r;
    }
    
    
    public static AdminRefundRecord newInstance(BuyerTask3 bt) {
        Validate.notNull(bt);
        Validate.notNull(bt.id);
        Validate.notNull(bt.taskId);
        Validate.notNull(bt.buyerId);
        Validate.notNull(bt.sellerId);
        Validate.notNull(bt.transNo);
        
        AdminRefundRecord r = new AdminRefundRecord();
        r.buyerTaskId = bt.id;
        r.taskId = bt.taskId;
        r.buyerId = bt.buyerId;
        r.sellerId = bt.sellerId;
        r.transNo = bt.transNo.trim();
        return r;
    }
    
    public static AdminRefundRecord newInstance(BuyerTask2 bt) {
        Validate.notNull(bt);
        Validate.notNull(bt.id);
        Validate.notNull(bt.taskId);
        Validate.notNull(bt.buyerId);
        Validate.notNull(bt.sellerId);
        Validate.notNull(bt.transNo);
        
        AdminRefundRecord r = new AdminRefundRecord();
        r.buyerTaskId = bt.id;
        r.taskId = bt.taskId;
        r.buyerId = bt.buyerId;
        r.sellerId = bt.sellerId;
        r.transNo = bt.transNo.trim();
        return r;
    }
    
    
    
    public static AdminRefundRecord newInstance(String transNo) {
        AdminRefundRecord r = new AdminRefundRecord();
        r.transNo = transNo.trim();
        return r;
    }

    public AdminRefundRecord task(long buyerTaskId, long taskId) {
        this.buyerTaskId = buyerTaskId;
        this.taskId = taskId;
        return this;
    }

    public AdminRefundRecord user(long buyerId, long sellerId) {
        this.buyerId = buyerId;
        this.sellerId = sellerId;
        return this;
    }
    
    public AdminRefundRecord createTime(Date createTime) {
        this.createTime = createTime;
        return this;
    }
}
