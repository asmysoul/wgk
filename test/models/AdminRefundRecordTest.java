package models;

import static org.junit.Assert.*;

import java.util.Date;

import models.mappers.AdminRefundRecordMapper;

import org.junit.Test;

import com.aton.db.SessionFactory;
import com.aton.test.DBHelper;
import com.aton.test.UnitTest;
import com.aton.util.MixHelper;


public class AdminRefundRecordTest extends UnitTest {

    @Test
    public void test_mapper() {
        DBHelper.truncate(AdminRefundRecord.TABLE_NAME);
        
        AdminRefundRecordMapper mapper = SessionFactory.getSqlSession().getMapper(AdminRefundRecordMapper.class);
        
        AdminRefundRecord record = AdminRefundRecord.newInstance("transNoSSSS").user(0, 0).task(0,0).createTime(new Date());
        mapper.insert(record);
        
        record = mapper.selectByBuyerTaskId(record.buyerTaskId);
        assertNotNull(record);
        assertNotNull(record.id);
        assertTrue(MixHelper.isNotEmpty(record.transNo));
        assertNotNull(record.taskId);
        assertNotNull(record.buyerTaskId);
        assertNotNull(record.buyerId);
        assertNotNull(record.sellerId);
        assertNotNull(record.createTime);
    }

}
