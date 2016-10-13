package models;

import java.math.BigDecimal;
import java.util.Date;

import models.SellerPledgeRecord.PledgeAction;
import models.User.UserType;
import models.mappers.MemberChargeRecordMapper;
import models.mappers.SellerPledgeRecordMapper;
import models.mappers.UserFlowRecordMapper;
import models.mappers.UserIngotRecordMapper;
import models.marketing.UserInvitedRecord;

import org.apache.ibatis.session.SqlSession;
import org.joda.time.DateTime;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import play.data.validation.Required;
import vos.Page;

import com.aton.config.BizConstants;
import com.aton.config.ReturnCode;
import com.aton.db.SessionFactory;

import controllers.MoneyManage.MoneyRecordSearchVo;
import enums.Sign;

/**
 * 
 * 
 * (卖家)流量记录
 * 
 * @author fufei
 * @since  v2.7
 * @created 2015年3月6日 下午12:19:41
 */
public class UserFlowRecord {

	private static final Logger log = LoggerFactory.getLogger(UserFlowRecord.class);

	public static final String TABLE_NAME = "user_flow_record";

	public long id;
	public Long userId;
	public long taskId;

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
	
	public boolean isReward;

	public Date createTime;
	public String memo;
	
	public String userNick;
	public String taskIdStr;
	
	
	/**
	 * 
	 * 创建业务记录.
	 *
	 * @since  v1.0
	 * @author youblade
	 * @created 2014年11月16日 下午1:06:49
	 */
	public void create() {
	    SqlSession ss = SessionFactory.getSqlSession();
	    try {
            // 获取上一次的金币记录计算结余
	    	UserFlowRecordMapper ingotMapper = ss.getMapper(UserFlowRecordMapper.class);
            UserFlowRecord lastIngotRecord = ingotMapper.selectLastRecord(this.userId);
            
            UserFlowRecord record = UserFlowRecord.newInstance(this.userId, lastIngotRecord)
                .minus(amount).memo(memo).createTime(new Date());
            ingotMapper.insert(record);
            
            // 更新用户缓存
            User.findByIdWichCache(this.userId).ingot(record.balance).updateCache();
	    } finally {
	        ss.close();
	    }
	}
	
	/**
	 * 
	 * 创建单表记录【测试用】
	 * 
	 * @since v0.1
	 * @author moloch
	 * @created 2014-9-12 下午5:19:50
	 */
	public void simpleCreate() {
		this.createTime = DateTime.now().toDate();
		SqlSession ss = SessionFactory.getSqlSession();
		try {
			UserFlowRecordMapper mapper = ss.getMapper(UserFlowRecordMapper.class);
            mapper.insert(this);
		} finally {
			ss.close();
		}
	}
	
	/**
	 * 
	 * 用户购买金币套餐.
	 *
	 * @since  0.1
	 * @author youblade
	 * @created 2014年9月16日 下午10:23:00
	 */
    public void buy() {
        SqlSession ss = SessionFactory.getSqlSession();
        try {
        	UserFlowRecordMapper mapper = ss.getMapper(UserFlowRecordMapper.class);
            // 取上次记录计算本次结余
            UserFlowRecord lastRecord = mapper.selectLastRecord(this.userId);
            UserFlowRecord record = UserFlowRecord.newInstance(this.userId, lastRecord).plus(this.amount)
                .memo(this.memo).createTime(DateTime.now().toDate());
            mapper.insert(record);
            
            // 更新用户缓存数据，实时显示金币数的变化
            User.findByIdWichCache(this.userId).ingot(record.balance).updateCache();
            
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            throw new RuntimeException("购买金币出错");
        } finally {
            ss.close();
        }
        
    }

	/**
	 * 
	 * TODO Comment.
	 *
	 * @param vo
	 * @return
	 * @since  0.1
	 * @author youblade
	 * @created 2014年9月16日 下午1:39:27
	 */
    public static Page<UserFlowRecord> findByPage(MoneyRecordSearchVo vo) {
        SqlSession ss = SessionFactory.getSqlSession();
        try {
        	UserFlowRecordMapper mapper = ss.getMapper(UserFlowRecordMapper.class);
        	int totalCount = 0;
        	
            if(vo.isReward){
            	totalCount = mapper.countIsReward(vo);
            }else{
            	totalCount = mapper.count(vo);
            }
            
            if (totalCount <= 0) {
                return Page.EMPTY;
            }

            Page<UserFlowRecord> page = Page.newInstance(vo.pageNo, vo.pageSize, totalCount);
            page.items = vo.isReward == true ? mapper.selectIsRewardList(vo):mapper.selectList(vo);
            return page;
        } finally {
            ss.close();
        }
    
    }
   
   
    
	public static UserFlowRecord newInstance(long userId, UserFlowRecord lastRecord) {
		UserFlowRecord record = new UserFlowRecord();
		// 将上次结余作为本次结余初始值，用于后续计算
        record.balance = (lastRecord != null) ? lastRecord.balance : 0;
		record.userId = userId;
		return record;
	}

	public UserFlowRecord taskId(long taskId) {
		this.taskId = taskId;
		return this;
	}

	public UserFlowRecord plus(long amount) {
		this.amount = amount;
		this.sign = Sign.PLUS;
		this.balance = balance + amount;
		return this;
	}

	public UserFlowRecord minus(long amount) {
		this.amount = amount;
		this.sign = Sign.MINUS;
		this.balance = balance - amount;
		return this;
	}
	
    public UserFlowRecord memo(String memo) {
        this.memo = memo;
        return this;
    }
    
    @Deprecated
    public UserFlowRecord createTime(DateTime now) {
        this.createTime = now.toDate();
        return this;
    }
    
    public UserFlowRecord createTime(Date now) {
        this.createTime = now;
        return this;
    }
    
    public UserFlowRecord isReward(boolean isReward){
    	this.isReward = isReward;
    	return this;
    }

    /**
	 * 
	 * 使用金币和押金混合付款
	 *
	 * @param ss
	 * @param lastIngot
	 * @param lastPledge
	 * @since  v2.7
	 * @author fufei
	 * @created 2015年3月9日 下午2:33:01
	 */
	public static void payFlowWithMultipleWay(User user,SqlSession ss,UserIngotRecord lastIngot,SellerPledgeRecord lastPledge,long totalAmount) {
		try {
			Date now = DateTime.now().toDate();
			UserIngotRecordMapper ingotMapper = ss.getMapper(UserIngotRecordMapper.class);
			SellerPledgeRecordMapper pledgeMapper = ss.getMapper(SellerPledgeRecordMapper.class);
			/*
			 * 金币不足时，使用押金兑换成金币补足
			 */
			if (lastIngot.balance < totalAmount) {
			    // 计算差额，从押金记录中扣除
			    String memo = "购买流量套餐使用账户押金兑换金币";
			    long amount = totalAmount - lastIngot.balance;
			    lastPledge = SellerPledgeRecord.newInstance(user.id, lastPledge)
			        .action(PledgeAction.EXCHANGE_INGOT, amount).memo(memo).createTime(now);
			    pledgeMapper.insert(lastPledge);

			    // 将扣除的押金充值为金币
			    lastIngot = UserIngotRecord.newInstance(user.id, lastIngot).plus(amount).memo(memo)
			        .createTime(now);
			    ingotMapper.insert(lastIngot);
			}
			
			String memo = "购买流量套餐花费" + totalAmount/100 + "金币";
			// 扣除金币
			lastIngot = UserIngotRecord.newInstance(user.id, lastIngot).minus(totalAmount).memo(memo).createTime(now);
			ingotMapper.insert(lastIngot);
			long pledge=lastPledge==null?0:lastPledge.balance;
			User.findByIdWichCache(user.id).ingot(lastIngot.balance).pledge(pledge).updateCache();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
    /**
     * 
     * 返还用户没点完的流量
     *
     * @since  v3.2
     * @author fufei
     * @created 2015年4月13日 下午3:59:01
     */
	public static void refundFlow(TrafficRecord record) {
	    SqlSession ss = SessionFactory.getSqlSession();
        try {
            UserFlowRecordMapper mapper = ss.getMapper(UserFlowRecordMapper.class);
            UserFlowRecord flowRecord=mapper.selectLastRecord(record.userId);
            String memo="宝贝【"+record.nid+"】关键词【"+record.kwd+"】流量没有导入，退还"+record.times+"个流量";
            flowRecord=UserFlowRecord.newInstance(record.userId, flowRecord).plus(record.times).memo(memo).createTime(DateTime.now().toDate());
            mapper.insert(flowRecord);
            User.findByIdWichCache(record.userId).flow(flowRecord.balance).updateCache();
        } finally {
            ss.close();
        }
    
    }
	
	  /**
     * 
     * 系统充值流量
     *
     * @since  v3.2
     * @author fufei
     * @created 2015年4月13日 下午3:59:01
     */
    public static boolean rechargeFlow(String type, int amount, long userId,String memo) {
        SqlSession ss = SessionFactory.getSqlSession();
        try {
            UserFlowRecordMapper mapper = ss.getMapper(UserFlowRecordMapper.class);
            UserFlowRecord flowRecord = mapper.selectLastRecord(userId);
           
            if ("recharge".equals(type)) {
                flowRecord = UserFlowRecord.newInstance(userId, flowRecord).plus(amount).memo(memo)
                    .createTime(DateTime.now().toDate());
                mapper.insert(flowRecord);
            }
            if ("deduct".equals(type)) {
                if (amount > flowRecord.balance) {
                    return false;
                }
                flowRecord = UserFlowRecord.newInstance(userId, flowRecord).minus(amount).memo(memo)
                    .createTime(DateTime.now().toDate());
                mapper.insert(flowRecord);
            }
            User.findByIdWichCache(userId).flow(flowRecord.balance).updateCache();
        } catch (Exception e) {
            log.info("flow recharge faild");
            return false;
        } finally {
            ss.close();
        }
        return true;
    }
}
