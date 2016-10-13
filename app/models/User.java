package models;

import java.util.Date;
import java.util.List;

import javax.persistence.Transient;

import models.User.UserType;
import models.User.VipStatus;
import models.mappers.BuyerExperienceRecordMapper;
import models.mappers.SellerPledgeRecordMapper;
import models.mappers.UserFlowRecordMapper;
import models.mappers.UserIngotRecordMapper;
import models.mappers.UserMapper;
import models.mappers.fund.BuyerDepositRecordMapper;
import models.mappers.marketing.UserInvitedRecordMapper;
import models.marketing.UserInvitedRecord;

import org.apache.ibatis.session.SqlSession;
import org.joda.time.DateTime;
import org.joda.time.LocalDate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import play.cache.Cache;
import play.libs.Codec;
import vos.Page;
import vos.UserSearchVo;

import com.aton.base.secure.Secure;
import com.aton.config.CacheType;
import com.aton.db.SessionFactory;
import com.aton.util.CacheUtil;
import com.aton.util.StringUtils;
import com.google.common.base.Strings;
/**
 * 
 * 用户分【卖家】【买手】两种类型.
 * 
 * @author youblade
 * @since  0.1
 * @created 2014年7月9日 下午3:32:58
 */
public class User implements java.io.Serializable{

    private static final Logger log = LoggerFactory.getLogger(User.class);
    private static final Logger cacheLog = LoggerFactory.getLogger("cache");
    
    private static final long serialVersionUID = -7599268033418477238L;

    public static final String TABLE_NAME = "user";
    
    public static final String FIELD_NICK = "nick";
    public static final String FIELD_EMAIL = "email";
    public static final String FIELD_QQ = "qq";
    public static final String FIELD_MOBILE = "mobile";
    public static final String FIELD_ACTIVE_CODE = "active_code";
    
    public long id;
    public String nick;
    
    /** 注册激活码  */
    public String activeCode;
    public Date activeCodeCreateTime;
    /**
     * 存储MD5(origin password+salt)
     */
    public String password;
    
    /** 6位随机字符  */
    public String salt;
    public Date lastLoginTime;
    public UserStatus status;
    
    /** 对接管理员的昵称 */
    public String name;
    /** 对接管理员给对接用户的备注 */
    public String dockingMessage;
    /** 对接管理员的Id */
    public String adminId;
    
    public enum UserStatus {
        /**
         * 注册后未激活
         */
        INACTIVE("注册后未激活"),
        /**
         * 已激活，尚未开通会员
         */
        ACTIVE("已激活，尚未开通会员"),
        /**
         * 注册后开通会员
         */
        VALID("注册后开通会员"),
        /**
         * 会员过期
         */
        INVALID("会员过期"),
        /**
         * 账号被冻结
         */
        LOCKED("账号被冻结");
        
        public String title;
        private UserStatus(String title){
            this.title = title;
        }
    }
    
    public UserType type;
    public enum UserType{
        BUYER("买手"),
        SELLER("商家"),
        ALL("全部");
        public String title;
        private UserType(String title){
            this.title = title;
        }
    }
    
    public VipStatus vipStatus;
    public enum VipStatus{
    	ALL("全部"),
    	LOW("降权用户"),
    	NORMAL("普通用户"),
    	VIP1("VIP一级"),
    	VIP2("VIP二级"),
    	VIP3("VIP三级"),
    	SPECIAL("合作用户"),
    	;
    	public String title;
        private VipStatus(String title){
            this.title = title;
        }
    }
    
    public String avatar;
    public String qq;
    public String email;
    public String mobile;
    
    /** 支付密码 */
    public String payPassword;
    
    /** 会员等级 */
    public int level;
    
    /** 
     * 会员到期时间: <br>
     * 1.定时任务根据该字段检查并将用户状态设置为“已过期”
     * 2.充值会员时需要修改该字段
     */
    public Date dueTime;
    
    /** 注册激活时间 */
    public Date registTime;
    
    /** 默认退款账号 */
    @Transient
    public FundAccount defaultFundAccount;
    
    /** 金币 */
    @Transient
    public Long ingot;
    /** 流量*/
    @Transient
    public Long flow;
    /**
     * 【买手】经验值
     */
    @Transient
    public Long experience;
    /**
     * 【买手】垫付本金
     */
    @Transient
    public Long deposit;
    
    /**
     * 【卖家】押金：显示用，精确到元
     */
    @Transient
    public Long pledge;
    
    /**
     * 注册邀请人：传参用
     */
    @Transient
    public Long inviteUserId;
    
    public Date createTime;
    public Date modifyTime;
    
    /**
     * 邀请展示页面
     */
    /* 邀请用户数量 */
    @Transient
    public long userCount;
    /* 被邀请人完成任务数  */
    @Transient
    public long buyerTaskCount;
    /* 邀请人获得总奖励 */
    @Transient
    public long rewardCount;
    
    /**
     * 商家配置
     */
    /* 同一个账号（兼职牛账号），接同一个商家（根据商家ID判断） */
    @Transient
	public int buyerAndSellerTime;
	/* 同一个账号（兼职牛账号），接同一个店铺（根据店铺ID判断） */
    @Transient
	public int buyerAndShopTime;
	/* 同一个买号（旺旺号）接同一个店铺（根据店铺ID判断） */
    @Transient
	public int buyerAcountAndShopTime;
	/* 同一个买号（旺旺号）接同一个商品（根据商品ID判断） */
    @Transient
	public int buyerAcountAndItemTime;
    
    /**
     * 买手配置
     */
    /* 是否显示不可解任务 */
    @Transient
    public boolean isClearView;
    
    
    
    
    public User() {
		super();
	}

	public User(String nick, String password, UserType buyer, String qq,
			String email, String mobile) {
		// TODO Auto-generated constructor stub
    	this.nick=nick;
    	this.password=password;
    	this.type=buyer;
    	this.qq=qq;
    	this.email=email;
    	this.mobile=mobile;
	}

	/**
     * 
     * 用户是否已存在（用于判断是否注册过）.
     *
     * @since  0.1
     * @author youblade
     * @created 2014年8月4日 下午9:54:34
     */
    public static boolean isUserExists(String field, Object value) {
        User user = findByField(field, value);
        return (user != null) && (user.id > 0);
    }

    /**
     * 
     * 按照某个字段（属性）获取用户.
     *
     * @param field
     * @param value
     * @return
     * @since  0.1
     * @author youblade
     * @created 2014年8月5日 上午11:11:22
     */
    public static User findByField(String field, Object value) {
        SqlSession ss = SessionFactory.getSqlSession();
        try {
            UserMapper mapper = ss.getMapper(UserMapper.class);
            return mapper.selectByField(field, value);
        } finally {
            ss.close();
        }
    }
    
    
    
    
    
    
    
    
    
    
    

	/**
     * 
     * 按昵称查询用户.
     *
     * @param nick
     * @return
     * @since  0.1
     * @author youblade
     * @created 2014年7月9日
     */
    public static User findByNick(String nick) {
        SqlSession ss = SessionFactory.getSqlSession();
        try {
            UserMapper mapper = ss.getMapper(UserMapper.class);
            return mapper.selectByNick(nick);
        } finally {
            ss.close();
        }
    }
    
    /**
     * 
     * 获取用户：先从缓存中取，再从DB中取.
     *
     * @param id
     * @return
     * @since  0.1
     * @author youblade
     * @created 2014年7月16日 下午5:44:45
     */
    public static User findByIdWichCache(Long id) {
        String key = CacheType.USER_INFO.getKey(id);
        User user = CacheUtil.get(key, User.class);
        if (user != null) {
            return user;
        }
        
        // Fetch from DB if null
        user = findById(Long.valueOf(id));
        if (user != null) {
            setUserData(user);
            CacheUtil.setJson(key, user, CacheType.USER_INFO.expiredTime);
        }
        return user;
    }


	/**
	 * 根据任务ID查找用户
	 *
	 * @param id2
	 * @return
	 * @since  v2.0
	 * @author Mark Xu
	 * @created 2015-4-3 下午3:57:35
	 */
	public static User findByTaskId(long taskId) {
		 SqlSession ss = SessionFactory.getSqlSession();
	     try {
	        UserMapper mapper = ss.getMapper(UserMapper.class);
	        return mapper.selectByTaskId(taskId);
	    } finally {
	        ss.close();
	    }
	}
    /*
     * 设置用户的账户押金、金币、经验值等
     */
    private static void setUserData(User user) {
        SqlSession ss = SessionFactory.getSqlSession();
        try {
            /*
             * 【商家】设置押金余额
             */
            if (user.isSeller()) {
                SellerPledgeRecordMapper mapper2 = ss.getMapper(SellerPledgeRecordMapper.class);
                SellerPledgeRecord record = mapper2.selectLastRecord(user.id);
                if (record != null) {
                    user.pledge = record.balance;
                }else{
                    user.pledge = 0L;
                }
                
                UserFlowRecordMapper flowRecord=ss.getMapper(UserFlowRecordMapper.class);
                UserFlowRecord ur= flowRecord.selectLastRecord(user.id);
                user.flow=ur==null?0L:ur.balance;
            }
            
            /*
             * 【买手】设置经验值、本金余额
             */
            if(user.isBuyer()){
                BuyerExperienceRecord record4 = ss.getMapper(BuyerExperienceRecordMapper.class).selectLastRecord(
                    user.id);
                user.experience = (record4 != null) ? record4.balance : 0L;

                BuyerDepositRecord record = ss.getMapper(BuyerDepositRecordMapper.class).selectLastRecord(user.id);
                user.deposit = (record != null) ? record.balance : 0L;
            }
            
            // 用户金币
            UserIngotRecordMapper mapper3 = ss.getMapper(UserIngotRecordMapper.class);
            UserIngotRecord record = mapper3.selectLastRecord(user.id);
            if (record != null) {
                user.ingot = record.balance;
            }else{
                user.ingot = 0L;
            }

            // 会员到期时间
            Date today = LocalDate.now().toDate();
            if (user.dueTime == null || !user.dueTime.after(today)) {
                user.dueTime = today;
            }
        } finally {
            ss.close();
        }
    }
    
    /**
     * 
     * 按ID查询用户（直接查DB），大部分场景下优先使用性能更优的{@link #findByIdWichCache(Long)}.
     *
     * @param id
     * @return
     * @since  0.1
     * @author youblade
     * @created 2014年7月16日 下午5:44:45
     */
    public static User findById(Long id) {
        SqlSession ss = SessionFactory.getSqlSession();
        try {
            UserMapper mapper = ss.getMapper(UserMapper.class);
            return mapper.selectById(id);
        } finally {
            ss.close();
        }
    }
    /**
     * 
     * TODO 按邮箱查找用户.
     *
     * @param mail
     * @return
     * @since  v0.1
     * @author playlaugh
     * @created 2014年8月2日 下午6:18:40
     */
    public static User findByMail(String mail){
    	SqlSession ss = SessionFactory.getSqlSession();
        try {
            UserMapper mapper = ss.getMapper(UserMapper.class);
            return mapper.selectByMail(mail);
        } finally {
            ss.close();
        }
    }
    
    /**
     * 
     * 更新用户个人信息，同时清除了USER_INFO缓存.
     *
     * @param user  
     * @return
     * @since  v0.1
     * @author moloch
     * @created 2014-8-1 下午4:07:32
     */
    public void save(boolean isAdmin){
    	SqlSession ss = SessionFactory.getSqlSession();
    	try{
    		UserMapper mapper = ss.getMapper(UserMapper.class);
    		/*
    		 * 再次检查更新的信息是否已存在，不允许重复(管理员不用检查)
    		 */
			if (!isAdmin) {
				User userInDb = null;
				if (StringUtils.isNotBlank(this.nick)) {
					userInDb = mapper.selectByField(User.FIELD_NICK, this.nick);
				}
				if (StringUtils.isNotBlank(this.email)) {
					userInDb = mapper.selectByField(User.FIELD_EMAIL, this.email);
				}
				if (StringUtils.isNotBlank(this.qq)) {
					userInDb = mapper.selectByField(User.FIELD_QQ, this.qq);
				}
				if (StringUtils.isNotBlank(this.mobile)) {
					userInDb = mapper.selectByField(User.FIELD_MOBILE, this.mobile);
				}
				if (userInDb != null) {
					Object[] args = new Object[] { userInDb.nick, userInDb.email, userInDb.qq, userInDb.mobile };
					log.warn("Can't updated info to exits user, nick={},email={},qq={},mobile={}", args);
					return;
				}
			}
            
    		this.modifyTime = DateTime.now().toDate();
    		mapper.updateById(this);
    		
    		// Cleaer cache清除缓存
    		Cache.safeDelete(CacheType.USER_INFO.getKey(this.id));
    	}finally{
    		ss.close();
    	}
    }
    
	/**
	 * 修改对接管理员对卖家的备注信息
	 *
	 * @param adminId
	 * @param dockingMessage2
	 * @since  v2.0
	 * @author Mark Xu
	 * @created 2015-2-11 下午5:11:49
	 */
	public void save(long adminId, String dockingMessage, long id) {
		SqlSession ss = SessionFactory.getSqlSession();
    	try{
    		UserMapper mapper = ss.getMapper(UserMapper.class);
    		mapper.updateUser(adminId, dockingMessage, id);
    	}finally{
    		ss.close();
    	}
	}
    /**
     * 
     * 验证用户密码
     *
     * @since  v0.1
     * @author youblade
     * @created 2014年7月11日 下午2:42:44
     */
    public boolean validate(String inputPass){
        if(Strings.isNullOrEmpty(inputPass)){
            return false;
        }
        return this.password.equals(Codec.hexMD5(inputPass + this.salt));
    }
   
    /**
     * 
     * 添加用户
     *
     * @param user
     * @since  v0.1
     * @author playlaugh
     * @created 2014年8月2日 上午11:01:03
     */
    public void create() {
        SqlSession ss = SessionFactory.getSqlSessionWithoutAutoCommit();
        try {
            UserMapper mapper = ss.getMapper(UserMapper.class);
            User userInDb = mapper.selectByField(User.FIELD_NICK, this.nick);
            if (userInDb != null) {
                log.warn("Checked duplicated regist user={},email={}", userInDb.nick, userInDb.email);
                return;
            }
            if (mapper.selectByField(User.FIELD_EMAIL, this.email) != null) {
                return;
            }
            if (mapper.selectByField(User.FIELD_QQ, this.qq) != null) {
                return;
            }
            if (mapper.selectByField(User.FIELD_MOBILE, this.mobile) != null) {
                return;
            }
            
            this.activeCode = Codec.UUID().replace('-', '7');
           
            this.salt = String.valueOf(Math.random()).substring(2, 10);
            this.password = Codec.hexMD5(this.password + this.salt);
            System.out.println("create----------------"+salt);
            this.status = UserStatus.INACTIVE;
            Date now = DateTime.now().toDate();
            this.vipStatus = VipStatus.NORMAL;
            this.createTime = now;
            this.modifyTime = now;
            mapper.insert(this);
            
            // 保存邀请注册记录
            if (this.inviteUserId != null) {
                User inviteUser = mapper.selectById(this.inviteUserId);
                if (inviteUser == null) {
                    return;
                }
                UserInvitedRecord record = new UserInvitedRecord();
                record.userId = this.id;
                record.inviteUserId = inviteUserId;
                record.createTime = now;
                record.modifyTime = now;
                ss.getMapper(UserInvitedRecordMapper.class).insert(record);
            }
            
            ss.commit();
        } finally {
            ss.close();
        }
    }
  /**
   * 
   * TODO 模拟添加用户
   *
   * @since  v2.0
   * @author fufei
   * @created 2015年1月28日 下午4:23:09
   */
    public void testCreate() {
        SqlSession ss = SessionFactory.getSqlSessionWithoutAutoCommit();
        try {
            UserMapper mapper = ss.getMapper(UserMapper.class);
            User userInDb = mapper.selectByField(User.FIELD_NICK, this.nick);
            if (userInDb != null) {
                log.warn("Checked duplicated regist user={},email={}", userInDb.nick, userInDb.email);
                return;
            }
           
            
            this.activeCode = Codec.UUID().replace('-', '7');
            
            this.salt = String.valueOf(Math.random()).substring(2, 10);
            this.password = Codec.hexMD5(this.password + this.salt);
            this.vipStatus=VipStatus.SPECIAL;
            this.status = UserStatus.ACTIVE;
            Date now = DateTime.now().toDate();
            this.registTime=now;
            this.createTime = now;
            this.modifyTime = now;
            mapper.insert(this);
            ss.commit();
        } finally {
            ss.close();
        }
    }
    
    /**
     * 
     * 修改密码
     *
     * @param id
     * @param pass
     * @since  v0.1
     * @author moloch
     * @created 2014-8-20 下午12:06:49
     */
    public static void updatePass(long id, String pass){
    	SqlSession ss = SessionFactory.getSqlSession();
    	try{
    	    //取出salt生成新密码
    		UserMapper mapper = ss.getMapper(UserMapper.class);
    		String salt = mapper.selectById(id).salt;
    		System.out.println("----------"+salt);
    		User user = User.instance(id);
    		user.salt = salt;
    		user.password = Codec.hexMD5(pass + salt);
    		
    		//更新密码
    		mapper.updateById(user);
    		
    		// Cleaer cache
            Cache.safeDelete(CacheType.USER_INFO.getKey(id));
    	}finally{
    		ss.close();
    	}
    }
    
    /**
     * 
     * TODO 激活账户后active_code 字段为null.
     *
     * @param id
     * @since  v0.1
     * @author playlaugh
     * @created 2014年8月2日 下午9:43:45
     */
    public static void updateActiveCode(long id){
    	SqlSession ss = SessionFactory.getSqlSession();
        try {
            UserMapper mapper = ss.getMapper(UserMapper.class);
            mapper.updateActiveCode(id);
        } finally {
            ss.close();
        }
    }
    
    /**
	 * 更改用户的VIP等级
	 *
	 * @param id2
	 * @param vipStatus2
	 * @since  v2.0
	 * @author Mark Xu
	 * @created 2015-3-6 上午11:22:44
	 */
	public void changeVipStatus(long id, VipStatus vipStatus) {
		// TODO Auto-generated method stub
		SqlSession ss = SessionFactory.getSqlSession();
		try {
			UserMapper mapper = ss.getMapper(UserMapper.class);
			User user = new User();
			user.id = id;
			user.vipStatus = vipStatus;
			mapper.updateById(user);
		} finally {
			ss.close();
		}
	}
	/**
	 * 
	 * 后台统计新增买手接单数
	 *
	 * @since  v2.9
	 * @author fufei
	 * @created 2015年3月23日 下午3:21:45
	 */
	public static List<User> findUserTakenCount(String takenTime) {
        SqlSession ss = SessionFactory.getSqlSession();
        try {
            UserMapper mapper = ss.getMapper(UserMapper.class);
           return mapper.findUserTakenCount(takenTime);
        } finally {
            ss.close();
        }
    }
    
    
    /**
     * 
     * 更新用户缓存.
     *
     * @since  0.1
     * @author youblade
     * @created 2014年9月12日 下午8:14:16
     */
    public void updateCache() {
        if (this.isBuyer()) {
            cacheLog.debug("Buyer={} deposit={},ingot={},experience={}", new Object[] { this.id, this.deposit,
                    this.ingot, this.experience });
        } else {
            cacheLog.debug("Seller={} pledge={},ingot={}", new Object[] { this.id, this.pledge, this.ingot });
        }
        CacheUtil.setJson(CacheType.USER_INFO.getKey(this.id), this, CacheType.USER_INFO.expiredTime);
    }
    

    /**
     * 
     * 分页获取用户数据（后台管理）.
     *
     * @param vo
     * @return
     * @since  0.1
     * @author youblade
     * @created 2014年9月22日 下午1:39:00
     */
    public static Page<User> findByPage(UserSearchVo vo) {
        // 过滤参数
        vo.nick = StringUtils.trimToNull(vo.nick);
        vo.email = StringUtils.trimToNull(vo.email);
        vo.qq = StringUtils.trimToNull(vo.qq);
        vo.mobile = StringUtils.trimToNull(vo.mobile);
        vo.adminName = StringUtils.trimToNull(vo.adminName);

        SqlSession ss = SessionFactory.getSqlSession();
        try {
            UserMapper mapper = ss.getMapper(UserMapper.class);
            int totalCount = mapper.count(vo);
            if (totalCount <= 0) {
                return Page.EMPTY;
            }

            Page<User> page = Page.newInstance(vo.pageNo, vo.pageSize, totalCount);
            page.items = mapper.selectListForAdmin(vo);
            return page;
        } finally {
            ss.close();
        }
    }
    
    public static boolean isInvited(long userId){
        SqlSession ss = SessionFactory.getSqlSession();
        try {
            UserInvitedRecordMapper mapper = ss.getMapper(UserInvitedRecordMapper.class);
            UserInvitedRecord record = mapper.selectByInvitedUserId(userId);
            if(record == null){
                return false;
            }else{
                return true;
            }
        } finally {
            ss.close();
        }
    }
    /**
     * 
     * 临时接口，延长一个月会员
     *
     * @param userId
     * @return
     * @since  v2.6
     * @author fufei
     * @created 2015年2月10日 上午11:42:20
     */
    public static void delayUserMember(long userId,Date dueTimes){
        SqlSession ss = SessionFactory.getSqlSession();
        try {
            UserMapper mapper = ss.getMapper(UserMapper.class);
            mapper.delayUserMember(userId, dueTimes);
        } finally {
            ss.close();
        }
    }
    
    public boolean isBuyer() {
        return this.type == UserType.BUYER;
    }
    public boolean isSeller() {
        return this.type == UserType.SELLER;
    }
    
    public static User instance(long userId){
        User u = new User();
        u.id = userId;
        return u; 
    }

    public User status(UserStatus status) {
        this.status = status;
        return this;
    }

    public User regitstTime(DateTime time) {
        this.registTime = time.toDate();
        return this;
    }
    
    public User dueTime(LocalDate time) {
        this.dueTime = time.toDate();
        return this;
    }
    
    public User ingot(long ingot) {
        this.ingot = ingot;
        return this;
    }
    public User flow(long flow) {
        this.flow = flow;
        return this;
    }
    
    public User pledge(long pledge) {
        this.pledge = pledge;
        return this;
    }
    
    public User experience(long experience) {
        this.experience = experience;
        return this;
    }

    public User deposit(long deposit) {
        this.deposit = deposit;
        return this;
    }

    
 	public User buyerAndSellerTime(int buyerAndSellerTime) {
 		this.buyerAndSellerTime = buyerAndSellerTime;
 		return this;
 	}
 	
 	public User buyerAndShopTime(int buyerAndShopTime) {
 		this.buyerAndShopTime = buyerAndShopTime;
 		return this;
 	}
 	
 	public User buyerAcountAndShopTime(int buyerAcountAndShopTime) {
 		this.buyerAcountAndShopTime = buyerAcountAndShopTime;
 		return this;
 	}
 	
 	public User buyerAcountAndItemTime(int buyerAcountAndItemTime) {
 		this.buyerAcountAndItemTime = buyerAcountAndItemTime;
 		return this;
 	}

	public User modifyTime(Date modifyTime) {
		this.modifyTime = modifyTime;
        return this;
	}
 	
}
