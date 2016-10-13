package jobs;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import models.FlowJsonModels;
import models.TrafficRecord;
import models.TrafficRecord.TrafficStatus;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import play.jobs.Every;
import play.jobs.On;
import vos.TrafficRecordVo;

import com.aton.job.BaseJob;
import com.aton.util.TrafficRecordUtil;
import com.aton.util.TrafficRecordUtil.Method;
/**
 * 
 * 
 * 把过时的任务设为已完成
 * 每天凌晨1点执行
 * @author fufei
 * @since  v2.0
 * @created 2015年1月17日 下午5:54:45
 */
@On("0 0 1 * * ?")
public class FinshedFlowTask extends BaseJob{
	private static final Logger log = LoggerFactory.getLogger(FinshedFlowTask.class);

	public void doJob() throws Exception {
		log.info("=====FinshedFlowTask Started=====");
		List<TrafficRecord> records = TrafficRecord.searchByStatus(new SimpleDateFormat("yyyy-MM-dd").format(new Date()));
		for (TrafficRecord trafficRecord : records) {
			TrafficRecord.getReturnTimes(trafficRecord);//最后再查询一次点击次数
			TrafficRecordVo vo=new TrafficRecordVo();
			vo.id=trafficRecord.id;
			vo.status=TrafficStatus.FINISHED.toString();
			TrafficRecord.modifyTraffic(vo);
		}
	}
}
