package models;

import java.util.Date;

import models.mappers.fund.BuyerDepositRecordMapper;

import org.apache.ibatis.session.SqlSession;
import org.joda.time.DateTime;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import vos.Page;

import com.aton.config.ReturnCode;
import com.aton.db.SessionFactory;

import controllers.MoneyManage.MoneyRecordSearchVo;
import enums.Sign;

/**
 * 
 * 买手本金记录.
 * 
 * @author youblade
 * @since  v1.7
 * @created 2014年11月28日 下午5:52:15
 */
public class BuyerDepositRecord {

	private static final Logger log = LoggerFactory.getLogger(BuyerDepositRecord.class);

	public static final String TABLE_NAME = "buyer_deposit_record";

	public long id;
	public Long userId;
	public Long buyerTaskId;
	public long taskId;
	public String userNick;
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
	
	public Date createTime;
	public String memo;
	
//	public String userNick;
//	public String taskIdStr;
	
	/**
	 * 
	 * 创建业务记录.
	 *
	 * @since  v1.7
	 * @author youblade
	 * @created 2014年11月28日 下午5:56:42
	 */
	public void create() {
	    SqlSession ss = SessionFactory.getSqlSession();
	    try {
            // 获取上一次的金币记录计算结余
            BuyerDepositRecordMapper depositMapper = ss.getMapper(BuyerDepositRecordMapper.class);
            BuyerDepositRecord lastIngotRecord = depositMapper.selectLastRecord(this.userId);
            
            BuyerDepositRecord record = BuyerDepositRecord.newInstance(this.userId, lastIngotRecord).taskId(taskId)
                .minus(amount).memo(memo).createTime(new Date());
            depositMapper.insert(record);
            
            // 更新用户缓存
            User.findByIdWichCache(this.userId).deposit(record.balance).updateCache();
	    } finally {
	        ss.close();
	    }
	}
	
	/**
	 * 
	 * 创建单表记录【测试用】
	 *
	 * @since  v1.7
	 * @author youblade
	 * @created 2014年11月28日 下午5:56:56
	 */
	public void simpleCreate() {
		this.createTime = DateTime.now().toDate();
		SqlSession ss = SessionFactory.getSqlSession();
		try {
            BuyerDepositRecordMapper mapper = ss.getMapper(BuyerDepositRecordMapper.class);
            mapper.insert(this);
		} finally {
			ss.close();
		}
	}
	
	/**
	 * 
	 * 分页获取记录.
	 *
	 * @param vo
	 * @return
	 * @since  v1.7
	 * @author youblade
	 * @created 2014年11月28日 下午5:57:14
	 */
    public static Page<BuyerDepositRecord> findByPage(MoneyRecordSearchVo vo) {
        SqlSession ss = SessionFactory.getSqlSession();
        try {
        	BuyerDepositRecordMapper mapper = ss.getMapper(BuyerDepositRecordMapper.class);
        	int totalCount = mapper.count(vo);
            if (totalCount <= 0) {
                return Page.EMPTY;
            }

            Page<BuyerDepositRecord> page = Page.newInstance(vo.pageNo, vo.pageSize, totalCount);
            page.items = mapper.selectList(vo);
            return page;
        } finally {
            ss.close();
        }
    
    }
    
	public static BuyerDepositRecord newInstance(long userId, BuyerDepositRecord lastRecord) {
		BuyerDepositRecord record = new BuyerDepositRecord();
		// 将上次结余作为本次结余初始值，用于后续计算
        record.balance = (lastRecord != null) ? lastRecord.balance : 0;
		record.userId = userId;
		return record;
	}

	public BuyerDepositRecord taskId(long taskId) {
		this.taskId = taskId;
		return this;
	}

	public BuyerDepositRecord plus(long amount) {
		this.amount = amount;
		this.sign = Sign.PLUS;
		this.balance = balance + amount;
		return this;
	}

	public BuyerDepositRecord minus(long amount) {
		this.amount = amount;
		this.sign = Sign.MINUS;
		this.balance = balance - amount;
		return this;
	}
	
    public BuyerDepositRecord memo(String memo) {
        this.memo = memo;
        return this;
    }
    
    public BuyerDepositRecord createTime(Date now) {
        this.createTime = now;
        return this;
    }

    /**
     *买手本金充值
     *
     * @param type
     * @param amount2
     * @param userId2
     * @param memo2
     * @return
     * @since  v3.4
     * @author fufei
     * @created 2015年4月28日 上午10:42:00
     */
    public static void rechargeDeposit(long amount2, long userId2, String memo2) {
        SqlSession ss = SessionFactory.getSqlSession();
        try {
            BuyerDepositRecordMapper ingotMapper = ss.getMapper(BuyerDepositRecordMapper.class);
            // 获取上一次的金币记录计算结余
            BuyerDepositRecord lastRecord = ingotMapper.selectLastRecord(userId2);
            if (lastRecord == null) {
                lastRecord = BuyerDepositRecord.newInstance(userId2, lastRecord);
            }
            lastRecord = BuyerDepositRecord.newInstance(userId2, lastRecord).plus(amount2).memo(memo2)
                .createTime(DateTime.now().toDate());
            ingotMapper.insert(lastRecord);
            // 设置用户缓存中的本金余额
            User.findByIdWichCache(userId2).deposit(lastRecord.balance).updateCache();
        } catch (Exception e) {
            log.info("deposit recharge faild!");
        } finally {
            ss.close();
        }
       
    }
    

    /**
     *买手本金扣款
     *
     * @param type
     * @param amount2
     * @param userId2
     * @param memo2
     * @return
     * @since  v3.4
     * @author fufei
     * @created 2015年4月28日 上午10:42:00
     */
    public static void deductDeposit(long amount2, long userId2, String memo2) {
        SqlSession ss = SessionFactory.getSqlSession();
        try {
            BuyerDepositRecordMapper ingotMapper = ss.getMapper(BuyerDepositRecordMapper.class);
            // 获取上一次的金币记录计算结余
            BuyerDepositRecord lastRecord = ingotMapper.selectLastRecord(userId2);
            if (lastRecord == null) {
                lastRecord = BuyerDepositRecord.newInstance(userId2, lastRecord);
            }
            lastRecord = BuyerDepositRecord.newInstance(userId2, lastRecord).minus(amount2).memo(memo2)
                .createTime(DateTime.now().toDate());
            ingotMapper.insert(lastRecord);
            // 设置用户缓存中的本金余额
            User.findByIdWichCache(userId2).deposit(lastRecord.balance).updateCache();
        } catch (Exception e) {
            log.info("deposit recharge faild!");
        } finally {
            ss.close();
        }
       
    }
}
