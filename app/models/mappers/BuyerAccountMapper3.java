package models.mappers;

import java.util.List;

import models.BuyerAccount3;

import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

import vos.BuyerAccountSearchVo3;
import enums.Platform3;

public interface BuyerAccountMapper3 {
	
	int count(BuyerAccountSearchVo3 vo);
	
	List<BuyerAccount3> selectBySearch(BuyerAccountSearchVo3 vo);

	@Insert("insert into buyer_account3" +
			"(nick,gender,platform,consignee,state,city,region,address,mobile,user_id,status,create_time,modify_time) " +
			"values(#{nick},#{gender},#{platform},#{consignee},#{state},#{city},#{region},#{address},#{mobile},#{userId},#{status},#{createTime},#{modifyTime})")
	void insert(BuyerAccount3 account);
	
	void updateById(BuyerAccount3 account);
	
	BuyerAccount3 selectById(Long id);

    BuyerAccount3 selectForCheckDuplicate(BuyerAccount3 svo);
    
    @Select("select id,nick,gender,state,city,region,address,consignee,mobile,status,memo,order_number from " + BuyerAccount3.TABLE_NAME 
            +" where user_id=#{userId} and platform=#{platform}")
        List<BuyerAccount3> selectByUserIdAndPlatform(@Param("userId") long userId, @Param("platform")  Platform3 platform);

    void updateBuyerAccount(BuyerAccount3 vo);

	@Select("select * from " + BuyerAccount3.TABLE_NAME + " where id = #{accountId}")
	BuyerAccount3 findCountByAccountId(@Param("accountId") long accountId);

	@Update("update " + BuyerAccount3.TABLE_NAME + " set order_number = #{orderNumber} where id=#{id}")
	void updateOrderNumber(@Param("id")long id, @Param("orderNumber")long orderNumber);
}
