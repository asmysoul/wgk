package models.mappers;

import java.util.Date;
import java.util.List;

import models.BuyerConfig;
import models.SellerConfig;

import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

public interface BuyerConfigMapper {
	
	@Select("select * from " + BuyerConfig.TABLE_NAME + " where buyer_id = #{buyerId}")
	BuyerConfig findByBuyerId(@Param("buyerId") long buyerId);

	void updateByBuyerConfig(BuyerConfig config);
	
	void insert(BuyerConfig config);
	
	@Select("select count(*) from " + BuyerConfig.TABLE_NAME + " where buyer_id = #{buyerId}")
	int count(@Param("buyerId") long buyerId);

	void updateByConfig(BuyerConfig config);
	
}
