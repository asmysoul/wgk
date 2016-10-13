package models.mappers;

import java.util.List;

import models.Shop;

import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

import vos.ShopSearchVo;
import enums.Platform;

public interface ShopMapper {

    @Select("select 1 from " + Shop.TABLE_NAME + " where (nick=#{nick} and platform=#{platform}) or url=#{url}")
    Integer selectExists(Shop shop);
    
    @Select("select count(1) from " + Shop.TABLE_NAME + " where platform=#{platform} and seller_id=#{sellerId}")
    int countByPlatformAndSellerId(Shop shop);
    
    @Insert("insert into " + Shop.TABLE_NAME + "(seller_id,platform,url,name,nick,address,create_time,seller_name,mobile,street,branch) values(#{sellerId},#{platform},#{url},#{name},#{nick},#{address},#{createTime},#{sellerName},#{mobile},#{street},#{branch})")
    void insert(Shop shop);

    @Select("select * from " + Shop.TABLE_NAME + " where platform=#{platform} and seller_id=#{sellerId}")
    List<Shop> selectByPlatform(@Param("platform") Platform platform, @Param("sellerId") long sellerId);
    
    @Select("select * from " + Shop.TABLE_NAME + " where id = #{shopId}")
    Shop selectShopById(@Param("shopId") long shopId);
    List<ShopSearchVo> selectAll(ShopSearchVo vo);
    
    Integer count(ShopSearchVo vo);
    @Update("update "+Shop.TABLE_NAME+" set nick=#{nick},url=#{url},address=#{address},seller_name=#{sellerName},mobile=#{mobile},street=#{street},branch=#{branch} where id=#{id}")
    Integer updateShop(ShopSearchVo vo);
    @Select("select * from " + Shop.TABLE_NAME + " where platform=#{platform} and nick=#{nick}")
    List<Shop> selectByPlatformAndNick(@Param("platform") Platform platform, @Param("nick") String nick);

    @Update("update "+Shop.TABLE_NAME+" set address=#{address},mobile=#{mobile},seller_name=#{sellerName},street=#{street},branch=#{branch} where id=#{id}")
	void update(Shop shop);
}
