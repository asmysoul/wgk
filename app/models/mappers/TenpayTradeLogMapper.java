package models.mappers;

import java.util.List;

import models.TenpayTradeLog;
import models.TenpayTradeLog.TradeResult;
import models.TenpayTradeLog.TradeType;

import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.SelectKey;
import org.apache.ibatis.annotations.Update;


public interface TenpayTradeLogMapper {

    @Select("select * from " + TenpayTradeLog.TABLE_NAME + " where id=#{id} limit 1")
    TenpayTradeLog selectById(long id);

    @Select("select * from " + TenpayTradeLog.TABLE_NAME + " where ${field}=#{value} limit 1")
    TenpayTradeLog selectByField(@Param("field") String field, @Param("value") Object value);

    @Insert("insert into " + TenpayTradeLog.TABLE_NAME
        + "(out_trade_no,type,biz_id,amount,user_id,memo,result,create_time,modify_time)"
        + " values(#{outTradeNo},#{type},#{bizId},#{amount},#{userId},#{memo},#{result},#{createTime},#{modifyTime})")
    @SelectKey(before = false, resultType = Long.class, keyProperty = "id", statement = { "select LAST_INSERT_ID() as id" })
    long insert(TenpayTradeLog log);

    @Update("update " + TenpayTradeLog.TABLE_NAME + " set result=#{result},modify_time=#{modifyTime} where id=#{id}")
    void updateById(TenpayTradeLog tenpayTradeLog);
    
    @Select("select id from " + TenpayTradeLog.TABLE_NAME + " where type = #{type} and biz_id = #{bizId} and result = #{result} order by create_time limit 1")
    TenpayTradeLog selectByTypeAndBizIdAndResult(@Param("type") TradeType type,@Param("bizId") long bizId, @Param("result") TradeResult result);
    
    @Select("select sum(amount) from " + TenpayTradeLog.TABLE_NAME + " where type = #{type} and biz_id = #{bizId} and result = #{result}")
    Long selectAmountByTypeAndBizIdAndResult(@Param("type") TradeType type,@Param("bizId") long bizId, @Param("result") TradeResult result);
    
    @Select("select id,out_trade_no from " + TenpayTradeLog.TABLE_NAME + " limit 1000")
    List<TenpayTradeLog> selectList();
}
