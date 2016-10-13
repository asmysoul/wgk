package models.mappers;

import java.util.List;

import models.TaskItemSearchPlan;
import models.TaskItemSearchPlan3;

import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

public interface TaskItemSearchPlanMapper3 {

    public void insert(TaskItemSearchPlan3 plan);

    public void updateById(TaskItemSearchPlan3 plan);

    @Delete("delete from " + TaskItemSearchPlan3.TABLE_NAME + " where task_id=#{taskId}")
    public void deleteByTaskId(Long taskId);
   
    @Select("select * from " + TaskItemSearchPlan3.TABLE_NAME + " where task_id = #{taskId}  AND taken_num<total_num")
    List<TaskItemSearchPlan3> selectByTaskId(@Param("taskId") long taskId);
    
    @Select("select * from " + TaskItemSearchPlan3.TABLE_NAME + " where id = #{id}")
    TaskItemSearchPlan3 selectById(@Param("id") long id);
    
    @Select("select * from " + TaskItemSearchPlan3.TABLE_NAME + " where task_id = #{taskId}")
    List<TaskItemSearchPlan3> getOneTaskPlan(@Param("taskId") long taskId);

}
