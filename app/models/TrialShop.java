package models;

import java.util.Date;
import java.util.List;

import models.mappers.TrialShopMapper;

import org.apache.commons.lang.Validate;
import org.apache.ibatis.session.SqlSession;

import vos.Page;
import vos.TrialShopSearchVo;


import com.aton.db.SessionFactory;
import com.aton.util.StringUtils;

import enums.Platform;
/**
 * 
 * @ClassName trialshop
 * @Description 试用店铺
 * @author 抽离
 * @Date 2016年10月12日 下午2:23:37
 * @version 1.0.0
 */
public class TrialShop {

    public static final String TABLE_NAME = "trialshop";
    
    public long id;
    public long sellerId;

    /** 所属电商平台 */
    public Platform platform;
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
    
    public TrialShop() {
    }

    public TrialShop(long id, Platform platform, String url, String nick, String address) {
        this.id = id;
        this.platform = platform;
        this.url = url;
        this.nick = nick;
        this.address = address;
    }
    
    public static List<TrialShop> findBySellerId(long id2) {
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
	public static List<TrialShop> findByPlatformAndSellerId(Platform platform, long sellerId) {
		SqlSession ss = SessionFactory.getSqlSession();
		try {
			TrialShopMapper mapper = ss.getMapper(TrialShopMapper.class);
			return mapper.selectByPlatform(platform, sellerId);
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
	public static Page<TrialShopSearchVo> findAllForAdmin(TrialShopSearchVo vo) {
	    /*
	     *  过滤下输入条件
	     */
	    // 注册用户昵称
	    vo.userNick = StringUtils.trimToNull(vo.userNick);
	    // 店铺旺旺ID
	    vo.nick = StringUtils.trimToNull(vo.nick);
	    
		SqlSession ss = SessionFactory.getSqlSession();
		try {
			TrialShopMapper mapper = ss.getMapper(TrialShopMapper.class);
			int count = mapper.count(vo);
			if (count <= 0) {
				return Page.EMPTY;
			}
			Page<TrialShopSearchVo> page = Page.newInstance(vo.pageNo, vo.pageSize, count);
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
	    Validate.notNull(this.platform);
	    
	    String domain = StringUtils.removeStartIgnoreCase(url, "http://");
        if (domain.indexOf("/") > 0) {
            domain = StringUtils.substringBetween(url.toLowerCase(), "http://", "/");
	    }
	    //淘宝
        if (Platform.TAOBAO == platform) {
            return domain.endsWith(".taobao.com");
        } 
        //天猫
        if (Platform.TMALL == platform) {
            return domain.endsWith(".tmall.com") || domain.endsWith(".tmall.hk");
        }
        //京东
        if (Platform.JD == platform) {
            return domain.endsWith(".jd.com");
        }
        //蘑菇街
        if (Platform.MOGUJIE == platform) {
            return domain.endsWith(".mogujie.com");
        }
        
        // TODO 其他平台暂时不做检查
        return true;
	}
	
	public static TrialShop newInstance(Platform p,String url){
	    TrialShop s = new TrialShop();
	    s.url = url;
	    s.platform = p;
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
	public static Integer updateShop(TrialShopSearchVo vo) {
		SqlSession ss = SessionFactory.getSqlSession();
		try {
			TrialShopMapper mapper = ss.getMapper(TrialShopMapper.class);
			return mapper.updateShop(vo);
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
	public static List<TrialShop> selectByPlatformAndNick(TrialShopSearchVo vo) {
		SqlSession ss = SessionFactory.getSqlSession();
		try {
			TrialShopMapper mapper = ss.getMapper(TrialShopMapper.class);
			return mapper.selectByPlatformAndNick(vo.platform,vo.nick);
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
	public static TrialShop selectById(long id) {
		SqlSession ss = SessionFactory.getSqlSession();
		try {
			TrialShopMapper mapper = ss.getMapper(TrialShopMapper.class);
			return mapper.selectShopById(id);
		}finally {
			ss.close();
		}
	}
}
