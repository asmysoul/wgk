package models;

import static org.junit.Assert.*;

import java.util.Collection;
import java.util.List;
import java.util.Set;
import java.util.concurrent.TimeUnit;

import javax.management.MXBean;

import models.TenpayTradeLog.TradeResult;
import models.TenpayTradeLog.TradeType;
import models.mappers.TenpayTradeLogMapper;

import org.junit.Test;

import play.jobs.Job;
import play.libs.F.Promise;

import com.aton.db.SessionFactory;
import com.aton.test.DBHelper;
import com.aton.test.UnitTest;
import com.aton.util.MixHelper;
import com.aton.util.NumberUtils;
import com.google.common.collect.Lists;
import com.google.common.collect.Sets;


/**
 * TODO Comment.
 * 
 * @author youblade
 * @since  0.1
 * @created 2014年9月11日 下午7:21:45
 */
public class TenpayTradeLogTest extends UnitTest {

    /**
     * Test method for {@link models.TenpayTradeLog#save()}.
     */
    @Test
    public void testSave() {
        DBHelper.truncate(TenpayTradeLog.TABLE_NAME);
        
        Collection<Promise<Boolean>> promises = Lists.newArrayList();
        for (int i = 0; i < 30; i++) {
            Promise<Boolean> promise = new Job<Boolean>(){
                public Boolean doJobWithResult() throws Exception {
                    MixHelper.print("----thread----");
                    TenpayTradeLog log = TenpayTradeLog.instance(null).userId(0).amount(1).result(TradeResult.UNTREATED);
                    log.save();
                    assertNotNull(log.outTradeNo);
                    return true;
                };
            }.now();
            MixHelper.pause(TimeUnit.MILLISECONDS, 200);
            promises.add(promise);
        }
        Promise.waitAll(promises);
        
        TenpayTradeLogMapper mapper = SessionFactory.getSqlSession().getMapper(TenpayTradeLogMapper.class);
        List<TenpayTradeLog> list = mapper.selectList();
        
        Set<String> tradeNoSet = Sets.newHashSet();
        for (TenpayTradeLog log : list) {
            assertNotNull(log.outTradeNo);
            tradeNoSet.add(log.outTradeNo);
        }
        assertTrue(NumberUtils.isGreaterThan(list.size(), 1));
        MixHelper.print("set={}-list={}",tradeNoSet.size(),list.size());
        assertEquals(tradeNoSet.size(), list.size());
        
        
//        TenpayTradeLog log = TenpayTradeLog.instance(null).userId(0).amount(1).result(TradeResult.UNTREATED);
//        log.save();
//        assertNotNull(log.id);
        
        DBHelper.truncate(TenpayTradeLog.TABLE_NAME);
    }

    /**
     * Test method for {@link models.TenpayTradeLog#findById(java.lang.String)}.
     */
    @Test
    public void testFindById() {
        DBHelper.truncate(TenpayTradeLog.TABLE_NAME);
        
        TradeType type = TradeType.TASK;
        TradeResult result = TradeResult.UNTREATED;
        String bizId = "1233555";
        TenpayTradeLog.newInstance(type, bizId).userId(0).amount(1).type(type).result(result).save();
        
        // 仅有一条记录
        TenpayTradeLog log = TenpayTradeLog.findById("1");
        assertNotNull(log);
        assertEquals(type, log.type);
        assertEquals(result, log.result);
        assertEquals(0, log.userId.intValue());
        
        assertNotNull(log.createTime);
        assertNotNull(log.modifyTime);
        
        MixHelper.print(log.outTradeNo);
        assertNotNull(log.outTradeNo);
//        DBHelper.truncate(TenpayTradeLog.TABLE_NAME);
    }

    @Test
    public void test_findByOutTradeNo(){
        DBHelper.truncate(TenpayTradeLog.TABLE_NAME);
        TenpayTradeLog log = TenpayTradeLog.instance(null).userId(0).amount(1).result(TradeResult.UNTREATED);
        log.save();
        
        log = TenpayTradeLog.findByOutTradeNo(log.outTradeNo);
        assertNotNull(log);
    }
}
