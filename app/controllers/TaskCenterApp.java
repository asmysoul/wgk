package controllers;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.BooleanUtils;
import org.apache.commons.lang.Validate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import play.data.validation.Min;
import play.data.validation.Required;
import models.BuyerAccount;
import models.BuyerAccount2;
import models.BuyerTask;
import models.BuyerTask2;
import models.BuyerTaskStep2;
import models.FundAccount;
import models.SysConfig;
import models.Task2;
import models.TaskItemSearchPlan;
import models.TaskOrderMessage;
import models.User;
import models.SysConfig.SysConfigKey;
import vos.BuyerAccountSearchVo2;
import vos.BuyerTaskStepVo;
import vos.BuyerTaskVo2;
import vos.Page;
import vos.TaskSearchVo;
import vos.TaskSearchVo2;
import vos.BuyerAccountSearchVo.ExamineStatus;
import vos.BuyerAccountSearchVo2.ExamineStatus2;

import com.aton.base.BaseController;
import com.aton.config.BizConstants;
import com.aton.config.Config;
import com.aton.config.ReturnCode;
import com.aton.util.CollectionUtils;
import com.aton.util.NumberUtils;
import com.aton.util.StringUtils;

import domain.TaskStats;
import domain.TaskStats2;
import enums.BuyerTaskStepType;
import enums.Device;
import enums.Platform;
import enums.Platform2;
import enums.TaskStatus;
import enums.pay.PayPlatform;

public class TaskCenterApp extends BaseController{
	private static final Logger log = LoggerFactory.getLogger(TaskExecutor.class);
	public static final Logger buyerTaskStepLog = LoggerFactory.getLogger("taskstep");
	/**
	 * 
	 * 推广列表->分页获取推广(暂时无分页).
	 *
	 * @since  0.1
	 * @author 尤齐春
	 * @created 2016年8-3日 
	 */
	public static void listTasks2Demo(String platform) {
    	TaskSearchVo2 vo = new TaskSearchVo2();
    	vo.platform = Platform2.valueOf(platform);
    	vo.device = Device.valueOf("MOBILE");
    	renderJson(listTasks2(vo));
	}
	public static List listTasks2(@Required TaskSearchVo2 vo) {
	 
		List<Task2> tasks = Task2.findALLList(vo.status(TaskStatus.PUBLISHED));
		for(Task2 task:tasks){
			task.idStr=task.id.toString();		
			}
		return tasks;
	}
	
	/**
	 * 
	 * 获取买手的购买账号.
	 * 
	 * @author yqc
	 * @created 2016-8-9
	 */
	public static void listBuyerAccounts2(Platform2 platform, String nick, boolean receive) {
		//handleWrongInput(false);
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
	
	
	
	public static void doTasks(long taskId,String buyer, long buyerAccountId,String nick) {
		BuyerTask2 bt = new BuyerTask2();
		bt.taskId = taskId;
		bt.device = Device.valueOf("MOBILE");
		bt.buyerAccountNick = buyer;
		bt.buyerAccountId = buyerAccountId;
		renderJson(take2(bt, nick));
	}
	
	
	
	/**
	 * 
	 * 推广列表->买手接推广
	 * 
	 * @param buyerTask
	 * @since 0.1
	 * @author youblade
	 * @created 2014年8月8日 下午7:11:20
	 */
	public synchronized static long take2(@Required BuyerTask2 bt,String nick) {
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
		if (session.contains(BizConstants.BUYER_TASK_ID2)) {
			if (BuyerTask2.findById(Long.parseLong(session.get(BizConstants.BUYER_TASK_ID2))).status == TaskStatus.CANCLED) {
				session.remove(BizConstants.BUYER_TASK_ID2);
			} else {
				renderFailedJson(ReturnCode.TASK_UNFINISHED, session.get(BizConstants.BUYER_TASK_ID2));
			}
		}

		User buyer = User.findByNick(nick);
		BuyerAccount2 buyerAccount = BuyerAccount2.findById(bt.buyerAccountId);
		System.out.println(""+buyerAccount.toString());
		if (!BuyerAccount2.validate(buyerAccount, buyer)) {
			log.error("买手={}违规注册多个账号，本次使用买号={}不属于当前登录用户！", buyer.nick, bt.buyerAccountNick);
			renderFailedJson(ReturnCode.BIZ_LIMIT, "当前登录用户状态异常，请退出后再登录~~");
		}
		
		// 该任务已被用户接手过，不能重复领取
		if (BuyerTask2.isDuplicateTaking(bt.buyerAccountId, bt.taskId)) {
			renderFailedJson(ReturnCode.BIZ_LIMIT, "你已经领取过该任务了，换一个吧~~");
		}
		
		// 存在尚未完成的任务，不能领取新任务
		BuyerTask2 todoTask = BuyerTask2.findTodoTask(buyer.id);
		if (todoTask != null) {
			renderFailedJson(ReturnCode.TASK_UNFINISHED, todoTask.id);
		}
		
		// 父任务数据异常
		Task2 task = getTask2(bt.taskId);
		System.out.println(""+task.itemId);
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
			System.out.println("任务已全被领取完");
			renderFailedJson(ReturnCode.TASK_TAKE_OVER);
		}
		
//		// 金币不足需要充值购买才能接手任务
//		if (buyer.ingot < BizConstants.TASK_TAKING_INGOT) {
//			System.out.println("您的金币不足，无法领取任务");
//			renderFailedJson(ReturnCode.BIZ_LIMIT, "您的金币不足，无法领取任务~~");
//		}
		
		// 平台限制每个【买号】30单/天，210单/周，300单/月
		TaskStats2 taskStats = TaskStats2.findForBuyerUntilNow(bt.buyerAccountId);
		
		String day=SysConfig.getConfigValue(SysConfigKey.BUYER_TASK_DAY_COUNT);
        if(StringUtils.isEmpty(day)){
            day=Config.BUYER_TASK_DAY_COUNT2;
        }
        
        String week=SysConfig.getConfigValue(SysConfigKey.BUYER_TASK_WEEK_COUNT);
        if(StringUtils.isEmpty(week)){
            week=Config.BUYER_TASK_WEEK_COUNT2;
        }
        String mouth=SysConfig.getConfigValue(SysConfigKey.BUYER_TASK_MONTH_COUNT);
        if(StringUtils.isEmpty(mouth)){
            mouth=Config.BUYER_TASK_MONTH_COUNT2;
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
				if (!isValidateBuyerAccountAndShopId2(bt.buyerAccountId, task.shopId)) {
					renderFailedJson(ReturnCode.BIZ_LIMIT, "推广号在该店铺已经领取过任务");

				}
				// 买号和商品之间的限制
				/*if (!isAvailableBuyerAccountAndItem2(bt.buyerAccountId, task.itemId)) {
					renderFailedJson(ReturnCode.BIZ_LIMIT, "该推广号已经领取过该任务");
				}*/
		
		// TODO 新手必须完成新手任务后才能接手其他任务
		boolean result=Task2.isTaoBaoAndTmall(bt.taskId);//除淘宝、天猫外的其他平台
		Object[] args = new Object[] { buyer.id, buyer.nick, bt.taskId };
		buyerTaskStepLog.info("Buyer={}-{} take Task={}", args);
		 //* 创建买手任务记录
		bt.sellerId = task.sellerId;
		bt.pledgeIngot = BizConstants.TASK_TAKING_INGOT * 100L;
//		bt.pledgeIngot = BizConstants.TASK_TAKING_INGOT * 100L;
		bt.rewardIngot = task.calculateRewardIngot(bt.device);
		//评论上传图片，给买手0.5个金币
        /*if(StringUtils.isNotEmpty(task.goodCommentImg)){
            bt.rewardIngot+=50;
        }*/
		// 写入任务的经验值
		User seller = User.findByIdWichCache(task.sellerId);
		bt.experience = task.getListType(seller.registTime).experience;
		System.out.println("经验值："+bt.experience );
		if(result){
			bt.status=TaskStatus.WAIT_PAY;
		}else {
			bt.status = TaskStatus.RECIEVED;
		}
		bt.create();
		
		long currentTaskId = getCurrentBuyerTaskId();

		// 若不存在未完成的任务，则设置当前访问的任务为“正在做”的任务
		if (currentTaskId <= 0) {
			currentTaskId = bt.id;
			session.put(BizConstants.BUYER_TASK_ID2, currentTaskId);
			// 默认进行第一步：挑选商品
			if (result) {
				session.put(BizConstants.BUYER_TASK_STEP2, BuyerTaskStepType.ORDER_AND_PAY);
			} else {
				session.put(BizConstants.BUYER_TASK_STEP2, BuyerTaskStepType.CHOOSE_ITEM);
			}
		}
		// 返回ID以方便前端跳转到做任务的页面
		return bt.id;
	}
	/**
	 * 
	 * 进入买手做推广的页面.<br>
	 * 
	 * 使用session控制每次只能做一个推广，上次未完成的推广做完之前则不能做新推广。
	 * @author 尤齐春
	 * @created 2016年8月16日 
	 */
	public static void perform2(long buyerAccountId,long performId,String buyerNick) {
		long currentTaskId = performId;
		HashMap<String, Object> map = new HashMap<String, Object>();
		BuyerTaskStepType str = BuyerTaskStepType.ORDER_AND_PAY ;
		// 若不存在未完成的任务，则设置当前访问的任务为“正在做”的任务
		if (currentTaskId <= 0) {
			currentTaskId = buyerAccountId;
			session.put(BizConstants.BUYER_TASK_ID2, currentTaskId);
			// 默认进行第一步：挑选商品
			BuyerTask2 buyerTask=BuyerTask2.findById(currentTaskId);
			if(buyerTask!=null&&Task2.isTaoBaoAndTmall(buyerTask.taskId)){
				session.put(BizConstants.BUYER_TASK_STEP2, BuyerTaskStepType.ORDER_AND_PAY);
				str = BuyerTaskStepType.ORDER_AND_PAY;
			}else {
				session.put(BizConstants.BUYER_TASK_STEP2, BuyerTaskStepType.CHOOSE_ITEM);
				str = BuyerTaskStepType.CHOOSE_ITEM;
			}
		}
		// 设置当前买手任务要做的步骤：后端业务步骤
		BuyerTaskStepType currentTaskStep = str;
		renderArgs.put("taskStep", currentTaskStep);
		User buyer = User.findByNick(buyerNick);
		System.out.println(currentTaskId+"//"+buyer.id);
		System.out.println(currentTaskId+"//"+buyer.id);
		System.out.println(currentTaskId+"//"+buyer.id);
		System.out.println(currentTaskId+"//"+buyer.id);
		System.out.println(currentTaskId+"//"+buyer.id);
		System.out.println(currentTaskId+"//"+buyer.id);
		System.out.println(currentTaskId+"//"+buyer.id);
		BuyerTaskVo2 task = BuyerTask2.findForPerforming(currentTaskId, buyer.id);
		System.out.println(task);
		System.out.println(task);
		System.out.println(task);
		System.out.println(task);
		System.out.println(task);
		System.out.println(task.itemSubwayPic); 
		System.out.println(task.itemSubwayPic); 
		System.out.println(task.itemSubwayPic); 
		System.out.println(task.itemSubwayPic); 
		System.out.println(task.itemSubwayPic); 
		String[] sourceStrArray = task.itemSubwayPic.split(",");
		 String array = "";
		 for(String a:sourceStrArray){
			 array+=a+",";
		 }
		notFoundIfNull(task);
		task.id = currentTaskId;
		Object[] args = new Object[] { buyer.id, buyer.nick, currentTaskStep.toString(), task.taskId, task.id, task.status };
		buyerTaskStepLog.info("Buyer={}-{} begin to perform step={} of buyerTask={}-{} with status={}", args);

		// 设置任务当前要做的步骤：前端页面上显示的小步骤
		int initTaskStepNo = 1;
		if (currentTaskStep == BuyerTaskStepType.VIEW_AND_INQUIRY) {
			initTaskStepNo = 2;
		}
		if (currentTaskStep == BuyerTaskStepType.ORDER_AND_PAY) {
			initTaskStepNo = 4;
		}
		renderArgs.put("initTaskStepNo", initTaskStepNo);

		// 随机取出一条订单留言
		renderArgs.put("orderMessage", TaskOrderMessage.getOneMessage(task.taskId));

		// 取出一套搜索方案
		if(task.searchPlanId>0){
			map.put("searchPlan",TaskItemSearchPlan.findById(task.searchPlanId));
			renderArgs.put("searchPlan", TaskItemSearchPlan.findById(task.searchPlanId));
		}else {
			map.put("searchPlan", TaskItemSearchPlan.getOnePlanOld(task.taskId));
			renderArgs.put("searchPlan", TaskItemSearchPlan.getOnePlanOld(task.taskId));
		}
		renderArgs.put("img",array);
		renderArgs.put("imgsize",sourceStrArray.length);
		renderArgs.put("taskInfo", task);
		renderArgs.put("taskPlatform", task.platform);
		map.put("currentTaskStep",currentTaskStep);
		map.put("initTaskStepNo", initTaskStepNo);
		map.put("taskInfo",task);
		map.put("taskPlatform", task.platform);
		renderJson(map);
	}
	
	public static void DosaveStep1(long taskId ,String nick ,long buyerAccountId) {
		// TODO Auto-generated method stub
		BuyerTaskStepVo vo = new BuyerTaskStepVo();
		BuyerTaskStepType type = BuyerTaskStepType.valueOf("CHOOSE_ITEM"); 
		session.put(BizConstants.BUYER_TASK_STEP2, BuyerTaskStepType.CHOOSE_ITEM);
		renderJson(saveStep2(taskId, type, vo, nick,buyerAccountId));
	}
	
	
	public static void DosaveStep3(long taskId , String picUrls,String itemUrls ,String nick ,long buyerAccountId) {
		// TODO Auto-generated method stub
		BuyerTaskStepVo vo = new BuyerTaskStepVo();
		vo.picUrls =new ArrayList<String>();
		vo.itemUrls=new ArrayList<String>();
		vo.picUrls.add(picUrls);
		if(",".equals(itemUrls.trim())){
			vo.itemUrls.add("");
		}else{
			String[] newUrl = itemUrls.split(",");
			for(int i=0;i<newUrl.length;i++){
				vo.itemUrls.add(newUrl[i]);
			}
		}
		
		BuyerTaskStepType type = BuyerTaskStepType.valueOf("VIEW_AND_INQUIRY"); 
		session.put(BizConstants.BUYER_TASK_STEP2, BuyerTaskStepType.VIEW_AND_INQUIRY);
		renderJson(saveStep2(taskId, type, vo, nick,buyerAccountId));
	}
	
	
	public static void DosaveStep5(long taskId , String nick ,long buyerAccountId) {
		// TODO Auto-generated method stub
		BuyerTaskStepVo vo = new BuyerTaskStepVo();
		BuyerTaskStepType type = BuyerTaskStepType.valueOf("ORDER_AND_PAY");
		session.put(BizConstants.BUYER_TASK_STEP2, BuyerTaskStepType.ORDER_AND_PAY);
		renderJson(saveStep2(taskId, type, vo, nick,buyerAccountId));
	}
	
	
	/**
	 * 保存每一步做推广的进度数据.
	 *            :买手推广进度步骤
	 */
	public synchronized static String saveStep2(@Required @Min(1) long id,
			@Required BuyerTaskStepType type, @Required BuyerTaskStepVo vo,String nick,long buyerAccountId) {
		System.out.println(id);
		System.out.println(id);
		System.out.println(id);
		System.out.println(id);
		BuyerTask2 buyerTask = BuyerTask2.findById(id);
		boolean result = Task2.isTaoBaoAndTmall(buyerTask.taskId);
		User buyer = User.findByNick(nick);
		
		Object[] args = new Object[] { buyer.id, buyer.nick, id, type.toString() };
		buyerTaskStepLog.info("Buyer={}-{} perform buyerTask={} save step={}", args);

		// 检查任务ID是否正确，以及该步骤是否已做过（通过比较BuyerTaskStepType的顺序）
//		long currentTaskId = getCurrentBuyerTaskId2();
		long currentTaskId = id;
		if(type==null){
			
			renderFailedJson(ReturnCode.OP_BIZ_LIMIT);
		}
		BuyerTaskStepType currentStep = type;
		if (currentTaskId != id || currentStep.getOrder() > type.getOrder()) {
			Object[] args2 = new Object[] { buyer.id, buyer.nick, id, type.toString(), currentTaskId, currentStep.title };
			buyerTaskStepLog.error("[Buyer={}-{} perform buyerTask={} save step={}] Current SESSION taskId={} step={}", args2);
			renderFailedJson(ReturnCode.OP_BIZ_LIMIT);
		}
		if (!result) {
			/*
			 * 检查任务步骤应包含的数据
			 */
			// 第一步“挑选商品”：四个其他商品链接
			/*if (type == BuyerTaskStepType.CHOOSE_ITEM && !vo.validateUrls(vo.itemUrls, 2)) {
				renderFailedJson(ReturnCode.WRONG_INPUT);
			}*/
			// 第三步“浏览店铺及询盘”：四个其他商品链接，一个聊天截图的图片url   && (!vo.validateUrls(vo.itemUrls, 2) /*|| !vo.validateUrls(vo.picUrls, 1)*/)
			/*
			 * 检查上个做完的步骤
			 */
			buyerTaskStepLog.info("Fetch and Check Buyer={} last perform step of buyerTask={}", buyer.id, id);

			BuyerTaskStep2 lastTaskStep = BuyerTaskStep2.findBuyerLastStep(currentTaskId, buyer.id);
			System.out.println("=======lastTaskStep========"+currentTaskId+",,,,,,"+buyer.id);
			System.out.println("=======type========"+lastTaskStep);
			System.out.println("=======type========"+lastTaskStep);
			System.out.println("=======type========"+lastTaskStep);
			System.out.println("=======type========"+lastTaskStep);
			System.out.println("=======type========"+lastTaskStep);
			// 上个步骤记录不存在时，当前步骤必须是第一步“货比三家”
			if (lastTaskStep == null && currentStep != BuyerTaskStepType.CHOOSE_ITEM) {
				buyerTaskStepLog.error("[Buyer={}-{} perform buyerTask={} save step={}] Input step must be first:[CHOOSE_ITEM]", args);
				System.out.println("=================================");
				System.out.println("=================================");
				System.out.println("=================================");
				System.out.println("=================================");
				System.out.println("=================================");
				System.out.println("=================================");
				renderFailedJson(ReturnCode.OP_BIZ_LIMIT);
			}
			// 上个步骤记录存在时，当前步骤必须为“上个步骤记录的下一个步骤”
			if (lastTaskStep != null && currentStep != lastTaskStep.type.getNext()) {
				Object[] args3 = new Object[] { buyer.id, buyer.nick, id, type.toString(), currentTaskId, currentStep.title };
				buyerTaskStepLog.error("[Buyer={}-{} perform buyerTask={} save step={}] Current SESSION taskId={} step={}", args3);
				renderFailedJson(ReturnCode.OP_BIZ_LIMIT);
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
			System.out.println("?????????????????");
			System.out.println("?????????????????");
			System.out.println("?????????????????");
			System.out.println("?????????????????");
			System.out.println("?????????????????");
			System.out.println("?????????????????");
			renderFailedJson(ReturnCode.WRONG_INPUT);
		}

		// 第五步下单支付：检查用户填写的“订单号”及“付款金额”
		if (type == BuyerTaskStepType.ORDER_AND_PAY) {
			/*validation.isTrue(vo.validateUrls(vo.picUrls, 1));
			validation.isTrue(StringUtils.isNumeric(vo.orderNo));
			validation.isTrue(NumberUtils.isNumber(vo.realPaidFee));
			validation.required(messageId);
			validation.min(messageId, 1);*/
			handleWrongInput(false);

		}

		args = new Object[] { buyer.id, buyer.nick, type.toString(), buyerTask.taskId, buyerTask.id, buyerTask.status };
		buyerTaskStepLog.info("Buyer={}-{} perform save step={} of buyerTask={}-{} with status={}", args);
		BuyerTaskStep2.newInstance(buyerTask.id, buyer.id).type(type).content(vo).create(buyerTask);
		/*
		 * 更新当前要做的任务步骤标记
		 */
		if (BuyerTaskStepType.ORDER_AND_PAY == type) {
			// 删除session中当前进行任务id
			session.remove(BizConstants.BUYER_TASK_ID2);
			// 删除当前任务进行的步骤
			session.remove(BizConstants.BUYER_TASK_STEP2);
		} else {
			// 更新当前任务进行的步骤为下一个步骤
			session.put(BizConstants.BUYER_TASK_STEP2, type.getNext());
		}
		//renderSuccessJson();
		return "OK";
	}
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	/**
	 * 
	 * 获取买手的购买账号.
	 * 
	 * @author yqc
	 * @created 2016-8-9
	 */
	public static void listBuyerAccounts2(Platform2 platform, boolean receive,String nick) {
		//handleWrongInput(false);
		BuyerAccountSearchVo2 vo = BuyerAccountSearchVo2.newInstance().platform(platform).userId(User.findByNick(nick).id);
		// 接手任务时仅返回可供使用的买号（每个买号每天限制5个任务）
        if (receive) {
            vo.status(ExamineStatus2.EXAMINED);
            renderJson(BuyerAccount2.findForTakingTask(vo));
        }

		List<BuyerAccount2> list = BuyerAccount2.findList(vo);
		renderJson(list);
	}
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	private static long getCurrentBuyerTaskId2() {
		return NumberUtils.toLong(session.get(BizConstants.BUYER_TASK_ID2));
	}
	private static long getCurrentBuyerTaskId() {
		return NumberUtils.toLong(session.get(BizConstants.BUYER_TASK_ID));
	}
	
	/**
	 * 
	 * @author 尤齐春
	 * @created 2016年8月3日 下午
	 */
	public static Task2 getTask2(long taskId) {
		// 父任务数据异常
		return Task2.findById("id,platform,status,base_order_ingot,extra_reward_ingot,pc_order_num,mobile_order_num,pc_taken_count,mobile_taken_count,"
				+ "finished_count,seller_id,create_time,shop_id,item_id,sys_refund,"
				+ "publish_timer_interval,publish_timer_value,last_batch_publish_time,good_comment_img", taskId);
	}
	
	public static boolean isValidateBuyerAccountAndShopId2(long buyerAccountId, Long shopId) {
		Validate.notNull(buyerAccountId);
		Validate.notNull(shopId);
		// 取【最近15天】【该买号】在【该商家】的（非取消）任务记录
		List<BuyerTaskVo2> buyerTasks = TaskStats.findBuyerAccountIdAndShopId2(buyerAccountId, shopId);
		
		if(CollectionUtils.isNotEmpty(buyerTasks)){
			return false;
		}
		return true;
	}
	
	/**
	 * 
	 * 买号和商品之间的限制
	 * 
	 * @created 2016年8月13日 下午3:47:45
	 */
	public static boolean isAvailableBuyerAccountAndItem2(long buyerAccountId, String itemId) {
		Validate.notNull(buyerAccountId);
		Validate.notEmpty(itemId);
		if (CollectionUtils.isNotEmpty(TaskStats2.selectByBuyerAccountIdAndItemIdAndPeriod(buyerAccountId, itemId))) {
			return false;
		}
		return true;
	}
	 /**
     * 
     * （买手）我的tuiguang ->分页获取推广列表.
     *
     * @since  0.1
     * @author 尤齐春
     * @created 2016年8月12日 下午6:00:15
     */
    
    public static void get(String nick){
    	Map<String, Object> map=new HashMap<String, Object>();
    	map.put("EXPRESS_PRINT", listBuyer("EXPRESS_PRINT",nick));
    	map.put("yijieshou_count", listBuyer("EXPRESS_PRINT",nick).totalCount);
    	map.put("RECIEVED", listBuyer("RECIEVED",nick));
    	map.put("daifan_count", listBuyer("RECIEVED",nick).totalCount);
    	map.put("sharePROCESS", PROCESS(nick));
    	renderJson(map);
    	
    }
    
    public static  Page<BuyerTaskVo2> listBuyer(String type,String nick) {
    	TaskSearchVo2 vo=new TaskSearchVo2();
    	vo.pageNo=1;
    	vo.pageSize=100;
    	//String type="PROCESS";       
    	//String type="EXPRESS_PRINT";           //待返佣
    	//String type="RECIEVED";//已接手，未开始
    	//String type="FINISHED";//已完成

    	User buyer =User.findByNick(nick);
        Page<BuyerTaskVo2> p = null;

			if(vo.status==null){
				//进行中
				//p = BuyerTask2.findListByPage(vo.buyerId(buyer.id).statusExclude(TaskStatus.CANCLED,TaskStatus.FINISHED));
				if("EXPRESS_PRINT".equals(StringUtils.trim(type))){
					p = BuyerTask2.findListByPage(vo.buyerId(buyer.id).status(TaskStatus.EXPRESS_PRINT));
				}
				if("RECIEVED".equals(StringUtils.trim(type))){
					p = BuyerTask2.findListByPage(vo.buyerId(buyer.id).status(TaskStatus.RECIEVED));
				}
			}
			else {
				p = BuyerTask2.findListByPage(vo.buyerId(buyer.id));
			}
        return  p;
    }

    
    
    public synchronized static Map PROCESS(String nick){
  		TaskSearchVo2 vo = new TaskSearchVo2();
  		vo.pageNo=1;
  		vo.pageSize=1000;
  		return listBuyerTasks2(vo,"PROCESS",nick);
  	}
    
    
    /**
     * 
     * （买手）我的tuiguang ->分页获取推广列表.
     *
     * @since  0.1
     * @author 尤齐春
     * @created 2016年8月12日 下午6:00:15
     */
    public static Map listBuyerTasks2(@Required TaskSearchVo2 vo,String type,String nick) {
        if (vo != null) {
            vo.orderId = StringUtils.trimToNull(vo.orderId);
            vo.buyerAccountNick = StringUtils.trimToNull(vo.buyerAccountNick);
        }
        //handleWrongInput(false);

        User buyer = User.findByNick(nick);
        Page<BuyerTaskVo2> p = null;
        if("FINISHED".equals(StringUtils.trim(type))){
        	p = BuyerTask2.findListByPage(vo.buyerId(buyer.id).status(TaskStatus._FINISHED));
        }else if("CANCEL".equals(StringUtils.trim(type))){
        	p = BuyerTask2.findListByPage(vo.buyerId(buyer.id).status(TaskStatus.CANCLED));
        }else if ("ALL".equals(StringUtils.trim(type))) {
        	p = BuyerTask2.findListByPage(vo.buyerId(buyer.id));
		}else {
			if(vo.status==null){
				//进行中
				p = BuyerTask2.findListByPage(vo.buyerId(buyer.id).statusExclude(TaskStatus.CANCLED,TaskStatus.FINISHED));
				
			}else {
				p = BuyerTask2.findListByPage(vo.buyerId(buyer.id));
			}
		}
        
        
        for (BuyerTaskVo2 taskVo : p.items) {
            taskVo.subShopNamePrefix();
        }
        Map<String,Object> map = new HashMap<String,Object>();
  		map.put("items",p.items);
  		map.put("totalCount",p.totalCount);
        return map;
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
	public static void cancelBuyerTask2(long id,String nick) {
		//handleWrongInput(false);
		User buyer = User.findByNick(nick);
		BuyerTask2 bt = BuyerTask2.findById(id);
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
	
	public static void addShareAccount(String platform, String nick ,String mobile, String consignee ,String state ,String city,String region,String address,String buyer){
		BuyerAccount2 account = new BuyerAccount2();
		account.platform = Platform2.valueOf(platform);
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
	public synchronized static String saveBuyerAccount(@Required BuyerAccount2 account , String nick){
		
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
        
		account.status = ExamineStatus2.WAIT_EXAMINE;
		account.userId = buyer.id;
		account.save();
		
		return "OK";
	}
    
}
