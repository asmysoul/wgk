package domain;

import java.math.BigDecimal;
import java.util.List;

import models.Task;
import models.mappers.BuyerTaskMapper;

import org.apache.ibatis.session.SqlSession;
import org.junit.Test;

import vos.TaskSearchVo;

import com.aton.db.SessionFactory;
import com.aton.test.UnitTest;
import com.aton.util.JsonUtil;
import com.aton.util.MixHelper;

import enums.TaskStatus;


/**
 * 
 * TODO Comment.
 * 
 * @author youblade
 * @since  v2.0
 * @created 2015年1月8日 下午6:51:12
 */
public class TaskCostDetailTest extends UnitTest {

    /**
     * Test method for {@link domain.TaskCostDetail#valueOf(models.Task)}.
     */
    @Test
    public void testValueOf() {
        Task t = Task.findById(560317402152173568L);

        // 撤销过未接单任务的情况下，已完成的任务数与任务单数不一致，会导致计算结果不准确
        assertTrue(t.status != TaskStatus.CANCLED);
        t.pcTaskCount = t.pcOrderNum;
        t.mobileTaskCount = t.mobileOrderNum;

        TaskCostDetail costDetail = TaskCostDetail.valueOf(t);
        assertEquals("2.64", costDetail.sysRefundFee);
        assertEquals("2.64", costDetail.vasFeeSum);
        assertEquals(t.vasIngot.longValue(), new BigDecimal(costDetail.vasFeeSum).movePointRight(2).longValue());
    }

    /**
     * Test method for {@link domain.TaskCostDetail#findBySellerNick(java.lang.String)}.
     */
    @Test
    public void testFindBySellerNick() {
        List<TaskCostDetail> list = TaskCostDetail.findByTaskMessage("15190323555",null,null);
        assertTrue(MixHelper.isNotEmpty(list));
        
        MixHelper.print(JsonUtil.toJson(list));
    }

}
