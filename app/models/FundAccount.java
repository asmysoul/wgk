package models;

import java.util.Date;
import java.util.List;

import javax.persistence.Transient;

import models.mappers.FundAccountMapper;

import org.apache.commons.lang.Validate;
import org.apache.ibatis.session.SqlSession;
import org.joda.time.DateTime;

import vos.Page;

import com.aton.db.SessionFactory;
import com.aton.util.StringUtils;
import com.google.common.base.Strings;

import controllers.admins.UserManage.FundAccountSearchVo;
import enums.pay.PayPlatform;

/**
 * 
 * 退款、提现账号.
 * 
 * @author youblade
 * @since  0.1
 */
public class FundAccount {
	
	public static final String TABLE_NAME = "fund_account";
    public long id;
    public long userId;
    /**
     * 账户姓名
     */
    public String name;
    /**
     * 账号ID（银行卡号）
     */
    public String no;
    
    public PayPlatform type;
    
    /**
     * 开户行
     */
    public String openingBank;
    public String address;
    public Date createTime;
    public Date modifyTime;
    
    @Transient
    public String userNick;
    
	/**
	 * 
	 * 取得用户所有绑定账户信息
	 * 
	 * @param userId
	 * @return
	 * @since v0.1
	 * @author moloch
	 * @created 2014-7-30 下午2:27:12
	 */
	public static List<FundAccount> findByUserId(long userId) {
		SqlSession ss = SessionFactory.getSqlSession();
		try {
			FundAccountMapper mapper = ss.getMapper(FundAccountMapper.class);
			return mapper.selectByUserId(userId);
		} finally {
			ss.close();
		}

	}
	
	/**
	 * 
	 * 保存提现、退款账号
	 *
	 * @since  v0.1
	 * @author moloch & youblade
	 * @created 2014-8-7 下午4:21:18
	 */
    public void save() {
        SqlSession ss = SessionFactory.getSqlSession();
        try {
            FundAccountMapper mapper = ss.getMapper(FundAccountMapper.class);
            this.modifyTime = DateTime.now().toDate();
            
            // 只有管理员才能修改
            if (this.id > 0) {
                // 将空参数置为null，避免误修改数据
                if(Strings.isNullOrEmpty(this.address)){
                    this.address = null;
                }
                mapper.updateById(this);
                return;
            }
            
            this.createTime = modifyTime;
            mapper.insert(this);
        } finally {
            ss.close();
        }
    }

    /**
     * 
     * 根据类型取得账号数据
     *
     * @param platform  :支付平台
     * @param userId
     * @return
     * @since  0.1
     * @author youblade
     * @created 2014年9月12日 下午6:25:24
     */
    public static FundAccount findByType(PayPlatform platform, long userId) {
        SqlSession ss = SessionFactory.getSqlSession();
        try {
            FundAccountMapper mapper = ss.getMapper(FundAccountMapper.class);
            return mapper.selectByType(userId, platform.toString());
        } finally {
            ss.close();
        }
    }
    
    /**
     * 
     * 获取用户的默认退款/提现账号.
     *
     * @param user
     * @return
     * @since  0.1
     * @author youblade
     * @created 2014年10月16日 下午7:27:49
     */
    public static FundAccount findDefault(User user) {
        Validate.notNull(user.id);
        Validate.notNull(user.type);
        
        SqlSession ss = SessionFactory.getSqlSession();
        try {
            FundAccountMapper mapper = ss.getMapper(FundAccountMapper.class);
            // 【买手】取财付通账号
            if(user.isBuyer()){
                return mapper.selectByType(user.id, PayPlatform.TENPAY.toString());
            }
            
            // 【商家】取银行卡，目前系统仅支持绑定一张银行卡
            return mapper.selectDefaultOneForSeller(user.id);
        } finally {
            ss.close();
        }
    }
    
    /**
     * 
     * TODO 获取银行卡
     *
     * @param user
     * @return
     * @since  v1.6
     * @author playlaugh
     * @created 2014年11月25日 下午6:02:35
     */
    public static FundAccount findBank(long userId) {
        
        SqlSession ss = SessionFactory.getSqlSession();
        try {
            FundAccountMapper mapper = ss.getMapper(FundAccountMapper.class);
            return mapper.selectBank(userId);
        } finally {
            ss.close();
        }
    }
    /**
     * 
     * 获取财付通账户
     *
     * @param userId
     * @return
     * @since  v1.6
     * @author playlaugh
     * @created 2014年11月29日 下午3:59:55
     */
    
    public static FundAccount findTenpay(long userId) {
        
        SqlSession ss = SessionFactory.getSqlSession();
        try {
            FundAccountMapper mapper = ss.getMapper(FundAccountMapper.class);
            return mapper.selectTenpay(userId);
        } finally {
            ss.close();
        }
    }
    /**
     * 
     * 获取支付宝账户
     *
     * @param userId
     * @return
     * @since  v1.6
     * @author playlaugh
     * @created 2014年11月29日 下午4:00:21
     */
    
    public static FundAccount findAlipay(long userId) {
        
        SqlSession ss = SessionFactory.getSqlSession();
        try {
            FundAccountMapper mapper = ss.getMapper(FundAccountMapper.class);
            return mapper.selectAlipay(userId);
        } finally {
            ss.close();
        }
    }
    

    /**
     * 
     * 【后台管理】分页获取账号记录.
     *
     * @param vo
     * @return
     * @since  v1.0
     * @author youblade
     * @created 2014年11月21日 下午2:41:35
     */
    public static Page<FundAccount> findByPage(FundAccountSearchVo vo) {
        vo.nick = StringUtils.trimToNull(vo.nick);
        vo.no = StringUtils.trimToNull(vo.no);
        vo.name = StringUtils.trimToNull(vo.name);
        
        SqlSession ss = SessionFactory.getSqlSession();
        try {
            FundAccountMapper mapper = ss.getMapper(FundAccountMapper.class);
            int count = mapper.count(vo);
            if (count <= 0) {
                return Page.EMPTY;
            }
            
            Page<FundAccount> page = Page.newInstance(vo.pageNo, vo.pageSize, count);
            page.items = mapper.selectList(vo);
            for (FundAccount fa : page.items) {
				fa.address=StringUtils.replaceOnce(fa.address, ",", "");
			}
            return page;
        } finally {
            ss.close();
        }
    }
    /**
     * 查询所有记录
     * @return
     */
    public static List<FundAccount> selectAll() {
    	 SqlSession ss = SessionFactory.getSqlSession();
         try {
             FundAccountMapper mapper = ss.getMapper(FundAccountMapper.class);
             return mapper.selectAll();
         } finally {
             ss.close();
         }
	}
    /**
     * 根据id修改地址
     * @param id
     * @param address
     * @return
     */
    public static Integer updateAddress(long id,String address) {
    	 SqlSession ss = SessionFactory.getSqlSession();
         try {
             FundAccountMapper mapper = ss.getMapper(FundAccountMapper.class);
             return mapper.updateAddress(id,address);
         } finally {
             ss.close();
         }
	}
}
