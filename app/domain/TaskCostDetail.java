package domain;

import java.math.BigDecimal;
import java.util.Collections;
import java.util.Date;
import java.util.List;

import models.Task;
import models.mappers.TaskMapper;

import org.apache.commons.lang.BooleanUtils;
import org.apache.ibatis.session.SqlSession;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.aton.config.BizConstants;
import com.aton.db.SessionFactory;
import com.aton.util.CollectionUtils;
import com.aton.util.DateUtils;
import com.aton.util.NumberUtils;
import com.google.common.collect.Lists;

import enums.TaskStatus;

/**
 * 
 * 任务费用明细.
 * 
 * @author youblade
 * @since  v2.0
 * @created 2015年1月7日 下午3:09:03
 */
public class TaskCostDetail {
    
    public static final Logger log = LoggerFactory.getLogger(TaskCostDetail.class);

    public Long id;
    public String taskId;
    public String taskStatus;
    /** 商品价格 **/
    public String itemPrice;
    /** 购买件数 **/
    public String itemBuyNum;
    
    /*============
     * 押金部分
     */
    /** 退款保障金 **/
    public String deposit;
    /** 非包邮商品的运费押金 **/
    public String expressDeposit;
    
    /** 每单押金合计 **/
    public String orderPledge;
    
    /*============
     * 基础服务费用
     */
    public String pcOrderFee;
    public String mobileOrderFee;
    /** 快递费用合计 **/
    public String expressFeeSum;
    
    /** 基础服务费用合计（含快递费） **/
    public String bsFeeSum;
    
    /*============
     * 增值服务
     */
    /** 平台返款服务费用 **/
    public String sysRefundFee;
    /** 加速任务完成费用 **/
    public String speedTaskFee;
    /** 优先审单费用 **/
    public String speedExamineFee = "0";
    /** 每单加赏佣金 **/
    public String orderExtReward;
    /** 设置任务发布间隔费用 **/
    public String timerPublishFee = "0";
    /** 延长购买周期费用 **/
    public String buyingCycleFee = "0";
    
    /** 增值服务费用合计 **/
    public String vasFeeSum;
    
    public String pledgeSum;
    public String ingotSum;
    
    /**
     * 实际收取费用
     */
    public String totalVasIngot;
    public String totalIngot;
    
    public String publisTime;
    public String itemId;
    
    public int pcOrderNum;
    public int pcTaskCount;
    public int mobileOrderNum;
    public int mobileTaskCount;
    
    public static TaskCostDetail valueOf(Task t) {
        TaskCostDetail d = new TaskCostDetail();
        // TODO
        d.taskId = t.id.toString();
        d.taskStatus = t.status.title;
        d.itemId = t.itemId;
        d.itemPrice = NumberUtils.formatToStr(t.itemPrice / 100.0);
        d.itemBuyNum = String.valueOf(t.itemBuyNum);
        d.pcOrderNum = t.pcOrderNum;
        d.pcTaskCount = t.pcTaskCount;
        d.mobileOrderNum = t.mobileOrderNum;
        d.mobileTaskCount = t.mobileTaskCount;

        d.pcOrderFee = NumberUtils.formatToStr(t.baseOrderIngot / 100.0);
        // TODO check baseOrderIngot

        long mobileOrderFee = (long) (t.baseOrderIngot + BizConstants.TASK_MOBILE_INGOT * 100);
        d.mobileOrderFee = NumberUtils.formatToStr(mobileOrderFee / 100.0);

        // 注意：费用计算时以实际的买手任务数为准
        int totalTaskCount = t.pcTaskCount + t.mobileTaskCount;
        // 进行中的任务 费用计算时以总刷单数为准
        if (t.status == TaskStatus.PUBLISHED || t.status == TaskStatus.WAIT_PUBLISH) {
            totalTaskCount = t.pcOrderNum + t.mobileOrderNum;
        }
        // 快递费用合计
        BigDecimal expressFeeSum = BigDecimal.valueOf(t.expressIngot * totalTaskCount);
        d.expressFeeSum = expressFeeSum.movePointLeft(2).toString();
        /*
         * 基础服务费用合计（刷单费用+快递费）
         */
        long bsFeeSum = BigDecimal.valueOf(t.baseOrderIngot * t.pcTaskCount)
            .add(BigDecimal.valueOf(mobileOrderFee * t.mobileTaskCount))
            .add(expressFeeSum).longValue();

        d.bsFeeSum = NumberUtils.formatToStr(bsFeeSum / 100.0);

        // 增值服务费用合计：精确到分
        BigDecimal vasFeeDecCent = BigDecimal.valueOf(0);
        // 平台返款费用
        if (BooleanUtils.isTrue(t.sysRefund)) {
            long totalsysRefundFee = t.calculateSysRefundFee(totalTaskCount);
            d.sysRefundFee = NumberUtils.formatToStr(totalsysRefundFee / 100.0);
            vasFeeDecCent = vasFeeDecCent.add(BigDecimal.valueOf(totalsysRefundFee));
        }
        // 加速任务
        d.speedTaskFee = t.speedTaskIngot.toString();
        vasFeeDecCent = vasFeeDecCent.add(BigDecimal.valueOf(t.speedTaskIngot).movePointRight(2));

        // 加速审核
        if (t.speedExamine) {
            d.speedExamineFee = String.valueOf(BizConstants.TASK_SPEED_EXAMINE_INGOT);
            vasFeeDecCent = vasFeeDecCent.add(BigDecimal.valueOf(BizConstants.TASK_SPEED_EXAMINE_INGOT).movePointRight(2));
        }

        // 每单加赏佣金
        BigDecimal orderExtReward = BigDecimal.valueOf(t.extraRewardIngot);
        d.orderExtReward = orderExtReward.toString();
        vasFeeDecCent = vasFeeDecCent
            .add(orderExtReward.movePointRight(2).multiply(BigDecimal.valueOf(totalTaskCount)));

        // 定时发布
        if (NumberUtils.isGreaterThanZero(t.publishTimerInterval)) {
            d.timerPublishFee = String.valueOf(BizConstants.TASK_TIMING_PUBLISH_INGOT);
            vasFeeDecCent = vasFeeDecCent.add(BigDecimal.valueOf(BizConstants.TASK_TIMING_PUBLISH_INGOT).movePointRight(2));
        }
        
        // 购买周期
        if (NumberUtils.isGreaterThanZero(t.buyTimeInterval)) {
            t.buyTimeInterval = (t.buyTimeInterval != null) ? t.buyTimeInterval : 0;
            d.buyingCycleFee = String.valueOf(t.buyTimeInterval * totalTaskCount);
            vasFeeDecCent = vasFeeDecCent.add(new BigDecimal(d.buyingCycleFee).movePointRight(2));
        }

        /*
         * 增值服务费用合计
         */
        d.vasFeeSum = vasFeeDecCent.movePointLeft(2).setScale(2).toString();
        d.totalVasIngot = NumberUtils.formatToStr(t.vasIngot / 100.0);

        d.ingotSum = NumberUtils.formatToStr((bsFeeSum + vasFeeDecCent.longValue()) / 100.0);
        d.totalIngot = NumberUtils.formatToStr(t.totalIngot / 100.0);
        
        // Check
        if (t.status != TaskStatus.CANCLED && t.totalIngot != bsFeeSum + vasFeeDecCent.longValue()) {
            log.warn("任务[{}]的实际费用{}，与新计算出的总费用{}不一致，发布时间:{}", t.id, t.totalIngot, bsFeeSum + vasFeeDecCent.longValue(),
                DateUtils.formatToStr(t.publishTime));
        }
        return d;
    }
    
    
    public static List<TaskCostDetail> findByTaskMessage(String sellerNick, Date timeStart, Date timeEnd){
        SqlSession ss = SessionFactory.getSqlSession();
        try {
            TaskMapper mapper = ss.getMapper(TaskMapper.class);
            List<Task> tasks = mapper.selectForFinance(sellerNick, timeStart, timeEnd);
            if(CollectionUtils.isEmpty(tasks)){
                return Collections.EMPTY_LIST;
            }
            
            log.info("Export TaskCostDetail size={},seller={}", tasks.size(), sellerNick);
            List<TaskCostDetail> list = Lists.newArrayListWithExpectedSize(tasks.size());
            for (Task task : tasks) {
                list.add(TaskCostDetail.valueOf(task));
            }
            
            return list;
        } finally {
            ss.close();
        }
    }
}
