package jobs;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import models.FlowJsonModels;
import models.TrafficRecord;
import models.TrafficRecord.TrafficStatus;
import models.UserFlowRecord;

import org.joda.time.DateTime;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import play.jobs.Every;
import play.jobs.On;
import vos.TrafficRecordVo;

import com.aton.job.BaseJob;
import com.aton.util.StringUtils;
import com.aton.util.TrafficRecordUtil;
import com.aton.util.TrafficRecordUtil.Method;


/**
 * 
 * 退还没有点完的流量任务
 * 
 * @author fufei
 * @since  v3.2
 * @created 2015年4月13日 下午3:21:01
 */
@On("0 0 2 * * ?")
public class RefoundFlow extends BaseJob{
    private static final Logger log = LoggerFactory.getLogger(RefoundFlow.class);

    public void doJob() throws Exception {
        log.info("=====RefoundFlow Started=====");
        List<TrafficRecord> records = TrafficRecord.listRefoundFlow(new SimpleDateFormat("yyyy-MM-dd").format(DateTime
            .now().minusDays(1).toDate()));
        log.info("=====records size====="+records.size());
        for (TrafficRecord trafficRecord : records) {
            if(trafficRecord.userId>0)
            UserFlowRecord.refundFlow(trafficRecord);
        }
    }
    
}
