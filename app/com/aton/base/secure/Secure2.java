package com.aton.base.secure;

import java.io.UnsupportedEncodingException;

import models.AdminUser;
import models.Notice;
import models.Task;
import models.User;
import models.User.UserStatus;
import models.User.UserType;

import org.apache.commons.lang.Validate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import play.libs.Codec;
import play.mvc.Before;
import play.mvc.Http.Cookie;
import play.mvc.Util;

import com.aton.Constant;
import com.aton.config.CacheType;
import com.aton.config.AppMode;
import com.aton.util.CacheUtil;
import com.aton.config.AppMode.Mode;
import com.aton.config.ReturnCode;
import com.aton.util.NumberUtils;
import com.aton.util.QnCloudUtil.QnFileBucket;
import com.aton.util.StringUtils;

import controllers.UserAuthentication;

/**
 * 
 * 安全校验过滤器<br>
 * 
 * 1、检查用户（包括普通用户、Admin等）操作是否合法.<br>
 * 2、对普通用户的操作进行log，并持久化到MongoDB.<br>
 * 
 * @priority = 1~3
 * @author youblade
 * @since v0.1
 */
public class Secure2 extends BaseSecure {

	private static final Logger log = LoggerFactory.getLogger(Secure2.class);
	
	/**
	 * 用于判断用户是否登录的标记
	 */
	public static final String FIELD_AUTH = "authcode";
	public static final String FLAG_ADMIN_AUTH = "admin-authcode";
	public static final String FLAG_KJKD_AUTH = "kjkd-authcode";
	public static final String FLAG_FLOW_AUTH = "flow-authcode";
	public static final String TASK_NUM = "taskNum";
	public static final String UPLOAD_TASK_ADDRESS = "upload_task_address";
	public static final String UPLOAD_NOTICE_ADDRESS = "upload_notice_address";
	public static final String LOGIN_TIMES = "loginTimes";//判断登录次数
	/**
	 * 
	 * 检查是否登录用户.
	 * 
	 * @throws UnsupportedEncodingException
	 * 
	 * @since v0.1
	 */
	@Before(priority = 1, unless = { "Application.regist", "Application.index",
			"Application.login", "Application.findPass", "Application.index",
			"Admin.login", "Admin.doLogin", "ExpressPrint.login",
			"ExpressPrint.doLogin","Flow.login","Flow.doLogin" })
	public static void checkAuthentication() {
		// 本地开发测试时模拟登录
		// if (request.url.startsWith("/admin/getcode") && isDevOnLocalHost()) {
		// return;
		// }
		/**
		 * 准备初始数据--最新公告
		 */
		
		Notice notice = Notice.findTopNotice();
		if(notice.url == null || notice.url.equals("")){
			notice.url = "/notice/" + notice.id;
		}
		renderArgs.put(TOP_NOTICE, notice);
		
		// 检查请求的操作是否需要管理员权限
		if (request.url.startsWith("/admin")) {
			checkAdminOperate();
			// 设置图片上传空间
			setFileUploadSpace();
			
			String loginAdminId = session.get(FLAG_ADMIN_AUTH);
			
			if (loginAdminId == null) {
				log.error("Login admin not exists,admin_id={}", loginAdminId);
				handleIllegalAdminRequest(ReturnCode.INVALID_SESSION);
			}
			AdminUser admin = AdminUser.findByIdWichCache(Long.valueOf(loginAdminId));
			session.put(FLAG_ADMIN_AUTH, admin.id);
			//管理员页面上显示当前可接任务数的总数
			
			renderArgs.put(TASK_NUM, Task.fetchBuyerTasksNotTakenNum());
			//添加管理员数据，供页面使用
			renderArgs.put(FIELD_ADMIN, admin);
			return;
		}

		// 判断是否是快递用户登录
		if (request.url.startsWith("/express")) {
			checkKjkdOperate();
			return;
		}
		
		if (request.url.startsWith("/flow")) {
            checkFlowOperate();
            return;
        }
		
		// 检查是否合法请求
		/*String loginUserId = session.get(FIELD_AUTH);
		if (StringUtils.isBlank(loginUserId)) {
			handleIllegalRequest(ReturnCode.INVALID_SESSION);
		}*/
		if (UserAuthentication.LQBZ==0) {
			handleIllegalRequest(ReturnCode.INVALID_SESSION);
		}

		User user = User.findByIdWichCache(UserAuthentication.LQBZ);
		if (user == null) {
			log.error("Login user not exists,user_id={}", UserAuthentication.LQBZ);//
			handleIllegalRequest(ReturnCode.INVALID_SESSION);
		}
		
		// 添加用户数据，供页面显示用
		renderArgs.put(FIELD_USER, user);

		// 设置图片上传空间
		setFileUploadSpace();
		}

	/**
     * TODO Comment
     *
     * @since  v2.9
     * @author fufei
     * @created 2015年4月8日 上午11:52:06
     */
    private static void checkFlowOperate() {
        // 设置角色标识：用于控制页面显示内容
        if (isDevOnLocalHost()) {
          renderArgs.put("role", "kjkd");
          return;
      }
        if(!isValidFlow()){
            renderText("FUCK");
        }
        renderArgs.put("role", "flow");
    }

    /*
	 * 检查是否有快递管理员操作权限
	 */
	private static void checkKjkdOperate() {
		if (isDevOnLocalHost()) {
			renderArgs.put("role", "kjkd");
			return;
		}

		if (!isValidKJKD()) {
			renderText("FUCK");
		}

		// 设置角色标识：用于控制页面显示内容
		renderArgs.put("role", "kjkd");
	}

	@Util
    public static boolean isValidFlow() {
        // 校验过之后的简单校验
        String role = String.valueOf(renderArgs.get("role"));
        if (StringUtils.equals(role, "flow")) {
            return true;
        }

        // 首次校验
        Cookie atonCookie = request.cookies.get(FLAG_FLOW_AUTH);
        if (atonCookie == null) {
            return false;
        }
        String code = atonCookie.value;
        int codeLength = code.length();
        if (codeLength == 0) {
            return false;
        }
        String num1Str = code.substring(1, 2);
        String num2Str = code.substring(code.length() - 1, code.length());

        int num1 = NumberUtils.toInt(num1Str);
        int num2 = NumberUtils.toInt(num2Str);
        if (num1 + num2 != 8) {
            return false;
        }
        return true;
    }
	
	@Util
	public static boolean isValidKJKD() {
		// 校验过之后的简单校验
		String role = String.valueOf(renderArgs.get("role"));
		if (StringUtils.equals(role, "kjkd")) {
			return true;
		}

		// 首次校验
		Cookie atonCookie = request.cookies.get(FLAG_KJKD_AUTH);
		if (atonCookie == null) {
			return false;
		}
		String code = atonCookie.value;
		int codeLength = code.length();
		if (codeLength == 0) {
			return false;
		}
		String num1Str = code.substring(1, 2);
		String num2Str = code.substring(code.length() - 1, code.length());

		int num1 = NumberUtils.toInt(num1Str);
		int num2 = NumberUtils.toInt(num2Str);
		if (num1 + num2 != 8) {
			return false;
		}
		return true;
	}

	/*
	 * 设置
	 */
	private static void setFileUploadSpace() {
		if (AppMode.get().mode == Mode.TEST) {
			renderArgs.put(UPLOAD_TASK_ADDRESS, QnFileBucket.TEST.code);
			renderArgs.put(UPLOAD_NOTICE_ADDRESS, QnFileBucket.TEST.code);
		} else if (AppMode.get().mode == Mode.ONLINE) {
			renderArgs
					.put(UPLOAD_TASK_ADDRESS, QnFileBucket.ONLINE_PUBLIC.code);
			renderArgs.put(UPLOAD_NOTICE_ADDRESS,
					QnFileBucket.ONLINE_NOTICE_PUBLIC.code);
		} else {
			renderArgs.put(UPLOAD_TASK_ADDRESS, QnFileBucket.DEV.code);
			renderArgs.put(UPLOAD_NOTICE_ADDRESS, QnFileBucket.DEV.code);
		}
	}

	/*
	 * 是否本地开发测试
	 */
	private static boolean isDevOnLocalHost() {
		String ip = request.remoteAddress;

		// 本地开发测试模式下无来访IP
		if (StringUtils.isBlank(ip) && AppMode.get().mode == AppMode.Mode.TEST) {
			return true;
		}

		// 根据访问的IP来判断
		return Constant.LOCAL_HOST_IP.equals(ip) && AppMode.get().isNotOnline();
	}

	/*
	 * 检查是否有管理员操作权限
	 */
	private static void checkAdminOperate() {
//		if (isDevOnLocalHost()) {
//			renderArgs.put("role", "admin");
//			return;
//		}
//
//		if (!isValidAdmin()) {
//			renderText("FUCK");
//		}

		// 设置角色标识：用于控制页面显示内容
		renderArgs.put("role", "admin");
	}

	@Util
	static boolean isValidate(String inputPasword, String pasword, String salt) {
		Validate.notEmpty(inputPasword);
		Validate.notEmpty(pasword);
		Validate.notEmpty(salt);

		return Codec.hexMD5(inputPasword + salt).equalsIgnoreCase(pasword);
	}

	@Util
	public static boolean isValidAdmin() {
		// 校验过之后的简单校验
		String role = String.valueOf(renderArgs.get("role"));
		if (StringUtils.equals(role, "admin")) {
			return true;
		}

		// 首次校验
		Cookie atonCookie = request.cookies.get(FLAG_ADMIN_AUTH);
		if (atonCookie == null) {
			return false;
		}
		String code = atonCookie.value;
		int codeLength = code.length();
		if (codeLength == 0) {
			return false;
		}
		String num1Str = code.substring(1, 2);
		String num2Str = code.substring(code.length() - 1, code.length());

		int num1 = NumberUtils.toInt(num1Str);
		int num2 = NumberUtils.toInt(num2Str);
		if (num1 + num2 != 8) {
			return false;
		}
		return true;
	}

	/**
	 * 
	 * 检查是否用户身份：是否为卖家.
	 * 
	 * @since 0.1
	 * @author youblade
	 * @created 2014年8月7日 下午4:22:43
	 */
	// @formatter:off
	@Before(priority = 3, only = { "TaskCenter.sellerTasks",
			"TaskCenter.listSellerTasks", "TaskCenter.taskDetail",
			"TaskCenter.exportBuyerTasks", "TaskCenter.sendGoods",
			"TaskCenter.refund", "TaskPublish.publish", "TaskPublish.saveTask",
			"TaskPublish.getInfo", "TaskPublish.getItemInfo",
			"TaskPublish.republish", "UserCenter.shop", "UserCenter.listShops",
			"UserCenter.addShop", "UserCenter.blacklist", })
	// @formatter:on
	public static void checkSellerRole() {
		if (isValidAdmin()) {
			return;
		}
		checkUserRole(UserType.SELLER);
	}

	/**
	 * 
	 * 检查是否用户身份：是否为买手.
	 * 
	 * @since 0.1
	 * @author youblade
	 * @created 2014年8月7日 下午4:23:20
	 */
	// @formatter:off
	@Before(priority = 3, only = { "TaskCenter.buyerTasks",
			"taskCenter.buyerTaskDetail", "TaskCenter.take",
			"TaskExecutor.perform", "UserCenter.buyerAccount",
			"UserCenter.accountTaskStatus", "UserCenter.saveBuyerAccount",
			"UserCenter.listBuyerAccounts", })
	// @formatter:on
	public static void checkBuyerRole() {
		if (isValidAdmin()) {
			return;
		}
		checkUserRole(UserType.BUYER);
	}

	/**
	 * 
	 * 限制未开通会员、会员过期用户使用功能.
	 * 
	 * @since 0.1
	 * @author youblade
	 * @created 2014年9月16日 下午7:56:08
	 */
	// @formatter:off
	@Before(priority = 5, only = {
			// 绑定店铺
			"UserCenter.shop",
			"UserCenter.listShops",
			"UserCenter.addShop",
			// 发布任务
			"TaskPublish.publish", "TaskPublish.getItemInfo",
			"TaskPublish.saveTask", "TaskPublish.confirmPayment",
			"TaskPublish.pay", "TaskPublish.getItemImages",
			"TaskPublish.uploadItemImages", "TaskPublish.edit",
			"TaskPublish.republish",
			// 接任务、查看“任务列表”
			"TaskCenter.take", "TaskCenter.tasks", "TaskCenter.listTasks",
			// 资金管理
			"UserCenter.saveFundAccount", "TaskPublish.publishFlow"})
	// @formatter:on
	protected static void checkMember() {
		if (AppMode.get().disableMemberCheck || isAdminOperate()) {
			return;
		}

		User seller = getCurrentUser();
		if (seller.status == UserStatus.VALID) {
			return;
		}

		// 针对Ajax请求返回特定格式的消息
		if (request.isAjax()) {
			renderFailedJson(ReturnCode.FREE_BIZ_LIMIT);
		}

		// 页面访问直接返回一个统一的结果提示页
		String url = request.url;
		if (url.startsWith("/user/shop")) {
			renderArgs.put("module", "shop");
		}
		if (StringUtils.startsWithIgnoreCase(request.action, "TaskPublish")) {
			renderArgs.put("module", "publishTask");
		}
		if (url.startsWith("/user/money")) {
			renderArgs.put("module", "money");
		}
		render("result.html");
	}
}
