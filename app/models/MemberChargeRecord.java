package models;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;
import java.util.Map;

import models.SellerPledgeRecord.PledgeAction;
import models.User.UserStatus;
import models.User.UserType;
import models.User.VipStatus;
import models.mappers.MemberChargeRecordMapper;
import models.mappers.SellerPledgeRecordMapper;
import models.mappers.UserIngotRecordMapper;
import models.mappers.UserMapper;
import models.marketing.UserInvitedRecord;

import org.apache.commons.lang.Validate;
import org.apache.ibatis.session.SqlSession;
import org.joda.time.DateTime;
import org.joda.time.LocalDate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import vos.Page;

import com.aton.config.BizConstants;
import com.aton.db.SessionFactory;
import com.aton.util.DateUtils;

import controllers.MoneyManage.MoneyRecordSearchVo;


public class MemberChargeRecord {

    private static final Logger log = LoggerFactory.getLogger(MemberChargeRecord.class);
    
    public static final String TABLE_NAME = "member_charge_record";
    
    public long id;
    public long userId;
    
    /**
     * 充值总金额
     */
    public long amount;
    /**
     * 使用金币支付的部分金额
     */
    public long ingot;
    public UserType userType;
    public String memo;

    /**
     * 开通时间，单位：月
     */
    public int month;
    public Date startTime;
    public Date endTime;
    public Date createTime;
    
    public String userNick;
    public String taskIdStr;
    /**
     * 
     * 创建单表记录：用于网银支付时调用.
     *
     * @since  0.1
     * @author youblade
     * @created 2014年9月18日 下午1:45:58
     */
    public void simpleCreate() {
        User user = User.findByIdWichCache(this.userId);
        
        SqlSession ss = SessionFactory.getSqlSession();
        try {
            MemberChargeRecordMapper mapper = ss.getMapper(MemberChargeRecordMapper.class);
            this.userType = user.type;
            
            // 计算起止时间、备注，插入记录
            insert(ss, mapper);
        } finally {
            ss.close();
        }
    }
    
    /**
     * 
     * 根据用户类型、开通会员时间获取对应的套餐费用.
     *
     * @param userType
     * @param month
     * @return
     * @since  0.1
     * @author youblade
     * @created 2014年9月25日 下午8:31:53
     */
    public static long getRechargeAmount(UserType userType, Integer month) {
        if (month == null) {
            return 0;
        }
        
        Map<Integer, Long> map = null;
        if (userType == UserType.SELLER) {
            map = BizConstants.SELLER_MEMBER_FEE;
        } else {
            map = BizConstants.BUYER_MEMBER_FEE;
        }
        if (map.containsKey(month)) {
            return map.get(month);
        }
        return 0;
    }
    
    /**
     * 
     * 用户充值：使用账户押金、金币支付时调用.
     *
     * @param useIngot
     * @param usePledge
     * @since  0.1
     * @author youblade
     * @created 2014年8月25日 下午2:05:12
     */
    public boolean create(boolean useIngot, boolean usePledge) {
        // 正常情况下调用该方法之前amount已经计算好了，此处只是多加一层检查
        if (this.amount <= 0 && this.userType != null) {
            if (this.userType == UserType.SELLER) {
                this.amount = BizConstants.SELLER_MEMBER_FEE.get(this.month);
            } else {
                this.amount = BizConstants.BUYER_MEMBER_FEE.get(this.month);
            }
        }
        
        SqlSession ss = SessionFactory.getSqlSessionWithoutAutoCommit();
        try {
            /*
             * 扣除账户余额
             */
            Date now = DateTime.now().toDate();
            
            // 使用金币支付的部分金额
            long ingotPayFee = 0;
            if (useIngot) {
                UserIngotRecordMapper mapper = ss.getMapper(UserIngotRecordMapper.class);
                UserIngotRecord lastRecord = mapper.selectLastRecord(this.userId);
                ingotPayFee = Math.min(lastRecord.balance, this.amount);
                UserIngotRecord record = UserIngotRecord.newInstance(userId, lastRecord).minus(ingotPayFee)
                    .createTime(now).memo("用户开通会员" + this.month + "个月");
                mapper.insert(record);
                
                // 更新用户缓存
                User.findByIdWichCache(userId).ingot(record.balance).updateCache();            
                
                this.ingot = ingotPayFee;
            }

            // 使用押金支付的部分金额
            long pledgePayFee = 0;
            if (usePledge && this.amount > ingotPayFee) {
                SellerPledgeRecordMapper mapper2 = ss.getMapper(SellerPledgeRecordMapper.class);
                SellerPledgeRecord lastRecord = mapper2.selectLastRecord(this.userId);
                // 使用押金支付剩余的部分
                pledgePayFee = Math.min(lastRecord.balance, this.amount - ingotPayFee);
                SellerPledgeRecord record = SellerPledgeRecord.newInstance(this.userId, lastRecord)
                    .action(PledgeAction.MEMBER, pledgePayFee).createTime(now).memo("用户开通会员" + this.month + "个月");
                mapper2.insert(record);
                
                // 更新用户缓存
                User.findByIdWichCache(userId).pledge(record.balance).updateCache();    
            }

            // 检查账户余额是否足够支付
            if (ingotPayFee + pledgePayFee < this.amount) {
                log.error("User={} charge failed:useIngot={},usePledge={} less than total amount={}", new Object[] {
                        this.userId, ingotPayFee, pledgePayFee, this.amount });
                return false;
            }

            /*
             * 创建充值记录
             */
            MemberChargeRecordMapper mapper = ss.getMapper(MemberChargeRecordMapper.class);
            insert(ss, mapper);

            ss.commit();
            return true;
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            ss.rollback();
            return false;
        } finally {
            ss.close();
        }
    }

	private void insert(SqlSession ss, MemberChargeRecordMapper mapper) {
		Validate.isTrue(this.month > 0);
		UserMapper userMapper = ss.getMapper(UserMapper.class);
		// 默认开通起始时间为今天
		Date now = DateTime.now().toDate();
		this.startTime = now;

		// 检查会员费用
		long fee = MemberChargeRecord.getRechargeAmount(this.userType, this.month);
		// 若上次会员时间尚未到期，则在此基础上累加本次开通的时间段
		MemberChargeRecord lastRecord = mapper.selectLastByUserId(this.userId);
		if (lastRecord != null && lastRecord.endTime.after(this.startTime)) {
			this.startTime = lastRecord.endTime;
		}
		LocalDate endDate = new LocalDate(this.startTime).plusMonths(this.month);
		this.endTime = endDate.toDate();

		// 设置备注
		String endDateStr = endDate.toString(DateUtils.YYYY_MM_DD);
		if (lastRecord==null||(lastRecord != null&&lastRecord.amount==0&&lastRecord.ingot==0)) {
			// 判断是不是第一次开通会员
			this.memo = "开通VIP会员，有效期至：" + endDateStr;
			User user = userMapper.selectById(this.userId);
			UserIngotRecordMapper userIngotmapper = ss.getMapper(UserIngotRecordMapper.class);
			UserIngotRecord userIngotRecord = userIngotmapper.selectLastRecord(user.id);
			// 如果用户是买手，赠送4金币
			if (user.type == UserType.BUYER) {
				UserIngotRecord record = UserIngotRecord.newInstance(user.id, userIngotRecord).plus(BizConstants.BUYER_FIRST_TIME_ACTIVE_MEMBER)
						.createTime(now)
						.memo("第一次开通会员赠送" + new BigDecimal(BizConstants.BUYER_FIRST_TIME_ACTIVE_MEMBER).movePointLeft(2).intValue() + "金币");
				userIngotmapper.insert(record);
				// 更新用户缓存
				User.findByIdWichCache(user.id).ingot(record.balance).updateCache();
			}
			// 如果用户是被邀请用户，返还金币给邀请者
			if (User.isInvited(user.id)) {
				UserInvitedRecord invitedRecord = UserInvitedRecord.findMyInvitedRecord(user.id);
				User inviter = User.findById(invitedRecord.inviteUserId);
				UserIngotRecord inviterLastRecord = userIngotmapper.selectLastRecord(inviter.id);
				BigDecimal rewardFee =null;
				String memo = "";
				if(inviter.vipStatus.equals(VipStatus.SPECIAL)){
					rewardFee = new BigDecimal(fee * BizConstants.SPECIAL_INVITED_REWARD);
				}else{
					rewardFee = new BigDecimal(fee * BizConstants.INVITED_REWARD);
				}
				if (user.isBuyer()) {
					memo = "您邀请的买手" + user.nick + "第一次充值会员，奖励您" + rewardFee.movePointLeft(2) + "金币";
				} else if (user.type == UserType.SELLER) {
					memo = "您邀请的商家" + user.nick + "第一次充值会员，奖励您" + rewardFee.movePointLeft(2) + "金币";
				}
				UserIngotRecord rewardIngot = UserIngotRecord.newInstance(inviter.id, inviterLastRecord).plus(rewardFee.longValue()).createTime(now).memo(memo);
				userIngotmapper.insert(rewardIngot);

				// 更新会员开通时间和奖励金币数
				invitedRecord.updateRecordForMemberOpen(rewardFee.longValue());

				// 更新用户缓存
				User.findByIdWichCache(inviter.id).ingot(rewardIngot.balance).updateCache();
			}

		} else {
			this.memo = "VIP会员续费，有效期至：" + endDateStr;
		}

		this.createTime = now;
		mapper.insert(this);

		/*
		 * 更新用户数据
		 */
		
		User user = User.instance(this.userId).dueTime(endDate).status(UserStatus.VALID);
		userMapper.updateById(user);
		// Cleaer cache
		User userCached = User.findByIdWichCache(this.userId).dueTime(endDate).status(UserStatus.VALID);
		userCached.updateCache();

	}
    
	/**
	 * 给赠送会员的新用户额外赠送8金币的基础资金
	 *
	 * @param id2
	 * @since  v2.0
	 * @author Mark Xu
	 * @created 2015-3-9 下午2:55:41
	 */
	public static void giveMemberIngot(long id) {
		// 如果用户是买手，赠送20金币
		SqlSession ss = SessionFactory.getSqlSession();
		try {
			UserMapper userMapper = ss.getMapper(UserMapper.class);
			UserIngotRecordMapper userIngotmapper = ss.getMapper(UserIngotRecordMapper.class);
			User user = userMapper.selectById(id);
			UserIngotRecord userIngotRecord = userIngotmapper.selectLastRecord(user.id);
			if (user.type == UserType.BUYER) {
				UserIngotRecord record = UserIngotRecord.newInstance(user.id, userIngotRecord).plus(BizConstants.BUYER_RRGIST_MEMBER)
						.createTime(DateTime.now().toDate())
						.memo("新注册用户免费赠送" + new BigDecimal(BizConstants.BUYER_RRGIST_MEMBER).movePointLeft(2).intValue() + "金币");
				userIngotmapper.insert(record);
				// 更新用户缓存
				User.findByIdWichCache(user.id).ingot(record.balance).updateCache();
			}
		} catch (Exception e) {
            log.error(e.getMessage(), e);
		} finally {
            ss.close();
        }
		
	}
	
    /**
     * 
     * 分页获取会员充值记录.
     *
     * @param vo
     * @return
     * @since  0.1
     * @author youblade
     * @created 2014年8月25日 下午4:34:16
     */
    public static Page<MemberChargeRecord> findByPage(MoneyRecordSearchVo vo) {
        SqlSession ss = SessionFactory.getSqlSession();
        try {
            MemberChargeRecordMapper mapper = ss.getMapper(MemberChargeRecordMapper.class);
            int totalCount = mapper.count(vo);
            if (totalCount <= 0) {
                return Page.EMPTY;
            }

            Page<MemberChargeRecord> page = Page.newInstance(vo.pageNo, vo.pageSize, totalCount);
            page.items = mapper.selectList(vo);
            return page;
        } finally {
            ss.close();
        }
    }

    public static MemberChargeRecord newInstance(long userId, int month) {
        MemberChargeRecord r = new MemberChargeRecord();
        r.userId = userId;
        r.month = month;
        return r;
    }

    public MemberChargeRecord userType(UserType userType) {
        this.userType = userType;
        return this;
    }

    public MemberChargeRecord amount(long amount) {
        this.amount = amount;
        return this;
    }
	/**
	 * 
	 *查询所有会员充值记录
	 *
	 * @return
	 * @since  v2.6
	 * @author fufei
	 * @created 2015年2月10日 上午11:24:00
	 */
    public static List<MemberChargeRecord> findAll() {
        SqlSession ss = SessionFactory.getSqlSession();
        try {
            MemberChargeRecordMapper mapper = ss.getMapper(MemberChargeRecordMapper.class);
            return mapper.findAll();
        } finally {
            ss.close();
        }
    }
    
    /**
	 * 
	 *延长一个月会员时间
	 *
	 * @return
	 * @since  v2.6
	 * @author fufei
	 * @created 2015年2月10日 上午11:24:00
	 */
    public static int delayMember(MemberChargeRecord chargeRecord) {
        SqlSession ss = SessionFactory.getSqlSession();
        try {
            MemberChargeRecordMapper mapper = ss.getMapper(MemberChargeRecordMapper.class);
            return mapper.delayMember(chargeRecord);
        } finally {
            ss.close();
        }
    }
    /**
     * 
     * 激活会员没有金币、押金变更，不涉及其他表，直接插记录
     *
     * @since  v2.0
     * @author Mark Xu
     * @created 2015-3-31 上午11:21:14
     */
    public static void insert(MemberChargeRecord record){
    	SqlSession ss = SessionFactory.getSqlSession();
    	try {
    		MemberChargeRecordMapper mapper = ss.getMapper(MemberChargeRecordMapper.class);
    		record.createTime = DateTime.now().toDate();
    		record.startTime= DateTime.now().toDate();
    		record.endTime = DateTime.now().plusMonths(record.month).toDate();
    		record.ingot = 0;
    		mapper.insert(record);
    	}finally {
    		ss.close();
    	}
    }
    /**
     * 
     * 和管理员给后台做延期处理
     *
     * @since  v2.0
     * @author Mark Xu
     * @created 2015-3-31 下午1:14:59
     */
    public static void memberDelay(MemberChargeRecord record, int delayDay){
    	SqlSession ss = SessionFactory.getSqlSession();
    	MemberChargeRecordMapper mapper = ss.getMapper(MemberChargeRecordMapper.class);
    	record.memberDelay(ss, mapper, delayDay);
    } 
    /**
     * 
     * 后台延期处理逻辑
     *
     * @param ss
     * @param mapper
     * @since  v2.0
     * @author Mark Xu
     * @created 2015-3-31 下午1:35:25
     */
    private void memberDelay(SqlSession ss, MemberChargeRecordMapper mapper, int delayDay) {
		this.month = 0;
		UserMapper userMapper = ss.getMapper(UserMapper.class);
		// 默认开通起始时间为今天
		Date now = DateTime.now().toDate();
		this.startTime = now;

		// 若上次会员时间尚未到期，则在此基础上累加本次开通的时间段
		MemberChargeRecord lastRecord = mapper.selectLastByUserId(this.userId);
		if (lastRecord != null && lastRecord.endTime.after(this.startTime)) {
			this.startTime = lastRecord.endTime;
		}
		LocalDate endDate = new LocalDate(this.startTime).plusDays(delayDay);
		this.endTime = endDate.toDate();

		// 设置备注
		String endDateStr = endDate.toString(DateUtils.YYYY_MM_DD);
		this.memo = "会员延期" + delayDay + "天，有效期至：" + endDateStr;

		this.createTime = now;
		mapper.insert(this);

		/*
		 * 更新用户数据
		 */
		
		User user = User.instance(this.userId).dueTime(endDate).status(UserStatus.VALID);
		userMapper.updateById(user);
		// Cleaer cache
		User userCached = User.findByIdWichCache(this.userId).dueTime(endDate).status(UserStatus.VALID);
		userCached.updateCache();

	}
}
