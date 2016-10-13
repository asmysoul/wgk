package com.aton.base;

import java.text.MessageFormat;
import java.util.List;

import models.User;
import models.User.UserType;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import play.Play;
import play.mvc.Before;
import play.mvc.Catch;
import play.mvc.Controller;

import com.aton.Constant;
import com.aton.base.secure.SessionInvalidException;
import com.aton.config.AppMode;
import com.aton.config.AppMode.Mode;
import com.aton.config.ReturnCode;
import com.aton.util.JsonUtil;
import com.aton.util.MixHelper;
import com.aton.vo.AjaxResult;
import com.aton.vo.PageAjaxResult;

// @formatter:off
/**
 * 
 * 所有Controller的父类，提供公用的便利方法、统一处理异常等.<br>
 * 
 * 【注意】：
 * 1.不要在此类中添加@Before方法，由于Secure\FeatureCheck等拦截器类继承自该类，此类中定义的@Before方法会被重复调用. <br>
 * 		See <a href="http://www.playframework.com/documentation/1.2.5/controllers#interceptions">Play官方文档</a> <br>
 * 
 * 2.添加@After方法时需注意测试，避免上述问题 
 * 
 * @author youblade
 * @since v0.1
 */
// @formatter:on
public class BaseController extends Controller {

	private static final Logger log = LoggerFactory.getLogger(BaseController.class);

	public static final String TAG = "BaseController";

	/**
	 * 前后端交互时，存储认证用户身份标识的字段
	 */
	public static final String FIELD_USER = "user";
	
	/**
	 * 前后端交互时，存储认证管理员身份标识的字段 
	 */
	public static final String FIELD_ADMIN = "admin";
	
	/**
	 * 置顶公告标识字段
	 */
	public static final String TOP_NOTICE = "topNotice";
	
	/**
	 * 对返回数据进行包装，格式为：{"code":200,"msg":"","results":"输入对象的JSON结构"}
	 */
	protected static void renderJson(Object obj) {
		renderJSON(JsonUtil.toJson(new AjaxResult(ReturnCode.OK, StringUtils.EMPTY, obj)));
	}
	
	/**
	 * 对返回数据进行包装，格式为：{"code":200,"msg":"","results":"输入对象的JSON结构","totalCount":100}
	 */
	protected static void renderPageJson(List<?> list,int totalCount) {
		renderText(new PageAjaxResult(list, totalCount).toJson());
	}

	/**
	 * 对返回数据进行包装，格式为：{"code":200,"msg":""}
	 */
	protected static void renderSuccessJson() {
		renderJSON(JsonUtil.toJson(new AjaxResult(ReturnCode.OK)));
	}

	/**
	 * 对返回数据进行包装，格式为：{"code":输入的code,"msg":""}
	 */
	protected static void renderFailedJson(int code) {
		renderJSON(JsonUtil.toJson(new AjaxResult(code)));
	}

	/**
	 * 对返回数据进行包装，格式为：{"code":输入的code,"msg":"输入的msg"}
	 */
	protected static void renderFailedJson(int code, String msg) {
		renderJSON(JsonUtil.toJson(new AjaxResult(code, msg)));
	}
	protected static void renderFailedJson(int code, Object obj) {
		renderJSON(JsonUtil.toJson(new AjaxResult(code, StringUtils.EMPTY, obj)));
	}

	/**
	 * 
	 * 返回统一样式的提示信息页面.
	 *
	 * @param title
	 * @param msg
	 * @since  v1.4
	 * @author youblade
	 * @created 2014年11月21日 上午10:41:04
	 */
	protected static void result(String title, String msg) {
        renderArgs.put("errTitle", title);
        renderArgs.put("errMsg", msg);
        render("result.html");
    }
	
	/**
	 * 
	 * 封装前端功能菜单的module属性，用于标记当前功能菜单为其添加选中样式.
	 * [参考]前端页面frame.html
	 * 
	 * @author youblade
	 * @since  v1.0
	 * @created 2014年11月21日 上午10:49:22
	 */
	protected enum Module{
	    // 卖家
	    user,
	    myTask,
	    publishTask,
	    shop,
	    money,
	    blacklist,
	    // 买手
	    buyerAccount,
	    // 管理员
	    task,
	    account,
	    notice,
	    tasklist,
	    //快递打印
	    waitExpressPrint,
	    printingOrders,
	    expressPrintFinish,
	}
	
	/**
	 * 
	 * 返回统一样式的提示信息页面.
	 *
	 * @param title
	 * @param msg
	 * @param module :用于在前端页面标记选中当前功能菜单
	 * @since  v1.4
	 * @author youblade
	 * @created 2014年11月21日 上午10:41:04
	 */
	protected static void result(String title, String msg, Module module) {
	    renderArgs.put("module", module.toString());
	    renderArgs.put("errTitle", title);
	    renderArgs.put("errMsg", msg);
	    render("result.html");
	}
    
	/**
	 * 
	 * 获取当前会话中的用户.
	 * 
	 * @return
	 * @since v0.1
	 */
	public static User getCurrentUser() {
		// Secure校验通过后，已设置用户数据
	    if (renderArgs.get(FIELD_USER) == null&&renderArgs.get(FIELD_ADMIN)==null) {
            log.error("user INVALID_SESSION ");
            handleIllegalRequest(ReturnCode.INVALID_SESSION);
        }
		return (User) renderArgs.get(FIELD_USER);
	}

	/**
	 * 
	 * 设置一些全局的开关参数.
	 *
	 * @since  0.1
	 * @author youblade
	 * @created 2014年10月9日 下午7:55:41
	 */
    @Before(priority = 0)
    public static void setGlobalSwitcher() {
        // 本机访问时不进行设置
        String domain = request.domain;
        if(Constant.LOCAL_HOST.equals(domain)||Constant.LOCAL_HOST_IP.equals(domain)){
            return;
        }
        
        if(AppMode.get().mode == Mode.ONLINE){
            renderArgs.put("enableBaidu", Boolean.TRUE);
        }
    }
    
	/**
	 * 
	 * 全局的Session失效异常处理.
	 * 
	 * @param e
	 * @since v0.1
	 */
	@Catch(SessionInvalidException.class)
	static void handle(SessionInvalidException e) {
		log.error("---catch SessionInvalidException--");
		handleIllegalRequest(e.code);
	}

	/**
	 * 
	 * 全局的运行时异常处理.
	 * 
	 * @param e
	 * @since v0.1
	 */

	@Catch(RuntimeException.class)
	static void handleServerError(Throwable e) {
	    // 为开发时方便调试，显示异常堆栈信息到500页面
	    if(Play.mode.isDev() && !request.isAjax()){
	        e.printStackTrace();
	        return;
	    }

		log.error("=======>Server Fatal Error<======", e.getMessage());
		log.error(e.getMessage(),e);

		// 针对Ajax请求返回特定格式的消息
		if (request.isAjax()) {
			renderFailedJson(ReturnCode.INNER_ERROR);
		}
		// 跳转到宕机页面
		render("errors/500.html",e);
	}
	/**
	 * 
	 * 处理非法请求：未认证、会话过期等
	 * 
	 * @param failedReturnCode 针对Ajax请求返回的错误码
	 * @since v0.1
	 */
    protected static void handleIllegalRequest(int failedReturnCode) {

		// 针对Ajax请求返回特定格式的消息
		if (request.isAjax()) {
			renderFailedJson(failedReturnCode);
		}

		// 转向登录页
		redirect("/login");
	}
	
	/**
	 * 
	 * 处理非法请求：未认证、会话过期等
	 * 
	 * @param failedReturnCode 针对Ajax请求返回的错误码
	 * @since v0.1
	 */
	protected static void handleIllegalAdminRequest(int failedReturnCode) {

		// 针对Ajax请求返回特定格式的消息
		if (request.isAjax()) {
			renderFailedJson(failedReturnCode);
		}

		// 转向登录页
		redirect("/admin/login");
	}

	/**
	 * 
	 * 处理错误的用户输入.
	 * 
	 * @param withErrMsg
	 * @since  v0.1
	 */
    protected static void handleWrongInput(boolean withErrMsg) {
        if (!validation.hasErrors()) {
            return;
        }
        for (play.data.validation.Error error : validation.errors()) {
            try {
                log.error("Input wrong: {}", error.message());
            } catch (Exception e) {
                log.warn("Input wrong, and Exception={} when log", e.getMessage());
            }
            
            // 开发测试时打印参数校验错误，便于调试
            if (AppMode.get().isNotOnline()) {
                MixHelper.print("Input wrong: {}", error.message());
            }
            
            if (withErrMsg) {
                renderFailedJson(ReturnCode.WRONG_INPUT, error.message());
            }
            renderFailedJson(ReturnCode.WRONG_INPUT);
        }
    }

	/**
	 * 
	 * 禁止非指定角色的用户访问.
	 *
	 * @param userType
	 * @since  0.1
	 * @author youblade
	 * @created 2014年8月1日 下午2:35:07
	 */
	protected static void checkUserRole(UserType userType) {
	    if(getCurrentUser().type != userType){
	        forbidden(MessageFormat.format("该功能仅对[{0}]用户开放！", userType.title));
	    }
	}
	
	/**
	 * 
	 * 是否管理员操作.
	 *
	 * @return
	 * @since  v0.2
	 * @author youblade
	 * @created 2014年10月22日 下午12:38:57
	 */
	protected static boolean isAdminOperate() {
	    return request.url.startsWith("/admin");
	}
}


