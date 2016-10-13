package models.mappers;

import java.util.List;

import models.BuyerAccount;

import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

import vos.BuyerAccountSearchVo;
import enums.Platform;

public interface BuyerAccountMapper {
	
	int count(BuyerAccountSearchVo vo);
	
	List<BuyerAccount> selectBySearch(BuyerAccountSearchVo vo);

	@Insert("insert into buyer_account" +
			"(nick,platform,consignee,state,city,region,address,mobile,user_id,status,create_time,modify_time) " +
			"values(#{nick},#{platform},#{consignee},#{state},#{city},#{region},#{address},#{mobile},#{userId},#{status},#{createTime},#{modifyTime})")
	void insert(BuyerAccount account);
	
	void updateById(BuyerAccount account);
	
	BuyerAccount selectById(Long id);

    BuyerAccount selectForCheckDuplicate(BuyerAccount svo);
    
    @Select("select id,nick,state,city,region,address,consignee,mobile,status,memo,order_number from " + BuyerAccount.TABLE_NAME 
        +" where user_id=#{userId} and platform=#{platform}")
    List<BuyerAccount> selectByUserIdAndPlatform(@Param("userId") long userId, @Param("platform")  Platform platform);

    void updateBuyerAccount(BuyerAccount vo);

	@Select("select * from " + BuyerAccount.TABLE_NAME + " where id = #{accountId}")
	BuyerAccount findCountByAccountId(@Param("accountId") long accountId);

	@Update("update " + BuyerAccount.TABLE_NAME + " set order_number = #{orderNumber} where id=#{id}")
	void updateOrderNumber(@Param("id")long id, @Param("orderNumber")long orderNumber);
}
