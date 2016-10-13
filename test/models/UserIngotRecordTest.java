package models;

import models.mappers.UserIngotRecordMapper;

import org.apache.ibatis.session.SqlSession;
import org.junit.Test;

import com.aton.db.SessionFactory;
import com.aton.test.DBHelper;
import com.aton.test.UnitTest;

import enums.Sign;


/**
 * TODO Comment.
 * 
 * @author youblade
 * @since  0.1
 * @created 2014年9月25日 下午8:18:48
 */
public class UserIngotRecordTest extends UnitTest{

    /**
     * Test method for {@link models.UserIngotRecord#buy()}.
     */
    @Test
    public void testBuy() {
        DBHelper.truncate(UserIngotRecord.TABLE_NAME);
        
        // 首次购买
        long userId = 0;
        long amount = 100;
        UserIngotRecord.newInstance(userId, null).plus(amount).memo("购买金币").buy();
        
        SqlSession ss = SessionFactory.getSqlSession();
        UserIngotRecordMapper mapper = ss.getMapper(UserIngotRecordMapper.class);
        UserIngotRecord firstRecord = mapper.selectLastRecord(0);
        assertNotNull(firstRecord);
        assertEquals(Sign.PLUS, firstRecord.sign);
        assertEquals(amount, firstRecord.amount);
        assertEquals(amount, firstRecord.balance);
        ss.commit();
        
        // 二次购买
        UserIngotRecord.newInstance(userId, firstRecord).plus(amount).memo("购买金币").buy();
        UserIngotRecord lastRecord = mapper.selectLastRecord(userId);
        assertNotNull(lastRecord);
        assertEquals(Sign.PLUS, lastRecord.sign);
        assertEquals(amount, lastRecord.amount);
        assertEquals(firstRecord.balance + amount, lastRecord.balance);
        
        DBHelper.truncate(UserIngotRecord.TABLE_NAME);
    }

}
