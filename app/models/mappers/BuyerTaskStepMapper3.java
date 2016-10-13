package models.mappers;

import java.util.List;

import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

import enums.BuyerTaskStepType;
import models.BuyerTaskStep3;

public interface BuyerTaskStepMapper3 {

    @Insert("insert into "
        + BuyerTaskStep3.TABLE_NAME
        + "(buyer_task_id,buyer_id,type,no,content,create_time,modify_time)"
        + " values (#{buyerTaskId},#{buyerId},#{type},#{no},#{content},#{createTime},#{modifyTime})")
    void insert(BuyerTaskStep3 buyerTaskStep);

    @Select("select * from " + BuyerTaskStep3.TABLE_NAME + " where buyer_task_id=#{buyerTaskId}")
    List<BuyerTaskStep3> slelectByBuyerTaskId(long buyerTaskId);

    @Select("select id,type,create_time from " + BuyerTaskStep3.TABLE_NAME
        + " where buyer_task_id=#{buyerTaskId} and buyer_id=#{buyerId}"
        + " order by id desc limit 1")
    BuyerTaskStep3 selectByTaskIdAndStepBuyerId(@Param("buyerTaskId") long buyerTaskId, @Param("buyerId") long buyerId);
    
    @Select("select id,type,is_valid from " + BuyerTaskStep3.TABLE_NAME
        + " where buyer_task_id=#{buyerTaskId} and type=#{type}"
        + " and buyer_id=#{buyerId}"
        + " limit 1")
    BuyerTaskStep3 selectByTaskIdAndTypeAndBuyerId(@Param("buyerTaskId") long buyerTaskId,
        @Param("type") BuyerTaskStepType type, @Param("buyerId") long buyerId);

    @Update("update " + BuyerTaskStep3.TABLE_NAME + " set is_valid=#{isValid},modify_time=#{modifyTime} where id=#{id}")
    void updateById(BuyerTaskStep3 step);

}
