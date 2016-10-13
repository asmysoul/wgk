package models.mappers;

import java.util.List;

import models.BuyerAccount;
import models.BuyerAccount2;

import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

import vos.BuyerAccountSearchVo;
import vos.BuyerAccountSearchVo2;
import enums.Platform;
import enums.Platform2;

public interface BuyerAccountMapper2 {
	
	int count(BuyerAccountSearchVo2 vo);
	
	List<BuyerAccount2> selectBySearch(BuyerAccountSearchVo2 vo);

	@Insert("insert into buyer_account2" +
			"(nick,platform,consignee,state,city,region,address,mobile,user_id,status,create_time,modify_time) " +
			"values(#{nick},#{platform},#{consignee},#{state},#{city},#{region},#{address},#{mobile},#{userId},#{status},#{createTime},#{modifyTime})")
	void insert(BuyerAccount2 account);
	
	void updateById(BuyerAccount2 account);
	
	BuyerAccount2 selectById(Long id);

    BuyerAccount2 selectForCheckDuplicate(BuyerAccount2 svo);
    
    @Select("select id,nick,state,city,region,address,consignee,mobile,status,memo,order_number from " + BuyerAccount2.TABLE_NAME 
        +" where user_id=#{userId} and platform=#{platform}")
    List<BuyerAccount2> selectByUserIdAndPlatform(@Param("userId") long userId, @Param("platform")  Platform2 platform);

    void updateBuyerAccount(BuyerAccount2 vo);

	@Select("select * from " + BuyerAccount2.TABLE_NAME + " where id = #{accountId}")
	BuyerAccount2 findCountByAccountId(@Param("accountId") long accountId);

	@Update("update " + BuyerAccount2.TABLE_NAME + " set order_number = #{orderNumber} where id=#{id}")
	void updateOrderNumber(@Param("id")long id, @Param("orderNumber")long orderNumber);
}
