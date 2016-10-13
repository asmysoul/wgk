package models.mappers;

import java.util.List;

import models.PayTradeLog;
import models.User;
import models.PayTradeLog.FinanceLogVo;

import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.SelectKey;
import org.apache.ibatis.annotations.Update;

import enums.pay.TradeResult;
import enums.pay.TradeType;


public interface PayTradeLogMapper {


    @Select("select * from " + PayTradeLog.TABLE_NAME + " where id=#{id} limit 1")
    PayTradeLog selectById(long id);

    @Select("select * from " + PayTradeLog.TABLE_NAME + " where ${field}=#{value} limit 1")
    PayTradeLog selectByField(@Param("field") String field, @Param("value") Object value);
    
    @Insert("insert into " + PayTradeLog.TABLE_NAME
        + "(trade_no,type,biz_id,biz_member_month,amount,withdraw_amount,user_id,memo,result,create_time,modify_time)"
        + " values(#{tradeNo},#{type},#{bizId},#{bizMemberMonth},#{amount},#{withdrawAmount},#{userId},#{memo},#{result},#{createTime},#{modifyTime})")
    @SelectKey(before = false, resultType = Long.class, keyProperty = "id", statement = { "select LAST_INSERT_ID() as id" })
    long insert(PayTradeLog log);

    @Update("update " + PayTradeLog.TABLE_NAME
        + " set result=#{result},deal_id=#{dealId},bank=#{bank},bank_deal_id=#{bankDealId},fee=#{fee},modify_time=#{modifyTime} where id=#{id}")
    void updateById(PayTradeLog tenpayTradeLog);

    @Select("select id,amount from " + PayTradeLog.TABLE_NAME
        + " where type = #{type} and biz_id = #{bizId} and result = #{result}"
        + " order by id desc limit 1")
    PayTradeLog selectByTypeAndBizIdAndResult(@Param("type") TradeType type, @Param("bizId") long bizId,
        @Param("result") TradeResult result);

    @Select("select sum(amount) from " + PayTradeLog.TABLE_NAME
        + " where type = #{type} and biz_id = #{bizId} and result = #{result}")
    Long selectAmountByTypeAndBizIdAndResult(@Param("type") TradeType type, @Param("bizId") long bizId,
        @Param("result") TradeResult result);

    @Select("select id,trade_no,deal_id,amount,withdraw_amount,create_time from " + PayTradeLog.TABLE_NAME
        + " where user_id=#{userId}"
        + " and result='OK'"
        + " and (withdraw_amount is null or amount > withdraw_amount)"
        + " and (type='PLEDGE' or type='TASK')"
        + " order by create_time asc"
        + " limit ${size}")
    List<PayTradeLog> selectListForRefund(@Param("userId") long userId, @Param("size") int size);

    @Select("SELECT count(*) FROM pay_trade_log WHERE result='OK' AND DATE_FORMAT(create_time, '%Y-%m-%d') = #{date}")
	int platCount(@Param("date")String date);
	
    @Select("SELECT l.*, u.nick userNick, u.type userType FROM " + PayTradeLog.TABLE_NAME + " l left join " + User.TABLE_NAME + " u on u.id = l.user_id WHERE result='OK' AND DATE_FORMAT(l.create_time, '%Y-%m-%d') = #{date}")
    List<PayTradeLog> platCountList(@Param("date")String date);
    
}
