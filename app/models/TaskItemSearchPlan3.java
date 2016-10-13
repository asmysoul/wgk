package models;

import java.util.Date;
import java.util.List;
import java.util.Random;

import models.mappers.TaskItemSearchPlanMapper;
import models.mappers.TaskItemSearchPlanMapper3;

import org.apache.commons.lang.StringUtils;
import org.apache.ibatis.session.SqlSession;
import org.joda.time.DateTime;

import com.aton.db.SessionFactory;
import com.aton.util.MixHelper;

public class TaskItemSearchPlan3 {

    public static final String TABLE_NAME = "task_item_search_plan3";
    
    public long id;
    public long taskId;
    public long buyerTaskId;
    
	public String word;
	public String skus;
	public Boolean inTmall;
	
	public Date createTime;
	public Date modifyTime;
	
	public long takenNum;//已接单数
	public long totalNum;//总单数
	public long flowNum;
	
	public static TaskItemSearchPlan3 newInstance(String word){
		TaskItemSearchPlan3 p = new TaskItemSearchPlan3();
		p.word = word;
		return p;
	}
	
	public TaskItemSearchPlan3 setSkus(String skus){
		this.skus = skus;
		return this;
	}
	
	public TaskItemSearchPlan3 inTmall(boolean inTmall){
	    this.inTmall = inTmall;
	    return this;
	}

	/**
	 * 
	 * TODO 批量保存.
	 *
	 * @param searchPlans
	 * @since  0.1
	 * @author youblade
	 * @created 2014年8月13日 下午6:25:11
	 */
	@Deprecated
    public static void batchSave(long taskId, List<TaskItemSearchPlan3> searchPlans) {

        SqlSession ss = SessionFactory.getSqlSessionForBatch();
        try {
            TaskItemSearchPlanMapper3 mapper = ss.getMapper(TaskItemSearchPlanMapper3.class);
            Date createTime = DateTime.now().toDate();
            for (TaskItemSearchPlan3 plan : searchPlans) {
                // 跳过关键词为空的记录
                if(StringUtils.isBlank(plan.word)){
                    continue;
                }
                
                //TODO 删除旧的记录
                
                plan.taskId = taskId;
                plan.createTime = createTime;
                plan.modifyTime = createTime;
                mapper.insert(plan);
            }
            
            ss.commit();
        } finally {
            ss.close();
        }
    }
	
	/**
	 * 
	 * 
	 * @param taskId
	 * @return
	 * @since v0.1
	 * @author moloch
	 * @created 2014-9-24 下午5:51:22
	 */
	public static TaskItemSearchPlan3 getOnePlan(long taskId) {
		SqlSession ss = SessionFactory.getSqlSession();
		try {
			TaskItemSearchPlanMapper3 tispMapper = ss.getMapper(TaskItemSearchPlanMapper3.class);
			List<TaskItemSearchPlan3> plans = tispMapper.selectByTaskId(taskId);
			if(MixHelper.isEmpty(plans)){
				return null;
			}
			
			int i = new Random().nextInt(plans.size());
			return plans.get(i);

		} finally {
			ss.close();
		}
	}
	/**
	 * 
	 * 为防止老的数据错误，按原来逻辑随机取一种搜索方案
	 *
	 * @param taskId
	 * @return
	 * @since  v2.6
	 * @author fufei
	 * @created 2015年2月27日 下午2:27:05
	 */
	public static TaskItemSearchPlan3 getOnePlanOld(long taskId) {
		SqlSession ss = SessionFactory.getSqlSession();
		try {
			TaskItemSearchPlanMapper3 tispMapper = ss.getMapper(TaskItemSearchPlanMapper3.class);
			List<TaskItemSearchPlan3> plans = tispMapper.getOneTaskPlan(taskId);
			if(MixHelper.isEmpty(plans)){
				return null;
			}
			
			int i = new Random().nextInt(plans.size());
			return plans.get(i);

		} finally {
			ss.close();
		}
	}
	/**
	 * 
	 * 
	 *按id查询
	 * @param id
	 * @return
	 * @since  v2.6
	 * @author fufei
	 * @created 2015年2月27日 上午11:54:31
	 */
	public static TaskItemSearchPlan3 findById(long id) {
		SqlSession ss = SessionFactory.getSqlSession();
		try {
			TaskItemSearchPlanMapper3 tispMapper = ss.getMapper(TaskItemSearchPlanMapper3.class);
			return tispMapper.selectById(id);
		} finally {
			ss.close();
		}
	}
	
	/**
	 * 修改
	 * @param id
	 * @return
	 * @since  v2.6
	 * @author fufei
	 * @created 2015年2月27日 上午11:54:31
	 */
	public static void update(TaskItemSearchPlan3 itemSearchPlan) {
		SqlSession ss = SessionFactory.getSqlSession();
		try {
			TaskItemSearchPlanMapper3 tispMapper = ss.getMapper(TaskItemSearchPlanMapper3.class);
			tispMapper.updateById(itemSearchPlan);
		} finally {
			ss.close();
		}
	}
	
}
