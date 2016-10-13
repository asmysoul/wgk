package controllers;

import models.AdminUser;
import models.AdminUser.AdminType;
import play.data.validation.Required;

import com.aton.base.BaseController;
import com.aton.base.secure.Secure;
import com.aton.config.BizConstants;
import com.aton.util.StringUtils;

import controllers.Admin;

/**
 * 
 * 管理员认证模块，处理管理员登陆流程
 * 
 * @author Mark Xu
 * @since  v2.0
 * @created 2015-1-14 上午10:20:16
 */
public class AdminAuthentication extends BaseController{

	/**
	 * 
	 * 验证用户登陆
	 *
	 * @param name
	 * @param pass
	 * @since  v2.0
	 * @author Mark Xu
	 * @created 2015-1-14 上午11:35:44
	 */
	public static void doLogin(@Required String name, @Required String pass,String validCode) {
		handleWrongInput(false);
		// 校验验证码
		String times=session.get(Secure.LOGIN_TIMES)==null?"1":session.get(Secure.LOGIN_TIMES);
		if (Integer.parseInt(times) > 3) {
			validCode = StringUtils.lowerCase(validCode);
			if (!validCode.equals(StringUtils.lowerCase(session.get("captcha")))) {
				flash(BizConstants.MSG, "验证码错误");
				renderArgs.put("name", name);
				Admin.login();
			}
		}
		AdminUser admin = AdminUser.findByName(name);
		
		//用户名或者密码错误，重新登录
		if(admin==null || !admin.validate(pass)) {
			if(times!=null&&!"".equals(times)){
				int loginTimes=Integer.parseInt(times)+1;
				session.put(Secure.LOGIN_TIMES, loginTimes);
			}else {
				session.put(Secure.LOGIN_TIMES, 1);
			}
			validation.keep();
			params.flash();
			flash.put("msg", "用户名或密码错误");
			Admin.login();
		}
		if(admin.status.toString()=="INVALID") {
			if(times!=null&&!"".equals(times)){
				int loginTimes=Integer.parseInt(times)+1;
				session.put(Secure.LOGIN_TIMES, loginTimes);
			}else {
				session.put(Secure.LOGIN_TIMES, 1);
			}
			flash.put("msg", "此管理员账号不允许登录");
			Admin.login();
		}
		//登录成功
		renderArgs.put("role", "admin");
		session.put(Secure.FLAG_ADMIN_AUTH, admin.id);
			Admin.index();
	}
	/**
	 * 
	 * 用户登出
	 *
	 * @since  v2.0
	 * @author Mark Xu
	 * @created 2015-1-14 上午11:38:43
	 */
    public static void logout() {
        session.remove("admin-authcode");
        redirect("/admin/login");
    }
}
