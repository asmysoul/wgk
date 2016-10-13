package models;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import models.User.UserType;
import models.mappers.AdminTradeLogMapper;
import models.mappers.PayTradeLogMapper;
import models.mappers.TaskMapper;
import models.mappers.TaskMapper2;
import models.mappers.TaskMapper3;

import org.apache.ibatis.session.SqlSession;
import org.joda.time.DateTime;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import vos.Page;

import com.aton.db.SessionFactory;
import com.aton.util.KQpayUtil;

import enums.TaskStatus;
import enums.pay.PayPlatform;
import enums.pay.TradeResult;
import enums.pay.TradeType;

/**
 * 
 * 支付交易记录.
 * 
 * @author youblade
 * @since v1.0
 * @created 2014年11月13日 下午8:43:32
 */
public class PayTradeLog {

    // TODO 配置log单独输出到指定文件
    public static final Logger log = LoggerFactory.getLogger(PayTradeLog.class);

    public static final String TABLE_NAME = "pay_trade_log";

    public Long id;
    /**
     * 商户订单号
     */
    public String tradeNo;
    /**
     * 业务字段：本地业务记录的ID
     */
    public Long bizId;
    /**
     * 业务字段：会员开通时间
     */
    public Integer bizMemberMonth;
    /**
     * 业务字段：收费业务类型
     */
    public TradeType type;
    /**
     * 交易金额：精确到分<br>
     * 【注意】接收到支付成功通知消息时需要再次校验该金额
     */
    public Long amount;
    /**
     * 该笔交易的商户手续费
     */
    public Long fee;
    
    public Long userId;
    
    public String userNick;
    
    public UserType userType;
    /**
     * 支付平台交易流水号（退款时需要）
     */
    public String dealId;

    /**
     * 该交易在银行支付时对庒的交易号，如果丌是通过银行卡支付，则为空
     */
    public String bankDealId;
    public PayPlatform bank;

    public TradeResult result;
    public String memo;

    /**
     * 已提现金额：精确到分<br>
     */
    public Long withdrawAmount;
    public Date createTime;
    public Date modifyTime;

    /**
     * 
     * 保存/更新交易记录.
     * 
     * @since 0.1
     * @author youblade
     * @created 2014年9月10日 下午3:15:59
     */
    public void save() {
        SqlSession ss = SessionFactory.getSqlSessionWithoutAutoCommit();
        try {
            PayTradeLogMapper mapper = ss.getMapper(PayTradeLogMapper.class);
            this.modifyTime = DateTime.now().toDate();

            // 更新记录
            if (this.id != null && this.id > 0) {
                mapper.updateById(this);
                return;
            }

            // 创建新记录
            this.tradeNo = KQpayUtil.createTradeNo();
            this.createTime = modifyTime;
            mapper.insert(this);
            
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            throw new RuntimeException("[支付]交易记录保存失败~~");
        } finally {
            ss.commit();
            ss.close();
        }
    }

    /**
     * 
     * 查询记录.
     * 
     * @param id
     * @return
     * @since 0.1
     * @author youblade
     * @created 2014年9月11日 下午7:18:26
     */
    public static PayTradeLog findById(long id) {
        SqlSession ss = SessionFactory.getSqlSessionForBatch();
        try {
            PayTradeLogMapper mapper = ss.getMapper(PayTradeLogMapper.class);
            return mapper.selectById(id);
        } finally {
            ss.close();
        }
    }

    /**
     * 
     * 根据"交易号（商户订单ID）"查询记录.
     * 
     * @param tradeNo
     * @return
     * @since 0.1
     * @author youblade
     * @created 2014年9月17日 下午7:01:41
     */
    public static PayTradeLog findByTradeNo(String tradeNo) {
        SqlSession ss = SessionFactory.getSqlSessionForBatch();
        try {
            PayTradeLogMapper mapper = ss.getMapper(PayTradeLogMapper.class);
            return mapper.selectByField("trade_no", tradeNo);
        } finally {
            ss.close();
        }
    }
    
    
    
    
    
    /**
     * 
     * 为支付[任务]创建交易记录，若之前有“未支付”的记录则直接拿来重新使用
     *
     * @return
     * @since  v1.2
     * @author youblade
     * @created 2014年11月18日 下午10:51:04
     */
    public static long findOrCreate3(Task3 task, long payFee) {
    	SqlSession ss = SessionFactory.getSqlSessionWithoutAutoCommit();
        try {
            // 发布任务时使用网银支付则将其状态置为“待支付”
            Date now = DateTime.now().toDate();
            Task3 t = Task3.instance(task.id).status(TaskStatus.WAITING_PAY).modifyTime(now);
            ss.getMapper(TaskMapper3.class).updateById(t);
            
            PayTradeLogMapper mapper = ss.getMapper(PayTradeLogMapper.class);
            PayTradeLog log = mapper.selectByTypeAndBizIdAndResult(TradeType.TASK, task.id, TradeResult.UNTREATED);
            // 当存在【该任务】的【待处理】的【相同金额】的交易记录，则重新使用该记录（向支付网关发起支付请求）
            if (log != null && log.amount == payFee) {
                return log.id;
            }

            // 该任务为初次支付时创建新的交易记录
            log = PayTradeLog.newInstance(TradeType.TASK, task.sellerId).amount(payFee).bizId(task.id)
                .memo("发布任务[" + task.id + "]");
            log.tradeNo = KQpayUtil.createTradeNo();
            log.createTime = now;
            log.modifyTime = now;
            mapper.insert(log);
            
            
            return log.id;
        }catch(Exception e){
            ss.rollback();
            log.error(e.getMessage(), e);
            throw new RuntimeException("为任务床架");
        } finally {
            ss.commit();
            ss.close();
        }
    }
    
    
    
    
    
    
    /**
     * 
     * 为支付[任务]创建交易记录，若之前有“未支付”的记录则直接拿来重新使用
     *
     * @return
     * @since  v1.2
     * @author youblade
     * @created 2014年11月18日 下午10:51:04
     */
    public static long findOrCreate(Task task, long payFee) {
        SqlSession ss = SessionFactory.getSqlSessionWithoutAutoCommit();
        try {
            // 发布任务时使用网银支付则将其状态置为“待支付”
            Date now = DateTime.now().toDate();
            Task t = Task.instance(task.id).status(TaskStatus.WAITING_PAY).modifyTime(now);
            ss.getMapper(TaskMapper.class).updateById(t);
            
            PayTradeLogMapper mapper = ss.getMapper(PayTradeLogMapper.class);
            PayTradeLog log = mapper.selectByTypeAndBizIdAndResult(TradeType.TASK, task.id, TradeResult.UNTREATED);
            // 当存在【该任务】的【待处理】的【相同金额】的交易记录，则重新使用该记录（向支付网关发起支付请求）
            if (log != null && log.amount == payFee) {
                return log.id;
            }

            // 该任务为初次支付时创建新的交易记录
            log = PayTradeLog.newInstance(TradeType.TASK, task.sellerId).amount(payFee).bizId(task.id)
                .memo("发布任务[" + task.id + "]");
            log.tradeNo = KQpayUtil.createTradeNo();
            log.createTime = now;
            log.modifyTime = now;
            mapper.insert(log);
            
            
            return log.id;
        }catch(Exception e){
            ss.rollback();
            log.error(e.getMessage(), e);
            throw new RuntimeException("为任务床架");
        } finally {
            ss.commit();
            ss.close();
        }
    }
    
    
    
    /**
     * 
     * 为支付[推广]创建交易记录，若之前有“未支付”的记录则直接拿来重新使用
     *
     * @return
     * @since  v1.2
     * @author youblade
     * @created 2014年11月18日 下午10:51:04
     */
    public static long findOrCreate2(Task2 task, long payFee) {
        SqlSession ss = SessionFactory.getSqlSessionWithoutAutoCommit();
        try {
            // 发布任务时使用网银支付则将其状态置为“待支付”
            Date now = DateTime.now().toDate();
            Task2 t = Task2.instance(task.id).status(TaskStatus.WAITING_PAY).modifyTime(now);
            ss.getMapper(TaskMapper2.class).updateById(t);
            
            PayTradeLogMapper mapper = ss.getMapper(PayTradeLogMapper.class);
            PayTradeLog log = mapper.selectByTypeAndBizIdAndResult(TradeType.TASK, task.id, TradeResult.UNTREATED);
            // 当存在【该任务】的【待处理】的【相同金额】的交易记录，则重新使用该记录（向支付网关发起支付请求）
            if (log != null && log.amount == payFee) {
                return log.id;
            }

            // 该任务为初次支付时创建新的交易记录
            log = PayTradeLog.newInstance(TradeType.TASK, task.sellerId).amount(payFee).bizId(task.id)
                .memo("发布任务[" + task.id + "]");
            log.tradeNo = KQpayUtil.createTradeNo();
            log.createTime = now;
            log.modifyTime = now;
            mapper.insert(log);
            
            
            return log.id;
        }catch(Exception e){
            ss.rollback();
            log.error(e.getMessage(), e);
            throw new RuntimeException("为任务床架");
        } finally {
            ss.commit();
            ss.close();
        }
    }
    
    
    
    
    
    public class FinanceLogVo extends Page{
    	public Date date;
    	public long amount;
    	public long platAmount;
    	public long adminAmount;
    }
    /**
     * 
     * 获取供商家提现处理的交易记录.
     * 交易成功的、存在可提现金额的记录
     * 
     * @param uid
     * @param pageSize
     * @return
     * @since v0.2.5
     * @author youblade
     * @created 2014年11月4日 下午6:44:45
     */
    public static List<PayTradeLog> findListForSellerWithdraw(Long uid, int pageSize, long amount) {
        SqlSession ss = SessionFactory.getSqlSessionForBatch();
        try {
            PayTradeLogMapper mapper = ss.getMapper(PayTradeLogMapper.class);
            return mapper.selectListForRefund(uid, pageSize);
        } finally {
            ss.close();
        }
    }

    /**
	 * 
	 *
	 * @param 查询平台进账明细
	 * @return
	 * @since  v2.0
	 * @author Mark Xu
	 * @created 2015-4-19 下午2:41:52
	 */
	public static List<PayTradeLog> findByDate(String date) {
		SqlSession ss = SessionFactory.getSqlSession();
		List<PayTradeLog> list = new ArrayList<PayTradeLog>();
    	try {
    		PayTradeLogMapper mapper = ss.getMapper(PayTradeLogMapper.class);
    		if(mapper.platCount(date)==0) {
    			return list;
    		}
    		
    		list = mapper.platCountList(date);
    		return list;
    		
    	}finally {
    		ss.close();
    	}
	}
	
    /**
     * 
     * 生成新记录：默认为“未处理”状态.
     * 
     * @param type
     * @param userId
     * @return
     * @since v0.2.4
     * @author youblade
     * @created 2014年10月31日 下午4:31:04
     */
    public static PayTradeLog newInstance(TradeType type, long userId) {
        PayTradeLog log = new PayTradeLog();
        log.type = type;
        if (type == TradeType.INGOT) {
            log.memo("充值金币");
        }
        if (type == TradeType.PLEDGE) {
            log.memo("充值押金");
            log.withdrawAmount = 0L;
        }
        if (type == TradeType.TASK) {
            log.withdrawAmount = 0L;
        }
        log.userId = userId;
        log.result = TradeResult.UNTREATED;
        return log;
    }

	
    public static PayTradeLog instance(Long id) {
        PayTradeLog log = new PayTradeLog();
        log.id = id;
        return log;
    }

    public PayTradeLog bizId(Long bizId) {
        this.bizId = bizId;
        return this;
    }

    public PayTradeLog bizMemberMonth(int month) {
        this.bizMemberMonth = month;
        return this;
    }

    public PayTradeLog amount(long amount) {
        this.amount = amount;
        return this;
    }

    public PayTradeLog type(TradeType type) {
        this.type = type;
        return this;
    }

    public PayTradeLog result(TradeResult result) {
        this.result = result;
        return this;
    }

    public PayTradeLog memo(String memo) {
        this.memo = memo;
        return this;
    }

    public PayTradeLog bank(PayPlatform bank, String bankDealId) {
        this.bank = bank;
        this.bankDealId = bankDealId;
        return this;
    }

    public PayTradeLog fee(Long fee) {
        this.fee = fee;
        return this;
    }
    
    public PayTradeLog dealId(String dealId) {
        this.dealId = dealId;
        return this;
    }

}
