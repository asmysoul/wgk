package models;

import java.beans.Encoder;

import javax.mail.Session;

import models.mappers.AdminMapper;
import models.mappers.UserMapper;

import org.apache.ibatis.session.SqlSession;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import play.libs.Codec;
import vos.AdminSearchVo;

import com.aton.config.CacheType;
import com.aton.db.SessionFactory;
import com.aton.util.CacheUtil;
import com.aton.util.StringUtils;
import vos.Page;
import com.google.common.base.Strings;

/**
 * 
 * 管理员用户分 【管理员】 【客服】【财务】 三种类型
 * 
 * @author Mark Xu
 * @since  v2.0
 * @created 2015-1-13 下午4:57:33
 */
public class AdminUser implements java.io.Serializable{
    private static final Logger log = LoggerFactory.getLogger(AdminUser.class);
    private static final Logger cacheLog = LoggerFactory.getLogger("cache");
    
    private static final long serialVersionUID = -7599268033418477238L;

    public static final String TABLE_NAME = "admin";
    
    public static final String FIELD_NAME = "name";
    public static final String FIELD_EMAIL = "email";
    public static final String FIELD_QQ = "qq";
    public static final String FIELD_MOBILE = "mobile";
    
    public long id;
    
    public AdminStatus status;
    public enum AdminStatus {
    	/**
    	 * 可登录的账号
    	 */
    	VALID("可登录的账号"),
    	/**
    	 * 不可登录的账号
    	 */
    	INVALID("不可登录的账号");
    	public String title;
        private AdminStatus(String title){
            this.title = title;
        }
    } 
    public AdminType type;
    public enum AdminType {
    	/**
    	 * 超级管理员
    	 */
    	SUPERADMIN("超级管理员"),
    	/**
    	 * 管理员
    	 */
    	ADMIN("管理员"),
    	/**
    	 * 财务
    	 */
    	FINANCE("财务"),
    	/**
    	 * 客服
    	 */
    	SERVICE("客服");
    	public String title;

		private AdminType(String title) {
			this.title = title;
		}
    }
    /** 随机生成的随机数 */
    public String salt;
    
    /**
     * 存储MD5(origin password+salt)
     */
    public String password;
    
    public String message;
    public String name;
    public String email;
    public String qq;
    public String mobile;

    
    
	/**
	 * 根据姓名查找用户
	 *
	 * @param name
	 * @return 
	 * @since  v2.0
	 * @author Mark Xu
	 * @created 2015-1-14 上午10:26:41
	 */
	public static AdminUser findByName(String name) {
		// TODO Auto-generated method stub
		SqlSession ss = SessionFactory.getSqlSession();
		try {
			AdminMapper mapper = ss.getMapper(AdminMapper.class);
			return mapper.selectByName(name);
		}finally {
			ss.close();
		}
	}
	/**
	 * 
	 * 验证管理员登陆密码
	 *
	 * @param inputPass
	 * @return
	 * @since  v2.0
	 * @author Mark Xu
	 * @created 2015-1-14 上午11:06:59
	 */
	public boolean validate(String inputPass) {
		if(Strings.isNullOrEmpty(inputPass)) {
			return false;
		}
		return this.password.equals(Codec.hexMD5(inputPass + this.salt));
	}
    
	/**
	 * 
	 * 获取当前登录的管理员用户,到DB中找
	 *
	 * @param id
	 * @return
	 * @since  v2.0
	 * @author Mark Xu
	 * @created 2015-1-14 下午3:43:59
	 */
	public static AdminUser findByIdWichCache(Long id) {
		AdminUser admin = findById(Long.valueOf(id));
        return admin;
    }

	/**
	 * 
	 *
	 * @param valueOf
	 * @return
	 * @since  v2.0
	 * @author Mark Xu
	 * @created 2015-1-14 下午4:07:01
	 */
	public static AdminUser findById(Long id) {
		SqlSession ss = SessionFactory.getSqlSession();
        try {
            AdminMapper mapper = ss.getMapper(AdminMapper.class);
            return mapper.selectById(id);
        } finally {
            ss.close();
        }
	}
	
	/**
	 * 分页获取管理员用户数据
	 *
	 * @param vo
	 * @return
	 * @since  v2.0
	 * @author Mark Xu
	 * @created 2015-1-15 下午6:16:54
	 */
	public static Page<AdminUser> findByPage(AdminSearchVo vo) {
		vo.name = StringUtils.trimToNull(vo.name);//过滤参数
		vo.qq = StringUtils.trimToNull(vo.qq);
		vo.email = StringUtils.trimToNull(vo.email);
		vo.mobile = StringUtils.trimToNull(vo.mobile);
		SqlSession ss = SessionFactory.getSqlSession();
        try {
            AdminMapper mapper = ss.getMapper(AdminMapper.class);
            int totalCount = mapper.count(vo);
            if (totalCount <= 0) {
                return Page.EMPTY;
            }

            Page<AdminUser> page = Page.newInstance(vo.pageNo, vo.pageSize, totalCount);
            page.items = mapper.selectListForAdmin(vo);
            return page;
        } finally {
            ss.close();
        }
	}
	
	/**
	 * 
	 * 通过管理员对象更改管理员信息
	 *
	 * @param admin
	 * @since  v2.0
	 * @author Mark Xu
	 * @created 2015-1-17 上午10:05:40
	 */
	public static void adminModifyByAdmin(AdminUser admin) {
		admin.name = StringUtils.trimToNull(admin.name);//过滤参数
		admin.password = StringUtils.trimToNull(admin.password);
		if(admin.password != null) {
			admin.password = Codec.hexMD5(admin.password + findById(admin.id).salt); 
		}
		admin.qq = StringUtils.trimToNull(admin.qq);
		admin.email = StringUtils.trimToNull(admin.email);
		admin.mobile = StringUtils.trimToNull(admin.mobile);
		admin.message = StringUtils.trimToNull(admin.message);
		SqlSession ss = SessionFactory.getSqlSession();
		try {
			AdminMapper mapper = ss.getMapper(AdminMapper.class);
			mapper.updateByAdmin(admin);
		}finally {
			ss.close();
		}
	}
	/**
	 * 通过管理员对象插入管理员信息
	 *
	 * @param admin
	 * @since  v2.0
	 * @author Mark Xu
	 * @created 2015-1-17 下午1:39:27
	 */
	public static void adminInsertByAdmin(AdminUser admin) {
		admin.name = StringUtils.trimToNull(admin.name);//过滤参数
		admin.password = StringUtils.trimToNull(admin.password);
		admin.salt = String.valueOf(Math.random()).substring(2, 10);
		admin.password = Codec.hexMD5(admin.password + admin.salt); 
		admin.qq = StringUtils.trimToNull(admin.qq);
		admin.email = StringUtils.trimToNull(admin.email);
		admin.mobile = StringUtils.trimToNull(admin.mobile);
		admin.message = StringUtils.trimToNull(admin.message);
		SqlSession ss = SessionFactory.getSqlSession();
		try {
			AdminMapper mapper = ss.getMapper(AdminMapper.class);
			mapper.insertByAdmin(admin);
		}finally {
			ss.close();
		}
		
	}
}
