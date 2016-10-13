package models;

import models.SellerPledgeRecord.PledgeAction;
import models.mappers.SellerPledgeRecordMapper;

import org.apache.ibatis.session.SqlSession;
import org.junit.Test;

import com.aton.db.SessionFactory;
import com.aton.test.DBHelper;
import com.aton.test.UnitTest;

public class SellerPledgeRecordTest extends UnitTest{

    /**
     * Test method for {@link models.SellerPledgeRecord#create()}.
     */
    @Test
    public void testCreate() {
        DBHelper.truncate(SellerPledgeRecord.TABLE_NAME);
        
        long sellerId = 0L;
        SellerPledgeRecord.newInstance(sellerId, null).taskId(0).action(PledgeAction.RECHARGE, 100).create();

        SqlSession ss = SessionFactory.getSqlSession();
        SellerPledgeRecordMapper mapper = ss.getMapper(SellerPledgeRecordMapper.class);
        SellerPledgeRecord record = mapper.selectLastRecord(sellerId);
        assertNotNull(record);
        assertEquals(record.amount, record.balance);
        
        DBHelper.truncate(SellerPledgeRecord.TABLE_NAME);
    }
    
    @Test
    public void test_isUnlocked() {
        DBHelper.truncate(SellerPledgeRecord.TABLE_NAME);

        long sellerId = 0;
        long taskId = 999;
        // 充值
        SellerPledgeRecord firstRecord = SellerPledgeRecord.newInstance(sellerId, null).action(PledgeAction.RECHARGE,
            10000);
        firstRecord.create();
        // 发任务
        SellerPledgeRecord lastRecord = SellerPledgeRecord.newInstance(sellerId, firstRecord)
            .action(PledgeAction.LOCK, 100).taskId(taskId);
        lastRecord.create();
        // 审核不通过
        lastRecord = SellerPledgeRecord.newInstance(sellerId, lastRecord).action(PledgeAction.UNLOCK, 100)
            .taskId(taskId);
        lastRecord.create();
        // 重新修改发布
        lastRecord = SellerPledgeRecord.newInstance(sellerId, lastRecord).action(PledgeAction.LOCK, 100).taskId(taskId);
        lastRecord.create();

        /*
         * 随便加几条其他任务的冻结记录作为干扰
         */
        for (int i = 1; i <= 3; i++) {
            SellerPledgeRecord.newInstance(sellerId, lastRecord).action(PledgeAction.LOCK, 100).taskId(taskId + i)
                .create();
        }

        // 任务完成，解冻
        lastRecord = SellerPledgeRecord.newInstance(sellerId, lastRecord).action(PledgeAction.UNLOCK, 100)
            .taskId(taskId);
        lastRecord.create();
    }
}
