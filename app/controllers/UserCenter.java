package controllers;

import java.util.List;
import java.util.Map;

import models.BuyerAccount;
import models.BuyerAccount2;
import models.BuyerAccount3;
import models.BuyerConfig;
import models.BuyerDepositRecord;
import models.BuyerTask;
import models.BuyerTask2;
import models.BuyerTask3;
import models.FundAccount;
import models.MemberChargeRecord;
import models.Notice;
import models.SellerConfig;
import models.Shop2;
import models.Shop3;
import models.User.VipStatus;
import models.UserIngotRecord;
import models.Notice.Role;
import models.PayTradeLog;
import models.Region;
import models.SellerPledgeRecord.PledgeAction;
import models.Shop;
import models.Task;
import models.TrialShop;
import models.User;
import models.User.UserStatus;
import models.User.UserType;
import models.UserWithdrawRecord;
import models.mappers.BuyerAccountMapper;
import models.mappers.BuyerAccountMapper2;
import models.mappers.BuyerAccountMapper3;
import models.mappers.BuyerTaskMapper;
import models.mappers.SellerPledgeRecordMapper;
import models.mappers.ShopMapper;
import models.mappers.ShopMapper2;
import models.mappers.ShopMapper3;
import models.mappers.TaskMapper;
import models.mappers.TrialShopMapper;
import models.mappers.UserIngotRecordMapper;
import models.mappers.fund.BuyerDepositRecordMapper;
import models.marketing.TaskRewardLog;
import models.marketing.UserInvitedRecord;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.BooleanUtils;
import org.apache.commons.lang.Validate;
import org.apache.ibatis.session.SqlSession;
import org.joda.time.DateTime;
import org.joda.time.Days;
import org.joda.time.LocalDate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import play.data.validation.Required;
import play.mvc.With;
import vos.BuyerAccountSearchVo;
import vos.BuyerAccountSearchVo.ExamineStatus;
import vos.BuyerAccountSearchVo2;
import vos.BuyerAccountSearchVo2.ExamineStatus2;
import vos.BuyerAccountSearchVo3;
import vos.BuyerAccountSearchVo3.ExamineStatus3;
import vos.BuyerTaskSearchVo;
import vos.BuyerTaskSearchVo3;
import vos.Page;
import vos.PersonalInfoVo;
import vos.TaskRewardLogVo;
import vos.TaskSearchVo;
import vos.UserSearchVo;

import com.aton.base.BaseController;
import com.aton.base.secure.Secure;
import com.aton.config.BizConstants;
import com.aton.config.CacheType;
import com.aton.config.ReturnCode;
import com.aton.db.SessionFactory;
import com.aton.util.CacheUtil;
import com.aton.util.QnCloudUtil;
import com.aton.util.StringUtils;
import com.google.common.base.Strings;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;

import controllers.MoneyManage.WithdrawSearchVo;
import domain.UserMoneyStats;
import enums.Platform;
import enums.Platform2;
import enums.Platform3;
import enums.TaskStatus;
import enums.pay.KQpayPlatform;
import enums.pay.PayPlatform;
import enums.pay.TradeType;

@With(Secure.class)
public class UserCenter extends BaseController{

    private static final Logger log = LoggerFactory.getLogger(UserCenter.class);
    
    public static void result(String title, String msg) {
        renderArgs.put("errTitle", title);
        renderArgs.put("errMsg", msg);
        render("result.html");
    }
    
    /**
     * 
     * 个人中心页面.
     *
     * @since  0.1
     * @author youblade
     */
    public static void index() {

        User user = getCurrentUser();
        SqlSession ss = SessionFactory.getSqlSession();
        try {
            /*
             *  【商家】
             */
            if(user.isSeller()){
                long lockedPledage = 0;
                //获取押金被冻结的商家返款任务
                List<Task> list = Task.findlockedPledgeTask(user.id);
                
                // 返款冻结押金：任务冻结押金
                if(!list.isEmpty()){
                    for(Task t:list){
                        lockedPledage += t.pledge*(t.totalOrderNum-t.finishedCount);
                    }
                }                
                renderArgs.put("lockedPledage", lockedPledage);
                
                // 平台返款冻结押金：取该商家所有已支付未完成的任务，分别减去每个任务未完成的子任务押金，即为当前被冻结的押金
                TaskSearchVo vo = TaskSearchVo
                    .newInstance("t.pledge,total_order_num,finished_count")
                    .sellerId(user.id)
                    .sysRefund(true)
                    .statusIn(TaskStatus.WAIT_EXAMINE, TaskStatus.EXAMINING, TaskStatus.PUBLISHED,
                        TaskStatus.WAIT_PUBLISH).pageSize(500);
                List<Task> unfinishedTasks = ss.getMapper(TaskMapper.class).selectSimple(vo);
                long sysLockedPledage = 0;
                if(CollectionUtils.isNotEmpty(unfinishedTasks)){
                    for (Task task : unfinishedTasks) {
                        sysLockedPledage += task.pledge * (task.totalOrderNum - task.finishedCount);
                    }
                }
                renderArgs.put("sysLockedPledage", sysLockedPledage);
                render();
            }
            
            /*
             *  【买手】
             */
            // 获取待返款任务数
            BuyerTaskMapper mapper = ss.getMapper(BuyerTaskMapper.class);
            // 涉及“已支付”~“已退款”中的所有状态
            List<TaskStatus> statuses = Lists.newArrayList(TaskStatus.WAIT_SEND_GOODS, TaskStatus.WAIT_EXPRESS_PRINT,
                TaskStatus.EXPRESS_PRINT, TaskStatus.WAIT_CONFIRM, TaskStatus.WAIT_REFUND,TaskStatus.WAIT_SELLER_CONFIRM_SYS_REFUND,
                TaskStatus.WAIT_BUYER_CONFIRM_SYS_REFUND,TaskStatus.WAIT_BUYER_CONFIRM_EDITED_SYS_REFUND,
                TaskStatus.BUYER_REJECT_SYS_REFUND,TaskStatus.WAIT_SYS_REFUND);
            PersonalInfoVo vo = mapper.selectBuyerPaidFeeInfo(user.id, statuses);
            renderArgs.put("waitRefundTaskCount", vo.taskCount);
            renderArgs.put("waitRefundAmount", vo.sumPaidFee);
            
            // 待发放佣金、冻结金币
            // 涉及“已完成”、“已取消”之外的全部状态
            statuses = Lists.newArrayList(TaskStatus.FINISHED, TaskStatus.CANCLED);
            vo = mapper.selectBuyerExcutingInfo(user.id, statuses);
            renderArgs.put("unreceivedRewardIngot", vo.sumRewardIngot);
            renderArgs.put("lockedIngot", vo.getLockedIngot());
            
            /*
             *  垫付本金统计
             */
            // 统计维度：商家返款、平台返款
            statuses = Lists.newArrayList(TaskStatus.RECIEVED, TaskStatus.WAIT_PAY,
                TaskStatus.CANCLED, TaskStatus.FINISHED);
            List<UserMoneyStats> buyerDepositStats = mapper.sumPaidFeeByBuyerIdAndStatusNotIn(user.id, statuses);
            
            UserMoneyStats lockedSysRefundDeposit = new UserMoneyStats();
            UserMoneyStats lockedSellerRefundDeposit = new UserMoneyStats();
            for (UserMoneyStats stats : buyerDepositStats) {
                // 平台返款
                if(BooleanUtils.isTrue(stats.sysRefund)){
                    lockedSysRefundDeposit.amount = stats.amount;
                    lockedSysRefundDeposit.count = stats.count;
                    continue;   
                }
                // 商家直接返款
                lockedSellerRefundDeposit.amount = stats.amount;
                lockedSellerRefundDeposit.count = stats.count;
            }
            renderArgs.put("lockedSysRefundDeposit", lockedSysRefundDeposit);
            renderArgs.put("lockedSellerRefundDeposit", lockedSellerRefundDeposit);
            // 垫付本金总计
            renderArgs.put("totalLockedDepositAmt", lockedSysRefundDeposit.amount + lockedSellerRefundDeposit.amount);
            
            // 统计待核实退款的任务，维度：商家返款、平台返款
            statuses = Lists.newArrayList(TaskStatus.WAIT_BUYER_CONFIRM_EDITED_SYS_REFUND, TaskStatus.WAIT_BUYER_CONFIRM_SYS_REFUND,
                TaskStatus.REFUNDING);
            List<UserMoneyStats> buyerWaitConfirmDepositStats = mapper.sumPaidFeeWaitConfirmByBuyerIdAndStatusIn(user.id,statuses);
            
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
            renderArgs.put("watiConfirmSysRefundDeposit", watiConfirmSysRefundDeposit);
            renderArgs.put("watiConfirmSellerRefundDeposit", watiConfirmSellerRefundDeposit);
            
            // 可提现
            long withDepositAmt = 0;
            BuyerDepositRecord depositRecord = ss.getMapper(BuyerDepositRecordMapper.class).selectLastRecord(user.id);
            if (depositRecord != null) {
                withDepositAmt = depositRecord.balance;
            }
            renderArgs.put("withDepositAmt", withDepositAmt);
            
        } finally {
            ss.close();
        }

        //
        render();
    }
    
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
     *  用户中心->绑定店铺.
     *
     * @since  0.1
     * @author youblade
     * @created 2014年7月16日 下午2:22:29
     */
    public static void shop(){
        renderArgs.put("platforms", Platform.values());
        
        //取得省级数据
        renderArgs.put("regions", Region.findRoot());
        render();
    }
    
    
    /**
     * 
     *  用户中心->绑定店铺.
     *
     * @since  0.1
     * @author youblade
     * @created 2014年7月16日 下午2:22:29
     */
    public static void shop2(){
        renderArgs.put("platforms2", Platform2.values());
        //取得省级数据
        renderArgs.put("regions", Region.findRoot());
        render();
    }
    
    /**
     *  用户中心->绑定流量店铺.
     */
    public static void shop3(){
        renderArgs.put("platforms3", Platform3.values());
        renderArgs.put("regions", Region.findRoot()); //取得省级数据
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
	 * 用户中心->绑定推广：获取指定平台下已绑定的推广.
	 * 
	 * @since 0.1
	 * @author youblade
	 * @created 2014年7月16日 下午2:22:14
	 */
	public static void listShops2(@Required Platform2 platform) {
		handleWrongInput(false);

		
		long sellerId = getCurrentUser().id;
		List<Shop2> list = Shop2.findByPlatformAndSellerId(platform, sellerId);
		/*System.out.println("|size======="+list.size());
		System.out.println("|||||||||||||+"+list.toString());
		System.out.println("===========");
		System.out.println("===========");
		System.out.println("===========");
		System.out.println("===========");
		System.out.println("===========");
		System.out.println("===========");
		System.out.println("===========");
		System.out.println("===========");*/
		renderJson(list);
	}
	/**
	 * 用户中心->绑定浏览单：获取指定平台下已绑定的浏览单.
	 */
	public static void listShops3(@Required Platform3 platform) {
		handleWrongInput(false);

		long sellerId = getCurrentUser().id;
		List<Shop3> list = Shop3.findByPlatformAndSellerId(platform, sellerId);
		renderJson(list);
	}
	
	
	
	public static void shopDetail(@Required long id) {
		handleWrongInput(false);
		
		long sellerId = getCurrentUser().id;
		Shop shop = Shop.selectById(id);
		if(shop==null){
		    renderFailedJson(ReturnCode.BIZ_LIMIT);
		}
		shop.url = shop.url.length()<=50?shop.url:StringUtils.substring(shop.url, 0, shop.url.length()/2)+ "...";
		if(shop.sellerId!=sellerId) {
			renderFailedJson(ReturnCode.BIZ_LIMIT);
		}else {
			renderJson(shop);
		}
	}
	
	
	public static void shopDetail2(@Required long id) {
		handleWrongInput(false);
		
		long sellerId = getCurrentUser().id;
		
		System.out.println("------"+id);
		System.out.println("---id---"+id);
		System.out.println("---id---"+id);
		System.out.println("---id---"+id);
		
		System.out.println("------"+id);
		System.out.println("------"+id);
		
		Shop2 shop = Shop2.selectById(id);
		if(shop==null){
		    renderFailedJson(ReturnCode.BIZ_LIMIT);
		}
		//shop.url = shop.url.length()<=50?shop.url:StringUtils.substring(shop.url, 0, shop.url.length()/2)+ "...";
		if(shop.sellerId!=sellerId) {
			renderFailedJson(ReturnCode.BIZ_LIMIT);
		}else {
			renderJson(shop);
		}
	}
	
	
	
	
	public static void shopDetail3(@Required long id) {
		handleWrongInput(false);
		long sellerId = getCurrentUser().id;
		Shop3 shop = Shop3.selectById(id);
		if(shop==null){
		    renderFailedJson(ReturnCode.BIZ_LIMIT);
		}
		shop.url = shop.url.length()<=50?shop.url:StringUtils.substring(shop.url, 0, shop.url.length()/2)+ "...";
		if(shop.sellerId!=sellerId) {
			renderFailedJson(ReturnCode.BIZ_LIMIT);
		}else {
			renderJson(shop);
		}
	}
	
	
	/**
	 * 
	 * 获取某个用户的VIP等级
	 *
	 * @since  v2.0
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
	 * @since  v0.1
	 * @author moloch
	 * @created 2014-7-29 上午9:52:30
	 */
	public static void region(@Required int id){
		handleWrongInput(false);
		
		List<Region> list = Region.findByParentId(id);
		renderJson(list);
	}
	
    /**
     * 
     *  用户中心->绑定店铺：绑定新店铺.
     *
     * @since  0.1
     * @author youblade
     * @created 2014年7月16日 下午2:24:09
     */
    public static void addShop(@Required Shop shop){
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
            if (Platform.TAOBAO == shop.platform || Platform.TMALL == shop.platform) {
                validation.required(shop.nick);
            } else {
                //validation.required(shop.name);
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
//        if (!shop.validateUrl()) {
//        	System.out.println("2");
//        	renderFailedJson(ReturnCode.OP_BIZ_LIMIT, "店铺网址与所选平台不一致！");
//        }
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
            //后台绑定店铺长度限制
            if (shop.url.length()>300) {
            	renderFailedJson(ReturnCode.OP_BIZ_LIMIT, "店铺url过长！");
            }
            // 检查用户在该平台已绑定的店铺是否已经达到上限
            User user = getCurrentUser();
            shop.sellerId = user.id;
            int count = mapper.countByPlatformAndSellerId(shop);
            VipStatus vipStatus = User.findById(getCurrentUser().id).vipStatus;
            int maxCount = 3;
            if(vipStatus==VipStatus.VIP1) {
    			maxCount = 5;
    		}else if(vipStatus==VipStatus.VIP2){
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
    
    
    
    
    
    
    
    
    /**
     * 
     *  用户中心->绑定推广：绑定新推广.
     *
     * @since  0.1
     * @author youblade
     * @created 2014年7月16日 下午2:24:09
     */
    public static void addShop2(@Required Shop2 shop2){
    	if (shop2 != null) {
            validation.required(shop2.nick);
            /*validation.required(shop2.url);*/
            validation.required(shop2.address);
            validation.required(shop2.platform2);
            validation.required(shop2.sellerName);
            validation.required(shop2.mobile);
            validation.required(shop2.street);
            //validation.required(shop2.branch);
            log.info("---------------------ww----");
            shop2.nick = StringUtils.trim(shop2.nick);
            /*shop2.url = StringUtils.trim(shop2.url);*/
            shop2.sellerName = StringUtils.trim(shop2.sellerName);
            shop2.mobile = StringUtils.trim(shop2.mobile);
            shop2.street = StringUtils.trim(shop2.street);
            //shop2.branch = StringUtils.trim(shop2.branch);
        }
    	System.out.println("321321");
        handleWrongInput(false);
        
        System.out.println("1");
        /*
         * 逻辑比较简单暂时就不做那么多封装了，直接写在view层
         */
        SqlSession ss = SessionFactory.getSqlSession();
        try {
            // 检查该店铺是否已绑定过（店铺名）
            ShopMapper2 mapper = ss.getMapper(ShopMapper2.class);
            if (mapper.selectExists(shop2) != null) {
            	renderFailedJson(ReturnCode.OP_BIZ_LIMIT, "该店铺已被绑定过！");
            }
            //后台绑定店铺长度限制
            /*if (shop2.url.length()>300) {
            	renderFailedJson(ReturnCode.OP_BIZ_LIMIT, "店铺url过长！");
            }*/
            // 检查用户在该平台已绑定的店铺是否已经达到上限
            User user = getCurrentUser();
            shop2.sellerId = user.id;
            
            
            int count = mapper.countByPlatformAndSellerId(shop2);
            VipStatus vipStatus = User.findById(getCurrentUser().id).vipStatus;      //VIP状态
            int maxCount = 3;
            if(vipStatus==VipStatus.VIP1) {
    			maxCount = 5;
    		}else if(vipStatus==VipStatus.VIP2){
    			maxCount = 10;
    		}
            if (vipStatus != VipStatus.VIP3 && count >= maxCount) {
            	 System.out.println("4");
            	renderFailedJson(ReturnCode.OP_BIZ_LIMIT, "店铺数量已达到上限！");
            }
            
            // 保存
            shop2.createTime = DateTime.now().toDate();
            shop2.sellerId = user.id;
            mapper.insert(shop2);
            System.out.println("5");
        } finally {
        	ss.commit(true);
            ss.close();
        }
        renderSuccessJson();
    }
    
    
    
    
    
    
    
    /**
     * 
     *  用户中心->绑定店铺：绑定新店铺.
     *
     * @since  0.1
     * @author youblade
     * @created 2014年7月16日 下午2:24:09
     */
    public static void addShop3(@Required Shop3 shop){
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
            if (Platform3.TAOBAO == shop.platform || Platform3.TMALL == shop.platform) {
                validation.required(shop.nick);
            } else {
                //validation.required(shop.name);
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
        /*
         * 逻辑比较简单暂时就不做那么多封装了，直接写在view层
         */
        SqlSession ss = SessionFactory.getSqlSession();
        try {
            // 检查该店铺是否已绑定过（店铺名）
            ShopMapper3 mapper = ss.getMapper(ShopMapper3.class);
            if (mapper.selectExists(shop) != null) {
            	renderFailedJson(ReturnCode.OP_BIZ_LIMIT, "该店铺已被绑定过！");
            }
            //后台绑定店铺长度限制
            if (shop.url.length()>300) {
            	renderFailedJson(ReturnCode.OP_BIZ_LIMIT, "店铺url过长！");
            }
            // 检查用户在该平台已绑定的店铺是否已经达到上限
            User user = getCurrentUser();
            shop.sellerId = user.id;
            int count = mapper.countByPlatformAndSellerId(shop);
            VipStatus vipStatus = User.findById(getCurrentUser().id).vipStatus;
            int maxCount = 3;
            if(vipStatus==VipStatus.VIP1) {
    			maxCount = 5;
    		}else if(vipStatus==VipStatus.VIP2){
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
    
    
    
    
    
    
    
    
    //更改店铺信息
    public static void modifyShop(@Required String address, @Required String sellerName,@Required String mobile,@Required String street,@Required String branch,@Required long id){
    	
    	
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
    
    

    //更改推广信息
    public static void modifyShop2(@Required String address, @Required String sellerName,@Required String mobile,@Required String street,@Required long id){
    	
    	
        Shop2 shop = new Shop2();
            
        shop.sellerName = StringUtils.trim(sellerName);
        shop.mobile = StringUtils.trim(mobile);
        shop.street = StringUtils.trim(street);
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
            ShopMapper2 mapper = ss.getMapper(ShopMapper2.class);
            
            mapper.update(shop);
        } finally {
        	ss.commit(true);
            ss.close();
        }
        renderSuccessJson();
    }
    
    
    
    //更改店铺信息
    public static void modifyShop3(@Required String address, @Required String sellerName,@Required String mobile,@Required String street,@Required String branch,@Required long id){
        Shop3 shop = new Shop3();
        shop.sellerName = StringUtils.trim(sellerName);
        shop.mobile = StringUtils.trim(mobile);
        shop.street = StringUtils.trim(street);
        shop.branch = StringUtils.trim(branch);
        shop.id = id;
        shop.address = address;
        shop.modifyTime = DateTime.now().toDate();
        handleWrongInput(false);
        SqlSession ss = SessionFactory.getSqlSession();
        try {
            // 检查该店铺是否已绑定过（店铺名）
            ShopMapper3 mapper = ss.getMapper(ShopMapper3.class);
            mapper.update(shop);
        } finally {
        	ss.commit(true);
            ss.close();
        }
        renderSuccessJson();
    }
    
    
    /**
     * 
     * @Description 返回试用店铺和省份信息
     * @author 抽离
     */
    public static void trialshop(){
        renderArgs.put("platforms", Platform.values());
        
        //取得省级数据
        renderArgs.put("regions", Region.findRoot());
        render();
    }



	/**
	 * 
	 * @Description 返回已经绑定的试用店铺
	 * @param platform 所选平台
	 * @author 抽离
	 */
	public static void listTrialShops(@Required Platform platform) {
		handleWrongInput(false);

		long sellerId = getCurrentUser().id;
		List<TrialShop> list = TrialShop.findByPlatformAndSellerId(platform, sellerId);
		renderJson(list);
	}





/**
	 * 
	 * @Description 修改绑定试用店铺加载对应已有数据
	 * @param id
	 * @author 抽离
	 */
	public static void trialShopDetail(@Required long id) {
		handleWrongInput(false);
		
		long sellerId = getCurrentUser().id;
		TrialShop shop = TrialShop.selectById(id);
		if(shop==null){
		    renderFailedJson(ReturnCode.BIZ_LIMIT);
		}
		shop.url = shop.url.length()<=50?shop.url:StringUtils.substring(shop.url, 0, shop.url.length()/2)+ "...";
		if(shop.sellerId!=sellerId) {
			renderFailedJson(ReturnCode.BIZ_LIMIT);
		}else {
			renderJson(shop);
		}
	}



    
    /**
     * 
     * @Description 绑定新的试用店铺
     * @param shop
     */
    public static void addTrialShop(@Required TrialShop shop){
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
            if (Platform.TAOBAO == shop.platform || Platform.TMALL == shop.platform) {
                validation.required(shop.nick);
            } else {
                //validation.required(shop.name);
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
//        if (!shop.validateUrl()) {
//        	System.out.println("2");
//        	renderFailedJson(ReturnCode.OP_BIZ_LIMIT, "店铺网址与所选平台不一致！");
//        }
        /*
         * 逻辑比较简单暂时就不做那么多封装了，直接写在view层
         */
        SqlSession ss = SessionFactory.getSqlSession();
        try {
            // 检查该店铺是否已绑定过（店铺名）
            TrialShopMapper mapper = ss.getMapper(TrialShopMapper.class);
            if (mapper.selectExists(shop) != null) {
            	renderFailedJson(ReturnCode.OP_BIZ_LIMIT, "该店铺已被绑定过！");
            }
            //后台绑定店铺长度限制
            if (shop.url.length()>300) {
            	renderFailedJson(ReturnCode.OP_BIZ_LIMIT, "店铺url过长！");
            }
            // 检查用户在该平台已绑定的店铺是否已经达到上限
            User user = getCurrentUser();
            shop.sellerId = user.id;
            int count = mapper.countByPlatformAndSellerId(shop);
            VipStatus vipStatus = User.findById(getCurrentUser().id).vipStatus;
            int maxCount = 3;
            if(vipStatus==VipStatus.VIP1) {
    			maxCount = 5;
    		}else if(vipStatus==VipStatus.VIP2){
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
    

  
  /**
   * 
   * @Description 修改试用店铺信息
   * @param address
   * @param sellerName
   * @param mobile
   * @param street
   * @param branch
   * @param id
   */
 public static void modifyTrialShop(@Required String address, @Required String sellerName,@Required String mobile,@Required String street,@Required String branch,@Required long id){
	 TrialShop shop = new TrialShop();
            
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
        	TrialShopMapper mapper = ss.getMapper(TrialShopMapper.class);
            
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
                    renderArgs.put("bankNoLastNum", fund.no.substring(fund.no.length() - 5, fund.no.length()));
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
     * @since  v1.6
     * @author playlaugh
     * @created 2014年11月25日 下午4:41:51
     */
    public static void withdraw() {
        
        User user = getCurrentUser();
        // 取得财付通、支付宝、银行数据
        renderArgs.put("tenpay", FundAccount.findTenpay(user.id));
        renderArgs.put("alipay", FundAccount.findAlipay(user.id));
        renderArgs.put("bank",FundAccount.findBank(user.id));
        List<KQpayPlatform> lists = Lists.newArrayList(KQpayPlatform.values());
        lists.remove(KQpayPlatform.POST);
        renderArgs.put(BizConstants.PAY_PLATFORMS, lists);
        renderArgs.put("regions", Region.findRoot());
        
        if(user.isSeller()){
            renderArgs.put("isSeller", true);
            render();
        }
        
        // 设置本月已提现次数
        WithdrawSearchVo svo = WithdrawSearchVo.newInstance().userId(user.id);
        int withdrawCount = BizConstants.WITHDRAW_MONTH_COUNT - UserWithdrawRecord.countThisMonthApply(svo);
        renderArgs.put("countThisMonthApply", Math.max(0, withdrawCount));
        
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
        renderArgs.put("withDepositAmt", withDepositAmt);
        render();
    }
    
    /**
     * 
     * 商家个性化配置页面
     *
     * @since  v2.0
     * @author Mark Xu
     * @created 2015-4-10 上午11:35:50
     */
    public static void sellerConfig(){
    	
    	User user = getCurrentUser();
    	SellerConfig config = SellerConfig.findBySellerId(user.id);
    	if(config==null) {
    		config = config.newInstance(0, 0, 0, 0);
    	}
    	render(config);
    }
    
    /**
     * 
     * 买手个性化配置页面
     *
     * @since  v2.0
     * @author Mark Xu
     * @created 2015-4-28 下午4:18:46
     */
    public static void buyerConfig() {
    	User buyer = getCurrentUser();
    	BuyerConfig config = BuyerConfig.findByBuyerId(buyer.id);
    	if(config == null) {
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
     * @since  v2.0
     * @author Mark Xu
     * @created 2015-4-9 下午4:20:26
     */
    public static void limitMessage(@Required long sellerId) {
    	handleWrongInput(false);
    	SellerConfig config = SellerConfig.findBySellerId(sellerId);
    	Map map = Maps.newHashMapWithExpectedSize(4);
		if (config==null) {
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
     * @since  v2.0
     * @author Mark Xu
     * @created 2015-4-9 下午5:51:17
     */
    public static void modifyLimitMessage(@Required SellerConfig config) {
    	handleWrongInput(false);
    	
    	if(!isAdminOperate()) {
    		User user = getCurrentUser();
    		if(user!=null) {
        		config.sellerId = user.id;	
    		}else {
    			renderFailedJson(ReturnCode.WRONG_INPUT);
    		}
    	}
    	if(config.isNull(config.sellerId)) {
    		config.insert(config);
    	}else {
    		config.updateBySellerConfig(config);
    	}
    	renderSuccessJson();
    }
    
    /**
     * 
     * 更改买手配置信息
     *
     * @since  v2.0
     * @author Mark Xu
     * @created 2015-4-28 下午5:22:23
     */
    public static void modifyBuyerConfig(@Required BuyerConfig config) {
    	handleWrongInput(false);
    	
    	User user = getCurrentUser();
    	if(user!=null) {
    		config.buyerId = user.id;
    	}else {
    		renderFailedJson(ReturnCode.WRONG_INPUT);
    	}
    	//检测用户的VIP等级
    	if(user.vipStatus!=VipStatus.VIP2 && user.vipStatus!=VipStatus.VIP3 && user.vipStatus!=VipStatus.SPECIAL) {
    		renderFailedJson(ReturnCode.OP_BIZ_LIMIT, "VIP等级过低，请先通过充值会员达到指定的VIP等级");
    	}
    	if(config.isNull(config.buyerId)) {
    		config.insert(config);
    	}else {
    		config.updateByBuyerConfig(config);
    	}
    	renderSuccessJson();
    }
    
    /**
     * 
     * TODO 资金管理->获取退款/提现账号.
     *
     * @since  0.1
     * @author youblade
     * @created 2014年7月29日 下午5:42:05
     */
    public static void fundAccount(){
    	
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
		FundAccount fAccount = FundAccount.findByType(account.type, account.userId);
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
		if(account.type == PayPlatform.TENPAY){
    		if(FundAccount.findTenpay(user.id)!=null){
    		    renderFailedJson(ReturnCode.OP_BIZ_LIMIT);
    		}
		}else if(account.type == PayPlatform.ALIPAY){
		    if(FundAccount.findAlipay(user.id)!=null){
                renderFailedJson(ReturnCode.OP_BIZ_LIMIT);
            }
		}else{
		    if(FundAccount.findBank(user.id)!=null){
                renderFailedJson(ReturnCode.OP_BIZ_LIMIT);
            }
		    if(account.type.toString().equals(KQpayPlatform.POST.toString())) {
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
	 * 用户中心->买手账号.
	 */
    public static void buyerAccount(){
    	//平台数据 这里做了筛选 不显示天猫
    	List<Platform> pfs = Lists.newArrayList(Platform.values());
    	pfs.remove(Platform.TMALL);
    	renderArgs.put("platforms", pfs);
    	//取得省级地域数据
        renderArgs.put("regions", Region.findRoot());
    	
        render();
    }
    
    
    /**
	 * 用户中心->浏览单账号.
	 */
    public static void buyerAccount3(){
    	List<Platform3> pfs = Lists.newArrayList(Platform3.values());//平台数据 这里做了筛选 不显示天猫
    	pfs.remove(Platform3.TMALL);
    	renderArgs.put("platforms", pfs);
        renderArgs.put("regions", Region.findRoot());//取得省级地域数据
        render();
    }
    
	/**
	 * 
	 * 用户中心->买手推广号账号.
	 *
	 * @since  0.1
	 * @author youblade
	 * @created 2014年8月1日 下午12:47:49
	 */
    public static void buyerAccount2(){
    	List<Platform2> pfs = Lists.newArrayList(Platform2.values());
    	renderArgs.put("platforms", pfs);
    	//取得省级地域数据
        renderArgs.put("regions", Region.findRoot());
    	
        render();
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
	public synchronized static void saveBuyerAccount(@Required BuyerAccount account){
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
		handleWrongInput(false);

		// 将无关信息过滤为null
		account.nick = StringUtils.trimToNull(account.nick);
		account.mobile = StringUtils.trimToNull(account.mobile);
		account.consignee = StringUtils.trimToNull(account.consignee);
		account.state = StringUtils.trimToNull(account.state);
		account.city = StringUtils.trimToNull(account.city);
		// 三级行政区域有可能为空，将非空的修改为空值时不能将其设置为null，否则无法构造SQL条件
		account.region = StringUtils.trimToEmpty(account.region);
		account.address = StringUtils.trimToNull(account.address);
		
		User buyer = getCurrentUser();
        checkBeforeSaveBuyerAccount(account, buyer);
		
		// 买号改动需要审核
		account.status = ExamineStatus.WAIT_EXAMINE;
		account.userId = buyer.id;
		account.save();
		renderSuccessJson();
	}

	
	
	/**
	 * 
	 * 用户中心->买手账号->保存买号.
	 */
	public synchronized static void saveBuyerAccount3(@Required BuyerAccount3 account){
		validation.required("platform", account.platform);

		// 新添加买号时再检查，前端仅传参有改动的字段
		if (account != null && account.id <= 0) {
			validation.required("nick", account.nick);
			//validation.required("mobile", account.mobile);
			//validation.required("consignee", account.consignee);
			validation.required("state", account.state);
			validation.required("city", account.city);
			//validation.required("address", account.address);
		}
		handleWrongInput(false);

		// 将无关信息过滤为null
		account.nick = StringUtils.trimToNull(account.nick);
		account.mobile = StringUtils.trimToNull(account.mobile);
		account.consignee = StringUtils.trimToNull(account.consignee);
		account.state = StringUtils.trimToNull(account.state);
		account.city = StringUtils.trimToNull(account.city);
		// 三级行政区域有可能为空，将非空的修改为空值时不能将其设置为null，否则无法构造SQL条件
		account.region = StringUtils.trimToEmpty(account.region);
		account.address = StringUtils.trimToNull(account.address);

		User buyer = getCurrentUser();
		checkBeforeSaveBuyerAccount3(account, buyer);

		// 买号改动需要审核
		account.status = ExamineStatus3.WAIT_EXAMINE;
		account.userId = buyer.id;
		account.save();
		renderSuccessJson();
	}
	
	
	
	
	
	/**
	 * 
	 * 用户中心->买手账号->保存推广.
	 * 
	 * @since 0.1
	 * @author youblade
	 * @throws InterruptedException 
	 * @created 2014年8月1日 下午12:52:12
	 */
	public synchronized static void saveBuyerAccount2(@Required BuyerAccount2 account){
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
		handleWrongInput(false);

		// 将无关信息过滤为null
		account.nick = StringUtils.trimToNull(account.nick);
		account.mobile = StringUtils.trimToNull(account.mobile);
		account.consignee = StringUtils.trimToNull(account.consignee);
		account.state = StringUtils.trimToNull(account.state);
		account.city = StringUtils.trimToNull(account.city);
		// 三级行政区域有可能为空，将非空的修改为空值时不能将其设置为null，否则无法构造SQL条件
		account.region = StringUtils.trimToEmpty(account.region);
		account.address = StringUtils.trimToNull(account.address);
		
		User buyer = getCurrentUser();
        checkBeforeSaveBuyerAccount2(account, buyer);
		
		// 买号改动需要审核
		account.status = ExamineStatus2.WAIT_EXAMINE;
		account.userId = buyer.id;
		account.save();
		renderSuccessJson();
	}
	
	
	private static void checkBeforeSaveBuyerAccount2(BuyerAccount2 account, User buyer) {
        Validate.notNull(account.platform);
        
        boolean isAddNew = account.id <= 0;
        SqlSession ss = SessionFactory.getSqlSession();
        try {
            BuyerAccountMapper2 mapper = ss.getMapper(BuyerAccountMapper2.class);
            // 修改买号时检查记录是否存在、是否属于该用户
            if (!isAddNew) {
                BuyerAccount2 buyerAccount = mapper.selectById(account.id);
                if (buyerAccount == null || !buyerAccount.belongTo(buyer)) {
                    log.warn("Buyer={}-{} save buyerAccount forbidden:{}", buyer.id, buyer.nick, account.toJson());
                    renderFailedJson(ReturnCode.OP_BIZ_LIMIT);
                }
            }
            
            // 检查信息是否重复
            log.info("Buyer={}-{} save buyerAccount: Check Duplicate", buyer.id, buyer.nick);
            
            account.userId = buyer.id;
            BuyerAccount2 buyerAccount = mapper.selectForCheckDuplicate(account);
            if (buyerAccount != null) {
                log.warn("Buyer={}-{} save buyerAccount forbidden:{}", buyer.id, buyer.nick, account.toJson());
                renderFailedJson(ReturnCode.OP_BIZ_LIMIT);
            }
            //检查当前已绑定的买号数量（如果是合作商家，无需检查已绑定的买号数）
            if(User.findById(account.userId).vipStatus != VipStatus.SPECIAL) {
            	log.info("Buyer={}-{} save buyerAccount: Check binded count", buyer.id, buyer.nick);
            	
            	int count = mapper.count(BuyerAccountSearchVo2.newInstance().platform(account.platform)
            			.userId(account.userId));
            	if (isAddNew && count >= 2) {
            		log.warn("Buyer={}-{} save buyerAccount forbidden-More than 3 :{}", buyer.id, buyer.nick, account.toJson());
            		renderFailedJson(ReturnCode.BIZ_LIMIT);
            	}
            }
        } finally {
            ss.close();
        }
    }
	
	private static void checkBeforeSaveBuyerAccount3(BuyerAccount3 account, User buyer) {
		Validate.notNull(account.platform);

		boolean isAddNew = account.id <= 0;
		SqlSession ss = SessionFactory.getSqlSession();
		try {
			BuyerAccountMapper3 mapper = ss.getMapper(BuyerAccountMapper3.class);
			// 修改买号时检查记录是否存在、是否属于该用户
			if (!isAddNew) {
				BuyerAccount3 buyerAccount = mapper.selectById(account.id);
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
			BuyerAccount3 buyerAccount = mapper.selectForCheckDuplicate(account);
			if (buyerAccount != null) {
				log.warn("Buyer={}-{} save buyerAccount forbidden:{}",
						buyer.id, buyer.nick, account.toJson());
				renderFailedJson(ReturnCode.OP_BIZ_LIMIT);
			}
			// 检查当前已绑定的买号数量（如果是合作商家，无需检查已绑定的买号数）
			if (User.findById(account.userId).vipStatus != VipStatus.SPECIAL) {
				log.info("Buyer={}-{} save buyerAccount: Check binded count",
						buyer.id, buyer.nick);

				int count = mapper.count(BuyerAccountSearchVo3.newInstance()
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

	
	
    private static void checkBeforeSaveBuyerAccount(BuyerAccount account, User buyer) {
        Validate.notNull(account.platform);
        
        boolean isAddNew = account.id <= 0;
        SqlSession ss = SessionFactory.getSqlSession();
        try {
            BuyerAccountMapper mapper = ss.getMapper(BuyerAccountMapper.class);
            // 修改买号时检查记录是否存在、是否属于该用户
            if (!isAddNew) {
                BuyerAccount buyerAccount = mapper.selectById(account.id);
                if (buyerAccount == null || !buyerAccount.belongTo(buyer)) {
                    log.warn("Buyer={}-{} save buyerAccount forbidden:{}", buyer.id, buyer.nick, account.toJson());
                    renderFailedJson(ReturnCode.OP_BIZ_LIMIT);
                }
            }
            
            // 检查信息是否重复
            log.info("Buyer={}-{} save buyerAccount: Check Duplicate", buyer.id, buyer.nick);
            
            account.userId = buyer.id;
            BuyerAccount buyerAccount = mapper.selectForCheckDuplicate(account);
            if (buyerAccount != null) {
                log.warn("Buyer={}-{} save buyerAccount forbidden:{}", buyer.id, buyer.nick, account.toJson());
                renderFailedJson(ReturnCode.OP_BIZ_LIMIT);
            }
            //检查当前已绑定的买号数量（如果是合作商家，无需检查已绑定的买号数）
            if(User.findById(account.userId).vipStatus != VipStatus.SPECIAL) {
            	log.info("Buyer={}-{} save buyerAccount: Check binded count", buyer.id, buyer.nick);
            	
            	int count = mapper.count(BuyerAccountSearchVo.newInstance().platform(account.platform)
            			.userId(account.userId));
            	if (isAddNew && count >= 2) {
            		log.warn("Buyer={}-{} save buyerAccount forbidden-More than 3 :{}", buyer.id, buyer.nick, account.toJson());
            		renderFailedJson(ReturnCode.BIZ_LIMIT);
            	}
            }
        } finally {
            ss.close();
        }
    }

	/**
	 * 
	 * 获取买手的购买账号.
	 */
	public static void listBuyerAccounts(Platform platform, boolean receive) {
		handleWrongInput(false);
		
		// 淘宝、天猫数据目前不分开
		if (platform == Platform.TMALL) {
			platform = Platform.TAOBAO;
		}
		
        BuyerAccountSearchVo vo = BuyerAccountSearchVo.newInstance().platform(platform).userId(getCurrentUser().id);
		
		// 接手任务时仅返回可供使用的买号（每个买号每天限制5个任务）
        if (receive) {
            vo.status(ExamineStatus.EXAMINED);
            renderJson(BuyerAccount.findForTakingTask(vo));
        }

		List<BuyerAccount> list = BuyerAccount.findList(vo);
		renderJson(list);
	}
	
	
	/**
	 * 获取买手的购买账号.
	 */
	public static void listBuyerAccounts3(Platform3 platform, boolean receive) {
		handleWrongInput(false);

		// 淘宝、天猫数据目前不分开
		if (platform == Platform3.TMALL) {
			platform = Platform3.TAOBAO;
		}

		BuyerAccountSearchVo3 vo = BuyerAccountSearchVo3.newInstance()
				.platform(platform).userId(getCurrentUser().id);
		// 接手任务时仅返回可供使用的买号（每个买号每天限制5个任务）
		if (receive) {
			vo.status(ExamineStatus3.EXAMINED);
			renderJson(BuyerAccount3.findForTakingTask(vo));
		}

		List<BuyerAccount3> list = BuyerAccount3.findList(vo);
		// 封掉的买号不显示出来，只显示2个
		// if(list.size()==3)
		// for (BuyerAccount buyerAccount : list) {
		// if(buyerAccount.status==ExamineStatus.NOT_PASS){
		// list.remove(buyerAccount);
		// break;
		// }
		// }
		renderJson(list);
	}
	
	
	/**
	 * 
	 * 获取买手的购买账号.
	 * 
	 * @author yqc
	 * @created 2016-8-9
	 */
	public static void listBuyerAccounts2(Platform2 platform, boolean receive) {
		handleWrongInput(false);
		BuyerAccountSearchVo2 vo = BuyerAccountSearchVo2.newInstance().platform(platform).userId(getCurrentUser().id);
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
	 * 修改买手买号的排序数
	 *
	 * @since  v2.0
	 * @author Mark Xu
	 * @created 2015-4-22 下午1:56:41
	 */
	public static void modifyOrderNumber(@Required long id,@Required long orderNumber) {
		handleWrongInput(false);
		
		User u = getCurrentUser();
		if(u==null || !BuyerAccount.includeAccount(id, u.id)) {
			renderFailedJson(ReturnCode.BIZ_LIMIT, "无效买号");
		}
		BuyerAccount.modifyOrderNumber(id, orderNumber);
		renderSuccessJson();
	}
	
	
	
	/**
	 * 
	 * 修改买手买号的排序数
	 *
	 * @since  v2.0
	 * @author Mark Xu
	 * @created 2015-4-22 下午1:56:41
	 */
	public static void modifyOrderNumber2(@Required long id,@Required long orderNumber) {
		handleWrongInput(false);
		
		User u = getCurrentUser();
		if(u==null || !BuyerAccount2.includeAccount(id, u.id)) {
			renderFailedJson(ReturnCode.BIZ_LIMIT, "无效买号");
		}
		BuyerAccount2.modifyOrderNumber(id, orderNumber);
		renderSuccessJson();
	}
	
	/**
	 * 修改买手买号的排序数
	 */
	public static void modifyOrderNumber3(@Required long id,@Required long orderNumber) {
		handleWrongInput(false);
		User u = getCurrentUser();
		if(u==null || !BuyerAccount3.includeAccount(id, u.id)) {
			renderFailedJson(ReturnCode.BIZ_LIMIT, "无效买号");
		}
		BuyerAccount3.modifyOrderNumber(id, orderNumber);
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
		//查询与该买号有关的任务状态
		ba.hasTaskExecuting = BuyerTask.hasExecuteTask(btVo);
		ba.hasTask = BuyerTask.isTakeTask(btVo);
		
		renderJson(ba);
	}
	
	/**
	 * 
	 * 查看买号的任务状态
	 */
	public static void accountTaskStatus3(@Required BuyerTaskSearchVo3 btVo) {
		if (btVo != null) {
			validation.required(btVo.accountId);
		}
		handleWrongInput(false);
		btVo.buyerId = getCurrentUser().id;

		BuyerAccount3 ba = new BuyerAccount3();
		// 查询与该买号有关的任务状态
		ba.hasTaskExecuting = BuyerTask3.hasExecuteTask(btVo);
		ba.hasTask = BuyerTask3.isTakeTask(btVo);

		renderJson(ba);
	}
	
	
	
	/**
	 * 
	 * 查看买号推广状态
	 * 
	 * @param btVo
	 * @since v0.1
	 * @author moloch
	 * @created 2014-8-6 上午10:41:43
	 */
	public static void accountTaskStatus2(@Required BuyerTaskSearchVo btVo) {
		if (btVo != null) {
			validation.required(btVo.accountId);
		}
		handleWrongInput(false);
		btVo.buyerId = getCurrentUser().id;
		
		BuyerAccount2 ba = new BuyerAccount2();
		//查询与该买号有关的任务状态
		ba.hasTaskExecuting = BuyerTask2.hasExecuteTask(btVo);
		ba.hasTask = BuyerTask2.isTakeTask(btVo);
		
		renderJson(ba);
	}
	
	
    /**
     * 
     * TODO 黑名单.
     *
     * @since  0.1
     * @author youblade
     * @created 2014年7月26日 下午3:45:30
     */
    public static void blacklist(){
    	render();
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
	public static void modifyUser(@Required User user, String oldPass) {
		handleWrongInput(false);

		User u = getCurrentUser();

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
				renderSuccessJson();
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
		renderSuccessJson();
	}
    
	/**
	 * 
	 * 会员开通/续费页面.
	 *
	 * @since  0.1
	 * @author youblade
	 * @created 2014年9月5日 下午4:58:29
	 */
    public static void member() {
    	renderArgs.put(BizConstants.PAY_PLATFORMS, KQpayPlatform.values());
    	
        // 计算会员有效期剩余天数
        LocalDate dueDate = new LocalDate(getCurrentUser().dueTime);
        int daysRemaining = Days.daysBetween(LocalDate.now(), dueDate).getDays();
        render(daysRemaining);
    }
    
    /**
     * 
     * 会员充值.
     * 【商家】
     *  1.使用金币支付
     *  2.使用押金支付
     *  3.使用网银实时支付
     * 【买手】
     *  1.使用金币支付
     *  2.使用网银实时支付
     *
     * @param month
     * @param useIngot
     * @param usePledge
     * @since  0.1
     * @author youblade
     * @created 2014年8月25日 下午12:25:50
     */
    public static void memberCharge(@Required Integer month, boolean useIngot, boolean usePledge) {

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
            PayTradeLog log = PayTradeLog.newInstance(TradeType.MEMBER, user.id).amount(payFee)
                .bizMemberMonth(month).memo("用户开通会员" + month + "个月");
            log.save();
            renderJson(log.id);
        }
        // 使用押金或金币支付
        MemberChargeRecord r = MemberChargeRecord.newInstance(user.id, month).userType(user.type).amount(payFee);
        boolean result = r.create(useIngot, usePledge);
        if (!result) {
            renderFailedJson(ReturnCode.FAIL);
        }
        //给用户升级会员到VIP1、VIP2、VIP3
        Integer minMonth = BizConstants.UPGRADE_NORMAL_COUNT;
        if(user.vipStatus == VipStatus.VIP1) {
        	minMonth = BizConstants.UPGRADE_VIP1_COUNT;
        }else if(user.vipStatus == VipStatus.VIP2) {
        	minMonth = BizConstants.UPGRADE_VIP2_COUNT;
        }else if(user.vipStatus == VipStatus.VIP3) {
        	minMonth = BizConstants.UPGRADE_VIP3_COUNT;
        }
        if(minMonth < month) {
        	VipStatus vipStatus = VipStatus.NORMAL;
        	if(BizConstants.UPGRADE_VIP1_COUNT<=month && month<BizConstants.UPGRADE_VIP2_COUNT) {
        		vipStatus = VipStatus.VIP1;
        	}else if(BizConstants.UPGRADE_VIP2_COUNT<=month && month<BizConstants.UPGRADE_VIP3_COUNT) {
        		vipStatus = VipStatus.VIP2;
        	}else {
        		vipStatus = VipStatus.VIP3;
        	}
        	
        	user.changeVipStatus(user.id, vipStatus);
        }
        renderSuccessJson();
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
     * 获取文件上传凭证.<br>
     * 
     * 已放入缓存中（3小时），平台所有用户共享使用.
     *
     * @since  0.1
     * @author youblade
     * @created 2014年8月22日 下午2:33:56
     */
    public static void fetchUploadToken2(boolean force) {
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
	 * 邀请好友页面
	 *
	 * @since  v2.0
	 * @author youblade
	 * @created 2014年12月19日 下午5:01:52
	 */
	public static void invite() {
		User user=getCurrentUser();
		if(user.isBuyer()&&BuyerTask.findListByPage(TaskSearchVo.newInstance().buyerId(user.id).status(TaskStatus.FINISHED)).items.size()>0){
			renderArgs.put("allowInvite", "true");
		}else if(user.isSeller()&&Task.findByPage(TaskSearchVo.newInstance().buyerId(user.id).status(TaskStatus.PUBLISHED).fields("t.id")).items.size()>0){
			renderArgs.put("allowInvite", "true");
		}else {
			renderArgs.put("allowInvite", "true");
		}
	    renderArgs.put("userStatusList", UserStatus.values());
	    UserSearchVo uvo =  UserSearchVo.newInstance();
	    uvo.id = user.id;
	    renderArgs.put("inviteStatistics", UserInvitedRecord.findInviteStatistics(uvo));
        render();
    }
	
	/**
	 * 
	 * 邀请任务奖励页面
	 *
	 * @since  v2.0
	 * @author Mark Xu
	 * @created 2015-3-10 下午3:18:08
	 */
	public static void taskInvite() {
		renderArgs.put("userStatusList", UserStatus.values());
        render();
    }
	
	/**
	 * 
	 * 邀请好友->获取已邀请到的好友列表
	 *
	 * @since  v2.0
	 * @author youblade
	 * @created 2014年12月20日 上午11:07:47
	 */
	public static void listInvitedUsers(@Required UserSearchVo vo) {
	    handleWrongInput(false);
	    
	    vo.nick = StringUtils.trimToNull(vo.nick);
	    vo.email = StringUtils.trimToNull(vo.email);
	    vo.qq = StringUtils.trimToNull(vo.qq);
	    User u = getCurrentUser();
	    vo.inviteNick = u.nick;
	    Page<UserInvitedRecord> page = UserInvitedRecord.findByPage(vo);
        renderPageJson(page.items,page.totalCount);
	}
	
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
		handleWrongInput(false);
		vo.userNick = StringUtils.trimToNull(vo.userNick);
		vo.inviteUserNick = User.findByIdWichCache(Long.parseLong(session.get(Secure.FIELD_AUTH))).nick;
		
		Page<TaskRewardLog> page = TaskRewardLog.findByPage(vo);
		renderPageJson(page.items, page.totalCount);
	}
	
	/**
	 * 
	 * 买手发送心跳数据
	 *
	 * @param durexCode
	 * @since  v0.2.6
	 * @author tr0j4n
	 * @created 2014-11-5 下午3:57:25
	 */
	public static void pulse(String durexCode) {
		User user = getCurrentUser();
		log.info("UserNick={},DurexCode={}", user.nick, durexCode);
		
		
		
		renderSuccessJson();
	}
}
