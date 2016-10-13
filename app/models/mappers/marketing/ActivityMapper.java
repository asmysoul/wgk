package models.mappers.marketing;

import java.util.Date;
import java.util.List;

import models.Task;
import models.marketing.Activity;
import models.marketing.Activity.ActivityBizType;
import models.marketing.Activity.ActivityStatus;

import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

/**
 * 活动管理DAO
 * 
 * @author zhushan
 * @since  v2.0
 * @created 2014-12-20 上午11:18:54
 */
public interface ActivityMapper {
	/**
	 * 查询活动记录
	 * 
	 * @param field
	 *            条件名字
	 * @param value
	 *            条件值
	 * @return 活动
	 * @since  v2.0
	 * @author zhushan
	 * @created 2014-12-20 上午11:25:12
	 */
	@Select("SELECT id,title,page_url,banner_pic,start_time,end_time,status,biz_type,rule_content,modify_time FROM activity "
			+ Activity.TABLE_NAME + " where ${field}=#{value} limit 1")
	Activity selectByField(@Param("field") String field,
			@Param("value") Object value);
    
    @Select("SELECT rule_content FROM " + Activity.TABLE_NAME + " where biz_type=#{type} limit 1")
    String selectRuleByBizType(@Param("type") ActivityBizType type);

	@Update("update " + Activity.TABLE_NAME
            + " set status=#{status},modify_time=#{modifyTime}"
            + " where id=#{id}")
	void updateStatus(@Param("id") long id,
			@Param("modifyTime") Date modifyTime,
			@Param("status") ActivityStatus status);

	void update(Activity ac);

	List<Activity> selectAll();

	  void insert(Activity ac);
}
