package models.mappers;

import java.util.List;

import models.FundAccount;

import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

import controllers.admins.UserManage.FundAccountSearchVo;

public interface FundAccountMapper {
	@Select("select (1) from " + FundAccount.TABLE_NAME + " where user_id = #{id} and type= #{type}")
	Integer selectByUserIdAndType(FundAccount fundAccount);
	
	@Select("select * from " + FundAccount.TABLE_NAME + " where user_id = #{userId}")
	List<FundAccount> selectByUserId(@Param("userId") long userId);
	
	void updateById(FundAccount fundAccount);

	@Insert("insert into "
			+ FundAccount.TABLE_NAME
			+ "(user_id,name,no,type,opening_bank,address,create_time) values(#{userId},#{name},#{no},#{type},#{openingBank},#{address},#{createTime})")
	void insert(FundAccount fundAccount);
	
	@Select("select * from " + FundAccount.TABLE_NAME +" where user_id = #{userId} and type= #{type} limit 1")
	FundAccount selectByType(@Param("userId") long userId, @Param("type") String type);
	
	@Select("select * from " + FundAccount.TABLE_NAME +" where user_id = #{userId} and type!='TENPAY' and type!='ALIPAY' limit 1")
	FundAccount selectDefaultOneForSeller(@Param("userId") long userId);
	
	@Select("select * from " + FundAccount.TABLE_NAME +" where user_id = #{userId} and type!='TENPAY' and type!='ALIPAY' limit 1")
    FundAccount selectBank(@Param("userId") long userId);
	
	@Select("select * from " + FundAccount.TABLE_NAME +" where user_id = #{userId} and type ='TENPAY'  limit 1")
	FundAccount selectTenpay(long userId);
	
	@Select("select * from " + FundAccount.TABLE_NAME +" where user_id = #{userId} and type ='ALIPAY' limit 1")
	FundAccount selectAlipay(long userId);
	
    int count(FundAccountSearchVo vo);
    List<FundAccount> selectList(FundAccountSearchVo vo);
    @Select("select * from " + FundAccount.TABLE_NAME)
    List<FundAccount> selectAll();
    @Update("update "+FundAccount.TABLE_NAME +" set address=#{address} where id=#{id}")
	Integer updateAddress(@Param("id")long id, @Param("address")String address);
}
