package models;

import models.mappers.BuyerConfigMapper;

import org.apache.ibatis.session.SqlSession;
import org.codehaus.groovy.runtime.metaclass.NewInstanceMetaMethod;

import com.aton.db.SessionFactory;

public class BuyerConfig {
	
	public static final String TABLE_NAME = "buyer_config";
	
	public long id;
	/*  */
	public long buyerId;
	/* 同一个账号（挖顾客账号），接同一个商家（根据商家ID判断） */
	public Boolean isClearView;
	
	/**
	 * 
	 * 根据买手Id查找买手个性化配置
	 *
	 * @param sellerId
	 * @return
	 * @since  v2.0
	 * @author Mark Xu
	 * @created 2015-4-28 下午4:13:11
	 */
	public static BuyerConfig findByBuyerId(long buyerId) {
		SqlSession ss = SessionFactory.getSqlSession();
		try {
			BuyerConfigMapper mapper = ss.getMapper(BuyerConfigMapper.class);
			BuyerConfig config = mapper.findByBuyerId(buyerId);
			return config;
		} finally{
			ss.close();
		}
	}
	/**
	 * 更新买手个性化配置
	 *
	 * @param config
	 * @since  v2.0
	 * @author Mark Xu
	 * @created 2015-4-28 下午5:30:29
	 */
	public static void updateByBuyerConfig(BuyerConfig config) {
		SqlSession ss = SessionFactory.getSqlSession();
		try {
			BuyerConfigMapper mapper = ss.getMapper(BuyerConfigMapper.class);
			mapper.updateByConfig(config);
		}finally {
			ss.close();
		}
	}
	/**
	 * 买手个性化配置信息是否为空
	 *
	 * @param buyerId2
	 * @return
	 * @since  v2.0
	 * @author Mark Xu
	 * @created 2015-4-28 下午5:42:04
	 */
	public boolean isNull(long buyerId) {
		SqlSession ss = SessionFactory.getSqlSession();
		try {
			BuyerConfigMapper mapper = ss.getMapper(BuyerConfigMapper.class);
			if(mapper.count(buyerId)>0) {
				return false;
			}else {
				return true;
			}
		}finally {
			ss.close();
		}
	}
	/**
	 * 插入买手个性配置信息
	 *
	 * @param config
	 * @since  v2.0
	 * @author Mark Xu
	 * @created 2015-4-28 下午5:43:06
	 */
	public void insert(BuyerConfig config) {
		SqlSession ss = SessionFactory.getSqlSession();
		try {
			BuyerConfigMapper mapper = ss.getMapper(BuyerConfigMapper.class);
			mapper.insert(config);
			//更新用户卖家设置缓存
			User.findByIdWichCache(config.buyerId).updateCache();
		}finally {
			ss.close();
		}
	}

	
}
