package models.mappers;

import java.util.List;

import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

import controllers.MoneyManage.MoneyRecordSearchVo;
import models.MemberChargeRecord;

public interface MemberChargeRecordMapper {

    @Insert("insert into " + MemberChargeRecord.TABLE_NAME
        + " (user_id,user_type,amount,ingot,month,start_time,end_time,memo,create_time)"
        + " values(#{userId},#{userType},#{amount},#{ingot},#{month},#{startTime},#{endTime},#{memo},#{createTime})")
    void insert(MemberChargeRecord record);

    @Select("select * from " + MemberChargeRecord.TABLE_NAME
        + " where user_id=#{userId} order by end_time desc limit 1")
    MemberChargeRecord selectLastByUserId(long userId);

    int count(MoneyRecordSearchVo vo);

    List<MemberChargeRecord> selectList(MoneyRecordSearchVo vo);
    @Update("update " + MemberChargeRecord.TABLE_NAME +" set end_time=#{endTime} where id=#{id}")
    int delayMember(MemberChargeRecord chargeRecord);
    @Select("select * from " + MemberChargeRecord.TABLE_NAME +" group by user_id")
    List<MemberChargeRecord> findAll();
}
