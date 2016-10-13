package models.mappers;

import java.util.List;

import models.SellerPledgeRecord;
import models.SellerPledgeRecord.PledgeAction;

import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

import controllers.MoneyManage.MoneyRecordSearchVo;

public interface SellerPledgeRecordMapper {

    //@formatter:off
    @Insert("insert into " + SellerPledgeRecord.TABLE_NAME
        + "(seller_id,task_id,amount,sign,action,balance,memo,create_time)"
        + " values(#{sellerId},#{taskId},#{amount},#{sign},#{action},#{balance},#{memo},#{createTime})")
    //@formatter:on
    void insert(SellerPledgeRecord sellerPledgeRecord);
    
    /**
     * 查询某个任务最后的押金冻结记录
     * 【注意】此处仅查出ID用于后续直接更新，不能查询其他字段，否则会造成误更新数据
     */
    @Select("select id from " + SellerPledgeRecord.TABLE_NAME 
        + " where task_id=#{taskId} and action=#{action} and seller_id=#{sellerId}" 
        + " order by id desc limit 1")
    SellerPledgeRecord selectLastLock(@Param("taskId") long taskId, @Param("sellerId") long sellerId,
        @Param("action") PledgeAction action);
    
    @Select("select id,amount from " + SellerPledgeRecord.TABLE_NAME
        + " where task_id=#{taskId} and action=#{action} order by create_time limit 1")
    SellerPledgeRecord selectByTaskIdAndAction(@Param("taskId") long taskId, @Param("action") PledgeAction action);
    
    /**
     * 查询上次的押金记录：用于计算结余.
     */
    @Select("select id,amount,balance from " + SellerPledgeRecord.TABLE_NAME
            + " where seller_id=#{sellerId} order by id desc limit 1")
    SellerPledgeRecord selectLastRecord(long sellerId);
    
    @Select("select * from " + SellerPledgeRecord.TABLE_NAME
        + " where seller_id=#{sellerId} order by id desc limit 1")
    SellerPledgeRecord selectLastRecordForTest(long sellerId);
    
    /**
     * 按照卖家、任务及action查询记录（测试用）.
     */
    @Select("select id,amount,balance,sign from " + SellerPledgeRecord.TABLE_NAME
        + " where seller_id=#{sellerId} and task_id=#{taskId} and action=#{action}"
        + " order by id desc limit 1")
    SellerPledgeRecord selectBySellerAndTaskAndAction(@Param("sellerId") long sellerId, @Param("taskId") long taskId,
        @Param("action") PledgeAction action);
    
    int count(MoneyRecordSearchVo vo);
    
    List<SellerPledgeRecord> selectList(MoneyRecordSearchVo vo);
    
    @Select("select sum(amount) from "+ SellerPledgeRecord.TABLE_NAME + " where task_id=#{taskId} and action=#{action}")
    Long selectAmoutByTaskIdAndAction(@Param("taskId") long taskId, @Param("action") PledgeAction action);

    /**
     * 合计卖家当前被冻结的押金.
     * is_deduct仅在action=SYS_LOCK时有效
     */
    @Select("select ifnull(sum(amount),0) from " + SellerPledgeRecord.TABLE_NAME
        + " where seller_id=#{sellerId} and action=#{action}"
        + " and (is_deduct is null or is_deduct=0)")
    long sumAmmountByAction(@Param("sellerId") long sellerId, @Param("action") PledgeAction action);
    
	@Update("update " + SellerPledgeRecord.TABLE_NAME + " set is_withdraw = true, modify_time=#{modifyTime} where id=#{id}")
	void updateIsWithdraw(SellerPledgeRecord record);
	
	/**
	 * 将“平台返款”任务的押金冻结记录更新为“已扣款”
	 */
	@Update("update " + SellerPledgeRecord.TABLE_NAME + " set is_deduct = true, modify_time=#{modifyTime} where id=#{id}")
	void updateIsDeduct(SellerPledgeRecord record);
}
