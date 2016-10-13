package controllers;

import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import models.BuyerTask;
import models.mappers.BuyerTaskMapper;

import org.apache.ibatis.session.SqlSession;
import org.joda.time.DateTime;
import org.joda.time.LocalDate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import play.data.validation.Max;
import play.data.validation.Min;
import play.data.validation.Required;
import play.libs.Codec;
import play.mvc.With;
import vos.OrderExpress;
import vos.Page;
import vos.TaskSearchVo;

import com.aton.base.BaseController;
import com.aton.base.secure.Secure;
import com.aton.config.ReturnCode;
import com.aton.db.SessionFactory;
import com.aton.util.MixHelper;
import com.aton.util.NumberUtils;
import com.aton.util.StringUtils;
import com.google.common.base.Strings;
import com.google.common.collect.Lists;

import enums.ExpressType;
import enums.TaskStatus;

@With(Secure.class)
public class ExpressPrint extends BaseController {

	private static final Logger log = LoggerFactory
			.getLogger(ExpressPrint.class);

	public static void login() {
		renderArgs.put("msg", flash.get("msg"));
		render();
	}

	public static void doLogin(@Required String usr, @Required String pass,
			@Max(30) int duration) {
		if (validation.hasErrors()) {
			flash.put("msg", "用户名或密码不正确！");
			login();
		}

		// 快递打印登录
		if ("kjkd".equals(usr.trim()) && "kjkd".equals(pass.trim())) {
			String code = StringUtils.replace(Codec.UUID(), "-", "");
			int num1 = NumberUtils.getRandomBetween(0, 8);
			int num2 = 8 - num1;
			code = code.substring(0, 1) + num1 + code.substring(1) + num2;

			// 默认免登录有效时间为1天
			String durationDays = Math.max(1, duration) + "d";
			response.setCookie(Secure.FLAG_KJKD_AUTH, code, durationDays);
			waitExpressPrint();
		}
		log.error("Try login failed:user={},pass={}", usr, pass);
		login();
	}

	/**
	 * 待打印订单
	 * 
	 * @author fufei
	 */
	public static void waitExpressPrint() {
		
		render();
	}

	public static void orderCount() {
		Integer waitExpressPrintCount = BuyerTask
				.selectOrdersCountByStatus(TaskStatus.WAIT_SEND_GOODS);
		Integer expressPrintingCount = BuyerTask
				.selectOrdersCountByStatus(TaskStatus.WAIT_EXPRESS_PRINT);
		Integer expressPrintFinishCount = BuyerTask
				.selectOrdersCountByStatus(TaskStatus.EXPRESS_PRINT);
		Map<String, Integer> map=new HashMap<String,Integer>();
		map.put("waitExpressPrintCount", waitExpressPrintCount);
		map.put("expressPrintingCount", expressPrintingCount);
		map.put("expressPrintFinishCount", expressPrintFinishCount);
		renderJSON(map);
	}

	/**
	 * 正在打印订单
	 * 
	 * @author fufei
	 */
	public static void expressPrinting() {
		render();
	}

	/**
	 * 已打印订单
	 * 
	 * @author fufei
	 */
	public static void expressPrintFinish() {
		render();
	}

	/**
	 * 
	 * 分页获取待处理的订单.
	 * 
	 * @author fufei
	 * 
	 */
	public static void listOrders(@Required TaskSearchVo vo) {
		if (vo != null) {
			validation.required(vo.pageSize);
			vo.shopName = StringUtils.trimToNull(vo.shopName);
			vo.expressNo=StringUtils.trimToNull(vo.expressNo);
			vo.expressType=ExpressType.KJKD;
		}
		handleWrongInput(false);
		Page<OrderExpress> page = BuyerTask.findOrdersByPage(vo);

		renderPageJson(page.items, page.totalCount);
	}

	/**
	 * 准备打印订单
	 * 
	 * @author fufei
	 */
	public static void preparePrintExpress(@Required TaskSearchVo vo) {
		if (vo != null) {
			validation.required(vo.exportNo);
		}
		handleWrongInput(false);
		// 取出需要导出的数据
		vo.status(TaskStatus.WAIT_SEND_GOODS);
		List<OrderExpress> orders = BuyerTask.waitPrintOrders(vo);
		if (MixHelper.isEmpty(orders)) {
			renderSuccessJson();
		}

		// 批量更新任务状态
		List<Long> ids = Lists.newArrayList();
		for (OrderExpress order : orders) {
			ids.add(order.id);
		}
		DateTime now = DateTime.now();
		BuyerTask.batchModifyStatus(ids, TaskStatus.WAIT_EXPRESS_PRINT,
				now.toDate());
		renderSuccessJson();
	}

	/**
	 * 确认打印订单
	 * 
	 * @author fufei
	 */
	public static void printOrderFinsh(@Required String expressNos) {
		handleWrongInput(false);
		Map<Long, String> maps = new HashMap<Long, String>();
		String[] printIds = expressNos.split(",");
		for (int i = 0; i < printIds.length; i++) {
			if (printIds[i] != null && !"".equals(printIds[i])) {
				maps.put(Long.parseLong(printIds[i].split("-")[0]),
						printIds[i].split("-")[1]);
			}
		}
		List<Long> ids = Lists.newArrayList();
		Iterator i = maps.entrySet().iterator();
		while (i.hasNext()) {
			Entry entry = (Entry) i.next();
			ids.add(Long.parseLong(entry.getKey().toString()));
		}
		List<OrderExpress> orders = BuyerTask.findOrderByIds(ids);

		int count = 0;

		// 更新任务状态
		for (OrderExpress order : orders) {
			// 从集合里取运单号
			order.expressNo = maps.get(order.id);
			//
			if (Strings.isNullOrEmpty(order.expressNo)) {
				renderFailedJson(-2, "请检查该条数据：编号：" + order.number + " 订单号："
						+ order.orderId + " 保存失败，[快递单号]不存在。");
			}

			BuyerTask bt = new BuyerTask(order);
			BuyerTask btFromLocal = BuyerTask.findById(bt.id);

			// 数据库中记录不存在 返回前端
			if (btFromLocal == null) {
				renderFailedJson(-2, "请检查该条数据：编号：" + order.number + " 订单号："
						+ order.orderId + " 保存失败，该任务不存在。");
			}

			// 状态已经更新过 跳过更新
			if (btFromLocal.status == TaskStatus.EXPRESS_PRINT) {
				continue;
			} else if (btFromLocal.status != TaskStatus.WAIT_EXPRESS_PRINT) {
				renderFailedJson(-2, "请检查该条数据：编号：" + order.number + " 订单号："
						+ order.orderId + " 保存失败，该任务状态不符合条件");
			}

			bt.status = TaskStatus.EXPRESS_PRINT;
			bt.expressNo = order.expressNo;
			if (BuyerTask.modify(bt) <= 0) {
				renderFailedJson(-2, "保存失败，请检查该条数据：编号：" + order.number
						+ " 订单号：" + order.orderId);
			}

			// 统计导入成功数目
			count++;
		}
		renderJson(count);
	}

	/**
	 * 修改运单号
	 * 
	 * @param id
	 * @param expressNo
	 */
	public static void updateById(@Required Long id, @Required String expressNo) {
		if (id != null && expressNo != null) {
			validation.required(id);
			validation.required(expressNo);
		}
		handleWrongInput(false);
		BuyerTask buyerTask = BuyerTask.findById(id);
		if (buyerTask == null) {
			renderFailedJson(-2, "您要更新的订单不存在！");
		}
		buyerTask.expressNo = expressNo;
		if (BuyerTask.updateExpressNoById(buyerTask) <= 0) {
			renderFailedJson(-2, "保存失败，请与管理员联系！");
		}
		renderSuccessJson();

	}

	/**
	 * 
	 * 快递单打印->.
	 * 
	 * @since v0.2.1
	 * @author youblade
	 * @created 2014年10月28日 下午4:53:18
	 */
	public static void resetExpressOrder(@Required @Min(1) long buyerTaskId) {
		handleWrongInput(false);

		BuyerTask task = BuyerTask.findById(buyerTaskId);
		if (task == null || task.status != TaskStatus.WAIT_EXPRESS_PRINT) {
			renderFailedJson(ReturnCode.BIZ_LIMIT);
		}

		// 更新状态为“待发货”
		BuyerTask buyerTask = BuyerTask.instance(buyerTaskId)
				.status(TaskStatus.WAIT_SEND_GOODS)
				.modifyTime(LocalDate.now().toDate());

		SqlSession ss = SessionFactory.getSqlSession();
		try {
			ss.getMapper(BuyerTaskMapper.class).updateById(buyerTask);
		} catch (Exception e) {
			log.error(e.getMessage(), e);
			renderFailedJson(ReturnCode.FAIL);
		} finally {
			ss.close();
		}
		renderSuccessJson();
	}
	
	/**
	 * 
	 * 快递单打印->.
	 * 
	 * @since v0.2.1
	 * @author youblade
	 * @created 2014年10月28日 下午4:53:18
	 */
	public static void resetExpressOrderPrinting(@Required @Min(1) long buyerTaskId) {
		handleWrongInput(false);

		BuyerTask task = BuyerTask.findById(buyerTaskId);
		if (task == null || task.status != TaskStatus.EXPRESS_PRINT) {
			renderFailedJson(ReturnCode.BIZ_LIMIT);
		}

		BuyerTask buyerTask = BuyerTask.instance(buyerTaskId)
				.status(TaskStatus.WAIT_EXPRESS_PRINT)
				.modifyTime(LocalDate.now().toDate());

		SqlSession ss = SessionFactory.getSqlSession();
		try {
			ss.getMapper(BuyerTaskMapper.class).updateById(buyerTask);
		} catch (Exception e) {
			log.error(e.getMessage(), e);
			renderFailedJson(ReturnCode.FAIL);
		} finally {
			ss.close();
		}
		renderSuccessJson();
	}
}