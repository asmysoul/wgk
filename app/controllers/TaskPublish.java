package controllers;

import java.text.MessageFormat;
import java.text.SimpleDateFormat;
import java.util.Iterator;

import models.AdminOperatorLog;
import models.AdminUser;
import models.Item;
import models.PayTradeLog;
import models.Shop;
import models.Shop2;
import models.Task;
import models.Task2;
import models.Task3;
import models.TaskItemSearchPlan;
import models.TaskItemSearchPlan3;
import models.TrafficRecord;
import models.User;
import models.AdminOperatorLog.LogType;
import models.mappers.TaskMapper;
import models.mappers.TaskMapper3;

import org.apache.commons.lang.BooleanUtils;
import org.apache.ibatis.session.SqlSession;
import org.joda.time.DateTime;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import play.data.validation.Min;
import play.data.validation.Required;
import play.mvc.With;
import vos.CancelTaskVo;
import vos.CancelTaskVo2;
import vos.CancelTaskVo3;
import vos.TaskSearchVo;
import vos.TaskSearchVo3;
import vos.TrafficRecordVo;
import vos.TrafficRecordVo.ListKwds;

import com.aton.base.BaseController;
import com.aton.base.secure.Secure;
import com.aton.config.AppMode;
import com.aton.config.BizConstants;
import com.aton.config.ReturnCode;
import com.aton.db.SessionFactory;
import com.aton.util.CollectionUtils;
import com.aton.util.MixHelper;
import com.aton.util.NumberUtils;
import com.aton.util.StringUtils;
import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.Lists;
import com.google.common.collect.Multimap;
import com.sun.org.apache.xalan.internal.xsltc.compiler.sym;

import enums.Platform;
import enums.Platform2;
import enums.Platform3;
import enums.TaskStatus;
import enums.TaskType;
import enums.TaskType2;
import enums.TaskType3;
import enums.pay.KQpayPlatform;

/**
 * 
 * 卖家发布任务.
 * 
 * @author youblade
 * @since  0.1
 * @created 2014年7月14日 下午5:55:12
 */
@With(Secure.class)
public class TaskPublish extends BaseController{

    public static final Logger log = LoggerFactory.getLogger("task");
    
    /**
     * 
     * 任务发布页面.
     *
     */
    public static void publish() {
        User user = getCurrentUser();
        log.info("User={} publish new task", user.id);
        
       
        // “审核不通过”的任务最多不能超过10个，否则不允许发布任务
        int count = 0;
        SqlSession ss = SessionFactory.getSqlSession();
        try {
            count = ss.getMapper(TaskMapper.class).count(
                TaskSearchVo.newInstance().sellerId(user.id).status(TaskStatus.NOT_PASS));
        } finally {
            ss.close();
        }
        if (count >= 10) {
            log.info("User={} has more than 10 'NOT_PASS' tasks", user.id);
            renderArgs.put(BizConstants.MSG, "您有太多“审核不通过”的任务未处理");
            renderArgs.put(BizConstants.URL, "/seller/tasks/examine/NOT_PASS");
            renderArgs.put("module", "publishTask");
            render("result.html");
        }

        setTaskRenderArgs();
        render();
    }
    
    
    
    
    /**
     * 
     * 推广发布页面.
     *
     */
    public static void publish2() {
        User user = getCurrentUser();
        log.info("User={} publish new task2", user.id);
        
       System.out.println("|||||||||||||||||||");
       System.out.println("|||||||||||||||||||");
       System.out.println("|||||||||||||||||||");
       System.out.println("|||||||||||||||||||");
       System.out.println("|||||||||||||||||||");
       System.out.println("|||||||||||||||||||");
        // “审核不通过”的任务最多不能超过10个，否则不允许发布任务
        int count = 0;
        SqlSession ss = SessionFactory.getSqlSession();
        try {
            count = ss.getMapper(TaskMapper.class).count(
                TaskSearchVo.newInstance().sellerId(user.id).status(TaskStatus.NOT_PASS));
            
        } finally {
        	 System.out.println("|||||||||||||||||||2");
             System.out.println("|||||||||||||||||||2");
             System.out.println("|||||||||||||||||||2");
             System.out.println("|||||||||||||||||||2");
             System.out.println("|||||||||||||||||||2");
             System.out.println("|||||||||||||||||||2");
            ss.close();
        }
        if (count >= 10) {
            log.info("User={} has more than 10 'NOT_PASS' tasks2", user.id);
            renderArgs.put(BizConstants.MSG, "您有太多“审核不通过”的任务未处理");
            renderArgs.put(BizConstants.URL, "/seller/tasks/examine2/NOT_PASS");
            renderArgs.put("module", "publishTask2");
            System.out.println("|||||||||||||||||||23");
            System.out.println("|||||||||||||||||||23");
            System.out.println("|||||||||||||||||||23");
            System.out.println("|||||||||||||||||||23");
            System.out.println("|||||||||||||||||||23");
            System.out.println("|||||||||||||||||||23");
            render("result.html");
        }

        setTaskRenderArgs2();
        System.out.println("+++++++++++++++++++++");
        System.out.println("+++++++++++++++++++++");
        System.out.println("+++++++++++++++++++++");
        System.out.println("+++++++++++++++++++++");
        System.out.println("+++++++++++++++++++++");
        System.out.println("+++++++++++++++++++++");
        System.out.println("+++++++++++++++++++++");
        System.out.println("+++++++++++++++++++++");
        System.out.println("+++++++++++++++++++++");
        render();
        System.out.println("+++++++++++++++++++++2");
        System.out.println("+++++++++++++++++++++2");
        System.out.println("+++++++++++++++++++++2");
        System.out.println("+++++++++++++++++++++2");
        System.out.println("+++++++++++++++++++++2");
        System.out.println("+++++++++++++++++++++");
        System.out.println("+++++++++++++++++++++");
        System.out.println("+++++++++++++++++++++");
        System.out.println("+++++++++++++++++++++");
    }
    
    
    
    
    public static void publish3() {
    	User user = getCurrentUser();
        log.info("User={} publish new task", user.id);
        
       
        // “审核不通过”的任务最多不能超过10个，否则不允许发布任务
        int count = 0;
        SqlSession ss = SessionFactory.getSqlSession();
        try {
            count = ss.getMapper(TaskMapper3.class).count(
                TaskSearchVo3.newInstance().sellerId(user.id).status(TaskStatus.NOT_PASS));
        } finally {
            ss.close();
        }
        if (count >= 10) {
            log.info("User={} has more than 10 'NOT_PASS' tasks", user.id);
            renderArgs.put(BizConstants.MSG, "您有太多“审核不通过”的任务未处理");
            renderArgs.put(BizConstants.URL, "/seller/tasks/examine/NOT_PASS");
            renderArgs.put("module", "publishTask3");
            render("result.html");
        }

        setTaskRenderArgs3();
        render();
    }
    
    
    
    
    
    
    /**
     * 
     * 重新发布任务.
     *
     * @param id
     * @since  0.1
     * @author youblade
     * @created 2014年8月17日 下午12:53:51
     */
    public static void republish(@Required @Min(1) long id) {
        handleWrongInput(false);
        
        User user = getCurrentUser();
        log.info("User={} repubish task={}", user.id, id);
        
        Task task = Task.findById(id);
        notFoundIfNull(task);
       
        task.id = null;
        task.goodCommentImg=null;
        setTaskInfo(task, user);
        setTaskRenderArgs();
        render("@publish");
    }

    
    
    /**
     * 
     * 重新发布浏览.
     */
    public static void republish3(@Required @Min(1) long id) {
        handleWrongInput(false);
        
        User user = getCurrentUser();
        log.info("User={} repubish task={}", user.id, id);
        
        Task3 task = Task3.findById(id);
        notFoundIfNull(task);
       
        task.id = null;
        task.goodCommentImg=null;
        setTaskInfo3(task, user);
        setTaskRenderArgs3();
        render("@publish3");
    }
    
    
    /**
     * 
     * 重新发布推广.
     *
     */
    public static void republish2(@Required @Min(1) long id) {
        handleWrongInput(false);
        User user = getCurrentUser();
        log.info("User={} repubish task2={}", user.id, id);
        Task2 task = Task2.findById(id);
        notFoundIfNull(task);
       
        task.id = null;
        task.goodCommentImg=null;
        setTaskInfo2(task, user);
        setTaskRenderArgs2();
        render("@publish2");
    }
    
private static void setTaskInfo2(Task2 task, User seller) {
    	
    	System.out.println("task:-总jinbi---------"+task.totalIngot);
    	System.out.println("task:-总jinbi---------"+task.totalIngot);
    	System.out.println("task:-总jinbi---------"+task.platform);
    	System.out.println("task:-总jinbi---------"+task.totalIngot);
    	System.out.println("task:-总jinbi---------"+task.toString());
    	System.out.println("task:-总jinbi---------"+task.totalIngot);
    	System.out.println("task:-总jinbi---------"+task.totalIngot);
    	
    	
    	if (task == null || task.sellerId.longValue() != seller.id) {
            notFound();
        }
        // 将任务中“商品规格”、“订单留言”转化为list，方便页面展示
        if(StringUtils.isNotBlank(task.itemProps)){
            task.itemPropsList = Lists.newArrayList(task.itemProps.split(StringUtils.COMMA));
        }
        
        System.out.println("-----------------");
        System.out.println("-----------------"+task.searchPlans.size());
        System.out.println("-----------------");
        System.out.println("-----------------");
        System.out.println("-----------------"); System.out.println("-----------------");
        
        
        // 分组“搜索关键词方案”，方便页面展示
        Multimap<Platform2, Object> map = ArrayListMultimap.create(2, 3);
        for (TaskItemSearchPlan plan : task.searchPlans) {
        	
            if (task.platform==Platform2.WEIXIN) {
            	System.out.println("WEIXIN");
            	 map.put(Platform2.WEIXIN, plan);
			}else if (task.platform==Platform2.WEIBO) {
				System.out.println("WEIBO");
				 map.put(Platform2.WEIBO, plan);
			}
			else if (task.platform==Platform2.QQ) {
				System.out.println("qq");
				 map.put(Platform2.QQ, plan);
			}else if (task.platform==Platform2.OTHER) {
				System.out.println("OTHER");
				 map.put(Platform2.OTHER, plan);
			}
           
        }
        renderArgs.put("searchPlans_" + Platform2.WEIXIN.toString(), map.get(Platform2.WEIXIN));
        renderArgs.put("searchPlans_" + Platform2.WEIBO.toString(), map.get(Platform2.WEIBO));
        renderArgs.put("searchPlans_" + Platform2.QQ.toString(), map.get(Platform2.QQ));
        renderArgs.put("searchPlans_" + Platform2.OTHER.toString(), map.get(Platform2.OTHER));
        renderArgs.put("task", task);
    }
    
		private static void setTaskInfo3(Task3 task, User seller) {
		    if (task == null || task.sellerId.longValue() != seller.id) {
		        notFound();
		    }
		    // 将任务中“商品规格”、“订单留言”转化为list，方便页面展示
		    if(StringUtils.isNotBlank(task.itemProps)){
		        task.itemPropsList = Lists.newArrayList(task.itemProps.split(StringUtils.COMMA));
		    }
		    // 分组“搜索关键词方案”，方便页面展示
		    Multimap<Platform3, Object> map = ArrayListMultimap.create(2, 3);
		    for (TaskItemSearchPlan3 plan : task.searchPlans) {
		        if (plan.inTmall) {
		            map.put(Platform3.TMALL, plan);
		            continue;
		        }else if (task.platform==Platform3.TAOBAO||task.platform==Platform3.TMALL) {
		        	 map.put(Platform3.TAOBAO, plan);
				}
		       
		    }
		    renderArgs.put("searchPlans_" + Platform3.TAOBAO.toString(), map.get(Platform3.TAOBAO));
		    renderArgs.put("searchPlans_" + Platform3.TMALL.toString(), map.get(Platform3.TMALL));
		    
		    System.out.println("======qrcode====="+task.qrcode);
		    System.out.println("======qrcode====="+task.qrcode);
		    System.out.println("======qrcode====="+task.qrcode);  System.out.println("======qrcode====="+task.qrcode);
		    System.out.println("======qrcode====="+task.qrcode);
		    
		    renderArgs.put("task", task);
		}

    
    
    private static void setTaskInfo(Task task, User seller) {
        if (task == null || task.sellerId.longValue() != seller.id) {
            notFound();
        }
        // 将任务中“商品规格”、“订单留言”转化为list，方便页面展示
        if(StringUtils.isNotBlank(task.itemProps)){
            task.itemPropsList = Lists.newArrayList(task.itemProps.split(StringUtils.COMMA));
        }
        // 分组“搜索关键词方案”，方便页面展示
        Multimap<Platform, Object> map = ArrayListMultimap.create(2, 3);
        for (TaskItemSearchPlan plan : task.searchPlans) {
            if (plan.inTmall) {
                map.put(Platform.TMALL, plan);
                continue;
            }else if (task.platform==Platform.TAOBAO||task.platform==Platform.TMALL) {
            	 map.put(Platform.TAOBAO, plan);
			}else if (task.platform==Platform.JD) {
				 map.put(Platform.JD, plan);
			}
           
        }
        renderArgs.put("searchPlans_" + Platform.TAOBAO.toString(), map.get(Platform.TAOBAO));
        renderArgs.put("searchPlans_" + Platform.TMALL.toString(), map.get(Platform.TMALL));
        renderArgs.put("searchPlans_" + Platform.JD.toString(), map.get(Platform.JD));
        renderArgs.put("task", task);
    }
    
    
    
    
    /**
     * 
     * 获取商品信息（目前仅支持淘宝、天猫、聚划算、京东）.
     *
     * @param url
     * @param p
     * @since  0.1
     * @author youblade
     * @created 2014年8月1日 下午4:03:28
     */
	public static void getItemInfo(@Required String url, @Required Platform p,TaskType taskType) {
		handleWrongInput(false);

		Item item = Item.findByUrl(url, p,taskType);
		if (item==null||StringUtils.isBlank(item.imgUrl)||StringUtils.isEmpty(item.id)) {
			renderFailedJson(ReturnCode.FAIL, "没有检测到结果,请手动输入");
		}
		renderJson(item);
	}
	
	
	  /**
     * 
     * 获取商品信息（目前仅支持淘宝、天猫、聚划算、京东）.
     */
	public static void getItemInfo3(@Required String url, @Required Platform3 p,TaskType3 taskType) {
		handleWrongInput(false);

		Item item = Item.findByUrl(url, p,taskType);
		if (item==null||StringUtils.isBlank(item.imgUrl)||StringUtils.isEmpty(item.id)) {
			renderFailedJson(ReturnCode.FAIL, "没有检测到结果,请手动输入");
		}
		renderJson(item);
	}
	
	
    /**
     * 
     * 卖家发布任务.
     *
     * @since  0.1
     * @author youblade
     * @created 2014年7月28日 下午4:46:33
     */
    public static void saveTask(@Required Task task) {
        if (task != null) {
            validation.required("task.platform", task.platform);
            validation.required("task.expressType", task.expressType);
            validation.required("task.type", task.type);
            validation.required("task.shopId", task.shopId);
            validation.min(task.shopId, 1);
            validation.isTrue("task.shopName", StringUtils.isNotBlank(task.shopName));

            validation.required("task.itemUrl", task.itemUrl);
            //          validation.url(task.itemUrl); 复杂的淘宝URL会被拦截
            validation.isTrue("task.itemTitle", StringUtils.isNotBlank(task.itemTitle));
            validation.isTrue("task.itemPicUrl", StringUtils.isNotBlank(task.itemPicUrl));
            if (task.type == TaskType.SUBWAY) {
                validation.required("task.itemSubwayPicUrl", task.itemSubwayPicUrl);
            }
            validation.isTrue("task.itemPic", StringUtils.isNotBlank(task.itemPic));
            // 价格区间：1.00~10000.00元，精确到分
            validation.range("task.itemPrice", task.itemPrice, 1, 1000000);
            validation.range("task.itemBuyNum", task.itemBuyNum, 1, 1000);
            // 搜索关键词方案
            if((task.platform==Platform.TAOBAO||task.platform==Platform.TMALL||task.platform==Platform.JD)&&task.type!=TaskType.JHS)
            validation.isTrue("task.searchPlans", CollectionUtils.isNotEmpty(task.searchPlans));
            validation.range("task.pcOrderNum", task.pcOrderNum, 0, 500);
            validation.range("task.mobileOrderNum", task.mobileOrderNum, 0, 500);
            validation.range("pcOrderNum+mobileOrderNum", task.pcOrderNum + task.mobileOrderNum, 1, 500);
            
            // 允许订单留言为空
//            validation.isTrue("task.oerderMessages", StringUtils.isNotBlank(task.orderMessages));
//            List<String> list = Lists.newArrayList(StringUtils.split(task.orderMessages,StringUtils.COMMA));
//            validation.min(list.size(), 5);
//            validation.max(list.size(), 10);
            /*
             * 增值服务
             */
//            validation.range("task.speedTaskIngot", task.speedTaskIngot, 0, 50);
            if (task.speedTaskIngot == null) {
                task.speedTaskIngot = 0;
            }
            validation.range("task.extraRewardIngot", task.extraRewardIngot, 0, 500);
            validation.range("task.publishTimerInterval", task.publishTimerInterval, 0, 5760);
            validation.range("task.publishTimerValue", task.publishTimerValue, 0, 5);
            validation.range("task.buyTimeInterval", task.buyTimeInterval, 0, 3);
            validation.range("task.expressIngot", task.expressIngot, 0,10);
            validation.range("task.delaySpan", task.delaySpan, 0, 12960);
            //TODO 优质好评关键词
        }
        handleWrongInput(false);
        User seller = getCurrentUser();
        task.sellerId = seller.id;
        task.sellerNick = seller.nick;
        task.expressIngot = task.expressIngot * 100;
		if (task.expressIngot.equals(BizConstants.TASK_EXPRESS_INGOT)) {
			task.sysExpress = true;
		} else {
			task.sysExpress = false;
			task.expressIngot=BizConstants.TASK_SELLER_EXPRESS_INGOT;
		}
       // 保存旧任务前需要检查任务状态
        if (task.id != null) {
            log.info("Check status of task={}, user={}", task.id, seller.id);
            Task taskInDb = Task.findById("status,seller_id", task.id);
            if (taskInDb != null && (taskInDb.isNotBelongTo(seller) || taskInDb.notAllowEdit())) {
                Object[] args = new Object[] { task.id, taskInDb.status, taskInDb.sellerId, seller.id };
                log.error("Save Forbidden: task={},status={},owner={}, user={}", args);
                renderFailedJson(ReturnCode.OP_BIZ_LIMIT);
            }
            log.info("Save task={} for user={}", task.id, seller.id);
        }
        
        log.info("Create new task for user={}", seller.id);
        //发布任务流量处理
        long totalFlowNum=0;
        if (CollectionUtils.isNotEmpty(task.searchPlans)) {
            for (TaskItemSearchPlan plan : task.searchPlans) {
                // 跳过关键词为空的记录
                if (plan == null || StringUtils.isBlank(plan.word)) {
                    continue;
                }
                totalFlowNum+=plan.flowNum;
            }
            if(seller.flow<totalFlowNum){
            	renderFailedJson(ReturnCode.BIZ_LIMIT, "您目前流量不足已支付，请先充值后再发布或者调整关键词对应的访客后再发布！");
            }
        }
        if(task.expressWeight!=null && task.expressWeight<=0 || task.expressWeight!=null && task.expressWeight>500 || task.expressWeight==null) {
        	renderFailedJson(ReturnCode.BIZ_LIMIT,"请填写正确的快递重量！");
        }
        Shop shop = Shop.selectById(task.shopId);
        if(shop==null || shop.mobile==null || shop.sellerName==null||shop.street==null || shop.branch==null) {
        	renderFailedJson(ReturnCode.BIZ_LIMIT, "店铺不存在或店铺信息不完善,请检查后重试！");
        }
        if(StringUtils.isNotEmpty(task.goodCommentImg)&&task.totalOrderNum>1){
            renderFailedJson(ReturnCode.BIZ_LIMIT, "需要上传图片评价的任务只能发布一单！");
        }
        task.itemId=Task.getItemIdByPlatform(task.platform,task.itemUrl);
        System.out.println("----------task.itemId------------"+task.itemId);
        System.out.println("----------task.itemId------------"+task.itemId);
        System.out.println("----------task.itemId------------"+task.itemId);
        System.out.println("----------task.itemId------------"+task.itemId);
        System.out.println("----------task.itemId------------"+task.itemId);
        if(StringUtils.isEmpty(task.itemId)){
            renderFailedJson(ReturnCode.BIZ_LIMIT, "请检查商品链接是否正确！");
        }
        try {
            task.save();  
        } catch (Exception e) {
            renderFailedJson(ReturnCode.BIZ_LIMIT, "任务保存失败！");
        }
        
        renderJson(String.valueOf(task.id));
    }
    
    
    
    /**
     * 
     * 卖家发布浏览.
     */
    public static void saveTask3(@Required Task3 task) {
        if (task != null) {
            validation.required("task.platform", task.platform);
           // validation.required("task.expressType", task.expressType);
            validation.required("task.type", task.type);
            validation.required("task.shopId", task.shopId);
            validation.min(task.shopId, 1);
            validation.isTrue("task.shopName", StringUtils.isNotBlank(task.shopName));
//
            validation.required("task.itemUrl", task.itemUrl);
  //                    validation.url(task.itemUrl); 复杂的淘宝URL会被拦截
            validation.isTrue("task.itemTitle", StringUtils.isNotBlank(task.itemTitle));
            validation.isTrue("task.itemPicUrl", StringUtils.isNotBlank(task.itemPicUrl));
            if (task.type == TaskType3.SUBWAY) {
                validation.required("task.itemSubwayPicUrl", task.itemSubwayPicUrl);
            }
            if(task.type == TaskType3.JHS){
            	validation.required("task.Qrcode", task.qrcode);
            }
            validation.isTrue("task.itemPic", StringUtils.isNotBlank(task.itemPic));
            // 价格区间：1.00~10000.00元，精确到分
           // validation.range("task.itemBuyNum", task.itemBuyNum, 1, 1000);
            // 搜索关键词方案
            if((task.platform==Platform3.TAOBAO||task.platform==Platform3.TMALL)&&task.type!=TaskType3.JHS)
           // validation.isTrue("task.searchPlans", CollectionUtils.isNotEmpty(task.searchPlans));
            validation.range("task.pcOrderNum", task.pcOrderNum, 0, 500);
            validation.range("task.mobileOrderNum", task.mobileOrderNum, 0, 500);
            validation.range("pcOrderNum+mobileOrderNum", task.pcOrderNum + task.mobileOrderNum, 1, 500);
            
          
            if (task.speedTaskIngot == null) {
                task.speedTaskIngot = 0;
            }
            validation.range("task.extraRewardIngot", task.extraRewardIngot, 0, 500);
            validation.range("task.publishTimerInterval", task.publishTimerInterval, 0, 5760);
            validation.range("task.publishTimerValue", task.publishTimerValue, 0, 5);
           
            validation.range("task.delaySpan", task.delaySpan, 0, 12960);
            //TODO 优质好评关键词
        }
        handleWrongInput(false);
        User seller = getCurrentUser();
        task.sellerId = seller.id;
        task.sellerNick = seller.nick;
      
       // 保存旧任务前需要检查任务状态
        if (task.id != null) {
            log.info("Check status of task={}, user={}", task.id, seller.id);
            Task3 taskInDb = Task3.findById("status,seller_id", task.id);
            if (taskInDb != null && (taskInDb.isNotBelongTo(seller) || taskInDb.notAllowEdit())) {
                Object[] args = new Object[] { task.id, taskInDb.status, taskInDb.sellerId, seller.id };
                log.error("Save Forbidden: task={},status={},owner={}, user={}", args);
                renderFailedJson(ReturnCode.OP_BIZ_LIMIT);
            }
            log.info("Save task={} for user={}", task.id, seller.id);
        }
        
        log.info("Create new task for user={}", seller.id);
        //发布任务流量处理
        long totalFlowNum=0;
        if (CollectionUtils.isNotEmpty(task.searchPlans)) {
            for (TaskItemSearchPlan3 plan : task.searchPlans) {
                // 跳过关键词为空的记录
                if (plan == null || StringUtils.isBlank(plan.word)) {
                    continue;
                }
                totalFlowNum+=plan.flowNum;
            }
            if(seller.flow<totalFlowNum){
            	renderFailedJson(ReturnCode.BIZ_LIMIT, "您目前流量不足已支付，请先充值后再发布或者调整关键词对应的访客后再发布！");
            }
        }

        if(StringUtils.isNotEmpty(task.goodCommentImg)&&task.totalOrderNum>1){
            renderFailedJson(ReturnCode.BIZ_LIMIT, "需要上传图片评价的任务只能发布一单！");
        }
        if(!"JHS".equals(task.type.toString())){
        	 task.itemId=Task3.getItemIdByPlatform(task.platform,task.itemUrl);
             if(StringUtils.isEmpty(task.itemId)){
                 renderFailedJson(ReturnCode.BIZ_LIMIT, "请检查商品链接是否正确！");
             }
        }
        try {
        	System.out.println("==============执行保存");
            task.save();
        } catch (Exception e) {
            renderFailedJson(ReturnCode.BIZ_LIMIT, "任务保存失败！");
        }
        
        renderJson(String.valueOf(task.id));
    }
    
    
    
    
    
    
    
    /**
     * 
     * 卖家发布推广.
     *
     * @since  0.1
     * @author youblade
     * @created 2014年7月28日 下午4:46:33
     */
    public static void saveTask2(@Required Task2 task) {
    	System.out.println("卖家开始发布推广");
    	System.out.println("卖家开始发布推广");
    	System.out.println("卖家开始发布推广");
    	System.out.println("卖家开始发布推广");
    	System.out.println("卖家开始发布推广");
    	System.out.println("卖家开始发布推广");
    	System.out.println("卖家开始发布推广");
    	System.out.println("卖家开始发布推广");
    	System.out.println("卖家开始发布推广");
    	System.out.println("卖家开始发布推广");
        if (task != null) {
            validation.required("task.platform", task.platform);
           // validation.required("task.expressType", task.expressType);
            validation.required("task.type", task.type);
            validation.required("task.shopId", task.shopId);
            validation.min(task.shopId, 1);
            validation.isTrue("task.shopName", StringUtils.isNotBlank(task.shopName));

           // validation.required("task.itemUrl", task.itemUrl);
            validation.isTrue("task.itemTitle", StringUtils.isNotBlank(task.itemTitle));
            //validation.isTrue("task.itemPicUrl", StringUtils.isNotBlank(task.itemPicUrl));
            
           // validation.isTrue("task.itemPic", StringUtils.isNotBlank(task.itemPic));
            //validation.range("task.pcOrderNum", task.pcOrderNum, 0, 500);
            
            validation.range("task.mobileOrderNum", task.mobileOrderNum, 0, 500);
            System.out.println("task.mobileOrderNum--------"+task.mobileOrderNum);
            System.out.println("task.mobileOrderNum--------"+task.mobileOrderNum);
            System.out.println("itemSubwayPicUrl---------------------"+task.itemSubwayPicUrl);
            
            //validation.range("pcOrderNum+mobileOrderNum", task.pcOrderNum + task.mobileOrderNum, 1, 500);
            
            validation.range("task.extraRewardIngot", task.extraRewardIngot, 0, 500);
            validation.range("task.publishTimerInterval", task.publishTimerInterval, 0, 5760);
            validation.range("task.publishTimerValue", task.publishTimerValue, 0, 5);
            validation.range("task.buyTimeInterval", task.buyTimeInterval, 0, 3);
            validation.range("task.delaySpan", task.delaySpan, 0, 12960);
            //TODO 优质好评关键词
        }
        
      
        handleWrongInput(false);
        User seller = getCurrentUser();
        task.sellerId = seller.id;
        task.sellerNick = seller.nick;
       // 保存旧任务前需要检查任务状态
        if (task.id != null) {
            log.info("Check status of task={}, user={}", task.id, seller.id);
            Task2 taskInDb = Task2.findById("status,seller_id", task.id);
            
            if (taskInDb != null && (taskInDb.isNotBelongTo(seller) || taskInDb.notAllowEdit())) {
                Object[] args = new Object[] { task.id, taskInDb.status, taskInDb.sellerId, seller.id };
                log.error("Save Forbidden: task={},status={},owner={}, user={}", args);
                renderFailedJson(ReturnCode.OP_BIZ_LIMIT);
            }
            log.info("Save task={} for user={}", task.id, seller.id);
        }
      
        log.info("Create new task for user={}", seller.id);
        //发布任务流量处理
        long totalFlowNum=0;
        if (CollectionUtils.isNotEmpty(task.searchPlans)) {
            for (TaskItemSearchPlan plan : task.searchPlans) {
                // 跳过关键词为空的记录
                if (plan == null || StringUtils.isBlank(plan.word)) {
                    continue;
                }
                totalFlowNum+=plan.flowNum;
            }
            if(seller.flow<totalFlowNum){
            	renderFailedJson(ReturnCode.BIZ_LIMIT, "您目前流量不足已支付，请先充值后再发布或者调整关键词对应的访客后再发布！");
            }
        }
     
        Shop2 shop = Shop2.selectById(task.shopId);
        if(shop==null || shop.mobile==null || shop.sellerName==null||shop.street==null ) {
        	renderFailedJson(ReturnCode.BIZ_LIMIT, "店铺不存在或店铺信息不完善,请检查后重试！");
        }
        if(StringUtils.isNotEmpty(task.goodCommentImg)&&task.totalOrderNum>1){
            renderFailedJson(ReturnCode.BIZ_LIMIT, "需要上传图片评价的任务只能发布一单！");
        }
        task.itemId=Task2.getItemIdByPlatform(task.platform,task.itemUrl);
        
        System.out.println("----------task.itemId------------"+task.itemId);
        System.out.println("----------task.itemId------------"+task.itemId);
        System.out.println("----------task.itemId------------"+task.itemId);
        System.out.println("----------task.itemId------------"+task.itemId);
        System.out.println("----------task.itemId------------"+task.itemId);
        /*if(StringUtils.isEmpty(task.itemId)){
            renderFailedJson(ReturnCode.BIZ_LIMIT, "请检查商品链接是否正确！");
        }*/
        
        
        
        
        
        try {
            task.save();
        } catch (Exception e) {
            renderFailedJson(ReturnCode.BIZ_LIMIT, "任务保存失败！");
        }
        
        renderJson(String.valueOf(task.id));
    }
    
    
    
    /**
     * 
     * 发布浏览->第五步支付：为发布的任务付款.
     */
    public synchronized static void confirmPayment3(@Required @Min(1) String tidStr, boolean useIngot, boolean usePledge, boolean other) {
        // 至少选择一种支付方式
        validation.isTrue("useIngot or usePledge", useIngot || usePledge || other);
        
        // 为简化业务流程，平台自身的虚拟货币支付方式不能与第三方支付方式混用
        if(other){
            validation.isTrue("useIngot", !useIngot);
            validation.isTrue("usePledge", !usePledge);
        }
        handleWrongInput(false);
        
        User user = getCurrentUser();
        log.info("User={} pay for task={}", user.id, tidStr);
        
        // 从DB获取任务数据，计算费用
        Task3 task = Task3.findById(NumberUtils.toLong(tidStr));
        if (BooleanUtils.isTrue(task.isPaid)
            || (task.status != TaskStatus.WAIT_EDIT && task.status != TaskStatus.WAITING_PAY)) {
            log.warn("Task={} isPaid, User={}", tidStr, user.id);
            renderFailedJson(ReturnCode.OP_BIZ_LIMIT, "该任务已支付过");
        }
        
        // 检查用户选择的支付方式是否足够支付本次任务的费用
        long payFee = (task.totalIngot + task.totalPledge)/10;
        System.out.println("支付金额---------------"+payFee);
        System.out.println("支付金额---------------"+payFee);
        System.out.println("支付金额---------------"+payFee);
        System.out.println("支付金额---------------"+payFee);
        System.out.println("支付金额---------------"+payFee);
        System.out.println("支付金额---------------"+payFee);
        
        boolean flag = useIngot && usePledge && (user.pledge.longValue() + user.ingot.longValue()) < payFee;
        boolean flag2 = useIngot && !usePledge && user.ingot.longValue() < payFee;
        boolean flag3 = usePledge && !useIngot && user.pledge.longValue() < payFee;
        if (flag || flag2 || flag3) {
            renderFailedJson(ReturnCode.BIZ_LIMIT, "您选择的支付方式，余额不足以支付本次任务的费用");
        }
        
        // 使用第三方支付平台
        if(other){
            if (AppMode.get().testPay) {
//                payFee = 100;
            }
            
            long tradeLogId = PayTradeLog.findOrCreate3(task, payFee);
            Object[] args = new Object[] { user.id, tidStr, payFee, tradeLogId };
            log.info("User={} pay task={} with amount={} with BANK, and pay_trade_log={}", args);
            
            renderJson(tradeLogId);
        }
        
        // 使用押金及金币支付
        Object[] args = new Object[] { user.id, tidStr, payFee, useIngot, usePledge };
        log.info("User={} pay task={} with amount={}, useIngot={},usePledge={}", args);
        task.pay(payFee, useIngot, usePledge);
        renderSuccessJson();
    }
    
    
    
    
    
    /**
     * 
     * 发布任务->第五步支付：为发布的任务付款.
     *
     * @since  0.1
     * @author youblade
     * @created 2014年8月15日 下午8:02:30
     */
    public synchronized static void confirmPayment(@Required @Min(1) String tidStr, boolean useIngot, boolean usePledge, boolean other) {
        // 至少选择一种支付方式
        validation.isTrue("useIngot or usePledge", useIngot || usePledge || other);
        // 为简化业务流程，平台自身的虚拟货币支付方式不能与第三方支付方式混用
        if(other){
            validation.isTrue("useIngot", !useIngot);
            validation.isTrue("usePledge", !usePledge);
        }
        handleWrongInput(false);
        User user = getCurrentUser();
        log.info("User={} pay for task={}", user.id, tidStr);
        System.out.println("-----------"+tidStr);
        System.out.println("-----------"+tidStr);
        // 从DB获取任务数据，计算费用
        Task task = Task.findById(NumberUtils.toLong(tidStr));
        if (BooleanUtils.isTrue(task.isPaid)
            || (task.status != TaskStatus.WAIT_EDIT && task.status != TaskStatus.WAITING_PAY)) {
            log.warn("Task={} isPaid, User={}", tidStr, user.id);
            renderFailedJson(ReturnCode.OP_BIZ_LIMIT, "该任务已支付过");
        }
        
        // 检查用户选择的支付方式是否足够支付本次任务的费用
        long payFee = task.totalIngot + task.totalPledge;
        
        boolean flag = useIngot && usePledge && (user.pledge.longValue() + user.ingot.longValue()) < payFee;
        boolean flag2 = useIngot && !usePledge && user.ingot.longValue() < payFee;
        boolean flag3 = usePledge && !useIngot && user.pledge.longValue() < payFee;
        if (flag || flag2 || flag3) {
            renderFailedJson(ReturnCode.BIZ_LIMIT, "您选择的支付方式，余额不足以支付本次任务的费用");
        }
        
        // 使用第三方支付平台
        if(other){
            if (AppMode.get().testPay) {
//                payFee = 100;
            }
            
            long tradeLogId = PayTradeLog.findOrCreate(task, payFee);
            Object[] args = new Object[] { user.id, tidStr, payFee, tradeLogId };
            log.info("User={} pay task={} with amount={} with BANK, and pay_trade_log={}", args);
            
            renderJson(tradeLogId);
        }
        
        // 使用押金及金币支付
        Object[] args = new Object[] { user.id, tidStr, payFee, useIngot, usePledge };
        log.info("User={} pay task={} with amount={}, useIngot={},usePledge={}", args);
        task.pay(payFee, useIngot, usePledge);
        
        renderSuccessJson();
    }
    
    
    
    
    
    
    
    
    /**
     * 
     * 发布推广->第五步支付：为发布的推广付款.
     *
     * @since  0.1
     * @author youblade
     * @created 2014年8月15日 下午8:02:30
     */
    public synchronized static void confirmPayment2(@Required @Min(1) String tidStr, boolean useIngot, boolean usePledge, boolean other) {
    	 System.out.println(" 从DB获取任务数据，计算费用");
         System.out.println(" 从DB获取任务数据，计算费用");
         System.out.println(" 从DB获取任务数据，计算费用");
         System.out.println(" 从DB获取任务数据，计算费用");
         System.out.println(" 从DB获取任务数据，计算费用");
         System.out.println(" 从DB获取任务数据，计算费用");
         System.out.println(" 从DB获取任务数据，计算费用");
         System.out.println(" 从DB获取任务数据，计算费用");
         System.out.println(" 从DB获取任务数据，计算费用");
         System.out.println(" 从DB获取任务数据，计算费用");
         System.out.println(" 从DB获取任务数据，计算费用1");
         System.out.println(" 从DB获取任务数据，计算费用1");
         System.out.println(" 从DB获取任务数据，计算费用1");
         System.out.println(" 从DB获取任务数据，计算费用1");
    	
    	// 至少选择一种支付方式
        validation.isTrue("useIngot or usePledge", useIngot || usePledge || other);
        
        // 为简化业务流程，平台自身的虚拟货币支付方式不能与第三方支付方式混用
        if(other){
            validation.isTrue("useIngot", !useIngot);
            validation.isTrue("usePledge", !usePledge);
        }
        handleWrongInput(false);
        
        User user = getCurrentUser();
        log.info("User={} pay for task={}", user.id, tidStr);
        
        // 从DB获取任务数据，计算费用
        Task2 task = Task2.findById(NumberUtils.toLong(tidStr));
        
        System.out.println(" 从DB获取任务数据，计算费用");
        System.out.println(" 从DB获取任务数据，计算费用");
        System.out.println(" 从DB获取任务数据，计算费用");
        System.out.println(" 从DB获取任务数据，计算费用");
        System.out.println(" 从DB获取任务数据，计算费用");
        System.out.println(" 从DB获取任务数据，计算费用");
        System.out.println(" 从DB获取任务数据，计算费用");
        if (BooleanUtils.isTrue(task.isPaid)
            || (task.status != TaskStatus.WAIT_EDIT && task.status != TaskStatus.WAITING_PAY)) {
            log.warn("Task={} isPaid, User={}", tidStr, user.id);
            renderFailedJson(ReturnCode.OP_BIZ_LIMIT, "该任务已支付过");
        }
        System.out.println(" 检查用户选择的支付方式是否足够支付本次任务的费用");
        System.out.println(" 检查用户选择的支付方式是否足够支付本次任务的费用");
        System.out.println(" 检查用户选择的支付方式是否足够支付本次任务的费用");
        System.out.println(" 检查用户选择的支付方式是否足够支付本次任务的费用");
        System.out.println(" 检查用户选择的支付方式是否足够支付本次任务的费用");
        System.out.println(" 检查用户选择的支付方式是否足够支付本次任务的费用");
        System.out.println(" 检查用户选择的支付方式是否足够支付本次任务的费用");
        System.out.println(" 检查用户选择的支付方式是否足够支付本次任务的费用");
        // 检查用户选择的支付方式是否足够支付本次任务的费用
        long payFee = task.totalIngot + task.totalPledge;
        System.out.println("检查用户选择的支付方式是否足够支付本次任务的费用"+payFee);
        System.out.println("检查用户选择的支付方式是否足够支付本次任务的费用"+payFee);
        System.out.println("检查用户选择的支付方式是否足够支付本次任务的费用"+payFee);
        System.out.println("检查用户选择的支付方式是否足够支付本次任务的费用"+payFee);
        System.out.println("检查用户选择的支付方式是否足够支付本次任务的费用"+payFee);
        System.out.println("检查用户选择的支付方式是否足够支付本次任务的费用"+payFee);
        System.out.println("检查用户选择的支付方式是否足够支付本次任务的费用"+payFee);
        System.out.println("检查用户选择的支付方式是否足够支付本次任务的费用"+payFee);
        
        boolean flag = useIngot && usePledge && (user.pledge.longValue() + user.ingot.longValue()) < payFee;
        boolean flag2 = useIngot && !usePledge && user.ingot.longValue() < payFee;
        boolean flag3 = usePledge && !useIngot && user.pledge.longValue() < payFee;
        if (flag || flag2 || flag3) {
            renderFailedJson(ReturnCode.BIZ_LIMIT, "您选择的支付方式，余额不足以支付本次任务的费用");
        }
        System.out.println(" 使用第三方支付平台");
        System.out.println(" 使用第三方支付平台");
        System.out.println(" 使用第三方支付平台");
        System.out.println(" 使用第三方支付平台");
        System.out.println(" 使用第三方支付平台");
        System.out.println(" 使用第三方支付平台");
        // 使用第三方支付平台
        if(other){
            if (AppMode.get().testPay) {
//                payFee = 100;
            }
            
            long tradeLogId = PayTradeLog.findOrCreate2(task, payFee);
            Object[] args = new Object[] { user.id, tidStr, payFee, tradeLogId };
            log.info("User={} pay task={} with amount={} with BANK, and pay_trade_log={}", args);
            
            renderJson(tradeLogId);
        }
        System.out.println(" 使用押金及金币支付");
        System.out.println(" 使用押金及金币支付");
        System.out.println(" 使用押金及金币支付");
        System.out.println(" 使用押金及金币支付");
        System.out.println(" 使用押金及金币支付");
        System.out.println(" 使用押金及金币支付");
        System.out.println(" 使用押金及金币支付");
        // 使用押金及金币支付
        Object[] args = new Object[] { user.id, tidStr, payFee, useIngot, usePledge };
        log.info("User={} pay task={} with amount={}, useIngot={},usePledge={}", args);
        task.pay(payFee, useIngot, usePledge);
        
        renderSuccessJson();
    }
    
    
    
    
    
    
    
    
    
    
    
    /**
     * 
     * 1.卖家[我的任务]->编辑“待编辑”任务（第1步）
     * 2.卖家[我的任务]/[平台审核中]->编辑“审核不通过”任务（第1步）
     * 3.卖家[我的任务]->继续支付“待支付”任务（待支付步骤）
     * 
     * @since 0.1
     * @author youblade
     * @created 2014年8月17日 上午11:48:14
     */
    public static void edit(Long id) {
        User seller = getCurrentUser();
        log.info("User={} edit task={}", seller.id, id);

        Task task = Task.findById(id);

        // 修改任务时，只有“审核不通过”的才允许修改（简化流程）
        if (task == null) {
            result("出现错误", "任务 [" + id + "] 不存在！", Module.myTask);
        }
        if (task.isNotBelongTo(seller)) {
            result("拒绝访问", "任务 [" + id + "] 不属于当前登录用户！", Module.myTask);
        }
        if (task.notAllowEdit() && task.status != TaskStatus.WAITING_PAY) {
            result("拒绝访问", MessageFormat.format("任务 [{0}] 的状态为“{1}”，不允许进行编辑！", id.toString(), task.status.title),
                Module.myTask);
        }

        // 编辑“审核不通过”的任务时要将其状态置为“待编辑”
        if (task.status == TaskStatus.NOT_PASS) {
            Task.instance(task.id).setStatus(TaskStatus.WAIT_EDIT).simpleUpdate();
        }
        setTaskInfo(task, seller);
        setTaskRenderArgs();
        render("@publish");
    }

    
    /**
     * 
     * 1.卖家[我的任务]->编辑“待编辑”任务（第1步）
     * 2.卖家[我的任务]/[平台审核中]->编辑“审核不通过”任务（第1步）
     * 3.卖家[我的任务]->继续支付“待支付”任务（待支付步骤）
     */
    public static void edit3(Long id) {
        User seller = getCurrentUser();
        log.info("User={} edit task={}", seller.id, id);

        Task3 task = Task3.findById(id);

        // 修改任务时，只有“审核不通过”的才允许修改（简化流程）
        if (task == null) {
            result("出现错误", "任务 [" + id + "] 不存在！", Module.myTask);
        }
        if (task.isNotBelongTo(seller)) {
            result("拒绝访问", "任务 [" + id + "] 不属于当前登录用户！", Module.myTask);
        }
        if (task.notAllowEdit() && task.status != TaskStatus.WAITING_PAY) {
            result("拒绝访问", MessageFormat.format("任务 [{0}] 的状态为“{1}”，不允许进行编辑！", id.toString(), task.status.title),
                Module.myTask);
        }

        // 编辑“审核不通过”的任务时要将其状态置为“待编辑”
        if (task.status == TaskStatus.NOT_PASS) {
            Task3.instance(task.id).setStatus(TaskStatus.WAIT_EDIT).simpleUpdate();
        }
        setTaskInfo3(task, seller);
        setTaskRenderArgs3();
        render("@publish3");
    }
    
    
    
    public static void edit2(Long id) {
        User seller = getCurrentUser();
        log.info("User={} edit task={}", seller.id, id);
        System.out.println("id------------"+id);
        System.out.println("id------------"+id);
        System.out.println("id------------"+id);
        System.out.println("id------------"+id);
        System.out.println("id------------"+id);
        System.out.println("id------------"+id);
        Task2 task = Task2.findById(id);

        // 修改任务时，只有“审核不通过”的才允许修改（简化流程）
        if (task == null) {
            result("出现错误", "任务 [" + id + "] 不存在！", Module.myTask);
        }
        if (task.isNotBelongTo(seller)) {
            result("拒绝访问", "任务 [" + id + "] 不属于当前登录用户！", Module.myTask);
        }
        if (task.notAllowEdit() && task.status != TaskStatus.WAITING_PAY) {
            result("拒绝访问", MessageFormat.format("任务 [{0}] 的状态为“{1}”，不允许进行编辑！", id.toString(), task.status.title),
                Module.myTask);
        }

        // 编辑“审核不通过”的任务时要将其状态置为“待编辑”
        if (task.status == TaskStatus.NOT_PASS) {
            Task2.instance(task.id).setStatus(TaskStatus.WAIT_EDIT).simpleUpdate();
        }
        setTaskInfo2(task, seller);
        setTaskRenderArgs2();
        render("@publish2");
    }
    
    
    
    
    
    
    
    
    
    
    
    private static void setTaskRenderArgs() {
        renderArgs.put("Platform", Platform.class);
        renderArgs.put("TaskType", TaskType.class);
        renderArgs.put(BizConstants.PAY_PLATFORMS, KQpayPlatform.values());
    }
    
    
    private static void setTaskRenderArgs2() {
        renderArgs.put("Platform2", Platform2.class);
        renderArgs.put("TaskType2", TaskType2.class);
        renderArgs.put(BizConstants.PAY_PLATFORMS, KQpayPlatform.values());
    }
    
    private static void setTaskRenderArgs3() {
        renderArgs.put("Platform", Platform3.class);
        renderArgs.put("TaskType", TaskType3.class);
        renderArgs.put(BizConstants.PAY_PLATFORMS, KQpayPlatform.values());
    }
    
    

    /**
     * 
     * 我的任务->加速买手做完成.
     *
     * @param t
     * @since  0.1
     * @author youblade
     * @created 2014年8月28日 下午6:00:21
     */
    public static void speedPerform(@Required Task t) {
        if (t != null) {
            validation.required(t.id);
            validation.range(t.speedTaskIngot, 10, 100);
            validation.range(t.extraRewardIngot, 0f, 100f);
        }
        handleWrongInput(false);

        Task task = Task.findById("seller_id,status", t.id);
        if (task == null || task.sellerId.intValue() != getCurrentUser().id || task.isUnmodifiable()) {
            handleIllegalRequest(ReturnCode.WRONG_INPUT);
        }

        // 新建一个实例，避免前端传入非法参数修改其他字段
        Task.instance(t.id).speedTaskIngot(t.speedTaskIngot).extraRewardIngot(t.extraRewardIngot).save();
        renderSuccessJson();
    }
    
    
    /**
     * 
     * 【卖家】我的任务->取得将要撤销的任务信息
     * 
     * @param id
     * @since v0.1
     * @author moloch
     * @created 2014-9-22 下午3:35:18
     */
    public static void fetchCancelTaskInfo(@Required long id) {
        handleWrongInput(false);
        
        checkBeforeCancel(id);

        CancelTaskVo vo = Task.findCancelInfo(id);
        renderJson(vo);
    }
    
    
    
    
    /**
     * 
     * 【卖家】我的任务->取得将要撤销的任务信息
     */
    public static void fetchCancelTaskInfo3(@Required long id) {
        handleWrongInput(false);
        checkBeforeCancel3(id);
        CancelTaskVo3 vo = Task3.findCancelInfo(id);
        renderJson(vo);
    }
    
    
    
    /**
     * 
     * 【卖家】我的推广->取得将要撤销的推广信息
     * 
     * @param id
     * @since v0.1
     * @author 尤齐春
     * @created 2016-8-4 下午
     */
    public static void fetchCancelTaskInfo2(@Required long id) {
        handleWrongInput(false);
        checkBeforeCancel2(id);
        CancelTaskVo2 vo = Task2.findCancelInfo(id);
        renderJson(vo);
    }

    /**
     * 
     * 【卖家】我的任务->撤销未接单任务
     * 
     * @since v0.1
     * @author moloch
     * @created 2014-9-20 下午5:20:12
     */
    public static void cancel(@Required long id) {
        handleWrongInput(false);
        checkBeforeCancel(id);
        
        Task.cancledAndRefund(id);
        if(request.url.startsWith("/admin")) {
        	String adminAccount = AdminUser.findByIdWichCache(Long.valueOf(Long.parseLong(session.get("admin-authcode")))).name;
        	String message = MixHelper.format("任务编号{}", id);
        	AdminOperatorLog.insert(adminAccount, LogType.CANCEL_TASK, message);
        }
        renderSuccessJson();
    }
    
    
    /**
     * 【卖家】我的浏览->撤销未接单浏览
     */
    public static void cancel3(@Required long id) {
        handleWrongInput(false);
        checkBeforeCancel(id);
        
        Task3.cancledAndRefund(id);
        if(request.url.startsWith("/admin")) {
        	String adminAccount = AdminUser.findByIdWichCache(Long.valueOf(Long.parseLong(session.get("admin-authcode")))).name;
        	String message = MixHelper.format("任务编号{}", id);
        	AdminOperatorLog.insert(adminAccount, LogType.CANCEL_TASK, message);
        }
        renderSuccessJson();
    }
    
    

    /**
     * 
     * 【卖家】我的推广->撤销未接单推广
     * 
     * @since v0.1
     * @author moloch
     * @created 2014-9-20 下午5:20:12
     */
    public static void cancel2(@Required long id) {
        handleWrongInput(false);
        checkBeforeCancel2(id);
        
        Task2.cancledAndRefund(id);
        if(request.url.startsWith("/admin")) {
        	String adminAccount = AdminUser.findByIdWichCache(Long.valueOf(Long.parseLong(session.get("admin-authcode")))).name;
        	String message = MixHelper.format("推广编号{}", id);
        	AdminOperatorLog.insert(adminAccount, LogType.CANCEL_TASK, message);
        }
        renderSuccessJson();
    }
    
    
    /*
     * 撤销前检查
     */
    private static void checkBeforeCancel(long id) {
        Task3 task = Task3.findById("status,seller_id,total_order_num,pc_taken_count,mobile_taken_count", id);
        User seller = User.findById(task.sellerId);
        if (task == null || task.isNotBelongTo(seller)) {
            renderFailedJson(ReturnCode.WRONG_INPUT);
        }
        // 只有发布状态(定时任务为“待发布”)下的任务可以撤销
        if (task.status != TaskStatus.PUBLISHED && task.status != TaskStatus.WAIT_PUBLISH) {
            renderFailedJson(ReturnCode.WRONG_INPUT);
        }
    }
    
    
    /*
     * 撤销前检查
     */
    private static void checkBeforeCancel3(long id) {
        Task3 task = Task3.findById("status,seller_id,total_order_num,pc_taken_count,mobile_taken_count", id);
        User seller = User.findById(task.sellerId);
        if (task == null || task.isNotBelongTo(seller)) {
            renderFailedJson(ReturnCode.WRONG_INPUT);
        }
        // 只有发布状态(定时任务为“待发布”)下的任务可以撤销
        if (task.status != TaskStatus.PUBLISHED && task.status != TaskStatus.WAIT_PUBLISH) {
            renderFailedJson(ReturnCode.WRONG_INPUT);
        }
    }
    
    
    
    private static void checkBeforeCancel2(long id) {
        Task2 task = Task2.findById("status,seller_id,total_order_num,pc_taken_count,mobile_taken_count", id);
        User seller = User.findById(task.sellerId);
        if (task == null || task.isNotBelongTo(seller)) {
            renderFailedJson(ReturnCode.WRONG_INPUT);
        }
        // 只有发布状态(定时任务为“待发布”)下的任务可以撤销
        if (task.status != TaskStatus.PUBLISHED && task.status != TaskStatus.WAIT_PUBLISH) {
            renderFailedJson(ReturnCode.WRONG_INPUT);
        }
    }
    
    
    
    /**
     * 
     * 发布流量任务
     *
     * @since  v2.9
     * @author fufei
     * @created 2015年4月10日 上午11:30:23
     */
    public static void publishFlow() {
        setTaskRenderArgs();
        render();
    }
    
    
    /**
     * 保存流量
     * @since  v3.2
     * @author fufei
     * @created 2015年4月10日 下午2:57:49
     */
    public synchronized static void saveFlow(@Required TrafficRecordVo vo,@Required Platform platform,@Required String url) {
        handleWrongInput(false);
        vo.nid=Task.getItemIdByPlatform(platform,url);
        if(StringUtils.isEmpty(vo.nid)){
            renderFailedJson(ReturnCode.BIZ_LIMIT, "请检查商品链接是否正确！");
        }
        
        if(StringUtils.contains(url, "taobao")){
            vo.shopType="c";
        }
        
        if(StringUtils.contains(url, "tmall")){
            vo.shopType="b";
        }
       
       if(DateTime.parse(vo.beginTime).isAfter(DateTime.parse(vo.endTime))){
           renderFailedJson(ReturnCode.BIZ_LIMIT, "开始时间不能大于结束时间");
       }
        vo.userId=getCurrentUser().id;
        int totalFlow=0;
        for (ListKwds s : vo.listKeyWords) {
            if(StringUtils.contains(vo.type, "MOBILE")){
                totalFlow+=s.clickTimes*3;
            }else {
                totalFlow+=s.clickTimes;
            }
        }
        vo.times=totalFlow;
        if(!TrafficRecord.isFullTraffic(vo)){
            renderFailedJson(ReturnCode.BIZ_LIMIT, "流量不足！发布失败");
        }
        for (ListKwds s : vo.listKeyWords) {
            vo.kwd=s.keywords;
            vo.times=s.clickTimes;
            TrafficRecord.publishTraffic(vo);
        }
        renderSuccessJson();
    }

}
