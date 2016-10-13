package models.mappers.marketing;

import java.util.List;

import models.AdminOperatorLog;
import models.Region;
import models.TenpayTradeLog;
import models.User;
import models.marketing.TaskRewardLog;

import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.SelectKey;
import org.apache.ibatis.annotations.Update;

import vos.TaskRewardLogVo;
import vos.UserSearchVo;


public interface TaskRewardLogMapper {
	@Insert("insert into " + TaskRewardLog.TABLE_NAME + "(invite_user_id,user_id,task_id,task_finished_time,memo,reward_ingot) "
			+ "values(#{inviteUserId},#{userId},#{taskId},#{taskFinishedTime},#{memo},#{rewardIngot})")
    void insert(TaskRewardLog log);

	int count(TaskRewardLogVo vo);
	List<TaskRewardLog> selectList(TaskRewardLogVo vo);


}
