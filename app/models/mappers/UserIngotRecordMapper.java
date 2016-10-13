package models.mappers;

import java.util.List;

import models.UserIngotRecord;

import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import controllers.MoneyManage.MoneyRecordSearchVo;
import enums.Sign;

public interface UserIngotRecordMapper {

    /**
     * 查询上次的押金记录：用于计算结余.
     */
    @Select("select id,sign,amount,balance from " + UserIngotRecord.TABLE_NAME 
        + " where user_id=#{userId} order by id desc limit 1")
    UserIngotRecord selectLastRecord(long userId);
    
    @Select("select * from " + UserIngotRecord.TABLE_NAME 
        + " where user_id=#{userId} order by id desc limit 1")
    UserIngotRecord selectLastRecordForTest(long userId);
    
    //,   2014-11-15 14:11:05
    //concat(cancat('%',#{username}),'%')
    //like '%'+#{name,jdbcType=VARCHAR}+'%'
    //SELECT * FROM waguke.user_ingot_record where user_id=1678 and sign = 'PLUS'  and create_time like '2016-06-23%';
    @Select("select sum(amount) from " + UserIngotRecord.TABLE_NAME 
            + " where user_id=#{userId} and sign = 'PLUS' and create_time like #{day} '%'")
    Long selectListanount(@Param("userId") long userId,@Param("day")String day );
    
    
	@Insert("insert into " + UserIngotRecord.TABLE_NAME
			+ "(user_id,task_id,amount,is_reward,sign,balance,memo,create_time)"
			+ " values(#{userId},#{taskId},#{amount},#{isReward},#{sign},#{balance},#{memo},#{createTime})")
	void insert(UserIngotRecord userIngotRecord);
	
	@Select("select id,amount from "+ UserIngotRecord.TABLE_NAME
	    + " where task_id = #{taskId} and sign=#{sign} order by create_time limit 1")
	UserIngotRecord selectByTaskIdAndSign(@Param("taskId") long taskId, @Param("sign") Sign sign);
	
	@Select("select sum(amount) from " + UserIngotRecord.TABLE_NAME + " where task_id = #{taskId} and sign=#{sign}")
	Long selectAmountByTaskIdAndSign(@Param("taskId") long taskId, @Param("sign") Sign sign);

	int count(MoneyRecordSearchVo vo);
    
    int countIsReward(MoneyRecordSearchVo vo);

    List<UserIngotRecord> selectList(MoneyRecordSearchVo vo);
    
    
   
    
    
	List<UserIngotRecord> selectIsRewardList(MoneyRecordSearchVo vo);

	@Select("select amount from " + UserIngotRecord.TABLE_NAME + " where user_id=#{buyerId} and task_id=#{taskId} and sign = 'MINUS' order by id desc limit 1")
	int selectLastRecordAboutThisRecord(@Param("buyerId")Long buyerId, @Param("taskId")Long taskId);

}
