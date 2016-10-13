package models;

import java.util.Date;
import java.util.List;

import models.mappers.ShopMapper;
import models.mappers.ShopMapper2;

import org.apache.commons.lang.Validate;
import org.apache.ibatis.session.SqlSession;

import vos.Page;
import vos.ShopSearchVo;
import vos.ShopSearchVo2;

import com.aton.db.SessionFactory;
import com.aton.util.StringUtils;

import enums.Platform;
import enums.Platform2;

public class Shop2 {

    public static final String TABLE_NAME = "shop2";
    
    public long id;
    public long sellerId;

    /** 所属电商平台 */
    public Platform2 platform2;
    /** 店铺地址 */
    public String url;
    /**
     *  店铺名字（非淘宝平台需要该字段）
     */
    public String name;
    /** 卖家账号（旺旺ID等） */
    public String nick;
    /** 发件地址 */
    public String address;
    
    public Date createTime;
    
    public Date modifyTime;
    /** 店铺发货人姓名 */
    public String sellerName;
    /** 店铺发货人电话 */
    public String mobile;
    /** 店铺发货人详细地址 */
    public String street;
    /** 快递揽件网点编码 */
    public String branch;
    
    public Shop2() {
    }

    public Shop2(long id, Platform2 platform2, String url, String nick, String address) {
        this.id = id;
        this.platform2 = platform2;
        this.url = url;
        this.nick = nick;
        this.address = address;
    }
    
    public static List<Shop2> findBySellerId(long id2) {
        // TODO Auto-generated method stub
        return null;
    }

	/**
	 * 
	 * 根据平台取某个用户已绑定店铺信息
	 * 
	 * @param platform
	 * @param sellerId
	 * @return
	 * @since v0.1
	 * @author moloch
	 * @created 2014-7-28 下午3:31:03
	 */
	public static List<Shop2> findByPlatformAndSellerId(Platform2 platform2, long sellerId) {
		SqlSession ss = SessionFactory.getSqlSession();
		try {
			ShopMapper2 mapper = ss.getMapper(ShopMapper2.class);
			return mapper.selectByPlatform(platform2, sellerId);
		} finally {
			ss.close();
		}
	}
	
	/**
	 * 
	 * 取得所有的店铺信息
	 * 
	 * @param vo
	 * @return
	 * @since v0.1
	 * @author moloch
	 * @created 2014-10-6 下午2:08:52
	 */
	public static Page<ShopSearchVo2> findAllForAdmin(ShopSearchVo2 vo) {
	    /*
	     *  过滤下输入条件
	     */
	    // 注册用户昵称
	    vo.userNick = StringUtils.trimToNull(vo.userNick);
	    // 店铺旺旺ID
	    vo.nick = StringUtils.trimToNull(vo.nick);
	    
		SqlSession ss = SessionFactory.getSqlSession();
		try {
			ShopMapper2 mapper = ss.getMapper(ShopMapper2.class);
			int count = mapper.count(vo);
			if (count <= 0) {
				return Page.EMPTY;
			}
			Page<ShopSearchVo2> page = Page.newInstance(vo.pageNo, vo.pageSize, count);
			page.items = mapper.selectAll(vo);
			return page;
		} finally {
			ss.close();
		}
	}
    
	/**
	 * 
	 * 检查店铺网址与选择的平台是否对应.
	 *
	 * @return
	 * @since  v0.2.3
	 * @author youblade
	 * @created 2014年10月30日 下午3:05:58
	 */
	public boolean validateUrl(){
	    Validate.notNull(this.url);
	    Validate.notNull(this.platform2);
	    
	    String domain = StringUtils.removeStartIgnoreCase(url, "http://");
        if (domain.indexOf("/") > 0) {
            domain = StringUtils.substringBetween(url.toLowerCase(), "http://", "/");
	    }
        
        // TODO 其他平台暂时不做检查
        return true;
	}
	
	public static Shop2 newInstance(Platform2 p,String url){
	    Shop2 s = new Shop2();
	    s.url = url;
	    s.platform2 = p;
	    return s;
	}
	/**
	 * 
	 * 更新店铺
	 *
	 * @param vo
	 * @return
	 * @since  v2.4
	 * @author fufei
	 * @created 2015年2月5日 下午4:29:22
	 */
	public static Integer updateShop(ShopSearchVo2 vo) {
		SqlSession ss = SessionFactory.getSqlSession();
		try {
			ShopMapper2 mapper = ss.getMapper(ShopMapper2.class);
			return mapper.updateShop2(vo);
		} finally {
			ss.close();
		}
	}
	/**
	 * 
	 * 查询店铺
	 *
	 * @param vo
	 * @return
	 * @since  v2.4
	 * @author fufei
	 * @created 2015年2月5日 下午4:29:22
	 */
	public static List<Shop2> selectByPlatformAndNick(ShopSearchVo2 vo) {
		SqlSession ss = SessionFactory.getSqlSession();
		try {
			ShopMapper2 mapper = ss.getMapper(ShopMapper2.class);
			return mapper.selectByPlatformAndNick(vo.platform2,vo.nick);
		} finally {
			ss.close();
		}
	}
	
	/**
	 * 
	 * 根据Id查询店铺
	 *
	 * @param id
	 * @return
	 * @since  v2.0
	 * @author Mark Xu
	 * @created 2015-4-10 下午2:35:54
	 */
	public static Shop2 selectById(long id) {
		SqlSession ss = SessionFactory.getSqlSession();
		try {
			ShopMapper2 mapper = ss.getMapper(ShopMapper2.class);
			return mapper.selectShop2ById(id);
		}finally {
			ss.close();
		}
	}
}
