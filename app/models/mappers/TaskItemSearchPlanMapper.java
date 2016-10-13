package models.mappers;

import java.util.List;

import models.TaskItemSearchPlan;

import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

public interface TaskItemSearchPlanMapper {

    public void insert(TaskItemSearchPlan plan);

    public void updateById(TaskItemSearchPlan plan);

    @Delete("delete from " + TaskItemSearchPlan.TABLE_NAME + " where task_id=#{taskId}")
    public void deleteByTaskId(Long taskId);
   
    @Select("select * from " + TaskItemSearchPlan.TABLE_NAME + " where task_id = #{taskId}  AND taken_num<total_num")
    List<TaskItemSearchPlan> selectByTaskId(@Param("taskId") long taskId);
    
    @Select("select * from " + TaskItemSearchPlan.TABLE_NAME + " where id = #{id}")
    TaskItemSearchPlan selectById(@Param("id") long id);
    
    @Select("select * from " + TaskItemSearchPlan.TABLE_NAME + " where task_id = #{taskId}")
    List<TaskItemSearchPlan> getOneTaskPlan(@Param("taskId") long taskId);

}
