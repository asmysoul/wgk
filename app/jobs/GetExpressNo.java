package jobs;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import models.BuyerTask;
import models.FlowJsonModels;
import models.Region;
import models.TrafficRecord;
import models.TrafficRecord.TrafficStatus;

import org.joda.time.DateTime;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import play.jobs.Every;
import vos.FaBaoGuoVo;
import vos.OrderExpress;
import vos.TaskSearchVo;
import vos.TrafficRecordVo;

import com.aton.config.AppMode;
import com.aton.job.BaseJob;
import com.aton.util.FaBaoGuoUtil;
import com.aton.util.FaBaoGuoUtil.FaBaoGuoReturn;
import com.aton.util.TrafficRecordUtil;
import com.aton.util.TrafficRecordUtil.Method;

import enums.ExpressType;
import enums.TaskStatus;

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
public class GetExpressNo extends BaseJob {

    private static final Logger log = LoggerFactory.getLogger(GetExpressNo.class);

    public void doJob() throws Exception {
        log.info("=====GetExpressNo Started=====");
        TaskSearchVo vo = TaskSearchVo.newInstance();
        vo.exportNo = 500;
        vo.status(TaskStatus.WAIT_SEND_GOODS);
        vo.expressType = ExpressType.YDKD;
        List<OrderExpress> orders = BuyerTask.findOrders(vo);
        List<FaBaoGuoVo> baoGuoVos = FaBaoGuoUtil.convert(orders);
        for (FaBaoGuoVo guoVo : baoGuoVos) {
            FaBaoGuoReturn baoGuoReturn = FaBaoGuoUtil.execute(guoVo);
            if (baoGuoReturn == null) {
                log.info("fa bao guo return null!");
                continue;
            }
            log.info("retrun code "+baoGuoReturn.res+",express_no:"+baoGuoReturn.express_no);
            if (baoGuoReturn.res == 0) {
                BuyerTask btFromLocal = BuyerTask.findByOrderId(guoVo.order_sn);
                if (btFromLocal == null) {
                    log.info("check order number " + guoVo.order_sn + " not exsit");
                    continue;
                }
                btFromLocal.status = TaskStatus.EXPRESS_PRINT;
                btFromLocal.expressNo = baoGuoReturn.express_no;
                btFromLocal.modifyTime = DateTime.now().toDate();
                BuyerTask.modifyYDKD(btFromLocal);
                
            }else {
                log.info("retrun code "+baoGuoReturn.res);
                continue;
            }
        }
    }
   
}
