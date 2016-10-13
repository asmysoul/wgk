package controllers;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import models.BuyerAccount;
import models.BuyerAccount2;
import models.BuyerConfig;
import models.BuyerDepositRecord;
import models.BuyerExperienceRecord;
import models.BuyerTask;
import models.BuyerTaskStep;
import models.FundAccount;
import models.MemberChargeRecord;
import models.Notice;
import models.SysConfig;
import models.TaskItemSearchPlan;
import models.TaskOrderMessage;
import models.UserIngotRecord;
import models.Notice.Role;
import models.PayTradeLog;
import models.Region;
import models.SellerConfig;
import models.Shop;
import models.Task;
import models.User;
import models.SysConfig.SysConfigKey;
import models.User.UserStatus;
import models.User.UserType;
import models.User.VipStatus;
import models.UserWithdrawRecord;
import models.mappers.BuyerAccountMapper;
import models.mappers.BuyerExperienceRecordMapper;
import models.mappers.BuyerTaskMapper;
import models.mappers.ShopMapper;
import models.mappers.TaskMapper;
import models.mappers.UserIngotRecordMapper;
import models.mappers.UserMapper;
import models.mappers.fund.BuyerDepositRecordMapper;
import models.marketing.TaskRewardLog;
import models.marketing.UserInvitedRecord;
import notifiers.Mails;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.BooleanUtils;
import org.apache.commons.lang.Validate;
import org.apache.ibatis.session.SqlSession;
import org.joda.time.DateTime;
import org.joda.time.Days;
import org.joda.time.LocalDate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import play.data.validation.Min;
import play.data.validation.Required;
import play.libs.Codec;
import play.libs.Images;
import play.mvc.With;
import play.mvc.Http.Header;
import vos.BuyerAccountSearchVo;
import vos.BuyerAccountSearchVo.ExamineStatus;
import vos.BuyerAccountSearchVo2.ExamineStatus2;
import vos.TaskSearchVo.SearchModule;
import vos.BuyerAccountSearchVo2;
import vos.BuyerTaskSearchVo;
import vos.BuyerTaskStepVo;
import vos.BuyerTaskVo;
import vos.Page;
import vos.PersonalInfoVo;
import vos.TaskRewardLogVo;
import vos.TaskSearchVo;
import vos.UserSearchVo;

import com.aton.base.BaseController;
import com.aton.base.secure.Secure;
import com.aton.base.secure.Secure2;
import com.aton.config.BizConstants;
import com.aton.config.CacheType;
import com.aton.config.Config;
import com.aton.config.ReturnCode;
import com.aton.db.SessionFactory;
import com.aton.util.CacheUtil;
import com.aton.util.JsonUtil;
import com.aton.util.MixHelper;
import com.aton.util.NumberUtils;
import com.aton.util.QnCloudUtil;
import com.aton.util.SmsUtil;
import com.aton.util.StringUtils;
import com.aton.vo.AjaxResult;
import com.google.common.base.Strings;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.net.HttpHeaders;

import controllers.MoneyManage.WithdrawSearchVo;
import domain.BuyerTaskProcessor;
import domain.TaskStats;
import domain.UserMoneyStats;
import enums.BuyerTaskStepType;
import enums.Device;
import enums.Platform;
import enums.Platform2;
import enums.TaskListType;
import enums.TaskStatus;
import enums.TaskType;
import enums.pay.KQpayPlatform;
import enums.pay.PayPlatform;
import enums.pay.TradeType;


public class UserCenterApp extends BaseController {

	private static final Logger log = LoggerFactory
			.getLogger(UserCenterApp.class);

	public static void result(String title, String msg) {
		renderArgs.put("errTitle", title);
		renderArgs.put("errMsg", msg);
		render("result.html");
	}
	
	public static void doLoginApp(@Required String nick, @Required String pass) {
		//handleWrongInput(false);

		Map<String, Object> map = new HashMap<String, Object>();
		User user = User.findByNick(nick);
		//System.out.println(user.toString());
		// System.out.println(user);
		// 400：用户名或密码错误
		if (user == null || !user.validate(pass)) {
			map.put("result", 400);
			// validation.keep();
			// params.flash();
			// flash.put(BizConstants.MSG, "用户名或密码错误");
			// Application.login();
		} else {
			// 200：登陆成功
			map.put("result", 200);
			map.put("authcode", user.id);
			map.put("user", user);
			session.put(Secure.FIELD_AUTH, user.id);
		}
		
		renderJson(map);
	}
		
	
	/**
	 * 
	 * @param newPass  新密码
	 * @param oldPass  旧密码
	 * @param nick     买手昵称
	 */

	public static void modifyUserPwd(String newPass, String oldPass,String nick) {
		// TODO Auto-generated method stub
		User u = new User();
		u.password =newPass;
		renderJson(modifyUser(u,oldPass,nick));
	}
	
	
	/**
     * 
     * 修改个人信息
     *
     * @param user
     * @param oldPass
     * @since  v0.1
     * @author moloch
     * @created 2014-8-1 下午4:44:31
     */
	public static String modifyUser(@Required User user, String oldPass,String nick) {
		//handleWrongInput(false);

		User u = User.findByNick(nick);

		/*
		 *  修改登录密码时，检查填写的旧密码是否正确
		 */
		if (StringUtils.isNotBlank(oldPass) && !u.validate(oldPass)) {
			renderFailedJson(ReturnCode.WRONG_INPUT, "您输入的旧密码错误，请重新输入");
		}

		if (user.password != null) {
			// 登录后到“个人中心”修改密码
			if (oldPass != null) {
				User.updatePass(u.id, user.password);
				return "success";
			}

			// “找回密码”邮件中的连接进入“密码修改页面”重设密码
			User.updatePass(u.id, user.password);
			flash(BizConstants.MSG, "请用新密码登陆");
			Application.login();
		}

		/*
		 *  修改密码以外字段
		 */
		user.save(false);
		return "-1";
	}
	
	public static void modifyPayPwd(String payPassword,String nick){
		User u = User.findByNick(nick);
		User user = new User();
		user.id = u.id;
		user.payPassword = payPassword;
		renderJson(modifyPay(user));
	}
	
	/**
     * 
     * 添加支付密码
     *
     * @param user
     * @param oldPass
     * @since  v0.1
     * @author moloch
     * @created 2014-8-1 下午4:44:31
     */
	public static String modifyPay(@Required User user){
		user.save(false);
		return "success";
	}
	
	public static void modifyQQDemo(String qq,String nick){
		User u = User.findByNick(nick);
		User user = new User();
		user.id = u.id;
		user.qq = qq;
		renderJson(modifyPay(user));
	}
	
	
	//修改qq
	public static String modifyQQ(@Required User user){
		user.save(false);
		return "success";
	}
	
	
	
	public static void modifyPhoneDemo(String phone,String nick){
		User u = User.findByNick(nick);
		User user = new User();
		user.id = u.id;
		user.mobile = phone;
		renderJson(modifyPay(user));
	}
	
	
	//修改手机
	public static String modifyPhone(@Required User user){
		user.save(false);
		return "success";
	}
	
	
	
	/**
     * 
     * 验证用户密码
     *
     * @since  v0.1
     * @author youblade
     * @created 2014年7月11日 下午2:42:44
     */
    public static void validate(@Required String inputPass,@Required String nick){
    	User u = User.findByNick(nick);
    	renderJson(u.password.equals(Codec.hexMD5(inputPass + u.salt)));
    }
    
	
    public static void checkPwd(String inputPass,String nick){
    	User u = User.findByNick(nick);
    	renderJson(Codec.hexMD5(inputPass + u.salt)+"");
    }
	
	
	
	

	/**
	 * 
	 * 邀请好友->获取已邀请到的好友列表
	 *
	 * @since  v2.0
	 * @author youblade
	 * @created 2014年12月20日 上午11:07:47
	 */
	public static void listInvitedUsers(@Required UserSearchVo vo,String nick) {
	    //handleWrongInput(false);
	    vo.nick = StringUtils.trimToNull(vo.nick);
	    vo.email = StringUtils.trimToNull(vo.email);
	    vo.qq = StringUtils.trimToNull(vo.qq);
	    User u = User.findByNick(nick);
	    vo.inviteNick = u.nick;
	    Page<UserInvitedRecord> page = UserInvitedRecord.findByPage(vo);
        renderPageJson(page.items,page.totalCount);
	}
	
	

	/**
	 * 
	 * TODO 验证码.
	 * 
	 * @since v0.1
	 * @author playlaugh
	 * @created 2014年8月2日 下午10:43:00
	 */

	public static void captcha() {
		Images.Captcha captcha = Images.captcha();
		String code = captcha.getText();
		session.put("captcha", code);
		renderJson(code);

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
	public static void doFindPass(@Required String email) {
		//handleWrongInput(false);

		
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
		renderJson("success");
	}
	
	
	
	
	/**
	 * 新用户注册.接口
	 * @param nick
	 * @param password
	 * @param email
	 * @param qq
	 * @param mobile
	 */
	public static void doRegistDemo(@Required String nick,@Required String password,@Required String email,@Required String qq,@Required String mobile) {
		User user = new User(nick,password,UserType.BUYER,qq,email,mobile);
		renderJson(doRegist(user,(long)0));
	}
    
	/**
	 * 
	 * 新用户注册.
	 * 
	 * @since 0.1
	 * @author youblade
	 * @created 2014年7月12日 下午6:08:17
	 */
	public static String doRegist(@Required User user, Long inviteUserId) {
		if (user != null) {
			validation.required(user.nick);
			validation.required(user.password);
			validation.required(user.email);
			validation.required(user.qq);
			validation.required(user.mobile);
			validation.required(user.type);
		}

		//handleWrongInput(false);

		String sessioninviteUserId = session.get("inviteUserId");
		if (sessioninviteUserId != null) {
			user.inviteUserId = Long.parseLong(sessioninviteUserId);
		}
		user.create();

		// 保存成功的用户才发送注册邮件
		if (StringUtils.isNotBlank(user.activeCode)) {
			Mails.validRegist(user);
		}

		// 进入下一步:激活账号页面
		return "success";
	}
	
	
	/**
	 * 
	 * 重新发送邮箱激活链接.
	 * 
	 * @param uid
	 * @since v0.1
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
		renderJson("success");
	}
	
    /**
     * 
     * 用户是否已存在（用于判断是否注册过）.
     *
     * @since  0.1
     * @author youblade
     * @created 2014年8月4日 下午9:54:34
     */
    public static void isUserExists(String field, String value) {
        User user = findByField(field, value);
        if((user != null) && (user.id > 0)){
        	renderJson("true");
        }else{
        	renderJson("false");
        }
    }

    /**
     * 
     * 按照某个字段（属性）获取用户.
     *
     * @param field
     * @param value
     * @return
     * @since  0.1
     * @author youblade
     * @created 2014年8月5日 上午11:11:22
     */
    public static User findByField(String field, Object value) {
        SqlSession ss = SessionFactory.getSqlSession();
        try {
            UserMapper mapper = ss.getMapper(UserMapper.class);
            return mapper.selectByField(field, value);
        } finally {
            ss.close();
        }
    }
    /**
     * 
     * @param nick 用户名
     * @param mobile 手机号
     */
	public static void sendSmsValidCodeDeomo(String nick,String mobile){
		User user = new User();
		user.nick = nick;
		user.mobile = mobile;
		renderJson(sendSmsValidCode(user));
	}
	/**
	 * 
	 * 发送短信验证码. 验证码有效期为60s，期间限制再次发送
	 * 
	 * @param user
	 * @since 0.1
	 * @author youblade
	 * @created 2014年9月27日 下午1:56:10
	 */
	public static Map sendSmsValidCode(@Required User user) {
		Map<String, Object> map = new HashMap<String, Object>();
		if (user != null) {
			validation.required("user.nick", user.nick);
			validation.required("user.mobile", user.mobile);
		}
		//handleWrongInput(false);

		// 检查手机号
		boolean isExists = User.isUserExists(User.FIELD_MOBILE, user.mobile);
		if (isExists) {
			map.put("code", String.valueOf(ReturnCode.OP_BIZ_LIMIT));
			return map;
		}

		/*
		 * TODO 需要进一步检查是否真实用户，避免被滥用
		 */
		String referer = StringUtils.EMPTY;
		Header header = request.headers.get(HttpHeaders.REFERER);
		if (header != null) {
			referer = header.value();
		}
		log.info("User={} send validCode to {},referer:{}", new Object[] {
				user.nick, user.mobile, referer });

		// 若上次发送的尚未过期，则限制再次发送，避免高频滥发
		String key = CacheType.SMS_VALID_CODE.getKey(user.nick);
		Object object = CacheUtil.get(key);
		if (object != null) {
			log.info("User={} has smsValidCode exists,{}", user.nick,
					object.toString());
			log.warn(
					"User={},mobile={} from ip={} send smsValidCode too fast!",
					new Object[] { user.nick, user.mobile,
							request.remoteAddress });
			map.put("result", "success");
			return map;
		}

		// 生成6位随机数字并缓存
		String smsValidCode = String.valueOf(Math.random()).substring(2, 8);
		String smsContent = MixHelper.format("您的验证码为[{}]，本验证码1分钟内有效，感谢您使用.",
				smsValidCode);
		log.info("User={},mobile={},sms={}", new Object[] { user.nick,
				user.mobile, smsContent });

		boolean isSuccess = SmsUtil.send(user.mobile, smsContent);
		if (!isSuccess) {
			log.error("User={} send smsValidCode failed");
			map.put("code", String.valueOf(ReturnCode.FAIL));
		}

		CacheUtil.set(key, smsValidCode, CacheType.SMS_VALID_CODE.expiredTime);
		map.put("result", "success");
		map.put("smsValidCode", smsValidCode);
		return map;
	}
	
	
	
	
	
	
	
	//--------------------------------------------------------绑定买号----------------------------------------------------------------------------
	//--------------------------------------------------------绑定买号----------------------------------------------------------------------------
	//--------------------------------------------------------绑定买号----------------------------------------------------------------------------
	//--------------------------------------------------------绑定买号----------------------------------------------------------------------------
	//--------------------------------------------------------绑定买号----------------------------------------------------------------------------
	//--------------------------------------------------------绑定买号----------------------------------------------------------------------------
	/*
	 * 保存买号  app  调用接口
	 * 
	 */
	
	public static void addBuyerAccount(String platform, String nick ,String mobile, String consignee ,String state ,String city,String region,String address,String buyer){
		BuyerAccount account = new BuyerAccount();
		account.platform = Platform.valueOf(platform);
		account.nick = nick;
		account.mobile = mobile;
		account.consignee = consignee;
		account.state = state;
		account.city = city;
		account.region = region;
		account.address = address;
		renderJson(saveBuyerAccount(account,buyer));
	}
	public static void addBuyerAccount2(String platform,long id,String nick ,String mobile, String consignee ,String state ,String city,String region,String address,String buyer){

		BuyerAccount account = new BuyerAccount();
		account.platform = Platform.valueOf(platform);
		account.id = id;
		account.nick = nick;
		account.mobile = mobile;
		account.consignee = consignee;
		account.state = state;
		account.city = city;
		account.region = region;
		account.address = address;
		renderJson(saveBuyerAccount(account,buyer));
	}
	
	
	
	
	
	/**
	 * 
	 * 用户中心->买手账号->保存买号.
	 * 
	 * @since 0.1
	 * @author youblade
	 * @throws InterruptedException 
	 * @created 2014年8月1日 下午12:52:12
	 */
	public synchronized static String saveBuyerAccount(@Required BuyerAccount account , String nick){
		
	    validation.required("platform", account.platform);
	
	    // 新添加买号时再检查，前端仅传参有改动的字段
        if (account != null && account.id <= 0) {
            validation.required("nick", account.nick);
            validation.required("mobile", account.mobile);
            validation.required("consignee", account.consignee);
            validation.required("state", account.state);
            validation.required("city", account.city);
            validation.required("address", account.address);
        }
		//handleWrongInput(false);

		// 将无关信息过滤为null
		account.nick = StringUtils.trimToNull(account.nick);
		account.mobile = StringUtils.trimToNull(account.mobile);
		account.consignee = StringUtils.trimToNull(account.consignee);
		account.state = StringUtils.trimToNull(account.state);
		account.city = StringUtils.trimToNull(account.city);
		// 三级行政区域有可能为空，将非空的修改为空值时不能将其设置为null，否则无法构造SQL条件
		account.region = StringUtils.trimToEmpty(account.region);
		account.address = StringUtils.trimToNull(account.address);
		
		//User buyer = getCurrentUser();
		User buyer = User.findByNick(nick);
		
        //checkBeforeSaveBuyerAccount(account, buyer);
        
		// 买号改动需要审核
        
		account.status = ExamineStatus.WAIT_EXAMINE;
		account.userId = buyer.id;
		account.save();
		
		return "OK";
	}
//	/**
//	 * 
//	 * 用户中心->买手账号->修改买号.
//	 * 
//	 * @since 0.1
//	 * @author youblade
//	 * @throws InterruptedException 
//	 * @created 2014年8月1日 下午12:52:12
//	 */
//	public synchronized static String saveBuyerAccount2(@Required BuyerAccount account , String nick){
//		
//		validation.required("platform", account.platform);
//	    
//	    // 新添加买号时再检查，前端仅传参有改动的字段
//        if (account != null && account.id <= 0) {
//            validation.required("nick", account.nick);
//            validation.required("mobile", account.mobile);
//            validation.required("consignee", account.consignee);
//            validation.required("state", account.state);
//            validation.required("city", account.city);
//            validation.required("address", account.address);
//        }
//		//handleWrongInput(false);
//
//		// 将无关信息过滤为null
//		account.nick = StringUtils.trimToNull(account.nick);
//		account.mobile = StringUtils.trimToNull(account.mobile);
//		account.consignee = StringUtils.trimToNull(account.consignee);
//		account.state = StringUtils.trimToNull(account.state);
//		account.city = StringUtils.trimToNull(account.city);
//		// 三级行政区域有可能为空，将非空的修改为空值时不能将其设置为null，否则无法构造SQL条件
//		account.region = StringUtils.trimToEmpty(account.region);
//		account.address = StringUtils.trimToNull(account.address);
//		
//		User buyer = User.findByNick(nick);
//       // checkBeforeSaveBuyerAccount2(account, buyer);
//		
//		// 买号改动需要审核
//		account.status = ExamineStatus.WAIT_EXAMINE;
//		account.userId = buyer.id;
//		account.save();
//		return "OK";
//	}
//	
	
	
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
	public static void listBuyerAccounts(Platform platform,String nick, boolean receive) {
		//handleWrongInput(false);
		
		// 淘宝、天猫数据目前不分开
		if (platform == Platform.TMALL) {
			platform = Platform.TAOBAO;
		}
		User user = User.findByNick(nick);
        BuyerAccountSearchVo vo = BuyerAccountSearchVo.newInstance().platform(platform).userId(user.id);
		
		// 接手任务时仅返回可供使用的买号（每个买号每天限制5个任务）
        if (receive) {
            vo.status(ExamineStatus.EXAMINED);
            renderJson(BuyerAccount.findForTakingTask(vo));
        }

		List<BuyerAccount> list = BuyerAccount.findList(vo);
		//封掉的买号不显示出来，只显示2个
//		if(list.size()==3)
//		for (BuyerAccount buyerAccount : list) {
//			if(buyerAccount.status==ExamineStatus.NOT_PASS){
//				list.remove(buyerAccount);
//				break;
//			}
//		}
		renderJson(list);
	}
	//--------------------------------------------------------绑定买号----------------------------------------------------------------------------
		//--------------------------------------------------------绑定买号----------------------------------------------------------------------------
		//--------------------------------------------------------绑定买号----------------------------------------------------------------------------
		//--------------------------------------------------------绑定买号----------------------------------------------------------------------------
		//--------------------------------------------------------绑定买号----------------------------------------------------------------------------
		//--------------------------------------------------------绑定买号----------------------------------------------------------------------------
		//WAIT_SEND_GOODS--------------------------------------------------------绑定买号----------------------------------------------------------------------------	
	// >>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>我的任务<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<
		// >>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>我的任务<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<
		// >>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>我的任务<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<
		// >>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>我的任务<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<
	
	
	public synchronized static Map REFUNDING(String status,String nick){
		TaskSearchVo vo = new TaskSearchVo();
		vo.pageNo=1;
		vo.pageSize=1000;
		vo.status=TaskStatus.valueOf(status);
		return listBuyerTasks(vo,null,nick);
	}
	
	
	
	
	/**
		 * 
		 * 获取未完成任务
		 * 
		 * @since 0.1
		 * @author youblade
		 * @created 2014年8月2日 下午6:00:15
		 */
	
		// >>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>我的任务<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<
		// >>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>我的任务<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<
		// >>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>我的任务<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<
		// >>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>我的任务<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<
	
	
	public static Task getTask(long taskId) {
		// 父任务数据异常
		
		return Task.findById("id,platform,status,base_order_ingot,extra_reward_ingot,pc_order_num,mobile_order_num,pc_taken_count,mobile_taken_count,"
				+ "finished_count,seller_id,create_time,shop_id,item_id,sys_refund,"
				+ "publish_timer_interval,publish_timer_value,last_batch_publish_time,good_comment_img", taskId);
	}
	
	
	public static final Logger buyerTaskStepLog = LoggerFactory.getLogger("taskstep");
	
	private static long getCurrentBuyerTaskId() {
		return NumberUtils.toLong(session.get(BizConstants.BUYER_TASK_ID));
	}
	/**
	 * 
	 * 买号和商品之间的限制
	 * 
	 * @param 旺旺号
	 * @param 商品id
	 * @return
	 * @since v2.0
	 * @author fufei
	 * @created 2015年1月13日 下午3:47:45
	 */
	public static boolean isAvailableBuyerAccountAndItem(long buyerAccountId, String itemId) {
		Validate.notNull(buyerAccountId);
		Validate.notEmpty(itemId);
		if (CollectionUtils.isNotEmpty(TaskStats.selectByBuyerAccountIdAndItemIdAndPeriod(buyerAccountId, itemId))) {
			return false;
		}
		return true;
	}
	
	/**
	 * 
	 * 账号和商家之间的限制
	 * 
	 * @param buyerId
	 * @param shopId
	 * @return
	 * @since v2.0
	 * @author fufei
	 * @created 2015年1月13日 下午4:52:28
	 */
	public static boolean isAvailableBuyerIdAndSellerId(long buyerId, long sellerId) {
		if(getCurrentUser().vipStatus==VipStatus.SPECIAL){
			return true;
		}
		List<BuyerTaskVo> buyerSellerTasks = TaskStats.findBuyerIdAndSellerId(buyerId, sellerId);
		if (CollectionUtils.isNotEmpty(buyerSellerTasks)) {
			return false;
		}
		return true;
	}
	
	/**
	 * 
	 * 买号和店铺之间的限制
	 * 
	 * @return
	 * @since v2.0
	 * @author fufei
	 * @created 2015年1月13日 下午2:45:34
	 */
	public static boolean isValidateBuyerAccountAndShopId(long buyerAccountId, Long shopId) {
		Validate.notNull(buyerAccountId);
		Validate.notNull(shopId);
		// 取【最近15天】【该买号】在【该商家】的（非取消）任务记录
		List<BuyerTaskVo> buyerTasks = TaskStats.findBuyerAccountIdAndShopId(buyerAccountId, shopId);
		
		if(CollectionUtils.isNotEmpty(buyerTasks)){
			return false;
		}
		return true;
	}
	
	
	//------------------------------------------------------做任务-------------------------------------------------------------------------------
	//------------------------------------------------------做任务------------------------------------------------------------------------------
	//------------------------------------------------------做任务---------------------------------------------------------------------------------
	//------------------------------------------------------做任务---------------------------------------------------------------------------------
	//------------------------------------------------------做任务-------------------------------------------------------------------------------
	/*
	 * 
	 * taskid 任务id
	 * buyerAccountId  买号
	 * buyer 买号昵称
	 * 
	 */
	public static void doTask(long taskId, long buyerAccountId ,String buyer ,String nick){
		BuyerTask bt = new BuyerTask();
		bt.taskId = taskId;
		bt.buyerAccountId = buyerAccountId;
		bt.buyerAccountNick = buyer;
		bt.device = Device.valueOf("MOBILE");
		renderJson(take(bt,nick));
	}
	public synchronized static long take(@Required BuyerTask bt ,String nick) {

		if (bt != null) {
			validation.required(bt.taskId);
			validation.required(bt.device);
			validation.required(bt.buyerAccountId);
			validation.required(bt.buyerAccountNick);
		}
		//handleWrongInput(false);

		/*
		 * 检查是否可接手任务
		 */
		// 存在未完成的任务
		if (session.contains(BizConstants.BUYER_TASK_ID)) {
			if (BuyerTask.findById(Long.parseLong(session.get(BizConstants.BUYER_TASK_ID))).status == TaskStatus.CANCLED) {
				session.remove(BizConstants.BUYER_TASK_ID);
			} else {
				renderFailedJson(ReturnCode.TASK_UNFINISHED, session.get(BizConstants.BUYER_TASK_ID));
			}
		}

		User buyer = User.findByNick(nick);

		BuyerAccount buyerAccount = BuyerAccount.findById(bt.buyerAccountId);
		if (!BuyerAccount.validate(buyerAccount, buyer)) {
			log.error("买手={}违规注册多个账号，本次使用买号={}不属于当前登录用户！", buyer.nick, bt.buyerAccountNick);
			renderFailedJson(ReturnCode.BIZ_LIMIT, "当前登录用户状态异常，请退出后再登录~~");
		}
	
		// 该任务已被用户接手过，不能重复领取
		if (BuyerTask.isDuplicateTaking(bt.buyerAccountId, bt.taskId)) {
			renderFailedJson(ReturnCode.BIZ_LIMIT, "你已经领取过该任务了，换一个吧~~");
		}
		// 存在尚未完成的任务，不能领取新任务
		BuyerTask todoTask = BuyerTask.findTodoTask(buyer.id);
		if (todoTask != null) {
			renderFailedJson(ReturnCode.TASK_UNFINISHED, todoTask.id);
		}

		// 父任务数据异常
		Task task = getTask(bt.taskId);
		
		if (task == null || task.status != TaskStatus.PUBLISHED) {
			renderFailedJson(ReturnCode.WRONG_INPUT);
		}
		
		
		// 领取非“平台返款”的任务需要绑定【财付通】作为退款账号
		if (BooleanUtils.isFalse(task.sysRefund)) {
			FundAccount tenpayAccount = FundAccount.findByType(PayPlatform.TENPAY, buyer.id);
			if (tenpayAccount == null) {
				renderFailedJson(ReturnCode.OP_BIZ_LIMIT);
			}
		}

		// 任务已全被领取完
		if (task.isTakenOver(bt.device)) {
			renderFailedJson(ReturnCode.TASK_TAKE_OVER);
		}

		// 平台限制每个【买号】5单/天，30单/周，90单/月
		TaskStats taskStats = TaskStats.findForBuyerUntilNow(bt.buyerAccountId);
		
		String day=SysConfig.getConfigValue(SysConfigKey.BUYER_TASK_DAY_COUNT);
        if(StringUtils.isEmpty(day)){
            day=Config.BUYER_TASK_DAY_COUNT;
        }
        
        String week=SysConfig.getConfigValue(SysConfigKey.BUYER_TASK_WEEK_COUNT);
        if(StringUtils.isEmpty(week)){
            week=Config.BUYER_TASK_WEEK_COUNT;
        }
        
        String mouth=SysConfig.getConfigValue(SysConfigKey.BUYER_TASK_MONTH_COUNT);
        if(StringUtils.isEmpty(mouth)){
            mouth=Config.BUYER_TASK_MONTH_COUNT;
        }
		
		if (taskStats.dayCount >= Integer.parseInt(day)) {
			log.warn("BuyerAccount={} has taken {} tasks Today", bt.buyerAccountId, taskStats.dayCount);
			renderFailedJson(ReturnCode.BIZ_LIMIT, "该买号今日可领取任务已达上限");
		}
		if (taskStats.weekCount >= Integer.parseInt(week)) {
			log.warn("BuyerAccount={} has taken {} tasks this Week", bt.buyerAccountId, taskStats.weekCount);
			renderFailedJson(ReturnCode.BIZ_LIMIT, "该买号本周可领取任务已达上限");
		}
		if (taskStats.monthCount >= Integer.parseInt(mouth)) {
			log.warn("BuyerAccount={} has taken {} tasks this Month", bt.buyerAccountId, taskStats.monthCount);
			renderFailedJson(ReturnCode.BIZ_LIMIT, "该买号本月可领取任务已达上限");
		}

		bt.buyerId = buyer.id;
		
		
		// 买号和店铺之间的限制
		if (!isValidateBuyerAccountAndShopId(bt.buyerAccountId, task.shopId)) {
			renderFailedJson(ReturnCode.BIZ_LIMIT, "旺旺号在该店铺已经领取过任务");

		}
		// 买号和商品之间的限制
		if (!isAvailableBuyerAccountAndItem(bt.buyerAccountId, task.itemId)) {
			renderFailedJson(ReturnCode.BIZ_LIMIT, "该旺旺号已经领取过该任务");
		}
		
		boolean result=Task.isTaoBaoAndTmall(bt.taskId);//除淘宝、天猫外的其他平台
		Object[] args = new Object[] { buyer.id, buyer.nick, bt.taskId };
		buyerTaskStepLog.info("Buyer={}-{} take Task={}", args);
		
		/*
		 * 创建买手任务记录
		 */
		bt.sellerId = task.sellerId;
		bt.pledgeIngot = BizConstants.TASK_TAKING_INGOT * 100L;
		bt.rewardIngot = task.calculateRewardIngot(bt.device);
		//评论上传图片，给买手0.5个金币
        if(StringUtils.isNotEmpty(task.goodCommentImg)){
            bt.rewardIngot+=50;
        }
		// 写入任务的经验值
		User seller = User.findByIdWichCache(task.sellerId);
		bt.experience = task.getListType(seller.registTime).experience;
		if(result){
			bt.status=TaskStatus.WAIT_PAY;
		}else {
			bt.status = TaskStatus.RECIEVED;
		}
		//关键词对应已接单数加1
		TaskItemSearchPlan p=TaskItemSearchPlan.getOnePlan(bt.taskId);
		if(p!=null&&p.takenNum<p.totalNum){
			p.takenNum+=1;
			TaskItemSearchPlan.update(p);
			bt.searchPlanId=p.id;
		}
		bt.create();
		
		long currentTaskId = getCurrentBuyerTaskId();

		// 若不存在未完成的任务，则设置当前访问的任务为“正在做”的任务
		if (currentTaskId <= 0) {
			currentTaskId = bt.id;
			session.put(BizConstants.BUYER_TASK_ID, currentTaskId);
			// 默认进行第一步：挑选商品
			if (result) {
				session.put(BizConstants.BUYER_TASK_STEP, BuyerTaskStepType.ORDER_AND_PAY);
			} else {
				session.put(BizConstants.BUYER_TASK_STEP, BuyerTaskStepType.CHOOSE_ITEM);
			}
		}

		// 返回ID以方便前端跳转到做任务的页面

		return bt.id;
	}
	/**
	 * 
	 * 进入买手做任务的页面.<br>
	 * 
	 * 使用session控制每次只能做一个任务，上次未完成的任务做完之前则不能做新任务。
	 * 
	 * @param id
	 *            BuyerTask.id
	 * @since 0.1
	 * @author youblade
	 * @created 2014年8月1日 下午12:17:27
	 */
	public static void perform(long taskId, long sontaskId, String nick) {
		BuyerTaskStepType str = BuyerTaskStepType.CHOOSE_ITEM;
		long currentTaskId = sontaskId;
		// 若不存在未完成的任务，则设置当前访问的任务为“正在做”的任务
		if (currentTaskId <= 0) {
			currentTaskId = taskId;
			session.put(BizConstants.BUYER_TASK_ID, currentTaskId);
			// 默认进行第一步：挑选商品
			BuyerTask buyerTask = BuyerTask.findById(currentTaskId);
			System.out.println(buyerTask);
			if (buyerTask != null && Task.isTaoBaoAndTmall(buyerTask.taskId)) {
				// session.put(BizConstants.BUYER_TASK_STEP,
				// BuyerTaskStepType.ORDER_AND_PAY);
				str = BuyerTaskStepType.ORDER_AND_PAY;
			} else {
				// session.put(BizConstants.BUYER_TASK_STEP,
				// BuyerTaskStepType.CHOOSE_ITEM);
				str = BuyerTaskStepType.CHOOSE_ITEM;
			}
		}

		// 设置当前买手任务要做的步骤：后端业务步骤
		BuyerTaskStepType currentTaskStep = BuyerTaskStepType.valueOf(str
				.toString());

		User buyer = User.findByNick(nick);
		BuyerTaskVo task = BuyerTask.findForPerforming(currentTaskId, buyer.id);
		notFoundIfNull(task);
		task.id = currentTaskId;
		Object[] args = new Object[] { buyer.id, buyer.nick,
				currentTaskStep.toString(), task.taskId, task.id, task.status };
		buyerTaskStepLog
				.info("Buyer={}-{} begin to perform step={} of buyerTask={}-{} with status={}",
						args);
		// 设置任务当前要做的步骤：前端页面上显示的小步骤
		int initTaskStepNo = 1;
		if (currentTaskStep == BuyerTaskStepType.VIEW_AND_INQUIRY) {
			initTaskStepNo = 2;
		}
		if (currentTaskStep == BuyerTaskStepType.ORDER_AND_PAY) {
			initTaskStepNo = 4;
		}
		HashMap<String, Object> map = new HashMap<String, Object>();
		map.put("currentTaskStep", currentTaskStep);
		map.put("initTaskStepNo", initTaskStepNo);
		map.put("taskInfo", task);
		map.put("taskPlatform", task.platform);
		map.put("orderMessage", TaskOrderMessage.getOneMessage(task.taskId));
		if (task.searchPlanId > 0) {
			map.put("searchPlan",
					TaskItemSearchPlan.findById(task.searchPlanId));
		} else {
			map.put("searchPlan", TaskItemSearchPlan.getOnePlanOld(task.taskId));
		}
		renderJson(map);
	}
	/**
	 * 
	 * 第二步： 通过商品链接对找到的商品进行核对.
	 * 
	 * @param itemUrl
	 * @since 0.1
	 * @author youblade
	 * @created 2014年8月13日 下午1:52:56
	 */
	public static void checkItem(@Required String itemUrl,long buyerAccountId,String nick) {
		validation.isTrue(StringUtils.isNotBlank(itemUrl));
		// 限制最大长度300个字符串
		validation.maxSize(itemUrl, 300);
		//handleWrongInput(false);
		// 获取买手当前正在做的任务
		long buyerTaskId = buyerAccountId;
		User buyer = User.findByNick(nick);
		long buyerId = buyer.id;
		BuyerTaskVo taskVo = BuyerTask.findVoById(buyerTaskId, buyerId);
		if (taskVo == null || taskVo.status != TaskStatus.WAIT_PAY) {
			Object[] args = new Object[] { buyerId, itemUrl, buyerTaskId };
			log.warn("User={} check item_url={} for buyerTask={} FAIL:[data error]", args);
			renderFailedJson(ReturnCode.FAIL);
		}

		// 先比较商品url是否完全一致
		if (taskVo.itemUrl.equals(itemUrl)) {
			renderJson("seccuss");
		}

		// 若url不完全一致，则从商品链接中提取商品ID与任务目标商品进行比较
		
		String inputItemId = Task.getItemIdByPlatform(taskVo.platform, itemUrl);
		
		
		if (Strings.isNullOrEmpty(inputItemId) || !inputItemId.equals(taskVo.itemId.toString())) {
			renderFailedJson(ReturnCode.FAIL);
		}
		renderJson("seccuss");
	}
	//第一步保存
	public static void firstStep2(long id, String urls, String nick,
			long buyerAccountId, long messageId) {
		BuyerTaskStepVo vo = new BuyerTaskStepVo();
		String[] sourceStrArray = urls.split(",");
		System.out.println(vo.itemUrls);
		for (int i = 0; i < sourceStrArray.length; i++) {
			
			vo.itemUrls.add(sourceStrArray[i]);
		}
		BuyerTaskStepType type = BuyerTaskStepType.valueOf("CHOOSE_ITEM");

		renderJson(saveStep(id, type, vo, nick, buyerAccountId, messageId));
	}
	//第三步保存
	public static void threeStep(long id , String url1,String nick ,long buyerAccountId,long messageId){
		BuyerTaskStepVo vo = new BuyerTaskStepVo();
		String[] sourceStrArray = url1.split(",");
		for (int i = 0; i < sourceStrArray.length; i++) {
			vo.itemUrls.add(sourceStrArray[i]);
		}
		System.out.println(sourceStrArray[4]);
		vo.picUrls.add(sourceStrArray[4]);
		BuyerTaskStepType type = BuyerTaskStepType.valueOf("VIEW_AND_INQUIRY"); 
		renderJson(saveStep(id, type, vo, nick,buyerAccountId,messageId));
	}
	
	//保存第五步
	public static void fifthStep(long id ,String url,String orderNo,String money,String nick ,long buyerAccountId,long messageId){
		
		BuyerTaskStepVo vo = new BuyerTaskStepVo();
		vo.picUrls.add(url);
		vo.orderNo=orderNo;
		vo.realPaidFee=money;
		BuyerTaskStepType type = BuyerTaskStepType.valueOf("ORDER_AND_PAY"); 
		renderJson(saveStep(id, type, vo, nick,buyerAccountId,messageId));
	}
	
	/**
	 * id 任务id后面那四位
	 * type CHOOSE_ITEM
	 * vo 所输入的url
	 */
	public synchronized static String saveStep(@Required @Min(1) long id, @Required BuyerTaskStepType type, @Required BuyerTaskStepVo vo, @Required String nick,long buyerAccountId,long messageId) {
		//handleWrongInput(false);
		BuyerTask buyerTask = BuyerTask.findById(id);
		boolean result = Task.isTaoBaoAndTmall(buyerTask.taskId);
		//User buyer = getCurrentUser();
		User buyer = User.findByNick(nick);
		Object[] args = new Object[] { buyer.id, buyer.nick, id, type.toString() };
		buyerTaskStepLog.info("Buyer={}-{} perform buyerTask={} save step={}", args);

		// 检查任务ID是否正确，以及该步骤是否已做过（通过比较BuyerTaskStepType的顺序）
		long currentTaskId = buyerAccountId;
		/*if(session.get(BizConstants.BUYER_TASK_STEP)==null){
			renderFailedJson(ReturnCode.OP_BIZ_LIMIT);
		}*/
		BuyerTaskStepType currentStep = type;
		if (currentTaskId != id || currentStep.getOrder() > type.getOrder()) {
			Object[] args2 = new Object[] { buyer.id, buyer.nick, id, type.toString(), currentTaskId, currentStep.title };
			buyerTaskStepLog.error("[Buyer={}-{} perform buyerTask={} save step={}] Current SESSION taskId={} step={}", args2);
			return ReturnCode.OP_BIZ_LIMIT+"";
		}

		if (!result) {
			/*
			 * 检查任务步骤应包含的数据
			 */
			// 第一步“挑选商品”：四个其他商品链接
			if (type == BuyerTaskStepType.CHOOSE_ITEM && !vo.validateUrls(vo.itemUrls, 2)) {
				return ReturnCode.WRONG_INPUT+"";
			}
			// 第三步“浏览店铺及询盘”：四个其他商品链接，一个聊天截图的图片url
			if (type == BuyerTaskStepType.VIEW_AND_INQUIRY && (!vo.validateUrls(vo.itemUrls, 2) || !vo.validateUrls(vo.picUrls, 1))) {
				return ReturnCode.WRONG_INPUT+"";
			}

			/*
			 * 检查上个做完的步骤
			 */
			buyerTaskStepLog.info("Fetch and Check Buyer={} last perform step of buyerTask={}", buyer.id, id);

			BuyerTaskStep lastTaskStep = BuyerTaskStep.findBuyerLastStep(currentTaskId, buyer.id);
			// 上个步骤记录不存在时，当前步骤必须是第一步“货比三家”
			if (lastTaskStep == null && currentStep != BuyerTaskStepType.CHOOSE_ITEM) {
				buyerTaskStepLog.error("[Buyer={}-{} perform buyerTask={} save step={}] Input step must be first:[CHOOSE_ITEM]", args);
				return ReturnCode.OP_BIZ_LIMIT+"";
			}
			// 上个步骤记录存在时，当前步骤必须为“上个步骤记录的下一个步骤”
			if (lastTaskStep != null && currentStep != lastTaskStep.type.getNext()) {
				Object[] args3 = new Object[] { buyer.id, buyer.nick, id, type.toString(), currentTaskId, currentStep.title };
				buyerTaskStepLog.error("[Buyer={}-{} perform buyerTask={} save step={}] Current SESSION taskId={} step={}", args3);
				return ReturnCode.OP_BIZ_LIMIT+"";
			}

			args = new Object[] { currentStep.toString(), buyer.id, id, };
			buyerTaskStepLog.info("Sessoin current taskStep={} ,buyer={},buyerTask={}", args);

			if (lastTaskStep != null) {
				args = new Object[] { buyer.id, lastTaskStep.id, lastTaskStep.type.toString(), id };
				buyerTaskStepLog.info("Buyer={} last perform step={}[{}] of buyerTask={}", args);
			}
		}
		/*
		 * 检查正在做的任务(状态)
		 */

		if (buyerTask == null || buyerTask.isNotBelongTo(buyer)
				|| (buyerTask.status != TaskStatus.WAIT_PAY && buyerTask.status != TaskStatus.RECIEVED)) {
			return ReturnCode.WRONG_INPUT+"";
		}

		// 第五步下单支付：检查用户填写的“订单号”及“付款金额”
		if (type == BuyerTaskStepType.ORDER_AND_PAY) {
			validation.isTrue(vo.validateUrls(vo.picUrls, 1));
			validation.isTrue(StringUtils.isNumeric(vo.orderNo));
			validation.isTrue(NumberUtils.isNumber(vo.realPaidFee));
			validation.required(messageId);
			validation.min(messageId, 1);
			//handleWrongInput(false);

			// 设置随机分配的订单留言记录
			buyerTask.messageId = messageId;
		}

		args = new Object[] { buyer.id, buyer.nick, type.toString(), buyerTask.taskId, buyerTask.id, buyerTask.status };
		buyerTaskStepLog.info("Buyer={}-{} perform save step={} of buyerTask={}-{} with status={}", args);

		BuyerTaskStep.newInstance(buyerTask.id, buyer.id).type(type).content(vo).create(buyerTask);

		/*
		 * 更新当前要做的任务步骤标记
		 */
		if (BuyerTaskStepType.ORDER_AND_PAY == type) {
			// 删除session中当前进行任务id
			session.remove(BizConstants.BUYER_TASK_ID);

			// 删除当前任务进行的步骤
			session.remove(BizConstants.BUYER_TASK_STEP);
		} else {
			// 更新当前任务进行的步骤为下一个步骤
			session.put(BizConstants.BUYER_TASK_STEP, type.getNext());
		}
		return "success";
	}
	
	/**
     * 
     * 获取文件上传凭证.<br>
     * 
     * 已放入缓存中（3小时），平台所有用户共享使用.
     *
     * @since  0.1
     * @author youblade
     * @created 2014年8月22日 下午2:33:56
     */
    public static void fetchUploadToken(boolean force) {
        // 强制标记：为用户生成一个全新的token，以解决频繁的“上传失败”问题
        if (force) {
            renderJson(QnCloudUtil.generateUploadToken());
        }
        
        String key = CacheType.FILE_UPTOKEN.getKey();
        String uptoken = CacheUtil.get(key);
        if (Strings.isNullOrEmpty(uptoken)) {
            uptoken = QnCloudUtil.generateUploadToken();
            CacheUtil.set(key, uptoken, CacheType.FILE_UPTOKEN.expiredTime);
        }
        renderJson(uptoken);
    }
	
	

	/**
	 * 
	 * 我的任务->任务进展：撤销买手任务
	 * 
	 * @param id
	 * @since v0.1
	 * @author moloch
	 * @created 2014-9-23 上午11:21:52
	 */
	public static void cancelBuyerTask(@Required @Min(1) long id,String nick) {
		//handleWrongInput(false);
		User buyer = User.findByNick(nick);
		BuyerTask bt = BuyerTask.findById(id);
		if (bt == null || bt.isNotBelongTo(buyer)) {
			renderJson(ReturnCode.WRONG_INPUT);
		}
		// 状态限制
		if (bt.status != TaskStatus.RECIEVED && bt.status != TaskStatus.WAIT_PAY) {
			renderJson(ReturnCode.BIZ_LIMIT);
		}
		Object[] args = new Object[] { buyer.id, buyer.nick, bt.taskId, bt.id };
		buyerTaskStepLog.info("Buyer={}-{} cancle buyerTask={}-{}", args);

		// 撤销任务
		bt.cancel();
		
		// 若当前操作任务被撤销 则将其从session中删除
		if (Long.toString(id).equals(session.get(BizConstants.BUYER_TASK_ID))) {
			session.remove(BizConstants.BUYER_TASK_ID);
		}

		renderJson("success");
	}
	
	
	/**
	 * 
	 * 我的任务->“快递单已打印，待确认收货”：买手确认收货并复制淘宝后台好评
	 * 
	 * @since v0.1
	 * @author tr0j4n
	 * @created 2014-8-16 下午9:48:50
	 */
	public static void confirmRecv(@Required Platform platform, @Required long id,String nick) {
		//handleWrongInput(false);
		User user = User.findByNick(nick);
		
		Map map = new HashMap<String,Object>();
		//Map map2 = new HashMap<String,Object>();
		renderArgs.put("currPlatform", platform);
		map.put("currPlatform", platform);
		// 获取待处理的任务
		TaskSearchVo vo = TaskSearchVo.newInstance().buyerId(user.id).platform(platform).buyerTaskId(id);
		List<BuyerTaskVo> list = BuyerTask.findListForConfirm(vo);
		
		for (BuyerTaskVo buyerTaskVo : list) {
            buyerTaskVo.value();
            
        }
		String[] includeFields = new String[] { "id","taskIdStr","orderId", "itemId", "itemTitle", "itemPic","goodCommentWords","goodCommentImg", "shopId", "shopName", "buyerAccountNick",
				"platform" };
		renderArgs.put("jsonTaskList", JsonUtil.toJson(list, BuyerTaskVo.class, includeFields));
		map.put("jsonTaskList", list);
		//Task task=Task.findById("goodCommentWords",vo.taskId);
		//renderArgs.put("goodCommentWords", task.goodCommentWords);
		// TODO 存在待处理任务的平台
		renderArgs.put(BizConstants.PLATFORMS, Platform.values());
		map.put("platforms",  Platform.values());
		renderJson(map);
	}

	
	/**
	 * app第六步
	 * @param id
	 * @param url
	 * @param nick
	 */
	
	public static void confirmRecvGoodDemo(@Required long id, String url ,String nick) {
		// TODO Auto-generated method stub
		BuyerTaskStepVo vo = new BuyerTaskStepVo();
		vo.picUrls.add(url);
		renderJson(confirmRecvGoods(id,vo,nick));

	}
	
	
	
	/**
	 * 
	 * 确认收货并好评.
	 * 
	 * @param id  子任务id(4位)
	 *            :买手任务ID
	 * @param vo
	 * @since 0.1
	 */
	public static String  confirmRecvGoods(@Required long id, @Required BuyerTaskStepVo vo ,String nick) {
		if (vo != null) {
			validation.required(vo.picUrls);
			// 该功能仅上传一张图片，校验第一个即可
			String picUrl = vo.picUrls.get(0);
			validation.required(picUrl);
			validation.url(picUrl);
		}
		//handleWrongInput(false);
		
		User user = User.findByNick(nick);

		// 检查任务是否存在、是否所属当前用户、是否待确认发货
		BuyerTask buyerTask = BuyerTask.findById(id);
		if(!isAdminOperate()) {
			if (buyerTask == null || !buyerTask.isBelongTo(user) || buyerTask.status != TaskStatus.WAIT_CONFIRM) {
				return ReturnCode.WRONG_INPUT+"";
			}
		}
		buyerTask.confirmGoodsAndRate(vo);
		return "success";
	}
	
	
	
	 /**
     * 
     * 【买手】我的任务-> (退款页面) 获取退款列表
     *
     * @param vo
     * @since  v1.6
     * @author playlaugh
     * @created 2014年11月29日 下午3:07:07
     */
    
  
	
	
	
	/**
	 *app 获取已付款的各个状态下买手的任务数(好像没什么用）
	 * @param status
	 * @param nick
	 */
	public static void buyerTaskCountsDemo(String status,String nick) {
		// TODO Auto-generated method stub
		TaskSearchVo vo = new TaskSearchVo();
		vo.status = TaskStatus.valueOf(status);
		vo.module = SearchModule.valueOf(status);
		renderJson(buyerTaskCounts(vo,nick));
	}
	
	/**
     * 
     * 获取已付款的各个状态下买手的任务数
     *
     * @param vo
     * @since  v2.0
     */
    public static Map buyerTaskCounts(@Required TaskSearchVo vo ,String nick) {
    	if (vo != null) {
            vo.orderId = StringUtils.trimToNull(vo.orderId);
            vo.buyerAccountNick = StringUtils.trimToNull(vo.buyerAccountNick);
        }
    	//handleWrongInput(false);
    	User buyer = User.findByNick(nick);
    	vo.module=SearchModule.WAIT_BUYER_CONFIRM_SYS_REFUND;
    	Integer p1 = BuyerTask.findListByPage(vo.buyerId(buyer.id)).totalCount;
    	
    	vo.module=SearchModule.WAIT_BUYER_CONFIRM_EDITED_SYS_REFUND;
    	Integer p2 = BuyerTask.findListByPage(vo.buyerId(buyer.id)).totalCount;
    	
    	vo.module=SearchModule.WAIT_SYS_REFUND;
    	Integer p3 = BuyerTask.findListByPage(vo.buyerId(buyer.id)).totalCount;
    	
    	Map map = Maps.newHashMapWithExpectedSize(3);
        map.put("count1", p1);
        map.put("count2", p2);
        map.put("count3", p3);
        return map;
    }
	
    
    /**
	 * 确认与驳回
	 * 【买手】我的任务->核实平台退款页面：确认“平台返款”任务的退款金额
	 * 
	 * @param id(四位-->子任务id)
	 * @param isReject（false 为 确认 true 为 驳回）
	 * @since v1.7
	 */
	public synchronized static void confirmSysRefund(@Required @Min(1) long id, boolean isReject ,String nick) {
		//handleWrongInput(false);
		BuyerTask buyerTask = BuyerTask.findById(id);
		//是否是管理员处理
		if(!isAdminOperate()) {
			// Check BuyerTask
			User buyer = User.findByNick(nick);
			if (buyerTask == null
					|| buyerTask.isNotBelongTo(buyer)
					|| (buyerTask.status != TaskStatus.WAIT_BUYER_CONFIRM_SYS_REFUND && buyerTask.status != TaskStatus.WAIT_BUYER_CONFIRM_EDITED_SYS_REFUND)) {
				if (isReject) {
					log.warn("Buyer={} reject sysRefund of buyerTask={} with status={}", buyer.id, id, buyerTask.status);
				} else {
					log.warn("Buyer={} confirm sysRefund of buyerTask={} with status={}", buyer.id, id, buyerTask.status);
				}
				renderFailedJson(ReturnCode.WRONG_INPUT);
			}
		}
		BuyerTaskProcessor.confirmSysRefund(buyerTask, isReject);
		
		renderSuccessJson();
	}

	
	
	/**
	 * 
	 * 核实退款
	 * 
	 * @param bt
	 * @since v0.1
	 * @author moloch
	 * @created 2014-8-17 下午6:49:55
	 */
	public static void confirmRefund(@Required BuyerTask bt) {
		if (bt != null) {
			validation.required(bt.id);
			validation.required(bt.taskId);
			validation.required(bt.status);
		}
		handleWrongInput(false);
		
		BuyerTask buyerTask = BuyerTask.findById(bt.id);
		//查看是否为管理员操作，如果是，则不需要验证
		if(!isAdminOperate()) {
			// Check BuyerTask
			User buyer = getCurrentUser();
			if (buyerTask == null || buyerTask.isNotBelongTo(buyer) || buyerTask.status != TaskStatus.REFUNDING) {
				renderFailedJson(ReturnCode.WRONG_INPUT);
			}
		}
		buyerTask.confirmSellerRefund();
		renderSuccessJson();
	}
	
	
	
	
	//------------------------------------------------------做任务----------------------------------------------------
	//------------------------------------------------------做任务----------------------------------------------------
	//------------------------------------------------------做任务----------------------------------------------------
	//------------------------------------------------------做任务----------------------------------------------------
	//------------------------------------------------------做任务----------------------------------------------------
	//------------------------------------------------------做任务----------------------------------------------------
	
	/**
	 * 
	 * 任务邀请奖励->获取已发放奖励列表
	 *
	 * @param vo
	 * @since  v2.0
	 * @author Mark Xu
	 * @created 2015-3-10 下午4:56:53
	 */
	public static void listTaskRewardLog(@Required TaskRewardLogVo vo) {
		//handleWrongInput(false);
		vo.userNick = StringUtils.trimToNull(vo.userNick);
		vo.inviteUserNick = User.findByIdWichCache(Long.parseLong(session.get(Secure.FIELD_AUTH))).nick;
		
		Page<TaskRewardLog> page = TaskRewardLog.findByPage(vo);
		renderPageJson(page.items, page.totalCount);
	}
	
	

	/**
	 * 
	 * 个人中心页面.
	 * 
	 * @since 0.1
	 * @author youblade
	 */
	// >>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>个人中心<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<
	// >>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>个人中心<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<
	// >>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>个人中心<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<
	// >>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>个人中心<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<
	public static Map index(String nick) {
		User user = User.findByNick(nick);
		SqlSession ss = SessionFactory.getSqlSession();
		try {
			
			
			/*
			 * 【买手】
			 */
			// 获取待返款任务数
			BuyerTaskMapper mapper = ss.getMapper(BuyerTaskMapper.class);
			// 涉及“已支付”~“已退款”中的所有状态
			List<TaskStatus> statuses = Lists.newArrayList(
					TaskStatus.WAIT_SEND_GOODS, TaskStatus.WAIT_EXPRESS_PRINT,
					TaskStatus.EXPRESS_PRINT, TaskStatus.WAIT_CONFIRM,
					TaskStatus.WAIT_REFUND,
					TaskStatus.WAIT_SELLER_CONFIRM_SYS_REFUND,
					TaskStatus.WAIT_BUYER_CONFIRM_SYS_REFUND,
					TaskStatus.WAIT_BUYER_CONFIRM_EDITED_SYS_REFUND,
					TaskStatus.BUYER_REJECT_SYS_REFUND,
					TaskStatus.WAIT_SYS_REFUND);
			PersonalInfoVo vo = mapper
					.selectBuyerPaidFeeInfo(user.id, statuses);
			renderArgs.put("waitRefundTaskCount", vo.taskCount);
			renderArgs.put("waitRefundAmount", vo.sumPaidFee);

			// 待发放佣金、冻结金币
			// 涉及“已完成”、“已取消”之外的全部状态
			statuses = Lists.newArrayList(TaskStatus.FINISHED,
					TaskStatus.CANCLED);
			vo = mapper.selectBuyerExcutingInfo(user.id, statuses);
			renderArgs.put("unreceivedRewardIngot", vo.sumRewardIngot);
			renderArgs.put("lockedIngot", vo.getLockedIngot());

			/*
			 * 垫付本金统计
			 */
			// 统计维度：商家返款、平台返款
			statuses = Lists.newArrayList(TaskStatus.RECIEVED,
					TaskStatus.WAIT_PAY, TaskStatus.CANCLED,
					TaskStatus.FINISHED);
			List<UserMoneyStats> buyerDepositStats = mapper
					.sumPaidFeeByBuyerIdAndStatusNotIn(user.id, statuses);

			UserMoneyStats lockedSysRefundDeposit = new UserMoneyStats();
			UserMoneyStats lockedSellerRefundDeposit = new UserMoneyStats();
			for (UserMoneyStats stats : buyerDepositStats) {
				// 平台返款
				if (BooleanUtils.isTrue(stats.sysRefund)) {

					lockedSysRefundDeposit.amount = stats.amount;
					lockedSysRefundDeposit.count = stats.count;
					continue;
				}
				// 商家直接返款
				lockedSellerRefundDeposit.amount = stats.amount;
				lockedSellerRefundDeposit.count = stats.count;
			}
			renderArgs.put("lockedSysRefundDeposit", lockedSysRefundDeposit);
			renderArgs.put("lockedSellerRefundDeposit",
					lockedSellerRefundDeposit);
			// 垫付本金总计
			renderArgs.put("totalLockedDepositAmt",
					lockedSysRefundDeposit.amount
							+ lockedSellerRefundDeposit.amount);

			// 统计待核实退款的任务，维度：商家返款、平台返款
			statuses = Lists.newArrayList(
					TaskStatus.WAIT_BUYER_CONFIRM_EDITED_SYS_REFUND,
					TaskStatus.WAIT_BUYER_CONFIRM_SYS_REFUND,
					TaskStatus.REFUNDING);
			List<UserMoneyStats> buyerWaitConfirmDepositStats = mapper
					.sumPaidFeeWaitConfirmByBuyerIdAndStatusIn(user.id,
							statuses);

			UserMoneyStats watiConfirmSysRefundDeposit = new UserMoneyStats();
			UserMoneyStats watiConfirmSellerRefundDeposit = new UserMoneyStats();
			for (UserMoneyStats stats : buyerWaitConfirmDepositStats) {
				// 商家直接返款
				if (stats.staus == TaskStatus.REFUNDING) {
					watiConfirmSellerRefundDeposit.amount = stats.amount;
					watiConfirmSellerRefundDeposit.count = stats.count;
					continue;
				}
				// 平台返款
				watiConfirmSysRefundDeposit.amount = stats.amount;
				watiConfirmSysRefundDeposit.count = stats.count;
			}
			renderArgs.put("watiConfirmSysRefundDeposit",
					watiConfirmSysRefundDeposit);
			renderArgs.put("watiConfirmSellerRefundDeposit",
					watiConfirmSellerRefundDeposit);

			// 可提现
			long withDepositAmt = 0;
			BuyerDepositRecord depositRecord = ss.getMapper(
					BuyerDepositRecordMapper.class).selectLastRecord(user.id);
			if (depositRecord != null) {
				withDepositAmt = depositRecord.balance;
			}
			renderArgs.put("withDepositAmt", withDepositAmt);
			/*
			 * 【买手】设置经验值、本金余额
			 */
			BuyerExperienceRecord record4 = ss.getMapper(
					BuyerExperienceRecordMapper.class)
					.selectLastRecord(user.id);
			user.experience = (record4 != null) ? record4.balance : 0L;

			BuyerDepositRecord record = ss.getMapper(
					BuyerDepositRecordMapper.class).selectLastRecord(user.id);
			user.deposit = (record != null) ? record.balance : 0L;
			// 用户金币
			UserIngotRecordMapper mapper3 = ss
					.getMapper(UserIngotRecordMapper.class);
			UserIngotRecord record1 = mapper3.selectLastRecord(user.id);
			if (record1 != null) {
				user.ingot = record1.balance;
			} else {
				user.ingot = 0L;
			}
			HashMap<String, Object> map = new HashMap<String, Object>();
			map.put("user", user);
			map.put("taskCount", vo.taskCount);
			map.put("sumPaidFee", vo.sumPaidFee);
			map.put("sumRewardIngot", vo.sumRewardIngot);
			map.put("getLockedIngot", vo.getLockedIngot());
			map.put("totalLockedDepositAmt",
					(lockedSysRefundDeposit.amount + lockedSellerRefundDeposit.amount));
			map.put("withDepositAmt", withDepositAmt);
			map.put("ingot", user.ingot);
			//
			return map;

		} finally {
			ss.close();
		}

	}

	// >>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>个人中心<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<
	// >>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>个人中心<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<
	// >>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>个人中心<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<
	// >>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>个人中心<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<

	// >>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>任务列表<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<
	// >>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>任务列表<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<
	// >>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>任务列表<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<
	// >>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>任务列表<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<

	/**
	 * 
	 * 任务列表->分页获取任务(暂时无分页).
	 * 
	 * @since 0.1
	 * @author youblade
	 * @created 2014年7月25日 下午6:03:14
	 */
	public static void listTasks(@Required TaskSearchVo vo) {
		List<Task> tasks = Task.findALLList(vo.status(TaskStatus.PUBLISHED));
		renderJSON(tasks);
	}

	/**
	 * 
	 * 判断该条任务是否可接
	 * 
	 * @return
	 * @since v2.0
	 * @author fufei
	 * @created 2015年1月23日 下午5:06:31
	 */
	public static boolean isTakeTasks(BuyerTask bt, Task tasks) {
		/*
		 * 检查是否可接手任务
		 */
		// 存在未完成的任务【提示：任务进行中】
		if (session.contains(BizConstants.BUYER_TASK_ID)) {
			if (BuyerTask.findById(Long.parseLong(session
					.get(BizConstants.BUYER_TASK_ID))).status == TaskStatus.CANCLED) {
				session.remove(BizConstants.BUYER_TASK_ID);
			} else {
				tasks.tags = false;
				tasks.reason = "存在未完成的任务";
				return false;
			}
		}

		// 存在未完成的任务【提示：买号异常】
		User buyer = getCurrentUser();
		if (bt.buyerAccountId <= 0) {
			tasks.tags = false;
			tasks.reason = "买号没有绑定";
			return false;
		}
		BuyerAccount buyerAccount = BuyerAccount.findById(bt.buyerAccountId);
		if (!BuyerAccount.validate(buyerAccount, buyer)) {
			tasks.tags = false;
			tasks.reason = "买号异常";
			return false;
		}

		// 该任务已被用户接手过，不能重复领取【提示：已经领取过】
		if (BuyerTask.isDuplicateTaking(bt.buyerAccountId, bt.taskId)) {
			tasks.tags = false;
			tasks.reason = "已经领取过";
			return false;
		}
		// 存在尚未完成的任务，不能领取新任务【提示：任务进行中】
		BuyerTask todoTask = BuyerTask.findTodoTask(buyer.id);
		if (todoTask != null) {
			tasks.tags = false;
			tasks.reason = "存在尚未完成的任务";
			return false;
		}
		Task task = TaskExecutor.getTask(bt.taskId);

		// 任务已全被领取完【提示：任务已领完】
		if (task.isTakenOver(bt.device)) {
			tasks.tags = false;
			tasks.reason = "任务已领完";
			return false;
		}

		// 父任务数据异常【提示：任务状态有误】
		if (task == null || task.status != TaskStatus.PUBLISHED) {
			tasks.tags = false;
			tasks.reason = "任务状态有误";
			return false;
		}

		// 领取非“平台返款”的任务需要绑定【财付通】作为退款账号【提示：未绑定财付通】
		if (BooleanUtils.isFalse(task.sysRefund)) {
			FundAccount tenpayAccount = FundAccount.findByType(
					PayPlatform.TENPAY, buyer.id);
			if (tenpayAccount == null) {
				tasks.tags = false;
				tasks.reason = "未绑定财付通";
				return false;
			}
		}

		// 金币不足需要充值购买才能接手任务【提示：金币不足】
		if (buyer.ingot < BizConstants.TASK_TAKING_INGOT) {
			tasks.tags = false;
			tasks.reason = "金币不足";
			return false;
		}

		// 平台限制每个【买号】5单/天，30单/周，90单/月
		TaskStats taskStats = TaskStats.findForBuyerUntilNow(bt.buyerAccountId);

		String day = SysConfig
				.getConfigValue(SysConfigKey.BUYER_TASK_DAY_COUNT);
		if (StringUtils.isEmpty(day)) {
			day = Config.BUYER_TASK_DAY_COUNT;
		}

		String week = SysConfig
				.getConfigValue(SysConfigKey.BUYER_TASK_WEEK_COUNT);
		if (StringUtils.isEmpty(week)) {
			week = Config.BUYER_TASK_WEEK_COUNT;
		}

		String mouth = SysConfig
				.getConfigValue(SysConfigKey.BUYER_TASK_MONTH_COUNT);
		if (StringUtils.isEmpty(mouth)) {
			mouth = Config.BUYER_TASK_MONTH_COUNT;
		}

		// 【提示：当日已做完】
		if (taskStats.dayCount >= Integer.parseInt(day)) {
			log.warn("BuyerAccount={} has taken {} tasks Today",
					bt.buyerAccountId, taskStats.dayCount);
			tasks.tags = false;
			tasks.reason = "当日任务已做完";
			return false;
		}
		// 【提示：本周已做完】
		if (taskStats.weekCount >= Integer.parseInt(week)) {
			log.warn("BuyerAccount={} has taken {} tasks this Week",
					bt.buyerAccountId, taskStats.weekCount);
			tasks.tags = false;
			tasks.reason = "本周任务已做完";
			return false;
		}
		// 【提示：本月已做完】
		if (taskStats.monthCount >= Integer.parseInt(mouth)) {
			log.warn("BuyerAccount={} has taken {} tasks this Month",
					bt.buyerAccountId, taskStats.monthCount);
			tasks.tags = false;
			tasks.reason = "本月任务已做完";
			return false;
		}

		bt.buyerId = buyer.id;
		// 账号和商家之间的限制【提示：账号和商家之间的限制】
		if (task.platform == Platform.TAOBAO || task.platform == Platform.TMALL) {
			// 目前只限制淘宝平台,其他平台不做限制。
			if (!TaskExecutor.isAvailableBuyerIdAndSellerId(buyer.id,
					task.sellerId)) {
				tasks.tags = false;
				tasks.reason = "该商家的任务已经领取过";
				return false;
			}
		}

		// 账号和店铺之间的限制【提示：账号和店铺之间的限制】
		// if (!TaskExecutor.isAvailableBuyerIdAndShopId(buyer.id, task.shopId))
		// {
		// return false;
		// }
		// 买号和店铺之间的限制【提示：买号和店铺之间的限制】
		if (!TaskExecutor.isValidateBuyerAccountAndShopId(bt.buyerAccountId,
				task.shopId)) {
			tasks.tags = false;
			tasks.reason = "该店铺的任务已经领取过";
			return false;
		}
		// 买号和商品之间的限制【提示：买号和商品之间的限制】
		if (!TaskExecutor.isAvailableBuyerAccountAndItem(bt.buyerAccountId,
				task.itemId)) {
			tasks.tags = false;
			tasks.reason = "该商品的任务已经领取过";
			return false;
		}
		tasks.tags = true;
		return true;
	}

	/**
	 * 
	 * 判断该条任务是否可接
	 * 
	 * @return
	 * @since v2.0
	 * @author fufei
	 * @created 2015年1月23日 下午5:06:31
	 */
	public static boolean isTakeTask(BuyerTask bt) {
		/*
		 * 检查是否可接手任务
		 */
		// 存在未完成的任务【提示：任务进行中】
		if (session.contains(BizConstants.BUYER_TASK_ID)) {
			if (BuyerTask.findById(Long.parseLong(session
					.get(BizConstants.BUYER_TASK_ID))).status == TaskStatus.CANCLED) {
				session.remove(BizConstants.BUYER_TASK_ID);
			} else {
				return false;
			}
		}

		// 存在未完成的任务【提示：买号异常】
		User buyer = getCurrentUser();
		BuyerAccount buyerAccount = BuyerAccount.findById(bt.buyerAccountId);
		if (!BuyerAccount.validate(buyerAccount, buyer)) {
			return false;
		}

		// 该任务已被用户接手过，不能重复领取【提示：已经领取过】
		if (BuyerTask.isDuplicateTaking(bt.buyerAccountId, bt.taskId)) {
			return false;
		}
		// 存在尚未完成的任务，不能领取新任务【提示：任务进行中】
		BuyerTask todoTask = BuyerTask.findTodoTask(buyer.id);
		if (todoTask != null) {
			return false;
		}
		Task task = TaskExecutor.getTask(bt.taskId);
		// 父任务数据异常【提示：任务状态有误】
		if (task == null || task.status != TaskStatus.PUBLISHED) {
			return false;
		}

		// 领取非“平台返款”的任务需要绑定【财付通】作为退款账号【提示：未绑定财付通】
		if (BooleanUtils.isFalse(task.sysRefund)) {
			FundAccount tenpayAccount = FundAccount.findByType(
					PayPlatform.TENPAY, buyer.id);
			if (tenpayAccount == null) {
				return false;
			}
		}

		// 任务已全被领取完【提示：任务已领完】
		if (task.isTakenOver(bt.device)) {
			return false;
		}

		// 金币不足需要充值购买才能接手任务【提示：金币不足】
		if (buyer.ingot < BizConstants.TASK_TAKING_INGOT) {
			return false;
		}

		// 平台限制每个【买号】5单/天，30单/周，90单/月
		TaskStats taskStats = TaskStats.findForBuyerUntilNow(bt.buyerAccountId);

		String day = SysConfig
				.getConfigValue(SysConfigKey.BUYER_TASK_DAY_COUNT);
		if (StringUtils.isEmpty(day)) {
			day = Config.BUYER_TASK_DAY_COUNT;
		}

		String week = SysConfig
				.getConfigValue(SysConfigKey.BUYER_TASK_WEEK_COUNT);
		if (StringUtils.isEmpty(week)) {
			week = Config.BUYER_TASK_WEEK_COUNT;
		}

		String mouth = SysConfig
				.getConfigValue(SysConfigKey.BUYER_TASK_MONTH_COUNT);
		if (StringUtils.isEmpty(mouth)) {
			mouth = Config.BUYER_TASK_MONTH_COUNT;
		}

		// 【提示：当日已做完】
		if (taskStats.dayCount >= Integer.parseInt(day)) {
			log.warn("BuyerAccount={} has taken {} tasks Today",
					bt.buyerAccountId, taskStats.dayCount);
			return false;
		}
		// 【提示：本周已做完】
		if (taskStats.weekCount >= Integer.parseInt(week)) {
			log.warn("BuyerAccount={} has taken {} tasks this Week",
					bt.buyerAccountId, taskStats.weekCount);
			return false;
		}
		// 【提示：本月已做完】
		if (taskStats.monthCount >= Integer.parseInt(mouth)) {
			log.warn("BuyerAccount={} has taken {} tasks this Month",
					bt.buyerAccountId, taskStats.monthCount);
			return false;
		}

		bt.buyerId = buyer.id;
		// 账号和商家之间的限制【提示：账号和商家之间的限制】
		if (task.platform == Platform.TAOBAO || task.platform == Platform.TMALL)// 目前只限制淘宝平台的
			if (!TaskExecutor.isAvailableBuyerIdAndSellerId(buyer.id,
					task.sellerId)) {
				return false;
			}
		// 账号和店铺之间的限制【提示：账号和店铺之间的限制】
		// if (!TaskExecutor.isAvailableBuyerIdAndShopId(buyer.id, task.shopId))
		// {
		// return false;
		// }
		// 买号和店铺之间的限制【提示：买号和店铺之间的限制】
		if (!TaskExecutor.isValidateBuyerAccountAndShopId(bt.buyerAccountId,
				task.shopId)) {
			return false;
		}
		// 买号和商品之间的限制【提示：买号和商品之间的限制】
		if (!TaskExecutor.isAvailableBuyerAccountAndItem(bt.buyerAccountId,
				task.itemId)) {
			return false;
		}
		return true;
	}

	// >>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>任务列表<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<
	// >>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>任务列表<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<
	// >>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>任务列表<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<
	// >>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>任务列表<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<

	
	/**
	 * 
	 * 重设密码页面
	 * 
	 * @since v0.1
	 * @author moloch & youblade
	 * @created 2014-8-20 上午11:07:22
	 */
	public static void resetPass() {
		renderArgs.put(UserAuthentication.FIELD_EMAIL, getCurrentUser().email);
		render();
	}

	/**
	 * 
	 * 用户中心->绑定店铺.
	 * 
	 * @since 0.1
	 * @author youblade
	 * @created 2014年7月16日 下午2:22:29
	 */
	public static void shop() {
		renderArgs.put("platforms", Platform.values());

		// 取得省级数据
		renderArgs.put("regions", Region.findRoot());
		render();
	}

	/**
	 * 
	 * 用户中心->绑定店铺：获取指定平台下已绑定的店铺.
	 * 
	 * @since 0.1
	 * @author youblade
	 * @created 2014年7月16日 下午2:22:14
	 */
	public static void listShops(@Required Platform platform) {
		handleWrongInput(false);

		long sellerId = getCurrentUser().id;
		List<Shop> list = Shop.findByPlatformAndSellerId(platform, sellerId);
		renderJson(list);
	}

	/**
	 * 
	 * 获取某个用户的VIP等级
	 * 
	 * @since v2.0
	 * @author Mark Xu
	 * @created 2015-2-28 下午3:09:39
	 */
	public static void getVipStatus() {
		Map map = Maps.newHashMapWithExpectedSize(1);
		map.put("VipStatus", User.findById(getCurrentUser().id).vipStatus);
		renderJson(map);
	}

	/**
	 * 
	 * 为绑定店铺操作取得发件区域数据
	 * 
	 * @param id
	 * @since v0.1
	 * @author moloch
	 * @created 2014-7-29 上午9:52:30
	 */
	public static void region(@Required int id) {
		handleWrongInput(false);

		List<Region> list = Region.findByParentId(id);
		renderJson(list);
	}

	/**
	 * 
	 * 用户中心->绑定店铺：绑定新店铺.
	 * 
	 * @since 0.1
	 * @author youblade
	 * @created 2014年7月16日 下午2:24:09
	 */
	public static void addShop(@Required Shop shop) {
		if (shop != null) {
			validation.required(shop.nick);
			validation.required(shop.url);
			validation.required(shop.address);
			validation.required(shop.platform);
			validation.required(shop.sellerName);
			validation.required(shop.mobile);
			validation.required(shop.street);
			validation.required(shop.branch);

			log.info("---------------------ww----");
			// 淘宝、天猫平台需要填写的是卖家账号，其他平台填写店铺名
			if (Platform.TAOBAO == shop.platform
					|| Platform.TMALL == shop.platform) {
				validation.required(shop.nick);
			} else {
				// validation.required(shop.name);
			}
			shop.nick = StringUtils.trim(shop.nick);
			shop.url = StringUtils.trim(shop.url);
			shop.sellerName = StringUtils.trim(shop.sellerName);
			shop.mobile = StringUtils.trim(shop.mobile);
			shop.street = StringUtils.trim(shop.street);
			shop.branch = StringUtils.trim(shop.branch);
		}
		System.out.println("321321");
		handleWrongInput(false);

		System.out.println("1");
		// if (!shop.validateUrl()) {
		// System.out.println("2");
		// renderFailedJson(ReturnCode.OP_BIZ_LIMIT, "店铺网址与所选平台不一致！");
		// }
		/*
		 * 逻辑比较简单暂时就不做那么多封装了，直接写在view层
		 */
		SqlSession ss = SessionFactory.getSqlSession();
		try {
			// 检查该店铺是否已绑定过（店铺名）
			ShopMapper mapper = ss.getMapper(ShopMapper.class);
			if (mapper.selectExists(shop) != null) {
				renderFailedJson(ReturnCode.OP_BIZ_LIMIT, "该店铺已被绑定过！");
			}
			// 后台绑定店铺长度限制
			if (shop.url.length() > 300) {
				renderFailedJson(ReturnCode.OP_BIZ_LIMIT, "店铺url过长！");
			}
			// 检查用户在该平台已绑定的店铺是否已经达到上限
			User user = getCurrentUser();
			shop.sellerId = user.id;
			int count = mapper.countByPlatformAndSellerId(shop);
			VipStatus vipStatus = User.findById(getCurrentUser().id).vipStatus;
			int maxCount = 3;
			if (vipStatus == VipStatus.VIP1) {
				maxCount = 5;
			} else if (vipStatus == VipStatus.VIP2) {
				maxCount = 10;
			}

			if (vipStatus != VipStatus.VIP3 && count >= maxCount) {
				System.out.println("4");
				renderFailedJson(ReturnCode.OP_BIZ_LIMIT, "店铺数量已达到上限！");
			}

			// 保存
			shop.createTime = DateTime.now().toDate();
			shop.sellerId = user.id;
			mapper.insert(shop);
			System.out.println("5");
		} finally {
			ss.commit(true);
			ss.close();
		}
		renderSuccessJson();
	}

	// 更改店铺信息
	public static void modifyShop(@Required String address,
			@Required String sellerName, @Required String mobile,
			@Required String street, @Required String branch, @Required long id) {

		Shop shop = new Shop();

		shop.sellerName = StringUtils.trim(sellerName);
		shop.mobile = StringUtils.trim(mobile);
		shop.street = StringUtils.trim(street);
		shop.branch = StringUtils.trim(branch);
		shop.id = id;
		shop.address = address;
		shop.modifyTime = DateTime.now().toDate();

		handleWrongInput(false);
		/*
		 * 逻辑比较简单暂时就不做那么多封装了，直接写在view层
		 */
		SqlSession ss = SessionFactory.getSqlSession();
		try {
			// 检查该店铺是否已绑定过（店铺名）
			ShopMapper mapper = ss.getMapper(ShopMapper.class);

			mapper.update(shop);
		} finally {
			ss.commit(true);
			ss.close();
		}
		renderSuccessJson();
	}

	/**
	 * 
	 * 资金管理.
	 * 
	 * @since 0.1
	 * @author youblade
	 * @created 2014年7月29日 下午5:40:50
	 */
	public static void money() {
		User user = getCurrentUser();
		List<FundAccount> accounts = FundAccount.findByUserId(user.id);

		// 设置已绑定的账号
		if (!accounts.isEmpty()) {
			for (FundAccount fund : accounts) {
				if (PayPlatform.ALIPAY == fund.type) {
					renderArgs.put("alipay", fund);
				} else if (PayPlatform.TENPAY == fund.type) {
					renderArgs.put("tenpay", fund);
				} else {
					renderArgs.put("bank", fund);
					renderArgs.put(
							"bankNoLastNum",
							fund.no.substring(fund.no.length() - 5,
									fund.no.length()));
				}
			}
		}

		// 取得银行数据
		renderArgs.put(BizConstants.PAY_PLATFORMS, KQpayPlatform.values());
		// 取得省级数据
		renderArgs.put("regions", Region.findRoot());

		render();
	}

	/**
	 * 
	 * TODO 提现管理
	 * 
	 * @since v1.6
	 * @author playlaugh
	 * @created 2014年11月25日 下午4:41:51
	 */
	public static void withdraw(String nick) {

		User user = User.findByNick(nick);
		// 取得财付通、支付宝、银行数据
		renderArgs.put("tenpay", FundAccount.findTenpay(user.id));
		renderArgs.put("alipay", FundAccount.findAlipay(user.id));
		renderArgs.put("bank", FundAccount.findBank(user.id));
		List<KQpayPlatform> lists = Lists.newArrayList(KQpayPlatform.values());
		lists.remove(KQpayPlatform.POST);
		renderArgs.put(BizConstants.PAY_PLATFORMS, lists);
		renderArgs.put("regions", Region.findRoot());

		if (user.isSeller()) {
			renderArgs.put("isSeller", true);
			render();
		}

		// 设置本月已提现次数
		WithdrawSearchVo svo = WithdrawSearchVo.newInstance().userId(user.id);
		int withdrawCount = BizConstants.WITHDRAW_MONTH_COUNT
				- UserWithdrawRecord.countThisMonthApply(svo);
		renderArgs.put("countThisMonthApply", Math.max(0, withdrawCount));
		Map<String,Object> map = new HashMap<String,Object>();
		map.put("countThisMonthApply", Math.max(0, withdrawCount));
		// 买手垫付本金的可提现金额
		long withDepositAmt = 0;
		SqlSession ss = SessionFactory.getSqlSession();
		try {
			BuyerDepositRecord depositRecord = ss.getMapper(
					BuyerDepositRecordMapper.class).selectLastRecord(user.id);
			if (depositRecord != null) {
				withDepositAmt = depositRecord.balance;
			}
		} finally {
			ss.close();
		}
		renderArgs.put("withDepositAmt", withDepositAmt);
		renderJson(map);
	}

	/**
	 * 
	 * 商家个性化配置页面
	 * 
	 * @since v2.0
	 * @author Mark Xu
	 * @created 2015-4-10 上午11:35:50
	 */
	public static void sellerConfig() {

		User user = getCurrentUser();
		SellerConfig config = SellerConfig.findBySellerId(user.id);
		if (config == null) {
			config = config.newInstance(0, 0, 0, 0);
		}
		render(config);
	}

	/**
	 * 
	 * 买手个性化配置页面
	 * 
	 * @since v2.0
	 * @author Mark Xu
	 * @created 2015-4-28 下午4:18:46
	 */
	public static void buyerConfig() {
		User buyer = getCurrentUser();
		BuyerConfig config = BuyerConfig.findByBuyerId(buyer.id);
		if (config == null) {
			config = new BuyerConfig();
			config.isClearView = false;
		}
		renderArgs.put("user", buyer);
		renderArgs.put("config", config);
		render();
	}

	/**
	 * 
	 * 回填商家限制信息
	 * 
	 * @param sellerId
	 * @since v2.0
	 * @author Mark Xu
	 * @created 2015-4-9 下午4:20:26
	 */
	public static void limitMessage(@Required long sellerId) {
		handleWrongInput(false);
		SellerConfig config = SellerConfig.findBySellerId(sellerId);
		Map map = Maps.newHashMapWithExpectedSize(4);
		if (config == null) {
			config = config.newInstance(0, 0, 0, 0);
		}
		map.put("buyerAndSellerTime", config.buyerAndSellerTime);
		map.put("buyerAndShopTime", config.buyerAndShopTime);
		map.put("buyerAcountAndShopTime", config.buyerAcountAndShopTime);
		map.put("buyerAcountAndItemTime", config.buyerAcountAndItemTime);
		renderJson(map);
	}

	/**
	 * 
	 * 增加修改商家规则限制
	 * 
	 * @param config
	 * @since v2.0
	 * @author Mark Xu
	 * @created 2015-4-9 下午5:51:17
	 */
	public static void modifyLimitMessage(@Required SellerConfig config) {
		handleWrongInput(false);

		if (!isAdminOperate()) {
			User user = getCurrentUser();
			if (user != null) {
				config.sellerId = user.id;
			} else {
				renderFailedJson(ReturnCode.WRONG_INPUT);
			}
		}
		if (config.isNull(config.sellerId)) {
			config.insert(config);
		} else {
			config.updateBySellerConfig(config);
		}
		renderSuccessJson();
	}

	/**
	 * 
	 * 更改买手配置信息
	 * 
	 * @since v2.0
	 * @author Mark Xu
	 * @created 2015-4-28 下午5:22:23
	 */
	public static void modifyBuyerConfig(@Required BuyerConfig config) {
		handleWrongInput(false);

		User user = getCurrentUser();
		if (user != null) {
			config.buyerId = user.id;
		} else {
			renderFailedJson(ReturnCode.WRONG_INPUT);
		}
		// 检测用户的VIP等级
		if (user.vipStatus != VipStatus.VIP2
				&& user.vipStatus != VipStatus.VIP3
				&& user.vipStatus != VipStatus.SPECIAL) {
			renderFailedJson(ReturnCode.OP_BIZ_LIMIT,
					"VIP等级过低，请先通过充值会员达到指定的VIP等级");
		}
		if (config.isNull(config.buyerId)) {
			config.insert(config);
		} else {
			config.updateByBuyerConfig(config);
		}
		renderSuccessJson();
	}

	/**
	 * 
	 * TODO 资金管理->获取退款/提现账号.
	 * 
	 * @since 0.1
	 * @author youblade
	 * @created 2014年7月29日 下午5:42:05
	 */
	public static void fundAccount() {

		renderSuccessJson();
	}

	/**
	 * 
	 * 获取某人某类型的退款账号信息
	 * 
	 * @param account
	 * @since v0.1
	 * @author moloch
	 * @created 2014-8-24 下午2:43:42
	 */
	public static void getFundAccount(@Required FundAccount account) {
		handleWrongInput(false);

		account.userId = getCurrentUser().id;
		FundAccount fAccount = FundAccount.findByType(account.type,
				account.userId);
		renderJson(fAccount);
	}

	/**
	 * 
	 * 资金管理->保存退款/提现账号.
	 * 
	 * @since 0.1
	 * @author youblade
	 * @created 2014年7月29日 下午5:42:05
	 */
	public static void saveFundAccount(@Required FundAccount account) {
		if (account != null) {
			validation.required(account.name);
			validation.required(account.no);
		}
		handleWrongInput(false);

		// 若已存在默认账号
		User user = getCurrentUser();
		if (account.type == PayPlatform.TENPAY) {
			if (FundAccount.findTenpay(user.id) != null) {
				renderFailedJson(ReturnCode.OP_BIZ_LIMIT);
			}
		} else if (account.type == PayPlatform.ALIPAY) {
			if (FundAccount.findAlipay(user.id) != null) {
				renderFailedJson(ReturnCode.OP_BIZ_LIMIT);
			}
		} else {
			if (FundAccount.findBank(user.id) != null) {
				renderFailedJson(ReturnCode.OP_BIZ_LIMIT);
			}
			if (account.type.toString().equals(KQpayPlatform.POST.toString())) {
				renderFailedJson(ReturnCode.BIZ_LIMIT, "暂时不支持绑定邮政银行卡");
			}
		}

		// 绑定账号
		account.userId = user.id;
		account.save();
		FundAccount fAccount = FundAccount.findByType(account.type, user.id);
		renderJson(fAccount);
	}

	/**
	 * 
	 * 用户中心->买手账号.
	 * 
	 * @since 0.1
	 * @author youblade
	 * @created 2014年8月1日 下午12:47:49
	 */
	public static void buyerAccount() {
		// 平台数据 这里做了筛选 不显示天猫
		List<Platform> pfs = Lists.newArrayList(Platform.values());
		pfs.remove(Platform.TMALL);
		renderArgs.put("platforms", pfs);
		// 取得省级地域数据
		renderArgs.put("regions", Region.findRoot());

		render();
	}

	private static void checkBeforeSaveBuyerAccount(BuyerAccount account,
			User buyer) {
		Validate.notNull(account.platform);

		boolean isAddNew = account.id <= 0;
		SqlSession ss = SessionFactory.getSqlSession();
		try {
			BuyerAccountMapper mapper = ss.getMapper(BuyerAccountMapper.class);
			// 修改买号时检查记录是否存在、是否属于该用户
			if (!isAddNew) {
				BuyerAccount buyerAccount = mapper.selectById(account.id);
				if (buyerAccount == null || !buyerAccount.belongTo(buyer)) {
					log.warn("Buyer={}-{} save buyerAccount forbidden:{}",
							buyer.id, buyer.nick, account.toJson());
					renderFailedJson(ReturnCode.OP_BIZ_LIMIT);
				}
			}
			
			// 检查信息是否重复
			log.info("Buyer={}-{} save buyerAccount: Check Duplicate",
					buyer.id, buyer.nick);
			
			account.userId = buyer.id;
			 
			BuyerAccount buyerAccount = mapper.selectForCheckDuplicate(account);
			System.out.println("到这里"+account.mobile+" "+account.consignee+" "+account.state+" "+account.city+" "+" "+account.region+" "+account.address+" "+account.id);
	        System.out.println("到这里"+buyerAccount);
	        System.out.println("到这里");
	        System.out.println("到这里");
	        System.out.println("到这里");
	        System.out.println("到这里");
			if (buyerAccount != null) {
				log.warn("Buyer={}-{} save buyerAccount forbidden:{}",
						buyer.id, buyer.nick, account.toJson());
				renderFailedJson(ReturnCode.OP_BIZ_LIMIT);
			}
			
			// 检查当前已绑定的买号数量（如果是合作商家，无需检查已绑定的买号数）
			if (User.findById(account.userId).vipStatus != VipStatus.SPECIAL) {
				log.info("Buyer={}-{} save buyerAccount: Check binded count",
						buyer.id, buyer.nick);

				int count = mapper.count(BuyerAccountSearchVo.newInstance()
						.platform(account.platform).userId(account.userId));
				if (isAddNew && count >= 2) {
					log.warn(
							"Buyer={}-{} save buyerAccount forbidden-More than 3 :{}",
							buyer.id, buyer.nick, account.toJson());
					renderFailedJson(ReturnCode.BIZ_LIMIT);
				}
			}
		} finally {
			ss.close();
		}
	}

	
	/**
	 * 
	 * 修改买手买号的排序数
	 * 
	 * @since v2.0
	 * @author Mark Xu
	 * @created 2015-4-22 下午1:56:41
	 */
	public static void modifyOrderNumber(@Required long id,
			@Required long orderNumber) {
		handleWrongInput(false);

		User u = getCurrentUser();
		if (u == null || !BuyerAccount.includeAccount(id, u.id)) {
			renderFailedJson(ReturnCode.BIZ_LIMIT, "无效买号");
		}
		BuyerAccount.modifyOrderNumber(id, orderNumber);
		renderSuccessJson();
	}

	/**
	 * 
	 * 查看买号的任务状态
	 * 
	 * @param btVo
	 * @since v0.1
	 * @author moloch
	 * @created 2014-8-6 上午10:41:43
	 */
	public static void accountTaskStatus(@Required BuyerTaskSearchVo btVo) {
		if (btVo != null) {
			validation.required(btVo.accountId);
		}
		handleWrongInput(false);
		btVo.buyerId = getCurrentUser().id;

		BuyerAccount ba = new BuyerAccount();
		// 查询与该买号有关的任务状态
		ba.hasTaskExecuting = BuyerTask.hasExecuteTask(btVo);
		ba.hasTask = BuyerTask.isTakeTask(btVo);

		renderJson(ba);
	}

	/**
	 * 
	 * TODO 黑名单.
	 * 
	 * @since 0.1
	 * @author youblade
	 * @created 2014年7月26日 下午3:45:30
	 */
	public static void blacklist() {
		render();
	}

	
	/**
	 * 
	 * 会员开通/续费页面.
	 * 
	 * @since 0.1
	 * @author youblade
	 * @created 2014年9月5日 下午4:58:29
	 */
	public static void member() {
		renderArgs.put(BizConstants.PAY_PLATFORMS, KQpayPlatform.values());

		// 计算会员有效期剩余天数
		LocalDate dueDate = new LocalDate(getCurrentUser().dueTime);
		int daysRemaining = Days.daysBetween(LocalDate.now(), dueDate)
				.getDays();
		render(daysRemaining);
	}

	/**
	 * 
	 * 会员充值. 【商家】 1.使用金币支付 2.使用押金支付 3.使用网银实时支付 【买手】 1.使用金币支付 2.使用网银实时支付
	 * 
	 * @param month
	 * @param useIngot
	 * @param usePledge
	 * @since 0.1
	 * @author youblade
	 * @created 2014年8月25日 下午12:25:50
	 */
	public static void memberCharge(@Required Integer month, boolean useIngot,
			boolean usePledge) {

		User user = getCurrentUser();
		user = user.findById(user.id);
		// 【买手】身份不能使用押金支付
		if (user.type == UserType.BUYER) {
			validation.isTrue("usePledge", !usePledge);
		}

		// 检查会员费用
		long payFee = MemberChargeRecord.getRechargeAmount(user.type, month);
		validation.min("payFee", payFee, 100);
		validation.range(month, 1, 24);

		// 检查余额是否足够支付
		if (useIngot && usePledge) {
			validation.isTrue("user.ingot+pledge", user.ingot + user.pledge);
		}
		if (useIngot && !usePledge) {
			validation.min("user.ingot", user.ingot, payFee);
		}
		if (usePledge && !useIngot) {
			validation.min("user.pledge", user.pledge, payFee);
		}
		handleWrongInput(false);

		// 使用网银直连现金支付
		if (!useIngot && !usePledge) {
			PayTradeLog log = PayTradeLog
					.newInstance(TradeType.MEMBER, user.id).amount(payFee)
					.bizMemberMonth(month).memo("用户开通会员" + month + "个月");
			log.save();
			renderJson(log.id);
		}
		// 使用押金或金币支付
		MemberChargeRecord r = MemberChargeRecord.newInstance(user.id, month)
				.userType(user.type).amount(payFee);
		boolean result = r.create(useIngot, usePledge);
		if (!result) {
			renderFailedJson(ReturnCode.FAIL);
		}
		// 给用户升级会员到VIP1、VIP2、VIP3
		Integer minMonth = BizConstants.UPGRADE_NORMAL_COUNT;
		if (user.vipStatus == VipStatus.VIP1) {
			minMonth = BizConstants.UPGRADE_VIP1_COUNT;
		} else if (user.vipStatus == VipStatus.VIP2) {
			minMonth = BizConstants.UPGRADE_VIP2_COUNT;
		} else if (user.vipStatus == VipStatus.VIP3) {
			minMonth = BizConstants.UPGRADE_VIP3_COUNT;
		}
		if (minMonth < month) {
			VipStatus vipStatus = VipStatus.NORMAL;
			if (BizConstants.UPGRADE_VIP1_COUNT <= month
					&& month < BizConstants.UPGRADE_VIP2_COUNT) {
				vipStatus = VipStatus.VIP1;
			} else if (BizConstants.UPGRADE_VIP2_COUNT <= month
					&& month < BizConstants.UPGRADE_VIP3_COUNT) {
				vipStatus = VipStatus.VIP2;
			} else {
				vipStatus = VipStatus.VIP3;
			}

			user.changeVipStatus(user.id, vipStatus);
		}
		renderSuccessJson();
	}

	

	/**
	 * 
	 * 检查密码是否正确
	 * 
	 * @param pass
	 * @since v0.1
	 * @author moloch
	 * @created 2014-9-25 下午3:22:04
	 */
	public static void checkPass(@Required String oldPassword) {
		handleWrongInput(false);

		boolean result = false;
		User user = getCurrentUser();
		if (StringUtils.isNotBlank(oldPassword)) {
			result = user.validate(oldPassword);
		}

		renderText(result);
	}

	/**
	 * 
	 * 个人中心公告
	 * 
	 * @param num
	 * @since v0.1
	 * @author moloch
	 * @created 2014-10-5 下午3:09:43
	 */
	public static void listUserNotice(@Required int num) {
		handleWrongInput(false);

		Role role = Role.ALL;
		User user = getCurrentUser();
		if (user.type == UserType.BUYER) {
			role = Role.BUYER;
		} else if (user.type == UserType.SELLER) {
			role = Role.SELLER;
		}
		renderJson(Notice.getUserNotice(num, role));
	}

	/**
	 * 
	 * 邀请好友页面书籍
	 * 
	 * @since v2.0
	 * @author youblade
	 * @created 2014年12月19日 下午5:01:52
	 */
	public static void invite(String nick) {
		User user = User.findByNick(nick);
		if (user.isBuyer()
				&& BuyerTask.findListByPage(TaskSearchVo.newInstance()
						.buyerId(user.id).status(TaskStatus.FINISHED)).items
						.size() > 0) {
			renderArgs.put("allowInvite", "true");
		} else if (user.isSeller()
				&& Task.findByPage(TaskSearchVo.newInstance().buyerId(user.id)
						.status(TaskStatus.PUBLISHED).fields("t.id")).items
						.size() > 0) {
			renderArgs.put("allowInvite", "true");
		} else {
			renderArgs.put("allowInvite", "true");
		}
		renderArgs.put("userStatusList", UserStatus.values());
		UserSearchVo uvo = UserSearchVo.newInstance();
		uvo.id = user.id;
		Map<String,Object> map = new HashMap<String,Object>();
		map.put("inviteStatistics", UserInvitedRecord.findInviteStatistics(uvo));
		renderJson(map);
	}
	
	
	/**
     * 
     * 获取买手的会员到期时间
     *
     * @param uid
     * @since  v2.0
     * @author Mark Xu
     * @created 2015-3-9 上午10:32:06
     */
    public static void memberDueTime(@Required long uid) {
    	//handleWrongInput(false);
    	System.out.println("进来了");
    	System.out.println("进来了");
    	System.out.println("进来了");
    	System.out.println("进来了");
    	System.out.println("进来了");
    	User user = User.findById(uid);
    	Map map = Maps.newHashMapWithExpectedSize(1);
    	if(user!=null&&user.dueTime!=null) {
    		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
    		map.put("lag", "YES");
        	map.put("memberDueTime", sdf.format(user.dueTime));
    	}else {
    		map.put("lag", "NOT");
    		map.put("memberDueTime", "用户未开通过会员"); 
    	}
    	renderJson(map);
    }
	
    
    /**
	 * 
	 * 【后台管理】-资金管理，查询今日收益
	 * 
	 * @param vo
	 * @since 0.1//@Required int userId
	 * @author youblade
	 * @created 2014年10月1日 上午11:29:14
	 */
	
	public static long amount(int userId) {
		Long amount=UserIngotRecord.findByAnount(userId);
		return amount;
	}

	
	/**
	 * 
	 * 获取买手的购买账号.
	 * 
	 * @author yqc
	 * @created 2016-8-9
	 */
	public static void listBuyerAccounts2(Platform2 platform,String nick ,boolean receive) {
		User user = User.findByNick(nick);
		BuyerAccountSearchVo2 vo = BuyerAccountSearchVo2.newInstance().platform(platform).userId(user.id);
		// 接手任务时仅返回可供使用的买号（每个买号每天限制5个任务）
        if (receive) {
            vo.status(ExamineStatus2.EXAMINED);
            renderJson(BuyerAccount2.findForTakingTask(vo));
        }

		List<BuyerAccount2> list = BuyerAccount2.findList(vo);
		renderJson(list);
	}
	
	
	
	
	/**
	 * 
	 * 资金管理->保存退款/提现账号.
	 * 
	 * @since 0.1
	 * @author youblade
	 * @created 2014年7月29日 下午5:42:05
	 * 'account.id': id,
			'account.no': no,
			'account.name': name,
			'account.userId': userId,
			'account.type': 'TENPAY'
			'account.openingBank': openingBank,
			'account.address': address
	 */
	public static void saveFundAccount(@Required String nick,@Required String no,@Required String name, String id, String userId,String type, String openingBank, String address) {
		FundAccount account = new FundAccount();
//		account.id = Long.valueOf(id);
		account.name = name;
		account.no = no;
		account.type = PayPlatform.valueOf(type);
		account.userId = Long.valueOf(userId);
		account.openingBank = openingBank;
		account.address = address;
		
		User user = User.findByNick(nick);
		if (account.type == PayPlatform.TENPAY) {
			if (FundAccount.findTenpay(user.id) != null) {
				renderFailedJson(ReturnCode.OP_BIZ_LIMIT);
			}
		} else if (account.type == PayPlatform.ALIPAY) {
			if (FundAccount.findAlipay(user.id) != null) {
				renderFailedJson(ReturnCode.OP_BIZ_LIMIT);
			}
		} else {
			if (FundAccount.findBank(user.id) != null) {
				renderFailedJson(ReturnCode.OP_BIZ_LIMIT);
			}
		}

		// 绑定账号
		account.userId = user.id;
		account.save();
		FundAccount fAccount = FundAccount.findByType(account.type, user.id);
		renderJson(fAccount);
	}
	
	/**
     * 
     * TODO 提现管理
     *
     * @since  v1.6
     * @author playlaugh
     * @created 2014年11月25日 下午4:41:51
     */
    public static void withdrawApp(String nick) {
        Map<String,Object> map = new HashMap<String,Object>();
        User user = User.findByNick(nick);
        // 取得财付通、支付宝、银行数据
        map.put("tenpay", FundAccount.findTenpay(user.id));
        map.put("alipay", FundAccount.findAlipay(user.id));
        map.put("bank",FundAccount.findBank(user.id));
        // 设置本月已提现次数
        WithdrawSearchVo svo = WithdrawSearchVo.newInstance().userId(user.id);
        int withdrawCount = BizConstants.WITHDRAW_MONTH_COUNT - UserWithdrawRecord.countThisMonthApply(svo);
        map.put("countThisMonthApply",Math.max(0, withdrawCount));
        // 买手垫付本金的可提现金额
        long withDepositAmt = 0;
        SqlSession ss = SessionFactory.getSqlSession();
        try {
            BuyerDepositRecord depositRecord = ss.getMapper(BuyerDepositRecordMapper.class).selectLastRecord(user.id);
            if (depositRecord != null) {
                withDepositAmt = depositRecord.balance;
            }
        } finally {
            ss.close();
        }
        map.put("withDepositAmt", withDepositAmt);
        renderJson(map);
    }
    
    
  //获取首页数据
  	public static  void getAll(String nick,String userid){
  		Map<String,Object> map = new HashMap<String,Object>();
  		map.put("AMOUNT", amount(Integer.parseInt(userid)));
  		map.put("INDEX", index(nick));
  		map.put("PROCESS", PROCESS("PROCESS",nick));
  		map.put("WAIT_SEND_GOODS", WAIT_SEND_GOODS("WAIT_SEND_GOODS",nick));
  		map.put("WAIT_CONFIRM", WAIT_CONFIRM("WAIT_CONFIRM",nick));
  		map.put("WAIT_BUYER_CONFIRM_SYS_REFUND", ListBuyerRefundDemo("WAIT_BUYER_CONFIRM_SYS_REFUND",nick));
  		//map.put("SYS_REFUND_WITHDRAW", ListBuyerRefundDemo2("SYS_REFUND_WITHDRAW",nick));
  		renderJson(map);
  	}
  	
  	public synchronized  Map WAIT_BUYER_CONFIRM_SYS_REFUND(String status,String nick){
  		TaskSearchVo vo = new TaskSearchVo();
  		vo.pageNo=1;
  		vo.pageSize=1000;
  		vo.status=TaskStatus.valueOf(status);
  		return listBuyerTasks(vo,null,nick);
  	}
  	public synchronized static Map WAIT_CONFIRM(String status,String nick){
  		TaskSearchVo vo = new TaskSearchVo();
  		vo.pageNo=1;
  		vo.pageSize=1000;
  		vo.status=TaskStatus.valueOf(status);
  		return listBuyerTasks(vo,null,nick);
  	}
  	public synchronized static Map WAIT_SEND_GOODS(String status,String nick){
  		TaskSearchVo vo = new TaskSearchVo();
  		vo.pageNo=1;
  		vo.pageSize=1000;
  		vo.status=TaskStatus.valueOf(status);
  		return listBuyerTasks(vo,null,nick);
  	}
  	public synchronized static Map PROCESS(String type,String nick){
  		TaskSearchVo vo = new TaskSearchVo();
  		vo.pageNo=1;
  		vo.pageSize=1000;
  		return listBuyerTasks(vo,type,nick);
  	}

  public static Page ListBuyerRefundDemo(String status,String nick){

  		TaskSearchVo vo = new TaskSearchVo();
  		vo.status = TaskStatus.valueOf(status);
  		vo.module = SearchModule.valueOf(status);
  		return ListBuyerRefund(vo,nick);
  	}
  	public static void ListBuyerRefundDemo2(String nick){

  		TaskSearchVo vo = new TaskSearchVo();
  		vo.module = SearchModule.valueOf("SYS_REFUND_WITHDRAW");
  		renderJson(ListBuyerRefund(vo,nick));
  	}




  /**
  		 * 
  		 * 获取未完成任务
  		 * 
  		 * @since 0.1
  		 * @author youblade
  		 * @created 2014年8月2日 下午6:00:15
  		 */
  	public synchronized static Map listBuyerTasks(TaskSearchVo vo,String type,String nick) {
  		
  		if (vo != null) {
  			vo.orderId = StringUtils.trimToNull(vo.orderId);
  			vo.buyerAccountNick = StringUtils.trimToNull(vo.buyerAccountNick);
  		}
  		// handleWrongInput(false);
  		User buyer = User.findByNick(nick);
  		Page<BuyerTaskVo> p = null;
  		if ("FINISHED".equals(StringUtils.trim(type))) {
  			p = BuyerTask.findListByPage(vo.buyerId(buyer.id).status(
  					TaskStatus._FINISHED));
  		} else if ("CANCEL".equals(StringUtils.trim(type))) {
  			p = BuyerTask.findListByPage(vo.buyerId(buyer.id).status(
  					TaskStatus.CANCLED));
  		} else if ("ALL".equals(StringUtils.trim(type))) {
  			p = BuyerTask.findListByPage(vo.buyerId(buyer.id));
  		} else {
  			if (vo.status == null) {
  				// 进行中
  				p = BuyerTask
  						.findListByPage(vo.buyerId(buyer.id).statusExclude(
  								TaskStatus.CANCLED, TaskStatus.FINISHED));
  			} else {
  				p = BuyerTask.findListByPage(vo.buyerId(buyer.id));
  			}
  		}
  		for (BuyerTaskVo taskVo : p.items) {
  			taskVo.subShopNamePrefix();
  		}
  		Map<String,Object> map = new HashMap<String,Object>();
  		map.put("items",p.items);
  		map.put("totalCount",p.totalCount);
  		return map;
  	}

  /**
       * 
       * 【买手】我的任务-> (退款页面) 获取退款列表
       *
       * @param vo
       * @since  v1.6
       * @author playlaugh
       * @created 2014年11月29日 下午3:07:07
       */
      
      public static Page ListBuyerRefund(@Required TaskSearchVo vo,String nick) {
          if (vo != null) {
              validation.min(vo.pageNo, 1);
              validation.max(vo.pageSize, 50);
          }
          //handleWrongInput(false);

          long BuyerId = User.findByNick(nick).id;
          vo.buyerId(BuyerId).pageSize(20);
          // 默认为“商家手动退款”
          if (vo.module == null) {
              vo.module(SearchModule.REFUNDING);
          }
          
          Page<BuyerTaskVo> p = BuyerTask.findListByPage(vo);

          //renderPageJson(p.items, p.totalCount);
          return p;
      }
      /**
  	 * 
  	 * 资金管理-买手垫付本金提现申请
  	 * 
  	 * @param tids
  	 *            ：买手本金提现时的相关任务ID
  	 * @param payPass
  	 * @since v1.7
  	 * @author youblade
  	 * @created 2014年11月29日 下午8:31:44
  	 */
  	public synchronized static void applyBuyerDepositWithdraw(String tids,
  			String payPass, String nick) {
  		// handleWrongInput(false);
  		String[] a = tids.split(",");
  		List<Long> list = new ArrayList<Long>();
  		for (int i = 0; i < a.length; i++) {
  			list.add(Long.valueOf(a[i]));
  		}
  		User buyer = User.findByNick(nick);
  		// 检查支付密码
  		if (Strings.isNullOrEmpty(buyer.payPassword)) {
  			renderJson("需要先设置支付密码~~");
  		}
  		if (!StringUtils.equals(payPass, buyer.payPassword)) {
  			renderJson("支付密码不正确");
  		}

  		// 检查是否设置银行卡
  		FundAccount bank = FundAccount.findBank(buyer.id);
  		if (bank == null) {
  			renderJson("需要先绑定银行卡~~");
  		}

  		// 提现金额后续使用任务垫付资金来计算得出
  		UserWithdrawRecord.newInstance(0).createDepositApply(bank, list);
  		renderJson("success");
  	}

  	
  	/**
	 * 
	 * 资金管理-（卖家押金、买手佣金）提现申请.
	 * 
	 * @param amount
	 * @param payPass
	 * @since 0.1
	 * @author youblade
	 * @created 2014年9月15日 下午1:24:30
	 */
	public synchronized static void applyWithdraw(@Required int amount,
			@Required String payPass, String nick) {
		// 最少100元
		validation.min("amount", amount, 100);
		// handleWrongInput(false);

		User user = User.findByNick(nick);
		// 检查支付密码
		if (Strings.isNullOrEmpty(user.payPassword)) {
			renderJson("请到个人中心设置支付密码~~");
		}
		if (!StringUtils.equals(payPass, user.payPassword)) {
			renderJson("支付密码不正确");
		}

		// 检查账户余额是否足够：卖家提现押金，买手提现金币
		long centAmount = amount * 100;
		if (user.type == UserType.SELLER) {
			validation.min(user.pledge, centAmount);
		} else {
			validation.min(user.ingot, centAmount);
		}
		/*
		 * 检查是否设置提现账号 1、卖家必须已绑定银行卡 2、买手必须已绑定银行卡 与 财付通
		 */
		FundAccount bank = FundAccount.findBank(user.id);
		if (user.isSeller() && bank == null) {
			renderJson("必须先绑定【银行卡】账号~~");
		}

		// 检查本月已申请提现次数：申请次数不计入
		if (user.isBuyer()) {
			WithdrawSearchVo svo = WithdrawSearchVo.newInstance().userId(
					user.id);
			if (UserWithdrawRecord.countThisMonthApply(svo) >= 3) {
				renderJson("每个月最多申请提现3次~~");
			}
		}

		UserWithdrawRecord.newInstance(centAmount).createApply(user);
		renderJson("success");
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
	public static void listBuyerAccountsApp(Platform platform,String nick,boolean receive ) {
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

}
