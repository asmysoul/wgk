package controllers;

import java.math.BigDecimal;
import java.util.List;

import models.BuyerAccount;
import models.BuyerTask;
import models.MemberChargeRecord;
import models.Task;
import models.User;
import models.UserIngotRecord;
import models.User.UserStatus;
import models.User.UserType;
import models.mappers.MemberChargeRecordMapper;
import models.mappers.UserIngotRecordMapper;
import notifiers.Mails;

import org.joda.time.DateTime;
import org.joda.time.LocalDate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import play.data.validation.Required;
import play.libs.Codec;
import play.libs.Images;
import play.mvc.Http.Header;
import play.mvc.With;
import vos.BuyerAccountSearchVo;
import vos.BuyerAccountSearchVo.ExamineStatus;
import vos.TaskSearchVo;

import com.aton.base.BaseController;
import com.aton.base.secure.BaseSecure;
import com.aton.base.secure.Secure;
import com.aton.config.BizConstants;
import com.aton.config.CacheType;
import com.aton.config.ReturnCode;
import com.aton.util.CacheUtil;
import com.aton.util.MixHelper;
import com.aton.util.SmsUtil;
import com.aton.util.SmsUtil111;
import com.aton.util.StringUtils;
import com.google.common.net.HttpHeaders;

import enums.Device;
import enums.Platform;
import enums.TaskStatus;

/**
 * 
 * 认证模块：处理用户注册、登录流程.
 * 
 * @author youblade
 * @since 0.1
 * @created 2014年7月12日 下午5:51:45
 */
@With(BaseSecure.class)
public class UserAuthentication extends BaseController {

	private static final Logger log = LoggerFactory.getLogger(UserAuthentication.class);
	
    public static final String FIELD_USER_ID = "userId";
    public static final String FIELD_EMAIL = "email";
    
    public static Long LQBZ = (long) 0;
    
    /*
     * 转发至注册页面（用于注册用户激活）
     */
    private static void forwardRegist(User user) {
        flash(FIELD_USER_ID, user.id);
        flash(FIELD_EMAIL, user.email);
        Application.regist(null);
    }
    
	/*
	 * 转发至重设密码页面
	 */
	private static void forwardResetpass(User user) {
		flash(FIELD_USER_ID, user.id);
		flash(FIELD_EMAIL, user.email);
		Application.findPass();
	}
    //--------------------------------------------------------------------------------------------------------
	//app链接接口
		public static void doLoginApp(@Required String nick, @Required String pass) {
			handleWrongInput(false);
			User user = User.findByNick(nick);
			// 用户名或密码错误 重新登陆
			if (user == null || !user.validate(pass)) {
				renderJson("1");
			}
			// 邮箱未激活则跳转到提示页面
			if (user.status == UserStatus.INACTIVE) {
				forwardRegist(user);
			}

			if (user.status == UserStatus.LOCKED) {
				validation.keep();
				params.flash();
				flash.put(BizConstants.MSG, "账号被冻结,有疑问请联系网站客服");
				Application.login();
			}
			// 登录成功
			session.put(Secure.FIELD_AUTH, user.id);
			LQBZ = user.id;
			// 添加用户数据，供页面显示用
			renderArgs.put(FIELD_USER, user);
			renderJson("0");
			// 设置用户未完成的任务
			setUserTodoTask(user);

			if (user.type == UserType.SELLER) {
				TaskCenter.sellerTasks();
			}
			TaskCenter.buyerTasks();
		}
		
		
		
		
		/*
		 * app 取任务
		 * 
		 */
		public static void ma(String platform, String status, String device) {
			TaskSearchVo vo = new TaskSearchVo();
			vo.status = TaskStatus.valueOf(status);
			vo.platform = Platform.valueOf(platform);
			vo.device = Device.valueOf(device);
			renderJson(Task.findALLList(vo));
		}
	
	
	
	
	
	
    /**
     * 
     * 检查用户注册信息.
     * 
     * @param user
     * @param smsValidCode
     * @since 0.1
     * @author youblade
     * @created 2014年7月14日 下午6:41:06
     */
    public static void checkRegist(@Required User user, String smsValidCode) {
        handleWrongInput(false);

        // 检查短信验证码
        if (StringUtils.isNotBlank(smsValidCode) && StringUtils.isNotBlank(user.nick)) {
            String key = CacheType.SMS_VALID_CODE.getKey(user.nick);
            String validCode = CacheUtil.get(key);
            log.info("User={},input code={} and cachedCode={}", new Object[] { user.nick, smsValidCode, validCode });
            boolean isValidCode = StringUtils.equals(validCode, smsValidCode);
            renderText(isValidCode);
        }
        
        boolean result = true;
        if (StringUtils.isNotBlank(user.nick)) {
            result = User.isUserExists(User.FIELD_NICK, user.nick);
        }
        if (StringUtils.isNotBlank(user.email)) {
            result = User.isUserExists(User.FIELD_EMAIL, user.email);
        }
        if (StringUtils.isNotBlank(user.qq)) {
            result = User.isUserExists(User.FIELD_QQ, user.qq);
        }
        if (StringUtils.isNotBlank(user.mobile)) {
            result = User.isUserExists(User.FIELD_MOBILE, user.mobile);
        }
        
        // 注意：若用户存在则返回false
        renderText(!result);
    }
    
    
    
  //-------------------------------------------------------------------------------------------------------------------------------------------
    /*
     * app 获取买手买号
     */
    /**
	 * 
	 * 获取买手的购买账号.
	 * 
	 * @param receive
	 *            :是否用于接手任务
	 * @since 0.1
	 * @author youblade
	 * @created 2014年8月1日 上午10:29:27
	 */
	public static void listBuyerAccountsApp(Platform platform, boolean receive ,String nick) {
		//handleWrongInput(false);
		

		User user = User.findByNick(nick);
		// 淘宝、天猫数据目前不分开
		if (platform == Platform.TMALL) {
			platform = Platform.TAOBAO;
		}
		
        BuyerAccountSearchVo vo = BuyerAccountSearchVo.newInstance().platform(platform).userId(user.id);
		
		// 接手任务时仅返回可供使用的买号（每个买号每天限制5个任务）
        if (receive) {
            vo.status(ExamineStatus.EXAMINED);
            renderJson(BuyerAccount.findForTakingTask(vo));
        }

		List<BuyerAccount> list = BuyerAccount.findList(vo);
		renderJson(list);
	}
    
    
    
    
    
    /**
     * 
     * 发送短信验证码.
     * 验证码有效期为60s，期间限制再次发送
     *
     * @param user
     * @since  0.1
     * @author youblade
     * @created 2014年9月27日 下午1:56:10
     */
    public static void sendSmsValidCode(@Required User user) {
        if (user != null) {
            validation.required("user.nick", user.nick);
            validation.required("user.mobile", user.mobile);
        }
        handleWrongInput(false);

        // 检查手机号
        boolean isExists = User.isUserExists(User.FIELD_MOBILE, user.mobile);
        if(isExists){
            renderFailedJson(ReturnCode.OP_BIZ_LIMIT);
        }
        
        /*
         * TODO 需要进一步检查是否真实用户，避免被滥用
         */
        String referer = StringUtils.EMPTY;
        Header header = request.headers.get(HttpHeaders.REFERER);
        if (header != null) {
            referer = header.value();
        }
        log.info("User={} send validCode to {},referer:{}", new Object[] { user.nick, user.mobile, referer });

        // 若上次发送的尚未过期，则限制再次发送，避免高频滥发
        String key = CacheType.SMS_VALID_CODE.getKey(user.nick);
        Object object = CacheUtil.get(key);
        if (object != null) {
            log.info("User={} has smsValidCode exists,{}", user.nick, object.toString());
            log.warn("User={},mobile={} from ip={} send smsValidCode too fast!", new Object[] { user.nick, user.mobile,
                    request.remoteAddress });
            renderSuccessJson();
        }

        // 生成6位随机数字并缓存
        String smsValidCode = String.valueOf(Math.random()).substring(2, 8);
        String smsContent = MixHelper.format("您的验证码为[{}]，本验证码2分钟内有效，感谢您使用.", smsValidCode);
        log.info("User={},mobile={},sms={}", new Object[] { user.nick, user.mobile, smsContent });
        
        boolean isSuccess = SmsUtil.send(user.mobile, smsContent);
        if (!isSuccess) {
            log.error("User={} send smsValidCode failed");
            renderFailedJson(ReturnCode.FAIL);
        }

        CacheUtil.set(key, smsValidCode, CacheType.SMS_VALID_CODE.expiredTime);
        renderSuccessJson();
    }

    /**
     * 
     * 新用户注册.
     * 
     * @since 0.1
     * @author youblade
     * @created 2014年7月12日 下午6:08:17
     */
    public static void doRegist(@Required User user, Long inviteUserId) {
        if (user != null) {
            validation.required(user.nick);
            validation.required(user.password);
            validation.required(user.email);
            validation.required(user.qq);
            validation.required(user.mobile);
            validation.required(user.type);
        }
        handleWrongInput(false);
        String sessioninviteUserId=session.get("inviteUserId");
        if(sessioninviteUserId!=null){
        	user.inviteUserId=Long.parseLong(sessioninviteUserId);
        }
        user.create();
        
        // 保存成功的用户才发送注册邮件
        if (StringUtils.isNotBlank(user.activeCode)) {
            Mails.validRegist(user);
        }

        // 进入下一步:激活账号页面
        forwardRegist(user);
    }
    
    /**
     * 
     * 重新发送邮箱激活链接.
     *
     * @param uid
     * @since  v0.1
     * @author playlaugh
     * @created 2014年8月2日 上午11:59:50
     */
    public static void resendRegistMail(@Required String email) {
        handleWrongInput(false);

        User user = User.findByMail(email);

        // 禁止非注册用户、非待激活用户的访问
        if (user == null || user.status != UserStatus.INACTIVE) {
            handleIllegalRequest(ReturnCode.OP_BIZ_LIMIT);
        }

        Mails.validRegist(user);
        renderSuccessJson();
    }
    
    /**
     * 
     * 点击邮箱链接，激活账户.
     *
     * @param activeCode
     * @since  v0.1
     * @author playlaugh & youblade
     * @created 2014年8月2日 上午11:59:41
     */
    public static void confirmRegistMail(@Required String activeCode) {
        handleWrongInput(false);

        User user = User.findByField(User.FIELD_ACTIVE_CODE, activeCode);
        if (user == null) {
            forbidden("该激活链接已失效.");
        }
        // 仅当用户处于“未激活”时才更新其状态
        if (user.status == UserStatus.INACTIVE) {
        	//仅当开通新用户注册成功送会员活动时才执行
        	if(BizConstants.IS_GIVE_MEMBER) {
    			if (user.type == UserType.BUYER) {
            		MemberChargeRecord.giveMemberIngot(user.id);
            		User.instance(user.id).status(UserStatus.VALID).regitstTime(DateTime.now()).save(false);
            	}else if(user.type==UserType.SELLER){
            		MemberChargeRecord r = new MemberChargeRecord();
            		LocalDate endDate = new LocalDate(DateTime.now()).plusMonths(1);
        			User.instance(user.id).status(UserStatus.VALID).regitstTime(DateTime.now()).dueTime(endDate).save(false);
        			MemberChargeRecord r1 = MemberChargeRecord.newInstance(user.id, 1).userType(user.type).amount(0);
        			r1.memo = "用户激活邮箱，赠送1个月会员";
        			r1.month = 1;
        			r1.insert(r1);
            	}
        	}else {
        		User.instance(user.id).status(UserStatus.VALID).regitstTime(DateTime.now()).save(false);
        	}
            session.put(Secure.FIELD_AUTH, user.id);
            User.updateActiveCode(user.id);
            UserCenter.index();
        }else {
        	UserCenter.index();
        }
    }


    /**
     * 
     * 用户登录.
     * 
     * @since 0.1
     * @author youblade
     * @created 2014年7月12日 下午6:08:17
     */
    public static void doLogin(@Required String nick, @Required String pass) {
        handleWrongInput(false);

        User user = User.findByNick(nick);
        //用户名或密码错误 重新登陆
        if (user == null || !user.validate(pass)) {
            validation.keep();
            params.flash();
            flash.put(BizConstants.MSG, "用户名或密码错误");
            Application.login();
        }

        // 邮箱未激活则跳转到提示页面
        if (user.status == UserStatus.INACTIVE) {
            forwardRegist(user);
        }

        if(user.status==UserStatus.LOCKED){
            validation.keep();
            params.flash();
            flash.put(BizConstants.MSG, "账号被冻结,有疑问请联系网站客服");
            Application.login(); 
        }
        // 登录成功
        session.put(Secure.FIELD_AUTH, user.id);
        
        // 设置用户未完成的任务
        setUserTodoTask(user);
        
        if (user.type == UserType.SELLER) {
            TaskCenter.sellerTasks();
        }
        TaskCenter.buyerTasks();
    }
    
    /*
     * 设置用户待做任务
     */
    private static void setUserTodoTask(User user) {
        // 获取买手未完成的任务并设置标记
        BuyerTask task = BuyerTask.findTodoTask(user.id);
        if (task != null) {
            session.put(BizConstants.BUYER_TASK_ID, task.id);
            session.put(BizConstants.BUYER_TASK_STEP, task.nextStep);
            return;
        }
        // 清除标记
        session.remove(BizConstants.BUYER_TASK_ID, BizConstants.BUYER_TASK_STEP);
    }
    
    public static void logout() {
        session.remove("authcode");
        session.remove("buyerTaskId");
        session.remove("buyerTaskStep");
        session.remove("captcha");
        redirect("/");
    }


	/**
	 * 
	 * 找回登录密码.
	 * 
	 * @param user
	 * @param pass
	 * @since 0.1
	 * @author youblade
	 * @created 2014年7月14日 下午6:40:28
	 */
	public static void doFindPass(@Required String email, @Required String validCode) {
		handleWrongInput(false);

		// 校验验证码
		validCode = StringUtils.lowerCase(validCode);
		if (!validCode.equals(StringUtils.lowerCase(session.get("captcha")))) {
			flash(BizConstants.MSG, "验证码错误");
			Application.findPass();
		}

		User user = User.findByMail(email);
		if (user == null) {
			flash(BizConstants.MSG, "该邮箱没有注册过");
			Application.findPass();
		}

		// 更新激活字段
		User u = User.instance(user.id);
		u.activeCode = Codec.UUID().replace('-', '7');
		u.activeCodeCreateTime = DateTime.now().toDate();
		u.save(false);

		// 发送找回密码邮件
		user.activeCode = u.activeCode;
		Mails.resetPassword(user);

		// 转到重设密码页面
		forwardResetpass(user);
	}
    
    /**
     * 
     * TODO 验证码.
     *
     * @since  v0.1
     * @author playlaugh
     * @created 2014年8月2日 下午10:43:00
     */

	public static void captcha() {
		Images.Captcha captcha=Images.captcha(); 
		captcha.addNoise();
		String code=captcha.getText();
		session.put("captcha", code);
		captcha.getText("#FF6347");
		renderBinary(captcha);
		
	}
	
	/**
	 * 
	 * 重设密码
	 *
	 * @param validCode
	 * @since  v0.1
	 * @author moloch & youblade
	 * @created 2014-8-20 上午10:59:12
	 */
	public static void resetPass(@Required String activeCode) {
		handleWrongInput(false);

		User user = User.findByField(User.FIELD_ACTIVE_CODE, activeCode);
		if (user == null) {
			forbidden("该链接已失效");
		}

		// 判断链接是否失效 30分钟有效期
		if (new DateTime(user.activeCodeCreateTime).plusMinutes(30).isBeforeNow()) {
			flash(BizConstants.MSG, "您的密码修改链接已经失效");
			Application.login();
		}

		session.put(Secure.FIELD_AUTH, user.id);
		UserCenter.resetPass();
	}

}