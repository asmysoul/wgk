package models.mappers;

import java.util.Date;
import java.util.List;

import models.SellerConfig;

import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

public interface SellerConfigMapper {
	
	@Select("select * from " + SellerConfig.TABLE_NAME + " where seller_id = #{sellerId}")
	SellerConfig findBySellerId(@Param("sellerId") long sellerId);

	void updateBySellerConfig(SellerConfig config);
	
	void insert(SellerConfig config);
	
	@Select("select count(*) from " + SellerConfig.TABLE_NAME + " where seller_id = #{sellerId}")
	int count(@Param("sellerId") long sellerId);
	
}
