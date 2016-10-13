package models.mappers;

import java.util.List;

import models.Shop2;

import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

import vos.ShopSearchVo2;
import enums.Platform2;

public interface ShopMapper2 {

    @Select("select 1 from " + Shop2.TABLE_NAME + " where (nick=#{nick} and platform2=#{platform2}) or url=#{url}")
    Integer selectExists(Shop2 Shop2);
    
    @Select("select count(1) from " + Shop2.TABLE_NAME + " where platform2=#{platform2} and seller_id=#{sellerId}")
    int countByPlatformAndSellerId(Shop2 Shop2);
    
    @Insert("insert into " + Shop2.TABLE_NAME + "(seller_id,platform2,name,nick,address,create_time,seller_name,mobile,street) values(#{sellerId},#{platform2},#{name},#{nick},#{address},#{createTime},#{sellerName},#{mobile},#{street})")
    void insert(Shop2 Shop2);

    @Select("select * from " + Shop2.TABLE_NAME + " where platform2=#{platform2} and seller_id=#{sellerId}")
    List<Shop2> selectByPlatform(@Param("platform2") Platform2 platform2, @Param("sellerId") long sellerId);
    
    @Select("select * from " + Shop2.TABLE_NAME + " where id = #{Shop2Id}")
    Shop2 selectShop2ById(@Param("Shop2Id") long Shop2Id);
    List<ShopSearchVo2> selectAll(ShopSearchVo2 vo);
    
    Integer count(ShopSearchVo2 vo);
    @Update("update "+Shop2.TABLE_NAME+" set nick=#{nick},url=#{url},address=#{address},seller_name=#{sellerName},mobile=#{mobile},street=#{street},branch=#{branch} where id=#{id}")
    Integer updateShop2(ShopSearchVo2 vo);
    @Select("select * from " + Shop2.TABLE_NAME + " where platform2=#{platform2} and nick=#{nick}")
    List<Shop2> selectByPlatformAndNick(@Param("platform2") Platform2 platform2, @Param("nick") String nick);

    @Update("update "+Shop2.TABLE_NAME+" set address=#{address},mobile=#{mobile},seller_name=#{sellerName},street=#{street}where id=#{id}")
	void update(Shop2 Shop2);
}
