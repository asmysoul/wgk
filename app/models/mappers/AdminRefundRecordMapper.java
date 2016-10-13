package models.mappers;

import models.AdminRefundRecord;

import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Select;

public interface AdminRefundRecordMapper {

    @Select("select * from " + AdminRefundRecord.TABLE_NAME + " where buyer_task_id=#{buyerTaskId} limit 1")
    AdminRefundRecord selectByBuyerTaskId(Long buyerTaskId);

    @Insert("insert into " + AdminRefundRecord.TABLE_NAME
        + "(buyer_task_id,task_id,buyer_id,seller_id,trans_no,create_time) "
        + "values(#{buyerTaskId},#{taskId},#{buyerId},#{sellerId},#{transNo},#{createTime})")
    void insert(AdminRefundRecord record);
}
