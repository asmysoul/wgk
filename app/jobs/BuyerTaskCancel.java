package jobs;

import java.util.Date;
import java.util.List;

import models.BuyerTask;
import models.mappers.BuyerTaskMapper;

import org.apache.ibatis.session.SqlSession;
import org.joda.time.DateTime;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import play.jobs.Every;

import com.aton.config.BizConstants;
import com.aton.db.SessionFactory;
import com.aton.job.BaseJob;

/**
 * 
 * 
 * 系统自动撤销超过规定时间的已接手未支付的买手任务
 * 
 * @author moloch
 * @since v0.1
 * @created 2014-9-23 下午3:10:00
 */
@Every("10min")
public class BuyerTaskCancel extends BaseJob {

	private static final Logger log = LoggerFactory.getLogger(BuyerTaskCancel.class);

	public void doJob() throws Exception {

		// 操作时间点 这个时间之前的已接手没支付的任务，全部撤销
		Date dateTip = DateTime.now().minusHours(BizConstants.BUYER_TASK_AUTO_CANCEL_TIME).toDate();
		log.info("cancel buyerTask start");
		SqlSession ss = SessionFactory.getSqlSessionWithoutAutoCommit();
		try {
			BuyerTaskMapper btMapper = ss.getMapper(BuyerTaskMapper.class);
			List<BuyerTask> bts = btMapper.selectForCancel(dateTip);

			// 循环撤销任务
			for (BuyerTask bt : bts) {
				bt.cancelStep(ss);
				ss.commit();
			}
			log.info("cancel buyerTask end");
		} finally {
			ss.close();
		}
	}
}
