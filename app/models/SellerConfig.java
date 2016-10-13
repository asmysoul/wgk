package models;

import models.mappers.SellerConfigMapper;

import org.apache.ibatis.session.SqlSession;
import org.codehaus.groovy.runtime.metaclass.NewInstanceMetaMethod;

import com.aton.db.SessionFactory;

public class SellerConfig {
	
	public static final String TABLE_NAME = "seller_config";
	
	public long id;
	/*  */
	public long sellerId;
	/* 同一个账号（挖顾客账号），接同一个商家（根据商家ID判断） */
	public int buyerAndSellerTime;
	/* 同一个账号（挖顾客账号），接同一个店铺（根据店铺ID判断） */
	public int buyerAndShopTime;
	/* 同一个买号（旺旺号）接同一个店铺（根据店铺ID判断） */
	public int buyerAcountAndShopTime;
	/* 同一个买号（旺旺号）接同一个商品（根据商品ID判断） */
	public int buyerAcountAndItemTime;
	/**
	 * 根据Id查找配置
	 *
	 * @param sellerId
	 * @return
	 * @since  v2.0
	 * @author Mark Xu
	 * @created 2015-4-9 下午4:37:46
	 */
	
	public static SellerConfig findBySellerId(long sellerId) {
		SqlSession ss = SessionFactory.getSqlSession();
		try {
			SellerConfigMapper mapper = ss.getMapper(SellerConfigMapper.class);
			SellerConfig config = mapper.findBySellerId(sellerId);
			return config;
		} finally{
			ss.close();
		}
	}
	
	/**
	 * 更新商家规则限制
	 *
	 * @since  v2.0
	 * @author Mark Xu
	 * @created 2015-4-9 下午6:08:52
	 */
	public void updateBySellerConfig(SellerConfig config) {
		SqlSession ss = SessionFactory.getSqlSession();
		try {
			SellerConfigMapper mapper = ss.getMapper(SellerConfigMapper.class);
			mapper.updateBySellerConfig(config);
			//更新用户卖家设置缓存
			User.findByIdWichCache(config.sellerId).buyerAndSellerTime(config.buyerAndSellerTime)
				.buyerAndShopTime(config.buyerAndShopTime).buyerAcountAndShopTime(config.buyerAcountAndShopTime)
				.buyerAcountAndItemTime(config.buyerAcountAndItemTime).updateCache();
		} finally {
			ss.close();
		}
		
	}

	/**
	 * 插入数据
	 *
	 * @param config
	 * @since  v2.0
	 * @author Mark Xu
	 * @created 2015-4-10 上午10:00:18
	 */
	public void insert(SellerConfig config) {
		SqlSession ss = SessionFactory.getSqlSession();
		try {
			SellerConfigMapper mapper = ss.getMapper(SellerConfigMapper.class);
			mapper.insert(config);
			//更新用户卖家设置缓存
			User.findByIdWichCache(config.sellerId).buyerAndSellerTime(config.buyerAndSellerTime)
			.buyerAndShopTime(config.buyerAndShopTime).buyerAcountAndShopTime(config.buyerAcountAndShopTime)
			.buyerAcountAndItemTime(config.buyerAcountAndItemTime).updateCache();
		}finally {
			ss.close();
		}
		
	}

	/**
	 * 是否存在该数据
	 *
	 * @param sellerId
	 * @return
	 * @since  v2.0
	 * @author Mark Xu
	 * @created 2015-4-10 上午10:33:16
	 */
	public boolean isNull(long sellerId) {
		SqlSession ss = SessionFactory.getSqlSession();
		try {
			SellerConfigMapper mapper = ss.getMapper(SellerConfigMapper.class);
			if(mapper.count(sellerId)>0) {
				return false;
			}else {
				return true;
			}
		}finally {
			ss.close();
		}
	}

	
	public static SellerConfig newInstance(int buyerAndSellerTime ,int buyerAndShopTime, int buyerAcountAndShopTime, int buyerAcountAndItemTime) {
		SellerConfig config = new SellerConfig();
		config.buyerAndSellerTime = buyerAndSellerTime;
		config.buyerAndShopTime = buyerAndShopTime;
		config.buyerAcountAndShopTime = buyerAcountAndShopTime;
		config.buyerAcountAndItemTime = buyerAcountAndItemTime;
		return config;
	}

	
}
