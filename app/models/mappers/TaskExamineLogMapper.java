package models.mappers;

import models.TaskExamineLog;

import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Select;

public interface TaskExamineLogMapper {

	@Insert("insert into " + TaskExamineLog.TABLE_NAME
			+ "(admin_id,task_id,start_time,create_time) values(#{adminId}, #{taskId}, #{startTime}, #{createTime})")
	void insert(TaskExamineLog log);

	Integer updateById(TaskExamineLog log);

	@Select("select * from " + TaskExamineLog.TABLE_NAME + " where task_id=#{id} order by id desc limit 1")
	TaskExamineLog selectByTaskId(Long id);
}
