package models.mappers;

import java.util.List;

import models.UserFlowRecord;
import models.UserIngotRecord;

import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import controllers.MoneyManage.MoneyRecordSearchVo;
import enums.Sign;

public interface UserFlowRecordMapper {

    /**
     * 查询上次的押金记录：用于计算结余.
     */
    @Select("select id,sign,amount,balance from " + UserFlowRecord.TABLE_NAME 
        + " where user_id=#{userId} order by id desc limit 1")
    UserFlowRecord selectLastRecord(long userId);
    
    @Select("select * from " + UserFlowRecord.TABLE_NAME 
        + " where user_id=#{userId} order by id desc limit 1")
    UserFlowRecord selectLastRecordForTest(long userId);
    
	@Insert("insert into " + UserFlowRecord.TABLE_NAME
			+ "(user_id,task_id,amount,sign,balance,memo,create_time)"
			+ " values(#{userId},#{taskId},#{amount},#{sign},#{balance},#{memo},#{createTime})")
	void insert(UserFlowRecord flowRecord);
	
	@Select("select id,amount from "+ UserFlowRecord.TABLE_NAME
	    + " where task_id = #{taskId} and sign=#{sign} order by create_time limit 1")
	UserFlowRecord selectByTaskIdAndSign(@Param("taskId") long taskId, @Param("sign") Sign sign);
	
	@Select("select sum(amount) from " + UserFlowRecord.TABLE_NAME + " where task_id = #{taskId} and sign=#{sign}")
	Long selectAmountByTaskIdAndSign(@Param("taskId") long taskId, @Param("sign") Sign sign);

	int count(MoneyRecordSearchVo vo);
    
    int countIsReward(MoneyRecordSearchVo vo);

    List<UserFlowRecord> selectList(MoneyRecordSearchVo vo);
    
	List<UserFlowRecord> selectIsRewardList(MoneyRecordSearchVo vo);
}
