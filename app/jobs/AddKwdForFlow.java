package jobs;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import models.FlowJsonModels;
import models.Region;
import models.TrafficRecord;
import models.TrafficRecord.TrafficStatus;

import org.joda.time.DateTime;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import play.jobs.Every;
import vos.TrafficRecordVo;

import com.aton.config.AppMode;
import com.aton.job.BaseJob;
import com.aton.util.TrafficRecordUtil;
import com.aton.util.TrafficRecordUtil.Method;

/**
 * 
 * 
 * 添加待处理流量任务，10分中执行一次
 * 
 * @author fufei
 * @since v2.0
 * @created 2015年1月17日 上午11:37:03
 */
@Every("10min")
public class AddKwdForFlow extends BaseJob {
	private static final Logger log = LoggerFactory.getLogger(AddKwdForFlow.class);

	public void doJob() throws Exception {
		log.info("=====AddFlow Started=====");
		List<TrafficRecord> records = TrafficRecord.findWaitByStatus(new SimpleDateFormat("yyyy-MM-dd").format(DateTime.now().toDate()));
		log.info("流量任务条数"+records.size());
		for (TrafficRecord trafficRecord : records) {
			String rest = TrafficRecordUtil.flow(trafficRecord, Method.ADD);
			if (!"".equals(rest)) {
				FlowJsonModels jsonModels = TrafficRecordUtil.formatToJSON(rest);
				// 成功操作
				log.info("return status"+jsonModels.status);
				if ("success".equals(jsonModels.status)) {
					TrafficRecordVo vo = new TrafficRecordVo();
					vo.id = trafficRecord.id;
					vo.status = TrafficStatus.PROCESSING.toString();
					log.info("result:" + rest);
					if (jsonModels.data.id != null) {
						vo.kid = Long.parseLong(jsonModels.data.id);
						TrafficRecord.modifyTraffic(vo);
						log.info("add flow success");
					}
				}
			} else {
				log.info("id：" + trafficRecord.id + " is faild ! result ：" + rest);
			}
		}
	}
}
