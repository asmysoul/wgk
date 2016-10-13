package controllers.admins;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Map;

import models.AdminOperatorLog;
import models.AdminOperatorLog.LogType;
import models.AdminTradeLog;
import models.AdminTradeLog.AdminTradeType;
import models.AdminUser;
import models.BuyerAccount;
import models.BuyerAccount2;
import models.BuyerAccount3;
import models.BuyerDepositRecord;
import models.FundAccount;
import models.MemberChargeRecord;
import models.SellerPledgeRecord;
import models.SellerPledgeRecord.PledgeAction;
import models.Shop;
import models.TenpayTradeLog.TradeType;
import models.User;
import models.User.UserStatus;
import models.User.UserType;
import models.User.VipStatus;
import models.UserIngotRecord;
import models.mappers.BuyerAccountMapper;
import models.mappers.SellerPledgeRecordMapper;
import models.mappers.UserIngotRecordMapper;

import org.apache.ibatis.session.SqlSession;
import org.joda.time.DateTime;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import play.cache.Cache;
import play.data.validation.Min;
import play.data.validation.Required;
import vos.BuyerAccountSearchVo;
import vos.BuyerAccountSearchVo.ExamineStatus;
import vos.BuyerAccountSearchVo2;
import vos.BuyerAccountSearchVo3;
import vos.Page;
import vos.ShopSearchVo;
import vos.UserSearchVo;

import com.aton.base.BaseController;
import com.aton.config.CacheType;
import com.aton.config.ReturnCode;
import com.aton.db.SessionFactory;
import com.aton.util.MixHelper;
import com.aton.util.StringUtils;
import com.google.common.collect.Maps;

import enums.Sign;
import enums.pay.PayPlatform;

/**
 * 
 * 用户相关数据：用户、买号、店铺等.
 * 
 * @author youblade
 * @since  v0.2
 * @created 2014年10月22日 下午6:13:21
 */
public class UserManage extends BaseController {

    private static final Logger log = LoggerFactory.getLogger(UserManage.class);
    
    /**
     * 
     * 后台管理->用户管理：为用户充值押金、金币.
     *
     * @param uid
     * @param yuan    :RMB元
     * @param type
     * @since  0.1
     * @author youblade
     * @created 2014年9月22日 上午11:56:36
     */
    public static void doRecharge(@Required long uid, @Required @Min(0.1) double yuan, TradeType type, @Required String memo) {
        handleWrongInput(false);
        User user = User.findById(uid);
        if (user == null) {
            renderFailedJson(ReturnCode.WRONG_INPUT);
        }
        
        if (user.status == UserStatus.INACTIVE || user.status == UserStatus.LOCKED) {
            String msg = MixHelper.format("用户{}状态异常：{}", user.nick, user.status);
            renderFailedJson(ReturnCode.BIZ_LIMIT, msg);
        }
        
        // 默认为【商家】充押金，为【买手】充金币
        if (type == null) {
            type = (user.type == UserType.SELLER) ? TradeType.PLEDGE : TradeType.INGOT;
        }
        
        // 转换金额单位为分
        long amount = (long) (yuan * 100);
        
        // 创建充值记录(在用户金币记录和管理员操作记录中)
        if(session.get("admin-authcode")==null){
            renderFailedJson(ReturnCode.INVALID_SESSION);
        }
    	AdminUser adminAccount = AdminUser.findByIdWichCache(Long.valueOf(Long.parseLong(session.get("admin-authcode"))));
    	AdminTradeLog adminTradeLog = new AdminTradeLog();
    	adminTradeLog = adminTradeLog.sign(Sign.PLUS).adminId(adminAccount.id).amount(amount).userId(user.id);
    	switch (type) {
        
        case INGOT:
            log.info("Mock Buying ingot={} for user={}", new Object[] { amount, user.id });
            UserIngotRecord.newInstance(user.id, null).plus(amount).memo(memo).buy();

        	if(user.type == UserType.BUYER) {	
            	String message = MixHelper.format("买手{} 为买手充金币 金额 {}元", user.nick, (double)amount/100);
            	AdminOperatorLog.insert(adminAccount.name, LogType.BUYER_INGOT, message);
            	adminTradeLog = adminTradeLog.memo(memo).tradeType(AdminTradeType.BUYER_INGOT);
            	AdminTradeLog.insert(adminTradeLog);
        	}else {
            	String message = MixHelper.format("商家{} 为商家充金币 金额 {}元", user.nick, (double)amount/100);
            	AdminOperatorLog.insert(adminAccount.name, LogType.SELLER_INGOT, message);
            	adminTradeLog = adminTradeLog.memo(memo).tradeType(AdminTradeType.SELLER_INGOT);
            	AdminTradeLog.insert(adminTradeLog);
        	}
            break;
        case PLEDGE:
            if(user.type == UserType.BUYER) { 
                BuyerDepositRecord.rechargeDeposit(amount, uid, memo);
                String message = MixHelper.format("{}买手充本金 金额 {}元", user.nick,(double)amount/100);
                AdminOperatorLog.insert(adminAccount.name, LogType.BUYER_DEPOSIT, message);
                
                adminTradeLog = adminTradeLog.memo(memo).tradeType(AdminTradeType.SELLER_PLEDGE);
                AdminTradeLog.insert(adminTradeLog);
            }
            else{
            log.info("Mock Recharging pledge={} for user={}", new Object[] { amount, user.nick });
            SellerPledgeRecord.newInstance(user.id, null).action(PledgeAction.RECHARGE, amount).memo(memo).create();
            
            String message = MixHelper.format("商家{} 为商家充押金 金额 {}元", user.nick, (double)amount/100);
        	AdminOperatorLog.insert(adminAccount.name, LogType.SELLER_PLEDGE, message);
        	
        	adminTradeLog = adminTradeLog.memo(memo).tradeType(AdminTradeType.SELLER_PLEDGE);
        	AdminTradeLog.insert(adminTradeLog);
            }
        }
        renderSuccessJson();
    }
    
    /**
     * 
     * 将金币转化为押金（限商家）
     *
     * @param uid
     * @since  v2.0
     * @author Mark Xu
     * @created 2015-3-12 下午1:49:29
     */
    public static void ingotToPledge(@Required long uid) {
    	handleWrongInput(false);
    	User user = User.findById(uid);
    	if(user.isBuyer()) {
    		renderFailedJson(34521, "该功能仅支持操作商家");
    	}
    	SqlSession ss = SessionFactory.getSqlSessionWithoutAutoCommit();
    	try {

            UserIngotRecordMapper userIngotMapper = ss.getMapper(UserIngotRecordMapper.class);
            SellerPledgeRecordMapper sellerPledgeMapper = ss.getMapper(SellerPledgeRecordMapper.class);
        	UserIngotRecord ingotLastRecord = userIngotMapper.selectLastRecord(user.id);
        	SellerPledgeRecord pledgeLastRecord = sellerPledgeMapper.selectLastRecord(user.id);
        	String adminAccount = AdminUser.findByIdWichCache(Long.valueOf(Long.parseLong(session.get("admin-authcode")))).name;
        	
        	String memo = MixHelper.format("管理员将商家金币余额{}元全部变更为押金", (double)ingotLastRecord.balance/100);
        	UserIngotRecord ingotRecord = UserIngotRecord.newInstance(user.id, ingotLastRecord).minus(ingotLastRecord.balance).createTime(new Date()).memo(memo);
        	SellerPledgeRecord pledgeRecord = SellerPledgeRecord.newInstance(user.id, pledgeLastRecord).plus(ingotLastRecord.balance).createTime(new Date()).memo(memo);
        	userIngotMapper.insert(ingotRecord);
        	sellerPledgeMapper.insert(pledgeRecord);
        	User.findByIdWichCache(user.id).ingot(ingotRecord.balance).updateCache();
        	User.findByIdWichCache(user.id).pledge(pledgeRecord.balance).updateCache();

        	String message = MixHelper.format("管理员将商家{}金币余额{}元全部变更为押金", user.nick, (double)ingotLastRecord.balance/100);
        	AdminOperatorLog.insert(adminAccount, LogType.BUYER_INGOT, message, ss);
        	ss.commit();
    	}catch(Exception e) {
			renderFailedJson(ReturnCode.BIZ_LIMIT, "更改失败");
    	}finally {
    		ss.close();
        }
    	renderSuccessJson();
    }
    
    /**
     * 
     * 删除用户记录
     *
     * @param uid
     * @since  v2.0
     * @author Mark Xu
     * @created 2015-3-27 上午11:02:48
     */
    public static void cleanIngotCache(@Required long uid) {
    	handleWrongInput(false);
    	Cache.safeDelete(CacheType.USER_INFO.getKey(uid));
    	renderSuccessJson();
    }
    /**
     * 
     * 后台管理->用户管理：获取用户列表数据.
     *
     * @param vo
     * @since  0.1
     * @author youblade
     * @created 2014年10月15日 下午5:58:18
     */
    public static void list(@Required UserSearchVo vo){
        handleWrongInput(false);
        Page<User> page = User.findByPage(vo);
        for(User user : page.items){
        	User u = User.findByIdWichCache(user.id);
        	user.ingot = u.ingot;
        	user.deposit = u.deposit;
        	user.pledge = u.pledge;
        	user.flow = u.flow;
        }
        renderPageJson(page.items, page.totalCount);
    }
    
    /**
     * 后台管理->买号管理：取得所有买号
     */
    public static void listBuyerAccount(@Required BuyerAccountSearchVo vo) {
        fetchAndRenderBuyerAccountPage(vo);
    }
    
    
    /**
     * 后台管理->买号管理：取得所有浏览号
     */
    public static void listBuyerAccount3(@Required BuyerAccountSearchVo3 vo) {
        fetchAndRenderBuyerAccountPage3(vo);
    }
    
    private static void fetchAndRenderBuyerAccountPage3(BuyerAccountSearchVo3 vo) {
        if (vo != null) {
            validation.required(vo.pageNo);
            validation.required(vo.pageSize);
            validation.min(vo.pageNo, 1);
            validation.min(vo.pageSize, 5);
        }
        handleWrongInput(false);
        
        Page<BuyerAccount3> page = BuyerAccount3.findForPage(vo);
        renderPageJson(page.items, page.totalCount);
    }
    
    //后台管理->推广号管理：取得所有推广号
    public static void listBuyerAccount2(@Required BuyerAccountSearchVo2 vo) {
        fetchAndRenderBuyerAccountPage2(vo);
    }
    //推广号
    private static void fetchAndRenderBuyerAccountPage2(BuyerAccountSearchVo2 vo) {
        if (vo != null) {
            validation.required(vo.pageNo);
            validation.required(vo.pageSize);
            validation.min(vo.pageNo, 1);
            validation.min(vo.pageSize, 5);
        }
        handleWrongInput(false);
        
        Page<BuyerAccount2> page = BuyerAccount2.findForPage(vo);
        renderPageJson(page.items, page.totalCount);
    }
    
    
    
    /**
     * 
     * 后台管理->买号审核：待审核买号列表
     * 
     * @param vo
     * @since v0.1
     * @author moloch
     * @created 2014-8-12 下午5:30:58
     */
    public static void listBuyerAccountWaitEaxmine(@Required BuyerAccountSearchVo vo) {
        if (vo == null) {
            renderFailedJson(ReturnCode.WRONG_INPUT);
        }
        fetchAndRenderBuyerAccountPage(vo.status(ExamineStatus.WAIT_EXAMINE));
    }
    
    private static void fetchAndRenderBuyerAccountPage(BuyerAccountSearchVo vo) {
        if (vo != null) {
            validation.required(vo.pageNo);
            validation.required(vo.pageSize);
            validation.min(vo.pageNo, 1);
            validation.min(vo.pageSize, 5);
        }
        handleWrongInput(false);
        
        Page<BuyerAccount> page = BuyerAccount.findForPage(vo);
        renderPageJson(page.items, page.totalCount);
    }
    
    
    
    
    
    
    
    /**
     * 
     * 修改买号资料
     *
     * @param vo
     * @since  v1.9
     * @author playlaugh
     * @created 2014年12月11日 下午3:06:19
     */
    public static void modifyBuyerAccount(@Required BuyerAccount vo){ 
        validation.required(vo.id);
        handleWrongInput(false);
        
        vo.save();
        renderSuccessJson();
    }
    
    
    /**
     * 修改浏览号资料
     */
    public static void modifyBuyerAccount3(@Required BuyerAccount3 vo){ 
        validation.required(vo.id);
        handleWrongInput(false);
        vo.save();
        renderSuccessJson();
    }
    
    public static void modifyBuyerAccount2(@Required BuyerAccount2 vo){ 
        validation.required(vo.id);
        handleWrongInput(false);
        
        vo.save();
        renderSuccessJson();
    }
    
    /**
     * 
     * 后台管理->店铺数据：获取卖家的店铺列表数据.
     * 
     * @since v0.1
     * @author moloch
     * @created 2014-10-6 下午1:41:27
     */
    public static void listShop(@Required ShopSearchVo vo) {
        if (vo != null) {
            validation.min(vo.pageNo, 1);
            validation.required(vo.pageNo);
            validation.required(vo.pageSize);
        }
        handleWrongInput(false);

        Page<ShopSearchVo> page = Shop.findAllForAdmin(vo);
        for(ShopSearchVo svo : page.items){
        	svo.displayPlatformTitle();
        }
        renderPageJson(page.items, page.totalCount);
    }
    /**
     * 
     * 表单数据回填
     *
     * @param uid
     * @since  v2.0
     * @author Mark Xu
     * @created 2015-2-3 上午11:25:09
     */
    public static void userMsg(@Required long uid) {
    	handleWrongInput(false);
        
        User user = User.findById(uid);
        if(user==null)
        	renderFailedJson(ReturnCode.ES_SEARCH_RETURN_EMPTY, "找不到该用户！");
        Map map = Maps.newHashMapWithExpectedSize(7);
        map.put("id", user.id);
        map.put("nick", user.nick);
        map.put("mobile", user.mobile);
        map.put("qq", user.qq);
        map.put("email", user.email);
        map.put("vipStatus", user.vipStatus);
        map.put("status", user.status);
        map.put("name", user.name);
        map.put("dockingMessage", user.dockingMessage);
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
    	handleWrongInput(false);
    	
    	User user = User.findById(uid);
    	Map map = Maps.newHashMapWithExpectedSize(1);
    	if(user!=null&&user.dueTime!=null) {
    		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        	map.put("memberDueTime", sdf.format(user.dueTime));
    	}else {
    		map.put("memberDueTime", "用户未开通过会员"); 
    	}
    	renderJson(map);
    }
    
    /**
     * 
     * 修改用户信息
     *
     * @param email
     * @param uid
     * @since  v1.1
     * @author youblade
     * @created 2014年11月17日 下午5:13:24
     */
    public static void edit(String email, @Required long uid, @Required UserStatus status, @Required VipStatus vipStatus, @Required String qq, @Required String mobile, String password) {
        handleWrongInput(false);
        
        User user = new User();
        user.id = uid;
        user.mobile = mobile;
        user.vipStatus = vipStatus;
        user.qq = qq;
        user.status = status;
        user.email = email;
        User oldUser = user.findById(uid);
        
        if (!password.equals("")) {
        	user.updatePass(uid, password);
        }
        
        
        String newQQ = qq, oldQQ = oldUser.qq, 
        		newMobile = mobile, oldMobile = oldUser.mobile, 
        		newEmail = email, oldEmail = oldUser.email,
        		newVipStatus = vipStatus.title , oldVipStatus = oldUser.vipStatus.title,
        		newStatus = status.title, oldStatus = oldUser.status.title;
        if(newQQ.equals(oldQQ)) {oldQQ = newQQ = "";}
        if(newMobile.equals(oldMobile)) {oldMobile=newMobile="";}
        if(newEmail.equals(oldEmail)) {oldEmail = newEmail="";}
        if(newVipStatus.equals(oldVipStatus)) {oldVipStatus = newVipStatus = "";}
        if(newStatus.equals(oldStatus)) {oldStatus = newStatus = "";}
        if(session.get("admin-authcode")==null){
        	renderFailedJson(ReturnCode.INVALID_PRIVILEGE);
        }
        String adminAccount = AdminUser.findByIdWichCache(Long.valueOf(Long.parseLong(session.get("admin-authcode")))).name;
    	String message = MixHelper.format("用户昵称{};QQ{}--{};电话{}--{};邮箱{}--{};等级:{}--{};状态:{}--{}", 
    											oldUser.nick,
    											oldQQ,newQQ,
    											oldMobile,newMobile,
    											oldEmail,newEmail,
    											oldVipStatus,newVipStatus,
    											oldStatus,newStatus);
    	
    	AdminOperatorLog.insert(adminAccount, LogType.CHANGE_USER, message);
    	//清理缓存
    	Cache.safeDelete(CacheType.USER_INFO.getKey(uid));
    	
        user.save(true);
        
        renderSuccessJson();
    }
    
    
    
    
    /**
     * 
     * 会员延期
     *
     * @param uid
     * @param memberDelayDuration
     * @since  v2.0
     * @author Mark Xu
     * @created 2015-3-9 上午11:19:31
     */
    public static void memberDelay(long uid, int memberDelayDuration) {
    	User user = User.findById(uid);
    	if(user.dueTime==null||user.dueTime.getTime() <= (new Date()).getTime()) {
    		user.dueTime = DateTime.now().plusDays(memberDelayDuration).toDate();
    	}else {
    		user.dueTime=DateTime.parse(new SimpleDateFormat("yyyy-MM-dd").format(user.dueTime)).plusDays(memberDelayDuration).toDate();
    	}
    	user.delayUserMember(uid,user.dueTime);
    	if(session.get("admin-authcode")==null){
    		renderFailedJson(ReturnCode.ES_SEARCH_RETURN_EMPTY);
    	}
    	MemberChargeRecord r = MemberChargeRecord.newInstance(user.id, 2).userType(user.type).amount(0);
    	r.memberDelay(r, memberDelayDuration);
    	String adminAccount = AdminUser.findByIdWichCache(Long.valueOf(Long.parseLong(session.get("admin-authcode")))).name;
    	String message = MixHelper.format("给用户{}延期会员{}天", 
    											user.nick,
    											memberDelayDuration);
    	AdminOperatorLog.insert(adminAccount, LogType.CHANGE_USER, message);
    	//清理缓存
		Cache.safeDelete(CacheType.USER_INFO.getKey(uid));
		
    	renderSuccessJson();
    }
    
    /***
     * 
     * 更改客服对接商家的备注信息
     *
     * @param message
     * @since  v2.0
     * @author Mark Xu
     * @created 2015-2-11 下午4:58:46
     */
    public static void editDockingMessage(String dockingMessage, long uid) {
    	handleWrongInput(false);
    	User user = new User();
    	user.dockingMessage = dockingMessage;
    	long adminId = AdminUser.findByIdWichCache(Long.valueOf(Long.parseLong(session.get("admin-authcode")))).id;
    	user.save(adminId, dockingMessage, uid);
    	renderSuccessJson();
    }
    
    /**
     * 
     * 取消客服与客户的对接
     *
     * @param uid
     * @since  v2.0
     * @author Mark Xu
     * @created 2015-2-11 下午6:37:51
     */
    public static void cancelDocking(long uid) {
    	handleWrongInput(false);
    	User user = new User();
    	user.save(0, null, uid);
    	renderSuccessJson();
    }
    
    /**
     * 
     * 获取用户账户余额
     *
     * @param uid
     * @since  v1.4
     * @author youblade
     * @created 2014年11月20日 下午8:28:18
     */
    public static void balance(@Required @Min(1) long uid) {
        handleWrongInput(false);
        
        User user = User.findByIdWichCache(uid);
        Map<String, Long> map = Maps.newHashMapWithExpectedSize(2);
        map.put("ingot", user.ingot);
        map.put("pledge", user.pledge);
        renderJson(map);
    }
    
    public class FundAccountSearchVo extends Page{
        public String nick;
        public String no;
        public String name;
        public PayPlatform type;
    }
    /**
     * 
     * 后台管理->资金账户管理：获取分页列表数据.
     *
     * @param vo
     * @since  v1.4
     * @author youblade
     * @created 2014年11月21日 下午2:25:36
     */
    public static void listFundAccount(@Required FundAccountSearchVo vo) {
        if (vo != null) {
            validation.min(vo.pageNo, 1);
            validation.required(vo.pageNo);
            validation.required(vo.pageSize);
        }
        handleWrongInput(false);

        Page<FundAccount> page = FundAccount.findByPage(vo);
       
        renderPageJson(page.items, page.totalCount);
    }
    
    /**
     * 
     * 后台管理->资金账户管理：修改用户的财付通账号
     *
     * @param account
     * @since  v1.4
     * @author youblade
     * @created 2014年11月21日 下午3:47:28
     */
    public static void saveFundAccount(@Required FundAccount account) {
        validation.required(account.id);
        validation.required(account.name);
        validation.required(account.no);
        validation.required(account.type);
        handleWrongInput(false);
        
        account.save();
        renderSuccessJson();
    }
    
    /**
     * 
     * 用户管理->重置支付密码
     *
     * @param uid
     * @since  v1.7
     * @author youblade
     * @created 2014年12月3日 下午3:28:48
     */
    public static void resetPayPassword(@Required @Min(1) long uid) {
        handleWrongInput(false);
        
        User user = User.instance(uid);
        user.payPassword = StringUtils.EMPTY;
        user.save(false);
        renderSuccessJson();
    }
}
