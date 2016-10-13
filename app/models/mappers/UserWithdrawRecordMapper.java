package models.mappers;

import java.util.List;

import models.UserWithdrawRecord;

import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

import vos.UserWithdrawRecordVo;
import controllers.MoneyManage.MoneyRecordSearchVo;
import controllers.MoneyManage.WithdrawSearchVo;

public interface UserWithdrawRecordMapper {

    //@formatter:off
    @Insert("insert into " + UserWithdrawRecord.TABLE_NAME
        + " (sn,is_buyer_deposit,user_id,fund_account_id,apply_amount,amount,status,memo,apply_time,modify_time)"
        + " values(#{sn},#{isBuyerDeposit},#{userId},#{fundAccountId},#{applyAmount},#{amount},#{status},#{memo},#{applyTime},#{modifyTime})")
    //@formatter:on
    void insert(UserWithdrawRecord record);

    int count(MoneyRecordSearchVo vo);
    
    List<UserWithdrawRecord> selectList(MoneyRecordSearchVo vo);

    @Update("update " + UserWithdrawRecord.TABLE_NAME
        + " set trade_no=#{tradeNo},status=#{status},memo=#{memo},modify_time=#{modifyTime} where id=#{id}")
    void updateById(UserWithdrawRecord userWithdrawRecord);
    
    @Select("select * from " + UserWithdrawRecord.TABLE_NAME + " where id =#{id}")
    UserWithdrawRecord findById(@Param("id") long id);
    
    
    int countForAdmin(WithdrawSearchVo vo);
	List<UserWithdrawRecord> selectForAdmin(WithdrawSearchVo vo);

    int buyerIngotWithdrawCount(WithdrawSearchVo vo);
}
