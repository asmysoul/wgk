package controllers;

import java.io.File;
import java.io.IOException;
import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Random;

import models.AdminOperatorLog;
import models.AdminOperatorLog.LogType;
import models.AdminTradeLog;
import models.AdminUser;
import models.AdminUser.AdminStatus;
import models.AdminUser.AdminType;
import models.BuyerAccount;
import models.BuyerAccount2;
import models.BuyerAccount3;
import models.BuyerDepositRecord;
import models.BuyerTask;
import models.BuyerTask3;
import models.MemberChargeRecord;
import models.PayTradeLog;
import models.PayTradeLog.FinanceLogVo;
import models.SellerPledgeRecord;
import models.Shop;
import models.SysConfig;
import models.Task;
import models.Task2;
import models.Task3;
import models.TrafficRecord;
import models.TrafficRecord.TrafficStatus;
import models.User;
import models.User.UserStatus;
import models.User.UserType;
import models.User.VipStatus;
import models.UserFlowRecord;
import models.UserWithdrawRecord;
import models.UserWithdrawRecord.WithdrawStatus;
import models.mappers.BuyerTaskMapper;
import models.marketing.Activity;
import models.marketing.UserInvitedRecord;
import net.sf.jxls.exception.ParsePropertyException;

import org.apache.ibatis.session.SqlSession;
import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import org.joda.time.DateTime;
import org.joda.time.LocalDate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import play.cache.Cache;
import play.data.validation.Min;
import play.data.validation.Required;
import play.mvc.With;
import vos.BuyerAccountSearchVo.ExamineStatus;
import vos.BuyerAccountSearchVo2.ExamineStatus2;
import vos.BuyerAccountSearchVo3.ExamineStatus3;
import vos.BuyerTakeTaskVo;
import vos.ExpressCountVo;
import vos.FaBaoGuoVo;
import vos.FlowTimesVo;
import vos.OrderExpress;
import vos.Page;
import vos.RewardCountVo;
import vos.SearchSystemCountVo;
import vos.SearchTakenCount;
import vos.SellerPutTimeVo;
import vos.SellerTaskVo;
import vos.SellerTaskVo2;
import vos.SellerTaskVo3;
import vos.ShopSearchVo;
import vos.TakeTaskCountVo;
import vos.TaskSearchVo;
import vos.TaskSearchVo2;
import vos.TaskSearchVo3;
import vos.TrafficRecordVo;

import com.aton.base.BaseController;
import com.aton.base.secure.Secure;
import com.aton.config.AppMode;
import com.aton.config.BizConstants;
import com.aton.config.CacheType;
import com.aton.config.Config;
import com.aton.config.ReturnCode;
import com.aton.db.SessionFactory;
import com.aton.util.CacheUtil;
import com.aton.util.CollectionUtils;
import com.aton.util.ExcelUtil;
import com.aton.util.FaBaoGuoUtil;
import com.aton.util.FaBaoGuoUtil.FaBaoGuoInfoReturn;
import com.aton.util.FaBaoGuoUtil.FaBaoGuoReturn;
import com.aton.util.MixHelper;
import com.aton.util.StringUtils;
import com.aton.util.TrafficRecordUtil;
import com.aton.util.TrafficRecordUtil.Method;
import com.google.common.base.Strings;
import com.google.common.collect.Lists;
import com.sun.org.apache.xpath.internal.ExpressionOwner;

import controllers.MoneyManage.MoneyRecordSearchVo;
import enums.Device;
import enums.ExpressType;
import enums.Platform;
import enums.Platform2;
import enums.Platform3;
import enums.Sign;
import enums.TaskStatus;
import enums.pay.PayPlatform;

@With(Secure.class)
public class Admin extends BaseController {
    private static final Logger log = LoggerFactory.getLogger(Admin.class);
    /**
     * 
     * 管理员登陆入口
     *
     * @since  v2.0
     * @author Mark Xu
     * @created 2015-1-14 下午2:03:59
     */
    public static void login() {
        renderArgs.put("msg", flash.get("msg"));
        renderArgs.put("code",false);
        if(session.get(Secure.LOGIN_TIMES)==null){
        	session.put(Secure.LOGIN_TIMES, 1);
        }
        if(session.get(Secure.LOGIN_TIMES)!=null&&Integer.parseInt(session.get(Secure.LOGIN_TIMES))>3){
        	 renderArgs.put("code",true);
        }
        render();
    }
	/**
	 * 
	 * TODO测试添加用户
	 *
	 * @since  v2.0
	 * @author fufei
	 * @created 2015年1月28日 下午4:07:28
	 */
    public static void regist() {
    	render();
	}

   /**
    * 
    * TODO 合作注册用户
    *
    * @param user
    * @param inviteUserId
    * @since  v2.2
    * @author fufei
    * @created 2015年1月28日 下午4:46:00
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
        user.testCreate();
        index();
    }
    
    /**
     * 
     * TODO 后台管理->（首页）任务中心.
     * 
     * @since 0.1
     * @author youblade
     * @created 2014年7月24日 上午9:41:04
     */
    public static void index() {
    	renderArgs.put("adminTypeList", AdminType.values());
    	renderArgs.put("userStatus", UserStatus.values());
    	render();
    }
    
    
    /**
     * 
     * TODO 后台管理->（首页）任务中心.
     */
    public static void index3() {
    	renderArgs.put("adminTypeList", AdminType.values());
    	renderArgs.put("userStatus", UserStatus.values());
    	render();
    }
    
    
    
    
    /**
     * 
     * TODO 后台管理->（首页）推广中心.
     * 
     * @since 0.1
     * @author youblade
     * @created 2014年7月24日 上午9:41:04
     */
    public static void index2() {
    	renderArgs.put("adminTypeList", AdminType.values());
    	renderArgs.put("userStatus", UserStatus.values());
    	render();
    }
    
    
    /**
     * 
     * TODO 快递单处理.
     *
     * @since  0.1
     * @author youblade
     * @created 2014年8月12日 下午2:47:11
     */
    public static void express() {
        render();
    }
    
	/**
	 * 
	 * 分页获取待处理的订单.
	 * 
	 * @since 0.1
	 * @author youblade
	 * @created 2014年8月12日 下午3:11:59
	 */
	public static void listOrders(@Required TaskSearchVo vo) {
		if (vo != null) {
			validation.required(vo.pageSize);
			vo.expressType=ExpressType.YDKD;
		}
		handleWrongInput(false);

		Page<OrderExpress> page = BuyerTask.findOrdersByPage(vo);
		renderPageJson(page.items, page.totalCount);
	}
    
    /**
     * 
     * 导出待打印快递号订单信息excel格式.
     *
     * @since  0.1
     * @author youblade
     * @throws IOException 
     * @throws InvalidFormatException 
     * @throws ParsePropertyException 
     * @created 2014年8月12日 下午2:49:54
     */
    public static void exportOrders(@Required TaskSearchVo vo) throws ParsePropertyException, InvalidFormatException, IOException {
    	if(vo!=null){
    		validation.required(vo.exportNo);
    	}
    	handleWrongInput(false);
    	
    	//取出需要导出的数据
    	vo.status(TaskStatus.WAIT_SEND_GOODS);
    	vo.expressType=ExpressType.YDKD;
    	List<OrderExpress> orders = BuyerTask.findOrders(vo);
    	if(MixHelper.isEmpty(orders)){
    		renderSuccessJson();
    	}
    	
    	// 批量更新任务状态
    	List<Long> ids = Lists.newArrayList();
    	for(OrderExpress order: orders){
    		ids.add(order.id);
    		order.weight=order.weight+(new Random().nextInt(5))/20f;
    	}
        DateTime now = DateTime.now();
        BuyerTask.batchModifyStatus(ids, TaskStatus.WAIT_EXPRESS_PRINT, now.toDate());
    	
    	//设置render方式
        String fileName = "order" + now.toString("yyyyMMddHHmm") + ".xls";
        response.contentType = "application/x-download";
		renderBinary(ExcelUtil.buildExportFile(Config.orderExpressXlsTemplate, orders), fileName);
    }
    
  /**
   * 
   * 导入订单
   *
   * @param file
   * @throws Exception
   * @since  v3.2
   * @author fufei
   * @created 2015年4月23日 下午3:22:31
   */
	public static void uploadOrderExpress(@Required File file) throws Exception {
		handleWrongInput(false);

		List<OrderExpress> orders = ExcelUtil.parseExcelFileToBeans(file, Config.orderExpressYdJxlsConfig);
		int count = 0;

		// 更新任务状态
		for (OrderExpress order : orders) {
		    // 
		    if (Strings.isNullOrEmpty(order.expressNo)) {
                renderText("请检查该条数据 订单号：" + order.orderId + " 导入失败，[快递单号]不存在。");
            }
		    
			BuyerTask btFromLocal = BuyerTask.findByOrderId(order.orderId);

			// 数据库中记录不存在 返回前端
			if (btFromLocal == null) {
				renderText("请检查该条数据 订单号：" + order.orderId + " 导入失败，该任务不存在。");
			}

			// 状态已经更新过 跳过更新
			if (btFromLocal.status == TaskStatus.EXPRESS_PRINT) {
				continue;
			} else if (btFromLocal.status != TaskStatus.WAIT_EXPRESS_PRINT) {
				renderText("请检查该条数据： 订单号：" + order.orderId + " 导入失败，该任务状态不符合条件");
			}

			btFromLocal.status = TaskStatus.EXPRESS_PRINT;
			btFromLocal.expressNo = order.expressNo;
			btFromLocal.modifyTime=DateTime.now().toDate();
			if(StringUtils.trim(order.orderId).equals(StringUtils.trim(btFromLocal.orderId)))
			if (BuyerTask.modify(btFromLocal) <= 0) {
				renderText("导入失败，请检查该条数据： 订单号：" + order.orderId);
			}

			// 统计导入成功数目
			count++;
		}
		renderText("导入成功，共" + count + "条");
	}
	
	/**
	 * 
	 * 快递单打印->.
	 *
	 * @since  v0.2.1
	 * @author youblade
	 * @created 2014年10月28日 下午4:53:18
	 */
    public static void resetExpressOrder(@Required @Min(1) long buyerTaskId) {
        handleWrongInput(false);

        BuyerTask task = BuyerTask.findById(buyerTaskId);
        if (task == null || (task.status != TaskStatus.WAIT_EXPRESS_PRINT&& task.status != TaskStatus.EXPRESS_PRINT)) {
            renderFailedJson(ReturnCode.BIZ_LIMIT);
        }

        // 更新状态为“待发货”
        BuyerTask buyerTask = BuyerTask.instance(buyerTaskId).modifyTime(LocalDate.now().toDate());
        if(task.status==TaskStatus.EXPRESS_PRINT){
            buyerTask.status(TaskStatus.WAIT_EXPRESS_PRINT);
        }else {
            buyerTask.status(TaskStatus.WAIT_SEND_GOODS);
        }
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
     * 买号审核
     * 
     * @since v0.1
     * @author tr0j4n
     * @created 2014-8-4 下午5:58:37
     */
    public static void buyerAudit() {
        render();
    }
    

	
    /**
     * 
     * 用户管理界面
     *
     * @since  v2.0
     * @author Mark Xu
     * @created 2015-1-15 下午2:06:13
     */
    public static void buyer() {
        renderArgs.put("userTypeList", UserType.values());
        renderArgs.put("userStatusList", UserStatus.values());
        renderArgs.put("userVipStatusList", VipStatus.values());
        renderArgs.put("jumpStatus", "BUYER");
        renderTemplate("Admin/user.html");
    }

    public static void seller() {
        renderArgs.put("userTypeList", UserType.values());
        renderArgs.put("userStatusList", UserStatus.values());
        
        renderArgs.put("userVipStatusList", VipStatus.values());
        renderArgs.put("jumpStatus", "SELLER");
        renderTemplate("Admin/user.html");
    }
    
	
	
	/**
	 * 
	 * 审核任务
	 * 
	 * @param t
	 * @since v0.1
	 * @author moloch
	 * @created 2014-8-22 下午2:13:23
	 */
	public synchronized static void examineTask(@Required Task t, String memo) {
		if (t != null) {
			validation.required(t.id);
			validation.required(t.status);
            validation.isTrue(t.status == TaskStatus.EXAMINING || t.status == TaskStatus.PUBLISHED
                || t.status == TaskStatus.NOT_PASS);
		}
		handleWrongInput(false);
		if(memo == null){
			memo = "";
		}
		
		String adminAccount = AdminUser.findByIdWichCache(Long.valueOf(Long.parseLong(session.get("admin-authcode")))).name;
		if(t.status == TaskStatus.NOT_PASS) {
			//将审核不通过记录插入到管理员操作记录表
			String message = MixHelper.format("任务编号{}-{}", t.id, memo);
			AdminOperatorLog.insert(adminAccount, LogType.AUDIT_TASK_FAIL, message);
		}
		
		Task task = Task.findById("status,publish_timer_interval,delay_span", t.id);
        // 仅允许审核状态为“待审核”、“审核中”的任务
        if (task.status != TaskStatus.WAIT_EXAMINE && task.status != TaskStatus.EXAMINING) {
        	renderFailedJson(ReturnCode.BIZ_LIMIT);
        }
        
        // 已经在审核中不再更新为审核中的状态
		if (task.status == TaskStatus.EXAMINING && t.status == TaskStatus.EXAMINING) {
			//将审核记录插入到管理员操作记录表
			renderSuccessJson();
		}
		
		// 判断是更新为审核中，还是审核通过
		t.modifyTime = DateTime.now().toDate();
		if (t.status == TaskStatus.EXAMINING) {
			t.examineTime = t.modifyTime;
		}
		if (t.status == TaskStatus.PUBLISHED) {
			t.publishTime = t.modifyTime;
			
			// 若该任务为定时任务，则设置状态为“待发布”，放入任务池中
			if (task.publishTimerInterval > 0 || task.delaySpan > 0) {
			    t.status = TaskStatus.WAIT_PUBLISH;
			}
		}
		
		t.examine(memo);

		if (t.status == TaskStatus.EXAMINING || t.status == TaskStatus.NOT_PASS) {
			renderSuccessJson();
		}
		
		

		//将审核记录插入到管理员操作记录表
		String message = MixHelper.format("任务编号{}", t.id);
		AdminOperatorLog.insert(adminAccount, LogType.AUDIT_TASK, message);
				
		renderSuccessJson();
	}
    
	
	/**
	 * 
	 * 审核浏览
	 */
	public synchronized static void examineTask3(@Required Task3 t, String memo) {
		if (t != null) {
			validation.required(t.id);
			validation.required(t.status);
            validation.isTrue(t.status == TaskStatus.EXAMINING || t.status == TaskStatus.PUBLISHED
                || t.status == TaskStatus.NOT_PASS);
		}
		handleWrongInput(false);
		if(memo == null){
			memo = "";
		}
		
		String adminAccount = AdminUser.findByIdWichCache(Long.valueOf(Long.parseLong(session.get("admin-authcode")))).name;
		if(t.status == TaskStatus.NOT_PASS) {
			//将审核不通过记录插入到管理员操作记录表
			String message = MixHelper.format("任务编号{}-{}", t.id, memo);
			AdminOperatorLog.insert(adminAccount, LogType.AUDIT_TASK_FAIL, message);
		}
		
		Task3 task = Task3.findById("status,publish_timer_interval,delay_span", t.id);
        // 仅允许审核状态为“待审核”、“审核中”的任务
        if (task.status != TaskStatus.WAIT_EXAMINE && task.status != TaskStatus.EXAMINING) {
        	renderFailedJson(ReturnCode.BIZ_LIMIT);
        }
        
        // 已经在审核中不再更新为审核中的状态
		if (task.status == TaskStatus.EXAMINING && t.status == TaskStatus.EXAMINING) {
			//将审核记录插入到管理员操作记录表
			renderSuccessJson();
		}
		
		// 判断是更新为审核中，还是审核通过
		t.modifyTime = DateTime.now().toDate();
		if (t.status == TaskStatus.EXAMINING) {
			t.examineTime = t.modifyTime;
		}
		if (t.status == TaskStatus.PUBLISHED) {
			t.publishTime = t.modifyTime;
			
			// 若该任务为定时任务，则设置状态为“待发布”，放入任务池中
			if (task.publishTimerInterval > 0 || task.delaySpan > 0) {
			    t.status = TaskStatus.WAIT_PUBLISH;
			}
		}
		
		t.examine(memo);

		if (t.status == TaskStatus.EXAMINING || t.status == TaskStatus.NOT_PASS) {
			renderSuccessJson();
		}
		
		

		//将审核记录插入到管理员操作记录表
		String message = MixHelper.format("任务编号{}", t.id);
		AdminOperatorLog.insert(adminAccount, LogType.AUDIT_TASK, message);
				
		renderSuccessJson();
	}
	
	
	
	/**
	 * 
	 * 审核推广
	 * 
	 * @param t
	 * @since v0.1
	 * @author moloch
	 * @created 2014-8-22 下午2:13:23
	 */
	public synchronized static void examineTask2(@Required Task2 t, String memo) {
		if (t != null) {
			validation.required(t.id);
			validation.required(t.status);
            validation.isTrue(t.status == TaskStatus.EXAMINING || t.status == TaskStatus.PUBLISHED
                || t.status == TaskStatus.NOT_PASS);
		}
		handleWrongInput(false);
		if(memo == null){
			memo = "";
		}
		
		String adminAccount = AdminUser.findByIdWichCache(Long.valueOf(Long.parseLong(session.get("admin-authcode")))).name;
		if(t.status == TaskStatus.NOT_PASS) {
			//将审核不通过记录插入到管理员操作记录表
			String message = MixHelper.format("任务编号{}-{}", t.id, memo);
			AdminOperatorLog.insert(adminAccount, LogType.AUDIT_TASK_FAIL, message);
		}
		
		Task2 task = Task2.findById("status,publish_timer_interval,delay_span", t.id);
        // 仅允许审核状态为“待审核”、“审核中”的任务
        if (task.status != TaskStatus.WAIT_EXAMINE && task.status != TaskStatus.EXAMINING) {
        	renderFailedJson(ReturnCode.BIZ_LIMIT);
        }
        
        // 已经在审核中不再更新为审核中的状态
		if (task.status == TaskStatus.EXAMINING && t.status == TaskStatus.EXAMINING) {
			//将审核记录插入到管理员操作记录表
			renderSuccessJson();
		}
		
		// 判断是更新为审核中，还是审核通过
		t.modifyTime = DateTime.now().toDate();
		if (t.status == TaskStatus.EXAMINING) {
			t.examineTime = t.modifyTime;
		}
		if (t.status == TaskStatus.PUBLISHED) {
			t.publishTime = t.modifyTime;
			
			// 若该任务为定时任务，则设置状态为“待发布”，放入任务池中
			if (task.publishTimerInterval > 0 || task.delaySpan > 0) {
			    t.status = TaskStatus.WAIT_PUBLISH;
			}
		}
		
		t.examine(memo);

		if (t.status == TaskStatus.EXAMINING || t.status == TaskStatus.NOT_PASS) {
			renderSuccessJson();
		}
		
		

		//将审核记录插入到管理员操作记录表
		String message = MixHelper.format("任务编号{}", t.id);
		AdminOperatorLog.insert(adminAccount, LogType.AUDIT_TASK, message);
				
		renderSuccessJson();
	}
    
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	/**
	 * 
	 * 审核买号.
	 * 
	 * @since 0.1
	 * @author youblade
	 * @created 2014年8月12日 下午1:30:58
	 */
	public static void examineBuyerAccount(@Required BuyerAccount ba) {
		// 校验必填字段
		if (ba != null) {
			validation.required(ba.id);
			validation.required(ba.status);
		}
		handleWrongInput(false);
		String message = ba.memo;
		ba.updateStatus();
		
		String adminAccount = AdminUser.findByIdWichCache(Long.valueOf(Long.parseLong(session.get("admin-authcode")))).name;
    	ba = BuyerAccount.findById(ba.id);
		if(ba.status == ExamineStatus.NOT_PASS) {
        	String message1 = MixHelper.format("买号{}-{}", ba.nick, message);
        	AdminOperatorLog.insert(adminAccount, LogType.AUDIT_SELLER_FAIL, message1);
		}else {
			String message1 = MixHelper.format("买号{}", ba.nick);
			AdminOperatorLog.insert(adminAccount, LogType.AUDIT_SELLER, message1);
		}
		renderSuccessJson();
	}
	
	
	/**
	 * 审核浏览号.
	 */
	public static void examineBuyerAccount3(@Required BuyerAccount3 ba) {
		// 校验必填字段
		if (ba != null) {
			validation.required(ba.id);
			validation.required(ba.status);
		}
		handleWrongInput(false);
		String message = ba.memo;
		ba.updateStatus();
		
		String adminAccount = AdminUser.findByIdWichCache(Long.valueOf(Long.parseLong(session.get("admin-authcode")))).name;
    	ba = BuyerAccount3.findById(ba.id);
		if(ba.status == ExamineStatus3.NOT_PASS) {
        	String message1 = MixHelper.format("买号{}-{}", ba.nick, message);
        	AdminOperatorLog.insert(adminAccount, LogType.AUDIT_SELLER_FAIL, message1);
		}else {
			String message1 = MixHelper.format("买号{}", ba.nick);
			AdminOperatorLog.insert(adminAccount, LogType.AUDIT_SELLER, message1);
		}
		renderSuccessJson();
	}
	
	
	/**
	 * 
	 * 审核推广号.
	 * 
	 * @since 0.1
	 * @author 尤齐春
	 * @created 2016-8-9
	 */
	public static void examineBuyerAccount2(@Required BuyerAccount2 ba) {
		// 校验必填字段
		if (ba != null) {
			validation.required(ba.id);
			validation.required(ba.status);
		}
		handleWrongInput(false);
		String message = ba.memo;
		ba.updateStatus();
		
		String adminAccount = AdminUser.findByIdWichCache(Long.valueOf(Long.parseLong(session.get("admin-authcode")))).name;
    	ba = BuyerAccount2.findById(ba.id);
		if(ba.status == ExamineStatus2.NOT_PASS) {
        	String message1 = MixHelper.format("买号{}-{}", ba.nick, message);
        	AdminOperatorLog.insert(adminAccount, LogType.AUDIT_SELLER_FAIL, message1);
		}else {
			String message1 = MixHelper.format("买号{}", ba.nick);
			AdminOperatorLog.insert(adminAccount, LogType.AUDIT_SELLER, message1);
		}
		renderSuccessJson();
	}
	
	
	
	
	/**
	 * 
	 * 处理用户提现申请
	 *
	 * @since  v0.1
	 * @author tr0j4n
	 * @created 2014-9-16 下午6:37:07
	 */
    public static void handleWithdrawal(){
    	render();
    }
    
	
	/**
	 * 
	 * 获取待审核任务列表
	 *
	 * @param vo
	 * @since  v0.1
	 * @author moloch & youblade
	 * @created 2014-8-22 上午10:17:23
	 */
    public static void listEaxmineTask(@Required TaskSearchVo vo) {
        handleWrongInput(false);
        
        // 必须有审核相关的状态参数
        if (vo.status != TaskStatus.WAIT_EXAMINE && vo.status != TaskStatus.EXAMINING) {
            renderFailedJson(ReturnCode.BIZ_LIMIT);
        }

        vo.sellerNick = StringUtils.trimToNull(vo.sellerNick);
        vo.shopName = StringUtils.trimToNull(vo.shopName);
        
        Page<SellerTaskVo> page = Task.findExamineVoByPage(vo);
        renderPageJson(page.items, page.totalCount);
    }
    
    /**
	 * 
	 * 获取待审核任务列表
	 */
    public static void listEaxmineTask3(@Required TaskSearchVo3 vo) {
        handleWrongInput(false);
        
        // 必须有审核相关的状态参数
        if (vo.status != TaskStatus.WAIT_EXAMINE && vo.status != TaskStatus.EXAMINING) {
            renderFailedJson(ReturnCode.BIZ_LIMIT);
        }

        vo.sellerNick = StringUtils.trimToNull(vo.sellerNick);
        vo.shopName = StringUtils.trimToNull(vo.shopName);
        
        Page<SellerTaskVo3> page = Task3.findExamineVoByPage(vo);
        renderPageJson(page.items, page.totalCount);
    }
    
    
    /**
	 * 
	 * 获取待审核推广列表
	 *
	 * @author 尤齐春
	 * @created 2016-8-2 
	 */
    public static void listEaxmineTask2(@Required TaskSearchVo2 vo) {
        handleWrongInput(false);
        
        // 必须有审核相关的状态参数
        if (vo.status != TaskStatus.WAIT_EXAMINE && vo.status != TaskStatus.EXAMINING) {
            renderFailedJson(ReturnCode.BIZ_LIMIT);
        }

        vo.sellerNick = StringUtils.trimToNull(vo.sellerNick);
        vo.shopName = StringUtils.trimToNull(vo.shopName);
        
        Page<SellerTaskVo2> page = Task2.findExamineVoByPage(vo);
        renderPageJson(page.items, page.totalCount);
    }
    
    
    
    
    
    
    private static boolean validate(String nick, String password) {
        // 开发测试时可以使用其他密码
        if(AppMode.get().isNotOnline()){
            return "admin".equals(nick) && ("m2470s".equals(password) || "ndyz.901".endsWith(password));
        }
        return "admin".equals(nick) && "m2470s".equals(password);
    }
    
    /**
     * 
     * 清理用户缓存.
     *
     * @param uid
     * @since  0.1
     * @author youblade
     * @created 2014年9月22日 上午11:34:57
     */
    public static void clearUserCache(@Required long uid){
        Cache.safeDelete(CacheType.USER_INFO.getKey(uid));
        renderSuccessJson();
    }
    
    /**
     * 
     * 清理缓存.
     *
     * @param key
     * @since  v1.9.9.1
     * @author youblade
     * @created 2015年1月5日 上午10:50:53
     */
    public static void clearCache(@Required String key){
        Cache.safeDelete(key);
        renderSuccessJson();
    }
    
    
    /**
     * 资金管理页面.
     *
     * @since  0.1
     * @author youblade
     * @created 2014年10月1日 上午11:34:19
     */
    public static void money(){
    	renderArgs.put("signs", Sign.values());
        render();
    }

    /**
     * 
     * 公告管理页面.
     *
     * @param vo
     * @since  0.1
     * @author youblade
     * @created 2014年9月30日 上午10:08:09
     */
    public static void notice(){
        render();
    }
    
  
	/**
	 * 
	 * 提现处理
	 * 
	 * @since v0.1
	 * @author moloch
	 * @created 2014-10-3 上午10:37:18
	 */
	public static void withdraw() {
		renderArgs.put("status", WithdrawStatus.values());
		render();
	}
	
	/**
	 * 
	 * 买手垫付本金提现
	 *
	 * @since  v1.6
	 * @author playlaugh
	 * @created 2014年12月5日 上午11:48:50
	 */
	public static void buyerDepositWithdraw() {
	    renderArgs.put("status", WithdrawStatus.values());
        render();
	}

	/**
	 * 
	 * 账户管理.
	 *
	 * @since  v1.0
	 * @author youblade
	 * @created 2014年11月21日 下午2:49:49
	 */
	public static void fundAccount() {
		renderArgs.put(BizConstants.PAY_PLATFORMS, PayPlatform.values());
		render();
	}
	
	/**
	 * 买号管理
	 */
	public static void buyerAccount() {
	    renderArgs.put("ExamineStatus", ExamineStatus.values());
	    render();
	}
	
	
	/**
	 * 浏览号管理
	 */
	public static void buyerAccount3() {
	    renderArgs.put("ExamineStatus", ExamineStatus.values());
	    render();
	}
	
	/**
	 * 推广号管理
	 */
	public static void buyerAccount2() {
	    renderArgs.put("ExamineStatus", ExamineStatus.values());
	    render();
	}
	
	
	/**
	 * 
	 * 店铺管理
	 *
	 * @since  v0.1
	 * @author moloch
	 * @created 2014-10-6 下午12:51:33
	 */
	public static void shop(){
		render();
	}
	

    /**
     * 
     * 任务管理
     * 
     * @since v0.1
     * @author moloch
     * @created 2014-10-3 上午11:41:38
     */
    public static void tasks() {
        renderArgs.put(BizConstants.PLATFORMS, Platform.values());
        renderArgs.put(BizConstants.DEVICES, Device.values());
        renderArgs.put(BizConstants.TASK_STATUS, TaskStatus.listForSellerTask());
        render();
    }
    
    
    /**
     * 
     * 推广管理
     * 
     * @since v0.1
     * @author 尤齐春
     * @created 2016-8-3 上午
     */
    public static void tasks2() {
        renderArgs.put(BizConstants.PLATFORMS, Platform2.values());
        renderArgs.put(BizConstants.DEVICES, Device.values());
        renderArgs.put(BizConstants.TASK_STATUS, TaskStatus.listForSellerTask());
        render();
    }
    
    
    
    
    /**
     * 推广管理
     */
    public static void tasks3() {
        renderArgs.put(BizConstants.PLATFORMS, Platform3.values());
        renderArgs.put(BizConstants.DEVICES, Device.values());
        renderArgs.put(BizConstants.TASK_STATUS, TaskStatus.listForSellerTask());
        render();
    }
    
    
    /**
     * 
     * 买手任务管理
     *
     * @since  v1.9
     * @author youblade
     * @created 2014年12月10日 下午6:30:49
     */
    public static void buyerTasks() {
        renderArgs.put(BizConstants.PLATFORMS, Platform.values());
        renderArgs.put(BizConstants.DEVICES, Device.values());
        renderArgs.put(BizConstants.TASK_STATUS, TaskStatus.ADMIN_BUYER_TASK_STATUS);
        renderArgs.put(BizConstants.EXPRESS_TYPE, ExpressType.values());
        render();
    }
    
    /**
     * 
     * 买手浏览管理
     */
    public static void buyerTasks3() {
        renderArgs.put(BizConstants.PLATFORMS, Platform3.values());
        renderArgs.put(BizConstants.DEVICES, Device.values());
        renderArgs.put(BizConstants.TASK_STATUS, TaskStatus.ADMIN_BUYER_TASK_STATUS);
        renderArgs.put(BizConstants.EXPRESS_TYPE, ExpressType.values());
        render();
    }
    
    
    /**
     * 
     * 买手推广管理
     *
     * @since  v1.9
     * @author	尤齐春
     * @created 2016年8月4日 下午
     */
    public static void buyerTasks2() {
        renderArgs.put(BizConstants.PLATFORMS, Platform2.values());
        renderArgs.put(BizConstants.DEVICES, Device.values());
        renderArgs.put(BizConstants.TASK_STATUS, TaskStatus.ADMIN_BUYER_TASK_STATUS);
        render();
    }
    
    
    
    /**
     * 
     * 邀请注册管理
     *
     * @since  v2.0
     * @author youblade
     * @created 2014年12月19日 下午6:14:07
     */
    public static void invite() {
        renderArgs.put("userStatusList", UserStatus.values());
        renderArgs.put("userTypeList", UserType.values());
        render();
    }
    
    public static void taskInvite() {
    	renderArgs.put("userStatusList", UserStatus.values());
    	render();
    }
    
    /**
     * 
     * 活动管理
     *
     * @since  v2.0
     * @author youblade
     * @created 2014年12月19日 下午8:41:33
     */
    public static void activity() {
        renderArgs.put("inviteRegistRule", Activity.findInviteRegistRule());
        render();
    }

    /**
     * 
     * 取得所有任务
     * 
     * @param vo
     * @since v0.1
     * @author moloch
     * @created 2014-10-7 下午4:43:28
     */
    public static void listSellerTask(@Required TaskSearchVo vo) {
        if (vo != null) {
            validation.required("vo.pageNo", vo.pageNo);
            validation.required("vo.pageSize", vo.pageSize);
            validation.range("vo.pageSize", vo.pageSize, 1, 50);
        }
        handleWrongInput(false);
        Calendar calendar=Calendar.getInstance();
        if(vo.publishTimeStart!=null&&vo.publishTimeEnd!=null&&!vo.publishTimeStart.before(vo.publishTimeEnd)){
        	calendar.setTime(vo.publishTimeEnd);
        	int day=calendar.get(Calendar.DATE);  
        	calendar.set(Calendar.DATE,day+1);  
        	vo.publishTimeEnd=calendar.getTime();
        }
        Page<Task> page = Task.findByPage(vo
            .fields("t.id,t.item_title,a.name as sellerAdminName,t.item_pic,t.platform,t.shop_name,t.seller_nick,t.publish_time,t.create_time,t.status,t.total_order_num"));
        for (Task t : page.items) {
            t.statusTitle = t.status.title;
            t.platformTitle = t.platform.title;
        }
        renderPageJson(page.items, page.totalCount);
    }
    
    
    /**
     * 
     * 取得所有任务
     */
    public static void listSellerTask3(@Required TaskSearchVo3 vo) {
        if (vo != null) {
            validation.required("vo.pageNo", vo.pageNo);
            validation.required("vo.pageSize", vo.pageSize);
            validation.range("vo.pageSize", vo.pageSize, 1, 50);
        }
        handleWrongInput(false);
        Calendar calendar=Calendar.getInstance();
        if(vo.publishTimeStart!=null&&vo.publishTimeEnd!=null&&!vo.publishTimeStart.before(vo.publishTimeEnd)){
        	calendar.setTime(vo.publishTimeEnd);
        	int day=calendar.get(Calendar.DATE);  
        	calendar.set(Calendar.DATE,day+1);  
        	vo.publishTimeEnd=calendar.getTime();
        }
        Page<Task3> page = Task3.findByPage(vo
            .fields("t.id,t.item_title,a.name as sellerAdminName,t.item_pic,t.platform,t.shop_name,t.seller_nick,t.publish_time,t.create_time,t.status,t.total_order_num"));
        for (Task3 t : page.items) {
            t.statusTitle = t.status.title;
            t.platformTitle = t.platform.title;
        }
        renderPageJson(page.items, page.totalCount);
    }
    
    
    /**
     * 
     * 取得所有推广
     * 
     * @param vo
     * @since v0.1
     * @author 尤齐春
     * @created 2016-8-4 下午
     */
    public static void listSellerTask2(@Required TaskSearchVo2 vo) {
        if (vo != null) {
            validation.required("vo.pageNo", vo.pageNo);
            validation.required("vo.pageSize", vo.pageSize);
            validation.range("vo.pageSize", vo.pageSize, 1, 50);
        }
        handleWrongInput(false);
        Calendar calendar=Calendar.getInstance();
        if(vo.publishTimeStart!=null&&vo.publishTimeEnd!=null&&!vo.publishTimeStart.before(vo.publishTimeEnd)){
        	calendar.setTime(vo.publishTimeEnd);
        	int day=calendar.get(Calendar.DATE);  
        	calendar.set(Calendar.DATE,day+1);  
        	vo.publishTimeEnd=calendar.getTime();
        }
        Page<Task2> page = Task2.findByPage(vo
            .fields("t.id,t.item_title,a.name as sellerAdminName,t.item_pic,t.platform,t.shop_name,t.seller_nick,t.publish_time,t.create_time,t.status,t.total_order_num"));
        for (Task2 t : page.items) {
            t.statusTitle = t.status.title;
            t.platformTitle = t.platform.title;
        }
        renderPageJson(page.items, page.totalCount);
    }
    
    
    
    
    
    
    /**
     * 
     * 
     * 根据id取出任务详情
     * 
     * @param id
     * @since v0.1
     * @author moloch
     * @created 2014-8-22 下午2:09:17
     */
    public static void taskDetail(@Required @Min(1) long id) {
        handleWrongInput(false);

        Task task = Task.findById(id);
        
        task.platformTitle = task.platform.title;
        renderJson(task);
    }
    
    
    /**
     * 根据id取出任务详情
     */
    public static void taskDetail3(@Required @Min(1) long id) {
        handleWrongInput(false);

        Task3 task = Task3.findById(id);
        task.platformTitle = task.platform.title;
        renderJson(task);
    }
    
    
    /**
     * 根据id取出任务详情
     * @author	尤齐春
     * @created 2016-8-2 
     */
    public static void taskDetail2(@Required @Min(1) long id) {
        handleWrongInput(false);

        Task2 task = Task2.findById(id);
        
        task.platformTitle = task.platform.title;
        renderJson(task);
    }
    
    
    
    
    public static void modifyTaskRequest(String taskRequest, @Required long id) {
    	handleWrongInput(false);
    	
    	Task.updateTaskRequest(taskRequest, id);
    	renderSuccessJson();
    }
    
    public static void modifyTaskRequest3(String taskRequest, @Required long id) {
    	handleWrongInput(false);
    	Task3.updateTaskRequest(taskRequest, id);
    	renderSuccessJson();
    }
    
    
    
    /**
     * 
     * TODO 查询用户验证码.
     *
     * @param id
     * @since  v0.2.5
     * @author youblade
     * @created 2014年11月7日 下午2:25:29
     */
    public static void viewSmsValidCode(@Required String nick) {
        handleWrongInput(false);
        
        String smsValidCode = CacheUtil.get(CacheType.SMS_VALID_CODE.getKey(nick));
        if(Strings.isNullOrEmpty(smsValidCode)){
            renderText("该用户的验证码已失效，需要重新发送！");
        }
        renderText(smsValidCode);
    }
    
    /**
   	 * 
   	 * 重置用户本金提现到‘待处理’状态
   	 * 
   	 * @since v2.0
   	 * @author fufei
   	 * @created 2015年1月12日 下午4:02:12
   	 */
   	public static void resetStatusUserWithdrawRecord(@Required @Min(1) long id) {
   		handleWrongInput(false);
   		UserWithdrawRecord record = UserWithdrawRecord.findById(id);
   		if (record == null) {
   			renderFailedJson(-1, "该条记录不存在");
   		}
   		if (record.status == WithdrawStatus.WAIT) {
   			renderFailedJson(-1, "该条记录已为待处理状态，不能重置！");
   		}
   		if (record.status == WithdrawStatus.FINISHED)
   			record.status = WithdrawStatus.PROCESSING;
   		else if (record.status == WithdrawStatus.PROCESSING)
   			record.status = WithdrawStatus.WAIT;
   		record.save();
   		renderSuccessJson();
   	}
   	/**
	 * 
	 * 流量记录页面
	 * 
	 * @since v2.0
	 * @author fufei
	 * @created 2015年1月14日 下午2:42:34
	 */
	public static void trafficRecord() {
		renderArgs.put("status", TrafficStatus.values());
   		renderArgs.put("adminUserList", AdminType.values());
		render();
	}

	/**
	 * 
	 * TODO 分页获取流量数据
	 * 
	 * @since v2.0
	 * @author fufei
	 * @created 2015年1月14日 下午2:00:05
	 */
	public static void listTrafficRecord(@Required TrafficRecordVo vo) {
		handleWrongInput(false);
		if (vo != null) {
			validation.required("vo.pageNo", vo.pageNo);
			validation.required("vo.pageSize", vo.pageSize);
		}
		
		Page<TrafficRecord> page = TrafficRecord.listTrafficRecord(vo);
		renderPageJson(page.items, page.totalCount);
	}
	/**
	 * 
	 * 添加流量任务
	 *
	 * @param vo
	 * @since  v2.0
	 * @author fufei
	 * @created 2015年1月15日 下午5:08:55
	 */
	public static void addTrafficRecord(@Required TrafficRecordVo vo) {
		handleWrongInput(false);
		if (vo != null) {
			validation.required("vo.kwd", vo.kwd);
			validation.required("vo.nid", vo.nid);
		}
		//保存操作
		TrafficRecord.modifyTraffic(vo);
		trafficRecord();
	}
	/**
	 * 
	 * 根据ID查找流量任务
	 *
	 * @param id
	 * @since  v2.0
	 * @author fufei
	 * @created 2015年1月16日 上午11:30:49
	 */
	public static void findTrafficById(@Required @Min(1)long id) {
		handleWrongInput(false);
		validation.required("id", id);
		TrafficRecordVo vo=TrafficRecordVo.newInstance();
		vo.id=id;
		List<TrafficRecord> records=TrafficRecord.listTrafficRecord(vo).items;
		if(CollectionUtils.isEmpty(records)){
			renderFailedJson(-2, "该流量记录不存在！");
		}
		renderJson(records.get(0));
	}
	/**
	 * 
	 * 更新流量任务
	 *
	 * @param vo
	 * @since  v2.0
	 * @author fufei
	 * @created 2015年1月16日 下午1:53:39
	 */
	public static void modifyTraffic(@Required TrafficRecordVo vo) {
		handleWrongInput(false);
		validation.required("vo.id", vo.id);
		TrafficRecord.modifyTraffic(vo);
		TrafficRecordVo recordVo=new TrafficRecordVo();
		recordVo.id=vo.id;
		List<TrafficRecord> records=TrafficRecord.listTrafficRecord(recordVo).items;
		//调用接口更新流量任务
		String rest=TrafficRecordUtil.flow(records.get(0), Method.MODIFY);
		log.info("modify result"+rest);
		renderSuccessJson();
   	}
	/**
	 * 
	 * 渲染超级管理员管理其他管理员界面
	 *
	 * @since  v2.0
	 * @author Mark Xu
	 * @created 2015-1-20 下午1:02:03
	 */
	public static void adminAccount() {
   		renderArgs.put("adminStatusList", AdminStatus.values());
   		renderArgs.put("adminUserList", AdminType.values());
   		render();
   	}
	/**
	 * 
	 * 获取流量返回次数接口
	 *
	 * @param status
	 * @param yyyy-MM-dd
	 * @since  v2.0
	 * @author fufei
	 * @created 2015年3月4日 上午9:54:24
	 */
	public static void getReturnTimesByEndTime(String time) {
		List<TrafficRecord> records = TrafficRecord.findByStatus(TrafficStatus.FINISHED.toString(),time);
		for (TrafficRecord trafficRecord : records) {
			TrafficRecord.getReturnTimes(trafficRecord);
		}
		renderSuccessJson();
	}
	
	public static void adminOperatorLog() {
		renderArgs.put("logTypeList", LogType.values());
		render();
	}
	
	/**
	 * 
	 * 导出某时间段任务
	 *
	 * @since  v2.0
	 * @author fufei
	 * @created 2015年1月21日 下午4:15:05
	 */
	public static void exportTasks(@Required TaskSearchVo vo,int count) {
		if (vo != null) {
            validation.required("vo.pageNo", vo.pageNo);
            validation.required("vo.pageSize", vo.pageSize);
            validation.required("vo.publishTimeStart", vo.publishTimeStart);
        }
        handleWrongInput(false);
        vo.pageSize=vo.pageSize>=count?vo.pageSize:count;
        Calendar calendar=Calendar.getInstance();
        if(vo.publishTimeStart!=null&&vo.publishTimeEnd!=null&&!vo.publishTimeStart.before(vo.publishTimeEnd)){
        	calendar.setTime(vo.publishTimeEnd);
        	int day=calendar.get(Calendar.DATE);  
        	calendar.set(Calendar.DATE,day+1);  
        	vo.publishTimeEnd=calendar.getTime();
        }
        Page<Task> page = Task.findByPage(vo
            .fields("t.id,item_title,item_pic,total_order_num,platform,shop_name,seller_nick,publish_time,t.create_time,status"));
        SimpleDateFormat sf=new SimpleDateFormat("yyyy-MM-dd");
        for (Task t : page.items) {
            t.statusTitle = t.status.title;
            t.publishTimeStr=sf.format(t.publishTime);
            t.createTimeStr=sf.format(t.createTime);
            t.idStr=t.id.toString();
        }
        DateTime now = DateTime.now();
    	//设置render方式
        String fileName = "tasks" + now.toString("yyyyMMddHHmm") + ".xls";
        response.contentType = "application/x-download";
		renderBinary(ExcelUtil.buildExportFile(Config.tasksXlsTemplate, page.items), fileName);
	}
	
	
	
	/**
	 * 
	 * 导出某时间段任务
	 */
	public static void exportTasks3(@Required TaskSearchVo3 vo,int count) {
		if (vo != null) {
            validation.required("vo.pageNo", vo.pageNo);
            validation.required("vo.pageSize", vo.pageSize);
            validation.required("vo.publishTimeStart", vo.publishTimeStart);
        }
        handleWrongInput(false);
        vo.pageSize=vo.pageSize>=count?vo.pageSize:count;
        Calendar calendar=Calendar.getInstance();
        if(vo.publishTimeStart!=null&&vo.publishTimeEnd!=null&&!vo.publishTimeStart.before(vo.publishTimeEnd)){
        	calendar.setTime(vo.publishTimeEnd);
        	int day=calendar.get(Calendar.DATE);  
        	calendar.set(Calendar.DATE,day+1);  
        	vo.publishTimeEnd=calendar.getTime();
        }
        Page<Task3> page = Task3.findByPage(vo
            .fields("t.id,item_title,item_pic,total_order_num,platform,shop_name,seller_nick,publish_time,t.create_time,status"));
        SimpleDateFormat sf=new SimpleDateFormat("yyyy-MM-dd");
        for (Task3 t : page.items) {
            t.statusTitle = t.status.title;
            t.publishTimeStr=sf.format(t.publishTime);
            t.createTimeStr=sf.format(t.createTime);
            t.idStr=t.id.toString();
        }
        DateTime now = DateTime.now();
    	//设置render方式
        String fileName = "tasks" + now.toString("yyyyMMddHHmm") + ".xls";
        response.contentType = "application/x-download";
		renderBinary(ExcelUtil.buildExportFile(Config.tasksXlsTemplate, page.items), fileName);
	}
	
	
	
	/**
	 * 
	 * 导出某时间段推广
	 *
	 * @since  v2.0
	 * @author 尤齐春
	 * @created 2016年8月3日 下午
	 */
	public static void exportTasks2(@Required TaskSearchVo2 vo,int count) {
		if (vo != null) {
            validation.required("vo.pageNo", vo.pageNo);
            validation.required("vo.pageSize", vo.pageSize);
            validation.required("vo.publishTimeStart", vo.publishTimeStart);
        }
        handleWrongInput(false);
        vo.pageSize=vo.pageSize>=count?vo.pageSize:count;
        Calendar calendar=Calendar.getInstance();
        if(vo.publishTimeStart!=null&&vo.publishTimeEnd!=null&&!vo.publishTimeStart.before(vo.publishTimeEnd)){
        	calendar.setTime(vo.publishTimeEnd);
        	int day=calendar.get(Calendar.DATE);  
        	calendar.set(Calendar.DATE,day+1);  
        	vo.publishTimeEnd=calendar.getTime();
        }
        Page<Task2> page = Task2.findByPage(vo
            .fields("t.id,item_title,item_pic,total_order_num,platform,shop_name,seller_nick,publish_time,t.create_time,status"));
        SimpleDateFormat sf=new SimpleDateFormat("yyyy-MM-dd");
        for (Task2 t : page.items) {
            t.statusTitle = t.status.title;
            t.publishTimeStr=sf.format(t.publishTime);
            t.createTimeStr=sf.format(t.createTime);
            t.idStr=t.id.toString();
        }
        DateTime now = DateTime.now();
    	//设置render方式
        String fileName = "tasks" + now.toString("yyyyMMddHHmm") + ".xls";
        response.contentType = "application/x-download";
		renderBinary(ExcelUtil.buildExportFile(Config.tasksXlsTemplate, page.items), fileName);
	}
	
	
	
	
	
	
	
	
	/**
	 * 
	 * 导出某个商家的押金
	 *
	 * @param vo
	 * @param count
	 * @since  v2.0
	 * @author Mark Xu
	 * @created 2015-1-29 上午9:57:22
	 */
	public static void exportPledge(@Required MoneyRecordSearchVo vo,int count) {
		if (vo != null) {
            validation.required("vo.pageNo", vo.pageNo);
            validation.required("vo.pageSize", vo.pageSize);
        }
        handleWrongInput(false);
        vo.pageSize=vo.pageSize>=count?vo.pageSize:count;
        Page<SellerPledgeRecord> page = SellerPledgeRecord.findByPage(vo
            .fields("id,user_nick,task_Id,create_time,amount,sign,balance,memo"));
        SimpleDateFormat sf=new SimpleDateFormat("yyyy-MM-dd-HH：mm");
        for (SellerPledgeRecord r : page.items) {
        	if(r.sign == Sign.MINUS) {
        		r.minusAmountStr = ((double)(r.amount) / 100 + "元").toString();
        		r.plusAmountStr = null;
        	}else {
        		r.plusAmountStr = ((double)(r.amount) / 100 + "元").toString();
        		r.minusAmountStr = null;
        	}
            r.createTimeStr=sf.format(r.createTime);
            if(r.taskId != null)
            r.taskIdStr = r.taskId.toString();
        }
        //将items倒序排列
        Collections.reverse(page.items);
        DateTime now = DateTime.now();
    	//设置render方式
        String fileName = "pledge" + now.toString("yyyyMMddHHmm") + ".xls";
        response.contentType = "application/x-download";
		renderBinary(ExcelUtil.buildExportFile(Config.pledgeXlsTemplate, page.items), fileName);
	}
	/**
	 * 
	 * 查询任务商家id
	 *
	 * @since  v2.0
	 * @author fufei
	 * @created 2015年1月31日 下午12:20:46
	 */
	public static void getSellerIdByTask(long taskId) {
		long sellerId=0;
		Task task=Task.findById("seller_id", taskId);
		if(task!=null){
			sellerId=task.sellerId;
		}
		renderJson(sellerId);
	}
	/**
	 * 
	 * 每日接单数统计
	 *
	 * @since  v2.4
	 * @author fufei
	 * @created 2015年2月5日 上午10:40:01
	 */
	public static void takeTaskCount() {
		render();
	}
	/**
	 * 查询所有接单数统计
	 *
	 * @since  v2.4
	 * @author fufei
	 * @created 2015年2月5日 上午10:41:39
	 */
	public static void listTakeCount(TakeTaskCountVo vo) {
		Page<TakeTaskCountVo> pageVo=BuyerTask.findTakeTaskCount(vo);
		renderPageJson(pageVo.items, pageVo.totalCount);
	}
	/**
	 * 
	 * 商家放单时间页面
	 *
	 * @since  v2.0
	 * @author Mark Xu
	 * @created 2015-3-25 上午10:30:23
	 */
	public static void sellerPutTime() {
		render();
	}
	
	/**
	 * 
	 * 买手接单统计页面
	 *
	 * @since  v2.0
	 * @author Mark Xu
	 * @created 2015-5-8 下午1:15:58
	 */
	public static void buyerTakeTask() {
		render();
	}
	
	/**
	 * 
	 * 商家放单时间统计（包括   商家昵称、放单总量，最后放单时间、对接客服）
	 *
	 * @since  v2.0
	 * @author Mark Xu
	 * @created 2015-3-23 下午5:17:50
	 */
	public static void sellerPutTimeList(@Required SellerPutTimeVo vo) {
		handleWrongInput(false);
		
		Page<SellerPutTimeVo> pageVo = Task.findBySellerPutTimeVo(vo);
		renderPageJson(pageVo.items, pageVo.totalCount);
	}
	/**
	 * 
	 * 导出商家放单统计
	 *
	 * @param vo
	 * @since  v2.0
	 * @author Mark Xu
	 * @created 2015-5-7 下午3:19:48
	 */
	public static void sellerPutTimeExport(@Required SellerPutTimeVo vo) {
		List<SellerPutTimeVo> vos = Task.findBySellerPutTimeVo(vo).items;
		
		SimpleDateFormat sf=new SimpleDateFormat("yyyy-MM-dd-HH：mm");
        for (SellerPutTimeVo r : vos) {
        	if(r.registTime!=null) {
        		r.registTimeStr=sf.format(r.registTime);
        	}else {
        		r.registTimeStr="---------";
        	}
        	if(r.maxTime!=null) {
        		r.maxTimeStr=sf.format(r.maxTime);
        	}else {
        		r.maxTimeStr="---------";
        	}
        	r.ccStr = r.cc + "单";
        }
        //将items倒序排列
        DateTime now = DateTime.now();
    	//设置render方式
        String fileName = "sellerPutTime" + now.toString("yyyyMMddHHmm") + ".xls";
        response.contentType = "application/x-download";
		renderBinary(ExcelUtil.buildExportFile(Config.sellerPutTimeTemplate, vos), fileName);
	}
	
	/**
	 * 
	 * 买手接单统计
	 *
	 * @param vo
	 * @since  v2.0
	 * @author Mark Xu
	 * @created 2015-5-8 下午2:06:27
	 */
	public static void buyerTakeTaskList(@Required BuyerTakeTaskVo vo) {
		handleWrongInput(false);
		
		Page<BuyerTakeTaskVo> pageVo = Task.findByBuyerTakeTaskVo(vo);
		renderPageJson(pageVo.items, pageVo.totalCount);
	}
	/**
	 * 
	 * 买手接单统计导出
	 *
	 * @param vo
	 * @since  v2.0
	 * @author Mark Xu
	 * @created 2015-5-8 下午3:24:58
	 */
	public static void buyerTakeTaskExport(@Required BuyerTakeTaskVo vo) {
		List<BuyerTakeTaskVo> vos = Task.findByBuyerTakeTaskVo(vo).items;
		
		SimpleDateFormat sf=new SimpleDateFormat("yyyy-MM-dd-HH：mm");
        for (BuyerTakeTaskVo r : vos) {
        	if(r.createTime!=null) {
        		r.createTimeStr=sf.format(r.createTime);
        	}else {
        		r.createTimeStr="---------";
        	}
        	if(r.maxTime!=null) {
        		r.maxTimeStr=sf.format(r.maxTime);
        	}else {
        		r.maxTimeStr="---------";
        	}
        	r.ccStr = r.cc + "单";
        }
        //将items倒序排列
        DateTime now = DateTime.now();
    	//设置render方式
        String fileName = "sellerPutTime" + now.toString("yyyyMMddHHmm") + ".xls";
        response.contentType = "application/x-download";
		renderBinary(ExcelUtil.buildExportFile(Config.buyerTakeTaskTemplate, vos), fileName);
	}
	/**
	 * 
	 * 修改店铺信息
	 *
	 * @since  v2.4
	 * @author fufei
	 * @created 2015年2月5日 下午4:32:00
	 */
	public static void updateShop(ShopSearchVo vo) {
		if (vo != null) {
            validation.required("vo.nick", vo.nick);
            validation.required("vo.url", vo.url);
            validation.required("vo.sellerName", vo.sellerName);
            validation.required("vo.mobile", vo.mobile);
            validation.required("vo.street", vo.street);
            validation.required("vo.branch", vo.branch);
            validation.required("vo.address", vo.address);
            validation.required("vo.id", vo.id);
            validation.required("vo.platform", vo.platform);
        }
        handleWrongInput(false);
        List<Shop> shops=Shop.selectByPlatformAndNick(vo);
        if(shops.size()>1){
        	renderFailedJson(-2, "店铺名字已存在！");
        }
        if(shops.size()==1&&shops.get(0).id!=vo.id){
        	renderFailedJson(-2, "店铺名字已存在！");
        }
        if(Shop.updateShop(vo)>0){
        	renderSuccessJson();
        }
        renderFailedJson(-2, "保存失败！");
	}
	
	/**
	 * 临时接口
	 *修改流量任务保存的错误商家时间格式
	 *
	 * @since  v2.0
	 * @author fufei
	 * @created 2015年2月10日 上午11:05:36
	 */
	public static void updateTrafficTime() {
		List<TrafficRecord> records=TrafficRecord.getAll();
		for (TrafficRecord trafficRecord : records) {
			try {
				trafficRecord.beginTime=new SimpleDateFormat("yyyy-MM-dd").format(new SimpleDateFormat("yyyy-MM-dd").parse(trafficRecord.beginTime));
				trafficRecord.endTime=new SimpleDateFormat("yyyy-MM-dd").format(new SimpleDateFormat("yyyy-MM-dd").parse(trafficRecord.endTime));
			} catch (ParseException e) {
				e.printStackTrace();
			}
			trafficRecord.updateTime(trafficRecord);
		}
		renderSuccessJson();
	}
	/**
	 * 
	 * 临时接口
	 * 延长会员的时间 （一个月）
	 * @since  v2.0
	 * @author fufei
	 * @created 2015年2月10日 上午11:07:31
	 */
	public static void delayMember() {
		List<MemberChargeRecord> memberChargeRecords=MemberChargeRecord.findAll();
		for (MemberChargeRecord memberChargeRecord : memberChargeRecords) {
			User user=User.findById(memberChargeRecord.userId);
			Date due_time=DateTime.now().toDate();
			if(user!=null&&user.dueTime!=null){
				due_time=user.dueTime;
			}
			Date delayDate=new LocalDate(due_time).plusMonths(1).toDate();
			User.delayUserMember(user.id, delayDate);
			
			//清理缓存
			Cache.safeDelete(CacheType.USER_INFO.getKey(user.id));
		}
		renderSuccessJson();
	}
	
	public static void sellerBlance() {
		renderArgs.put(BizConstants.PLATFORMS, Platform.values());
		render();
	}
	/**
	 * 
	 *系统统计
	 *
	 * @since  v2.9
	 * @author fufei
	 * @created 2015年3月21日 上午11:16:00
	 */
	public static void findSysCount() {
	    SearchSystemCountVo countVos=Task.findSysCountVo(); 
	    DecimalFormat df=new DecimalFormat("#.00");
	    countVos.buyerPayIngoStr=df.format(countVos.buyerPayIngo);
	    countVos.sellerOutIngotStr=df.format(countVos.sellerOutIngot);
	    countVos.sysOutIngotStr=df.format(countVos.sysOutIngot);
	    countVos.buyerIngotStr=df.format(countVos.buyerIngot);
	    countVos.expressIngotStr=df.format(countVos.expressIngot);
	    countVos.sysServiceIngotStr=df.format(countVos.sysServiceIngot);
	    countVos.sysRefoundIngotStr=df.format(countVos.sysRefoundIngot);
	    countVos.extraIngotStr=df.format(countVos.extraIngot);
	    countVos.speedExamineIngotStr=df.format(countVos.speedExamineIngot);
	    countVos.fixedTimeIngotStr=df.format(countVos.fixedTimeIngot);
	    countVos.speedFinishedStr=df.format(countVos.speedFinishedIngot);
	    countVos.buyTimeIntervalIngotStr=df.format(countVos.buyTimeIntervalIngot);
	    countVos.buyerWidthdrawStr=df.format(countVos.buyerWidthdraw);
	    countVos.buyerIngotWidthdrawStr=df.format(countVos.buyerIngotWidthdraw);
	    renderArgs.put("countVos", countVos);
	    render();
    }
	
    /**
     * 
     * 统计买手当日接单数
     * 
     * @since v2.9
     * @author fufei
     * @created 2015年3月23日 下午3:24:31
     */
    public static void findNewBuyerTakenNum(@Required String takenTime) {
        handleWrongInput(false);
        List<User> users = User.findUserTakenCount(takenTime);
        HashMap<String, Integer> hs = new HashMap<String, Integer>();
        for (User user : users) {
            Integer count = 1;
            if (hs.get(user.nick) != null) {
                count = hs.get(user.nick) + 1;
            }
            hs.put(user.nick, count);
        }
        List<SearchTakenCount> takenCounts=new ArrayList<SearchTakenCount>();
        Iterator iter = hs.entrySet().iterator();
        while (iter.hasNext()) {
            Map.Entry entry = (Map.Entry) iter.next();
            String key = entry.getKey().toString();
            int val = Integer.parseInt(entry.getValue().toString()) ;
            SearchTakenCount count=new SearchTakenCount(key, val);
            takenCounts.add(count);
        }
        renderJson(takenCounts);
    }
	
	   /**
     * 
     * 统计新商家发单数
     *
     * @since  v2.9
     * @author fufei
     * @created 2015年3月23日 下午3:24:31
     */
    public static void findNewSellerPublishNum(@Required String takenTime) {
        handleWrongInput(false);
        List<Task> tasks = Task.findSellerPublishCount(takenTime);
        
        HashMap<String, Integer> hs = new HashMap<String, Integer>();
        for (Task task : tasks) {
            if(hs.get(task.sellerNick)==null)
            hs.put(task.sellerNick, 0);
        }
        List<SearchTakenCount> publishCounts = new ArrayList<SearchTakenCount>();
        Iterator iter = hs.entrySet().iterator();
        while (iter.hasNext()) {
            Map.Entry entry = (Map.Entry) iter.next();
            String key = entry.getKey().toString();
            int count=0;
            for (Task task : tasks) {
                if(task.sellerNick.equals(key)){
                    count+=task.totalOrderNum;
                }
            }
            SearchTakenCount takenCount=new SearchTakenCount(key, count);
            publishCounts.add(takenCount);
        }
        renderJson(publishCounts);
    }
	/**
	 * 
	 *统计当天任务接单或发布数
	 *
	 * @since  v2.9
	 * @author fufei
	 * @created 2015年3月24日 下午1:53:27
	 */
    public static void listCountOfTasks(@Required String takenTime,@Required String type,SearchTakenCount vo) {
        handleWrongInput(false);
        if("PUBLISH".equals(type)){
            Page<SearchTakenCount> takenCounts=Task.findSellerPublishToday(takenTime,vo);
            renderPageJson(takenCounts.items , takenCounts.totalCount);
        }else {
            Page<SearchTakenCount> takenCounts=Task.findBuyerTakenToday(takenTime,vo);
            renderPageJson(takenCounts.items , takenCounts.totalCount);
        }
    }
    
    /**
     * 
     *统计当天任务接单或发布数
     *
     * @since  v2.9
     * @author fufei
     * @created 2015年3月24日 下午1:53:27
     */
    public static void countOfTasks(@Required String takenTime,@Required String type) {
        handleWrongInput(false);
        renderArgs.put("takenTime", takenTime);
        renderArgs.put("type", type);
        render();
    }
    /**
     * 
     * 统计邀请奖励页面
     *
     * @since  v2.0
     * @author Mark Xu
     * @created 2015-3-27 下午2:39:43
     */
    public static void rewardCount(){
    	render();
    }
    
    /**
     * 
     * 统计邀请奖励
     *
     * @since  v2.0
     * @author Mark Xu
     * @created 2015-3-27 下午2:59:02
     */
    public static void listRewardCount(@Required RewardCountVo vo) {
    	handleWrongInput(false);
    	
    	Page<User> page = UserInvitedRecord.findRewardCountPage(vo);
    	renderPageJson(page.items, page.totalCount);
    }

   /**
     * 
     * 后台配置
     *
     * @since  v2.9
     * @author fufei
     * @created 2015年3月26日 下午3:40:07
     */
    public static void sysConfig() {
       render();
    }
     /* 后台配置列表
     *
     * @since  v2.9
     * @author fufei
     * @created 2015年3月26日 下午3:40:07
     */
    public static void listSysConfig() {
        List<SysConfig> configs=SysConfig.findByAll();
        renderJson(configs);
    }
    
    /**
     * 
     * 后台配置列表
     *
     * @since  v2.9
     * @author fufei
     * @created 2015年3月26日 下午3:40:07
     */
    public static void findConfigById(@Required long id) {
        handleWrongInput(false);
        SysConfig config=SysConfig.findById(id);
        renderJson(config);
    }
    
    /**
     * 
     * 后台配置修改
     *
     * @since  v2.9
     * @author fufei
     * @created 2015年3月26日 下午3:40:07
     */
    public static void updateSysConfig(@Required SysConfig sysConfig) {
        handleWrongInput(false);
        if(sysConfig!=null){
            validation.required("sysConfig.value", sysConfig.value);
            validation.required("sysConfig.id", sysConfig.id);
            validation.required("sysConfig.record", sysConfig.record);
        }
        SysConfig config=SysConfig.findById(sysConfig.id);
        if(config==null){
            renderFailedJson(ReturnCode.BIZ_LIMIT);
        }
        config.record=sysConfig.record;
        config.value=sysConfig.value;
        config.update();
        //更新缓存
        SysConfig.initCache();
        renderSuccessJson();
    }
    /**
     * 
     * 显示超时子任务界面
     *
     * @since  v2.0
     * @author Mark Xu
     * @created 2015-4-19 上午11:21:49
     */
    public static void timeout() {
    	render();
    }
    
    /**
     * 
     * 显示超时子推广界面
     *
     */
    public static void timeout2() {
    	render();
    }
    
    /**
     * 显示超时子浏览界面
     *
     */
    public static void timeout3() {
    	render();
    }
    
    
    /**
     * 流量充值或扣款
     * 
     * @since  v3.2
     * @author fufei
     * @created 2015年4月14日 上午10:57:14
     */
    public static void rechargeFlow(@Required String type,@Required int amount,@Required long userId,@Required String memo) {
        handleWrongInput(false);
        validation.isTrue(amount>0);
        if(UserFlowRecord.rechargeFlow(type, amount, userId,memo)){
            renderSuccessJson();
        }else {
            renderFailedJson(ReturnCode.BIZ_LIMIT,"充值失败");
        }
    }
    
   /**
    * 
    * 本金扣款、充值
    *
    * @param type
    * @param amount
    * @param userId
    * @param memo
    * @since  v3.4
    * @author fufei
    * @created 2015年4月28日 上午10:38:56
    */
    public static void rechargeDeposit(@Required String type,@Required @Min(0.1) double amount,@Required long userId,@Required String memo) {
        handleWrongInput(false);
        validation.isTrue(amount>0);
//        if(BuyerDepositRecord.rechargeDeposit(type, (long)amount*100, userId,memo)){
//            renderSuccessJson();
//        }else {
//            renderFailedJson(ReturnCode.BIZ_LIMIT,"操作失败");
//        }
    }
    
    /**
     * 
     * 财务对账
     *
     * @since  v2.0
     * @author Mark Xu
     * @created 2015-4-15 上午11:02:00
     */
    public static void financeReconciliation() {
    	render();
    }
    
    /**
     * 
     * 金钱收入记录
     *
     * @since  v2.0
     * @author Mark Xu
     * @created 2015-4-15 上午11:24:28
     */
    public static void listReconciliation(FinanceLogVo vo) {
    	Page<FinanceLogVo> page = AdminTradeLog.findListForFinance(vo);
    	renderPageJson(page.items, page.totalCount);
    }
    
    public static void adminAmountList(String date) {
    	List<AdminTradeLog> items = AdminTradeLog.findByDate(date);
    	renderJson(items);
    }
    
    
    public static void platAmountList(String date) {
    	List<PayTradeLog> items = PayTradeLog.findByDate(date);
    	renderJson(items);
    }
    /**
     * 
     * 流量统计
     *
     * @since  3.2
     * @author fufei
     * @created 2015年4月21日 上午10:48:38
     */
    public static void clickCount() {
        render();
    }
    /**
     * 
     * 流量统计列表
     *
     * @since  3.2
     * @author fufei
     * @created 2015年4月21日 上午10:48:38
     */
    public static void listClickCount(@Required FlowTimesVo vo) {
        handleWrongInput(false);
        Page<FlowTimesVo> page=TrafficRecord.totalTrafficCount(vo);
        renderPageJson(page.items, page.totalCount);
    }
    /**
     * 
     * 发包裹
     *
     * @since  v3.4
     * @author fufei
     * @created 2015年4月29日 下午3:57:40
     */
    public static void faBaoGuo() {
        render();
    }
    
   /**
    * 
    * 分页获取发包裹待处理的订单.
    *
    * @param vo
    * @since  v3.4
    * @author fufei
    * @created 2015年4月29日 下午5:17:55
    */
    public static void listFaBaoGuoOrders(@Required TaskSearchVo vo) {
        if (vo != null) {
            validation.required(vo.pageSize);
            vo.batchNum=StringUtils.trimToNull(vo.batchNum);
        }
        handleWrongInput(false);
        Page<FaBaoGuoVo> page = BuyerTask.findFaBaoGuoOrders(vo);
        renderPageJson(page.items, page.totalCount);
    }
    /**
     * 
     * 导出发包裹订单
     *
     * @param vo
     * @throws ParsePropertyException
     * @throws InvalidFormatException
     * @throws IOException
     * @since  v3.4
     * @author fufei
     * @created 2015年4月30日 下午2:10:21
     */
    public static void exportFaBaoGuoOrders(@Required TaskSearchVo vo) throws ParsePropertyException, InvalidFormatException, IOException {
        if(vo!=null){
            validation.required(vo.exportNo);
        }
        vo.batchNum=StringUtils.trimToNull(vo.batchNum);
        handleWrongInput(false);
        if(vo.status==null){
            vo.status=TaskStatus.WAIT_SEND_GOODS;
        }
        //取出需要导出的数据
        List<FaBaoGuoVo> orders = BuyerTask.exportFaBaoGuoOrders(vo);
        if(MixHelper.isEmpty(orders)){
            renderText("没有要导出的数据");
        }
        
        // 批量更新任务状态
        List<Long> ids = Lists.newArrayList();
        for(FaBaoGuoVo order: orders){
            ids.add(order.id);
        }
        DateTime now = DateTime.now();
        if(vo.status==TaskStatus.WAIT_SEND_GOODS){
            BuyerTask.batchDeleteFaBaoGuo(ids, TaskStatus.WAIT_SEND_GOODS);
        }else{
            BuyerTask.batchFaBaoGuoModifyStatus(ids, TaskStatus.EXPRESS_PRINT, now.toDate());
        }
        //设置render方式
        String fileName = "order" + now.toString("yyyyMMddHHmm") + ".xls";
        response.contentType = "application/x-download";
        renderBinary(ExcelUtil.buildExportFile(Config.faBaoGuoOrderExpressXlsTemplate, orders), fileName);
    }
    /**
     * 
     * 重置订单状态
     *
     * @param 3.5
     * @author fufei
     * @created 2015年4月30日 下午3:33:03
     */
    public static void resetFaBaoGuoExpressOrder(@Required long id) {
        handleWrongInput(false);
        FaBaoGuoVo vo = BuyerTask.findFaBaoGuoById(id);
        if (vo == null || (vo.status != TaskStatus.WAIT_EXPRESS_PRINT&& vo.status != TaskStatus.EXPRESS_PRINT)) {
            renderFailedJson(ReturnCode.BIZ_LIMIT,"请联系管理员");
        }
        // 更新状态为“待发货”
        if(vo.status==TaskStatus.EXPRESS_PRINT){
            BuyerTask.updateFaBaoGuoById(id, TaskStatus.WAIT_EXPRESS_PRINT);
        }else {
            BuyerTask.updateFaBaoGuoById(id, TaskStatus.WAIT_SEND_GOODS);
        }
        renderSuccessJson();
    }
    /**
     * 
     *上传发包裹信息
     *
     * @param file
     * @throws Exception
     * @since  v3.4
     * @author fufei
     * @created 2015年4月30日 下午5:10:47
     */
    public static void uploadFaBaoGuoOrderExpress(@Required File file) throws Exception {
        handleWrongInput(false);

        int count= 0;
        try {
            List<FaBaoGuoVo> orders = ExcelUtil.parseExcelFileToBeans(file, Config.fabaoguoOrderExpressConfig);
            
            String batchNum=DateTime.now().toDate().getTime()+"";
            // 更新任务状态
            for (FaBaoGuoVo order : orders) {
                // 
                FaBaoGuoVo btFromLocal = BuyerTask.findFaBaoGuoByprderSn(order.order_sn);
                order.batch_num=batchNum;
                order.status=TaskStatus.WAIT_SEND_GOODS;
                order.send_time=DateTime.now().toDate();
                // 数据库中记录不存在 返回前端
                if (btFromLocal != null) {
                    renderText("请检查该条数据 订单号：" + order.order_sn + " 导入失败，该订单号已存在。");
                }
                BuyerTask.inserFabaoguoOrders(order);

                // 统计导入成功数目
                count++;
            }
        } catch (Exception e) {
            renderText(e.toString());
        }
        renderText("导入成功，共" + count + "条");
    }
    /**
     * 
     * 生成订单号
     *
     * @since  v3.4
     * @author fufei
     * @created 2015年5月4日 上午10:50:56
     */
    public static void printOrders() {
        TaskSearchVo vo=TaskSearchVo.newInstance();
        vo.exportNo=1000;
        vo.status(TaskStatus.WAIT_SEND_GOODS);
        List<FaBaoGuoVo> orders = BuyerTask.exportFaBaoGuoOrders(vo);
        if(orders.isEmpty()){
            renderFailedJson(-2, "没有待生成运单号的数据");
        }
        for (FaBaoGuoVo faBaoGuoVo : orders) {
            FaBaoGuoReturn returnVo= FaBaoGuoUtil.execute(faBaoGuoVo);
            if(returnVo==null){
                renderFailedJson(-2, "请检查单号为【"+faBaoGuoVo.order_sn+"】的订单返回失败!");
            }
            if(returnVo.res!=0){
                renderFailedJson(-2, "请检查单号为【"+faBaoGuoVo.order_sn+"】的订单返回失败!返回code为:"+returnVo.res+",原因是："+FaBaoGuoUtil.RETURN_CODE.get(returnVo.res));
            }
            BuyerTask.updateFaBaoGuoExpressNoById(faBaoGuoVo.id, TaskStatus.WAIT_EXPRESS_PRINT, returnVo.express_no);
        }
        renderSuccessJson();
    }
    /**
     * 
     * 快递单统计
     *
     * @param vo
     * @since  v3.4
     * @author fufei
     * @created 2015年5月6日 下午3:31:15
     */
    public static void listExpressCount(@Required ExpressCountVo vo) {
        handleWrongInput(false);
        Page<ExpressCountVo> page = BuyerTask.findExpressList(vo);
        renderPageJson(page.items, page.totalCount);
    }
    /**
     * 
     *快递单统计
     *
     * @since  v3.4
     * @author fufei
     * @created 2015年5月6日 下午4:27:56
     */
    public static void expressCount() {
        ExpressCountVo vo=ExpressCountVo.newInstance();
        ExpressCountVo vo2=ExpressCountVo.newInstance();
        vo.expressType=ExpressType.KJKD;
        vo2.kjkd=BuyerTask.findExpressCount(vo);
        vo.expressType=ExpressType.YDKD;
        vo2.ydkd=BuyerTask.findExpressCount(vo);
        vo.expressType=ExpressType.SELLERKD;
        vo2.sellerKd=BuyerTask.findExpressCount(vo);
        vo2.fabaoguo=BuyerTask.findFaBaoGuoCount(vo);
        FaBaoGuoInfoReturn infoReturn=FaBaoGuoUtil.getInfo();
        renderArgs.put("expressRest", infoReturn);
        renderArgs.put("express", vo2);
        render();
    }
    /**
     * 
     *快递单 总数
     *
     * @param vo
     * @since  v3.4
     * @author fufei
     * @created 2015年5月6日 下午4:31:36
     */
    public static void searchExpressTotal(@Required ExpressCountVo vo) {
        handleWrongInput(false);
       vo.sellerNick=StringUtils.trimToNull(vo.sellerNick);
       vo.startTime=StringUtils.trimToNull(vo.startTime);
       vo.endTime=StringUtils.trimToNull(vo.endTime);
       ExpressCountVo vo2=ExpressCountVo.newInstance();
       vo.expressType=ExpressType.KJKD;
       vo2.kjkd=BuyerTask.findExpressCount(vo);
       vo.expressType=ExpressType.YDKD;
       vo2.ydkd=BuyerTask.findExpressCount(vo);
       vo.expressType=ExpressType.SELLERKD;
       vo2.sellerKd=BuyerTask.findExpressCount(vo);
       vo2.fabaoguo=BuyerTask.findFaBaoGuoCount(vo);
       renderJson(vo2);
    }
    /**
     * 
     * 更新快递类型临时接口
     *
     * @since  v3.4
     * @author fufei
     * @created 2015年5月6日 下午5:59:06
     */
    public static void updateExpress() {
        Task.updateTaskExpressType();
        renderSuccessJson();
    }
    /**
     * 
     * 修改快递单号
     *
     * @since  v3.5
     * @author fufei
     * @created 2015年5月7日 上午11:13:28
     */
    public static void updateExpressNo(@Required @Min(1) long id,@Required String expressNo) {
        handleWrongInput(false);
        BuyerTask task=BuyerTask.findById(id);
        if(task==null){
            renderFailedJson(ReturnCode.BIZ_LIMIT, "当前任务不存在！");
        }
        task.expressNo=expressNo;
        BuyerTask.modify(task);
        renderSuccessJson();
    }
    
    
    /**
     * 
     * 修改快递单号
     */
    public static void updateExpressNo3(@Required @Min(1) long id,@Required String expressNo) {
        handleWrongInput(false);
        BuyerTask3 task=BuyerTask3.findById(id);
        if(task==null){
            renderFailedJson(ReturnCode.BIZ_LIMIT, "当前任务不存在！");
        }
        task.expressNo=expressNo;
        BuyerTask3.modify(task);
        renderSuccessJson();
    }
    
    
    /**
     * 
     *手动获取
     *
     * @param orderId
     * @since  v3.4 bug
     * @author fufei
     * @created 2015年5月8日 下午4:09:45
     */
    public static void getExpreeNo(@Required String orderId) {
        handleWrongInput(false);
        TaskSearchVo vo = new TaskSearchVo();
        vo.orderId = orderId;
        vo.expressType = ExpressType.YDKD;
        vo.exportNo = 1;
        vo.status(TaskStatus.WAIT_SEND_GOODS);
        List<OrderExpress> orders = BuyerTask.findOrdersByPage(vo).items;
        List<FaBaoGuoVo> baoGuoVos = FaBaoGuoUtil.convert(orders);
        if (baoGuoVos.size() > 0) {
            FaBaoGuoReturn baoGuoReturn = FaBaoGuoUtil.execute(baoGuoVos.get(0));
            if (baoGuoReturn == null) {
                log.info("fa bao guo return null!");
                renderFailedJson(-2, "返回为空");
            }
            log.info("retrun code " + baoGuoReturn.res + ",express_no:" + baoGuoReturn.express_no);
            if (baoGuoReturn.res == 0) {
                BuyerTask btFromLocal = BuyerTask.findByOrderId(baoGuoVos.get(0).order_sn);
                if (btFromLocal == null) {
                    renderFailedJson(-2, "订单不存在！");
                    log.info("check order number " + baoGuoVos.get(0).order_sn + " not exsit");
                }
                btFromLocal.status = TaskStatus.EXPRESS_PRINT;
                btFromLocal.expressNo = baoGuoReturn.express_no;
                btFromLocal.modifyTime = DateTime.now().toDate();
                BuyerTask.modifyYDKD(btFromLocal);

            } else {
                renderFailedJson(-2, "api返回结果：" + baoGuoReturn.res);
                log.info("retrun code " + baoGuoReturn.res);
            }

        }
        renderSuccessJson();
    }
    
}