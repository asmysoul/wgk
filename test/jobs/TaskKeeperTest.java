package jobs;

import java.util.Date;
import java.util.List;

import models.Task;
import models.User;
import models.mappers.TaskMapper;

import org.joda.time.DateTime;
import org.joda.time.Minutes;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import vos.TaskSearchVo;

import com.aton.db.SessionFactory;
import com.aton.test.DBHelper;
import com.aton.test.UnitTest;
import com.aton.util.MixHelper;

import enums.TaskStatus;


public class TaskKeeperTest extends UnitTest {
    
    private static TaskMapper mapper = SessionFactory.getSqlSession().getMapper(TaskMapper.class);

    @Test
    public void test_innerLogic() throws Exception {
        DateTime now = DateTime.now();
        Date lastBatchPublishTime = null;
        int mins = Minutes.minutesBetween(new DateTime(lastBatchPublishTime), now).getMinutes();
        MixHelper.print(mins);
        assertTrue(mins >= 30);
    }
    
    @Test
    public void test() throws Exception {
        
        User user = User.findByNick("0001");
        assertNotNull(user);
        
        /*
         *  上次发布时间在30分钟以前
         */
        DBHelper.truncate(Task.TABLE_NAME);
        Integer timeMinutes = 30;
        Date publishTime = DateTime.now().minusMinutes(31).toDate();
        for (int i = 1; i <= 3; i++) {
            Task.newInstance().setStatus(TaskStatus.WAIT_PUBLISH).setPublishTimerInterval(timeMinutes)
            .setLastBatchPublishTime(publishTime)
            .setSellerId(user.id).create();
        }
        new TaskKeeper().doJob();
        List<Task> list = mapper.selectListFromTaskPool(TaskSearchVo.newInstance().status(TaskStatus.PUBLISHED));
        assertEquals(3, list.size());
        // 检查其他字段未受影响
        for (Task task : list) {
            assertEquals(timeMinutes, task.publishTimerInterval);
            assertNotNull(task.lastBatchPublishTime);
        }
        
        /*
         *  上次发布时间在30分钟以内
         */
        DBHelper.truncate(Task.TABLE_NAME);
        publishTime = DateTime.now().minusMinutes(20).toDate();
        for (int i = 1; i <= 3; i++) {
            new Task().setStatus(TaskStatus.WAIT_PUBLISH).setPublishTimerInterval(timeMinutes)
            .setLastBatchPublishTime(publishTime)
            .setSellerId(user.id).create();
        }
        new TaskKeeper().doJob();
        list = mapper.selectList(TaskSearchVo.newInstance().status(TaskStatus.PUBLISHED));
        assertEquals(0, list.size());
        
    }
}
