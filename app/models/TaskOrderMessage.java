package models;

import java.util.Date;
import java.util.List;

import models.mappers.TaskItemSearchPlanMapper;
import models.mappers.TaskOrderMessageMapper;

import org.apache.commons.lang.StringUtils;
import org.apache.ibatis.session.SqlSession;
import org.apache.poi.util.StringUtil;
import org.joda.time.DateTime;

import com.aton.db.SessionFactory;
import com.aton.util.MixHelper;
import com.aton.util.ReflectionUtil;
import com.google.common.collect.Ordering;
import com.google.common.primitives.Doubles;

public class TaskOrderMessage {

    public static final String TABLE_NAME = "task_order_message";
    
    public Long id;
    public Long taskId;
    
	public String message;
	public Integer usedNum;
	
	public Date createTime;
	public Date modifyTime;
	
	public static TaskOrderMessage newInstance(long taskId){
		TaskOrderMessage o = new TaskOrderMessage();
		o.taskId = taskId;
		return o;
	}
	
	/**
	 * 
	 * 随机取得一个使用次数少的订单留言
	 * 
	 * @param taskId
	 * @return
	 * @since v0.1
	 * @author moloch
	 * @created 2014-9-24 下午1:19:33
	 */
	public static TaskOrderMessage getOneMessage(long taskId) {
		SqlSession ss = SessionFactory.getSqlSession();
		try {
			TaskOrderMessageMapper tomMapper = ss.getMapper(TaskOrderMessageMapper.class);
			List<TaskOrderMessage> toms = tomMapper.selectByTaskId(taskId);

			if (MixHelper.isEmpty(toms)) {
				return null;
			}

			// 按照usedNum排序
			final String fieldName = "usedNum";
			Ordering<TaskOrderMessage> ordMessage = new Ordering<TaskOrderMessage>() {
				public int compare(TaskOrderMessage tom1, TaskOrderMessage tom2) {
					Object val1 = ReflectionUtil.getFieldValue(tom1, fieldName);
					Object val2 = ReflectionUtil.getFieldValue(tom2, fieldName);
					double d1 = Doubles.tryParse(val1.toString());
					double d2 = Doubles.tryParse(val2.toString());
					Double offset = d2 - d1;
					if (offset.doubleValue() > 0) {
						return 1;
					} else {
						return -1;
					}
				}
			};

			toms = ordMessage.sortedCopy(toms);
			return toms.get(toms.size() - 1);
		} finally {
			ss.close();
		}
	}
	
}
