package jobs;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import models.FlowJsonClickModel;
import models.FlowJsonClickModel.FlowJsonClicks;
import models.TrafficRecord;
import models.TrafficRecord.TrafficStatus;

import org.joda.time.DateTime;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import play.jobs.Every;
import vos.TrafficRecordVo;

import com.aton.job.BaseJob;
import com.aton.util.TrafficRecordUtil;

/**
 * 
 * 
 * 获取正在处理的流量任务点击次数 .30分钟执行一次
 * 
 * @author fufei
 * @since v2.0
 * @created 2015年1月17日 下午1:50:58
 */
@Every("30min")
public class FlowClickTimes extends BaseJob {
	private static final Logger log = LoggerFactory.getLogger(FlowClickTimes.class);

	public void doJob() throws Exception {
		log.info("=====FlowClickTimes Started=====");
		List<TrafficRecord> records = TrafficRecord.findByStatus(TrafficStatus.PROCESSING.toString(),new SimpleDateFormat("yyyy-MM-dd").format(DateTime.now().toDate()));
		for (TrafficRecord trafficRecord : records) {
			TrafficRecord.getReturnTimes(trafficRecord);
		}
	}
	
}
