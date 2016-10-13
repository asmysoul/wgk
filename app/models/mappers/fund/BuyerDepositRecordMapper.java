package models.mappers.fund;

import java.util.List;

import models.BuyerDepositRecord;

import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import controllers.MoneyManage.MoneyRecordSearchVo;
import enums.Sign;

public interface BuyerDepositRecordMapper {

    /**
     * 查询上次的押金记录：用于计算结余.
     */
    @Select("select id,sign,amount,balance from " + BuyerDepositRecord.TABLE_NAME 
        + " where user_id=#{userId} order by id desc limit 1")
    BuyerDepositRecord selectLastRecord(long userId);
    
    @Select("select * from " + BuyerDepositRecord.TABLE_NAME 
        + " where user_id=#{userId} order by id desc limit 1")
    BuyerDepositRecord selectLastRecordForTest(long userId);
    
	@Insert("insert into " + BuyerDepositRecord.TABLE_NAME
			+ "(user_id,task_id,amount,sign,balance,memo,create_time)"
			+ " values(#{userId},#{taskId},#{amount},#{sign},#{balance},#{memo},#{createTime})")
	void insert(BuyerDepositRecord userIngotRecord);
	
	@Select("select id,amount from "+ BuyerDepositRecord.TABLE_NAME
	    + " where task_id = #{taskId} and sign=#{sign} order by create_time limit 1")
	BuyerDepositRecord selectByTaskIdAndSign(@Param("taskId") long taskId, @Param("sign") Sign sign);
	
	@Select("select sum(amount) from " + BuyerDepositRecord.TABLE_NAME + " where task_id = #{taskId} and sign=#{sign}")
	Long selectAmountByTaskIdAndSign(@Param("taskId") long taskId, @Param("sign") Sign sign);

	int count(MoneyRecordSearchVo vo);
    List<BuyerDepositRecord> selectList(MoneyRecordSearchVo vo);
}
