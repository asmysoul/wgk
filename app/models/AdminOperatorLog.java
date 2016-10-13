package models;

import java.util.Collections;
import java.util.Date;

import models.mappers.AdminOperatorLogMapper;

import org.apache.ibatis.session.SqlSession;
import org.joda.time.DateTime;

import play.data.validation.Required;
import vos.AdminOperatorLogSearchVo;
import vos.Page;

import com.aton.db.SessionFactory;
import com.aton.util.StringUtils;

/**
 * 
 * 管理员类用户操作买手金币、商家金币、商家押金、审核买号、审核任务、审核任务为不通过、取消任务以及取消子任务的记录
 * 
 * @author Mark Xu
 * @since  v2.0
 * @created 2015-1-24 下午3:50:35
 */
public class AdminOperatorLog {
	
	public static final String TABLE_NAME = "admin_operator_log";
	
	public long id;
	public String adminAccount;
	public LogType logType;
	public String message;
	public Date operatorTime;
	
	public enum LogType{
		BUYER_INGOT("变更买手金币"),
		BUYER_DEPOSIT("变更买手本金"),
		SELLER_INGOT("变更商家金币"),
		SELLER_PLEDGE("变更商家押金"),
		AUDIT_SELLER("审核买号"),
		AUDIT_SELLER_FAIL("审核买号不通过"),
		AUDIT_TASK("审核任务通过"),
		AUDIT_TASK_FAIL("审核不通过"),
		CANCEL_TASK("取消任务"),
		CANCEL_SUB_TASK("取消子任务"),
		CHANGE_USER("更改用户信息");
		public String title;
        private LogType(String title){
            this.title = title;
        }
	}

	/**
	 * 根据用户插入数据
	 *
	 * @since  v2.0
	 * @author Mark Xu
	 * @created 2015-1-26 下午1:53:03
	 */
	public static void insert(@Required String adminAccount, @Required LogType operatorType, @Required String message) {
		AdminOperatorLog log = new AdminOperatorLog();
		log.operatorTime = DateTime.now().toDate();
		log.adminAccount = adminAccount;
		log.logType = operatorType;
		log.message = message;
		SqlSession ss = SessionFactory.getSqlSession();
		try {
			AdminOperatorLogMapper mapper = ss.getMapper(AdminOperatorLogMapper.class);
			mapper.insert(log);
		}finally {
			ss.close();
		}
	}
	
	public static void insert(@Required String adminAccount, @Required LogType operatorType, @Required String message, @Required SqlSession ss) {
		AdminOperatorLog log = new AdminOperatorLog();
		log.operatorTime = DateTime.now().toDate();
		log.adminAccount = adminAccount;
		log.logType = operatorType;
		log.message = message;
		AdminOperatorLogMapper mapper = ss.getMapper(AdminOperatorLogMapper.class);
		mapper.insert(log);
	}

	/**
	 * 通过Page集成类获取管理员操作记录
	 *
	 * @param vo
	 * @return
	 * @since  v2.0
	 * @author Mark Xu
	 * @created 2015-1-27 下午5:08:13
	 */
	public static Page<AdminOperatorLog> findByPage(AdminOperatorLogSearchVo vo) {
		vo.adminAccount = StringUtils.trimToNull(vo.adminAccount);//过滤参数
		vo.message = StringUtils.trimToNull(vo.message);
		if(vo.message!=null) {
			vo.message = '%' + vo.message + '%';
		}
		SqlSession ss = SessionFactory.getSqlSession();
        try {
            AdminOperatorLogMapper mapper = ss.getMapper(AdminOperatorLogMapper.class);
            int totalCount = mapper.count(vo);
            if (totalCount <= 0) {
                return Page.EMPTY;
            }

            Page<AdminOperatorLog> page = Page.newInstance(vo.pageNo, vo.pageSize, totalCount);
            page.items = mapper.selectListForAdminOperatorLog(vo);
            return page;
        } finally {
            ss.close();
        }
	}
	
}