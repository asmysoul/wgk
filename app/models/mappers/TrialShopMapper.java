package models.mappers;

import java.util.List;

import models.Shop;
import models.TrialShop;

import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

import vos.ShopSearchVo;
import vos.TrialShopSearchVo;
import enums.Platform;

public interface TrialShopMapper {

    @Select("select 1 from " + TrialShop.TABLE_NAME + " where (nick=#{nick} and platform=#{platform}) or url=#{url}")
    Integer selectExists(TrialShop shop);
    
    @Select("select count(1) from " + TrialShop.TABLE_NAME + " where platform=#{platform} and seller_id=#{sellerId}")
    int countByPlatformAndSellerId(TrialShop shop);
    
    @Insert("insert into " + TrialShop.TABLE_NAME + "(seller_id,platform,url,name,nick,address,create_time,seller_name,mobile,street,branch) values(#{sellerId},#{platform},#{url},#{name},#{nick},#{address},#{createTime},#{sellerName},#{mobile},#{street},#{branch})")
    void insert(TrialShop shop);

    @Select("select * from " + TrialShop.TABLE_NAME + " where platform=#{platform} and seller_id=#{sellerId}")
    List<TrialShop> selectByPlatform(@Param("platform") Platform platform, @Param("sellerId") long sellerId);
    
    @Select("select * from " + TrialShop.TABLE_NAME + " where id = #{shopId}")
    TrialShop selectShopById(@Param("shopId") long shopId);
    List<TrialShopSearchVo> selectAll(TrialShopSearchVo vo);
    
    Integer count(TrialShopSearchVo vo);
    @Update("update "+TrialShop.TABLE_NAME+" set nick=#{nick},url=#{url},address=#{address},seller_name=#{sellerName},mobile=#{mobile},street=#{street},branch=#{branch} where id=#{id}")
    Integer updateShop(TrialShopSearchVo vo);
    @Select("select * from " + TrialShop.TABLE_NAME + " where platform=#{platform} and nick=#{nick}")
    List<TrialShop> selectByPlatformAndNick(@Param("platform") Platform platform, @Param("nick") String nick);

    @Update("update "+TrialShop.TABLE_NAME+" set address=#{address},mobile=#{mobile},seller_name=#{sellerName},street=#{street},branch=#{branch} where id=#{id}")
	void update(TrialShop shop);
}
