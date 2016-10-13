package models;

import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.Date;

import models.User.UserType;
import models.mappers.MemberChargeRecordMapper;
import models.mappers.UserIngotRecordMapper;
import models.marketing.UserInvitedRecord;

import org.apache.ibatis.session.SqlSession;
import org.joda.time.DateTime;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import vos.Page;

import antlr.collections.List;

import com.aton.config.BizConstants;
import com.aton.db.SessionFactory;

import controllers.MoneyManage.MoneyRecordSearchVo;
import enums.Sign;

/**
 * 
 * 
 * (卖家)金币记录
 * 
 * @author moloch
 * @since v0.1
 * @created 2014-9-12 下午5:15:57
 */
public class UserIngotRecord {

	private static final Logger log = LoggerFactory.getLogger(UserIngotRecord.class);

	public static final String TABLE_NAME = "user_ingot_record";

	public long id;
	public Long userId;
	public long taskId;
	
	public String day;

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
            UserIngotRecordMapper ingotMapper = ss.getMapper(UserIngotRecordMapper.class);
            UserIngotRecord lastIngotRecord = ingotMapper.selectLastRecord(this.userId);
            
            UserIngotRecord record = UserIngotRecord.newInstance(this.userId, lastIngotRecord)
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
            UserIngotRecordMapper mapper = ss.getMapper(UserIngotRecordMapper.class);
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
            UserIngotRecordMapper mapper = ss.getMapper(UserIngotRecordMapper.class);
            // 取上次记录计算本次结余
            UserIngotRecord lastRecord = mapper.selectLastRecord(this.userId);
            UserIngotRecord record = UserIngotRecord.newInstance(this.userId, lastRecord).plus(this.amount)
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
    public static Page<UserIngotRecord> findByPage(MoneyRecordSearchVo vo) {
        SqlSession ss = SessionFactory.getSqlSession();
        try {
        	UserIngotRecordMapper mapper = ss.getMapper(UserIngotRecordMapper.class);
        	int totalCount = 0;
        	
            if(vo.isReward){
            	totalCount = mapper.countIsReward(vo);
            }else{
            	totalCount = mapper.count(vo);
            }
            
            if (totalCount <= 0) {
                return Page.EMPTY;
            }

            Page<UserIngotRecord> page = Page.newInstance(vo.pageNo, vo.pageSize, totalCount);
            page.items = vo.isReward == true ? mapper.selectIsRewardList(vo):mapper.selectList(vo);
            System.out.println("------size---------"+page.items.size());
            for(UserIngotRecord record:page.items){
            	System.out.println("===================="+(record.amount));
            	System.out.println("===================="+(record.balance));//余额
            	System.out.println("=================="+record.sign);
            }
            return page;
        } finally {
            ss.close();
        }
    
    }
    
    
    
    
    
    /**
	 */
    public static Long  findByAnount(long userId) {
    	SqlSession ss = SessionFactory.getSqlSession();
        try {
        	UserIngotRecordMapper mapper = ss.getMapper(UserIngotRecordMapper.class);

        	Date date=new Date();
        	SimpleDateFormat format=new SimpleDateFormat("yyyy-MM-dd");
        	String day=format.format(date);
        	System.out.println("==========="+userId);
        	System.out.println("========="+day);
        	//String day="2016-06-23";
        	Long amount = mapper.selectListanount(userId,day);
            if(amount==null){
            	System.out.println("无收入");
            	amount=(long) 0;
            }
            System.out.println("------size---------"+amount);
            return amount;
        } finally {
            ss.close();
        }
    
    }
    
    
    
    
    
    
    
    /**
     * 
     * 第一次开通会员，买手增加4金币
     *
     * @param fee
     * @param user
     * @since  v1.9.6
     * @author playlaugh
     * @created 2014年12月23日 下午5:34:18
     */
    public static void firstTimeActiveMember(User u,long fee) {
        SqlSession ss = SessionFactory.getSqlSession();
        Date now = DateTime.now().toDate();
        try {
            UserIngotRecordMapper mapper = ss.getMapper(UserIngotRecordMapper.class);
            UserIngotRecord lastRecord = mapper.selectLastRecord(u.id);
            //如果用户是买手，赠送4金币
            if(u.type == UserType.BUYER){
            UserIngotRecord record = UserIngotRecord.newInstance(u.id, lastRecord).plus(BizConstants.BUYER_FIRST_TIME_ACTIVE_MEMBER)
                .createTime(now).memo("第一次开通会员赠送"+ new BigDecimal(BizConstants.BUYER_FIRST_TIME_ACTIVE_MEMBER).movePointLeft(2).intValue() + "金币");
            mapper.insert(record);
            // 更新用户缓存
            User.findByIdWichCache(u.id).ingot(record.balance).updateCache();
            }
            //如果用户是被邀请用户，返还金币给邀请者
            if(User.isInvited(u.id)){
                UserInvitedRecord invitedRecord = UserInvitedRecord.findMyInvitedRecord(u.id);
                User inviter = User.findById(invitedRecord.inviteUserId);
                
                UserIngotRecord inviterLastRecord = mapper.selectLastRecord(inviter.id);
                long rewardFee = 0;
                String memo = "";
                if(u.type == UserType.BUYER){
                    rewardFee = new BigDecimal(fee*BizConstants.BUYER_INVITED_REWARD).longValue();
                    memo = "您邀请的买手"+u.nick+"第一次充值会员，奖励您"+new BigDecimal(fee*BizConstants.BUYER_INVITED_REWARD).movePointLeft(2)+"金币";
                }else if (u.type == UserType.SELLER){
                    rewardFee = new BigDecimal(fee*BizConstants.SELLER_INVITED_REWARD).longValue();
                    memo = "您邀请的商家"+u.nick+"第一次充值会员，奖励您"+new BigDecimal(fee*BizConstants.SELLER_INVITED_REWARD).movePointLeft(2)+"金币";
                }
                UserIngotRecord rewardIngot = UserIngotRecord.newInstance(inviter.id, inviterLastRecord).plus(rewardFee)
                    .createTime(now).memo(memo);
                mapper.insert(rewardIngot);
                
                //更新会员开通时间和奖励金币数
                invitedRecord.updateRecordForMemberOpen(rewardFee);
                
                // 更新用户缓存
                User.findByIdWichCache(inviter.id).ingot(rewardIngot.balance).updateCache();
            }
               
        } finally {
            ss.close();
        }
    }
    
    /**
     * 
     * 用户是否第一次开通会员
     *
     * @param userId
     * @since  v2.0
     * @author playlaugh
     * @created 2014年12月24日 下午4:31:20
     */
    public static boolean isFirstTimeActiveMember(long userId){
        SqlSession ss = SessionFactory.getSqlSession();  
        try {
            MemberChargeRecordMapper mapper = ss.getMapper(MemberChargeRecordMapper.class);
            MemberChargeRecord record = mapper.selectLastByUserId(userId);
            if(record == null){
               return true;
            }else{
                return false;
            }
        } finally {
            ss.close();
        }
    }
    
	public static UserIngotRecord newInstance(long userId, UserIngotRecord lastRecord) {
		UserIngotRecord record = new UserIngotRecord();
		// 将上次结余作为本次结余初始值，用于后续计算
        record.balance = (lastRecord != null) ? lastRecord.balance : 0;
		record.userId = userId;
		return record;
	}

	public UserIngotRecord taskId(long taskId) {
		this.taskId = taskId;
		return this;
	}

	public UserIngotRecord plus(long amount) {
		this.amount = amount;
		this.sign = Sign.PLUS;
		this.balance = balance + amount;
		return this;
	}

	public UserIngotRecord minus(long amount) {
		this.amount = amount;
		this.sign = Sign.MINUS;
		this.balance = balance - amount;
		return this;
	}
	
    public UserIngotRecord memo(String memo) {
        this.memo = memo;
        return this;
    }
    
    @Deprecated
    public UserIngotRecord createTime(DateTime now) {
        this.createTime = now.toDate();
        return this;
    }
    
    public UserIngotRecord createTime(Date now) {
        this.createTime = now;
        return this;
    }
    
    public UserIngotRecord isReward(boolean isReward){
    	this.isReward = isReward;
    	return this;
    }


}
