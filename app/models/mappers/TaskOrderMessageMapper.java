package models.mappers;

import java.util.List;

import models.TaskOrderMessage;

import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

public interface TaskOrderMessageMapper {

	@Insert("insert into " + TaskOrderMessage.TABLE_NAME + "(task_id,message,used_num,create_time)"
			+ " values(#{taskId}, #{message}, #{usedNum}, #{createTime})")
	public void insert(TaskOrderMessage orderMessage);

	@Select("select count(1) from " + TaskOrderMessage.TABLE_NAME + " where task_id=#{taskId} and message=#{message}")
	Integer selectByTaskIdAndMessage(@Param("taskId") long taskId, @Param("message") String message);

	@Select("select * from " + TaskOrderMessage.TABLE_NAME + " where task_id=#{taskId}")
	List<TaskOrderMessage> selectByTaskId(@Param("taskId") long taskId);
	
	@Update("update " + TaskOrderMessage.TABLE_NAME + " set used_num = used_num + 1 where id = #{id}")
	Integer updateUsedNumById(@Param("id") long id);
	
	@Delete("delete from " + TaskOrderMessage.TABLE_NAME + " where task_id = #{taskId}")
	void deleteByTaskId(@Param("taskId") long taskId);
}
