package models.mappers;

import models.BuyerExperienceRecord;
import models.BuyerExperienceRecord;

import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Select;

public interface BuyerExperienceRecordMapper {
    
    /**
     * 查询上次的押金记录：用于计算结余.<br>
     * ps: sign主要用于测试
     */
    @Select("select id,sign,amount,balance from " + BuyerExperienceRecord.TABLE_NAME 
        + " where user_id=#{buyerId} order by id desc limit 1")
    BuyerExperienceRecord selectLastRecord(long buyerId);

    @Insert("insert into " + BuyerExperienceRecord.TABLE_NAME
        + "(user_id, amount, balance, sign, memo, create_time) values(#{userId}, #{amount}, #{balance}, #{sign}, #{memo}, #{createTime})")
    void insert(BuyerExperienceRecord ber);
}
