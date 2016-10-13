package controllers;

import java.io.File;
import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import models.AdminRefundRecord;
import models.BuyerAccount;
import models.BuyerAccount2;
import models.BuyerAccount3;
import models.BuyerDepositRecord;
import models.BuyerTask;
import models.BuyerTask2;
import models.BuyerTask3;
import models.BuyerTaskStep;
import models.BuyerTaskStep2;
import models.BuyerTaskStep3;
import models.FundAccount;
import models.SellerPledgeRecord;
import models.SysConfig;
import models.SellerPledgeRecord.PledgeAction;
import models.SysConfig.SysConfigKey;
import models.Task2;
import models.Task3;
import models.TrafficRecord.TrafficStatus;
import models.Task;
import models.TrafficRecord;
import models.User;
import models.User.UserType;
import models.User.VipStatus;
import models.mappers.AdminRefundRecordMapper;
import models.mappers.BuyerTaskMapper;
import models.mappers.BuyerTaskMapper2;
import models.mappers.BuyerTaskMapper3;
import models.mappers.BuyerTaskStepMapper;
import models.mappers.BuyerTaskStepMapper2;
import models.mappers.BuyerTaskStepMapper3;
import models.mappers.SellerPledgeRecordMapper;
import models.mappers.TaskMapper;
import models.mappers.fund.BuyerDepositRecordMapper;

import org.apache.commons.lang.BooleanUtils;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.session.SqlSession;
import org.joda.time.DateTime;
import org.joda.time.Hours;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import play.data.validation.Min;
import play.data.validation.Required;
import play.mvc.With;
import vos.BuyerAccountSearchVo;
import vos.BuyerAccountSearchVo.ExamineStatus;
import vos.BuyerAccountSearchVo2;
import vos.BuyerAccountSearchVo2.ExamineStatus2;
import vos.BuyerAccountSearchVo3;
import vos.BuyerAccountSearchVo3.ExamineStatus3;
import vos.BuyerTaskStepVo;
import vos.BuyerTaskVo;
import vos.BuyerTaskVo2;
import vos.BuyerTaskVo3;
import vos.Page;
import vos.SellerBalanceVo;
import vos.SellerTaskVo;
import vos.SellerTaskVo2;
import vos.SellerTaskVo3;
import vos.TaskCountVo;
import vos.TaskCountVo2;
import vos.TaskCountVo3;
import vos.TaskSearchVo;
import vos.TaskSearchVo.SearchModule;
import vos.TaskSearchVo2;
import vos.TaskSearchVo2.SearchModule2;
import vos.TaskSearchVo3;
import vos.TrafficRecordVo;

import com.aton.base.BaseController;
import com.aton.base.secure.Secure;
import com.aton.config.BizConstants;
import com.aton.config.CacheType;
import com.aton.config.Config;
import com.aton.config.ReturnCode;
import com.aton.db.SessionFactory;
import com.aton.util.CacheUtil;
import com.aton.util.CollectionUtils;
import com.aton.util.ExcelUtil;
import com.aton.util.NumberUtils;
import com.aton.util.StringUtils;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Multimap;

import domain.TaskStats;
import domain.TaskStats3;
import enums.BuyerTaskStepType;
import enums.Device;
import enums.Platform;
import enums.Platform2;
import enums.Platform3;
import enums.TaskListType;
import enums.TaskStatus;
import enums.TaskType;
import enums.TaskType2;
import enums.TaskType3;
import enums.pay.PayPlatform;

/**
 * 
 * 任务管理.
 * 
 * @author youblade
 * @since  0.1
 * @created 2014年7月14日 下午5:55:12
 */
@With(Secure.class)
public class TaskCenter extends BaseController{

    public static final Logger log = LoggerFactory.getLogger(TaskCenter.class);
    
    /**
     * 
     * TODO 任务列表页面.
     *
     * @since  0.1
     * @author youblade
     * @created 2014年7月14日 下午5:55:29
     */
    public static void tasks(){
    	//平台数据 这里做了筛选 不显示天猫以及无可接任务数的平台
    	List<Platform> pfs = Lists.newArrayList(Platform.values());
    	pfs.remove(Platform.TMALL);
    	if(BuyerTask.findCountByPlatform(Platform.JD)==0) pfs.remove(Platform.JD);
    	if(BuyerTask.findCountByPlatform(Platform.MOGUJIE)==0) pfs.remove(Platform.MOGUJIE);
        renderArgs.put("Platforms", pfs);
        renderArgs.put("Devices", Device.values());
        renderArgs.put("TaskTypes", TaskType.values());
        renderArgs.put("TaskListTypes", TaskListType.values());
        render();
    }
    
    
    /**
     * TODO 任务列表页面.
     */
    public static void tasks3(){
    	//平台数据 这里做了筛选 不显示天猫以及无可接任务数的平台
    	List<Platform3> pfs = Lists.newArrayList(Platform3.values());
    	pfs.remove(Platform3.TMALL);
        renderArgs.put("Platforms", pfs);
        renderArgs.put("Devices", Device.values());
        renderArgs.put("TaskTypes", TaskType3.values());
        renderArgs.put("TaskListTypes", TaskListType.values());
        render();
    }
    
    
    
    /**
     * 
     * TODO 推广列表页面.
     *
     * @since  0.1
     * @author youblade
     * @created 2014年7月14日 下午5:55:29
     */
    public static void tasks2(){
    	//平台数据 这里做了筛选 不显示天猫以及无可接任务数的平台
    	List<Platform2> pfs = Lists.newArrayList(Platform2.values());
        renderArgs.put("Platforms", pfs);
        renderArgs.put("Devices", Device.values());
        renderArgs.put("TaskTypes", TaskType2.values());
        renderArgs.put("TaskListTypes", TaskListType.values());
        render();
    }
    
    /**
     * 
     * 分别获取各平台任务
     *
     * @since  v2.0
     * @author Mark Xu
     * @created 2015-2-26 下午4:09:53
     */
    public static void platformCount() {
    	Map map = Maps.newHashMapWithExpectedSize(12);
        map.put(Platform.TAOBAO, BuyerTask.findCountByPlatform(Platform.TAOBAO)+BuyerTask.findCountByPlatform(Platform.TMALL));
        map.put(Platform.JD, BuyerTask.findCountByPlatform(Platform.JD));
        map.put(Platform.MOGUJIE, BuyerTask.findCountByPlatform(Platform.MOGUJIE));
        renderJson(map);
    }
    
    
    /**
     * 
     * 分别获取各平台任务
     */
    public static void platformCount3() {
    	Map map = Maps.newHashMapWithExpectedSize(12);
        map.put(Platform3.TAOBAO, BuyerTask3.findCountByPlatform(Platform3.TAOBAO)+BuyerTask3.findCountByPlatform(Platform3.TMALL));
//        map.put(Platform.YHD, BuyerTask.findCountByPlatform(Platform.YHD));
//        map.put(Platform.JUMEI, BuyerTask.findCountByPlatform(Platform.JUMEI));
//        map.put(Platform.AMAZON, BuyerTask.findCountByPlatform(Platform.AMAZON));
//        map.put(Platform.DANGDANG, BuyerTask.findCountByPlatform(Platform.DANGDANG));
//        map.put(Platform.QQ, BuyerTask.findCountByPlatform(Platform.QQ));
//        map.put(Platform.ALIBABA, BuyerTask.findCountByPlatform(Platform.ALIBABA));
//        map.put(Platform.GUOMEI, BuyerTask.findCountByPlatform(Platform.GUOMEI));
//        map.put(Platform.SUNING, BuyerTask.findCountByPlatform(Platform.SUNING));
        renderJson(map);
    }
    
    
    
    
    /**
     * 
     * 分别获取各平台推广
     *
     * @author 尤齐春
     * @created 2016-8-3
     */
    public static void platformCount2() {
    	Map map = Maps.newHashMapWithExpectedSize(12);
        map.put(Platform2.WEIXIN, BuyerTask2.findCountByPlatform(Platform2.WEIXIN));
        map.put(Platform2.QQ, BuyerTask2.findCountByPlatform(Platform2.QQ));
        map.put(Platform2.WEIBO, BuyerTask2.findCountByPlatform(Platform2.WEIBO));
        map.put(Platform2.OTHER, BuyerTask2.findCountByPlatform(Platform2.OTHER));
        renderJson(map);
    }
    
    
 
    
    
    
    /**
     * 
     * 选择平台后分别获取PC和PAD的数量
     *
     * @since  v2.0
     * @author Mark Xu
     * @created 2015-2-26 下午4:23:29
     */
    public static void deviceCount(@Required Platform platform) {
    	Map map = Maps.newHashMapWithExpectedSize(2);
    	if(platform == Platform.TAOBAO | platform == Platform.TMALL) {
    		map.put("PC", BuyerTask.findPcCountByPlatform(Platform.TAOBAO, null) + BuyerTask.findPcCountByPlatform(Platform.TMALL, null));
    		map.put("PAD", BuyerTask.findPadCountByPlatform(Platform.TAOBAO, null) + BuyerTask.findPadCountByPlatform(Platform.TMALL, null));
    	}else {
    		map.put("PC", BuyerTask.findPcCountByPlatform(platform, null));
    		map.put("PAD", BuyerTask.findPadCountByPlatform(platform, null));
    	}
        renderJson(map);
    }
    
    
    /**
     * 选择平台后分别获取PC和PAD的数量
     */
    public static void deviceCount3(@Required Platform3 platform) {
    	Map map = Maps.newHashMapWithExpectedSize(2);
    	if(platform == Platform3.TAOBAO | platform == Platform3.TMALL) {
    		map.put("PC", BuyerTask3.findPcCountByPlatform(Platform3.TAOBAO, null) + BuyerTask3.findPcCountByPlatform(Platform3.TMALL, null));
    		map.put("PAD", BuyerTask3.findPadCountByPlatform(Platform3.TAOBAO, null) + BuyerTask3.findPadCountByPlatform(Platform3.TMALL, null));
    	}else {
    		map.put("PC", BuyerTask3.findPcCountByPlatform(platform, null));
    		map.put("PAD", BuyerTask3.findPadCountByPlatform(platform, null));
    	}
        renderJson(map);
    }
    
    
    
    
    
    /**
     * 
     * 选择平台后分别获取PC和PAD的数量
     *
     * @since  v2.0
     * @author yqc
     * @created 2016-8-3 
     */
    public static void deviceCount2(@Required Platform2 platform) {
    	Map map = Maps.newHashMapWithExpectedSize(12);
    		map.put("PC", BuyerTask2.findPcCountByPlatform(platform, null));
    		map.put("PAD", BuyerTask2.findPadCountByPlatform(platform, null));
        renderJson(map);
    }
    /**
     * 
     * 选择终端类型后分别获取任务类型的数量
     *
     * @param platform
     * @since  v2.0
     * @author Mark Xu
     * @created 2015-2-26 下午4:26:54
     */
    public static void taskTypeCount(@Required Platform platform, @Required String device) {
    	Map map = Maps.newHashMapWithExpectedSize(3);
    	if(platform == Platform.TAOBAO | platform == Platform.TMALL) {
    		if(device.equals("PC")) {
    			map.put(TaskType.ORDER, BuyerTask.findPcCountByPlatform(Platform.TAOBAO, TaskType.ORDER) + BuyerTask.findPcCountByPlatform(Platform.TMALL, TaskType.ORDER));
    			map.put(TaskType.JHS, BuyerTask.findPcCountByPlatform(Platform.TAOBAO, TaskType.JHS) +BuyerTask.findPcCountByPlatform(Platform.TMALL, TaskType.JHS));
    			map.put(TaskType.SUBWAY, BuyerTask.findPcCountByPlatform(Platform.TAOBAO, TaskType.SUBWAY) + BuyerTask.findPcCountByPlatform(Platform.TMALL, TaskType.SUBWAY));
    		}else {
    			map.put(TaskType.ORDER, BuyerTask.findPadCountByPlatform(Platform.TAOBAO, TaskType.ORDER) + BuyerTask.findPadCountByPlatform(Platform.TMALL, TaskType.ORDER));
    			map.put(TaskType.JHS, BuyerTask.findPadCountByPlatform(Platform.TAOBAO, TaskType.JHS) + BuyerTask.findPadCountByPlatform(Platform.TMALL, TaskType.JHS));
    			map.put(TaskType.SUBWAY, BuyerTask.findPadCountByPlatform(Platform.TAOBAO, TaskType.SUBWAY) + BuyerTask.findPadCountByPlatform(Platform.TMALL, TaskType.SUBWAY));
    		}
    	}else {
    		if(device.equals("PC")) {
    			map.put(TaskType.ORDER, BuyerTask.findPcCountByPlatform(platform, TaskType.ORDER));
    			map.put(TaskType.JHS, BuyerTask.findPcCountByPlatform(platform, TaskType.JHS));
    			map.put(TaskType.SUBWAY, BuyerTask.findPcCountByPlatform(platform, TaskType.SUBWAY));
    		}else {
    			map.put(TaskType.ORDER, BuyerTask.findPadCountByPlatform(platform, TaskType.ORDER));
    			map.put(TaskType.JHS, BuyerTask.findPadCountByPlatform(platform, TaskType.JHS));
    			map.put(TaskType.SUBWAY, BuyerTask.findPadCountByPlatform(platform, TaskType.SUBWAY));
    		}
    	}
        renderJson(map);
    }
    
    
    
    /**
     * 
     * 选择终端类型后分别获取任务类型的数量
     */
    public static void taskTypeCount3(@Required Platform3 platform, @Required String device) {
    	Map map = Maps.newHashMapWithExpectedSize(3);
    	if(platform == Platform3.TAOBAO | platform == Platform3.TMALL) {
    		if(device.equals("PC")) {
    			map.put(TaskType3.ORDER, BuyerTask3.findPcCountByPlatform(Platform3.TAOBAO, TaskType3.ORDER) + BuyerTask3.findPcCountByPlatform(Platform3.TMALL, TaskType3.ORDER));
    			map.put(TaskType3.JHS, BuyerTask3.findPcCountByPlatform(Platform3.TAOBAO, TaskType3.JHS) +BuyerTask3.findPcCountByPlatform(Platform3.TMALL, TaskType3.JHS));
    			map.put(TaskType3.SUBWAY, BuyerTask3.findPcCountByPlatform(Platform3.TAOBAO, TaskType3.SUBWAY) + BuyerTask3.findPcCountByPlatform(Platform3.TMALL, TaskType3.SUBWAY));
    		}else {
    			map.put(TaskType3.ORDER, BuyerTask3.findPadCountByPlatform(Platform3.TAOBAO, TaskType3.ORDER) + BuyerTask3.findPadCountByPlatform(Platform3.TMALL, TaskType3.ORDER));
    			map.put(TaskType3.JHS, BuyerTask3.findPadCountByPlatform(Platform3.TAOBAO, TaskType3.JHS) + BuyerTask3.findPadCountByPlatform(Platform3.TMALL, TaskType3.JHS));
    			map.put(TaskType3.SUBWAY, BuyerTask3.findPadCountByPlatform(Platform3.TAOBAO, TaskType3.SUBWAY) + BuyerTask3.findPadCountByPlatform(Platform3.TMALL, TaskType3.SUBWAY));
    		}
    	}else {
    		if(device.equals("PC")) {
    			map.put(TaskType3.ORDER, BuyerTask3.findPcCountByPlatform(platform, TaskType3.ORDER));
    			map.put(TaskType3.JHS, BuyerTask3.findPcCountByPlatform(platform, TaskType3.JHS));
    			map.put(TaskType3.SUBWAY, BuyerTask3.findPcCountByPlatform(platform, TaskType3.SUBWAY));
    		}else {
    			map.put(TaskType3.ORDER, BuyerTask3.findPadCountByPlatform(platform, TaskType3.ORDER));
    			map.put(TaskType3.JHS, BuyerTask3.findPadCountByPlatform(platform, TaskType3.JHS));
    			map.put(TaskType3.SUBWAY, BuyerTask3.findPadCountByPlatform(platform, TaskType3.SUBWAY));
    		}
    	}
        renderJson(map);
    }
    
    
    
    
    /**
     * 
     * 选择终端类型后分别获取任务类型的数量
     *
     * @param platform
     * @since  v2.0
     * @author 尤齐春
     * @created 2016-8-3
     */
    public static void taskTypeCount2(@Required Platform2 platform, @Required String device) {
    	Map map = Maps.newHashMapWithExpectedSize(3);
    		if(device.equals("PC")) {
    			map.put(TaskType2.TOUPIAO, BuyerTask2.findPcCountByPlatform(platform, TaskType2.TOUPIAO));
    			map.put(TaskType2.QUNFA, BuyerTask2.findPcCountByPlatform(platform, TaskType2.QUNFA));
    			map.put(TaskType2.PENGYOUQUAN, BuyerTask2.findPcCountByPlatform(platform, TaskType2.PENGYOUQUAN));
    		}else {
    			map.put(TaskType2.TOUPIAO, BuyerTask2.findPadCountByPlatform(platform, TaskType2.TOUPIAO));
    			map.put(TaskType2.QUNFA, BuyerTask2.findPadCountByPlatform(platform, TaskType2.QUNFA));
    			map.put(TaskType2.PENGYOUQUAN, BuyerTask2.findPadCountByPlatform(platform, TaskType2.PENGYOUQUAN));
    		}
        renderJson(map);
    }
    
    
    
    
    /**
     * 
     * 任务列表->分页获取任务(暂时无分页).
     *
     * @since  0.1
     * @author youblade
     * @created 2014年7月25日 下午6:03:14
     */
	public static void listTasks(@Required TaskSearchVo vo, long buyerAccountId) {
	    if (vo != null) {
            validation.min(vo.pageSize, 1);
            validation.max(vo.pageSize, 50);
        }
        handleWrongInput(false);
		if (buyerAccountId <= 0) {
			User buyer = getCurrentUser();
			if (buyer != null) {
				BuyerAccountSearchVo accountSearchVo = BuyerAccountSearchVo.newInstance();
				accountSearchVo.userId = buyer.id;
				accountSearchVo.platform=vo.platform;
				accountSearchVo.status=ExamineStatus.EXAMINED;
				List<BuyerAccount> buyerAccount = BuyerAccount.findForTakingTask(accountSearchVo);
				if (CollectionUtils.isNotEmpty(buyerAccount)) {
					buyerAccountId = buyerAccount.get(0).id;
				}
			}
		}
		Page<Task> tasks = Task.findBySearchVo(vo.status(TaskStatus.PUBLISHED));
		// 处理id数字过长前端json无法正确转换的问题
        for (Iterator it = tasks.items.iterator(); it.hasNext();) {
        	Task task = (Task)it.next();
        	if (task.sellerNick.equals("xuliubin")) {
                task.sellerNick = "滑板鞋";
            }
            if (task.sellerNick.equals("wocaonimaaa")) {
                task.sellerNick = "可爱又迷人";
            }
            task.resolveId();
            BuyerTask buyerTask = BuyerTask.newInstance();
            buyerTask.buyerAccountId = buyerAccountId;
            buyerTask.taskId = task.id;
            buyerTask.device = vo.device;
            //是否过滤不可解任务
            User user = getCurrentUser();
        	if (!isTakeTasks(buyerTask, task) && user!=null && !isAdminOperate() && user.isClearView==true){
        		it.remove();
        	}
        }
		
		renderPageJson(tasks.items, tasks.totalCount);
	}
	
	
	/**
     * 
     * 浏览列表->分页获取任务(暂时无分页).
     */
	public static void listTasks3(@Required TaskSearchVo3 vo, long buyerAccountId) {
	    if (vo != null) {
            validation.min(vo.pageSize, 1);
            validation.max(vo.pageSize, 50);
        }
        handleWrongInput(false);
		if (buyerAccountId <= 0) {
			User buyer = getCurrentUser();
			if (buyer != null) {
				BuyerAccountSearchVo3 accountSearchVo = BuyerAccountSearchVo3.newInstance();
				accountSearchVo.userId = buyer.id;
				accountSearchVo.platform=vo.platform;
				accountSearchVo.status=ExamineStatus3.EXAMINED;
				List<BuyerAccount3> buyerAccount = BuyerAccount3.findForTakingTask(accountSearchVo);
				if (CollectionUtils.isNotEmpty(buyerAccount)) {
					buyerAccountId = buyerAccount.get(0).id;
				}
			}
		}
		Page<Task3> tasks = Task3.findBySearchVo(vo.status(TaskStatus.PUBLISHED));
		// 处理id数字过长前端json无法正确转换的问题
        for (Iterator it = tasks.items.iterator(); it.hasNext();) {
        	Task3 task = (Task3)it.next();
        	
        	
        	System.out.println("tasks=====totalIngot================="+task.toString());
        	System.out.println("tasks=====totalIngot================="+task.toString());
        	System.out.println("tasks=====totalIngot================="+task.toString());
        	System.out.println("tasks=====totalIngot================="+task.toString());
        	System.out.println("tasks=====totalIngot================="+task.toString());
        	System.out.println("tasks=====totalIngot================="+task.toString());
        	
        	
        	if (task.sellerNick.equals("xuliubin")) {
                task.sellerNick = "滑板鞋";
            }
            if (task.sellerNick.equals("wocaonimaaa")) {
                task.sellerNick = "可爱又迷人";
            }
            task.isJHS=false;
            if(task.type==TaskType3.JHS){
            	task.isJHS=true;
            }
            task.resolveId();
            BuyerTask3 buyerTask = BuyerTask3.newInstance();
            buyerTask.buyerAccountId = buyerAccountId;
            buyerTask.taskId = task.id;
            buyerTask.device = vo.device;
            //是否过滤不可解任务
            User user = getCurrentUser();
        	if (!isTakeTasks3(buyerTask, task) && user!=null && !isAdminOperate() && user.isClearView==true){
        		it.remove();
        	}
        }
		
		renderPageJson(tasks.items, tasks.totalCount);
	}
	
	
	
	 /**
     * 在任务列表中判断是不是收藏加购
     * 如果是收藏加购应执行到第四步，才可以继续接任务。
     */
   private static boolean isCollection(List<BuyerTaskVo3> lists, long buyerid) {
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
   		BuyerTaskStep3 laststep=BuyerTaskStep3.findBuyerLastStep(v.id, buyerid);
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
	
	
	
    /* 
    * 判断该条任务是否可接
    */
    public static boolean isTakeTasks3(BuyerTask3 bt,Task3 tasks) {
        // 存在未完成的任务【提示：买号异常】
        User buyer = getCurrentUser();
        if(bt.buyerAccountId<=0){
            tasks.tags = false;
            tasks.reason="买号没有绑定";
            return false;
        }
        BuyerAccount3 buyerAccount = BuyerAccount3.findById(bt.buyerAccountId);
        if (!BuyerAccount3.validate(buyerAccount, buyer)) {
            tasks.tags = false;
            tasks.reason="买号异常";
            return false;
        }

        // 该任务已被用户接手过，不能重复领取【提示：已经领取过】
        if (BuyerTask3.isDuplicateTaking(bt.buyerAccountId, bt.taskId)) {
            tasks.tags = false;
            tasks.reason="已经领取过";
            return false;
        }
        
        
        
        
        // 该任务性别不匹配
        if (!BuyerTask3.isSexTaking(bt.buyerAccountId, bt.taskId)) {
            tasks.tags = false;
            tasks.reason="性别不匹配";
            return false;
        }
        // 该任务地区不匹配
        if (!BuyerTask3.isZoneTaking(bt.buyerAccountId, bt.taskId)) {
            tasks.tags = false;
            tasks.reason="地区不匹配";
            return false;
        }
        
        
        
        
        
        //判断是否有没有做完的任务。
        List<BuyerTaskVo3> bto=BuyerTask3.findTodoTasks(buyer.id);
        boolean isConllection=isCollection(bto,buyer.id);
        if(!isConllection){
        	tasks.tags = false;
            tasks.reason="有未完成的任务";
            return false;
        }
        Task3 task =TaskExecutor.getTask3(bt.taskId);
        
        // 任务已全被领取完【提示：任务已领完】
        if (task.isTakenOver(bt.device)) {
            tasks.tags = false;
            tasks.reason="任务已领完";
            return false;
        }
        
        // 父任务数据异常【提示：任务状态有误】
        if (task == null || task.status != TaskStatus.PUBLISHED) {
            tasks.tags = false;
            tasks.reason="任务状态有误";
            return false;
        }

        // 金币不足需要充值购买才能接手任务【提示：金币不足】
        if (buyer.ingot < BizConstants.TASK_TAKING_INGOT) {
            tasks.tags = false;
            tasks.reason="金币不足";
            return false;
        }

        // 平台限制每个【买号】5单/天，30单/周，90单/月
        TaskStats3 taskStats = TaskStats3.findForBuyerUntilNow(bt.buyerAccountId);
        
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
        //【提示：当日已做完】
        if (taskStats.dayCount >= Integer.parseInt(day)) {
            log.warn("BuyerAccount={} has taken {} tasks Today", bt.buyerAccountId, taskStats.dayCount);
            tasks.tags = false;
            tasks.reason="当日任务已做完";
            return false;
        }
        //【提示：本周已做完】
        if (taskStats.weekCount >= Integer.parseInt(week)) {
            log.warn("BuyerAccount={} has taken {} tasks this Week", bt.buyerAccountId, taskStats.weekCount);
            tasks.tags = false;
            tasks.reason="本周任务已做完";
            return false;
        }
        //【提示：本月已做完】
        if (taskStats.monthCount >= Integer.parseInt(mouth)) {
            log.warn("BuyerAccount={} has taken {} tasks this Month", bt.buyerAccountId, taskStats.monthCount);
            tasks.tags = false;
            tasks.reason="本月任务已做完";
            return false;
        }

        bt.buyerId = buyer.id;
        // 账号和商家之间的限制【提示：账号和商家之间的限制】
        if(task.platform==Platform3.TAOBAO||task.platform==Platform3.TMALL){
        	//目前只限制淘宝平台,其他平台不做限制。
	        if (!TaskExecutor.isAvailableBuyerIdAndSellerId(buyer.id, task.sellerId)) {
	            tasks.tags = false;
                tasks.reason="该商家的任务已经领取过";
	            return false;
	        }
        }
        	
        // 账号和店铺之间的限制【提示：账号和店铺之间的限制】
        if (!TaskExecutor.isAvailableBuyerIdAndShopId(buyer.id, task.shopId)) {
        	return false;
        }
        
        System.out.println("============================");
        System.out.println("============================");
        System.out.println("============================");
        System.out.println("============================");
        System.out.println("============================");
        System.out.println("============================");
        
        // 买号和店铺之间的限制【提示：买号和店铺之间的限制】
        if (!TaskExecutor.isValidateBuyerAccountAndShopId3(bt.buyerAccountId, task.shopId)) {
            tasks.tags = false;
            tasks.reason="该店铺的任务已经领取过";
            return false;
        }
        //System.out.println(task.type.toString());
        // 买号和商品之间的限制【提示：买号和商品之间的限制】
        if(StringUtils.isEmpty(task.itemId)){
        	System.out.println(task.id);
        }
        System.out.println("============buyerAccountId================2"+bt.buyerAccountId);
        System.out.println("==========itemId=================="+task.itemId);
        System.out.println("===========type================="+task.type.toString());
        
        if (!TaskExecutor.isAvailableBuyerAccountAndItem3(bt.buyerAccountId, task.itemId,task.type.toString())) {
            tasks.tags = false;
            tasks.reason="该商品的任务已经领取过";
            return false;
        }
        tasks.tags=true;
        return true;
        
    }
	
	 /**
     * 
     * 推广列表->分页获取推广(暂时无分页).
     *
     * @since  0.1
     * @author 尤齐春
     * @created 2016年8-3日 
     */
	public static void listTasks2(@Required TaskSearchVo2 vo, long buyerAccountId) {
	    if (vo != null) {
            validation.min(vo.pageSize, 1);
            validation.max(vo.pageSize, 50);
        }
        handleWrongInput(false);
		if (buyerAccountId <= 0) {
			User buyer = getCurrentUser();
			
			if (buyer != null) {
				BuyerAccountSearchVo2 accountSearchVo = BuyerAccountSearchVo2.newInstance();
				accountSearchVo.userId = buyer.id;
				accountSearchVo.platform=vo.platform;
				accountSearchVo.status=ExamineStatus2.EXAMINED;
				List<BuyerAccount2> buyerAccount = BuyerAccount2.findForTakingTask(accountSearchVo);
				
				
				System.out.println("buyerAccountsize--------------"+buyerAccount.size());
				System.out.println("buyerAccountsize--------------"+buyerAccount.size());
				System.out.println("buyerAccountsize--------------"+buyerAccount.size());
				System.out.println("buyerAccountsize--------------"+buyerAccount.size());
				System.out.println("buyerAccountsize--------------"+buyerAccount.size());
				
				if (CollectionUtils.isNotEmpty(buyerAccount)) {
					buyerAccountId = buyerAccount.get(0).id;
				}
			}
		}
		Page<Task2> tasks = Task2.findBySearchVo(vo.status(TaskStatus.PUBLISHED));
		// 处理id数字过长前端json无法正确转换的问题
        for (Iterator it = tasks.items.iterator(); it.hasNext();) {
        	Task2 task = (Task2)it.next();
        	if (task.sellerNick.equals("xuliubin")) {
                task.sellerNick = "滑板鞋";
            }
            if (task.sellerNick.equals("wocaonimaaa")) {
                task.sellerNick = "可爱又迷人";
            }
            task.resolveId();
            BuyerTask2 buyerTask = BuyerTask2.newInstance();
            buyerTask.buyerAccountId = buyerAccountId;
            buyerTask.taskId = task.id;
            buyerTask.device = vo.device;
            System.out.println("------------------任务状态---------------------"+buyerTask.status);
            System.out.println("------------------任务状态---------------------"+buyerTask.status);
            System.out.println("------------------任务状态---------------------"+buyerTask.status);
            System.out.println("------------------任务状态---------------------"+buyerTask.status);
            System.out.println("------------------任务状态---------------------"+buyerTask.status);
            //是否过滤不可解任务
            User user = getCurrentUser();
            System.out.println("------------------"+isTakeTasks2(buyerTask, task));
        	if (!isTakeTasks2(buyerTask, task) && user!=null && !isAdminOperate() && user.isClearView==true){
        		it.remove();
        	}
        }
		renderPageJson(tasks.items, tasks.totalCount);
	}
	
	
	
	
	
	
	
	/**
	    * 
	    * 判断该条任务是否可接
	    * @return
	    * @since  v2.0
	    * @author fufei
	    * @created 2015年1月23日 下午5:06:31
	    */
	    public static boolean isTakeTasks(BuyerTask bt,Task tasks) {
	        /*
	         * 检查是否可接手任务
	         */
	        // 存在未完成的任务【提示：任务进行中】
	        if (session.contains(BizConstants.BUYER_TASK_ID)) {
	            if (BuyerTask2.findById(Long.parseLong(session.get(BizConstants.BUYER_TASK_ID))).status == TaskStatus.CANCLED) {
	                session.remove(BizConstants.BUYER_TASK_ID);
	            } else {
	                tasks.tags = false;
	                tasks.reason="存在未完成的任务";
	                return false;
	            }
	        }

	        // 存在未完成的任务【提示：买号异常】
	        User buyer = getCurrentUser();
	        if(bt.buyerAccountId<=0){
	            tasks.tags = false;
                tasks.reason="买号没有绑定";
                return false;
	        }
	        BuyerAccount buyerAccount = BuyerAccount.findById(bt.buyerAccountId);
	        if (!BuyerAccount.validate(buyerAccount, buyer)) {
	            tasks.tags = false;
                tasks.reason="买号异常";
	            return false;
	        }

	        // 该任务已被用户接手过，不能重复领取【提示：已经领取过】
	        if (BuyerTask.isDuplicateTaking(bt.buyerAccountId, bt.taskId)) {
	            tasks.tags = false;
                tasks.reason="已经领取过";
	            return false;
	        }
	        // 存在尚未完成的任务，不能领取新任务【提示：任务进行中】
	        BuyerTask todoTask = BuyerTask.findTodoTask(buyer.id);
	        if (todoTask != null) {
	            tasks.tags = false;
                tasks.reason="存在尚未完成的任务";
	            return false;
	        }
	        Task task =TaskExecutor.getTask(bt.taskId);
	        
	        // 任务已全被领取完【提示：任务已领完】
	        if (task.isTakenOver(bt.device)) {
	            tasks.tags = false;
                tasks.reason="任务已领完";
	            return false;
	        }
	        
	        // 父任务数据异常【提示：任务状态有误】
	        if (task == null || task.status != TaskStatus.PUBLISHED) {
	            tasks.tags = false;
                tasks.reason="任务状态有误";
	            return false;
	        }

	        // 领取非“平台返款”的任务需要绑定【财付通】作为退款账号【提示：未绑定财付通】
	        if (BooleanUtils.isFalse(task.sysRefund)) {
	            FundAccount tenpayAccount = FundAccount.findByType(PayPlatform.TENPAY, buyer.id);
	            if (tenpayAccount == null) {
	                tasks.tags = false;
	                tasks.reason="未绑定财付通";
	                return false;
	            }
	        }

	        // 金币不足需要充值购买才能接手任务【提示：金币不足】
	        if (buyer.ingot < BizConstants.TASK_TAKING_INGOT) {
	            tasks.tags = false;
                tasks.reason="金币不足";
	            return false;
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
	        
	        //【提示：当日已做完】
	        if (taskStats.dayCount >= Integer.parseInt(day)) {
	            log.warn("BuyerAccount={} has taken {} tasks Today", bt.buyerAccountId, taskStats.dayCount);
	            tasks.tags = false;
                tasks.reason="当日任务已做完";
	            return false;
	        }
	        //【提示：本周已做完】
	        if (taskStats.weekCount >= Integer.parseInt(week)) {
	            log.warn("BuyerAccount={} has taken {} tasks this Week", bt.buyerAccountId, taskStats.weekCount);
	            tasks.tags = false;
                tasks.reason="本周任务已做完";
	            return false;
	        }
	        //【提示：本月已做完】
	        if (taskStats.monthCount >= Integer.parseInt(mouth)) {
	            log.warn("BuyerAccount={} has taken {} tasks this Month", bt.buyerAccountId, taskStats.monthCount);
	            tasks.tags = false;
                tasks.reason="本月任务已做完";
	            return false;
	        }

	        bt.buyerId = buyer.id;
	        // 账号和商家之间的限制【提示：账号和商家之间的限制】
	        if(task.platform==Platform.TAOBAO||task.platform==Platform.TMALL){
	        	//目前只限制淘宝平台,其他平台不做限制。
		        if (!TaskExecutor.isAvailableBuyerIdAndSellerId(buyer.id, task.sellerId)) {
		            tasks.tags = false;
	                tasks.reason="该商家的任务已经领取过";
		            return false;
		        }
	        }
	        	
	        // 账号和店铺之间的限制【提示：账号和店铺之间的限制】
//	        if (!TaskExecutor.isAvailableBuyerIdAndShopId(buyer.id, task.shopId)) {
//	        	return false;
//	        }
	        // 买号和店铺之间的限制【提示：买号和店铺之间的限制】
	        if (!TaskExecutor.isValidateBuyerAccountAndShopId(bt.buyerAccountId, task.shopId)) {
	            tasks.tags = false;
                tasks.reason="该店铺的任务已经领取过";
	            return false;
	        }
	        // 买号和商品之间的限制【提示：买号和商品之间的限制】
	        if (!TaskExecutor.isAvailableBuyerAccountAndItem(bt.buyerAccountId, task.itemId)) {
	            tasks.tags = false;
                tasks.reason="该商品的任务已经领取过";
	            return false;
	        }
	        tasks.tags=true;
	        return true;
	    }
	
	    
	    
	    /**
		    * 
		    * 判断该条推广是否可接
		    * @return
		    * @since  v2.0
		    * @author 尤齐春
		    * @created 2016年8月3日 下午
		    */
		    public static boolean isTakeTasks2(BuyerTask2 bt,Task2 tasks) {
		        /*
		         * 检查是否可接手任务
		         */
		        // 存在未完成的任务【提示：任务进行中】
		        if (session.contains(BizConstants.BUYER_TASK_ID2)) {
		            if (BuyerTask2.findById(Long.parseLong(session.get(BizConstants.BUYER_TASK_ID2))).status == TaskStatus.CANCLED) {
		                session.remove(BizConstants.BUYER_TASK_ID2);
		            } else {
		                tasks.tags = false;
		                tasks.reason="存在未完成的任务";
		                return false;
		            }
		        }

		        // 存在未完成的任务【提示：买号异常】
		        User buyer = getCurrentUser();
		        if(bt.buyerAccountId<=0){
		            tasks.tags = false;
	                tasks.reason="买号没有绑定";
	                return false;
		        }
		        BuyerAccount2 buyerAccount = BuyerAccount2.findById(bt.buyerAccountId);
		        if (!BuyerAccount2.validate(buyerAccount, buyer)) {
		            tasks.tags = false;
	                tasks.reason="买号异常";
		            return false;
		        }

		        // 该任务已被用户接手过，不能重复领取【提示：已经领取过】
		        if (BuyerTask2.isDuplicateTaking(bt.buyerAccountId, bt.taskId)) {
		            tasks.tags = false;
	                tasks.reason="已经领取过";
		            return false;
		        }
		        // 存在尚未完成的任务，不能领取新任务【提示：任务进行中】
		        BuyerTask2 todoTask = BuyerTask2.findTodoTask(buyer.id);
		        if (todoTask != null) {
		            tasks.tags = false;
		            System.out.println("存在未完成的任务");
		            System.out.println("存在未完成的任务");
		            System.out.println("存在未完成的任务");
	                tasks.reason="存在尚未完成的任务";
		            return false;
		        }
		        Task2 task =TaskExecutor.getTask2(bt.taskId);
		        
		        // 任务已全被领取完【提示：任务已领完】
		        if (task.isTakenOver(bt.device)) {
		            tasks.tags = false;
	                tasks.reason="任务已领完";
		            return false;
		        }
		        
		        // 父任务数据异常【提示：任务状态有误】
		        if (task == null || task.status != TaskStatus.PUBLISHED) {
		            tasks.tags = false;
	                tasks.reason="任务状态有误";
		            return false;
		        }

		        // 领取非“平台返款”的任务需要绑定【财付通】作为退款账号【提示：未绑定财付通】
		        if (BooleanUtils.isFalse(task.sysRefund)) {
		            FundAccount tenpayAccount = FundAccount.findByType(PayPlatform.TENPAY, buyer.id);
		            if (tenpayAccount == null) {
		                tasks.tags = false;
		                tasks.reason="未绑定财付通";
		                return false;
		            }
		        }

		        // 金币不足需要充值购买才能接手任务【提示：金币不足】
		        if (buyer.ingot < BizConstants.TASK_TAKING_INGOT) {
		            tasks.tags = false;
	                tasks.reason="金币不足";
		            return false;
		        }

		        // 平台限制每个【买号】5单/天，30单/周，90单/月
		        TaskStats taskStats = TaskStats.findForBuyerUntilNow(bt.buyerAccountId);
		        
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
		        
		        //【提示：当日已做完】
		        if (taskStats.dayCount >= Integer.parseInt(day)) {
		            log.warn("BuyerAccount={} has taken {} tasks Today", bt.buyerAccountId, taskStats.dayCount);
		            tasks.tags = false;
	                tasks.reason="当日任务已做完";
		            return false;
		        }
		        //【提示：本周已做完】
		        if (taskStats.weekCount >= Integer.parseInt(week)) {
		            log.warn("BuyerAccount={} has taken {} tasks this Week", bt.buyerAccountId, taskStats.weekCount);
		            tasks.tags = false;
	                tasks.reason="本周任务已做完";
		            return false;
		        }
		        //【提示：本月已做完】
		        if (taskStats.monthCount >= Integer.parseInt(mouth)) {
		            log.warn("BuyerAccount={} has taken {} tasks this Month", bt.buyerAccountId, taskStats.monthCount);
		            tasks.tags = false;
	                tasks.reason="本月任务已做完";
		            return false;
		        }

		        bt.buyerId = buyer.id;
		        // 账号和商家之间的限制【提示：账号和商家之间的限制】
		       /* if(task.platform==Platform2.TAOBAO||task.platform==Platform.TMALL){
		        	//目前只限制淘宝平台,其他平台不做限制。
			        if (!TaskExecutor.isAvailableBuyerIdAndSellerId(buyer.id, task.sellerId)) {
			            tasks.tags = false;
		                tasks.reason="该商家的任务已经领取过";
			            return false;
			        }
		        }*/
		        	
		        // 买号和店铺之间的限制【提示：买号和店铺之间的限制】
		        if (!TaskExecutor.isValidateBuyerAccountAndShopId2(bt.buyerAccountId, task.shopId)) {
		            tasks.tags = false;
	                tasks.reason="该店铺的任务已经领取过";
		            return false;
		        }
		        // 买号和商品之间的限制【提示：买号和商品之间的限制】
		        /*if (!TaskExecutor.isAvailableBuyerAccountAndItem(bt.buyerAccountId, task.itemId)) {
		            tasks.tags = false;
	                tasks.reason="该商品的任务已经领取过";
		            return false;
		        }*/
		        tasks.tags=true;
		        return true;
		    }
		
	    
	    
	    
	    
	    
	    
	    
	    
	    
	    
	    
	    
   /**
    * 
    * 判断该条任务是否可接
    * @return
    * @since  v2.0
    * @author fufei
    * @created 2015年1月23日 下午5:06:31
    */
    public static boolean isTakeTask(BuyerTask bt) {
    	/*
		 * 检查是否可接手任务
		 */
		// 存在未完成的任务【提示：任务进行中】
		if (session.contains(BizConstants.BUYER_TASK_ID)) {
			if (BuyerTask2.findById(Long.parseLong(session.get(BizConstants.BUYER_TASK_ID))).status == TaskStatus.CANCLED) {
				session.remove(BizConstants.BUYER_TASK_ID);
			} else {
				return false;
			}
		}

		// 存在未完成的任务【提示：买号异常】
		User buyer = getCurrentUser();
		BuyerAccount2 buyerAccount = BuyerAccount2.findById(bt.buyerAccountId);
		if (!BuyerAccount2.validate(buyerAccount, buyer)) {
			return false;
		}

		// 该任务已被用户接手过，不能重复领取【提示：已经领取过】
		if (BuyerTask2.isDuplicateTaking(bt.buyerAccountId, bt.taskId)) {
			return false;
		}
		// 存在尚未完成的任务，不能领取新任务【提示：任务进行中】
		BuyerTask2 todoTask = BuyerTask2.findTodoTask(buyer.id);
		if (todoTask != null) {
			return false;
		}
		Task2 task =TaskExecutor.getTask2(bt.taskId);
		// 父任务数据异常【提示：任务状态有误】
		if (task == null || task.status != TaskStatus.PUBLISHED) {
			return false;
		}

		// 领取非“平台返款”的任务需要绑定【财付通】作为退款账号【提示：未绑定财付通】
		if (BooleanUtils.isFalse(task.sysRefund)) {
			FundAccount tenpayAccount = FundAccount.findByType(PayPlatform.TENPAY, buyer.id);
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
        
        //【提示：当日已做完】
		if (taskStats.dayCount >= Integer.parseInt(day)) {
			log.warn("BuyerAccount={} has taken {} tasks Today", bt.buyerAccountId, taskStats.dayCount);
			return false;
		}
		//【提示：本周已做完】
		if (taskStats.weekCount >= Integer.parseInt(week)) {
			log.warn("BuyerAccount={} has taken {} tasks this Week", bt.buyerAccountId, taskStats.weekCount);
			return false;
		}
		//【提示：本月已做完】
		if (taskStats.monthCount >= Integer.parseInt(mouth)) {
			log.warn("BuyerAccount={} has taken {} tasks this Month", bt.buyerAccountId, taskStats.monthCount);
			return false;
		}

		bt.buyerId = buyer.id;
		// 账号和商家之间的限制【提示：账号和商家之间的限制】
	/*	if(task.platform==Platform.TAOBAO||task.platform==Platform.TMALL)//目前只限制淘宝平台的
		if (!TaskExecutor.isAvailableBuyerIdAndSellerId(buyer.id, task.sellerId)) {
			return false;
		}*/
		// 买号和店铺之间的限制【提示：买号和店铺之间的限制】
		if (!TaskExecutor.isValidateBuyerAccountAndShopId(bt.buyerAccountId, task.shopId)) {
			return false;
		}
		// 买号和商品之间的限制【提示：买号和商品之间的限制】
		if (!TaskExecutor.isAvailableBuyerAccountAndItem(bt.buyerAccountId, task.itemId)) {
			return false;
		}
		return true;
	}
    
    
    
    
    
    public static boolean isTakeTask2(BuyerTask2 bt) {
    	/*
		 * 检查是否可接手任务
		 */
		// 存在未完成的任务【提示：任务进行中】
		if (session.contains(BizConstants.BUYER_TASK_ID2)) {
			if (BuyerTask2.findById(Long.parseLong(session.get(BizConstants.BUYER_TASK_ID2))).status == TaskStatus.CANCLED) {
				session.remove(BizConstants.BUYER_TASK_ID2);
			} else {
				return false;
			}
		}

		// 存在未完成的任务【提示：买号异常】
		User buyer = getCurrentUser();
		BuyerAccount2 buyerAccount = BuyerAccount2.findById(bt.buyerAccountId);
		if (!BuyerAccount2.validate(buyerAccount, buyer)) {
			return false;
		}

		// 该任务已被用户接手过，不能重复领取【提示：已经领取过】
		if (BuyerTask2.isDuplicateTaking(bt.buyerAccountId, bt.taskId)) {
			return false;
		}
		// 存在尚未完成的任务，不能领取新任务【提示：任务进行中】
		BuyerTask2 todoTask = BuyerTask2.findTodoTask(buyer.id);
		if (todoTask != null) {
			return false;
		}
		Task2 task =TaskExecutor.getTask2(bt.taskId);
		// 父任务数据异常【提示：任务状态有误】
		if (task == null || task.status != TaskStatus.PUBLISHED) {
			return false;
		}

		// 领取非“平台返款”的任务需要绑定【财付通】作为退款账号【提示：未绑定财付通】
		if (BooleanUtils.isFalse(task.sysRefund)) {
			FundAccount tenpayAccount = FundAccount.findByType(PayPlatform.TENPAY, buyer.id);
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

		// 平台限制每个【买号】30单/天，210单/周，300单/月
		TaskStats taskStats = TaskStats.findForBuyerUntilNow(bt.buyerAccountId);
		
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
        
        //【提示：当日已做完】
		if (taskStats.dayCount >= Integer.parseInt(day)) {
			log.warn("BuyerAccount={} has taken {} tasks Today", bt.buyerAccountId, taskStats.dayCount);
			return false;
		}
		//【提示：本周已做完】
		if (taskStats.weekCount >= Integer.parseInt(week)) {
			log.warn("BuyerAccount={} has taken {} tasks this Week", bt.buyerAccountId, taskStats.weekCount);
			return false;
		}
		//【提示：本月已做完】
		if (taskStats.monthCount >= Integer.parseInt(mouth)) {
			log.warn("BuyerAccount={} has taken {} tasks this Month", bt.buyerAccountId, taskStats.monthCount);
			return false;
		}

		bt.buyerId = buyer.id;
		// 账号和商家之间的限制【提示：账号和商家之间的限制】
	/*	if(task.platform==Platform.TAOBAO||task.platform==Platform.TMALL)//目前只限制淘宝平台的
		if (!TaskExecutor.isAvailableBuyerIdAndSellerId(buyer.id, task.sellerId)) {
			return false;
		}*/
		// 买号和店铺之间的限制【提示：买号和店铺之间的限制】
		if (!TaskExecutor.isValidateBuyerAccountAndShopId(bt.buyerAccountId, task.shopId)) {
			return false;
		}
		// 买号和商品之间的限制【提示：买号和商品之间的限制】
		if (!TaskExecutor.isAvailableBuyerAccountAndItem(bt.buyerAccountId, task.itemId)) {
			return false;
		}
		return true;
	}
    
    
    
    
    
    
    /**
     * 卖家->我的任务
     * 
     *
     * @since  v0.1
     * @author tr0j4n
     * @created 2014-7-31 下午5:05:43
     */
    public static void sellerTasks() {
        renderArgs.put(BizConstants.PLATFORMS, Platform.values());
        renderArgs.put("sellerStatusList", TaskStatus.SELLER_STATUS_LIST);
        
        User seller = getCurrentUser();
        Multimap<String, TaskCountVo> map = Task.findWaitingTaskCountInfo(seller.id);
        renderArgs.put("waitingTaskInfoMap", map);
        
        // 统计平台退款的任务数
        renderArgs.put("waitSysRefund", BuyerTask.findSysRefundCount(seller));
        render();
    }
    
    
    /**
     * 卖家->我的浏览
     * 
     *
     * @since  v0.1
     * @author tr0j4n
     * @created 2014-7-31 下午5:05:43
     */
    public static void sellerTasks3() {
        renderArgs.put(BizConstants.PLATFORMS, Platform3.values());
        renderArgs.put("sellerStatusList", TaskStatus.SELLER_STATUS_LIST);
        
        User seller = getCurrentUser();
        Multimap<String, TaskCountVo3> map = Task3.findWaitingTaskCountInfo(seller.id);
        renderArgs.put("waitingTaskInfoMap", map);
        
        // 统计平台退款的任务数
        renderArgs.put("waitSysRefund", BuyerTask3.findSysRefundCount(seller));
        render();
    }
    
    /**
     * 卖家->我的推广
     */
    public static void sellerTasks2() {
        renderArgs.put(BizConstants.PLATFORMS, Platform2.values());
        renderArgs.put("sellerStatusList", TaskStatus.SELLER_STATUS_LIST);
        
        User seller = getCurrentUser();
        Multimap<String, TaskCountVo2> map = Task2.findWaitingTaskCountInfo(seller.id);
        renderArgs.put("waitingTaskInfoMap", map);
        
        // 统计平台退款的任务数
        renderArgs.put("waitSysRefund", BuyerTask2.findSysRefundCount(seller));
        render();
    }
    
    
	/**
	 * 
	 * （卖家）我的任务->分页获取任务列表.
	 */
    public static void listSellerTasks(@Required TaskSearchVo vo,String type) {
        if (vo != null) {
            validation.range("pageNo", vo.pageNo, 1, 10);
            validation.range("pageSize", vo.pageNo, 1, 100);
        }
        handleWrongInput(false);

        vo.sellerId(getCurrentUser().id);
        Page<SellerTaskVo> p = null;
       
        if("FINISHED".equals(StringUtils.trim(type))){
        	p = Task.findVoByPage(vo.status(TaskStatus.FINISHED));
        }else if("CANCEL".equals(StringUtils.trim(type))){
        	p = Task.findVoByPage(vo.status(TaskStatus.CANCLED));
        }else if ("ALL".equals(StringUtils.trim(type))) {
        	p = Task.findVoByPage(vo);
		}else {
			if(vo.status==null){
				//进行中
				p = Task.findProcessTaskByPage(vo);
			}else {
				p = Task.findVoByPage(vo);
			}
		}
        renderPageJson(p.items, p.totalCount);
    }
    
    
    /**
	 * 
	 * （卖家）我的任务->分页获取任务列表.
	 */
    public static void listSellerTasks3(@Required TaskSearchVo3 vo,String type) {
        if (vo != null) {
            validation.range("pageNo", vo.pageNo, 1, 10);
            validation.range("pageSize", vo.pageNo, 1, 100);
        }
        handleWrongInput(false);

        vo.sellerId(getCurrentUser().id);
        Page<SellerTaskVo3> p = null;
       
        if("FINISHED".equals(StringUtils.trim(type))){
        	p = Task3.findVoByPage(vo.status(TaskStatus.FINISHED));
        }else if("CANCEL".equals(StringUtils.trim(type))){
        	p = Task3.findVoByPage(vo.status(TaskStatus.CANCLED));
        }else if ("ALL".equals(StringUtils.trim(type))) {
        	p = Task3.findVoByPage(vo);
		}else {
			if(vo.status==null){
				//进行中
				p = Task3.findProcessTaskByPage(vo);
			}else {
				p = Task3.findVoByPage(vo);
			}
		}
        renderPageJson(p.items, p.totalCount);
    }
    
    
    
    /**
	 * 
	 * （卖家）我的任务->分页获取任务列表.
	 * 
	 * @since 0.1
	 * @author youblade
	 * @created 2014年8月2日 下午6:00:15
	 */
    public static void listSellerTasks2(@Required TaskSearchVo2 vo,String type) {
        if (vo != null) {
            validation.range("pageNo", vo.pageNo, 1, 10);
            validation.range("pageSize", vo.pageNo, 1, 100);
        }
        handleWrongInput(false);
        
        vo.sellerId(getCurrentUser().id);
        Page<SellerTaskVo2> p = null;
       
        if("FINISHED".equals(StringUtils.trim(type))){
        	p = Task2.findVoByPage(vo.status(TaskStatus.FINISHED));
        }else if("CANCEL".equals(StringUtils.trim(type))){
        	p = Task2.findVoByPage(vo.status(TaskStatus.CANCLED));
        }else if ("ALL".equals(StringUtils.trim(type))) {
        	p = Task2.findVoByPage(vo);
		}else {
			
			if(vo.status==null){
				//进行中
				 System.out.println("（卖家）我的任务->分页获取任务列表.");
			    	System.out.println("（卖家）我的任务->分页获取任务列表.");
			    	System.out.println("（卖家）我的任务->分页获取任务列表.");
			    	System.out.println("（卖家）我的任务->分页获取任务列表.");
			    	System.out.println("（卖家）我的任务->分页获取任务列表.");
			    	System.out.println("（卖家）我的任务->分页获取任务列表.");
				p = Task2.findProcessTaskByPage(vo);
				System.out.println("--------"+p.totalCount);
				System.out.println("--------"+p.totalCount);
				System.out.println("--------"+p.totalCount);
				System.out.println("--------"+p.totalCount);
				System.out.println("--------"+p.totalCount);
				System.out.println("--------"+p.totalCount);
			}else {
					System.out.println("（卖家）我的任务->分页获取任务列表.2");
			    	System.out.println("（卖家）我的任务->分页获取任务列表.2");
			    	System.out.println("（卖家）我的任务->分页获取任务列表.2");
			    	System.out.println("（卖家）我的任务->分页获取任务列表.2");
			    	System.out.println("（卖家）我的任务->分页获取任务列表.2");
			    	System.out.println("（卖家）我的任务->分页获取任务列表.2");
				p = Task2.findVoByPage(vo);
			}
		}
        renderPageJson(p.items, p.totalCount);
    }
    
    
    
    
    
    
    
    
    
    
    
    /**
     * 
     * 买家->我的任务
     */
    public static void buyerTasks(){
        User buyer = getCurrentUser();
        Multimap<String, TaskCountVo> map = BuyerTask.findWaitingTaskCountInfo(buyer.id);
        renderArgs.put("waitingTaskInfoMap", map);
        
        // 获取买手【待核实退款】中“平台返款”任务的总数
        renderArgs.put("waitSysRefund", BuyerTask.findSysRefundCount(buyer));
        renderArgs.put("Platforms", Platform.values());
    	render();
    }
    
    
    
    /**
     * 
     * 买家->我的浏览
     */
    public static void buyerTasks3(){
        User buyer = getCurrentUser();
        Multimap<String, TaskCountVo3> map = BuyerTask3.findWaitingTaskCountInfo(buyer.id);
        renderArgs.put("waitingTaskInfoMap", map);
        
        // 获取买手【待核实退款】中“平台返款”任务的总数
        renderArgs.put("waitSysRefund", BuyerTask3.findSysRefundCount(buyer));
        renderArgs.put("Platforms", Platform3.values());
    	render();
    }
    
    
    
    /**
     * 
     * 买家->我的推广
     *
     * @since  v0.1
     * @author 尤齐春
     * @created 2016-8-9 
     */
    public static void buyerTasks2(){
    	 User buyer = getCurrentUser();
        Multimap<String, TaskCountVo2> map = BuyerTask2.findWaitingTaskCountInfo(buyer.id);
        renderArgs.put("waitingTaskInfoMap", map);
        
        // 获取买手【待核实退款】中“平台返款”任务的总数
        renderArgs.put("waitSysRefund", BuyerTask2.findSysRefundCount(buyer));
        renderArgs.put("Platforms", Platform2.values());
    	render();
    }
    
    /**
     * 
     * 获取已付款的各个状态下买手的任务数
     *
     * @param vo
     * @since  v2.0
     * @author Mark Xu
     * @created 2015-2-7 上午11:08:36
     */
    public static void buyerTaskCounts(@Required TaskSearchVo vo) {
    	if (vo != null) {
            vo.orderId = StringUtils.trimToNull(vo.orderId);
            vo.buyerAccountNick = StringUtils.trimToNull(vo.buyerAccountNick);
        }
    	handleWrongInput(false);
    	User buyer = getCurrentUser();
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
        renderJson(map);
    }
    
    
    
    /**
     * 
     * （买手）我的浏览->分页获取任务列表.
     */
    public static void listBuyerTasks3(@Required TaskSearchVo3 vo,String type) {
        if (vo != null) {
            vo.orderId = StringUtils.trimToNull(vo.orderId);
            vo.buyerAccountNick = StringUtils.trimToNull(vo.buyerAccountNick);
        }
        handleWrongInput(false);

        User buyer = getCurrentUser();
        Page<BuyerTaskVo3> p = null;
        if("FINISHED".equals(StringUtils.trim(type))){
        	p = BuyerTask3.findListByPage(vo.buyerId(buyer.id).status(TaskStatus._FINISHED));
        }else if("CANCEL".equals(StringUtils.trim(type))){
        	p = BuyerTask3.findListByPage(vo.buyerId(buyer.id).status(TaskStatus.CANCLED));
        }else if ("ALL".equals(StringUtils.trim(type))) {
        	p = BuyerTask3.findListByPage(vo.buyerId(buyer.id));
		}else {
			if(vo.status==null){
				//进行中
				p = BuyerTask3.findListByPage(vo.buyerId(buyer.id).statusExclude(TaskStatus.CANCLED,TaskStatus.FINISHED));
				
			}else {
				p = BuyerTask3.findListByPage(vo.buyerId(buyer.id));
			}
		}
        for (BuyerTaskVo3 taskVo : p.items) {
            taskVo.subShopNamePrefix();
        }
        renderPageJson(p.items, p.totalCount);
    }
    
    
    
    /**
     * 
     * （买手）我的任务->分页获取任务列表.
     *
     * @since  0.1
     * @author youblade
     * @created 2014年8月2日 下午6:00:15
     */
    public static void listBuyerTasks(@Required TaskSearchVo vo,String type) {
    	
        if (vo != null) {
            vo.orderId = StringUtils.trimToNull(vo.orderId);
            vo.buyerAccountNick = StringUtils.trimToNull(vo.buyerAccountNick);
        }
        handleWrongInput(false);

        User buyer = getCurrentUser();
        Page<BuyerTaskVo> p = null;
        if("FINISHED".equals(StringUtils.trim(type))){
        	p = BuyerTask.findListByPage(vo.buyerId(buyer.id).status(TaskStatus._FINISHED));
        }else if("CANCEL".equals(StringUtils.trim(type))){
        	p = BuyerTask.findListByPage(vo.buyerId(buyer.id).status(TaskStatus.CANCLED));
        }else if ("ALL".equals(StringUtils.trim(type))) {
        	p = BuyerTask.findListByPage(vo.buyerId(buyer.id));
		}else {
			if(vo.status==null){
				//进行中
				p = BuyerTask.findListByPage(vo.buyerId(buyer.id).statusExclude(TaskStatus.CANCLED,TaskStatus.FINISHED));
				
			}else {
				p = BuyerTask.findListByPage(vo.buyerId(buyer.id));
			}
		}
        for (BuyerTaskVo taskVo : p.items) {
            taskVo.subShopNamePrefix();
        }
        renderPageJson(p.items, p.totalCount);
    }
    
    
    /**
     * 
     * （买手）我的tuiguang ->分页获取推广列表.
     *
     * @since  0.1
     * @author 尤齐春
     * @created 2016年8月12日 下午6:00:15
     */
    public static void listBuyerTasks2(@Required TaskSearchVo2 vo,String type) {
    	
        if (vo != null) {
            vo.orderId = StringUtils.trimToNull(vo.orderId);
            vo.buyerAccountNick = StringUtils.trimToNull(vo.buyerAccountNick);
        }
        
        handleWrongInput(false);

  User buyer = getCurrentUser();
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
        
        System.out.println("-----"+ p.items);
        for (BuyerTaskVo2 taskVo : p.items) {
            taskVo.subShopNamePrefix();
            
            System.out.println("====="+taskVo.toString());
        }
        
        renderPageJson(p.items, p.totalCount);
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
    	renderJSON(map);
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
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    /**
     * 
     * 我的浏览->卖家任务详细.
     */
    public static void taskDetail3(@Required Long id) {
        handleWrongInput(false);

        Task3 task = Task3.findById(id);
        notFoundIfNull(task);
        
        // 管理员访问该方法时不进行任务所属用户的校验
        if (!Secure.isValidAdmin() && task.isNotBelongTo(getCurrentUser())) {
            notFound();
        }

        // 统计已完成的子任务数量
        SqlSession ss = SessionFactory.getSqlSession();
        try {
            BuyerTaskMapper3 mapper = ss.getMapper(BuyerTaskMapper3.class);
            List<TaskCountVo3> list = mapper.countFinished(id);
            Integer pcFinishedCount = 0, mobileFinishedCount = 0;
            for (TaskCountVo3 vo : list) {
                if(vo.device == Device.PC){
                    pcFinishedCount = vo.count;
                    continue;
                }
                mobileFinishedCount = vo.count;
            }
            renderArgs.put("pcFinishedCount", (pcFinishedCount != null) ? pcFinishedCount : 0);
            renderArgs.put("mobileFinishedCount", (mobileFinishedCount != null) ? mobileFinishedCount : 0);
            String publishTime = "请完成支付过程并等待管理员审核完成";
            //任务已审核完成
            if(task.publishTime != null && task.status == TaskStatus.WAIT_PUBLISH) {
            	//定时任务
            	Calendar publishTime1 = Calendar.getInstance();
            	if(task.publishTimerInterval !=0) {
            		if(task.lastBatchPublishTime == null && task.delaySpan != 0) {
            			Date date = task.publishTime;
            			publishTime1.setTime(date);
            			publishTime1.add(Calendar.MINUTE, task.delaySpan);
            		}else {
            			//定时，非延时任务，未被接的状态
            			if(task.lastBatchPublishTime == null) {
            				Date date = task.publishTime;
                			publishTime1.setTime(date); 
            			}else {
            				Date date = task.lastBatchPublishTime;
            				publishTime1 = Calendar.getInstance();
            				publishTime1.setTime(date);
            				publishTime1.add(Calendar.MINUTE, task.publishTimerInterval);
            			}
            		}
            	}
            	//非定时，延时任务
            	else if(task.publishTimerInterval == 0 && task.delaySpan != 0) {
            		Date date = task.publishTime;
        			publishTime1.setTime(date); 
        			publishTime1.add(Calendar.MINUTE, task.delaySpan);
            	}
            	SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            	publishTime = sdf.format(publishTime1.getTime());
                renderArgs.put("publishTime", publishTime);
            }
        }finally {
            ss.close();
        }
        renderArgs.put("task", task);
        render();
    }
    
    
    
    
    
    /**
     * 
     * 我的任务->卖家任务详细.
     *
     * @since  0.1
     * @author youblade
     * @created 2014年7月25日 下午5:10:44
     */
    public static void taskDetail(@Required Long id) {
        handleWrongInput(false);

        Task task = Task.findById(id);
        notFoundIfNull(task);
        
        // 管理员访问该方法时不进行任务所属用户的校验
        if (!Secure.isValidAdmin() && task.isNotBelongTo(getCurrentUser())) {
            notFound();
        }

        // 统计已完成的子任务数量
        SqlSession ss = SessionFactory.getSqlSession();
        try {
            BuyerTaskMapper mapper = ss.getMapper(BuyerTaskMapper.class);
            List<TaskCountVo> list = mapper.countFinished(id);
            Integer pcFinishedCount = 0, mobileFinishedCount = 0;
            for (TaskCountVo vo : list) {
                if(vo.device == Device.PC){
                    pcFinishedCount = vo.count;
                    continue;
                }
                mobileFinishedCount = vo.count;
            }
            renderArgs.put("pcFinishedCount", (pcFinishedCount != null) ? pcFinishedCount : 0);
            renderArgs.put("mobileFinishedCount", (mobileFinishedCount != null) ? mobileFinishedCount : 0);
            String publishTime = "请完成支付过程并等待管理员审核完成";
            //任务已审核完成
            if(task.publishTime != null && task.status == TaskStatus.WAIT_PUBLISH) {
            	//定时任务
            	Calendar publishTime1 = Calendar.getInstance();
            	if(task.publishTimerInterval !=0) {
            		if(task.lastBatchPublishTime == null && task.delaySpan != 0) {
            			Date date = task.publishTime;
            			publishTime1.setTime(date);
            			publishTime1.add(Calendar.MINUTE, task.delaySpan);
            		}else {
            			//定时，非延时任务，未被接的状态
            			if(task.lastBatchPublishTime == null) {
            				Date date = task.publishTime;
                			publishTime1.setTime(date); 
            			}else {
            				Date date = task.lastBatchPublishTime;
            				publishTime1 = Calendar.getInstance();
            				publishTime1.setTime(date);
            				publishTime1.add(Calendar.MINUTE, task.publishTimerInterval);
            			}
            		}
            	}
            	//非定时，延时任务
            	else if(task.publishTimerInterval == 0 && task.delaySpan != 0) {
            		Date date = task.publishTime;
        			publishTime1.setTime(date); 
        			publishTime1.add(Calendar.MINUTE, task.delaySpan);
            	}
            	SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            	publishTime = sdf.format(publishTime1.getTime());
                renderArgs.put("publishTime", publishTime);
            }
        }finally {
            ss.close();
        }
        renderArgs.put("task", task);
        render();
    }
    
    
    
    
    /**
     * 
     * 我的推广->卖家退工详细.
     *
     * @since  0.1
     * @author youblade
     * @created 2014年7月25日 下午5:10:44
     */
    public static void taskDetail2(@Required Long id) {
        handleWrongInput(false);
        
        System.out.println("找到id---"+id);
        System.out.println("找到id---"+id);
        System.out.println("找到id---"+id);
        System.out.println("找到id---"+id);
        System.out.println("找到id---"+id);
        Task2 task = Task2.findById(id);
        notFoundIfNull(task);
        System.out.println("=====================");
        System.out.println("=====================");
        System.out.println("====================="+task);
        System.out.println("=====================");
        System.out.println("=====================");
        // 管理员访问该方法时不进行任务所属用户的校验
        if (!Secure.isValidAdmin() && task.isNotBelongTo(getCurrentUser())) {
            notFound();
        }

        // 统计已完成的子任务数量
        
        SqlSession ss = SessionFactory.getSqlSession();
        try {
            BuyerTaskMapper2 mapper = ss.getMapper(BuyerTaskMapper2.class);
            List<TaskCountVo2> list = mapper.countFinished(id);
            Integer pcFinishedCount = 0, mobileFinishedCount = 0;
            for (TaskCountVo2 vo : list) {
                if(vo.device == Device.PC){
                    pcFinishedCount = vo.count;
                    continue;
                }
                mobileFinishedCount = vo.count;
            }
            renderArgs.put("pcFinishedCount", (pcFinishedCount != null) ? pcFinishedCount : 0);
            renderArgs.put("mobileFinishedCount", (mobileFinishedCount != null) ? mobileFinishedCount : 0);
            String publishTime = "请完成支付过程并等待管理员审核完成";
            //任务已审核完成
            if(task.publishTime != null && task.status == TaskStatus.WAIT_PUBLISH) {
            	//定时任务
            	Calendar publishTime1 = Calendar.getInstance();
            	if(task.publishTimerInterval !=0) {
            		if(task.lastBatchPublishTime == null && task.delaySpan != 0) {
            			Date date = task.publishTime;
            			publishTime1.setTime(date);
            			publishTime1.add(Calendar.MINUTE, task.delaySpan);
            		}else {
            			//定时，非延时任务，未被接的状态
            			if(task.lastBatchPublishTime == null) {
            				Date date = task.publishTime;
                			publishTime1.setTime(date); 
            			}else {
            				Date date = task.lastBatchPublishTime;
            				publishTime1 = Calendar.getInstance();
            				publishTime1.setTime(date);
            				publishTime1.add(Calendar.MINUTE, task.publishTimerInterval);
            			}
            		}
            	}
            	//非定时，延时任务
            	else if(task.publishTimerInterval == 0 && task.delaySpan != 0) {
            		Date date = task.publishTime;
        			publishTime1.setTime(date); 
        			publishTime1.add(Calendar.MINUTE, task.delaySpan);
            	}
            	SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            	publishTime = sdf.format(publishTime1.getTime());
                renderArgs.put("publishTime", publishTime);
            }
        }finally {
            ss.close();
        }
        renderArgs.put("task", task);
        render();
    }
    
    
    
    
    
    
    
    
    
    
    /**
     * 
     * 获取任务基本信息
     *
     * @param id
     * @since  v2.0
     * @author Mark Xu
     * @created 2015-5-6 下午2:50:10
     */
    public static void getTaskDetail(@Required long id){
    	handleWrongInput(false);
    	
    	Task task = Task.findById(id);
    	if(task == null) {
    		renderFailedJson(ReturnCode.ES_SEARCH_RETURN_EMPTY);
    	}
    	renderJson(task);
    }
    
    
    /**
     * 获取浏览基本信息
     */
    public static void getTaskDetail3(@Required long id){
    	handleWrongInput(false);
    	Task3 task = Task3.findById(id);
    	if(task == null) {
    		renderFailedJson(ReturnCode.ES_SEARCH_RETURN_EMPTY);
    	}
    	renderJson(task);
    }
    
    
    
    /**
     * 
     * 获取推广基本信息
     *
     * @param id
     * @since  v2.0
     * @author 尤齐春
     * @created 2016-8-3 下午
     */
    public static void getTaskDetail2(@Required long id){
    	handleWrongInput(false);
    	
    	Task2 task = Task2.findById(id);
    	if(task == null) {
    		renderFailedJson(ReturnCode.ES_SEARCH_RETURN_EMPTY);
    	}
    	renderJson(task);
    }
    
    
    /**
     * 
     * 修改任务信息
     *
     * @param id
     * @param taskRequest
     * @since  v2.0
     * @author Mark Xu
     * @created 2015-5-6 下午3:18:21
     */
    public static void modifyTask(@Required long id, String taskRequest,@Required Double expressWeight) {
    	handleWrongInput(false);
    	Task task = Task.findById(id);
    	if(task==null){
    	    renderFailedJson(ReturnCode.BIZ_LIMIT);
    	}
    	task.taskRequest=taskRequest;
    	task.expressWeight=expressWeight;
    	task.update();
    	renderSuccessJson();
    } 
    
    
    /**
     * 
     * 修改浏览信息
     */
    public static void modifyTask3(@Required long id, String taskRequest,@Required Double expressWeight) {
    	handleWrongInput(false);
    	Task3 task = Task3.findById(id);
    	if(task==null){
    	    renderFailedJson(ReturnCode.BIZ_LIMIT);
    	}
    	task.taskRequest=taskRequest;
    	task.expressWeight=expressWeight;
    	task.update();
    	renderSuccessJson();
    } 
    
    
    /**
     * 
     * 修改推广信息
     *
     * @param id
     * @param taskRequest
     * @since  v2.0
     * @author 尤齐春
     * @created 2016-8-3 
     */
    public static void modifyTask2(@Required long id, String taskRequest) {
    	handleWrongInput(false);
    	Task2 task = Task2.findById(id);
    	if(task==null){
    	    renderFailedJson(ReturnCode.BIZ_LIMIT);
    	}
    	task.taskRequest=taskRequest;
    	task.update();
    	renderSuccessJson();
    } 
    
    
    /**
     * 
     * 我的浏览->卖家任务详细-获取特定状态的子任务.
     */
    public static void listBuyerTasksForSeller3(@Required TaskSearchVo3 vo) {
		if (vo != null) {
			validation.required(vo.taskId);
			validation.min(vo.pageSize, 1);
			validation.max(vo.pageSize, 50);
			// validation.required(vo.status);
		}
        handleWrongInput(false);

        // 管理员访问该方法时不进行设置用户参数
        if(!Secure.isValidAdmin()){
            vo.sellerId = getCurrentUser().id;
        }
        Page<BuyerTask3> p = BuyerTask3.findByPageForSellerTask(vo);
        renderPageJson(p.items, p.totalCount);
    }
    
    
    
    /**
     * 
     * 我的任务->卖家任务详细-获取特定状态的子任务.
     *
     * @since  0.1
     * @author youblade
     * @created 2014年8月11日 下午3:35:12
     */
    public static void listBuyerTasksForSeller(@Required TaskSearchVo vo) {
		if (vo != null) {
			validation.required(vo.taskId);
			validation.min(vo.pageSize, 1);
			validation.max(vo.pageSize, 50);
			// validation.required(vo.status);
		}
        handleWrongInput(false);

        // 管理员访问该方法时不进行设置用户参数
        if(!Secure.isValidAdmin()){
            vo.sellerId = getCurrentUser().id;
        }
        Page<BuyerTask> p = BuyerTask.findByPageForSellerTask(vo);
        renderPageJson(p.items, p.totalCount);
    }
    
    
    
    /**
     * 
     * 我的推广->卖家推广详细-获取特定状态的子任务.
     *
     * @since  0.1
     * @author youblade
     * @created 2014年8月11日 下午3:35:12
     */
    public static void listBuyerTasksForSeller2(@Required TaskSearchVo2 vo) {
    	
    	System.out.println("进来了listBuyerTasksForSeller2");
    	System.out.println("进来了listBuyerTasksForSeller2");
    	System.out.println("进来了listBuyerTasksForSeller2");
    	System.out.println("进来了listBuyerTasksForSeller2");
    	System.out.println("进来了listBuyerTasksForSeller2");
    	System.out.println("进来了listBuyerTasksForSeller2");
    	
		if (vo != null) {
			System.out.println("进来了"+vo.taskId);
			System.out.println("进来了"+vo.taskId);
			System.out.println("进来了"+vo.taskId);
			System.out.println("进来了"+vo.taskId);System.out.println("进来了"+vo.taskId);
			validation.required(vo.taskId);
			validation.min(vo.pageSize, 1);
			validation.max(vo.pageSize, 50);
		}
        handleWrongInput(false);

        // 管理员访问该方法时不进行设置用户参数
        if(!Secure.isValidAdmin()){
            vo.sellerId = getCurrentUser().id;
        }
        Page<BuyerTask2> p = BuyerTask2.findByPageForSellerTask(vo);
       // renderPageJson(p.items, p.totalCount);
    }
    
    
    /**
     * 
     * 【买手】我的任务->买手任务详情.
     * 【商家】我的任务->任务详情->买手任务详情.
     * 【后台】任务管理->任务详情->买手任务详情.
     *
     * @since  0.1
     * @author youblade
     * @created 2014年7月25日 下午5:10:44
     */
    public static void buyerTaskDetail(@Required @Min(1) long id) {
        handleWrongInput(false);

        // 获取任务详情
        BuyerTaskVo taskVo = BuyerTask.findDetail(id);
        notFoundIfNull(taskVo);
        taskVo.value();
        
        User user = User.findById(taskVo.sellerId);
        BuyerTaskStep lastStep = BuyerTaskStep.findBuyerLastStep(id, taskVo.buyerId);
        //交易已完成
        if (lastStep != null && lastStep.type == BuyerTaskStepType.CONFIRM_REFUND) {
        	taskVo.finishTime=lastStep.createTime;
        }
        // 管理员访问该方法时不进行用户检查
        if(request.url.startsWith("/admin")){
            // 设置后台确认退款标记
            
            if (lastStep != null && lastStep.type == BuyerTaskStepType.CONFIRM_AND_COMMENT) {
                int recevHours = Hours.hoursBetween(new DateTime(lastStep.createTime), DateTime.now()).getHours();
                renderArgs.put("needSysRefund", recevHours >= 48);
               
            }
            
        } else if (taskVo.sellerId.longValue() != user.id && taskVo.buyerId.longValue() != user.id) {
            notFound();
        }
        //如果点击该链接的为SELLER用户，则把接单用户的平台账号抹除
        if(!request.url.startsWith("/admin")) {
        	taskVo.buyerNick = null;
        }
        renderArgs.put("taskInfo", taskVo);
        
        //获取任务经验值供显示
        Task task = Task.findById(taskVo.taskId);
        TaskListType tlt = task.getListType(user.registTime);
        renderArgs.put("taskType", tlt.experience);

        // 设置已完成的步骤
        if(taskVo.sysRefund){
            List<BuyerTaskStep> taskStepList = BuyerTaskStep.findByTaskIdForSysRefund(id);
            renderArgs.put("taskStepList", taskStepList);
            render("TaskCenter/sysRefundBuyerTaskDetail.html");
        }
        
        List<BuyerTaskStep> taskStepList = BuyerTaskStep.findByTaskId(id);
        for (BuyerTaskStep step : taskStepList) {
            renderArgs.put("step_" + step.type.toString(), step);
        }
        render();
    }
    
    
    
    /**
     * 
     * 【买手】我的推广->买手推广详情.
     * 【商家】我的推广->推广详情->买手推广详情.
     * 【后台】推广管理->推广详情->买手推广详情.
     * @author 尤齐春
     * @created 2016年7月25日 
     */
    public static void buyerTaskDetail2(@Required @Min(1) long id) {
        handleWrongInput(false);

        // 获取任务详情
        BuyerTaskVo2 taskVo = BuyerTask2.findDetail(id);
        notFoundIfNull(taskVo);
        taskVo.value();
        
        User user = User.findById(taskVo.sellerId);
        BuyerTaskStep2 lastStep = BuyerTaskStep2.findBuyerLastStep(id, taskVo.buyerId);
        
        //交易已完成
        if (lastStep != null && lastStep.type == BuyerTaskStepType.CONFIRM_REFUND) {
        	taskVo.finishTime=lastStep.createTime;
        }
        // 管理员访问该方法时不进行用户检查
        if(request.url.startsWith("/admin")){
            // 设置后台确认退款标记
            if (lastStep != null && lastStep.type == BuyerTaskStepType.CONFIRM_AND_COMMENT) {
                int recevHours = Hours.hoursBetween(new DateTime(lastStep.createTime), DateTime.now()).getHours();
                renderArgs.put("needSysRefund", recevHours >= 48);
               
            }
            
        } else if (taskVo.sellerId.longValue() != user.id && taskVo.buyerId.longValue() != user.id) {
            notFound();
        }
        //如果点击该链接的为SELLER用户，则把接单用户的平台账号抹除
        if(!request.url.startsWith("/admin")) {
        	taskVo.buyerNick = null;
        }
        renderArgs.put("taskInfo", taskVo);
        //获取任务经验值供显示
        Task2 task = Task2.findById(taskVo.taskId);
        TaskListType tlt = task.getListType(user.registTime);
        renderArgs.put("taskType", tlt.experience);

        // 设置已完成的步骤
        if(taskVo.sysRefund){
        	System.out.println();
            List<BuyerTaskStep2> taskStepList = BuyerTaskStep2.findByTaskIdForSysRefund(id);
            renderArgs.put("taskStepList", taskStepList);
            render("TaskCenter/sysRefundBuyerTaskDetail.html");
        }
        
        List<BuyerTaskStep2> taskStepList = BuyerTaskStep2.findByTaskId(id);
        for (BuyerTaskStep2 step : taskStepList) {
        	System.out.println("==========content======"+step.content);
        	System.out.println("==========content======"+step.content);
        	System.out.println("==========content======"+step.content);
        	System.out.println("==========content======"+step.content);
        	System.out.println("==========content======"+step.content);
        	renderArgs.put("itemurl", "1");
            renderArgs.put("step_" + step.type.toString(), step);
        }
        render();
    }
    
    
    
    
    /**
     * 
     * 【买手】我的任务->买手任务详情.
     * 【商家】我的任务->任务详情->买手任务详情.
     * 【后台】任务管理->任务详情->买手任务详情.
     *
     * @since  0.1
     * @author youblade
     * @created 2014年7月25日 下午5:10:44
     */
    public static void buyerTaskDetail3(@Required @Min(1) long id) {
        handleWrongInput(false);

        // 获取任务详情
        BuyerTaskVo3 taskVo = BuyerTask3.findDetail(id);
        notFoundIfNull(taskVo);
        taskVo.value();
        
        User user = User.findById(taskVo.sellerId);
        BuyerTaskStep3 lastStep = BuyerTaskStep3.findBuyerLastStep(id, taskVo.buyerId);
        //交易已完成
        if (lastStep != null && lastStep.type == BuyerTaskStepType.CONFIRM_REFUND) {
        	taskVo.finishTime=lastStep.createTime;
        }
        // 管理员访问该方法时不进行用户检查
        if(request.url.startsWith("/admin")){
            // 设置后台确认退款标记
            
            if (lastStep != null && lastStep.type == BuyerTaskStepType.CONFIRM_AND_COMMENT) {
                int recevHours = Hours.hoursBetween(new DateTime(lastStep.createTime), DateTime.now()).getHours();
                renderArgs.put("needSysRefund", recevHours >= 48);
               
            }
            
        } else if (taskVo.sellerId.longValue() != user.id && taskVo.buyerId.longValue() != user.id) {
            notFound();
        }
        //如果点击该链接的为SELLER用户，则把接单用户的平台账号抹除
        if(!request.url.startsWith("/admin")) {
        	taskVo.buyerNick = null;
        }
        renderArgs.put("taskInfo", taskVo);
        
        //获取任务经验值供显示
        Task3 task = Task3.findById(taskVo.taskId);
        TaskListType tlt = task.getListType(user.registTime);
        renderArgs.put("taskType", tlt.experience);

        // 设置已完成的步骤
        if(taskVo.sysRefund){
            List<BuyerTaskStep3> taskStepList = BuyerTaskStep3.findByTaskIdForSysRefund(id);
            renderArgs.put("taskStepList", taskStepList);
            render("TaskCenter/sysRefundBuyerTaskDetail.html");
        }
        
        List<BuyerTaskStep3> taskStepList = BuyerTaskStep3.findByTaskId(id);
        for (BuyerTaskStep3 step : taskStepList) {
            renderArgs.put("step_" + step.type.toString(), step);
        }
        render();
    }
    
    
    
    
    
    
    
    final static List<TaskStatus> EXAMINE_STATUS_LIST = Lists.newArrayList(TaskStatus.WAIT_EXAMINE,
        TaskStatus.EXAMINING, TaskStatus.NOT_PASS);
    /**
     * 
     * 我的任务->进入“平台审核中”的任务页面.
     *
     * @since  0.1
     * @author youblade
     * @created 2014年8月18日 下午4:35:15
     */
    public static void examine(@Required TaskStatus status) {
    	System.out.println("我的任务->进入“平台审核中”的任务页面1");
    	System.out.println("我的任务->进入“平台审核中”的任务页面1");
    	System.out.println("我的任务->进入“平台审核中”的任务页面1");
    	System.out.println("我的任务->进入“平台审核中”的任务页面1");
    	System.out.println("我的任务->进入“平台审核中”的任务页面1");
    	System.out.println("我的任务->进入“平台审核中”的任务页面1");
        renderArgs.put(BizConstants.PLATFORMS, Platform.values());
        renderArgs.put(BizConstants.DEVICES, Device.values());
        renderArgs.put(BizConstants.TASK_TYPES, TaskType.values());
        
        renderArgs.put(BizConstants.TASK_STATUS, EXAMINE_STATUS_LIST);
        render(status);
    }
    
    
    /**
     * 
     * 我的任务->进入“平台审核中”的任务页面.
     *
     * @since  0.1
     * @author youblade
     * @created 2014年8月18日 下午4:35:15
     */
    public static void examine2(@Required TaskStatus status) {
    	System.out.println("我的任务->进入“平台审核中”的任务页面");
    	System.out.println("我的任务->进入“平台审核中”的任务页面");
    	System.out.println("我的任务->进入“平台审核中”的任务页面");
    	System.out.println("我的任务->进入“平台审核中”的任务页面");
    	System.out.println("我的任务->进入“平台审核中”的任务页面");
    	System.out.println("我的任务->进入“平台审核中”的任务页面");
        renderArgs.put(BizConstants.PLATFORMS, Platform2.values());
        renderArgs.put(BizConstants.DEVICES, Device.values());
        renderArgs.put(BizConstants.TASK_TYPES, TaskType2.values());
        
        renderArgs.put(BizConstants.TASK_STATUS, EXAMINE_STATUS_LIST);
        render(status);
    }
    
    
    
    /**
     * 我的任务->确认发货页面.
     */
    public static void sendGoods(Platform platform){
        // 已选择的平台参数
        renderArgs.put("selectedPlatform", platform);
        renderArgs.put(BizConstants.PLATFORMS, Platform.values());
    	render();
    }
    
    /**
     * 我的任务->确认发货页面.
     */
    public static void sendGoods3(Platform3 platform){
        // 已选择的平台参数
        renderArgs.put("selectedPlatform", platform);
        renderArgs.put(BizConstants.PLATFORMS, Platform3.values());
    	render();
    }
    
    /**
     * 我的推广->确认发货页面.
     */
    public static void sendSHENHE(Platform2 platform){
        renderArgs.put("selectedPlatform", platform);
        renderArgs.put(BizConstants.PLATFORMS, Platform2.values());
    	render();
    }
    
    
    /**
     * 
     * 我的任务->（确认发货页面）获取待发货任务列表.
     *
     * @since  0.1
     * @author youblade
     * @created 2014年8月7日 下午4:38:12
     */
    public static void listWaitSendGoods(@Required TaskSearchVo vo) {
        if (vo != null) {
            validation.min(vo.pageNo, 1);
            validation.max(vo.pageSize, 50);
        }
        handleWrongInput(false);

        long sellerId = getCurrentUser().id;
        vo.sellerId(sellerId).status(TaskStatus._WAIT_SEND_GOODS).pageSize(20);
        Page<BuyerTaskVo> p = BuyerTask.findListByPage(vo);

        renderPageJson(p.items, p.totalCount);
    }
    
    
    
    /**
     * 我的推广->（确认审核页面）获取待审核推广列表.
     */
    public static void listWaitSendGoods2(@Required TaskSearchVo2 vo) {
        if (vo != null) {
            validation.min(vo.pageNo, 1);
            validation.max(vo.pageSize, 50);
        }
        handleWrongInput(false);
        long sellerId = getCurrentUser().id;
        vo.sellerId(sellerId).status(TaskStatus._WAIT_SEND_GOODS).pageSize(20);
        Page<BuyerTaskVo2> p = BuyerTask2.findListByPage(vo);
        renderPageJson(p.items, p.totalCount);
    }
    
    
    
    
    
    
	/**
	 * 
	 * 确认发货
	 * 
	 * @param buyerTask
	 * @param ids
	 * @since v0.1
	 * @author moloch
	 * @created 2014-8-12 上午11:04:42
	 */
	public static void sendGood(BuyerTask buyerTask, List<Long> ids) {
		
		// 单个确认
		if (ids == null) {
			buyerTask.sendGood();
			renderSuccessJson();
		}

		// 批量确认
		for (Long id : ids) {
			BuyerTask bt = new BuyerTask();
			bt.id = id;
//			bt.buyerId = buyerTask.buyerId;
			bt.sendGood();
		}
		renderSuccessJson();
	}
    
	
	
	
	/**
	 * 
	 * 审核通过
	 */
	/*public static void sendGood2(BuyerTask2 buyerTask, List<Long> ids) {
		// 单个确认
		if (ids == null) {
			buyerTask.sendGood();
			renderSuccessJson();
		}
		// 批量确认
		for (Long id : ids) {
			BuyerTask2 bt = new BuyerTask2();
			bt.id = id;
			bt.sendGood();
		}
		renderSuccessJson();
	}*/
	public static void sendGood2(BuyerTask2 buyerTask, List<Long> ids) {
		// 单个确认
		if (ids == null) {
			//buyerTask.sendGood();
			Long id = buyerTask.id;
			System.out.println("==================="+id);
			System.out.println("==================="+id);
			System.out.println("==================="+id);
			System.out.println("==================="+id);
			System.out.println("==================="+id);
			//bt.sendGood();
			BuyerTask2 bt = BuyerTask2.findById(id);
			bt.status=TaskStatus.WAIT_REFUND;
	        System.out.println("BuyerTask2================"+bt.toString());
	        System.out.println("=BuyerTask2=============="+bt.toString());
	        System.out.println("==BuyerTask2=============="+bt.toString());
	        System.out.println("==BuyerTask2=============="+bt.toString());
	        System.out.println("===BuyerTask2============="+bt.toString());
	        
	        if (bt == null || bt.status != TaskStatus.WAIT_REFUND) {
	            renderFailedJson(ReturnCode.WRONG_INPUT);
	        }
	        // 【管理员】操作时不检查用户身份
	        if (!isAdminOperate()) {
	            User seller = getCurrentUser();
	            if (bt.sellerId.longValue() != seller.id) {
	                renderFailedJson(ReturnCode.WRONG_INPUT);
	            }
	        }

	        SqlSession ss = SessionFactory.getSqlSessionWithoutAutoCommit();
	        try {
	            // 更新任务状态
	            BuyerTask2 buyerTask3 = BuyerTask2.instance(bt.id).status(TaskStatus.REFUNDING);
	            Date now = DateTime.now().toDate();
	            buyerTask3.modifyTime = now;
	            ss.getMapper(BuyerTaskMapper2.class).updateById(buyerTask3);
	            // 保存记录
	            BuyerTaskStep2 taskStep = BuyerTaskStep2.newInstance(bt.id, bt.buyerId).type(BuyerTaskStepType.REFUND);
	            taskStep.content = BuyerTaskStepVo.newInstance().transNo(bt.transNo).toJson();
	            taskStep.createTime = now;
	            ss.getMapper(BuyerTaskStepMapper2.class).insert(taskStep);

	            // 若管理员操作则创建备案记录，并扣除商家押金及退款保障金
	            if(isAdminOperate()){
	            	//将商家返款单号设置为空，用于之后的平台提现标识
	                AdminRefundRecord record = AdminRefundRecord.newInstance(bt).createTime(now);
	                bt.transNo = null;
	                ss.getMapper(AdminRefundRecordMapper.class).insert(record);
	                
	                SellerPledgeRecordMapper pledgeMapper = ss.getMapper(SellerPledgeRecordMapper.class);
	                SellerPledgeRecord lastPledge = pledgeMapper.selectLastRecord(bt.sellerId);
	                
	                //如果平台帮商家返款，则需要扣除商家的押金(扣除量等于买手垫付的资金)
	                long depositAmount = BuyerTask2.findById(id).paidFee;
	                //如果选择扣除商家延期押金，则总共扣除的押金包括商品价格量和5金币的延期罚金
	               /* if(isSubtraction) {
	                    depositAmount = BizConstants.SELLER_REFUND_SUBTRACTION*100 + depositAmount;
	                }*/

	                
	                String memo = "推广[" + bt.getTaskNo() + "]超时未退款，平台扣除押金";
	                /*if(isSubtraction) {
	                	memo = "推广[" + bt.getTaskNo() + "]超时未退款，平台扣除押金和5元额外的超时金额";
	                }*/
	                lastPledge = SellerPledgeRecord.newInstance(bt.sellerId, lastPledge)
	                    .action(PledgeAction.DEDUCT, depositAmount).taskId(bt.taskId).memo(memo).createTime(now);
	                pledgeMapper.insert(lastPledge);
	                
	                //返款本金到买手本金记录表中（买手本金记录）
	    			BuyerDepositRecordMapper depositRecordMapper = ss.getMapper(BuyerDepositRecordMapper.class);
	    			BuyerDepositRecord depositRecord = depositRecordMapper.selectLastRecord(bt.buyerId);
	    			depositRecord = BuyerDepositRecord.newInstance(bt.buyerId, depositRecord).taskId(bt.taskId).plus(bt.paidFee).createTime(DateTime.now().toDate());
	    			depositRecord.memo = "商家返款超时，由平台退还推广[" + bt.getTaskNo() + "]垫付本金到平台本金";
	    			depositRecordMapper.insert(depositRecord);
	    			
	                // 更新商家缓存
	    			User.findByIdWichCache(bt.buyerId).deposit(depositRecord.balance).updateCache();
	                User.findByIdWichCache(bt.sellerId).pledge(lastPledge.balance).updateCache();
	            }
	            ss.commit();
	            System.out.println("------status-----------"+buyerTask3.status);
	            System.out.println("------status-----------"+buyerTask3.status);
	            System.out.println("------status-----------"+buyerTask3.status);
	            System.out.println("-------status----------"+buyerTask3.status);
	            System.out.println("-----------------"+buyerTask3.status);
	            System.out.println("-----------------------------返款过了-");
	            System.out.println("-----------------------------返款过了-");
	            System.out.println("-----------------------------返款过了-");
	            System.out.println("-----------------------------返款过了-");
	            System.out.println("-----------------------------返款过了-");
	            BuyerTask2 buyerTask4 = BuyerTask2.findById(id);
	            buyerTask4.status=TaskStatus.FINISHED;
	            System.out.println("=================="+id);
	            System.out.println("=================="+id);
	            System.out.println("=================="+id);
	            System.out.println("=================="+buyerTask4.status);
	            System.out.println("=================="+buyerTask4.status);
	            System.out.println("=================="+buyerTask4.buyerId);
	            System.out.println("=================="+buyerTask4.buyerId);
	           
	    		//查看是否为管理员操作，如果是，则不需要验证
	    		if(!isAdminOperate()) {
	    			if (buyerTask4 == null ) {
	    				renderFailedJson(ReturnCode.WRONG_INPUT);
	    			}
	    		}
	    		
	    		buyerTask4.confirmSellerRefund2();
	            System.out.println("=========================执行完毕");
	            System.out.println("=========================执行完毕");
	            System.out.println("=========================执行完毕");
	            System.out.println("=========================执行完毕");
	            System.out.println("=========================执行完毕");
	            System.out.println("=========================执行完毕");
	        } catch (Exception e) {
	            log.error(e.getMessage(), e);
	            ss.rollback();
	            renderFailedJson(ReturnCode.FAIL);
	        } finally {
	            ss.close();
	        }
	        renderSuccessJson();
			
		}
		// 批量确认
		/*for (Long id : ids) {
			BuyerTask2 bt = new BuyerTask2();
			bt.id = id;
			bt.sendGood();
		}*/
		renderSuccessJson();
	}
	
	
	
	
	
	
    /**
     * 
     * 我的任务->退款页面.
     */
    public static void refund(Platform platform){
        // 已选择的平台参数
        renderArgs.put("selectedPlatform", platform);
        renderArgs.put(BizConstants.PLATFORMS, Platform.values());
    	render();
    }
    
    
    /**
     * 我的推广->退款页面.
     */
    public static void refund2(Platform2 platform){
        // 已选择的平台参数
        renderArgs.put("selectedPlatform", platform);
        renderArgs.put(BizConstants.PLATFORMS, Platform2.values());
    	render();
    }
    
    
    
    /**
     * 
     * 【商家】我的任务->处理“平台返款”任务页面
     *
     * @since  v1.7
     * @author youblade
     * @created 2014年11月28日 下午1:11:31
     */
    public static void sysRefund(){
        renderArgs.put(BizConstants.PLATFORMS, Platform.values());
        render();
    }
    
    /**
     * 【商家】我的推广->处理“平台返款”任务页面
     */
    public static void sysRefund2(){
        renderArgs.put(BizConstants.PLATFORMS, Platform2.values());
        render();
    }
    
    
    
    /**
     * 
     * 【商家】我的任务->（退款页面-平台退款）页面各个部分的数量
     *
     * @param vo
     * @since  v2.0
     * @author Mark Xu
     * @created 2015-2-6 下午12:05:02
     */
    public static void sellerTaskCount(@Required TaskSearchVo vo) {
    	vo.sellerId = Long.parseLong(session.get(Secure.FIELD_AUTH));
    	vo.module = SearchModule.WAIT_SELLER_CONFIRM_SYS_REFUND;
    	Integer p1 = BuyerTask.findListByPage(vo).totalCount;
    	
    	vo.module = SearchModule.BUYER_REJECT_SYS_REFUND;
    	Integer p2 = BuyerTask.findListByPage(vo).totalCount;
    	
    	vo.module = SearchModule.REFUNDING;
    	Integer p3 = BuyerTask.findListByPage(vo).totalCount;
    	Map map = Maps.newHashMapWithExpectedSize(3);
        map.put("count1", p1);
        map.put("count2", p2);
        map.put("count3", p3);
        renderJson(map);
    }
    
    
    /**
     * 
     * 【商家】我的任务->（退款页面-平台退款）页面各个部分的数量
     */
    public static void sellerTaskCount2(@Required TaskSearchVo2 vo) {
    	vo.sellerId = Long.parseLong(session.get(Secure.FIELD_AUTH));
    	vo.module = SearchModule2.WAIT_SELLER_CONFIRM_SYS_REFUND;
    	Integer p1 = BuyerTask2.findListByPage(vo).totalCount;
    	
    	vo.module = SearchModule2.BUYER_REJECT_SYS_REFUND;
    	Integer p2 = BuyerTask2.findListByPage(vo).totalCount;
    	
    	vo.module = SearchModule2.REFUNDING;
    	Integer p3 = BuyerTask2.findListByPage(vo).totalCount;
    	Map map = Maps.newHashMapWithExpectedSize(3);
        map.put("count1", p1);
        map.put("count2", p2);
        map.put("count3", p3);
        renderJson(map);
    }
    
    
    
    
    /**
     * 
     * 【商家】我的任务->（退款页面-费平台退款）页面各个部分的数量
     *
     * @param vo
     * @since  v2.0
     * @author Mark Xu
     * @created 2015-2-7 下午5:00:25
     */
    public static void sellerSelfCount(@Required TaskSearchVo vo) {
    	vo.sellerId = Long.parseLong(session.get(Secure.FIELD_AUTH));
    	vo.module = SearchModule.SELLER_REFUND;
    	vo.status = TaskStatus.WAIT_REFUND;
    	Integer p1 = BuyerTask.findListByPage(vo).totalCount;
    	
    	vo.status = TaskStatus.REFUNDING;
    	Integer p2 = BuyerTask.findListByPage(vo).totalCount;
    	
    	vo.status = TaskStatus.FINISHED;
    	Integer p3 = BuyerTask.findListByPage(vo).totalCount;
    	Map map = Maps.newHashMapWithExpectedSize(3);
        map.put("count1", p1);
        map.put("count2", p2);
        map.put("count3", p3);
        renderJson(map);
    }
    
    
    
    /**
     * 
     * 【商家】我的任务->（退款页面-费平台退款）页面各个部分的数量
     *
     * @param vo
     * @since  v2.0
     * @author Mark Xu
     * @created 2015-2-7 下午5:00:25
     */
    public static void sellerSelfCount2(@Required TaskSearchVo2 vo) {
    	vo.sellerId = Long.parseLong(session.get(Secure.FIELD_AUTH));
    	vo.module = SearchModule2.SELLER_REFUND;
    	vo.status = TaskStatus.WAIT_REFUND;
    	Integer p1 = BuyerTask2.findListByPage(vo).totalCount;
    	
    	vo.status = TaskStatus.REFUNDING;
    	Integer p2 = BuyerTask2.findListByPage(vo).totalCount;
    	
    	vo.status = TaskStatus.FINISHED;
    	Integer p3 = BuyerTask2.findListByPage(vo).totalCount;
    	Map map = Maps.newHashMapWithExpectedSize(3);
        map.put("count1", p1);
        map.put("count2", p2);
        map.put("count3", p3);
        renderJson(map);
    }
    
    
    
    /**
     * 
     * 【商家】我的任务->（退款页面）获取待退款任务列表.
     */
    public static void listSellerRefund(@Required TaskSearchVo vo) {
        if (vo != null) {
            validation.min(vo.pageNo, 1);
            validation.max(vo.pageSize, 50);
        }
        handleWrongInput(false);

        long sellerId = getCurrentUser().id;
        vo.sellerId(sellerId).pageSize(20);

        // 默认为“商家手动退款”
        if (vo.module == null) {
            vo.module(SearchModule.SELLER_REFUND);
        }
        // 查询SQL不同
        Page<BuyerTaskVo> p = BuyerTask.findListByPage(vo);
        renderPageJson(p.items, p.totalCount);
    }
    
    
    
    /**
     * 【商家】我的任务->（退款页面）获取待退款任务列表.
     */
    public static void listSellerRefund2(@Required TaskSearchVo2 vo) {
        if (vo != null) {
            validation.min(vo.pageNo, 1);
            validation.max(vo.pageSize, 50);
        }
        handleWrongInput(false);

        long sellerId = getCurrentUser().id;
        vo.sellerId(sellerId).pageSize(20);

        // 默认为“商家手动退款”
        if (vo.module == null) {
            vo.module(SearchModule2.SELLER_REFUND);
        }
        // 查询SQL不同
        Page<BuyerTaskVo2> p = BuyerTask2.findListByPage(vo);
        renderPageJson(p.items, p.totalCount);
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
    
    public static void listBuyerRefund(@Required TaskSearchVo vo) {
        if (vo != null) {
            validation.min(vo.pageNo, 1);
            validation.max(vo.pageSize, 50);
        }
        handleWrongInput(false);

        long BuyerId = getCurrentUser().id;
        vo.buyerId(BuyerId).pageSize(20);
        // 默认为“商家手动退款”
        if (vo.module == null) {
            vo.module(SearchModule.REFUNDING);
        }
        
        Page<BuyerTaskVo> p = BuyerTask.findListByPage(vo);

        renderPageJson(p.items, p.totalCount);
    }
    
    
	/**
	 * 
	 * 【卖家】确认退款
	 * 【管理员】确认退款
	 * 
	 * @param id   ：子任务ID
	 * @param transNo
	 * @since v0.1
	 * @author moloch
	 * @created 2014-8-17 下午6:49:55
	 */
    public synchronized static void confirmRefund(@Required @Min(1) long id, @Required String transNo, @Required boolean isSubtraction) {
        handleWrongInput(false);

        BuyerTask bt = BuyerTask.findById(id);
        if (bt == null || bt.status != TaskStatus.WAIT_REFUND) {
            renderFailedJson(ReturnCode.WRONG_INPUT);
        }
        
        // 【管理员】操作时不检查用户身份
        if (!isAdminOperate()) {
            User seller = getCurrentUser();
            if (bt.sellerId.longValue() != seller.id) {
                renderFailedJson(ReturnCode.WRONG_INPUT);
            }
        }

        SqlSession ss = SessionFactory.getSqlSessionWithoutAutoCommit();
        try {
            // 更新任务状态
            BuyerTask buyerTask = BuyerTask.instance(bt.id).status(TaskStatus.REFUNDING);
            if(!isAdminOperate()){
            	buyerTask.transNo = transNo.trim();
            }
            Date now = DateTime.now().toDate();
            buyerTask.modifyTime = now;
            ss.getMapper(BuyerTaskMapper.class).updateById(buyerTask);

            // 保存记录
            BuyerTaskStep taskStep = BuyerTaskStep.newInstance(bt.id, bt.buyerId).type(BuyerTaskStepType.REFUND);
            taskStep.content = BuyerTaskStepVo.newInstance().transNo(bt.transNo).toJson();
            taskStep.createTime = now;
            ss.getMapper(BuyerTaskStepMapper.class).insert(taskStep);

            // 若管理员操作则创建备案记录，并扣除商家押金及退款保障金
            if(isAdminOperate()){
            	//将商家返款单号设置为空，用于之后的平台提现标识
            	bt.transNo = transNo.trim();
                AdminRefundRecord record = AdminRefundRecord.newInstance(bt).createTime(now);
                bt.transNo = null;
                ss.getMapper(AdminRefundRecordMapper.class).insert(record);
                
                SellerPledgeRecordMapper pledgeMapper = ss.getMapper(SellerPledgeRecordMapper.class);
                SellerPledgeRecord lastPledge = pledgeMapper.selectLastRecord(bt.sellerId);
                
                //如果平台帮商家返款，则需要扣除商家的押金(扣除量等于买手垫付的资金)
                long depositAmount = BuyerTask.findById(id).paidFee;
                //如果选择扣除商家延期押金，则总共扣除的押金包括商品价格量和5金币的延期罚金
                if(isSubtraction) {
                    depositAmount = BizConstants.SELLER_REFUND_SUBTRACTION*100 + depositAmount;
                }

                
                String memo = "任务[" + bt.getTaskNo() + "]超时未退款，平台扣除押金";
                if(isSubtraction) {
                	memo = "任务[" + bt.getTaskNo() + "]超时未退款，平台扣除押金和5元额外的超时金额";
                }
                lastPledge = SellerPledgeRecord.newInstance(bt.sellerId, lastPledge)
                    .action(PledgeAction.DEDUCT, depositAmount).taskId(bt.taskId).memo(memo).createTime(now);
                pledgeMapper.insert(lastPledge);
                
                //返款本金到买手本金记录表中（买手本金记录）
    			BuyerDepositRecordMapper depositRecordMapper = ss.getMapper(BuyerDepositRecordMapper.class);
    			BuyerDepositRecord depositRecord = depositRecordMapper.selectLastRecord(bt.buyerId);
    			depositRecord = BuyerDepositRecord.newInstance(bt.buyerId, depositRecord).taskId(bt.taskId).plus(bt.paidFee).createTime(DateTime.now().toDate());
    			depositRecord.memo = "商家返款超时，由平台退还任务[" + bt.getTaskNo() + "]垫付本金到平台本金";
    			depositRecordMapper.insert(depositRecord);
    			
                // 更新商家缓存
    			User.findByIdWichCache(bt.buyerId).deposit(depositRecord.balance).updateCache();
                User.findByIdWichCache(bt.sellerId).pledge(lastPledge.balance).updateCache();
            }
            
            ss.commit();
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            ss.rollback();
            renderFailedJson(ReturnCode.FAIL);
        } finally {
            ss.close();
        }
        renderSuccessJson();
    }
    

    
    /**
	 * 
	 * 【卖家】确认退款
	 * 【管理员】确认退款
	 * @param id   ：子任务ID
	 * @param transNo
	 * @since v0.1
	 * @author moloch
	 */
    public synchronized static void confirmRefund3(@Required @Min(1) long id, @Required String transNo, @Required boolean isSubtraction) {
        handleWrongInput(false);

        BuyerTask3 bt = BuyerTask3.findById(id);
        if (bt == null || bt.status != TaskStatus.WAIT_REFUND) {
            renderFailedJson(ReturnCode.WRONG_INPUT);
        }
        
        // 【管理员】操作时不检查用户身份
        if (!isAdminOperate()) {
            User seller = getCurrentUser();
            if (bt.sellerId.longValue() != seller.id) {
                renderFailedJson(ReturnCode.WRONG_INPUT);
            }
        }

        SqlSession ss = SessionFactory.getSqlSessionWithoutAutoCommit();
        try {
            // 更新任务状态
            BuyerTask3 buyerTask = BuyerTask3.instance(bt.id).status(TaskStatus.REFUNDING);
            if(!isAdminOperate()){
            	buyerTask.transNo = transNo.trim();
            }
            Date now = DateTime.now().toDate();
            buyerTask.modifyTime = now;
            ss.getMapper(BuyerTaskMapper3.class).updateById(buyerTask);

            // 保存记录
            BuyerTaskStep3 taskStep = BuyerTaskStep3.newInstance(bt.id, bt.buyerId).type(BuyerTaskStepType.REFUND);
            taskStep.content = BuyerTaskStepVo.newInstance().transNo(bt.transNo).toJson();
            taskStep.createTime = now;
            ss.getMapper(BuyerTaskStepMapper3.class).insert(taskStep);

            // 若管理员操作则创建备案记录，并扣除商家押金及退款保障金
            if(isAdminOperate()){
            	//将商家返款单号设置为空，用于之后的平台提现标识
            	bt.transNo = transNo.trim();
                AdminRefundRecord record = AdminRefundRecord.newInstance(bt).createTime(now);
                bt.transNo = null;
                ss.getMapper(AdminRefundRecordMapper.class).insert(record);
                
                SellerPledgeRecordMapper pledgeMapper = ss.getMapper(SellerPledgeRecordMapper.class);
                SellerPledgeRecord lastPledge = pledgeMapper.selectLastRecord(bt.sellerId);
                
                //如果平台帮商家返款，则需要扣除商家的押金(扣除量等于买手垫付的资金)
                long depositAmount = BuyerTask3.findById(id).paidFee;
                //如果选择扣除商家延期押金，则总共扣除的押金包括商品价格量和5金币的延期罚金
                if(isSubtraction) {
                    depositAmount = BizConstants.SELLER_REFUND_SUBTRACTION*100 + depositAmount;
                }

                
                String memo = "任务[" + bt.getTaskNo() + "]超时未退款，平台扣除押金";
                if(isSubtraction) {
                	memo = "任务[" + bt.getTaskNo() + "]超时未退款，平台扣除押金和5元额外的超时金额";
                }
                lastPledge = SellerPledgeRecord.newInstance(bt.sellerId, lastPledge)
                    .action(PledgeAction.DEDUCT, depositAmount).taskId(bt.taskId).memo(memo).createTime(now);
                pledgeMapper.insert(lastPledge);
                
                //返款本金到买手本金记录表中（买手本金记录）
    			BuyerDepositRecordMapper depositRecordMapper = ss.getMapper(BuyerDepositRecordMapper.class);
    			BuyerDepositRecord depositRecord = depositRecordMapper.selectLastRecord(bt.buyerId);
    			depositRecord = BuyerDepositRecord.newInstance(bt.buyerId, depositRecord).taskId(bt.taskId).plus(bt.paidFee).createTime(DateTime.now().toDate());
    			depositRecord.memo = "商家返款超时，由平台退还任务[" + bt.getTaskNo() + "]垫付本金到平台本金";
    			depositRecordMapper.insert(depositRecord);
    			
                // 更新商家缓存
    			User.findByIdWichCache(bt.buyerId).deposit(depositRecord.balance).updateCache();
                User.findByIdWichCache(bt.sellerId).pledge(lastPledge.balance).updateCache();
            }
            
            ss.commit();
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            ss.rollback();
            renderFailedJson(ReturnCode.FAIL);
        } finally {
            ss.close();
        }
        renderSuccessJson();
    }
    
    
    
    
    
   /* *//**
	 * 【卖家】确认退款
	 * 【管理员】确认退款
	 *//*
*/    public synchronized static void confirmRefund2(@Required @Min(1) long id, @Required String transNo, @Required boolean isSubtraction) {
        handleWrongInput(false);

        BuyerTask2 bt = BuyerTask2.findById(id);
        if (bt == null || bt.status != TaskStatus.WAIT_REFUND) {
            renderFailedJson(ReturnCode.WRONG_INPUT);
        }
        // 【管理员】操作时不检查用户身份
        if (!isAdminOperate()) {
            User seller = getCurrentUser();
            if (bt.sellerId.longValue() != seller.id) {
                renderFailedJson(ReturnCode.WRONG_INPUT);
            }
        }

        SqlSession ss = SessionFactory.getSqlSessionWithoutAutoCommit();
        try {
            // 更新任务状态
            BuyerTask2 buyerTask = BuyerTask2.instance(bt.id).status(TaskStatus.REFUNDING);
            if(!isAdminOperate()){
            	buyerTask.transNo = transNo.trim();
            }
            Date now = DateTime.now().toDate();
            buyerTask.modifyTime = now;
            ss.getMapper(BuyerTaskMapper2.class).updateById(buyerTask);

            // 保存记录
            BuyerTaskStep2 taskStep = BuyerTaskStep2.newInstance(bt.id, bt.buyerId).type(BuyerTaskStepType.REFUND);
            taskStep.content = BuyerTaskStepVo.newInstance().transNo(bt.transNo).toJson();
            taskStep.createTime = now;
            ss.getMapper(BuyerTaskStepMapper2.class).insert(taskStep);

            // 若管理员操作则创建备案记录，并扣除商家押金及退款保障金
            if(isAdminOperate()){
            	//将商家返款单号设置为空，用于之后的平台提现标识
            	bt.transNo = transNo.trim();
                AdminRefundRecord record = AdminRefundRecord.newInstance(bt).createTime(now);
                bt.transNo = null;
                ss.getMapper(AdminRefundRecordMapper.class).insert(record);
                
                SellerPledgeRecordMapper pledgeMapper = ss.getMapper(SellerPledgeRecordMapper.class);
                SellerPledgeRecord lastPledge = pledgeMapper.selectLastRecord(bt.sellerId);
                
                //如果平台帮商家返款，则需要扣除商家的押金(扣除量等于买手垫付的资金)
                long depositAmount = BuyerTask2.findById(id).paidFee;
                //如果选择扣除商家延期押金，则总共扣除的押金包括商品价格量和5金币的延期罚金
                if(isSubtraction) {
                    depositAmount = BizConstants.SELLER_REFUND_SUBTRACTION*100 + depositAmount;
                }

                
                String memo = "推广[" + bt.getTaskNo() + "]超时未退款，平台扣除押金";
                if(isSubtraction) {
                	memo = "推广[" + bt.getTaskNo() + "]超时未退款，平台扣除押金和5元额外的超时金额";
                }
                lastPledge = SellerPledgeRecord.newInstance(bt.sellerId, lastPledge)
                    .action(PledgeAction.DEDUCT, depositAmount).taskId(bt.taskId).memo(memo).createTime(now);
                pledgeMapper.insert(lastPledge);
                
                //返款本金到买手本金记录表中（买手本金记录）
    			BuyerDepositRecordMapper depositRecordMapper = ss.getMapper(BuyerDepositRecordMapper.class);
    			BuyerDepositRecord depositRecord = depositRecordMapper.selectLastRecord(bt.buyerId);
    			depositRecord = BuyerDepositRecord.newInstance(bt.buyerId, depositRecord).taskId(bt.taskId).plus(bt.paidFee).createTime(DateTime.now().toDate());
    			depositRecord.memo = "商家返款超时，由平台退还推广[" + bt.getTaskNo() + "]垫付本金到平台本金";
    			depositRecordMapper.insert(depositRecord);
    			
                // 更新商家缓存
    			User.findByIdWichCache(bt.buyerId).deposit(depositRecord.balance).updateCache();
                User.findByIdWichCache(bt.sellerId).pledge(lastPledge.balance).updateCache();
            }
            
            ss.commit();
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            ss.rollback();
            renderFailedJson(ReturnCode.FAIL);
        } finally {
            ss.close();
        }
        renderSuccessJson();
    }
    
    
    //返款
    public static void confirmRefund3(long id,  boolean isSubtraction) {
    	System.out.println("======================");
        handleWrongInput(false);
        BuyerTask2 bt = BuyerTask2.findById(id);
        System.out.println("BuyerTask2================"+bt.toString());
        System.out.println("=BuyerTask2=============="+bt.toString());
        System.out.println("==BuyerTask2=============="+bt.toString());
        System.out.println("==BuyerTask2=============="+bt.toString());
        System.out.println("===BuyerTask2============="+bt.toString());
        
        if (bt == null || bt.status != TaskStatus.WAIT_REFUND) {
            renderFailedJson(ReturnCode.WRONG_INPUT);
        }
        // 【管理员】操作时不检查用户身份
        if (!isAdminOperate()) {
            User seller = getCurrentUser();
            if (bt.sellerId.longValue() != seller.id) {
                renderFailedJson(ReturnCode.WRONG_INPUT);
            }
        }

        SqlSession ss = SessionFactory.getSqlSessionWithoutAutoCommit();
        try {
            // 更新任务状态
            BuyerTask2 buyerTask = BuyerTask2.instance(bt.id).status(TaskStatus.REFUNDING);
            Date now = DateTime.now().toDate();
            buyerTask.modifyTime = now;
            ss.getMapper(BuyerTaskMapper2.class).updateById(buyerTask);
            // 保存记录
            BuyerTaskStep2 taskStep = BuyerTaskStep2.newInstance(bt.id, bt.buyerId).type(BuyerTaskStepType.REFUND);
            taskStep.content = BuyerTaskStepVo.newInstance().transNo(bt.transNo).toJson();
            taskStep.createTime = now;
            ss.getMapper(BuyerTaskStepMapper2.class).insert(taskStep);

            // 若管理员操作则创建备案记录，并扣除商家押金及退款保障金
            if(isAdminOperate()){
            	//将商家返款单号设置为空，用于之后的平台提现标识
                AdminRefundRecord record = AdminRefundRecord.newInstance(bt).createTime(now);
                bt.transNo = null;
                ss.getMapper(AdminRefundRecordMapper.class).insert(record);
                
                SellerPledgeRecordMapper pledgeMapper = ss.getMapper(SellerPledgeRecordMapper.class);
                SellerPledgeRecord lastPledge = pledgeMapper.selectLastRecord(bt.sellerId);
                
                //如果平台帮商家返款，则需要扣除商家的押金(扣除量等于买手垫付的资金)
                long depositAmount = BuyerTask2.findById(id).paidFee;
                //如果选择扣除商家延期押金，则总共扣除的押金包括商品价格量和5金币的延期罚金
                if(isSubtraction) {
                    depositAmount = BizConstants.SELLER_REFUND_SUBTRACTION*100 + depositAmount;
                }

                
                String memo = "推广[" + bt.getTaskNo() + "]超时未退款，平台扣除押金";
                if(isSubtraction) {
                	memo = "推广[" + bt.getTaskNo() + "]超时未退款，平台扣除押金和5元额外的超时金额";
                }
                lastPledge = SellerPledgeRecord.newInstance(bt.sellerId, lastPledge)
                    .action(PledgeAction.DEDUCT, depositAmount).taskId(bt.taskId).memo(memo).createTime(now);
                pledgeMapper.insert(lastPledge);
                
                //返款本金到买手本金记录表中（买手本金记录）
    			BuyerDepositRecordMapper depositRecordMapper = ss.getMapper(BuyerDepositRecordMapper.class);
    			BuyerDepositRecord depositRecord = depositRecordMapper.selectLastRecord(bt.buyerId);
    			depositRecord = BuyerDepositRecord.newInstance(bt.buyerId, depositRecord).taskId(bt.taskId).plus(bt.paidFee).createTime(DateTime.now().toDate());
    			depositRecord.memo = "商家返款超时，由平台退还推广[" + bt.getTaskNo() + "]垫付本金到平台本金";
    			depositRecordMapper.insert(depositRecord);
    			
                // 更新商家缓存
    			User.findByIdWichCache(bt.buyerId).deposit(depositRecord.balance).updateCache();
                User.findByIdWichCache(bt.sellerId).pledge(lastPledge.balance).updateCache();
            }
           // ss.commit();
            System.out.println("=========================执行完毕");
            System.out.println("=========================执行完毕");
            System.out.println("=========================执行完毕");
            System.out.println("=========================执行完毕");
            System.out.println("=========================执行完毕");
            System.out.println("=========================执行完毕");
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            //ss.rollback();
            renderFailedJson(ReturnCode.FAIL);
        } finally {
            ss.close();
        }
        renderSuccessJson();
    }
    
    
    
    
    /**
     * 
     * 【卖家】确认平台退款
     *
     * @param id
     * @param amount
     * @since v1.6
     * @author playlaugh
     * @created 2014年11月26日 下午5:36:12
     */
    public synchronized static void confirmSysRefund(@Required @Min(1) long id, @Required String amount) {
        validation.isTrue("amount", NumberUtils.isNumber(amount));
        handleWrongInput(false);

        User seller = getCurrentUser();
        
        BuyerTask bt = BuyerTask.findById(id);
        if (bt == null || bt.forbiddenConfirmSysRefund()) {
            log.warn("Seller={} confirm SYS_REFUND buyerTask={} forbidden, buyerTask is null or status invalid");
            renderFailedJson(ReturnCode.WRONG_INPUT);
        }
        
        // 【管理员】操作时不检查用户身份
        if (!isAdminOperate()) {
            if (bt.sellerId.longValue() != seller.id) {
                log.warn("Seller={} confirm SYS_REFUND buyerTask={} forbidden, owner invalid");
                renderFailedJson(ReturnCode.WRONG_INPUT);
            }
        }
        
        // 检查父任务是否“平台返款”任务，避免误修改数据
        Task t = Task.findById("sys_refund,item_price,item_buy_num", bt.taskId);
        if (t == null || BooleanUtils.isNotTrue(t.sysRefund)) {
            log.warn("Seller={} confirm SYS_REFUND buyerTask={} forbidden, task is null or not be sysRefund");
            renderFailedJson(ReturnCode.WRONG_INPUT);
        }
        
        // 卖家退款金额不能大于 商品单价*件数+退款保证金
        long newpaidFee = new BigDecimal(amount).movePointRight(2).longValue();
        
        if(newpaidFee < 0 ){
            log.warn("Seller={} confirm SYS_REFUND buyerTask={} forbidden, amount can't less than 0");
            renderFailedJson(ReturnCode.WRONG_INPUT);
        }
        //商家更改的价格不能超过改单任务发布金额的1.5倍
        double maxPaid = t.itemPrice * t.itemBuyNum * 1.5;
        //如果商品不包邮，则返款最大金额要加上快递十元
        if(Task.findById("is_free_shipping", bt.taskId).notFreeShipping()) {
        	maxPaid = maxPaid + BizConstants.TASK_EXPRESS_PLEDGE*100;
        }
        if (newpaidFee > maxPaid) {
            log.warn("Seller={} confirm SYS_REFUND buyerTask={} forbidden, input padiFee invalid");
            renderFailedJson(ReturnCode.OP_BIZ_LIMIT);
        }
        //判断当前商家押金是否够支付买手返款
        long price = t.itemPrice * t.itemBuyNum;
        //（如果不包邮，商家每单任务在平台上的总押金包括10元快递费押金）
        if(Task.findById("is_free_shipping", bt.taskId).notFreeShipping()) {
        	price = t.itemPrice * t.itemBuyNum + BizConstants.TASK_EXPRESS_PLEDGE*100;
        }
        //应该判断 商家账户余额与 单个任务商家在平台的押金-单个任务商家应该给买手的钱 比较
        if(seller.pledge < (newpaidFee - price)){
            log.warn("Seller={} confirm SYS_REFUND buyerTask={} forbidden, pledge is not enough");
            renderFailedJson(ReturnCode.BIZ_LIMIT);
        }
        

        // 更新任务
        log.info("Selller={} confirm SysRefund task={} with amount={}", new Object[] { seller.id, id, amount });
        Date now = DateTime.now().toDate();
        BuyerTask buyerTask = BuyerTask.instance(bt.id).status(TaskStatus.WAIT_BUYER_CONFIRM_SYS_REFUND)
            .modifyTime(now);
        SqlSession ss = SessionFactory.getSqlSession();
        try {
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
            
        } finally {
            ss.close();
        }
        
        renderSuccessJson();
    }
    
    
    
    /**
     * 
     * 【卖家】确认平台退款
     */
    public synchronized static void confirmSysRefund2(@Required @Min(1) long id, @Required String amount) {
        validation.isTrue("amount", NumberUtils.isNumber(amount));
        handleWrongInput(false);

        User seller = getCurrentUser();
        
        BuyerTask2 bt = BuyerTask2.findById(id);
        if (bt == null || bt.forbiddenConfirmSysRefund()) {
            log.warn("Seller={} confirm SYS_REFUND buyerTask={} forbidden, buyerTask is null or status invalid");
            renderFailedJson(ReturnCode.WRONG_INPUT);
        }
        
        // 【管理员】操作时不检查用户身份
        if (!isAdminOperate()) {
            if (bt.sellerId.longValue() != seller.id) {
                log.warn("Seller={} confirm SYS_REFUND buyerTask={} forbidden, owner invalid");
                renderFailedJson(ReturnCode.WRONG_INPUT);
            }
        }
        
        // 检查父任务是否“平台返款”任务，避免误修改数据
        Task2 t = Task2.findById("sys_refund,item_price,item_buy_num", bt.taskId);
        if (t == null || BooleanUtils.isNotTrue(t.sysRefund)) {
            log.warn("Seller={} confirm SYS_REFUND buyerTask={} forbidden, task is null or not be sysRefund");
            renderFailedJson(ReturnCode.WRONG_INPUT);
        }
        
        // 卖家退款金额不能大于 商品单价*件数+退款保证金
        long newpaidFee = new BigDecimal(amount).movePointRight(2).longValue();
        
        if(newpaidFee < 0 ){
            log.warn("Seller={} confirm SYS_REFUND buyerTask={} forbidden, amount can't less than 0");
            renderFailedJson(ReturnCode.WRONG_INPUT);
        }
        //商家更改的价格不能超过改单任务发布金额的1.5倍
        double maxPaid = t.itemPrice * t.itemBuyNum * 1.5;
        //如果商品不包邮，则返款最大金额要加上快递十元
        if(Task2.findById("is_free_shipping", bt.taskId).notFreeShipping()) {
        	maxPaid = maxPaid + BizConstants.TASK_EXPRESS_PLEDGE*100;
        }
        if (newpaidFee > maxPaid) {
            log.warn("Seller={} confirm SYS_REFUND buyerTask={} forbidden, input padiFee invalid");
            renderFailedJson(ReturnCode.OP_BIZ_LIMIT);
        }
        //判断当前商家押金是否够支付买手返款
        long price = t.itemPrice * t.itemBuyNum;
        //（如果不包邮，商家每单任务在平台上的总押金包括10元快递费押金）
        if(Task2.findById("is_free_shipping", bt.taskId).notFreeShipping()) {
        	price = t.itemPrice * t.itemBuyNum + BizConstants.TASK_EXPRESS_PLEDGE*100;
        }
        //应该判断 商家账户余额与 单个任务商家在平台的押金-单个任务商家应该给买手的钱 比较
        if(seller.pledge < (newpaidFee - price)){
            log.warn("Seller={} confirm SYS_REFUND buyerTask={} forbidden, pledge is not enough");
            renderFailedJson(ReturnCode.BIZ_LIMIT);
        }
        

        // 更新任务
        log.info("Selller={} confirm SysRefund task={} with amount={}", new Object[] { seller.id, id, amount });
        Date now = DateTime.now().toDate();
        BuyerTask2 buyerTask = BuyerTask2.instance(bt.id).status(TaskStatus.WAIT_BUYER_CONFIRM_SYS_REFUND)
            .modifyTime(now);
        SqlSession ss = SessionFactory.getSqlSession();
        try {
            BuyerTaskMapper2 mapper = ss.getMapper(BuyerTaskMapper2.class);
            
            // 若金额被修改则需要设置新状态
            long originPaidFee = mapper.selectById(id).paidFee;
            if (newpaidFee != originPaidFee) {
                buyerTask.paidFee(newpaidFee).status(TaskStatus.WAIT_BUYER_CONFIRM_EDITED_SYS_REFUND);
            }
            mapper.updateById(buyerTask);
            
            /*
             *  保存买手任务进度，用于买手任务详情页的显示
             */
            BuyerTaskStepMapper2 stepMapper = ss.getMapper(BuyerTaskStepMapper2.class);
            BuyerTaskStep2 confirmStep = stepMapper.selectByTaskIdAndTypeAndBuyerId(bt.id, BuyerTaskStepType.REFUND,
                bt.buyerId);
            // 若之前未进行过改操作则新建一个进度记录
            if (confirmStep == null) {
                BuyerTaskStep2 taskStep = BuyerTaskStep2.newInstance(bt.id, bt.buyerId).type(BuyerTaskStepType.REFUND)
                    .createTime(now).modifyTime(now);
                stepMapper.insert(taskStep);

                // 若之前进行过该操作，则直接更新显示标记及修改时间
            } else if (BooleanUtils.isFalse(confirmStep.isValid)) {
                stepMapper.updateById(BuyerTaskStep2.instance(confirmStep.id).valid(true).modifyTime(now));
            }
            
        } finally {
            ss.close();
        }
        
        renderSuccessJson();
    }
    
    
    
    
    
    /**
     * 
     * 【卖家】我的任务->任务详情也-导出买手任务
     *
     * @since  v1.4
     * @author youblade
     * @created 2014年11月20日 下午9:03:16
     */
    public static void exportBuyerTasks(@Required @Min(1) long taskId,boolean isSysRefundTask) {
        handleWrongInput(false);

        // check
        if (!Secure.isAdminOperate()) {
            Task task = Task.findById("status,seller_id", taskId);
            User seller = getCurrentUser();
            if (task == null || task.isNotBelongTo(seller)) {
                result("拒绝访问", "任务 [" + taskId + "] 不存在，或 不属于当前登录用户！", Module.myTask);
            }
        }

        List<BuyerTask> list = null;
        SqlSession ss = SessionFactory.getSqlSessionWithoutAutoCommit();
        try {
            list = ss.getMapper(BuyerTaskMapper.class).selectListForTaskDetailExportCsv(taskId,isSysRefundTask);
            for (BuyerTask bt : list) {
                bt.statusTitle = bt.status.title;
                if(bt.refundAccount != null){
                    bt.refundAccount.no = "财付通|" + bt.refundAccount.no;
                }
                if (bt.paidFee != null) {
                    bt.refundAmt = NumberUtils.formatToStr(bt.paidFee / 100.0f);
                }
                bt.expressCompany = "快捷快递";
            }
        } finally {
            ss.close();
        }

        String fileName = "任务" + taskId + "买手退款信息.csv";
        response.contentType = "application/x-download";
        renderBinary(ExcelUtil.buildExportFile(Config.buyerTaskXlsTemplate, list), fileName);
    }
    
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
	public static void listForTakeBuyerAccounts(Platform platform) {
		handleWrongInput(false);
		// 淘宝、天猫数据目前不分开
		if (platform == Platform.TMALL) {
			platform = Platform.TAOBAO;
		}
		BuyerAccountSearchVo vo = BuyerAccountSearchVo.newInstance().platform(platform).userId(getCurrentUser().id);
		vo.status(ExamineStatus.EXAMINED);
		renderJson(BuyerAccount.findForTakingTask(vo));

	}
	
	public static void sellerBlance() {
		renderArgs.put(BizConstants.PLATFORMS, Platform.values());
		render();
	}
	/**
	 * 
	 * 商家对账
	 *
	 * @param balanceVo
	 * @since  v2.0
	 * @author fufei
	 * @created 2015年3月2日 上午10:44:58
	 */
	public static void findBuyerTaskBalance(SellerBalanceVo vo, @Required @Param("type") String type) {
		handleWrongInput(false);
		type = StringUtils.trimToNull(type);
		vo.shopName = StringUtils.trimToNull(vo.shopName);
		vo.sellerNick=StringUtils.trimToNull(vo.sellerNick);
		User user=getCurrentUser();
		if(user!=null&&user.isSeller()){
			long sellerId = user.id;
			vo.sellerId(sellerId);
			vo.sellerNick=null;
		}
		if ("BUYERTASK".equals(type)) {
			Page<SellerBalanceVo> pageVo = BuyerTask.findBuyerTaskBalance(vo);
			for (SellerBalanceVo balanceVo : pageVo.items) {
				balanceVo.buyerTaskcalculate();
			}
			renderPageJson(pageVo.items, pageVo.totalCount);
		}
		if("TASK".equals(type)){
			Page<SellerBalanceVo> pageVo=BuyerTask.findSellerTaskBalance(vo);
			for (SellerBalanceVo balanceVo : pageVo.items) {
				balanceVo.taskcalculate();
			}
			renderPageJson(pageVo.items, pageVo.totalCount);
		}
	}
	
	/**
	 * 
	 * 后台商家对账
	 *
	 * @param balanceVo
	 * @since  v2.0
	 * @author fufei
	 * @created 2015年3月2日 上午10:44:58
	 */
	public static void findSellerTaskBalance(SellerBalanceVo vo) {
		handleWrongInput(false);
		Page<SellerBalanceVo> pageVo=BuyerTask.findSellerTaskBalance(vo);
		for (SellerBalanceVo balanceVo : pageVo.items) {
			balanceVo.buyerTaskcalculate();
		}
		renderPageJson(pageVo.items, pageVo.totalCount);
	}
	
	/**
	 * 
	 * 商家对账，导出excel
	 *
	 * @param taskId
	 * @param isSysRefundTask
	 * @since  v2.6
	 * @author fufei
	 * @created 2015年3月2日 下午5:40:13
	 */
    public static void exportSellerBlance(SellerBalanceVo vo, @Required @Param("type") String type) {
        handleWrongInput(false);
        type = StringUtils.trimToNull(type);
		vo.shopName = StringUtils.trimToNull(vo.shopName);
		vo.sellerNick=StringUtils.trimToNull(vo.sellerNick);
		User user=getCurrentUser();
		if(user!=null&&user.isSeller()){
			long sellerId = user.id;
			vo.sellerId(sellerId);
			vo.sellerNick=null;
		}
		List<SellerBalanceVo> vos=null;
		File template=null;
		String fileName="";
		if ("BUYERTASK".equals(type)) {
			template=Config.sellerBlanceXlsTemplate;
			if(vo.sellerNick!=null)
				fileName=vo.sellerNick+"子任务对账信息";
			else
				fileName="商家子任务对账信息";
			if(user!=null&&user.isSeller()){
				fileName=user.nick+"子任务对账信息";
			}
			vos=BuyerTask.exprotBuyerTaskBalance(vo);
			for (SellerBalanceVo balanceVo : vos) {
				balanceVo.buyerTaskcalculate();
				balanceVo.expressIngot=balanceVo.expressIngot/100;
				balanceVo.itemPrice=balanceVo.itemPrice/100;
				balanceVo.takeTimeStr=new SimpleDateFormat("yyyy-MM-dd hh:mm:ss").format(balanceVo.takeTime);
			}
		}
		if("TASK".equals(type)){
			if(vo.sellerNick!=null)
				fileName=vo.sellerNick+"父任务对账信息";
			else
				fileName="商家父任务对账信息";
			if(user!=null&&user.isSeller()){
				fileName=user.nick+"父任务对账信息";
			}
			template=Config.sellerTaskBlanceXlsTemplate;
			vos=BuyerTask.exprotSellerTaskBalance(vo);
			for (SellerBalanceVo balanceVo : vos) {
				balanceVo.taskcalculate();
			}
		}
		
		if(vos.size()>1000){
			renderText("当前导出数量过多!请按查询条件查询后再导出");
		}
		
        fileName +=new SimpleDateFormat("yyyy-MM-dd").format(new Date())+".csv";
        response.contentType = "application/x-download";
        renderBinary(ExcelUtil.buildExportFile(template, vos), fileName);
    }
    /**
     * 
     * 查看流量任务 
     *
     * @since  v3.2
     * @author fufei
     * @created 2015年4月13日 下午12:10:49
     */
    public static void findFlow() {
        renderArgs.put("status", TrafficStatus.values());
        render();
    }
    
    /**
     * 
     * 用户流量任务查询
     *
     * @since  v3.2
     * @author fufei
     * @created 2015年4月13日 下午2:28:53
     */
    public static void listSellerFlow(@Required TrafficRecordVo vo) {
        handleWrongInput(false);
        vo.userId=getCurrentUser().id;
        if(StringUtils.isEmpty(vo.beginTime)||DateTime.parse(vo.beginTime).isBefore(DateTime.parse("2015-04-22"))){
            vo.beginTime="2015-04-22";
        }
        Page<TrafficRecord> vos=TrafficRecord.listTrafficRecord(vo);
        renderPageJson(vos.items, vos.totalCount);
    }
}
