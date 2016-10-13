package domain;

import java.util.List;

import models.Task;
import models.TaskItemSearchPlan;

import org.junit.Test;

import vos.TaskCountVo;

import com.alibaba.fastjson.JSONObject;
import com.aton.test.UnitTest;
import com.aton.util.JsonUtil;
import com.aton.util.MixHelper;

public class BizServiceTest extends UnitTest {

    @Test
    public void test_fetchOne() {

        Task task = BizService.fetchOne(Task.class, BizService.TASK_DETAIL, 515777250512076800L);
        assertNotNull(task);
        MixHelper.print(task.id);
        
        int count = BizService.fetchOne(Integer.class, BizService.TASK_NOTPASS_COUNT, 4);
        // assertEquals(200, result.code);
        // assertNotNull(result.results);
    }

    @Test
    public void test_fetchList() {
        List<TaskCountVo> list = BizService.fetchList(TaskCountVo.class, BizService.TASK_FINISHED_COUNT, 515777250512076800L);
        assertNotNull(list);
        for (TaskCountVo taskCountVo : list) {
            MixHelper.print("{}-->{}", taskCountVo.platform, taskCountVo.count);
        }
    }

}
