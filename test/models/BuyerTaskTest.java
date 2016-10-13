package models;

import java.math.BigDecimal;
import java.util.List;

import models.mappers.BuyerExperienceRecordMapper;
import models.mappers.UserIngotRecordMapper;

import org.apache.ibatis.session.SqlSession;
import org.joda.time.DateTime;
import org.junit.Test;

import vos.BuyerTaskSearchVo;
import vos.BuyerTaskStepVo;
import vos.BuyerTaskVo;
import vos.Page;
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
import enums.Sign;
import enums.TaskStatus;
import enums.pay.PayPlatform;
public class BuyerTaskTest extends UnitTest {

	@Test
	public void test_countExecuteTask() {
		BuyerTaskSearchVo vo = new BuyerTaskSearchVo();
		vo.buyerId = 2L;
		vo.accountId = 9L;
		assertTrue(BuyerTask.hasExecuteTask(vo));
	}

    @Test
    public void test_isTakeTask() {
        BuyerTaskSearchVo vo = new BuyerTaskSearchVo();
        vo.buyerId = 3L;
        vo.accountId = 4L;
        assertTrue(!BuyerTask.isTakeTask(vo));
    }

    @Test
    public void test_findByPage() {
        TaskSearchVo vo = TaskSearchVo.newInstance().pageNo(1).pageSize(10);
        Page<BuyerTaskVo> page = BuyerTask.findListByPage(vo);
        assertNotNull(page);
    }

    @Test
    public void test_findByPageForSellerTask() {
        long sellerId = 1L;
        long taskId = 0L;
        long buyerId = 0L;
        TaskSearchVo vo = TaskSearchVo.newInstance().pageNo(1).pageSize(10).sellerId(sellerId).taskId(taskId).buyerId(buyerId);
        Page<BuyerTask> page = BuyerTask.findByPageForSellerTask(vo);
        assertNotNull(page);
        
        FundAccount f = new FundAccount();
        f.userId = buyerId;
        f.type = PayPlatform.TENPAY;
//        f.save();
//        for (int i = 0; i < 5; i++) {
//            BuyerTask bt = new BuyerTask();
//            bt.taskId = taskId;
//            bt.status = TaskStatus.FINISHED;
//            bt.sellerId =sellerId;
//            bt.buyerId = buyerId;
//            bt.create();
//        }
        
        page = BuyerTask.findByPageForSellerTask(vo);
        assertNotNull(page);
        assertEquals(5, page.items.size());
    }

    @Test
    public void test_findWaitingTaskCountInfo(){
        User user  = User.findByNick("buyer");
        Multimap<String, TaskCountVo> taskCountInfo = BuyerTask.findWaitingTaskCountInfo(user.id);
        assertNotNull(taskCountInfo);
        assertFalse(taskCountInfo.isEmpty());
        for (TaskCountVo vo : taskCountInfo.values()) {
            assertNotNull(vo.platform);
            assertNotNull(vo.status);
            assertNotNull(vo.count);
        }
    }
    
    @Test
    public void test_confirmRefund() {
        DBHelper.truncate(Task.TABLE_NAME, BuyerTask.TABLE_NAME, BuyerExperienceRecord.TABLE_NAME,
            UserIngotRecord.TABLE_NAME);

        User buyer = User.findByNick("buyer");
        Task task = Task.newInstance();
        task.sellerId = 1L;
        task.totalOrderNum = 1;
        task.create();

        BuyerTask buyerTask = BuyerTask.newInstance().buyerId(buyer.id).status(TaskStatus.REFUNDING);
        buyerTask.taskId = task.id;
        buyerTask.experience = 3;
        buyerTask.create();

        BuyerTask bt = BuyerTask.findList(TaskSearchVo.newInstance().buyerId(buyer.id)).get(0);
        bt.confirmSellerRefund();

        //Check
        // 子任务状态
        BuyerTask btSaved = BuyerTask.findList(TaskSearchVo.newInstance().buyerId(buyer.id)).get(0);
        assertEquals(TaskStatus.FINISHED, btSaved.status);

        // 经验值记录
        SqlSession ss = SessionFactory.getSqlSession();
        BuyerExperienceRecordMapper mapper = ss.getMapper(BuyerExperienceRecordMapper.class);
        BuyerExperienceRecord record = mapper.selectLastRecord(buyer.id);
        assertNotNull(record);
        assertEquals(buyerTask.experience.longValue(), record.amount);
        assertEquals(record.balance, record.amount);
        assertEquals(Sign.PLUS, record.sign);

        // 押金（金币）记录
        UserIngotRecordMapper mapper2 = ss.getMapper(UserIngotRecordMapper.class);
        UserIngotRecord record2 = mapper2.selectLastRecord(buyer.id);
        assertNotNull(record2);
        assertEquals(BizConstants.TASK_TAKING_INGOT * 100, record2.amount);
        assertEquals(Sign.PLUS, record2.sign);

        // 父任务状态
        Task taskSaved = Task.findById("status,finished_count", btSaved.taskId);
        assertEquals(TaskStatus.FINISHED, taskSaved.status);
        assertEquals(task.totalOrderNum, taskSaved.finishedCount);

        DBHelper.truncate(Task.TABLE_NAME, BuyerTask.TABLE_NAME, BuyerExperienceRecord.TABLE_NAME,
            UserIngotRecord.TABLE_NAME);
    }
    
	@Test
	public void test_isParentTaskFinished() {
		assertTrue(!BuyerTask.isParentTaskFinished(494808943982280704L));
	}
	
	@Test
    public void test_create() {
        DBHelper.truncate(Task.TABLE_NAME, BuyerTask.TABLE_NAME);

        // 构造用户数据（资金等）
        User buyer = User.findByNick("buyer");
        buyer = User.findByIdWichCache(buyer.id);
        if (buyer.ingot < BizConstants.TASK_TAKING_INGOT) {
            UserIngotRecord.newInstance(buyer.id, null).plus(BizConstants.TASK_TAKING_INGOT * 100).simpleCreate();
        }

        // 构造父任务
        Task t = Task.instanceForTest(1).setStatus(TaskStatus.PUBLISHED);
        t.pcOrderNum = 1;
        t.totalOrderNum = t.pcOrderNum;
        t.create();

        /*
         * 接手普通任务
         */
        BuyerTask bt = _createBuyerTask(buyer, t.id, Device.PC);

        // Check task record
        Task task = Task.findById(t.id);
        assertEquals(1, task.pcTakenCount.intValue());
        assertEquals(0, task.mobileTakenCount.intValue());
        assertEquals(0, task.finishedCount);
        // 已全部接完
        assertTrue(task.isTakenOver(bt.device));

        // Check buyerTask record
        BuyerTask buyerTask = BuyerTask.findById(bt.id);
        assertEquals(TaskStatus.RECIEVED, buyerTask.status);
        assertEquals(bt.buyerId, buyerTask.buyerId);
        assertEquals(bt.taskId, buyerTask.taskId);

        // Check ingot record
        UserIngotRecord ingotRecord = SessionFactory.getSqlSession().getMapper(UserIngotRecordMapper.class)
            .selectLastRecord(buyer.id);
        assertEquals(Sign.MINUS, ingotRecord.sign);
        assertEquals(BizConstants.TASK_TAKING_INGOT * 100, ingotRecord.amount);

        /*
         * 接手定时任务
         */
        // 【例1】 构造定时任务: 1分钟发布1单
        t.pcOrderNum = 5;
        t.pcTakenCount = 0;
        t.totalOrderNum = t.pcOrderNum;
        t.publishTimerInterval = 1;
        t.publishTimerValue = 1;
        t.lastBatchPublishTime = DateTime.now().toDate();
        t.simpleUpdate();

        // 第1单可以接
        DBHelper.truncate(BuyerTask.TABLE_NAME);
        bt = _createBuyerTask(buyer, t.id, Device.PC);
        assertNotNull(bt);

        // 第2单不能接
        bt = _createBuyerTask(buyer, t.id, Device.PC);
        assertNull(bt);

        // 【例2】 构造定时任务: 1分钟发布2单
        t.pcTakenCount = 0;
        t.publishTimerInterval = 1;
        t.publishTimerValue = 2;
        t.lastBatchPublishTime = DateTime.now().toDate();
        t.simpleUpdate();

        // 第1单可以接
        DBHelper.truncate(BuyerTask.TABLE_NAME);
        bt = _createBuyerTask(buyer, t.id, Device.PC);
        assertNotNull(bt);

        // 第2单可以接
        bt = _createBuyerTask(buyer, t.id, Device.PC);
        assertNotNull(bt);

        // 第3单不能接
        bt = _createBuyerTask(buyer, t.id, Device.PC);
        assertNull(bt);

        // 【例3】 构造定时任务: 1分钟发布2单， 时间过了可以接
        t.pcTakenCount = 0;
        t.publishTimerInterval = 1;
        t.publishTimerValue = 2;
        t.lastBatchPublishTime = DateTime.now().toDate();
        t.simpleUpdate();

        // 第1单可以接
        DBHelper.truncate(BuyerTask.TABLE_NAME);
        bt = _createBuyerTask(buyer, t.id, Device.PC);
        assertNotNull(bt);

        // 第2单可以接
        bt = _createBuyerTask(buyer, t.id, Device.PC);
        assertNotNull(bt);

        // 时间过了，第3单可以接
        t.lastBatchPublishTime = DateTime.now().minusMinutes(2).toDate();
        t.simpleUpdate();

        bt = _createBuyerTask(buyer, t.id, Device.PC);
        assertNotNull(bt);

        DBHelper.truncate(Task.TABLE_NAME, BuyerTask.TABLE_NAME);
    }

    private BuyerTask _createBuyerTask(User buyer, long taskId, Device device) {
        Task task = Task.findById("status,pc_order_num,mobile_order_num,pc_taken_count,mobile_taken_count,"
            + "publish_timer_value,publish_timer_interval,last_batch_publish_time", taskId);
        if (task.status!=TaskStatus.PUBLISHED || task.isTakenOver(device)) {
            return null;
        }

        BuyerTask bt = BuyerTask.newInstance().buyerId(buyer.id).taskId(taskId);
        bt.device = device;
        bt.create();
        return bt;
    }
	
	
	
	@Test
	public void test_confirmGoodsAndRate() {
	    DBHelper.truncate(BuyerTask.TABLE_NAME,BuyerTaskStep.TABLE_NAME);
	    
	    BuyerTask bt = BuyerTask.newInstance().buyerId(0L);
	    bt.taskId = Pandora.getInstance().nextId();
	    bt.simpleCreate();
	    
	    BuyerTaskStepVo stepVo = new BuyerTaskStepVo();
	    stepVo.picUrls = Lists.newArrayList("http://gd1.alicdn.com/imgextra/i1/220303377/T2Wfm2Xz4XXXXXXXXX_!!220303377.jpg");
	    bt.confirmGoodsAndRate(stepVo);
	    
	    BuyerTask task = BuyerTask.findById(bt.id);
	    assertEquals(TaskStatus.WAIT_REFUND, task.status);
	    List<BuyerTaskStep> list = BuyerTaskStep.findByTaskId(bt.id);
	    assertTrue(MixHelper.isNotEmpty(list));
	    
	    BuyerTaskStep step = list.get(0);
	    assertEquals(step.buyerTaskId, bt.id.longValue());
	    assertEquals(step.buyerId, bt.buyerId.longValue());
	    
//	    DBHelper.truncate(BuyerTask.TABLE_NAME);
	}
	
	@Test
	public void test_(){
	    String realPaidFee = "49.5";
	    long paidFee = new BigDecimal(realPaidFee).multiply(BigDecimal.valueOf(100)).longValue();
	    assertEquals(4950, paidFee);
	    
	    realPaidFee = "49.55";
	    paidFee = new BigDecimal(realPaidFee).multiply(BigDecimal.valueOf(100)).longValue();
	    assertEquals(4955, paidFee);
	    
	    realPaidFee = "49.01";
	    paidFee = new BigDecimal(realPaidFee).multiply(BigDecimal.valueOf(100)).longValue();
	    assertEquals(4901, paidFee);
	    
	    realPaidFee = "49";
	    paidFee = new BigDecimal(realPaidFee).multiply(BigDecimal.valueOf(100)).longValue();
	    assertEquals(4900, paidFee);
	}
}
