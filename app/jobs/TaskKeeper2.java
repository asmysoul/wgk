package jobs;

import java.util.Date;
import java.util.List;

import models.Task;
import models.Task2;
import models.mappers.TaskMapper;
import models.mappers.TaskMapper2;

import org.apache.ibatis.session.SqlSession;
import org.joda.time.DateTime;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import play.jobs.Every;
import vos.TaskSearchVo;
import vos.TaskSearchVo2;

import com.aton.db.SessionFactory;
import com.aton.job.BaseJob;
import com.google.common.collect.Lists;

import enums.Sign;
import enums.TaskStatus;

/**
 * 
 * 推广池处理定时器.<br>
 * 推广池为一个逻辑上的概念，将DB中所有处于“待发布”状态的推广视为在一个池中，这些推广无法在页面上显示，故无法被接手；
 * 系统定时检查其中推广的状态，将满足特定条件的推广状态修改为“已发布”，使其可以在页面上显示，可以被接手。
 * 
 * 根据用户发布推广时设置的时间规则（每隔X分钟放出Y单），将推广【分批次】放出。
 * 1、定时检查满足条件的推广，将其从推广池中放出
 * 2、买家接手推广时，检查【该批次】的放出推广是否已全部被接手，并放入推广池
 * 
 * @author 尤齐春
 * @since 0.1
 * @created 2016年7月31日 
 */
@Every("1min")
public class TaskKeeper2 extends BaseJob {

    public static final Logger log = LoggerFactory.getLogger("taskpool");

    public void doJob() throws Exception {

        DateTime now = DateTime.now();
        String checkId = now.toString("yyyyMMddHHmm");
        log.info("=={}==Begin to Check Tasks in pool~~", checkId);

        SqlSession ss = SessionFactory.getSqlSessionForBatch();
        try {
            TaskMapper2 mapper = ss.getMapper(TaskMapper2.class);

            // 取出所有“待发布”状态的推广
            TaskSearchVo2 vo = TaskSearchVo2.newInstance().status(TaskStatus.WAIT_PUBLISH);
            List<Task2> list = mapper.selectListFromTaskPool(vo);
            if (list.isEmpty()) {
                log.info("==={}==Finished to Check Tasks in pool~ No WAIT_PUBLISH tasks", checkId);
                return;
            }

            // 检查每个推广上次发布的时间，将到期的加入待发布列表
            List<Task2> tasks = Lists.newArrayList();
            for (Task2 task : list) {
                if (task.isLastBatchPublishTimeOverdue()) {
                    tasks.add(task);
                }
            }
            if (tasks.isEmpty()) {
                log.info("==={}==Finished to Check Tasks in pool~ No overdue WAIT_PUBLISH tasks", checkId);
                return;
            }

            // 批量发布推广
            int batchSize = 1000;
            for (List<Task2> batchList : Lists.partition(tasks, batchSize)) {
                log.info("==={}==Batch Update {} tasks--", checkId, batchSize);
                Date rightNow = DateTime.now().toDate();
                for (Task2 t : batchList) {
                    Task2 task = Task2.instance(t.id).setStatus(TaskStatus.PUBLISHED).setLastBatchPublishTime(rightNow)
                        .modifyTime(rightNow);
                    mapper.updateById(task);
                    
                    // 更新页面上“推广列表(X)”处的可接手推广数
                    Task2.updateTaskCount(Sign.PLUS, t.publishTimerValue);
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
