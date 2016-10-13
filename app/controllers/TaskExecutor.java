package controllers;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import models.BuyerAccount;
import models.BuyerAccount2;
import models.BuyerAccount3;
import models.BuyerTask;
import models.BuyerTask2;
import models.BuyerTask3;
import models.BuyerTaskStep;
import models.BuyerTaskStep2;
import models.BuyerTaskStep3;
import models.FundAccount;
import models.SysConfig;
import models.Task;
import models.Task2;
import models.Task3;
import models.TaskItemSearchPlan;
import models.TaskItemSearchPlan3;
import models.TaskOrderMessage;
import models.User;
import models.SysConfig.SysConfigKey;
import models.User.VipStatus;

import org.apache.commons.lang.BooleanUtils;
import org.apache.commons.lang.Validate;
import org.joda.time.Days;
import org.joda.time.LocalDate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import play.data.validation.Min;
import play.data.validation.Required;
import play.mvc.Util;
import play.mvc.With;
import vos.BuyerAccountSearchVo.ExamineStatus;
import vos.BuyerTaskStepVo;
import vos.BuyerTaskVo;
import vos.BuyerTaskVo2;
import vos.BuyerTaskVo3;
import vos.TaskSearchVo;
import vos.TaskSearchVo2;

import com.aton.base.BaseController;
import com.aton.base.secure.Secure;
import com.aton.config.BizConstants;
import com.aton.config.Config;
import com.aton.config.ReturnCode;
import com.aton.util.CollectionUtils;
import com.aton.util.JsonUtil;
import com.aton.util.NumberUtils;
import com.aton.util.StringUtils;
import com.aton.util.UrlUtil;
import com.google.common.base.Strings;
import com.google.common.collect.Sets;

import domain.BuyerTaskProcessor;
import domain.BuyerTaskProcessor3;
import domain.TaskStats;
import domain.TaskStats2;
import domain.TaskStats3;
import enums.BuyerTaskStepType;
import enums.Platform;
import enums.Platform2;
import enums.Platform3;
import enums.TaskStatus;
import enums.TaskType;
import enums.TaskType3;
import enums.pay.PayPlatform;

@With(Secure.class)
public class TaskExecutor extends BaseController {

	private static final Logger log = LoggerFactory.getLogger(TaskExecutor.class);
	public static final Logger buyerTaskStepLog = LoggerFactory.getLogger("taskstep");

	private static long getCurrentBuyerTaskId() {
		return NumberUtils.toLong(session.get(BizConstants.BUYER_TASK_ID));
	}

	private static long getCurrentBuyerTaskId2() {
		return NumberUtils.toLong(session.get(BizConstants.BUYER_TASK_ID2));
	}
	
	private static long getCurrentBuyerTaskId3() {
		return NumberUtils.toLong(session.get(BizConstants.BUYER_TASK_ID3));
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
	public static void perform(@Required @Min(1) long id) {
		handleWrongInput(false);
		long currentTaskId = getCurrentBuyerTaskId();

		// 若不存在未完成的任务，则设置当前访问的任务为“正在做”的任务
		if (currentTaskId <= 0) {
			currentTaskId = id;
			session.put(BizConstants.BUYER_TASK_ID, currentTaskId);
			// 默认进行第一步：挑选商品
			BuyerTask buyerTask=BuyerTask.findById(currentTaskId);
			if(buyerTask!=null&&Task.isTaoBaoAndTmall(buyerTask.taskId)){
				session.put(BizConstants.BUYER_TASK_STEP, BuyerTaskStepType.ORDER_AND_PAY);
			}else {
				session.put(BizConstants.BUYER_TASK_STEP, BuyerTaskStepType.CHOOSE_ITEM);
			}
			
		}

		// 设置当前买手任务要做的步骤：后端业务步骤
		BuyerTaskStepType currentTaskStep = BuyerTaskStepType.valueOf(session.get(BizConstants.BUYER_TASK_STEP));
		renderArgs.put("taskStep", currentTaskStep);

		User buyer = getCurrentUser();

		BuyerTaskVo task = BuyerTask.findForPerforming(currentTaskId, buyer.id);
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
			renderArgs.put("searchPlan", TaskItemSearchPlan.findById(task.searchPlanId));
		}else {
			renderArgs.put("searchPlan", TaskItemSearchPlan.getOnePlanOld(task.taskId));
		}
		renderArgs.put("taskInfo", task);
		renderArgs.put("taskPlatform", task.platform);
		render();
	}
	
	
	/**
	 * 
	 * 进入买手做任务的页面.<br>
	 * 
	 * 使用session控制每次只能做一个任务，上次未完成的任务做完之前则不能做新任务。
	 */
	public static void perform3(@Required @Min(1) long id) {
		handleWrongInput(false);
		
		User buyer = getCurrentUser();
		long currentTaskId = id;
		BuyerTaskVo3 task = BuyerTask3.findForPerforming(currentTaskId, buyer.id);
		session.put(BizConstants.BUYER_TASK_ID3, currentTaskId);
		Task3 t=Task3.findById(task.taskId);
		BuyerTaskStep3 laststep=BuyerTaskStep3.findBuyerLastStep(currentTaskId, buyer.id);
		if(laststep==null){
			if(t.type==TaskType3.JHS){
				session.put(BizConstants.BUYER_TASK_STEP3, BuyerTaskStepType.VIEW_AND_INQUIRY);
			}
			else{
				session.put(BizConstants.BUYER_TASK_STEP3, BuyerTaskStepType.CHOOSE_ITEM);
			}
		}
		else{
			session.put(BizConstants.BUYER_TASK_STEP3, laststep.type.getNext());
		}
		// 设置当前买手任务要做的步骤：后端业务步骤
		BuyerTaskStepType currentTaskStep = BuyerTaskStepType.valueOf(session.get(BizConstants.BUYER_TASK_STEP3));
		renderArgs.put("taskStep", currentTaskStep);

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
			renderArgs.put("searchPlan", TaskItemSearchPlan3.findById(task.searchPlanId));
		}else {
			renderArgs.put("searchPlan", TaskItemSearchPlan3.getOnePlanOld(task.taskId));
		}
		renderArgs.put("taskInfo", task);
		if(task.mobileOrdernum>0){
			renderArgs.put("ismobile", true);
		}
		renderArgs.put("taskPlatform", task.platform);
		render();
	}
	
	
	/**
	 * 
	 * 进入买手做推广的页面.<br>
	 * 
	 * 使用session控制每次只能做一个推广，上次未完成的推广做完之前则不能做新推广。
	 * @author 尤齐春
	 * @created 2016年8月16日 
	 */
	public static void perform2(@Required @Min(1) long id) {
		System.out.println("-------perform2id-----+id"+id);
		System.out.println("-------perform2id-----+id"+id);
		System.out.println("-------perform2id-----+id"+id);
		System.out.println("-------perform2id-----+id"+id);
		handleWrongInput(false);
		long currentTaskId = getCurrentBuyerTaskId2();
		System.out.println("------currentTaskId-----"+currentTaskId);
		System.out.println("------currentTaskId-----"+currentTaskId);
		System.out.println("------currentTaskId-----"+currentTaskId);
		System.out.println("------currentTaskId-----"+currentTaskId);
		// 若不存在未完成的任务，则设置当前访问的任务为“正在做”的任务
		if (currentTaskId <= 0) {
			currentTaskId = id;
			session.put(BizConstants.BUYER_TASK_ID2, currentTaskId);
			// 默认进行第一步：挑选商品
			BuyerTask2 buyerTask=BuyerTask2.findById(currentTaskId);
			System.out.println("-----buyerTask.taskId-----"+buyerTask.taskId);
			System.out.println("-----buyerTask-.taskId----"+buyerTask.taskId);
			System.out.println("-----buyerTask.taskId-----"+buyerTask.taskId);
			System.out.println("----------------"+Task2.isTaoBaoAndTmall(buyerTask.taskId));
			if(buyerTask!=null&&Task2.isTaoBaoAndTmall(buyerTask.taskId)){
				session.put(BizConstants.BUYER_TASK_STEP2, BuyerTaskStepType.ORDER_AND_PAY);
			}else {
				System.out.println("------------------");
				System.out.println("------------------");
				System.out.println("------------------");
				System.out.println("------------------");
				System.out.println("------------------");
				session.put(BizConstants.BUYER_TASK_STEP2, BuyerTaskStepType.CHOOSE_ITEM);
			}
		}
		// 设置当前买手任务要做的步骤：后端业务步骤
		BuyerTaskStepType currentTaskStep = BuyerTaskStepType.valueOf(session.get(BizConstants.BUYER_TASK_STEP2));
		System.out.println("---------currentTaskStep-------"+currentTaskStep);
		System.out.println("---------currentTaskStep-------"+currentTaskStep);
		System.out.println("---------currentTaskStep-------"+currentTaskStep);
		System.out.println("---------currentTaskStep-------"+currentTaskStep);
		renderArgs.put("taskStep", currentTaskStep);
		User buyer = getCurrentUser();
		System.out.println("===========================");
		System.out.println("===========================");
		System.out.println("===========================");
		System.out.println("===========================");
		System.out.println("===========================");
		BuyerTaskVo2 task = BuyerTask2.findForPerforming(currentTaskId, buyer.id);
		 String[] sourceStrArray = task.itemSubwayPic.split(",");
		
		 String array = "";
		 for(String a:sourceStrArray){
			 array+=a+",";
		 }
		 
		notFoundIfNull(task);
		task.id = currentTaskId;
		System.out.println("task----------"+task.toString());
		System.out.println("task----------"+task.toString());
		System.out.println("task----------"+task.toString());
		System.out.println("task----------"+task.toString());
		System.out.println("task----------"+task.toString());
		System.out.println("task----------"+task.toString());
		System.out.println("task----------"+task.toString());
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
			renderArgs.put("searchPlan", TaskItemSearchPlan.findById(task.searchPlanId));
		}else {
			renderArgs.put("searchPlan", TaskItemSearchPlan.getOnePlanOld(task.taskId));
		}
		renderArgs.put("img",array);
		renderArgs.put("imgsize",sourceStrArray.length);
		renderArgs.put("taskInfo", task);
		renderArgs.put("taskPlatform", task.platform);
		render();
	}
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	/**
	 * 
	 * 除天猫、淘宝以外的平台买手做任务
	 *
	 * @param id
	 * @since  v2.0
	 * @author fufei
	 * @created 2015年2月2日 下午3:54:57
	 */
	public static void performTakeTask(@Required @Min(1) long id) {
		handleWrongInput(false);
		long currentTaskId = getCurrentBuyerTaskId();

		// 若不存在未完成的任务，则设置当前访问的任务为“正在做”的任务
		if (currentTaskId <= 0) {
			currentTaskId = id;
			session.put(BizConstants.BUYER_TASK_ID, currentTaskId);
			// 默认进行第一步：挑选商品
			session.put(BizConstants.BUYER_TASK_STEP, BuyerTaskStepType.ORDER_AND_PAY);
		}

		// 设置当前买手任务要做的步骤：后端业务步骤
		BuyerTaskStepType currentTaskStep = BuyerTaskStepType.valueOf(session.get(BizConstants.BUYER_TASK_STEP));
		renderArgs.put("taskStep", currentTaskStep);

		User buyer = getCurrentUser();

		BuyerTaskVo task = BuyerTask.findForPerforming(currentTaskId, buyer.id);
		notFoundIfNull(task);

		task.id = currentTaskId;
		Object[] args = new Object[] { buyer.id, buyer.nick, currentTaskStep.toString(), task.taskId, task.id, task.status };
		buyerTaskStepLog.info("Buyer={}-{} begin to perform step={} of buyerTask={}-{} with status={}", args);

		// 设置任务当前要做的步骤：前端页面上显示的小步骤
		
		renderArgs.put("initTaskStepNo", 4);

		// 随机取出一条订单留言
		renderArgs.put("orderMessage", TaskOrderMessage.getOneMessage(task.taskId));

		// 随机取出一套搜索方案
		//renderArgs.put("searchPlan", TaskItemSearchPlan.getOnePlan(task.taskId));

		renderArgs.put("taskInfo", task);
		render();
	}
	
	/**
	 * 淘宝、天猫以外的其他平台任务保存
	 *
	 * @param id
	 * @param type
	 * @param vo
	 * @param messageId
	 * @since  v2.0
	 * @author fufei
	 * @created 2015年2月2日 下午4:07:56
	 */
	public static void saveStepOtherPlatform(@Required @Min(1) long id, @Required BuyerTaskStepType type, @Required BuyerTaskStepVo vo, long messageId) {
		handleWrongInput(false);
		User buyer = getCurrentUser();
		Object[] args = new Object[] { buyer.id, buyer.nick, id, type.toString() };
		buyerTaskStepLog.info("Buyer={}-{} perform buyerTask={} save step={}", args);

		// 检查任务ID是否正确，以及该步骤是否已做过（通过比较BuyerTaskStepType的顺序）
		long currentTaskId = getCurrentBuyerTaskId();
		BuyerTaskStepType currentStep = BuyerTaskStepType.valueOf(session.get(BizConstants.BUYER_TASK_STEP));
		if (currentTaskId != id || currentStep.getOrder() > type.getOrder()) {
			Object[] args2 = new Object[] { buyer.id, buyer.nick, id, type.toString(), currentTaskId, currentStep.title };
			buyerTaskStepLog.error("[Buyer={}-{} perform buyerTask={} save step={}] Current SESSION taskId={} step={}", args2);
			renderFailedJson(ReturnCode.OP_BIZ_LIMIT);
		}

		/*
		 * 检查上个做完的步骤
		 */
		buyerTaskStepLog.info("Fetch and Check Buyer={} last perform step of buyerTask={}", buyer.id, id);
		/*
		 * 检查正在做的任务(状态)
		 */
		BuyerTask buyerTask = BuyerTask.findById(id);
		if (buyerTask == null || buyerTask.isNotBelongTo(buyer)
				|| (buyerTask.status != TaskStatus.WAIT_PAY && buyerTask.status != TaskStatus.RECIEVED)) {
			renderFailedJson(ReturnCode.WRONG_INPUT);
		}

		// 第五步下单支付：检查用户填写的“订单号”及“付款金额”
		if (type == BuyerTaskStepType.ORDER_AND_PAY) {
			validation.isTrue(vo.validateUrls(vo.picUrls, 1));
			validation.isTrue(StringUtils.isNumeric(vo.orderNo));
			validation.isTrue(NumberUtils.isNumber(vo.realPaidFee));
			validation.required(messageId);
			validation.min(messageId, 1);
			handleWrongInput(false);

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
		renderSuccessJson();
	}
	
	
	/**
	 * 
	 * 获取买手做任务步骤（第五步：付款并上传截图）中页面显示所需的买号信息（收货信息）.
	 */
	public static void getBuyerAccountForPerform3(@Required @Min(1) long id) {
		handleWrongInput(false);

		BuyerAccount3 account = BuyerAccount3.findById(id);
		if (!BuyerAccount3.validate(account, getCurrentUser())) {
			renderFailedJson(ReturnCode.WRONG_INPUT);
		}
		renderJson(account);
	}
	
	
	
	/**
	 * 
	 * 获取买手做任务步骤（第五步：付款并上传截图）中页面显示所需的买号信息（收货信息）.
	 * 
	 * @param id
	 * @since 0.1
	 * @author youblade
	 * @created 2014年8月23日 上午10:38:29
	 */
	public static void getBuyerAccountForPerform(@Required @Min(1) long id) {
		handleWrongInput(false);

		BuyerAccount account = BuyerAccount.findById(id);
		if (!BuyerAccount.validate(account, getCurrentUser())) {
			renderFailedJson(ReturnCode.WRONG_INPUT);
		}
		renderJson(account);
	}
	
	
	/**
	 * 获取买手做推广步骤（第五步：付款并上传截图）中页面显示所需的买号信息（收货信息）.
	 */
	public static void getBuyerAccountForPerform2(@Required @Min(1) long id) {
		System.out.println("------------id----"+id);
		System.out.println("------------id----"+id);
		System.out.println("------------id----"+id);
		System.out.println("------------id----"+id);
		handleWrongInput(false);
		
		BuyerAccount2 account = BuyerAccount2.findById(id);
		System.out.println("------acount---"+account);
		System.out.println("------acount---"+account);
		System.out.println("------acount---"+account);
		System.out.println("------acount---"+account);
		if (!BuyerAccount2.validate(account, getCurrentUser())) {
			renderFailedJson(ReturnCode.WRONG_INPUT);
		}
		renderJson(account);
	}
	
	/**
	 * 检查第二步url是否正确
	 * @Title: checkSecUrl 
	 * @Description: TODO
	 * @param itemUrl
	 * @return: void
	 */
	public static boolean checkSecUrl(String itemUrl) {
		validation.isTrue(StringUtils.isNotBlank(itemUrl));
		// 限制最大长度300个字符串
		validation.maxSize(itemUrl, 300);
		handleWrongInput(false);

		// 获取买手当前正在做的任务
		long buyerTaskId = getCurrentBuyerTaskId3();
		long buyerId = getCurrentUser().id;
		BuyerTaskVo3 taskVo = BuyerTask3.findVoById(buyerTaskId, buyerId);
		
		
		if (taskVo == null || taskVo.status != TaskStatus.WAIT_PAY) {
			Object[] args = new Object[] { buyerId, itemUrl, buyerTaskId };
			log.warn("User={} check item_url={} for buyerTask={} FAIL:[data error]", args);
			renderFailedJson(ReturnCode.FAIL);
		}

		// 先比较商品url是否完全一致
		if (taskVo.itemUrl.equals(itemUrl)) {
			return true;
		}

		// 若url不完全一致，则从商品链接中提取商品ID与任务目标商品进行比较
		
		String inputItemId = Task3.getItemIdByPlatform(taskVo.platform, itemUrl);
		
		
		if (Strings.isNullOrEmpty(inputItemId) || !inputItemId.equals(taskVo.itemId.toString())) {
			return false;
		}
		return true;
	}
	
	/**
	 * 
	 * 保存每一步做浏览的进度数据.
	 */
	public synchronized static void saveStep3(@Required @Min(1) long id, @Required BuyerTaskStepType type, @Required BuyerTaskStepVo vo, long messageId) {
		handleWrongInput(false);
		BuyerTask3 buyerTask = BuyerTask3.findById(id);
		boolean result = Task3.isTaoBaoAndTmall(buyerTask.taskId);
		User buyer = getCurrentUser();
		Object[] args = new Object[] { buyer.id, buyer.nick, id, type.toString() };
		buyerTaskStepLog.info("Buyer={}-{} perform buyerTask={} save step={}", args);

		// 检查任务ID是否正确，以及该步骤是否已做过（通过比较BuyerTaskStepType的顺序）
		long currentTaskId = getCurrentBuyerTaskId3();
		System.out.println("========session.get(BizConstants.BUYER_TASK_STEP3)========="+session.get(BizConstants.BUYER_TASK_STEP3));
		System.out.println("========session.get(BizConstants.BUYER_TASK_STEP3)========="+session.get(BizConstants.BUYER_TASK_STEP3));
		System.out.println("========session.get(BizConstants.BUYER_TASK_STEP3)========="+session.get(BizConstants.BUYER_TASK_STEP3));
		System.out.println("========session.get(BizConstants.BUYER_TASK_STEP3)========="+session.get(BizConstants.BUYER_TASK_STEP3));
		System.out.println("========session.get(BizConstants.BUYER_TASK_STEP3)========="+session.get(BizConstants.BUYER_TASK_STEP3));
		
		if(session.get(BizConstants.BUYER_TASK_STEP3)==null){
			renderFailedJson(ReturnCode.OP_BIZ_LIMIT);
		}
		
		BuyerTaskStepType currentStep = BuyerTaskStepType.valueOf(session.get(BizConstants.BUYER_TASK_STEP3));
		
		
		System.out.println("==============currentTaskId===================="+currentTaskId);
		System.out.println("===================id==============="+id);
		System.out.println("=================currentStep.getOrder()================="+currentStep.getOrder());
		System.out.println("================type.getOrder()=================="+type.getOrder());
		System.out.println("==================================");
		System.out.println("==================================");
		
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
			if (type == BuyerTaskStepType.CHOOSE_ITEM && !vo.validateUrls(vo.itemUrls, 4)) {
				renderFailedJson(ReturnCode.WRONG_INPUT);
			}
			else if(type == BuyerTaskStepType.CONFIRM_REFUND&&!checkSecUrl(vo.secUrl)){
				renderFailedJson(ReturnCode.WRONG_INPUT);
			}
			// 第三步“浏览店铺及询盘”：四个其他商品链接，一个聊天截图的图片url
//			if (type == BuyerTaskStepType.VIEW_AND_INQUIRY && (!vo.validateUrls(vo.itemUrls, 4) || !vo.validateUrls(vo.picUrls, 1))) {
//				renderFailedJson(ReturnCode.WRONG_INPUT);
//			}

			/*
			 * 检查上个做完的步骤
			 */
			buyerTaskStepLog.info("Fetch and Check Buyer={} last perform step of buyerTask={}", buyer.id, id);

			BuyerTaskStep3 lastTaskStep = BuyerTaskStep3.findBuyerLastStep(currentTaskId, buyer.id);
			// 上个步骤记录不存在时，当前步骤必须是第一步“货比三家”
			if (lastTaskStep == null && currentStep != BuyerTaskStepType.CHOOSE_ITEM) {
				buyerTaskStepLog.error("[Buyer={}-{} perform buyerTask={} save step={}] Input step must be first:[CHOOSE_ITEM]", args);
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
			renderFailedJson(ReturnCode.WRONG_INPUT);
		}

		// 第五步下单支付：检查用户填写的“订单号”及“付款金额”
//		if (type == BuyerTaskStepType.ORDER_AND_PAY) {
//			validation.isTrue(vo.validateUrls(vo.picUrls, 1));
//			validation.isTrue(StringUtils.isNumeric(vo.orderNo));
//			validation.isTrue(NumberUtils.isNumber(vo.realPaidFee));
//			validation.required(messageId);
//			validation.min(messageId, 1);
//			handleWrongInput(false);
//
//			// 设置随机分配的订单留言记录
//			buyerTask.messageId = messageId;
//		}

		args = new Object[] { buyer.id, buyer.nick, type.toString(), buyerTask.taskId, buyerTask.id, buyerTask.status };
		buyerTaskStepLog.info("Buyer={}-{} perform save step={} of buyerTask={}-{} with status={}", args);

		BuyerTaskStep3.newInstance(buyerTask.id, buyer.id).type(type).content(vo).create(buyerTask);
		/**
		 * 返金币
		 */
		if(type == BuyerTaskStepType.CONFIRM_REFUND&&checkSecUrl(vo.secUrl)){
			buyerTask.confirmSellerRefund();
			renderSuccessJson();
		}
		
		/*
		 * 更新当前要做的任务步骤标记
		 */
		if (BuyerTaskStepType.ORDER_AND_PAY == type) {
			// 删除session中当前进行任务id
			session.remove(BizConstants.BUYER_TASK_ID3);

			// 删除当前任务进行的步骤
			session.remove(BizConstants.BUYER_TASK_STEP3);
		} else {
			// 更新当前任务进行的步骤为下一个步骤
			session.put(BizConstants.BUYER_TASK_STEP3, type.getNext());
		}
		renderSuccessJson();
	}
	
	
	
	
	
	

	/**
	 * 
	 * 保存每一步做任务的进度数据.
	 * 
	 * @param type
	 *            :买手任务进度步骤
	 * @param vo
	 *            :
	 * @since 0.1
	 * @author youblade
	 * @created 2014年8月21日 下午5:16:44
	 */
	public synchronized static void saveStep(@Required @Min(1) long id, @Required BuyerTaskStepType type, @Required BuyerTaskStepVo vo, long messageId) {
		handleWrongInput(false);
		BuyerTask buyerTask = BuyerTask.findById(id);
		boolean result = Task.isTaoBaoAndTmall(buyerTask.taskId);
		User buyer = getCurrentUser();
		Object[] args = new Object[] { buyer.id, buyer.nick, id, type.toString() };
		buyerTaskStepLog.info("Buyer={}-{} perform buyerTask={} save step={}", args);

		// 检查任务ID是否正确，以及该步骤是否已做过（通过比较BuyerTaskStepType的顺序）
		long currentTaskId = getCurrentBuyerTaskId();
		if(session.get(BizConstants.BUYER_TASK_STEP)==null){
			renderFailedJson(ReturnCode.OP_BIZ_LIMIT);
		}
		BuyerTaskStepType currentStep = BuyerTaskStepType.valueOf(session.get(BizConstants.BUYER_TASK_STEP));
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
			if (type == BuyerTaskStepType.CHOOSE_ITEM && !vo.validateUrls(vo.itemUrls, 2)) {
				renderFailedJson(ReturnCode.WRONG_INPUT);
			}
			// 第三步“浏览店铺及询盘”：四个其他商品链接，一个聊天截图的图片url
			if (type == BuyerTaskStepType.VIEW_AND_INQUIRY && (!vo.validateUrls(vo.itemUrls, 2) || !vo.validateUrls(vo.picUrls, 1))) {
				renderFailedJson(ReturnCode.WRONG_INPUT);
			}

			/*
			 * 检查上个做完的步骤
			 */
			buyerTaskStepLog.info("Fetch and Check Buyer={} last perform step of buyerTask={}", buyer.id, id);

			BuyerTaskStep lastTaskStep = BuyerTaskStep.findBuyerLastStep(currentTaskId, buyer.id);
			// 上个步骤记录不存在时，当前步骤必须是第一步“货比三家”
			if (lastTaskStep == null && currentStep != BuyerTaskStepType.CHOOSE_ITEM) {
				buyerTaskStepLog.error("[Buyer={}-{} perform buyerTask={} save step={}] Input step must be first:[CHOOSE_ITEM]", args);
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
			renderFailedJson(ReturnCode.WRONG_INPUT);
		}

		// 第五步下单支付：检查用户填写的“订单号”及“付款金额”
		if (type == BuyerTaskStepType.ORDER_AND_PAY) {
			validation.isTrue(vo.validateUrls(vo.picUrls, 1));
			validation.isTrue(StringUtils.isNumeric(vo.orderNo));
			validation.isTrue(NumberUtils.isNumber(vo.realPaidFee));
			validation.required(messageId);
			validation.min(messageId, 1);
			handleWrongInput(false);

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
		renderSuccessJson();
	}

	
	
	
	
	/**
	 * 保存每一步做推广的进度数据.
	 *            :买手推广进度步骤
	 */
	public synchronized static void saveStep2(@Required @Min(1) long id, @Required BuyerTaskStepType type, @Required BuyerTaskStepVo vo, long messageId) {
		handleWrongInput(false);
		System.out.println("================================");
		System.out.println("================================");
		System.out.println("================================");
		
		System.out.println("----------picUrls------------"+vo.picUrls);
		System.out.println("----------picUrls------------"+vo.picUrls);
		System.out.println("----------picUrls------------"+vo.picUrls);
		System.out.println("----------picUrls------------"+vo.picUrls);
		System.out.println("----------picUrls------------"+vo.picUrls);
		System.out.println("----------picUrls------------"+vo.picUrls);
		
		BuyerTask2 buyerTask = BuyerTask2.findById(id);
		boolean result = Task2.isTaoBaoAndTmall(buyerTask.taskId);
		User buyer = getCurrentUser();
		Object[] args = new Object[] { buyer.id, buyer.nick, id, type.toString() };
		buyerTaskStepLog.info("Buyer={}-{} perform buyerTask={} save step={}", args);

		// 检查任务ID是否正确，以及该步骤是否已做过（通过比较BuyerTaskStepType的顺序）
		long currentTaskId = getCurrentBuyerTaskId2();
		System.out.println("---------"+session.get(BizConstants.BUYER_TASK_STEP2));
		System.out.println("---------"+session.get(BizConstants.BUYER_TASK_STEP2));
		System.out.println("---------"+session.get(BizConstants.BUYER_TASK_STEP2));
		System.out.println("---------"+session.get(BizConstants.BUYER_TASK_STEP2));
		if(session.get(BizConstants.BUYER_TASK_STEP2)==null){
			renderFailedJson(ReturnCode.OP_BIZ_LIMIT);
		}
		BuyerTaskStepType currentStep = BuyerTaskStepType.valueOf(session.get(BizConstants.BUYER_TASK_STEP2));
		if (currentTaskId != id || currentStep.getOrder() > type.getOrder()) {
			System.out.println("id-----------------"+id);
			System.out.println("currentid=========="+currentTaskId);
			Object[] args2 = new Object[] { buyer.id, buyer.nick, id, type.toString(), currentTaskId, currentStep.title };
			buyerTaskStepLog.error("[Buyer={}-{} perform buyerTask={} save step={}] Current SESSION taskId={} step={}", args2);
			System.out.println("XXXXXXXXXXXXXXXXXXXXX1XXXXXXXXXXXXXXXXXXXXXXXXXXX");
			renderFailedJson(ReturnCode.OP_BIZ_LIMIT);
			System.out.println("XXXXXXXXXXXXXXXXXXXXX1XXXXXXXXXXXXXXXXXXXXXXXXXXX");
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
			
				if (type == BuyerTaskStepType.VIEW_AND_INQUIRY && (!vo.validate(vo.itemUrls))) {
					System.out.println("出错了");
					System.out.println("出错了");
					System.out.println("出错了");
					System.out.println("出错了");
					System.out.println("出错了");
					renderFailedJson(ReturnCode.WRONG_INPUT);
				}
			
			/*
			 * 检查上个做完的步骤
			 */
			buyerTaskStepLog.info("Fetch and Check Buyer={} last perform step of buyerTask={}", buyer.id, id);

			BuyerTaskStep2 lastTaskStep = BuyerTaskStep2.findBuyerLastStep(currentTaskId, buyer.id);
			// 上个步骤记录不存在时，当前步骤必须是第一步“货比三家”
			if (lastTaskStep == null && currentStep != BuyerTaskStepType.CHOOSE_ITEM) {
				buyerTaskStepLog.error("[Buyer={}-{} perform buyerTask={} save step={}] Input step must be first:[CHOOSE_ITEM]", args);
				System.out.println("XXXXXXXXXXXXXXXXXXXXX2XXXXXXXXXXXXXXXXXXXXXXXXXXX");
				renderFailedJson(ReturnCode.OP_BIZ_LIMIT);
				System.out.println("XXXXXXXXXXXXXXXXXXXXX2XXXXXXXXXXXXXXXXXXXXXXXXXXX");
			}
			// 上个步骤记录存在时，当前步骤必须为“上个步骤记录的下一个步骤”
			if (lastTaskStep != null && currentStep != lastTaskStep.type.getNext()) {
				Object[] args3 = new Object[] { buyer.id, buyer.nick, id, type.toString(), currentTaskId, currentStep.title };
				buyerTaskStepLog.error("[Buyer={}-{} perform buyerTask={} save step={}] Current SESSION taskId={} step={}", args3);
				System.out.println("XXXXXXXXXXXXXXXXXXXXX3XXXXXXXXXXXXXXXXXXXXXXXXXXX");
				renderFailedJson(ReturnCode.OP_BIZ_LIMIT);
				System.out.println("XXXXXXXXXXXXXXXXXXXXX3XXXXXXXXXXXXXXXXXXXXXXXXXXX");
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

			// 设置随机分配的订单留言记录
			buyerTask.messageId = messageId;
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
		renderSuccessJson();
	}
	
	
	
	/**
	 * 
	 * 第二步： 通过商品链接对找到的商品进行核对.
	 */
	public static void checkItem3(@Required String itemUrl) {
		validation.isTrue(StringUtils.isNotBlank(itemUrl));
		// 限制最大长度300个字符串
		validation.maxSize(itemUrl, 300);
		handleWrongInput(false);

		// 获取买手当前正在做的任务
		long buyerTaskId = getCurrentBuyerTaskId3();
		long buyerId = getCurrentUser().id;
		
		BuyerTaskVo3 taskVo = BuyerTask3.findVoById(buyerTaskId, buyerId);
		System.out.println("-------taskVo---------"+taskVo);
		if (taskVo == null || taskVo.status != TaskStatus.WAIT_PAY) {
			Object[] args = new Object[] { buyerId, itemUrl, buyerTaskId };
			log.warn("User={} check item_url={} for buyerTask={} FAIL:[data error]", args);
			renderFailedJson(ReturnCode.FAIL);
		}
		System.out.println("-------taskVo.itemUrl---------"+taskVo.itemUrl);
		System.out.println("-------.itemUrl---------"+itemUrl);
		// 先比较商品url是否完全一致
		if (taskVo.itemUrl.equals(itemUrl)) {
			renderSuccessJson();
		}
		System.out.println("==============================");
		System.out.println("==============================");
		System.out.println("==============================");
		System.out.println("==============================");
		
		
		
		
		
		// 若url不完全一致，则从商品链接中提取商品ID与任务目标商品进行比较
		
		String inputItemId = Task3.getItemIdByPlatform(taskVo.platform, itemUrl);
		System.out.println("-----------inputItemId-------------"+inputItemId);
		System.out.println("------------------------"+inputItemId);
		System.out.println("------------------------"+inputItemId);
		
		if (Strings.isNullOrEmpty(inputItemId) || !inputItemId.equals(taskVo.itemId.toString())) {
			renderFailedJson(ReturnCode.FAIL);
		}

		renderSuccessJson();
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
	public static void checkItem(@Required String itemUrl) {
		validation.isTrue(StringUtils.isNotBlank(itemUrl));
		// 限制最大长度300个字符串
		validation.maxSize(itemUrl, 300);
		handleWrongInput(false);

		// 获取买手当前正在做的任务
		long buyerTaskId = getCurrentBuyerTaskId();
		long buyerId = getCurrentUser().id;
		BuyerTaskVo taskVo = BuyerTask.findVoById(buyerTaskId, buyerId);
		if (taskVo == null || taskVo.status != TaskStatus.WAIT_PAY) {
			Object[] args = new Object[] { buyerId, itemUrl, buyerTaskId };
			log.warn("User={} check item_url={} for buyerTask={} FAIL:[data error]", args);
			renderFailedJson(ReturnCode.FAIL);
		}

		// 先比较商品url是否完全一致
		if (taskVo.itemUrl.equals(itemUrl)) {
			renderSuccessJson();
		}

		// 若url不完全一致，则从商品链接中提取商品ID与任务目标商品进行比较
		
		String inputItemId = Task.getItemIdByPlatform(taskVo.platform, itemUrl);
		
		
		if (Strings.isNullOrEmpty(inputItemId) || !inputItemId.equals(taskVo.itemId.toString())) {
			renderFailedJson(ReturnCode.FAIL);
		}

		renderSuccessJson();
	}

	/**
	 * 
	 * 我的任务->“快递单已打印，待确认收货”：买手确认收货并复制淘宝后台好评
	 * 
	 * @since v0.1
	 * @author tr0j4n
	 * @created 2014-8-16 下午9:48:50
	 */
	public static void confirmRecv(@Required Platform platform, @Required long id) {
		handleWrongInput(false);
		renderArgs.put("currPlatform", platform);
		// 获取待处理的任务
		TaskSearchVo vo = TaskSearchVo.newInstance().buyerId(getCurrentUser().id).platform(platform).buyerTaskId(id);
		List<BuyerTaskVo> list = BuyerTask.findListForConfirm(vo);
		for (BuyerTaskVo buyerTaskVo : list) {
            buyerTaskVo.value();
        }
		String[] includeFields = new String[] { "id","taskIdStr","orderId", "itemId", "itemTitle", "itemPic","goodCommentWords","goodCommentImg", "shopId", "shopName", "buyerAccountNick",
				"platform" };
		renderArgs.put("jsonTaskList", JsonUtil.toJson(list, BuyerTaskVo.class, includeFields));
		
		//Task task=Task.findById("goodCommentWords",vo.taskId);
		//renderArgs.put("goodCommentWords", task.goodCommentWords);
		// TODO 存在待处理任务的平台
		renderArgs.put(BizConstants.PLATFORMS, Platform.values());
		render();
	}

	
	/**
	 * 
	 * 我的推广->“快递单已打印，待确认收货”：买手确认收货并复制淘宝后台好评
	 */
	public static void confirmRecv2(@Required Platform2 platform, @Required long id) {
		System.out.println("=====================");
		System.out.println("=====================");
		System.out.println("=================2222===="+platform);
		System.out.println("=====================");
		System.out.println("=====================");
		handleWrongInput(false);
		renderArgs.put("currPlatform", platform);
		// 获取待处理的任务
		TaskSearchVo2 vo = TaskSearchVo2.newInstance().buyerId(getCurrentUser().id).platform(platform).buyerTaskId(id);
		List<BuyerTaskVo2> list = BuyerTask2.findListForConfirm(vo);
		for (BuyerTaskVo2 buyerTaskVo : list) {
            buyerTaskVo.value();
        }
		String[] includeFields = new String[] { "id","taskIdStr","orderId", "itemId", "itemTitle", "itemPic","goodCommentWords","goodCommentImg", "shopId", "shopName", "buyerAccountNick",
				"platform" };
		renderArgs.put("jsonTaskList", JsonUtil.toJson(list, BuyerTaskVo2.class, includeFields));
		// TODO 存在待处理任务的平台
		renderArgs.put(BizConstants.PLATFORMS, Platform2.values());
		render();
	}
	
	
	
	/**
	 * 
	 * 确认收货并好评.
	 * 
	 * @param id
	 *            :买手任务ID
	 * @param vo
	 * @since 0.1
	 * @author youblade
	 * @created 2014年9月9日 上午11:35:18
	 */
	public static void confirmRecvGoods(@Required long id, @Required BuyerTaskStepVo vo) {
		if (vo != null) {
			validation.required(vo.picUrls);
			// 该功能仅上传一张图片，校验第一个即可
			String picUrl = vo.picUrls.get(0);
			validation.required(picUrl);
			validation.url(picUrl);
		}
		handleWrongInput(false);

		// 检查任务是否存在、是否所属当前用户、是否待确认发货
		BuyerTask buyerTask = BuyerTask.findById(id);
		if(!isAdminOperate()) {
			if (buyerTask == null || !buyerTask.isBelongTo(getCurrentUser()) || buyerTask.status != TaskStatus.WAIT_CONFIRM) {
				renderFailedJson(ReturnCode.WRONG_INPUT);
			}
		}
		buyerTask.confirmGoodsAndRate(vo);
		renderSuccessJson();
	}

	
	/**
	 * 
	 * 确认收货并好评.
	 * 
	 * @param id
	 *            :买手任务ID
	 * @param vo
	 * @since 0.1
	 * @author youblade
	 * @created 2014年9月9日 上午11:35:18
	 */
	public static void confirmRecvGoods3(@Required long id, @Required BuyerTaskStepVo vo) {
		if (vo != null) {
			validation.required(vo.picUrls);
			// 该功能仅上传一张图片，校验第一个即可
			String picUrl = vo.picUrls.get(0);
			validation.required(picUrl);
			validation.url(picUrl);
		}
		handleWrongInput(false);

		// 检查任务是否存在、是否所属当前用户、是否待确认发货
		BuyerTask3 buyerTask = BuyerTask3.findById(id);
		if(!isAdminOperate()) {
			if (buyerTask == null || !buyerTask.isBelongTo(getCurrentUser()) || buyerTask.status != TaskStatus.WAIT_CONFIRM) {
				renderFailedJson(ReturnCode.WRONG_INPUT);
			}
		}
		buyerTask.confirmGoodsAndRate(vo);
		renderSuccessJson();
	}
	
	
	
	/**
	 * 确认并好评.
	 */
	public static void confirmRecvGoods2(@Required long id, @Required BuyerTaskStepVo vo) {
		if (vo != null) {
			validation.required(vo.picUrls);
			// 该功能仅上传一张图片，校验第一个即可
			String picUrl = vo.picUrls.get(0);
			validation.required(picUrl);
			validation.url(picUrl);
		}
		handleWrongInput(false);

		// 检查任务是否存在、是否所属当前用户、是否待确认发货
		BuyerTask2 buyerTask = BuyerTask2.findById(id);
		if(!isAdminOperate()) {
			if (buyerTask == null || !buyerTask.isBelongTo(getCurrentUser()) || buyerTask.status != TaskStatus.WAIT_CONFIRM) {
				renderFailedJson(ReturnCode.WRONG_INPUT);
			}
		}
		buyerTask.confirmGoodsAndRate(vo);
		renderSuccessJson();
	}
	
	
	
	
	
	/**
	 * 
	 * 买手确认商家已经退款
	 * 
	 * @since v0.1
	 * @author tr0j4n
	 * @created 2014-8-20 下午2:51:54
	 */
	public static void verifyRefund(@Required Platform platform) {
		// 已选择的平台参数
		renderArgs.put("currPlatform", platform);
		renderArgs.put(BizConstants.PLATFORMS, Platform.values());
		render();
	}

	/**
	 * 
	 * 买手确认商家已经退款
	 */
	public static void verifyRefund2(@Required Platform2 platform) {
		// 已选择的平台参数
		renderArgs.put("currPlatform", platform);
		renderArgs.put(BizConstants.PLATFORMS, Platform2.values());
		render();
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

	/**
	 * 
	 * 核实退款
	 */
	public static void confirmRefund3(@Required BuyerTask3 bt) {
		if (bt != null) {
			validation.required(bt.id);
			validation.required(bt.taskId);
			validation.required(bt.status);
		}
		handleWrongInput(false);
		
		BuyerTask3 buyerTask = BuyerTask3.findById(bt.id);
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
	
	
	
	/**
	 * 
	 * 核实退款
	 */
	public static void confirmRefund2(@Required BuyerTask2 bt) {
		if (bt != null) {
			validation.required(bt.id);
			validation.required(bt.taskId);
			validation.required(bt.status);
		}
		handleWrongInput(false);
		
		BuyerTask2 buyerTask = BuyerTask2.findById(bt.id);
		//查看是否为管理员操作，如果是，则不需要验证
		if(!isAdminOperate()) {
			User buyer = getCurrentUser();
			if (buyerTask == null || buyerTask.isNotBelongTo(buyer) || buyerTask.status != TaskStatus.REFUNDING) {
				renderFailedJson(ReturnCode.WRONG_INPUT);
			}
		}
		buyerTask.confirmSellerRefund();
		renderSuccessJson();
	}
	
	
	
	/**
	 * 
	 * 【买手】我的任务->核实平台退款页面
	 * 
	 * @param platform
	 * @since v1.7
	 * @author youblade
	 * @created 2014年11月27日 下午6:53:37
	 */
	public static void verifySysRefund() {
		renderArgs.put(BizConstants.PLATFORMS, Platform.values());
		render();
	}

	
	/**
	 * 【买手】我的推广->核实平台退款页面
	 */
	public static void verifySysRefund2() {
		renderArgs.put(BizConstants.PLATFORMS, Platform2.values());
		render();
	}
	
	
	/**
	 * 
	 * 【买手】我的任务->核实平台退款页面：确认“平台返款”任务的退款金额
	 * 
	 * @param id
	 * @param isReject
	 * @since v1.7
	 * @author youblade
	 * @created 2014年11月27日 下午6:53:52
	 */
	public synchronized static void confirmSysRefund(@Required @Min(1) long id, boolean isReject) {
		handleWrongInput(false);

		BuyerTask buyerTask = BuyerTask.findById(id);
		//是否是管理员处理
		if(!isAdminOperate()) {
			// Check BuyerTask
			User buyer = getCurrentUser();
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
	 * 【买手】我的任务->核实平台退款页面：确认“平台返款”任务的退款金额
	 */
	public synchronized static void confirmSysRefund3(@Required @Min(1) long id, boolean isReject) {
		handleWrongInput(false);

		BuyerTask3 buyerTask = BuyerTask3.findById(id);
		//是否是管理员处理
		if(!isAdminOperate()) {
			// Check BuyerTask
			User buyer = getCurrentUser();
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
		BuyerTaskProcessor3.confirmSysRefund(buyerTask, isReject);
		
		renderSuccessJson();
	}
	
	
	
	
	/**
	 * 
	 * 撤销未到期内的“平台返款”任务进程
	 * 
	 * @param id
	 * @since v1.7
	 * @author youblade
	 * @created 2014年11月28日 下午3:33:44
	 */
	public static void cancelSysRefund(@Required @Min(1) long id) {
		handleWrongInput(false);

		// Check BuyerTask
		User buyer = getCurrentUser();
		BuyerTask buyerTask = BuyerTask.findById(id);
		if (buyerTask == null || buyerTask.isNotBelongTo(buyer) || buyerTask.status != TaskStatus.WAIT_SYS_REFUND) {
			renderFailedJson(ReturnCode.WRONG_INPUT);
		}

		BuyerTask bt = BuyerTask.instance(id).status(TaskStatus.WAIT_BUYER_CONFIRM_SYS_REFUND);
		BuyerTaskProcessor.cancelConfirmedSysRefund(bt);
		renderSuccessJson();
	}

	
	/**
	 * 
	 * 我的浏览->任务进展：撤销买手任务
	 */
	public static void cancelBuyerTask3(@Required @Min(1) long id) {
		handleWrongInput(false);

		User buyer = getCurrentUser();
		BuyerTask3 bt = BuyerTask3.findById(id);
		if (bt == null || bt.isNotBelongTo(buyer)) {
			renderFailedJson(ReturnCode.WRONG_INPUT);
		}

		// 状态限制
		if (bt.status != TaskStatus.RECIEVED && bt.status != TaskStatus.WAIT_PAY) {
			renderFailedJson(ReturnCode.BIZ_LIMIT);
		}

		Object[] args = new Object[] { buyer.id, buyer.nick, bt.taskId, bt.id };
		buyerTaskStepLog.info("Buyer={}-{} cancle buyerTask={}-{}", args);

		// 撤销任务
		bt.cancel();
		
		// 若当前操作任务被撤销 则将其从session中删除
//		if (Long.toString(id).equals(session.get(BizConstants.BUYER_TASK_ID))) {
//			session.remove(BizConstants.BUYER_TASK_ID);
//		}

		renderSuccessJson();
	}
	
	
	
	/**
	 * 
	 * 我的任务->任务进展：撤销买手推广
	 * @author 尤齐春
	 * @created 2016-8-13 
	 */
	public static void cancelBuyerTask2(@Required @Min(1) long id) {
		handleWrongInput(false);

		User buyer = getCurrentUser();
		BuyerTask2 bt = BuyerTask2.findById(id);
		if (bt == null || bt.isNotBelongTo(buyer)) {
			renderFailedJson(ReturnCode.WRONG_INPUT);
		}

		// 状态限制
		if (bt.status != TaskStatus.RECIEVED && bt.status != TaskStatus.WAIT_PAY) {
			renderFailedJson(ReturnCode.BIZ_LIMIT);
		}

		Object[] args = new Object[] { buyer.id, buyer.nick, bt.taskId, bt.id };
		buyerTaskStepLog.info("Buyer={}-{} cancle buyerTask={}-{}", args);

		// 撤销推广
		bt.cancel();
		
		// 若当前操作推广被撤销 则将其从session中删除
		if (Long.toString(id).equals(session.get(BizConstants.BUYER_TASK_ID))) {
			session.remove(BizConstants.BUYER_TASK_ID);
		}

		renderSuccessJson();
	}

	
	
	/**
	 * 
	 * 我的推广->推广进展：撤销买手推广
	 * 
	 * @param id
	 * @since v0.1
	 * @author moloch
	 * @created 2014-9-23 上午11:21:52
	 */
	public static void cancelBuyerTask(@Required @Min(1) long id) {
		handleWrongInput(false);

		User buyer = getCurrentUser();
		BuyerTask bt = BuyerTask.findById(id);
		if (bt == null || bt.isNotBelongTo(buyer)) {
			renderFailedJson(ReturnCode.WRONG_INPUT);
		}

		// 状态限制
		if (bt.status != TaskStatus.RECIEVED && bt.status != TaskStatus.WAIT_PAY) {
			renderFailedJson(ReturnCode.BIZ_LIMIT);
		}

		Object[] args = new Object[] { buyer.id, buyer.nick, bt.taskId, bt.id };
		buyerTaskStepLog.info("Buyer={}-{} cancle buyerTask={}-{}", args);

		// 撤销任务
		bt.cancel();
		
		// 若当前操作任务被撤销 则将其从session中删除
		if (Long.toString(id).equals(session.get(BizConstants.BUYER_TASK_ID))) {
			session.remove(BizConstants.BUYER_TASK_ID);
		}

		renderSuccessJson();
	}

	
	
	
	/**
	 * 
	 * 任务列表->买手接任务
	 * 
	 * @param buyerTask
	 * @since 0.1
	 * @author youblade
	 * @created 2014年8月8日 下午7:11:20
	 */
	public synchronized static void take(@Required BuyerTask bt) {
		if (bt != null) {
			validation.required(bt.taskId);
			validation.required(bt.device);
			validation.required(bt.buyerAccountId);
			validation.required(bt.buyerAccountNick);
		}
		handleWrongInput(false);

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

		User buyer = getCurrentUser();
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

		// 金币不足需要充值购买才能接手任务
		if (buyer.ingot < BizConstants.TASK_TAKING_INGOT) {
			renderFailedJson(ReturnCode.BIZ_LIMIT, "您的金币不足，无法领取任务~~");
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
		
		// 账号和商家之间的限制
		if(task.platform==Platform.TAOBAO||task.platform==Platform.TMALL)//目前只限制淘宝平台的
		if (!isAvailableBuyerIdAndSellerId(buyer.id, task.sellerId)) {
			renderFailedJson(ReturnCode.BIZ_LIMIT, "你在该商家已经领取过任务");
		}
		// 账号和店铺之间的限制
//		if (!isAvailableBuyerIdAndShopId(buyer.id, task.shopId)) {
//			renderFailedJson(ReturnCode.BIZ_LIMIT, "你在该店铺已经领取过任务");
//		}
		// 买号和店铺之间的限制
		if (!isValidateBuyerAccountAndShopId(bt.buyerAccountId, task.shopId)) {
			renderFailedJson(ReturnCode.BIZ_LIMIT, "旺旺号在该店铺已经领取过任务");

		}
		// 买号和商品之间的限制
		if (!isAvailableBuyerAccountAndItem(bt.buyerAccountId, task.itemId)) {
			renderFailedJson(ReturnCode.BIZ_LIMIT, "该旺旺号已经领取过该任务");
		}
		// TODO 新手必须完成新手任务后才能接手其他任务
		// BuyerAccount.findForTakingTask(vo)
		// task.searchPlans
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
		renderJson(bt.id);
	}
	
	 /**
     * 在任务列表中判断是不是收藏加购
     * 如果是收藏加购应执行到第四步，才可以继续接任务。
     * @Title: isCollection 
     * @Description: TODO
     * @param lists
     * @return
     * @return: boolean
     */
   private static boolean isCollection(List<BuyerTaskVo3> lists,long buyerid) {
		// TODO Auto-generated method stub
   	boolean iscollection=true;
   	if(lists==null||lists.size()==0){
   		return true;
   	}
   	for(BuyerTaskVo3 v:lists){
   		if(v.collectionType==null){
   			iscollection=false;
   			break;
   		}
   		BuyerTaskStep laststep=BuyerTaskStep.findBuyerLastStep(v.id, buyerid);
		if(laststep==null){
			iscollection= false;
			break;
		}
		if(laststep.type.getOrder()<BuyerTaskStepType.VIEW_AND_INQUIRY.getOrder()){
			iscollection= false;
			break;
		}
   	}
		return iscollection;
	}
   
	
	/**
	 * 
	 * 浏览列表->买手接浏览
	 */
	public synchronized static void take3(@Required BuyerTask3 bt) {
		if (bt != null) {
			validation.required(bt.taskId);
			validation.required(bt.device);
			validation.required(bt.buyerAccountId);
			validation.required(bt.buyerAccountNick);
		}
		handleWrongInput(false);

		/*
		 * 检查是否可接手任务
		 */

		User buyer = getCurrentUser();
		BuyerAccount3 buyerAccount = BuyerAccount3.findById(bt.buyerAccountId);
		if (!BuyerAccount3.validate(buyerAccount, buyer)) {
			log.error("买手={}违规注册多个账号，本次使用买号={}不属于当前登录用户！", buyer.nick, bt.buyerAccountNick);
			renderFailedJson(ReturnCode.BIZ_LIMIT, "当前登录用户状态异常，请退出后再登录~~");
		}

		// 该任务已被用户接手过，不能重复领取
		if (BuyerTask3.isDuplicateTaking(bt.buyerAccountId, bt.taskId)) {
			renderFailedJson(ReturnCode.BIZ_LIMIT, "你已经领取过该任务了，换一个吧~~");
		}
		// 存在尚未完成的任务，不能领取新任务
		 List<BuyerTaskVo3> bto=BuyerTask3.findTodoTasks(buyer.id);
        boolean isConllection=isCollection(bto,buyer.id);
        if(!isConllection){
        	renderFailedJson(ReturnCode.BIZ_LIMIT);
        }
		// 父任务数据异常
		Task3 task = getTask3(bt.taskId);
		if (task == null || task.status != TaskStatus.PUBLISHED) {
			renderFailedJson(ReturnCode.WRONG_INPUT);
		}

		// 任务已全被领取完
		if (task.isTakenOver(bt.device)) {
			renderFailedJson(ReturnCode.TASK_TAKE_OVER);
		}

		// 金币不足需要充值购买才能接手任务
		if (buyer.ingot < BizConstants.TASK_TAKING_INGOT) {
			renderFailedJson(ReturnCode.BIZ_LIMIT, "您的金币不足，无法领取任务~~");
		}

		// 平台限制每个【买号】5单/天，30单/周，90单/月
		TaskStats taskStats = TaskStats.findForBuyerUntilNow(bt.buyerAccountId);
		
		String day=SysConfig.getConfigValue(SysConfigKey.BUYER_TASK_DAY_COUNT);
        if(StringUtils.isEmpty(day)){
            day=Config.BUYER_TASK_DAY_COUNT3;
        }
        
        String week=SysConfig.getConfigValue(SysConfigKey.BUYER_TASK_WEEK_COUNT);
        if(StringUtils.isEmpty(week)){
            week=Config.BUYER_TASK_WEEK_COUNT3;
        }
        
        String mouth=SysConfig.getConfigValue(SysConfigKey.BUYER_TASK_MONTH_COUNT);
        if(StringUtils.isEmpty(mouth)){
            mouth=Config.BUYER_TASK_MONTH_COUNT3;
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
		
		// 账号和商家之间的限制
		if(task.platform==Platform3.TAOBAO||task.platform==Platform3.TMALL)//目前只限制淘宝平台的
		if (!isAvailableBuyerIdAndSellerId(buyer.id, task.sellerId)) {
			renderFailedJson(ReturnCode.BIZ_LIMIT, "你在该商家已经领取过任务");
		}
		// 账号和店铺之间的限制
		// 买号和店铺之间的限制
		if (!isValidateBuyerAccountAndShopId3(bt.buyerAccountId, task.shopId)) {
			renderFailedJson(ReturnCode.BIZ_LIMIT, "旺旺号在该店铺已经领取过任务");

		}
		// 买号和商品之间的限制
		if (!isAvailableBuyerAccountAndItem3(bt.buyerAccountId, task.itemId,task.type.toString())) {
			renderFailedJson(ReturnCode.BIZ_LIMIT, "该旺旺号已经领取过该任务");
		}
		// TODO 新手必须完成新手任务后才能接手其他任务
		// BuyerAccount.findForTakingTask(vo)
		// task.searchPlans
		boolean result=Task3.isTaoBaoAndTmall(bt.taskId);//除淘宝、天猫外的其他平台
		Object[] args = new Object[] { buyer.id, buyer.nick, bt.taskId };
		buyerTaskStepLog.info("Buyer={}-{} take Task={}", args);

		/*
		 * 创建买手任务记录
		 */
		bt.sellerId = task.sellerId;
	//	bt.pledgeIngot = BizConstants.TASK_TAKING_INGOT * 100L;
		bt.pledgeIngot=(long)BizConstants.TASK_TAKING_INGOT;
		bt.rewardIngot = task.calculateRewardIngot(bt.device,task);
		
		
		System.out.println("======rewardIngot==========="+bt.rewardIngot);
		System.out.println("=======rewardIngot=========="+bt.rewardIngot);
		System.out.println("=======rewardIngot=========="+bt.rewardIngot);
		System.out.println("=======rewardIngot=========="+bt.rewardIngot);
		System.out.println("=======rewardIngot=========="+bt.rewardIngot);
		
		
		
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
		TaskItemSearchPlan3 p=TaskItemSearchPlan3.getOnePlan(bt.taskId);
		if(p!=null&&p.takenNum<p.totalNum){
			p.takenNum+=1;
			TaskItemSearchPlan3.update(p);
			bt.searchPlanId=p.id;
		}
		bt.create();

		// 返回ID以方便前端跳转到做任务的页面
		renderJson(bt.id);
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
	public synchronized static void take2(@Required BuyerTask2 bt) {
		if (bt != null) {
			validation.required(bt.taskId);
			validation.required(bt.device);
			validation.required(bt.buyerAccountId);
			validation.required(bt.buyerAccountNick);
		}
		handleWrongInput(false);
		/*
		 * 检查是否可接手任务
		 */
		// 存在未完成的任务
		if (session.contains(BizConstants.BUYER_TASK_ID2)) {
			if (BuyerTask2.findById(Long.parseLong(session.get(BizConstants.BUYER_TASK_ID2))).status == TaskStatus.CANCLED) {
				System.out.println("------------------------");
				session.remove(BizConstants.BUYER_TASK_ID2);
			} else {
				System.out.println("------------------------++++");
				System.out.println("------------------------++++");
				System.out.println("------------------------++++");
				System.out.println("------------------------++++");
				renderFailedJson(ReturnCode.TASK_UNFINISHED, session.get(BizConstants.BUYER_TASK_ID2));
			}
		}

		User buyer = getCurrentUser();
		BuyerAccount2 buyerAccount = BuyerAccount2.findById(bt.buyerAccountId);
		System.out.println(""+buyerAccount.toString());
		if (!BuyerAccount2.validate(buyerAccount, buyer)) {
			log.error("买手={}违规注册多个账号，本次使用买号={}不属于当前登录用户！", buyer.nick, bt.buyerAccountNick);
			renderFailedJson(ReturnCode.BIZ_LIMIT, "当前登录用户状态异常，请退出后再登录~~");
		}
		
		// 该任务已被用户接手过，不能重复领取
		if (BuyerTask2.isDuplicateTaking(bt.buyerAccountId, bt.taskId)) {
			System.out.println("--------------2----------++++");
			System.out.println("--------------2----------++++");
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
			System.out.println("父任务数据异常");
			System.out.println("父任务数据异常");
			System.out.println("父任务数据异常");
			System.out.println("父任务数据异常");
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
		
		// 金币不足需要充值购买才能接手任务
		if (buyer.ingot < BizConstants.TASK_TAKING_INGOT) {
			System.out.println("您的金币不足，无法领取任务");
			renderFailedJson(ReturnCode.BIZ_LIMIT, "您的金币不足，无法领取任务~~");
		}
		
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
				/*// 买号和商品之间的限制
				if (!isAvailableBuyerAccountAndItem2(bt.buyerAccountId, task.itemId)) {
					renderFailedJson(ReturnCode.BIZ_LIMIT, "该推广号已经领取过该任务");
				}*/
		
		// TODO 新手必须完成新手任务后才能接手其他任务
		boolean result=Task2.isTaoBaoAndTmall(bt.taskId);//除淘宝、天猫外的其他平台
		Object[] args = new Object[] { buyer.id, buyer.nick, bt.taskId };
		buyerTaskStepLog.info("Buyer={}-{} take Task={}", args);
		
		
		 //* 创建买手任务记录
		 
		bt.sellerId = task.sellerId;
		bt.pledgeIngot = BizConstants.TASK_TAKING_INGOT * 100L;
		System.out.println("--------------------任务押金------------------"+bt.pledgeIngot);
//		bt.pledgeIngot = BizConstants.TASK_TAKING_INGOT * 100L;
		bt.rewardIngot = task.calculateRewardIngot(bt.device);
		System.out.println("--------------------任务最终佣金-----------------"+bt.rewardIngot);
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
		System.out.println("id---------------"+bt.id);
		System.out.println("id---------------"+bt.id);
		System.out.println("id---------------"+bt.id);
		System.out.println("id---------------"+bt.id);
		System.out.println("id---------------"+bt.id);
		System.out.println("id---------------"+bt.id);
		System.out.println("id---------------"+bt.id);
		System.out.println("id---------------"+bt.id);
		// 返回ID以方便前端跳转到做任务的页面
		renderJson(bt.id);
	}
	
	
	
	
	
	
	
	
	/**
	 * 
	 * TODO查询task
	 *
	 * @param taskId
	 * @return
	 * @since  v2.0
	 * @author fufei
	 * @created 2015年1月28日 下午1:41:39
	 */
	public static Task getTask(long taskId) {
		// 父任务数据异常
		return Task.findById("id,platform,status,base_order_ingot,extra_reward_ingot,pc_order_num,mobile_order_num,pc_taken_count,mobile_taken_count,"
				+ "finished_count,seller_id,create_time,shop_id,item_id,sys_refund,"
				+ "publish_timer_interval,publish_timer_value,last_batch_publish_time,good_comment_img", taskId);
	}
	
	public static Task3 getTask3(long taskId) {
		// 父任务数据异常
				return Task3.findById("id,platform,status,base_order_ingot,extra_reward_ingot,pc_order_num,mobile_order_num,pc_taken_count,mobile_taken_count,"
						+ "finished_count,seller_id,create_time,shop_id,item_id,sys_refund,"
						+ "publish_timer_interval,publish_timer_value,last_batch_publish_time,good_comment_img,type", taskId);
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
	 * 账号和店铺之间的限制
	 * 
	 * @param buyerId
	 * @param shopId
	 * @return
	 * @since v2.0
	 * @author fufei
	 * @created 2015年1月13日 下午4:52:28
	 */
	public static boolean isAvailableBuyerIdAndShopId(long buyerId, long shopId) {
		if(getCurrentUser().vipStatus==VipStatus.SPECIAL){
			return true;
		}
		List<BuyerTaskVo> shopBuyerTasks = TaskStats.findBuyerIdAndShopId(buyerId, shopId);
		if (CollectionUtils.isNotEmpty(shopBuyerTasks)) {
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

	
	/**
	 * 
	 * 买号和店铺之间的限制
	 */
	public static boolean isValidateBuyerAccountAndShopId3(long buyerAccountId, Long shopId) {
		Validate.notNull(buyerAccountId);
		Validate.notNull(shopId);
		// 取【最近15天】【该买号】在【该商家】的（非取消）任务记录
		List<BuyerTaskVo3> buyerTasks = TaskStats3.findBuyerAccountIdAndShopId(buyerAccountId, shopId);
		
		if(CollectionUtils.isNotEmpty(buyerTasks)){
			return false;
		}
		return true;
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
	public static boolean isAvailableBuyerAccountAndItem(long buyerAccountId, String itemId) {
		Validate.notNull(buyerAccountId);
		Validate.notEmpty(itemId);
		if (CollectionUtils.isNotEmpty(TaskStats.selectByBuyerAccountIdAndItemIdAndPeriod(buyerAccountId, itemId))) {
			return false;
		}
		return true;
	}

	
	/**
	 * 买号和商品之间的限制
	 */
	public static boolean isAvailableBuyerAccountAndItem3(long buyerAccountId, String itemId,String type) {
		Validate.notNull(buyerAccountId);
		if(!"JHS".equals(type)){
			Validate.notEmpty(itemId);
			if (CollectionUtils.isNotEmpty(TaskStats3.selectByBuyerAccountIdAndItemIdAndPeriod(buyerAccountId, itemId))) {
				return false;
			}
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
     * 在买手接任务的时候获取商家的特殊要求
     *
     * @since  v2.0
     * @author Mark Xu
     * @created 2015-3-23 下午3:28:44
     */
    public static void getTaskRequest(@Required long taskId){
    	handleWrongInput(false);
    	String taskRequest = Task.findById(taskId).taskRequest;
    	renderJson(taskRequest);
    }
    
    /**
     * 在买手接任务的时候获取商家的特殊要求
     */
    public static void getTaskRequest3(@Required long taskId){
    	handleWrongInput(false);
    	String taskRequest = Task3.findById(taskId).taskRequest;
    	renderJson(taskRequest);
    }
    
    
    /**
     * 
     * 在买手接任务的时候获取商家的特殊要求
     *
     * @since  v2.0
     * @author yqc6-8-9
     */
    public static void getTaskRequest2(@Required long taskId){
    	handleWrongInput(false);
    	String taskRequest = Task2.findById(taskId).taskRequest;
    	renderJson(taskRequest);
    }
    
	/**
	 * 
	 * 前端测试分辨客户端机器
	 * 
	 * @since v0.1
	 * @author tr0j4n
	 * @created 2014-9-9 下午3:34:55
	 */
	public static void demo() {
		render();
	}
	/**
	 * 
	 *下载优质评论图
	 * @since  v3.0
	 * @author fufei
	 * @created 2015年4月2日 下午12:02:13
	 */
	public static void downloadCommentPic() {
        
    }
}
