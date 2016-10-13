package models;

import java.util.List;

import models.SellerPledgeRecord.PledgeAction;
import models.TenpayTradeLog.TradeResult;
import models.TenpayTradeLog.TradeType;
import models.mappers.BuyerTaskMapper;
import models.mappers.SellerPledgeRecordMapper;
import models.mappers.UserIngotRecordMapper;

import org.apache.ibatis.session.SqlSession;
import org.joda.time.LocalDate;
import org.junit.Test;

import vos.CancelTaskVo;
import vos.Page;
import vos.SellerTaskVo;
import vos.TaskCountVo;
import vos.TaskSearchVo;

import com.aton.config.BizConstants;
import com.aton.db.SessionFactory;
import com.aton.test.DBHelper;
import com.aton.test.UnitTest;
import com.aton.util.MixHelper;
import com.aton.util.Pandora;
import com.google.common.collect.Lists;
import com.google.common.collect.Multimap;

import enums.Device;
import enums.Platform;
import enums.Sign;
import enums.TaskListType;
import enums.TaskStatus;
import enums.TaskType;

public class TaskTest extends UnitTest {

    /**
     * 【会清空数据】
     */
	@Test
	public void test() {
		DBHelper.truncate(Task.TABLE_NAME);
		
		/*
		 * 全字段
		 */
		Task t = new Task();
		t.id = Pandora.getInstance().nextId();
		//1.任务类型
		t.platform = Platform.TAOBAO;
		t.shopId = 0L;
		t.type=TaskType.ORDER;
		//2.商品信息
		t.itemUrl = "http://item.taobao.com/item.html?id=123";
		t.itemTitle = "title";
		t.itemPic = "pic";
		t.itemPicUrl="http://img.taonao.com/ddd.img";
		t.itemProps="红色,XL码";
		t.itemPrice = 2000;
		t.itemDisplayPrice = 5000L;
		//2.2-的
		t.itemSearchMinPrice = 100L;
		t.itemSearchMaxPrice = 99999L;
		t.itemSearchLocation = "全国";
		t.isFreeShipping = true;
		t.orderMessages = "别忘了发货";
		t.totalOrderNum = 100;
		t.mobileOrderNum=10;
		
		// 附加商品
		t.status = TaskStatus.WAIT_PUBLISH;
		t.save();
		
//		for (TaskListType taskType : TaskListType.values()) {
//			List<Task> list = Task.findByListType(taskType);
//			assertTrue(MixHelper.isNotEmpty(list));
//		}
		
//		TaskSearchVo vo = new TaskSearchVo();
//		List<Task> list = Task.findBySearchVo(vo);
//		assertTrue(MixHelper.isNotEmpty(list));
//		for (Task task : list) {
//			assertNotNull(task.status);
//		}
	}
	
	/**
	 * 【会清空数据】
	 */
	@Test
	public void test_findVoByPage() {
		TaskSearchVo vo = new TaskSearchVo();

		vo.pageNo = 1;
		vo.pageSize = 10;
		vo.sellerId = 3L;
		// vo.device = Device.PC;
		// vo.status = TaskStatus.IN_PROGRESS;

		Page<SellerTaskVo> page = Task.findVoByPage(vo);
		assertEquals(page.items.size(), 1);
	}
	
	/**
     * 【会清空数据】
     */
	@Test
	public void test_findById() {
	    DBHelper.truncate(SellerPledgeRecord.TABLE_NAME);
	    
	    long id = 500248651327602688L;
	    Task task = Task.findById(id);
	    assertNotNull(task);
	    
	    assertNotNull(task.id);
	    assertNotNull(task.itemTitle);
	    assertNotNull(task.itemPicUrl);
	    assertTrue(MixHelper.isNotEmpty(task.searchPlans));
	    
	    assertTrue(task.isFreeShipping);
	    
	    /*
	     * 测试任务是否支付
	     */
	    assertFalse(task.isPaid);
	    
	    SellerPledgeRecord r = new SellerPledgeRecord();
	    r.action = PledgeAction.LOCK;
	    r.taskId = task.id;
	    r.sellerId = task.sellerId;
	    r.amount = (long) 100;
	    r.sign = Sign.MINUS;
	    r.balance = 10;
	    r.create();
	    task = Task.findById(id);
	    assertTrue(task.isPaid);
	    //TODO check other fields
	}
	
	/**
     * 【会清空数据】
	 * 测试统计审核状态的任务数
	 */
	@Test
    public void test_findWaitingTaskCountInfo_EXAMINE() {
        DBHelper.truncate(Task.TABLE_NAME);
        DBHelper.truncate(BuyerTask.TABLE_NAME);

        long sellerId = 100;
        /*
         * 审核状态的任务
         */
        // 待审核
        int countWaitExamine = 3;
        _generateTask(sellerId, TaskStatus.WAIT_EXAMINE, countWaitExamine);

        Multimap<String, TaskCountVo> map = Task.findWaitingTaskCountInfo(sellerId);
        List<TaskCountVo> list = (List<TaskCountVo>) map.get(BizConstants.EXAMINE_STATUS_LIST);
        assertEquals(1, list.size());
        _checkTaskCount(countWaitExamine, TaskStatus.WAIT_EXAMINE, list.get(0));

        // 审核中
        int countExaming = 5;
        _generateTask(sellerId, TaskStatus.EXAMINING, countExaming);
        map = Task.findWaitingTaskCountInfo(sellerId);
        list = (List<TaskCountVo>) map.get(BizConstants.EXAMINE_STATUS_LIST);
        assertEquals(2, list.size());
        for (TaskCountVo vo : list) {
            _checkTaskCount(countWaitExamine, TaskStatus.WAIT_EXAMINE, vo);
            _checkTaskCount(countExaming, TaskStatus.EXAMINING, vo);
        }

        // 审核不通过
        int countNotPass = 7;
        _generateTask(sellerId, TaskStatus.NOT_PASS, countNotPass);
        map = Task.findWaitingTaskCountInfo(sellerId);
        list = (List<TaskCountVo>) map.get(BizConstants.EXAMINE_STATUS_LIST);
        assertEquals(3, list.size());
        for (TaskCountVo vo : list) {
            _checkTaskCount(countWaitExamine, TaskStatus.WAIT_EXAMINE, vo);
            _checkTaskCount(countExaming, TaskStatus.EXAMINING, vo);
            _checkTaskCount(countNotPass, TaskStatus.NOT_PASS, vo);
        }
    }

    private void _checkTaskCount(int expectedCount,TaskStatus expectedStatus, TaskCountVo vo) {
        if (vo.status == expectedStatus) {
            assertEquals(expectedCount, vo.count);
        }
    }
	
    private void _generateTask(long sellerId, TaskStatus status, int totleNum) {
        for (int id = 1; id <= totleNum; id++) {
            // 业务限制初始创建的任务只能为“wait_edit”，此处使用更新来实现status的设置
            Task task = Task.newInstance().setSellerId(sellerId).setStatus(status);
            task.save();
            Task.instance(task.id).setStatus(status).save();
        }
    }
    
    /**
     * 【会清空数据】
     * 测试统计“待退款”，“待发货”状态的任务数
     */
    @Test
    public void test_findWaitingTaskCountInfo() {
        DBHelper.truncate(Task.TABLE_NAME);
        DBHelper.truncate(BuyerTask.TABLE_NAME);
        
        long sellerId = 100;
        
        // “待发货”状态的任务
        // “待退款”状态的任务
        int taskCount = 3;
        for (TaskStatus status : Lists.newArrayList(TaskStatus.EXPRESS_PRINT, TaskStatus.WAIT_REFUND)) {
//            int taskCount = NumberUtils.getRandomBetween(1, 5);
            
            // 生成测试数据
            _generateBuyerTask(sellerId, status, taskCount);
                
            // Check
            Multimap<String, TaskCountVo> map = Task.findWaitingTaskCountInfo(sellerId);
            List<TaskCountVo> list = (List<TaskCountVo>) map.get(status.toString());
            assertEquals(Platform.values().length, list.size());
            for (TaskCountVo vo : list) {
                if (TaskStatus.EXPRESS_PRINT == vo.status || TaskStatus.WAIT_REFUND == vo.status) {
                    assertEquals(taskCount, vo.count);
                }
            }
        }

    }
	
    private void _generateBuyerTask(long sellerId, TaskStatus status, int totleNum) {
        for (Platform platform : Platform.values()) {
            Task task = Task.newInstance().setSellerId(sellerId);
            task.platform = platform;
            task.save();
            
            for (int i = 0; i < totleNum; i++) {
                BuyerTask bt = new BuyerTask();
                bt.sellerId = task.sellerId;
                bt.taskId = task.id;
                bt.status = status;
                bt.simpleCreate();
            }
        }
    }
    
	@Test
	public void test_findVoExamineByPage() {
		TaskSearchVo vo = new TaskSearchVo();
		vo.status = TaskStatus.WAIT_EXAMINE;

		Page<SellerTaskVo> list = Task.findExamineVoByPage(vo);
		assertTrue(list.totalCount > 0);
	}
	
	@Test
	public void test_computecommission() {
	    
	    // 0~50
	    float x = commission(1);
	    assertTrue(6 == x);
	    x = commission(50.0f);
	    assertTrue(6 == x);
	    
	    // 50.01~150
	    x = commission(50.01f);	    
	    assertTrue(7.f == x);
	    x = commission(150.0f);
	    MixHelper.print(x);
	    assertTrue(7.f == x);
	    
	    x = commission(52.89f);
	    MixHelper.print(x);
	    assertTrue(7.f == x);
	    
	    // 650.01~750
	    x = commission(650.01f);	    
	    assertTrue(13.f == x);
	    x = commission(750.0f);
	    MixHelper.print(x);
	    assertTrue(13.f == x);
	    
	    x = commission(652.89f);
	    MixHelper.print(x);
	    assertTrue(13.f == x);
	    
	    // 3950.01~4050
	    x = commission(3950.01f);	    
	    
	    assertEquals(600, Task.computeFeePerOrder(5000,Platform.TAOBAO));
	    assertEquals(700, Task.computeFeePerOrder(5001,Platform.TAOBAO));
	    assertEquals(1300, Task.computeFeePerOrder(65001,Platform.TAOBAO));
	}
	
    private float commission(float price) {
        float x = Task.computeCommission(price,Platform.TAOBAO);
        MixHelper.print(x);
        return x;
    }
    
    @Test
    public void test_setTaskPledgeAndIngot() {
        Task t = Task.newInstance();
        t.itemPrice = 99*100L;
        t.itemBuyNum = 1;
        t.searchPlans = Lists.newArrayList(TaskItemSearchPlan.newInstance("test"));
        t.isFreeShipping = false;
        t.totalOrderNum = 10;
        t.pcOrderNum = 10;
        t.mobileOrderNum = 0;
        
        t.calculateAndSetTaskPledgeAndIngot();
        
        assertEquals(7, t.baseOrderIngot / 100.0f, 0);
        assertEquals(110, t.totalIngot / 100.0f, 0);

        assertEquals(113.95 *100, t.pledge, 0);
        assertEquals(1139.5 *100, t.totalPledge, 0);
            
        // 加赏佣金
        t.speedTaskIngot = 10;
        t.speedExamine = true;
        t.extraRewardIngot = 2;
        t.publishTimerInterval = 30;//min
        t.buyTimeInterval = 1;
        t.calculateAndSetTaskPledgeAndIngot();
        assertEquals(50 *100, t.vasIngot, 0);
        
        assertEquals(160, t.totalIngot / 100.0f, 0);
        assertEquals(1139.5 *100, t.totalPledge, 0);
        
        /*
         * 测试精确到分的价格：99.99
         */
        t.itemPrice = 9999;
        t.vasIngot = 0L;
        t.calculateAndSetTaskPledgeAndIngot();
        
        assertEquals(160, t.totalIngot / 100.0f, 0);
        assertEquals(114.99 *100, t.pledge, 0);
        assertEquals(114990, t.totalPledge.intValue());
        
        // 99.91
        t.itemPrice = 9991;
        t.vasIngot = 0L;
        t.calculateAndSetTaskPledgeAndIngot();
        assertEquals(114910, t.totalPledge.intValue());
        
        t.itemPrice = 9901;
        t.vasIngot = 0L;
        t.calculateAndSetTaskPledgeAndIngot();
        assertEquals(113960, t.totalPledge.intValue());
        
        t.itemPrice = 10024;
        t.vasIngot = 0L;
        t.calculateAndSetTaskPledgeAndIngot();
        assertEquals(115250, t.totalPledge.intValue());
    }
    
    /*
     * 计算具体某个任务的费用
     */
    @Test
    public void test_setTaskPledgeAndIngot_biz() {
        Task t = Task.newInstance();
        t.itemPrice = 650*100L;
        t.itemBuyNum = 1;
        t.searchPlans = Lists.newArrayList(TaskItemSearchPlan.newInstance("test"));
        t.isFreeShipping = false;
        t.totalOrderNum = 10;
        t.pcOrderNum = 10;
        t.mobileOrderNum = 0;
        
//        t.speedTaskIngot = 5;
        t.speedExamine = false;
        // 加赏佣金
//        t.extraRewardIngot = 2;
//        t.publishTimerInterval = 30;//min
//        t.buyTimeInterval = 1;
        t.calculateAndSetTaskPledgeAndIngot();
        
//        assertEquals(50 *100, t.vasIngot, 0);
        MixHelper.print(t.vasIngot);
        
        assertEquals(140 , t.totalIngot / 100.0f, 0);
        assertEquals(6925 *100, t.totalPledge, 0);
    }
    
    @Test
    public void test_getListType(){
        
        LocalDate registTime = LocalDate.now();
        Task t = Task.newInstance();
        t.createTime = registTime.toDate();
        TaskListType listType = t.getListType(registTime.toDate());
        assertEquals(TaskListType.NEW_SHOP, listType);
        
        t.createTime = LocalDate.now().toDate();
        registTime = LocalDate.now().minusWeeks(1).minusDays(1);
        assertEquals(TaskListType.COMMON, t.getListType(registTime.toDate()));
        
        t.extraRewardIngot = 1;
        assertEquals(TaskListType.EXTRA_REWARD, t.getListType(registTime.toDate()));
        
        t.speedTaskIngot = 1;
        assertEquals(TaskListType.SYS_RECOMMEND, t.getListType(registTime.toDate()));
    }

    @Test
    public void test_pay(){
        
        /*
         * 使用网银支付
         */
        DBHelper.truncate(SellerPledgeRecord.TABLE_NAME, UserIngotRecord.TABLE_NAME, TenpayTradeLog.TABLE_NAME);
        Task t = _createTaskForTest("seller", 500, 20);
        long totalPayFee = t.totalIngot+t.totalPledge;
        
        // 构造网银充值记录
        TenpayTradeLog.newInstance(TradeType.TASK, String.valueOf(t.id)).userId(t.sellerId).amount(totalPayFee).result(TradeResult.OK).save();
        t.pay(totalPayFee, false, false);
        
        // 检查
        SqlSession ss = SessionFactory.getSqlSession();
        SellerPledgeRecordMapper pledgeMapper = ss.getMapper(SellerPledgeRecordMapper.class);
        // 押金充值记录
        SellerPledgeRecord r = pledgeMapper.selectBySellerAndTaskAndAction(t.sellerId, t.id, PledgeAction.RECHARGE);
        assertEquals(totalPayFee, (long)r.amount);
        assertEquals(Sign.PLUS, r.sign);
        // 押金扣除记录：兑换金币
        r = pledgeMapper.selectBySellerAndTaskAndAction(t.sellerId, t.id, PledgeAction.EXCHANGE_INGOT);
        assertEquals(t.totalIngot.longValue(), (long)r.amount);
        assertEquals(Sign.MINUS, r.sign);
        // 金币充值记录：押金兑换
        UserIngotRecordMapper ingotMapper = ss.getMapper(UserIngotRecordMapper.class);
        UserIngotRecord ingotRecord = ingotMapper.selectByTaskIdAndSign(t.id, Sign.PLUS);
        assertEquals(t.totalIngot.longValue(), ingotRecord.amount);
        
        _checkTaskAndUserBalance(t,0,0);
        
        /*
         * 仅使用押金支付
         */
        DBHelper.truncate(SellerPledgeRecord.TABLE_NAME, UserIngotRecord.TABLE_NAME, TenpayTradeLog.TABLE_NAME);
        ss.commit();
        // 构造账户押金充值记录
        SellerPledgeRecord.newInstance(t.sellerId, null).taskId(t.id).action(PledgeAction.RECHARGE, totalPayFee).create();
        t.pay(totalPayFee, false, true);
        
        // 检查
        // 押金扣除记录：兑换金币
        r = pledgeMapper.selectBySellerAndTaskAndAction(t.sellerId, t.id, PledgeAction.EXCHANGE_INGOT);
        assertEquals(t.totalIngot.longValue(), (long)r.amount);
        assertEquals(Sign.MINUS, r.sign);
        // 金币充值记录：押金兑换
        ingotRecord = ingotMapper.selectByTaskIdAndSign(t.id, Sign.PLUS);
        assertEquals(t.totalIngot.longValue(), ingotRecord.amount);
        
        _checkTaskAndUserBalance(t,0,0);
        
        /*
         * 仅使用金币支付
         */
        DBHelper.truncate(SellerPledgeRecord.TABLE_NAME, UserIngotRecord.TABLE_NAME, TenpayTradeLog.TABLE_NAME);
        ss.commit();
        // 构造金币充值记录
        UserIngotRecord.newInstance(t.sellerId, null).plus(totalPayFee).simpleCreate();
        t.pay(totalPayFee, true, false);
        
        // 检查
        // 金币扣除记录：充值押金
        ingotRecord = ingotMapper.selectByTaskIdAndSign(t.id, Sign.MINUS);
        assertEquals(t.totalPledge.longValue(), ingotRecord.amount);
        // 押金增加记录：金币充值
        r = pledgeMapper.selectBySellerAndTaskAndAction(t.sellerId, t.id, PledgeAction.RECHARGE);
        assertEquals(t.totalPledge.longValue(), (long)r.amount);
        assertEquals(Sign.PLUS, r.sign);
        
        _checkTaskAndUserBalance(t,0,0);
        
        /*
         * 仅使用金币押金混合支付
         */
        //1.金币、押金余额分别都足够
        DBHelper.truncate(SellerPledgeRecord.TABLE_NAME, UserIngotRecord.TABLE_NAME, TenpayTradeLog.TABLE_NAME);
        ss.commit();
        
        // 构造金币\押金充值记录
        UserIngotRecord.newInstance(t.sellerId, null).plus(t.totalIngot).simpleCreate();
        SellerPledgeRecord.newInstance(t.sellerId, null).taskId(t.id).action(PledgeAction.RECHARGE, t.totalPledge).create();
        t.pay(totalPayFee, true, true);
        
        _checkTaskAndUserBalance(t,0,0);
        
        //2.金币余额足够、押金余额不够
        DBHelper.truncate(SellerPledgeRecord.TABLE_NAME, UserIngotRecord.TABLE_NAME, TenpayTradeLog.TABLE_NAME);
        ss.commit();
        
        UserIngotRecord.newInstance(t.sellerId, null).plus(t.totalIngot+1).simpleCreate();
        SellerPledgeRecord.newInstance(t.sellerId, null).taskId(t.id).action(PledgeAction.RECHARGE, t.totalPledge-1).create();
        t.pay(totalPayFee, true, true);
        
        // 金币充值押金：金币减、押金加
//        ingotRecord = ingotMapper.selectByTaskIdAndSign(t.id, Sign.MINUS);
//        assertEquals(Sign.MINUS, ingotRecord.sign);
//        assertEquals(1, ingotRecord.amount);
//        r = pledgeMapper.selectLastRecord(t.sellerId);
        
        ss.commit();
        _checkTaskAndUserBalance(t,0,0);
        
        //3.金币余额不够、押金余额足够
        DBHelper.truncate(SellerPledgeRecord.TABLE_NAME, UserIngotRecord.TABLE_NAME, TenpayTradeLog.TABLE_NAME);
        ss.commit();
        UserIngotRecord.newInstance(t.sellerId, null).plus(t.totalIngot-1).simpleCreate();
        SellerPledgeRecord.newInstance(t.sellerId, null).taskId(t.id).action(PledgeAction.RECHARGE, t.totalPledge+1).create();
        t.pay(totalPayFee, true, true);
        
        ss.commit();
        _checkTaskAndUserBalance(t,0,0);
        
    }

    private Task _createTaskForTest(String nick, long totalPledge, long totalIngot) {
        Task t = Task.instance(0);
        t.sellerId = User.findByNick(nick).id;
        t.itemTitle = "title";
        t.itemBuyNum = 10;
        t.totalPledge = totalPledge;
        t.totalIngot = totalIngot;
//        t.save();
        return t;
    }
    


    private void _checkTaskAndUserBalance(Task t, long userPledge, long userIngot) {
        SqlSession ss = SessionFactory.getSqlSession();
//        Task task = ss.getMapper(TaskMapper.class).selectFieldsById("status,seller_id", id);
//        assertEquals(TaskStatus.PUBLISHED, task.status);
        
        // 押金冻结记录
        SellerPledgeRecord r = ss.getMapper(SellerPledgeRecordMapper.class).selectByTaskIdAndAction(t.id, PledgeAction.LOCK);
        assertEquals(t.totalPledge.longValue(), (long)r.amount);
        // 金币扣除记录
        UserIngotRecord ingotRecord = ss.getMapper(UserIngotRecordMapper.class).selectLastRecord(t.sellerId);
        assertEquals(t.totalIngot.longValue(), ingotRecord.amount);
        
        User u = User.findByIdWichCache(t.sellerId);
        assertEquals(userPledge, u.pledge.longValue());
        assertEquals(userIngot, u.ingot.longValue());
    }
    
    @Test
    public void test_countExtraSearchPlan() {
        Task t = new Task();
        // 1个淘宝
        t.searchPlans = Lists.newArrayList(TaskItemSearchPlan.newInstance("test"));
        assertEquals(0, t.countExtraSearchPlan());

        // 2个淘宝
        t.searchPlans.add(TaskItemSearchPlan.newInstance("test2"));
        assertEquals(1, t.countExtraSearchPlan());

        // 2个淘宝,1个天猫
        TaskItemSearchPlan p = TaskItemSearchPlan.newInstance("test3").inTmall(true);
        t.searchPlans.add(p);
        assertEquals(2, t.countExtraSearchPlan());
        
        // 2个淘宝,2个天猫
        TaskItemSearchPlan p2 = TaskItemSearchPlan.newInstance("test4").inTmall(true);
        t.searchPlans.add(p2);
        assertEquals(3, t.countExtraSearchPlan());
        
        // 0个淘宝，2个天猫
        t.searchPlans = Lists.newArrayList(p,p2);
        assertEquals(2, t.countExtraSearchPlan());
        
    }
    
    @Test
    public void test_findCancelInfo(){
        
        // 测试count返回是否可能为null
        int count = SessionFactory.getSqlSession().getMapper(BuyerTaskMapper.class).countForSellerTask(0, Device.PC);
        assertEquals(0, count);
        
        /*
         *  取未被接手的新发布任务
         */
        long id = Long.parseLong("542135209517121536");
        Task task = Task.findById(id);
        assertNotNull(task);
        
        // 计算费用
        task.calculateAndSetTaskPledgeAndIngot();
        
        CancelTaskVo cancelInfo = Task.findCancelInfo(id);
        assertEquals(task.totalPledge.longValue(), cancelInfo.cancledPledge);
        assertEquals(task.totalIngot.longValue(), cancelInfo.cancledIngot);
        assertEquals(task.totalOrderNum, cancelInfo.cancledNum);
        
        /*
         * 构造已接手一半数量的任务
         */
        // 子任务数量必须是偶数才好测试
        assertEquals(0, task.totalOrderNum % 2);
        // 刷单数量减半后重新计算费用
        task.pcTakenCount = task.pcOrderNum/2;
        task.mobileTakenCount = task.mobileOrderNum/2;
        task.calculateAndSetTaskPledgeAndIngot();
        
        cancelInfo = Task.findCancelInfo(id);
        assertEquals(task.totalPledge.longValue(), cancelInfo.cancledPledge);
        assertEquals(task.totalIngot.longValue(), cancelInfo.cancledIngot);
        assertEquals(task.totalOrderNum, cancelInfo.cancledNum);
        
    }
    
    @Test
    public void test_biz_findCancelInfo(){
        
        long id = Long.parseLong("542135209517121536");
        Task task = Task.findById(id);
        assertNotNull(task);
        
        // 计算费用
//        task.calculateAndSetTaskPledgeAndIngot();
//        
        CancelTaskVo cancelInfo = Task.findCancelInfo(id);
//        assertEquals(task.totalPledge.longValue(), cancelInfo.cancledPledge);
//        assertEquals(task.totalIngot.longValue(), cancelInfo.cancledIngot);
//        assertEquals(task.totalOrderNum, cancelInfo.cancledNum);
        
        /*
         * 构造已接手一半数量的任务
         */
        task.pcTakenCount = 1;
        task.mobileTakenCount = 1;
        task.calculateAndSetTaskPledgeAndIngot();
        
        cancelInfo = Task.findCancelInfo(id);
        MixHelper.print("退还押金：{}",cancelInfo.cancledPledge);
        MixHelper.print("退还金币：{}",cancelInfo.cancledIngot);
//        assertEquals(task.totalPledge.longValue(), cancelInfo.cancledPledge);
//        assertEquals(task.totalIngot.longValue(), cancelInfo.cancledIngot);
//        assertEquals(task.totalOrderNum, cancelInfo.cancledNum);
        
    }
}
