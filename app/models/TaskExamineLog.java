package models;

import java.util.Date;

import models.mappers.TaskExamineLogMapper;

import org.apache.ibatis.session.SqlSession;
import org.joda.time.DateTime;

import com.aton.db.SessionFactory;
import com.aton.util.TenpayUtil;

/**
 * 
 * 
 * 任务审核记录
 * 
 * @author moloch
 * @since v0.1
 * @created 2014-9-18 上午11:08:34
 */
public class TaskExamineLog {
	
	public static final String TABLE_NAME ="task_examine_log";

	public Long id;

	/** 后台登陆id */
	public Long adminId;

	/** 在审核任务的id */
	public Long taskId;

	/** 是否通过审核 */
	public boolean isPass;

	/** 备注 */
	public String memo;

	/** 开始审核时间 */
	public Date startTime;

	/** 结束审核时间 */
	public Date finishTime;

	/** 记录创建时间 */
	public Date createTime;
	
	/**
	 * 
	 * 保存记录
	 * 
	 * @since v0.1
	 * @author moloch
	 * @created 2014-9-18 下午3:40:43
	 */
	public void save() {
		SqlSession ss = SessionFactory.getSqlSession();
		try {
			TaskExamineLogMapper mapper = ss.getMapper(TaskExamineLogMapper.class);

			// 更新记录
			if (this.id != null && this.id > 0) {
				this.finishTime = DateTime.now().toDate();
				mapper.updateById(this);
				return;
			}

			// 创建新记录
			this.createTime = DateTime.now().toDate();
			this.startTime = this.createTime;
			mapper.insert(this);
		} finally {
			ss.close();
		}
	}

}
