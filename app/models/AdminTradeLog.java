package models;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;

import models.PayTradeLog.FinanceLogVo;
import models.User.UserType;
import models.mappers.AdminOperatorLogMapper;
import models.mappers.AdminTradeLogMapper;

import org.apache.ibatis.session.SqlSession;
import org.joda.time.DateTime;

import play.data.validation.Required;
import vos.AdminOperatorLogSearchVo;
import vos.Page;

import com.aton.db.SessionFactory;
import com.aton.util.StringUtils;

import enums.Sign;


//管理员对用户的金币押金操作记录表（用于统计平台资金流动）
public class AdminTradeLog {
	public static final String TABLE_NAME = "admin_trade_log";
	
	public long id;
	public AdminTradeType type;
	//金额（分）
	public Sign sign;
	public long amount;
	
	public long userId;
	
	public String userNick;
	
	public UserType userType;
	
	public String result;
	
	public String memo;
	
	public Date createTime;
	
	public long adminId;
	
	public String adminName;
	
	//类型
	public enum AdminTradeType{
		BUYER_INGOT("变更买手金币"),
		SELLER_INGOT("变更商家金币"),
		BUYER_PLEDGE("变更买手押金"),
		SELLER_PLEDGE("变更商家押金");
		public String title;
        private AdminTradeType(String title){
            this.title = title;
        }
	}
	
	public AdminTradeLog tradeType(AdminTradeType type){
        this.type = type;
        return this;
    }
	public AdminTradeLog sign(Sign sign){
        this.sign = sign;
        return this;
    }
	public AdminTradeLog amount(long amount){
        this.amount = amount;
        return this;
    }
	public AdminTradeLog userId(long userId){
        this.userId = userId;
        return this;
    }
	public AdminTradeLog result(String result){
        this.result = result;
        return this;
    }
	public AdminTradeLog memo(String memo){
        this.memo = memo;
        return this;
    }
	public AdminTradeLog adminId(long adminId){
        this.adminId = adminId;
        return this;
    }
	public AdminTradeLog createTime(Date createTime){
        this.createTime = createTime;
        return this;
    }
	/**
	 * 插入数据
	 *
	 * @param log
	 * @since  v2.0
	 * @author Mark Xu
	 * @created 2015-4-16 下午2:16:17
	 */
	public static void insert(AdminTradeLog log) {
		log.createTime = DateTime.now().toDate();
		SqlSession ss = SessionFactory.getSqlSession();
		try {
			AdminTradeLogMapper mapper = ss.getMapper(AdminTradeLogMapper.class);
			mapper.insert(log);
		}finally {
			ss.close();
		}
	}
	/**
	 * 
	 * 查询后台进账总额
	 *
	 * @param vo
	 * @return
	 * @since  v2.0
	 * @author Mark Xu
	 * @created 2015-4-18 下午5:20:24
	 */
    public static Page<FinanceLogVo> findListForFinance(FinanceLogVo vo) {
    	SqlSession ss = SessionFactory.getSqlSession();
    	try {
    		AdminTradeLogMapper mapper = ss.getMapper(AdminTradeLogMapper.class);
    		int totalCount = mapper.count(vo);
    		if (totalCount <= 0) {
    			return Page.EMPTY;
    		}
    		vo.totalCount = totalCount;
    		vo.items = mapper.selectListForFinance(vo);
    		return vo;
    	}finally {
    		ss.close();
    	}
    }
	/**
	 * 查询后台进账明细
	 *
	 * @param date
	 * @return
	 * @since  v2.0
	 * @author Mark Xu
	 * @created 2015-4-19 下午1:43:48
	 */
	public static List<AdminTradeLog> findByDate(String date) {
		SqlSession ss = SessionFactory.getSqlSession();
		List<AdminTradeLog> list = new ArrayList<AdminTradeLog>();
    	try {
    		AdminTradeLogMapper mapper = ss.getMapper(AdminTradeLogMapper.class);
    		if(mapper.adminCount(date)==0) {
    			return list;
    		}
    		
    		list = mapper.adminCountList(date);
    		return list;
    		
    	}finally {
    		ss.close();
    	}
	}
	
}