package domain;

import models.User;

import org.joda.time.LocalDate;
import org.junit.Test;

import com.aton.config.CacheType;
import com.aton.test.UnitTest;
import com.aton.util.CacheUtil;
import com.aton.util.DateUtils;


/**
 * TODO Comment.
 * 
 * @author youblade
 * @since  0.1
 * @created 2014年8月24日 下午6:26:23
 */
public class TaskStatsTest extends UnitTest{

    /**
     * Test method for {@link domain.TaskStats#findForBuyerUntilNow(long)}.
     */
    @Test
    public void testFindForBuyerUntilNow() {
        User buyer = User.findByNick("buyer");
        assertNotNull(buyer);
        TaskStats stats = TaskStats.findForBuyerUntilNow(buyer.id);
        assertNotNull(stats);
        
        String key = CacheType.BUYER_TASK_STATS.getKey(LocalDate.now().toString(DateUtils.YYYY_MM_DD));
        TaskStats statsCached = CacheUtil.get(key, TaskStats.class);
        assertNotNull(statsCached);
    }

}
