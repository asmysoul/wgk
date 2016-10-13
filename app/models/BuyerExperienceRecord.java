package models;

import java.util.Date;

import enums.Sign;

public class BuyerExperienceRecord {

    public static final String TABLE_NAME = "buyer_experience_record";

    public long id;
    public long userId;

    /**
     * 经验值
     */
    public long amount;
    
    /**
     * 结余
     */
    public long balance;

    /**
     * 增 、减
     */
    public Sign sign;

    /**
     * 备注
     */
    public String memo;

    /**
     * 记录创建时间
     */
    public Date createTime;

    public BuyerExperienceRecord() {
    }

    /**
     * 
     * Constructs a <code>BuyerExperienceRecord</code>
     * 
     * @since v0.1
     */
    @Deprecated
    public BuyerExperienceRecord(Long userId, Long amount, Sign sign, String memo, Date createTime) {
        this.userId = userId;
        this.amount = amount;
        this.sign = sign;
        this.memo = memo;
        this.createTime = createTime;
    }

    public static BuyerExperienceRecord newInstance(long userId, BuyerExperienceRecord lastRecord) {
        BuyerExperienceRecord record = new BuyerExperienceRecord();
        // 将上次结余作为本次结余初始值，用于后续计算
        record.balance = (lastRecord != null) ? lastRecord.balance : 0;
        record.userId = userId;
        return record;
    }

    public BuyerExperienceRecord plus(long amount) {
        this.amount = amount;
        this.sign = Sign.PLUS;
        this.balance = balance + amount;
        return this;
    }

    public BuyerExperienceRecord minus(long amount) {
        this.amount = amount;
        this.sign = Sign.MINUS;
        this.balance = balance - amount;
        return this;
    }

    public BuyerExperienceRecord memo(String memo) {
        this.memo = memo;
        return this;
    }
}
