package controllers.admins;

import java.util.Map;

import models.AdminOperatorLog;
import models.AdminUser;
import models.User;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import play.data.validation.Min;
import play.data.validation.Required;
import vos.AdminOperatorLogSearchVo;
import vos.AdminSearchVo;

import com.aton.base.BaseController;
import com.aton.config.ReturnCode;
import com.google.common.collect.Maps;

import vos.Page;

/**
 * 
 * 超级管理员对管理员的相关操作
 * 
 * @author Mark Xu
 * @since  v2.0
 * @created 2015-1-15 下午5:54:48
 */
public class AdminManage extends BaseController{
	private static final Logger log = LoggerFactory.getLogger(UserManage.class);
	
	public static void adminList(@Required AdminSearchVo vo) {
		handleWrongInput(false);
		if(session.get("admin-authcode")==null)
			renderFailedJson(ReturnCode.ES_SEARCH_RETURN_EMPTY , "会话过期,请重新登录");
		long id = Long.parseLong(session.get("admin-authcode"));
		if(id != 1) {
			vo.id = id;
		}
		Page<AdminUser> page = AdminUser.findByPage(vo);
        renderPageJson(page.items, page.totalCount);
	}
	
	/**
	 * 
	 * 获取管理员操作记录列表
	 *
	 * @param vo
	 * @since  v2.0
	 * @author Mark Xu
	 * @created 2015-1-27 下午4:48:16
	 */
	public static void adminOperatorLogList(@Required AdminOperatorLogSearchVo vo) {
		handleWrongInput(false);
		Page<AdminOperatorLog> page = AdminOperatorLog.findByPage(vo);
		renderPageJson(page.items, page.totalCount);
	}
	
	/**
	 * 
	 * 查看某个管理员用户的详细资料
	 *
	 * @param uid
	 * @since  v2.0
	 * @author Mark Xu
	 * @created 2015-1-16 下午4:24:49
	 */
	public static void adminMsg(@Required @Min(1) long uid) {
        handleWrongInput(false);
        
        AdminUser admin = AdminUser.findByIdWichCache(uid);
        Map map = Maps.newHashMapWithExpectedSize(8);
        map.put("id", admin.id);
        map.put("name", admin.name);
        map.put("type", admin.type);
        map.put("qq", admin.qq);
        map.put("email", admin.email);
        map.put("mobile", admin.mobile);
        map.put("status", admin.status);
        map.put("message", admin.message);
        renderJson(map);
    }
	/**
	 * 
	 * 修改管理员信息
	 *
	 * @param admin
	 * @since  v2.0
	 * @author Mark Xu
	 * @created 2015-1-17 下午1:36:16
	 */
	public static void adminModify(@Required AdminUser admin) {
		handleWrongInput(false);
		AdminUser.adminModifyByAdmin(admin);
		renderSuccessJson();
	}
	
	/**
	 * 
	 * 增加管理员账户
	 *
	 * @param admin
	 * @since  v2.0
	 * @author Mark Xu
	 * @created 2015-1-17 下午1:37:01
	 */
	
	public static void adminInsert(@Required AdminUser admin) {
		handleWrongInput(false);
		AdminUser.adminInsertByAdmin(admin);
		renderSuccessJson();
	}
}
