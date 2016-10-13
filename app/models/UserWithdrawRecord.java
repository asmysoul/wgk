package models;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

import javax.persistence.Transient;

import models.SellerPledgeRecord.PledgeAction;
import models.User.UserType;
import models.mappers.BuyerTaskMapper;
import models.mappers.SellerPledgeRecordMapper;
import models.mappers.UserIngotRecordMapper;
import models.mappers.UserWithdrawRecordMapper;
import models.mappers.fund.BuyerDepositRecordMapper;

import org.apache.commons.lang.Validate;
import org.apache.ibatis.session.SqlSession;
import org.joda.time.DateTime;
import org.joda.time.LocalDate;

import vos.Page;

import com.aton.config.BizConstants;
import com.aton.db.SessionFactory;
import com.aton.util.Pandora;
import com.aton.util.StringUtils;

import controllers.MoneyManage.MoneyRecordSearchVo;
import controllers.MoneyManage.WithdrawSearchVo;

/**
 * 
 * 用户提现申请记录、提现资金记录.
 * 
 * @author youblade
 * @since  0.1
 * @created 2014年10月14日 上午10:33:11
 */
public class UserWithdrawRecord {

    public static final String TABLE_NAME = "user_withdraw_record";

    public Long id;
    public Long userId;
    public Long fundAccountId;
    /**
     * 申请提现金额
     */
    public Long applyAmount;
    /**
     * 实际提现金额（扣除手续费后）
     */
    public Long amount;
    
    /**
     * 转账交易号信息：分笔转账时会有多个<br>
     * 一般格式为：[银行][银行转账交易号 或 银行订单ID]
     */
    public String tradeNo;
    
    /**
     * 提现处理状态
     */
    public WithdrawStatus status;

    public enum WithdrawStatus {
        /** 待处理 */
        WAIT("待处理"),
        PROCESSING("处理中"),
        /**
         * 不符合提现条件，管理员撤销申请
         */
        CANCELED("已撤销"),
        /** 已退款 */
        FINISHED("已退款");

        public String name;

        private WithdrawStatus(String title) {
            this.name = title;
        }
    }

    /**
     * 是否为买手本金提现
     */
    public Boolean isBuyerDeposit;
    /**
     * 提现流水号：主要用于与buyerTask进行关联，否则无法在事务中同时处理这两张表
     */
    public Long sn;
    
    /**
     * 管理员撤销提现申请时填写的备注
     */
    public String memo;
    public Date applyTime;
    public Date modifyTime;

    @Transient
    public String userNick;
    @Transient
    public UserType userType;
    @Transient
    public String fundAccountType;
    
    /**
     * 
     * 分页查询提现记录.
     * 
     * @param vo
     * @return
     * @since 0.1
     * @author youblade
     * @created 2014年9月16日 下午12:40:46
     */
    public static Page<UserWithdrawRecord> findByPage(MoneyRecordSearchVo vo) {
        SqlSession ss = SessionFactory.getSqlSession();
        try {
            UserWithdrawRecordMapper mapper = ss.getMapper(UserWithdrawRecordMapper.class);
            int totalCount = mapper.count(vo);
            if (totalCount <= 0) {
                return Page.EMPTY;
            }

            Page<UserWithdrawRecord> page = Page.newInstance(vo.pageNo, vo.pageSize, totalCount);
            page.items = mapper.selectList(vo);
            return page;
        } finally {
            ss.close();
        }
    }

    /**
     * 
     * 创建（卖家押金、买手佣金）提现申请记录.
     *
     * @since  0.1.7
     * @author youblade
     * @created 2014年10月16日 下午7:48:06
     */
    public void createApply(User user) {
        Validate.notNull(this.applyAmount);
        this.userId = user.id;
        
        // 卖家、买手按不同手续费比例计算实际提现金额
        BigDecimal feePercent = null;
        if (user.isSeller()) {
            feePercent = new BigDecimal(BizConstants.WITHDRAW_PERCENT_SELLER);
        } else {
            feePercent = new BigDecimal(BizConstants.WITHDRAW_PERCENT_BUYER);
        }
        this.amount = this.applyAmount - BigDecimal.valueOf(this.applyAmount).multiply(feePercent).longValue();
        
        SqlSession ss = SessionFactory.getSqlSessionWithoutAutoCommit();
        User userCached = User.findByIdWichCache(userId);
        try {
            Date now = DateTime.now().toDate();
            /*
             *  扣除提现资金
             */
            // 创建金币、押金记录，【注意】扣除金额为“申请提现”的金额
            if (user.isBuyer()) {
                UserIngotRecordMapper ingotMapper = ss.getMapper(UserIngotRecordMapper.class);
                UserIngotRecord lastRecord = ingotMapper.selectLastRecord(userId);
                UserIngotRecord record = UserIngotRecord.newInstance(userId, lastRecord).minus(this.applyAmount)
                    .createTime(now).memo("用户提现");
                ingotMapper.insert(record);
                
                userCached.ingot(record.balance);
            }else{
                SellerPledgeRecordMapper pledgeMapper = ss.getMapper(SellerPledgeRecordMapper.class);
                SellerPledgeRecord lastRecord = pledgeMapper.selectLastRecord(userId);
                SellerPledgeRecord record = SellerPledgeRecord.newInstance(userId, lastRecord)
                    .action(PledgeAction.WITHDRAW, this.applyAmount).createTime(now).memo("用户提现");
                pledgeMapper.insert(record);
                
                userCached.pledge(record.balance);
            }
            
            // 创建申请记录
            UserWithdrawRecordMapper mapper = ss.getMapper(UserWithdrawRecordMapper.class);
            this.status = WithdrawStatus.WAIT;
            this.isBuyerDeposit = Boolean.FALSE;
            this.modifyTime = now;
            this.applyTime = now;
            
            //提现都走银行卡
            FundAccount bank = FundAccount.findBank(user.id);
            this.fundAccountId= bank.id;
            mapper.insert(this);
            
            ss.commit();
            
            // 更新用户缓存
            userCached.updateCache();
        } finally {
            ss.close();
        }
    }
    
    /**
     * 
     * 创建【买手】垫付本金提现申请记录.
     *
     * @param bankAccount
     * @param buyerTaskIds
     * @since  v1.7
     * @author youblade
     * @created 2014年11月29日 下午8:44:07
     */
    public void createDepositApply(FundAccount bankAccount, List<Long> buyerTaskIds) {
        Validate.notNull(bankAccount);
        Validate.notNull(bankAccount.userId);
        
        this.fundAccountId = bankAccount.id;
        this.userId = bankAccount.userId;
        
        User userCached = User.findByIdWichCache(userId);
        SqlSession ss = SessionFactory.getSqlSessionWithoutAutoCommit();
        try {
            Date now = DateTime.now().toDate();
            long nextId = Pandora.getInstance().nextId();
            
            // 计算提现金额
            BuyerTaskMapper buyerTaskMapper = ss.getMapper(BuyerTaskMapper.class);
            long centAmount = buyerTaskMapper.sumPaidFeeByBuyerIdAndIds(this.userId, buyerTaskIds);
            // 检查账户本金余额是否足够本次提现
            if (userCached.deposit < centAmount) {
                throw new RuntimeException("可提现金额不足");
            }
            
            this.applyAmount = centAmount;
            this.amount = centAmount;
            
            /*
             * 使用“提现流水号”标记买手任务
             */
            for (Long buyerTaskId : buyerTaskIds) {
                BuyerTask buyerTask = BuyerTask.instance(buyerTaskId).withdrawSn(nextId).modifyTime(now);
                buyerTaskMapper.updateById(buyerTask);
            }
            /*
             *  扣除提现资金
             */
            BuyerDepositRecordMapper depositMapper = ss.getMapper(BuyerDepositRecordMapper.class);
            BuyerDepositRecord lastRecord = depositMapper.selectLastRecord(userId);
            BuyerDepositRecord record = BuyerDepositRecord.newInstance(userId, lastRecord).minus(this.applyAmount)
                .createTime(now).memo("用户提现本金");
            depositMapper.insert(record);
            
            
            // 创建申请记录
            UserWithdrawRecordMapper mapper = ss.getMapper(UserWithdrawRecordMapper.class);
            this.sn = nextId;
            this.isBuyerDeposit = true;
            this.status = WithdrawStatus.WAIT;
            this.modifyTime = now;
            this.applyTime = now;
            mapper.insert(this);
            
            ss.commit();
            
            // 更新用户缓存
            userCached.deposit(record.balance).updateCache();
        } finally {
            ss.close();
        }
    }
    
    /**
     * 
     * 保存（更新）提现记录.
     *
     * @since  0.1
     * @author youblade
     * @created 2014年9月16日 下午3:55:11
     */
    public void save() {
        SqlSession ss = SessionFactory.getSqlSession();
        try {
            UserWithdrawRecordMapper mapper = ss.getMapper(UserWithdrawRecordMapper.class);
            this.modifyTime = DateTime.now().toDate();
            mapper.updateById(this);
        } finally {
            ss.close();
        }
    }
    
    /**
     * 
     * 根据ID取得记录
     *
     * @param id
     * @return
     * @since  v0.1
     * @author moloch
     * @created 2014-10-2 下午4:35:44
     */
    public static UserWithdrawRecord findById(long id){
    	 SqlSession ss = SessionFactory.getSqlSession();
         try {
             UserWithdrawRecordMapper mapper = ss.getMapper(UserWithdrawRecordMapper.class);
             return mapper.findById(id);
         } finally {
             ss.close();
         }
    }
    
	/**
	 * 
	 * 【后台管理】分页获取待处理的提现申请
	 * 
	 * @param vo
	 * @return
	 * @since v0.1
	 * @author moloch
	 * @created 2014-10-3 下午12:51:52
	 */
	public static Page<UserWithdrawRecord> findByPageForAdmin(WithdrawSearchVo vo) {
        // 预处理传入的参数
        vo.userNick = StringUtils.trimToNull(vo.userNick);
        vo.tradeNo = StringUtils.trimToNull(vo.tradeNo);
        vo.buyerAccountNick = StringUtils.trimToNull(vo.buyerAccountNick);
        vo.sellerNick = StringUtils.trimToNull(vo.sellerNick);
        
		SqlSession ss = SessionFactory.getSqlSession();
		try {
			UserWithdrawRecordMapper mapper = ss.getMapper(UserWithdrawRecordMapper.class);
			int totalCount = mapper.countForAdmin(vo);
			if (totalCount <= 0) {
				return Page.EMPTY;
			}

			Page<UserWithdrawRecord> page = Page.newInstance(vo.pageNo, vo.pageSize, totalCount);
			page.items = mapper.selectForAdmin(vo);
			return page;
		} finally {
			ss.close();
		}
	}
	
	/**
	 * 
	 * 统计提现记录.
	 *
	 * @param vo
	 * @return
	 * @since  v1.1.7
	 * @author youblade
	 * @created 2014年10月17日 下午1:51:48
	 */
	public static int count(WithdrawSearchVo vo) {
	    SqlSession ss = SessionFactory.getSqlSession();
	    try {
	        UserWithdrawRecordMapper mapper = ss.getMapper(UserWithdrawRecordMapper.class);
	        return mapper.buyerIngotWithdrawCount(vo);
	    } finally {
	        ss.close();
	    }
	}
	
	/**
	 * 
	 * 统计当月内某用户的申请提现记录数.
	 *
	 * @param svo
	 * @return
	 * @since  v1.1.7
	 * @author youblade
	 * @created 2014年10月17日 下午2:18:20
	 */
    public static int countThisMonthApply(WithdrawSearchVo svo) {
        Validate.notNull(svo.userId);
        
        LocalDate monthStart = LocalDate.now().withDayOfMonth(1);
        svo.applyTimeStart = monthStart.toDate();
        return count(svo);
    }

	/**
	 * 
	 * 根据用户类型及申请金额构造提现记录（按比例计算）.
	 *
	 * @param applyAmount
	 * @return
	 * @since  0.1
	 * @author youblade
	 * @created 2014年10月14日 下午12:00:45
	 */
    public static UserWithdrawRecord newInstance(long applyAmount) {
        UserWithdrawRecord r = new UserWithdrawRecord();
        r.applyAmount = applyAmount;
        return r;
    }

}
