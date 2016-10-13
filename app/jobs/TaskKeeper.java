package jobs;

import java.util.Date;
import java.util.List;

import models.Task;
import models.mappers.TaskMapper;

import org.apache.ibatis.session.SqlSession;
import org.joda.time.DateTime;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import play.jobs.Every;
import vos.TaskSearchVo;

import com.aton.db.SessionFactory;
import com.aton.job.BaseJob;
import com.google.common.collect.Lists;

import enums.Sign;
import enums.TaskStatus;

/**
 * 
 * 任务池处理定时器.<br>
 * 任务池为一个逻辑上的概念，将DB中所有处于“待发布”状态的任务视为在一个池中，这些任务无法在页面上显示，故无法被接手；
 * 系统定时检查其中任务的状态，将满足特定条件的任务状态修改为“已发布”，使其可以在页面上显示，可以被接手。
 * 
 * 根据用户发布任务时设置的时间规则（每隔X分钟放出Y单），将任务【分批次】放出。
 * 1、定时检查满足条件的任务，将其从任务池中放出
 * 2、买家接手任务时，检查【该批次】的放出任务是否已全部被接手，并放入任务池
 * 
 * @author youblade
 * @since 0.1
 * @created 2014年7月31日 下午4:28:36
 */
@Every("1min")
public class TaskKeeper extends BaseJob {

    public static final Logger log = LoggerFactory.getLogger("taskpool");

    public void doJob() throws Exception {

        DateTime now = DateTime.now();
        String checkId = now.toString("yyyyMMddHHmm");
        log.info("=={}==Begin to Check Tasks in pool~~", checkId);

        SqlSession ss = SessionFactory.getSqlSessionForBatch();
        try {
            TaskMapper mapper = ss.getMapper(TaskMapper.class);

            // 取出所有“待发布”状态的任务
            TaskSearchVo vo = TaskSearchVo.newInstance().status(TaskStatus.WAIT_PUBLISH);
            List<Task> list = mapper.selectListFromTaskPool(vo);
            if (list.isEmpty()) {
                log.info("==={}==Finished to Check Tasks in pool~ No WAIT_PUBLISH tasks", checkId);
                return;
            }

            // 检查每个任务上次发布的时间，将到期的加入待发布列表
            List<Task> tasks = Lists.newArrayList();
            for (Task task : list) {
                if (task.isLastBatchPublishTimeOverdue()) {
                    tasks.add(task);
                }
            }
            if (tasks.isEmpty()) {
                log.info("==={}==Finished to Check Tasks in pool~ No overdue WAIT_PUBLISH tasks", checkId);
                return;
            }

            // 批量发布任务
            int batchSize = 1000;
            for (List<Task> batchList : Lists.partition(tasks, batchSize)) {
                log.info("==={}==Batch Update {} tasks--", checkId, batchSize);
                Date rightNow = DateTime.now().toDate();
                for (Task t : batchList) {
                    Task task = Task.instance(t.id).setStatus(TaskStatus.PUBLISHED).setLastBatchPublishTime(rightNow)
                        .modifyTime(rightNow);
                    mapper.updateById(task);
                    
                    // 更新页面上“任务列表(X)”处的可接手任务数
                    Task.updateTaskCount(Sign.PLUS, t.publishTimerValue);
                }
            }
            ss.commit();
            
            log.info("==={}==Finished to Check Tasks in pool~ Total updated {}", checkId, tasks.size());
        } catch (Exception e) {
            log.error(e.getMessage(),e);
        } finally {
            ss.close();
        }
    }

}
