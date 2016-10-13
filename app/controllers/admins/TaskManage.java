package controllers.admins;

import java.io.File;
import java.io.IOException;
import java.math.BigDecimal;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Map;

import models.AdminOperatorLog;
import models.AdminRefundRecord;
import models.AdminUser;
import models.BuyerTask;
import models.BuyerTask2;
import models.BuyerTask3;
import models.BuyerTaskStep;
import models.BuyerTaskStep3;
import models.FundAccount;
import models.SellerPledgeRecord;
import models.Task;
import models.Task3;
import models.User;
import models.AdminOperatorLog.LogType;
import models.SellerPledgeRecord.PledgeAction;
import models.UserIngotRecord;
import models.mappers.AdminRefundRecordMapper;
import models.mappers.BuyerTaskMapper;
import models.mappers.BuyerTaskMapper3;
import models.mappers.BuyerTaskStepMapper;
import models.mappers.BuyerTaskStepMapper3;
import models.mappers.SellerPledgeRecordMapper;
import models.mappers.TaskMapper;
import models.mappers.UserIngotRecordMapper;
import models.TaskItemSearchPlan;
import net.sf.jxls.exception.ParsePropertyException;

import org.apache.commons.lang.BooleanUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.ibatis.session.SqlSession;
import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import org.jboss.netty.util.internal.StringUtil;
import org.joda.time.DateTime;

import play.data.validation.Min;
import play.data.validation.Required;
import play.mvc.With;
import vos.BuyerTaskStepVo;
import vos.BuyerTaskVo;
import vos.BuyerTaskVo2;
import vos.BuyerTaskVo3;
import vos.OrderExpress;
import vos.Page;
import vos.TaskSearchVo;
import vos.TaskSearchVo.SearchModule;
import vos.TaskSearchVo2;
import vos.TaskSearchVo3;

import com.aton.base.BaseController;
import com.aton.base.secure.Secure;
import com.aton.config.Config;
import com.aton.config.BizConstants;
import com.aton.config.ReturnCode;
import com.aton.db.SessionFactory;
import com.aton.util.CollectionUtils;
import com.aton.util.ExcelUtil;
import com.aton.util.MixHelper;
import com.aton.util.NumberUtils;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;

import controllers.TaskExecutor;

import domain.BuyerTaskProcessor;
import domain.BuyerTaskProcessor2;
import domain.BuyerTaskProcessor3;
import domain.TaskCostDetail;
import enums.BuyerTaskStepType;
import enums.TaskStatus;

@With(Secure.class)
public class TaskManage extends BaseController {

	/**
	 * 
	 * 手动触发“平台返款”任务的最后一步扣款转账流程，用于开发测试和加快流程 默认为定时检查30分钟后自动触发.
	 * 
	 * @since v1.7
	 * @author youblade
	 * @created 2014年12月2日 下午2:50:28
	 */
	public static void confirmSysRefundBuyerTask(@Required @Min(1) long id) {
		handleWrongInput(false);

		// 只能处理【买手】已核实退款金额的任务
		BuyerTask buyerTask = BuyerTask.findById(id);
		if (buyerTask == null) {
			renderFailedJson(ReturnCode.WRONG_INPUT, "买手任务不存在");
		}
		if (buyerTask.status != TaskStatus.WAIT_SYS_REFUND) {
			renderFailedJson(ReturnCode.BIZ_LIMIT, "买手任务状态必须为：WAIT_SYS_REFUND");
		}
		Task task = Task.findById("status", buyerTask.taskId);
		if (task.status != TaskStatus.PUBLISHED) {
			renderFailedJson(ReturnCode.BIZ_LIMIT, "父任务状态必须为：PUBLISHED");
		}

		buyerTask.confirmSysRefund();
		renderSuccessJson();
	}

	/**
	 * 
	 * 【买手任务管理】分页获取买手任务
	 * 
	 * @param vo
	 * @since v1.9
	 * @author youblade
	 * @created 2014年12月10日 下午6:47:50
	 */
	public static void listBuyerTask(@Required TaskSearchVo vo) {
		if (vo != null) {
			validation.required("vo.pageNo", vo.pageNo);
			validation.required("vo.pageSize", vo.pageSize);
			validation.range("vo.pageSize", vo.pageSize, 1, 50);
		}
		handleWrongInput(false);
		vo.buyerNick = StringUtils.trimToNull(vo.buyerNick);
		vo.shopName = StringUtils.trimToNull(vo.shopName);
		vo.sellerNick = StringUtils.trimToNull(vo.sellerNick);
		vo.expressNo=StringUtils.trimToNull(vo.expressNo);
		Page<BuyerTaskVo> page = BuyerTask.findByPageForAdmin(vo);
		renderPageJson(page.items, page.totalCount);
	}
	
	
	/**
	 * 
	 * 【买手任务管理】分页获取买手任务
	 */
	public static void listBuyerTask3(@Required TaskSearchVo3 vo) {
		if (vo != null) {
			validation.required("vo.pageNo", vo.pageNo);
			validation.required("vo.pageSize", vo.pageSize);
			validation.range("vo.pageSize", vo.pageSize, 1, 50);
		}
		handleWrongInput(false);
		vo.buyerNick = StringUtils.trimToNull(vo.buyerNick);
		vo.shopName = StringUtils.trimToNull(vo.shopName);
		vo.sellerNick = StringUtils.trimToNull(vo.sellerNick);
		vo.expressNo=StringUtils.trimToNull(vo.expressNo);
		Page<BuyerTaskVo3> page = BuyerTask3.findByPageForAdmin(vo);
		renderPageJson(page.items, page.totalCount);
	}
	
	
	/**
	 * 
	 * 【买手任务管理】分页获取买手推广
	 * 
	 * @param vo
	 * @since v1.9
	 * @author youblade
	 * @created 2014年12月10日 下午6:47:50
	 */
	public static void listBuyerTask2(@Required TaskSearchVo2 vo) {
		if (vo != null) {
			validation.required("vo.pageNo", vo.pageNo);
			validation.required("vo.pageSize", vo.pageSize);
			validation.range("vo.pageSize", vo.pageSize, 1, 50);
		}
		handleWrongInput(false);
		vo.buyerNick = StringUtils.trimToNull(vo.buyerNick);
		vo.shopName = StringUtils.trimToNull(vo.shopName);
		vo.sellerNick = StringUtils.trimToNull(vo.sellerNick);
		Page<BuyerTaskVo2> page = BuyerTask2.findByPageForAdmin(vo);
		renderPageJson(page.items, page.totalCount);
	}
	
	
	
	
	
	
	
	
	/**
	 * 
	 * 分页获取买手超时任务
	 *
	 * @param vo
	 * @since  v2.0
	 * @author Mark Xu
	 * @created 2015-3-30 下午4:45:52
	 */
	public static void listBuyerTimeoutTask(@Required TaskSearchVo vo) {
		if (vo != null) {
			validation.required("vo.pageNo", vo.pageNo);
			validation.required("vo.pageSize", vo.pageSize);
			validation.range("vo.pageSize", vo.pageSize, 1, 50);
		}
		handleWrongInput(false);
		DateTime now = DateTime.now();
		vo.buyerNick = StringUtils.trimToNull(vo.buyerNick);
		vo.shopName = StringUtils.trimToNull(vo.shopName);
		vo.sellerNick = StringUtils.trimToNull(vo.sellerNick);
		//买手任务超时
		if(vo.type.equals("sllerGood")) {
			vo.status(TaskStatus.EXPRESS_PRINT)
			.modifyTimeEnd(now.minusHours(48));
		//商家确认退款超时	
		}else if(vo.type.equals("sellerRefund")) {
			vo.status(TaskStatus.WAIT_REFUND)
			.modifyTimeEnd(now.minusHours(48));
		//商家确认退款超时（平台）	
		}else if(vo.type.equals("sellerRefundPlat")) {
			vo.status(TaskStatus.WAIT_SELLER_CONFIRM_SYS_REFUND)
			.modifyTimeEnd(now.minusHours(48));
		//买手确认收货超时	
		}else if(vo.type.equals("buyerGood")) {
			vo.status(TaskStatus.WAIT_CONFIRM)
			.modifyTimeEnd(now.minusHours(7*24));
		//买手确认退款超时	
		}else if(vo.type.equals("buyerRefund")) {
			vo.status(TaskStatus.REFUNDING)
			.modifyTimeEnd(now.minusHours(48));
		//买手确认退款超时（平台）	
		}else if(vo.type.equals("buyerRefundPlat")) {
			vo.status(TaskStatus.WAIT_BUYER_CONFIRM_SYS_REFUND)
			.modifyTimeEnd(now.minusHours(48));
		}else {
			renderFailedJson(ReturnCode.FAIL);
		}
		Page<BuyerTaskVo> page = BuyerTask.findByPageForAdmin(vo);
		renderPageJson(page.items, page.totalCount);
	}
	
	
	/**
	 * 
	 * 分页获取买手超时浏览
	 */
	public static void listBuyerTimeoutTask3(@Required TaskSearchVo3 vo) {
		if (vo != null) {
			validation.required("vo.pageNo", vo.pageNo);
			validation.required("vo.pageSize", vo.pageSize);
			validation.range("vo.pageSize", vo.pageSize, 1, 50);
		}
		handleWrongInput(false);
		DateTime now = DateTime.now();
		vo.buyerNick = StringUtils.trimToNull(vo.buyerNick);
		vo.shopName = StringUtils.trimToNull(vo.shopName);
		vo.sellerNick = StringUtils.trimToNull(vo.sellerNick);
		//买手任务超时
		if(vo.type.equals("sllerGood")) {
			vo.status(TaskStatus.EXPRESS_PRINT)
			.modifyTimeEnd(now.minusHours(48));
		//商家确认退款超时	
		}else if(vo.type.equals("sellerRefund")) {
			vo.status(TaskStatus.WAIT_REFUND)
			.modifyTimeEnd(now.minusHours(48));
		//商家确认退款超时（平台）	
		}else if(vo.type.equals("sellerRefundPlat")) {
			vo.status(TaskStatus.WAIT_SELLER_CONFIRM_SYS_REFUND)
			.modifyTimeEnd(now.minusHours(48));
		//买手确认收货超时	
		}else if(vo.type.equals("buyerGood")) {
			vo.status(TaskStatus.WAIT_CONFIRM)
			.modifyTimeEnd(now.minusHours(7*24));
		//买手确认退款超时	
		}else if(vo.type.equals("buyerRefund")) {
			vo.status(TaskStatus.REFUNDING)
			.modifyTimeEnd(now.minusHours(48));
		//买手确认退款超时（平台）	
		}else if(vo.type.equals("buyerRefundPlat")) {
			vo.status(TaskStatus.WAIT_BUYER_CONFIRM_SYS_REFUND)
			.modifyTimeEnd(now.minusHours(48));
		}else {
			renderFailedJson(ReturnCode.FAIL);
		}
		Page<BuyerTaskVo3> page = BuyerTask3.findByPageForAdmin(vo);
		renderPageJson(page.items, page.totalCount);
	}
	
	
	
	
	/**
	 * 
	 * 分页获取买手超时推广
	 *
	 * @param vo
	 * @since  v2.0
	 * @author Mark Xu
	 * @created 2015-3-30 下午4:45:52
	 */
	public static void listBuyerTimeoutTask2(@Required TaskSearchVo2 vo) {
		if (vo != null) {
			validation.required("vo.pageNo", vo.pageNo);
			validation.required("vo.pageSize", vo.pageSize);
			validation.range("vo.pageSize", vo.pageSize, 1, 50);
		}
		handleWrongInput(false);
		DateTime now = DateTime.now();
		vo.buyerNick = StringUtils.trimToNull(vo.buyerNick);
		vo.shopName = StringUtils.trimToNull(vo.shopName);
		vo.sellerNick = StringUtils.trimToNull(vo.sellerNick);
		//买手任务超时
		if(vo.type.equals("sllerGood")) {
			vo.status(TaskStatus.EXPRESS_PRINT)
			.modifyTimeEnd(now.minusHours(48));
		//商家确认退款超时	
		}else if(vo.type.equals("sellerRefund")) {
			vo.status(TaskStatus.WAIT_REFUND)
			.modifyTimeEnd(now.minusHours(48));
		//商家确认退款超时（平台）	
		}else if(vo.type.equals("sellerRefundPlat")) {
			vo.status(TaskStatus.WAIT_SELLER_CONFIRM_SYS_REFUND)
			.modifyTimeEnd(now.minusHours(48));
		//买手确认收货超时	
		}else if(vo.type.equals("buyerGood")) {
			vo.status(TaskStatus.WAIT_CONFIRM)
			.modifyTimeEnd(now.minusHours(7*24));
		//买手确认退款超时	
		}else if(vo.type.equals("buyerRefund")) {
			vo.status(TaskStatus.REFUNDING)
			.modifyTimeEnd(now.minusHours(48));
		//买手确认退款超时（平台）	
		}else if(vo.type.equals("buyerRefundPlat")) {
			vo.status(TaskStatus.WAIT_BUYER_CONFIRM_SYS_REFUND)
			.modifyTimeEnd(now.minusHours(48));
		}else {
			renderFailedJson(ReturnCode.FAIL);
		}
		Page<BuyerTaskVo2> page = BuyerTask2.findByPageForAdmin(vo);
		renderPageJson(page.items, page.totalCount);
	}
	
	
	
	
	
	
	
	
	
	
	//获取各类超时任务的数量
	public static void listBuyerTimeoutTaskCount(@Required TaskSearchVo vo) {
		if (vo != null) {
			validation.required("vo.pageNo", vo.pageNo);
			validation.required("vo.pageSize", vo.pageSize);
			validation.range("vo.pageSize", vo.pageSize, 1, 50);
		}
		handleWrongInput(false);

		DateTime now = DateTime.now();
		vo.status(TaskStatus.EXPRESS_PRINT).modifyTimeEnd(now.minusHours(48));
		int c1 = BuyerTask.count(vo);
		vo.status(TaskStatus.WAIT_REFUND).modifyTimeEnd(now.minusHours(48));
		int c2 = BuyerTask.count(vo);
		vo.status(TaskStatus.WAIT_SELLER_CONFIRM_SYS_REFUND).modifyTimeEnd(now.minusHours(48));
		int c3 = BuyerTask.count(vo);
		vo.status(TaskStatus.WAIT_CONFIRM).modifyTimeEnd(now.minusHours(7*24));
		int c4 = BuyerTask.count(vo);
		vo.status(TaskStatus.REFUNDING).modifyTimeEnd(now.minusHours(48));
		int c5 = BuyerTask.count(vo);
		vo.status(TaskStatus.WAIT_BUYER_CONFIRM_SYS_REFUND).modifyTimeEnd(now.minusHours(48));
		int c6 = BuyerTask.count(vo);
		Map map = Maps.newHashMapWithExpectedSize(6);
        map.put("count1", c1);
        map.put("count2", c2);
        map.put("count3", c3);
        map.put("count4", c4);
        map.put("count5", c5);
        map.put("count6", c6);
        renderJson(map);
	}
	
	
	//获取各类超时浏览的数量
		public static void listBuyerTimeoutTaskCount3(@Required TaskSearchVo3 vo) {
			if (vo != null) {
				validation.required("vo.pageNo", vo.pageNo);
				validation.required("vo.pageSize", vo.pageSize);
				validation.range("vo.pageSize", vo.pageSize, 1, 50);
			}
			handleWrongInput(false);

			DateTime now = DateTime.now();
			vo.status(TaskStatus.EXPRESS_PRINT).modifyTimeEnd(now.minusHours(48));
			int c1 = BuyerTask3.count(vo);
			vo.status(TaskStatus.WAIT_REFUND).modifyTimeEnd(now.minusHours(48));
			int c2 = BuyerTask3.count(vo);
			vo.status(TaskStatus.WAIT_SELLER_CONFIRM_SYS_REFUND).modifyTimeEnd(now.minusHours(48));
			int c3 = BuyerTask3.count(vo);
			vo.status(TaskStatus.WAIT_CONFIRM).modifyTimeEnd(now.minusHours(7*24));
			int c4 = BuyerTask3.count(vo);
			vo.status(TaskStatus.REFUNDING).modifyTimeEnd(now.minusHours(48));
			int c5 = BuyerTask3.count(vo);
			vo.status(TaskStatus.WAIT_BUYER_CONFIRM_SYS_REFUND).modifyTimeEnd(now.minusHours(48));
			int c6 = BuyerTask3.count(vo);
			Map map = Maps.newHashMapWithExpectedSize(6);
	        map.put("count1", c1);
	        map.put("count2", c2);
	        map.put("count3", c3);
	        map.put("count4", c4);
	        map.put("count5", c5);
	        map.put("count6", c6);
	        renderJson(map);
		}
	
	
	
	//获取各类超时推广的数量
		public static void listBuyerTimeoutTaskCount2(@Required TaskSearchVo2 vo) {
			
			System.out.println("=========");
			System.out.println("=========");
			System.out.println("=========");
			System.out.println("=========");
			System.out.println("=========");
			System.out.println("=========");
			
			if (vo != null) {
				validation.required("vo.pageNo", vo.pageNo);
				validation.required("vo.pageSize", vo.pageSize);
				validation.range("vo.pageSize", vo.pageSize, 1, 50);
			}
			handleWrongInput(false);

			DateTime now = DateTime.now();
			vo.status(TaskStatus.EXPRESS_PRINT).modifyTimeEnd(now.minusHours(48));
			int c1 = BuyerTask2.count(vo);
			vo.status(TaskStatus.WAIT_REFUND).modifyTimeEnd(now.minusHours(48));
			int c2 = BuyerTask2.count(vo);
			vo.status(TaskStatus.WAIT_SELLER_CONFIRM_SYS_REFUND).modifyTimeEnd(now.minusHours(48));
			int c3 = BuyerTask2.count(vo);
			vo.status(TaskStatus.WAIT_CONFIRM).modifyTimeEnd(now.minusHours(7*24));
			int c4 = BuyerTask2.count(vo);
			vo.status(TaskStatus.REFUNDING).modifyTimeEnd(now.minusHours(48));
			int c5 = BuyerTask2.count(vo);
			vo.status(TaskStatus.WAIT_BUYER_CONFIRM_SYS_REFUND).modifyTimeEnd(now.minusHours(48));
			int c6 = BuyerTask2.count(vo);
			Map map = Maps.newHashMapWithExpectedSize(6);
	        map.put("count1", c1);
	        map.put("count2", c2);
	        map.put("count3", c3);
	        map.put("count4", c4);
	        map.put("count5", c5);
	        map.put("count6", c6);
	        renderJson(map);
		}
	
	
	
	
	
	
	
	
	
	
	
	/**
	 * 
	 * 处理商家退款超时（非平台返款）
	 *
	 * @param uid
	 * @since  v2.0
	 * @author Mark Xu
	 * @created 2015-4-3 下午12:11:40
	 */
	public static void processTimeOutById(@Required long uid) {
		
	}
	
	/**
	 * 
	 * 处理商家退款超时（平台返款）
	 *
	 * @param uid
	 * @since  v2.0
	 * @author Mark Xu
	 * @created 2015-4-3 下午12:11:40
	 */
	public static void processTimeOutPlatById(@Required long id, @Required boolean isSubtraction) {
        handleWrongInput(false);

        BuyerTask bt = BuyerTask.findById(id);
        User seller = User.findById(bt.sellerId);
        long amount = bt.paidFee;
        if (bt == null || bt.forbiddenConfirmSysRefund()) {
            renderFailedJson(ReturnCode.WRONG_INPUT);
        }
        
        // 检查父任务是否“平台返款”任务，避免误修改数据
        Task t = Task.findById("sys_refund,item_price,item_buy_num", bt.taskId);
        if (t == null || BooleanUtils.isNotTrue(t.sysRefund)) {
            renderFailedJson(ReturnCode.WRONG_INPUT);
        }
        
        // 卖家退款金额不能大于 商品单价*件数+退款保证金
        long newpaidFee = new BigDecimal(amount).longValue();
        
        if(newpaidFee < 0 ){
            renderFailedJson(ReturnCode.WRONG_INPUT);
        }
        //商家更改的价格不能超过改单任务发布金额的1.5倍
        if (newpaidFee > t.itemPrice * t.itemBuyNum * 1.5) {
            renderFailedJson(ReturnCode.OP_BIZ_LIMIT);
        }
        //判断当前商家押金是否够支付买手返款
        long price = t.itemPrice * t.itemBuyNum;
        //（如果不包邮，商家每单任务在平台上的总押金包括10元快递费押金）
        if(Task.findById("is_free_shipping", bt.taskId).notFreeShipping()) {
        	price = t.itemPrice * t.itemBuyNum + BizConstants.TASK_EXPRESS_PLEDGE*100;
        }
        SqlSession ss = SessionFactory.getSqlSession();
        try {
        	//获取发出该子任务的商家的最后一次押金记录
        	SellerPledgeRecordMapper sellerMapper = ss.getMapper(SellerPledgeRecordMapper.class);
        	SellerPledgeRecord sellerPledge = sellerMapper.selectLastRecord(seller.id);
        	//应该判断 商家账户余额与 单个任务商家在平台的押金-单个任务商家应该给买手的钱 比较 
            //如果是管理员帮助返款，且选择扣除商家押金,则需要把扣除的5押金数算进去
            if(sellerPledge.balance < (newpaidFee - price + BizConstants.SELLER_REFUND_SUBTRACTION*100) && isSubtraction){
                renderFailedJson(ReturnCode.BIZ_LIMIT);
            }
            // 更新任务
            Date now = DateTime.now().toDate();
            BuyerTask buyerTask = BuyerTask.instance(bt.id).status(TaskStatus.WAIT_BUYER_CONFIRM_SYS_REFUND)
                .modifyTime(now);
        	
            BuyerTaskMapper mapper = ss.getMapper(BuyerTaskMapper.class);
            
            // 若金额被修改则需要设置新状态
            long originPaidFee = mapper.selectById(id).paidFee;
            if (newpaidFee != originPaidFee) {
                buyerTask.paidFee(newpaidFee).status(TaskStatus.WAIT_BUYER_CONFIRM_EDITED_SYS_REFUND);
            }
            mapper.updateById(buyerTask);
            
            /*
             *  保存买手任务进度，用于买手任务详情页的显示
             */
            BuyerTaskStepMapper stepMapper = ss.getMapper(BuyerTaskStepMapper.class);
            BuyerTaskStep confirmStep = stepMapper.selectByTaskIdAndTypeAndBuyerId(bt.id, BuyerTaskStepType.REFUND,
                bt.buyerId);
            // 若之前未进行过改操作则新建一个进度记录
            if (confirmStep == null) {
                BuyerTaskStep taskStep = BuyerTaskStep.newInstance(bt.id, bt.buyerId).type(BuyerTaskStepType.REFUND)
                    .createTime(now).modifyTime(now);
                stepMapper.insert(taskStep);

                // 若之前进行过该操作，则直接更新显示标记及修改时间
            } else if (BooleanUtils.isFalse(confirmStep.isValid)) {
                stepMapper.updateById(BuyerTaskStep.instance(confirmStep.id).valid(true).modifyTime(now));
            }
            //当点击的处理链接为需要扣除金币且状态不为
            if(isSubtraction) {
            	sellerPledge = sellerPledge.newInstance(seller.id, sellerPledge).action(PledgeAction.RECHARGE, BizConstants.SELLER_REFUND_SUBTRACTION*100).taskId(bt.taskId);
            	sellerPledge.memo = "客服帮助确认平台返款超时的子任务，扣除商家押金5元";
            	sellerPledge.createTime = DateTime.now().toDate();
            	sellerMapper.insert(sellerPledge);
                User.findByIdWichCache(bt.sellerId).pledge(sellerPledge.balance).updateCache();
            }
        } finally {
            ss.close();
        }
        
        renderSuccessJson();
	}
	
	
	/**
	 * 
	 * 处理商家退款超时（平台返款）
	 */
	public static void processTimeOutPlatById3(@Required long id, @Required boolean isSubtraction) {
        handleWrongInput(false);

        BuyerTask3 bt = BuyerTask3.findById(id);
        User seller = User.findById(bt.sellerId);
        long amount = bt.paidFee;
        if (bt == null || bt.forbiddenConfirmSysRefund()) {
            renderFailedJson(ReturnCode.WRONG_INPUT);
        }
        
        // 检查父任务是否“平台返款”任务，避免误修改数据
        Task3 t = Task3.findById("sys_refund,item_price,item_buy_num", bt.taskId);
        if (t == null || BooleanUtils.isNotTrue(t.sysRefund)) {
            renderFailedJson(ReturnCode.WRONG_INPUT);
        }
        
        // 卖家退款金额不能大于 商品单价*件数+退款保证金
        long newpaidFee = new BigDecimal(amount).longValue();
        
        if(newpaidFee < 0 ){
            renderFailedJson(ReturnCode.WRONG_INPUT);
        }
        //商家更改的价格不能超过改单任务发布金额的1.5倍
        if (newpaidFee > t.itemPrice * t.itemBuyNum * 1.5) {
            renderFailedJson(ReturnCode.OP_BIZ_LIMIT);
        }
        //判断当前商家押金是否够支付买手返款
        long price = t.itemPrice * t.itemBuyNum;
        //（如果不包邮，商家每单任务在平台上的总押金包括10元快递费押金）
        if(Task3.findById("is_free_shipping", bt.taskId).notFreeShipping()) {
        	price = t.itemPrice * t.itemBuyNum + BizConstants.TASK_EXPRESS_PLEDGE*100;
        }
        SqlSession ss = SessionFactory.getSqlSession();
        try {
        	//获取发出该子任务的商家的最后一次押金记录
        	SellerPledgeRecordMapper sellerMapper = ss.getMapper(SellerPledgeRecordMapper.class);
        	SellerPledgeRecord sellerPledge = sellerMapper.selectLastRecord(seller.id);
        	//应该判断 商家账户余额与 单个任务商家在平台的押金-单个任务商家应该给买手的钱 比较 
            //如果是管理员帮助返款，且选择扣除商家押金,则需要把扣除的5押金数算进去
            if(sellerPledge.balance < (newpaidFee - price + BizConstants.SELLER_REFUND_SUBTRACTION*100) && isSubtraction){
                renderFailedJson(ReturnCode.BIZ_LIMIT);
            }
            // 更新任务
            Date now = DateTime.now().toDate();
            BuyerTask3 buyerTask = BuyerTask3.instance(bt.id).status(TaskStatus.WAIT_BUYER_CONFIRM_SYS_REFUND)
                .modifyTime(now);
        	
            BuyerTaskMapper3 mapper = ss.getMapper(BuyerTaskMapper3.class);
            
            // 若金额被修改则需要设置新状态
            long originPaidFee = mapper.selectById(id).paidFee;
            if (newpaidFee != originPaidFee) {
                buyerTask.paidFee(newpaidFee).status(TaskStatus.WAIT_BUYER_CONFIRM_EDITED_SYS_REFUND);
            }
            mapper.updateById(buyerTask);
            
            /*
             *  保存买手任务进度，用于买手任务详情页的显示
             */
            BuyerTaskStepMapper3 stepMapper = ss.getMapper(BuyerTaskStepMapper3.class);
            BuyerTaskStep3 confirmStep = stepMapper.selectByTaskIdAndTypeAndBuyerId(bt.id, BuyerTaskStepType.REFUND,
                bt.buyerId);
            // 若之前未进行过改操作则新建一个进度记录
            if (confirmStep == null) {
                BuyerTaskStep3 taskStep = BuyerTaskStep3.newInstance(bt.id, bt.buyerId).type(BuyerTaskStepType.REFUND)
                    .createTime(now).modifyTime(now);
                stepMapper.insert(taskStep);

                // 若之前进行过该操作，则直接更新显示标记及修改时间
            } else if (BooleanUtils.isFalse(confirmStep.isValid)) {
                stepMapper.updateById(BuyerTaskStep3.instance(confirmStep.id).valid(true).modifyTime(now));
            }
            //当点击的处理链接为需要扣除金币且状态不为
            if(isSubtraction) {
            	sellerPledge = sellerPledge.newInstance(seller.id, sellerPledge).action(PledgeAction.RECHARGE, BizConstants.SELLER_REFUND_SUBTRACTION*100).taskId(bt.taskId);
            	sellerPledge.memo = "客服帮助确认平台返款超时的子任务，扣除商家押金5元";
            	sellerPledge.createTime = DateTime.now().toDate();
            	sellerMapper.insert(sellerPledge);
                User.findByIdWichCache(bt.sellerId).pledge(sellerPledge.balance).updateCache();
            }
        } finally {
            ss.close();
        }
        
        renderSuccessJson();
	}
	
	
	
	
	/**
	 * 
	 * 【买手任务管理】-导出“超过48小时未退款”的买手任务ID
	 * 
	 * @param vo
	 * @since v1.9.3
	 * @author youblade
	 * @created 2014年12月15日 下午2:50:56
	 */
	public static void exportRefundOverdueBuyerTasks() {
		DateTime now = DateTime.now();
		TaskSearchVo vo = TaskSearchVo.newInstance().pageNo(1).pageSize(200)
				.status(TaskStatus.WAIT_REFUND)
				.module(SearchModule.SELLE_REFUND_OVERDUE)
				.modifyTimeEnd(now.minusHours(48));

		Page<BuyerTaskVo> page = BuyerTask.findByPageForAdmin(vo);
		if (CollectionUtils.isEmpty(page.items)) {
			renderFailedJson(ReturnCode.OK, "没有可导出的数据！");
		}

		String fileName = "超过48小时未退款的任务_" + now.toString("yyyyMMddHHmm")
				+ ".xls";
		response.contentType = "application/x-download";
		renderBinary(ExcelUtil.buildExportFile(
				Config.buyerTaskRefundOverdueXlsTemplate, page.items), fileName);
	}

	/**
	 * 
	 * 后台操作：撤销买手任务，并返还买手任务押金。
	 * 
	 * @param id
	 * @since v1.6
	 * @author playlaugh
	 * @created 2014年12月15日 下午7:47:05
	 */
	public static void cancelBuyerTask(@Required @Min(1) long id) {
		BuyerTask bt = BuyerTask.findById(id);
		if (bt == null) {
			renderFailedJson(ReturnCode.FALSE);
		}
		if (bt.status == TaskStatus.FINISHED) {
			renderFailedJson(ReturnCode.FAIL);
		}
		if (bt.status == TaskStatus.CANCLED) {
			renderFailedJson(ReturnCode.WRONG_INPUT);
		}
		// 撤销任务
		bt.cancel();

		// 返还买手任务押金
		BuyerTaskProcessor.returnTaskIngot(bt);
		// 若当前操作任务被撤销 则将其从session中删除，并将撤销操作写入管理员操作日志表中
		if (Long.toString(id).equals(session.get(BizConstants.BUYER_TASK_ID))) {
			session.remove(BizConstants.BUYER_TASK_ID);
		}
		
    	String adminAccount = AdminUser.findByIdWichCache(Long.valueOf(Long.parseLong(session.get("admin-authcode")))).name;
    	String message = MixHelper.format("任务编号{}-子任务编号{}", bt.taskId, (bt.taskId + "-" + bt.id));
    	AdminOperatorLog.insert(adminAccount, LogType.CANCEL_SUB_TASK, message);
    	
		renderSuccessJson();
    }
    
	
	/**
	 * 
	 * 后台操作：撤销买手浏览，并返还买手任务押金。
	 */
	public static void cancelBuyerTask3(@Required @Min(1) long id) {
		BuyerTask3 bt = BuyerTask3.findById(id);
		if (bt == null) {
			renderFailedJson(ReturnCode.FALSE);
		}
		if (bt.status == TaskStatus.FINISHED) {
			renderFailedJson(ReturnCode.FAIL);
		}
		if (bt.status == TaskStatus.CANCLED) {
			renderFailedJson(ReturnCode.WRONG_INPUT);
		}
		// 撤销任务
		bt.cancel();
		// 返还买手任务押金
		BuyerTaskProcessor3.returnTaskIngot(bt);
		// 若当前操作任务被撤销 则将其从session中删除，并将撤销操作写入管理员操作日志表中
		if (Long.toString(id).equals(session.get(BizConstants.BUYER_TASK_ID3))) {
			session.remove(BizConstants.BUYER_TASK_ID3);
		}
		
    	String adminAccount = AdminUser.findByIdWichCache(Long.valueOf(Long.parseLong(session.get("admin-authcode")))).name;
    	String message = MixHelper.format("任务编号{}-子任务编号{}", bt.taskId, (bt.taskId + "-" + bt.id));
    	AdminOperatorLog.insert(adminAccount, LogType.CANCEL_SUB_TASK, message);
    	
		renderSuccessJson();
    }
	
	
	
	
	
	
	/**
	 * 
	 * 后台操作：撤销买手推广，并返还买手推广押金。
	 * 
	 * @param id
	 * @since v1.6
	 * @author playlaugh
	 * @created 2014年12月15日 下午7:47:05
	 */
	public static void cancelBuyerTask2(@Required @Min(1) long id) {
		BuyerTask2 bt = BuyerTask2.findById(id);
		if (bt == null) {
			renderFailedJson(ReturnCode.FALSE);
		}
		if (bt.status == TaskStatus.FINISHED) {
			renderFailedJson(ReturnCode.FAIL);
		}
		if (bt.status == TaskStatus.CANCLED) {
			renderFailedJson(ReturnCode.WRONG_INPUT);
		}
		// 撤销任务
		bt.cancel();

		// 返还买手任务押金
		BuyerTaskProcessor2.returnTaskIngot(bt);
		// 若当前操作任务被撤销 则将其从session中删除，并将撤销操作写入管理员操作日志表中
		if (Long.toString(id).equals(session.get(BizConstants.BUYER_TASK_ID2))) {
			session.remove(BizConstants.BUYER_TASK_ID2);
		}
		
    	String adminAccount = AdminUser.findByIdWichCache(Long.valueOf(Long.parseLong(session.get("admin-authcode")))).name;
    	String message = MixHelper.format("推广编号{}-子推广编号{}", bt.taskId, (bt.taskId + "-" + bt.id));
    	AdminOperatorLog.insert(adminAccount, LogType.CANCEL_SUB_TASK, message);
    	
		renderSuccessJson();
    }
    
	
	
	
	
    /**
     * 
     * 导出任务费用明细.
     *
     * @param vo
     * @throws ParsePropertyException
     * @throws InvalidFormatException
     * @throws IOException
     * @since  v2.0
     * @author youblade
     * @created 2015年1月8日 下午9:34:54
     */
    public static void exportCostDetails(@Required String message) throws ParsePropertyException, InvalidFormatException,
        IOException {
        handleWrongInput(false);
        String ary[] = message.split("&");
        // 取出需要导出的数据
        
        SimpleDateFormat sdf=new SimpleDateFormat("yyyy-MM-dd");//小写的mm表示的是分钟  
        java.util.Date date;
        java.util.Date date1;
        TaskSearchVo vo = new TaskSearchVo();
        List<TaskCostDetail> records = new ArrayList<TaskCostDetail>();
		try {
			date = sdf.parse(ary[1]);
			date1 = sdf.parse(ary[2]);
			records = TaskCostDetail.findByTaskMessage(ary[0], date, date1);
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
        if (MixHelper.isEmpty(records)) {
            renderSuccessJson();
        }

        String fileName = "卖家" + ary[0] + "的任务费用明细.xls";
        response.contentType = "application/x-download";
        File template =   new File(Config.confDir, "excel/taskCostDetailTpl.xls");
        renderBinary(ExcelUtil.buildExportFile(template, records), fileName);
    }
    
	/**
	 * 修改用户银行卡地址,将省和市用“,”隔开
	 */
	public static void updateFundAccountAddress() {
		List<FundAccount> account = FundAccount.selectAll();
		for (FundAccount fundAccount : account) {
			if (StringUtils.contains(fundAccount.address, ",")) {
				continue;
			}
			if (StringUtils.contains(fundAccount.address, "省")) {
				String province = StringUtils.replaceOnce(fundAccount.address,
						"省", "省,");
				fundAccount.updateAddress(fundAccount.id, province);
			} else if (StringUtils.startsWith(fundAccount.address, "北京")
					|| StringUtils.startsWith(fundAccount.address, "上海")
					|| StringUtils.startsWith(fundAccount.address, "天津")
					|| StringUtils.startsWith(fundAccount.address, "重庆")) {
				String pre = StringUtils.substring(fundAccount.address, 0, 2)
						+ ",";
				String province = StringUtils.replaceOnce(fundAccount.address,
						StringUtils.substring(fundAccount.address, 0, 2), pre);

				fundAccount.updateAddress(fundAccount.id, province);
			}
		}
	}
}
