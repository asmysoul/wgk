package models;

import java.util.Date;
import java.util.List;

import javax.persistence.Transient;

import models.mappers.SellerPledgeRecordMapper;

import org.apache.commons.lang.Validate;
import org.apache.ibatis.session.SqlSession;
import org.joda.time.DateTime;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import vos.Page;

import com.aton.db.SessionFactory;
import com.aton.util.JsonUtil;
import com.aton.util.MixHelper;

import controllers.MoneyManage.MoneyRecordSearchVo;
import enums.Sign;
/**
 * 
 * 【卖家】押金记录.
 * 
 * @author youblade
 * @since  0.1
 * @created 2014年8月19日 下午1:42:51
 */
public class SellerPledgeRecord{

    private static final Logger log = LoggerFactory.getLogger(SellerPledgeRecord.class);

    public static final String TABLE_NAME = "seller_pledge_record";
    
    public long id;
    public long sellerId;
    public Long taskId;
    
    /**
     * 金额，精确到分
     */
    public long amount;
    /**
     * 结余（账户押金余额）
     */
    public long balance;
    
    /**
     * 符号（加、减）
     */
    public Sign sign;
    
    public PledgeAction action;
    public enum PledgeAction {
        /**
         * 账户充值
         */
        RECHARGE,
        /**
         * 会员充值/续费
         */
        MEMBER,
        /**
         * 冻结（发布任务）
         */
        LOCK,
        /**
         * 解冻（任务结束、【平台】【买手】撤销买手任务）
         */
        UNLOCK,
        /**
         * 兑换金币
         */
        EXCHANGE_INGOT,
        /**
         * 提现
         */
        WITHDRAW,
        /**
         * 【平台】扣除任务的退款保障金
         */
        DEDUCT,
        
        /**
         * 冻结（【商家】发布“平台返款”任务）
         */
        LOCK_SYS_REFUND,
        /**
         * 解冻（【商家】撤销、【平台】审核不通过“平台返款”任务、【平台】买手任务完成后返还至商家押金，包含5%退款保证金和非包邮商品运费押金、）
         */
        UNLOCK_SYS_REFUND,
        /**
         * 扣款（【买手】完成“平台返款”任务）
         * 【注意】该动作涉及金额应为0，金额在LOCK_SYS_REFUND时已扣除
         */
        DEDUCT_SYS_REFUND,
    }
    
    public String memo;
    public Date createTime;
    public Date modifyTime;
    
    /*
     * 展示用
     */
    @Transient
    public String userNick;
    @Transient
    public String taskIdStr;
    @Transient
    public String plusAmountStr;
    @Transient
    public String minusAmountStr;
    @Transient
    public String createTimeStr;
	/**
	 * 
	 * 分页获取卖家押金记录
	 * 
	 * @param vo
	 * @return
	 * @since 0.1
	 * @author youblade
	 * @created 2014年8月19日 下午1:51:32
	 */
	public static Page<SellerPledgeRecord> findByPage(MoneyRecordSearchVo vo) {
		SqlSession ss = SessionFactory.getSqlSession();
		try {
			SellerPledgeRecordMapper mapper = ss.getMapper(SellerPledgeRecordMapper.class);
			int totalCount = mapper.count(vo);
			if (totalCount <= 0) {
				return Page.EMPTY;
			}

			Page<SellerPledgeRecord> page = Page.newInstance(vo.pageNo, vo.pageSize, totalCount);
			page.items = mapper.selectList(vo);
			return page;
		} finally {
			ss.close();
		}
	}
    
   
    /**
     * 
     * 创建记录.
     *
     * @since  0.1
     * @author youblade
     * @created 2014年8月19日 下午1:51:37
     */
    public void create() {
        Validate.notNull(this.sellerId);
        Validate.notNull(this.amount);
        Validate.notNull(this.action);
        Validate.notNull(this.sign);
        SqlSession ss = SessionFactory.getSqlSession();
        try {
            SellerPledgeRecordMapper mapper = ss.getMapper(SellerPledgeRecordMapper.class);
            SellerPledgeRecord record = mapper.selectLastRecord(this.sellerId);
            
            // 首次充值：结余即为本次收入金额
            if(record == null){
                this.balance = this.amount;
            }else if (this.sign == Sign.PLUS) {
                this.balance = record.balance + this.amount;
            }else if (this.sign == Sign.MINUS) {
                this.balance = record.balance - this.amount;
            }
            
            this.createTime = DateTime.now().toDate();
            mapper.insert(this);
            
            // 更新用户缓存数据
            User.findByIdWichCache(this.sellerId).pledge(this.balance).updateCache();
            
        } catch (Exception e) {
            log.error("[FETAL ERROR]Create seller_pledge_record failed. data={}", this.toString());
            log.error(e.getMessage(), e);
        } finally {
            ss.close();
        }
    }
    
    public static SellerPledgeRecord newInstance(long sellerId, SellerPledgeRecord lastRecord) {
        SellerPledgeRecord record = new SellerPledgeRecord();
        // 将上次结余作为本次结余初始值，用于后续计算
        record.balance = (lastRecord != null) ? lastRecord.balance : 0;
        record.sellerId = sellerId;
        return record;
    }
    
    public SellerPledgeRecord action(PledgeAction action, long amount) {
        this.action = action;
        switch (action) {
        case LOCK:
            this.sign = Sign.MINUS;
            break;
        case UNLOCK:
            this.sign = Sign.PLUS;
            break;
        case RECHARGE:
            this.sign = Sign.PLUS;
            break;
        case MEMBER:
            this.sign = Sign.MINUS;
            break;
        case EXCHANGE_INGOT:
            this.sign = Sign.MINUS;
            break;
        case WITHDRAW:
            this.sign = Sign.MINUS;
            break;
        case DEDUCT:
            this.sign = Sign.MINUS;
            break;
        case LOCK_SYS_REFUND:
            this.sign = Sign.MINUS;
            break;
        case UNLOCK_SYS_REFUND:
            this.sign = Sign.PLUS;
            break;
        case DEDUCT_SYS_REFUND:
            this.sign = Sign.MINUS;
            break;
        default:
            break;
        }

        this.amount = amount;
        if (this.sign == Sign.PLUS) {
            this.balance += amount;
        } else {
            this.balance -= amount;
        }
        return this;
    }
    
    public SellerPledgeRecord taskId(long taskId){
        this.taskId = taskId;
        return this;
    }
    
	public SellerPledgeRecord plus(long amount) {
		this.amount = amount;
		this.sign = Sign.PLUS;
		this.balance = balance + amount;
		return this;
	}

	public SellerPledgeRecord minus(long amount) {
		this.amount = amount;
		this.sign = Sign.MINUS;
		this.balance = balance - amount;
		return this;
	}
	
    public SellerPledgeRecord memo(String memo){
        this.memo = memo;
        return this;
    }
    
    public SellerPledgeRecord createTime(Date createTime){
        this.createTime = createTime;
        return this;
    }
    
    public SellerPledgeRecord modifyTime(Date modifyTime){
        this.modifyTime = modifyTime;
        return this;
    }
    
    @Override
    public String toString() {
        return JsonUtil.toJson(this);
    }
}
