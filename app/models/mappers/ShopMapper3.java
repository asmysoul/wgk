package models.mappers;

import java.util.List;

import models.Shop3;

import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

import vos.ShopSearchVo3;

import enums.Platform3;

public interface ShopMapper3 {

    @Select("select 1 from " + Shop3.TABLE_NAME + " where (nick=#{nick} and platform=#{platform}) or url=#{url}")
    Integer selectExists(Shop3 Shop3);
    
    @Select("select count(1) from " + Shop3.TABLE_NAME + " where platform=#{platform} and seller_id=#{sellerId}")
    int countByPlatformAndSellerId(Shop3 Shop3);
    
    @Insert("insert into " + Shop3.TABLE_NAME + "(seller_id,platform,url,name,nick,address,create_time,seller_name,mobile,street,branch) values(#{sellerId},#{platform},#{url},#{name},#{nick},#{address},#{createTime},#{sellerName},#{mobile},#{street},#{branch})")
    void insert(Shop3 Shop3);

    @Select("select * from " + Shop3.TABLE_NAME + " where platform=#{platform} and seller_id=#{sellerId}")
    List<Shop3> selectByPlatform(@Param("platform") Platform3 platform, @Param("sellerId") long sellerId);
    
    @Select("select * from " + Shop3.TABLE_NAME + " where id = #{Shop3Id}")
    Shop3 selectShopById(@Param("Shop3Id") long ShopId);
    List<ShopSearchVo3> selectAll(ShopSearchVo3 vo);
    
    Integer count(ShopSearchVo3 vo);
    @Update("update "+Shop3.TABLE_NAME+" set nick=#{nick},url=#{url},address=#{address},seller_name=#{sellerName},mobile=#{mobile},street=#{street},branch=#{branch} where id=#{id}")
    Integer updateShop(ShopSearchVo3 vo);
    @Select("select * from " + Shop3.TABLE_NAME + " where platform=#{platform} and nick=#{nick}")
    List<Shop3> selectByPlatformAndNick(@Param("platform") Platform3 platform, @Param("nick") String nick);

    @Update("update "+Shop3.TABLE_NAME+" set address=#{address},mobile=#{mobile},seller_name=#{sellerName},street=#{street},branch=#{branch} where id=#{id}")
	void update(Shop3 Shop3);
}
