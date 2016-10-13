package models;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;
import java.util.Map;

import javax.persistence.Transient;

import models.mappers.BuyerTaskMapper;
import models.mappers.BuyerTaskStepMapper;
import models.mappers.TaskOrderMessageMapper;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.Validate;
import org.apache.ibatis.session.SqlSession;
import org.codehaus.jackson.util.BufferRecycler.ByteBufferType;
import org.joda.time.DateTime;

import vos.BuyerTaskStepVo;

import com.aton.config.BizConstants;
import com.aton.db.SessionFactory;
import com.aton.util.JsonUtil;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;

import enums.BuyerTaskStepType;
import enums.TaskStatus;

/**
 * 
 * 买手做任务的进度数据.
 * 
 * @author youblade
 * @since 0.1
 * @created 2014年8月21日 下午5:37:56
 */
public class BuyerTaskStep {

    public static final String TABLE_NAME = "buyer_task_step";

    public long id;
    public long buyerTaskId;
    public long buyerId;

    /**
     * 任务步骤
     */
    public BuyerTaskStepType type;
    /**
     * 任务步骤序号
     */
    public int no;
    /**
     * 任务进度数据：JSON
     */
    public String content;
    /**
     * 完成时间
     */
    public Date createTime;
    
    /**
     * 是否有效：用于控制是否在详情页显示
     * “平台返款”的任务会有“撤销”、“驳回”等操作，这些操作导致某些已完成的步骤需要回退
     */
    public Boolean isValid;
    /**
     * 修改时间
     */
    public Date modifyTime;
    
    @Transient
    public BuyerTaskStepVo stepVo;

    /**
     * 
     * 记录买手做任务的步骤.
     *
     * @param buyerTask
     * @since  v0.1.8
     * @author youblade
     * @created 2014年10月20日 上午11:17:41
     */
    public void create(BuyerTask buyerTask) {
        Validate.notNull(type);
        Validate.notNull(stepVo);
        Validate.notNull(buyerId);
        Validate.notNull(buyerTask.id);
        Validate.notNull(buyerTask.status);
        
        // 第五步：下单支付
        if (BuyerTaskStepType.ORDER_AND_PAY == type) {
            Validate.notNull(buyerTask.messageId);
        }
        
        SqlSession ss = SessionFactory.getSqlSessionWithoutAutoCommit();
        try {
            /*
             *  保存任务进度
             */
            Date now = DateTime.now().toDate();
            this.createTime = now;
            ss.getMapper(BuyerTaskStepMapper.class).insert(this);

            /*
             *  更新任务状态
             */
            BuyerTask bt = BuyerTask.instance(buyerTask.id).buyerId(buyerId).modifyTime(now);
            BuyerTaskMapper mapper = ss.getMapper(BuyerTaskMapper.class);
            
            // 第一步提交时更新状态为待支付
            if (BuyerTaskStepType.CHOOSE_ITEM == type && buyerTask.status == TaskStatus.RECIEVED) {
                mapper.updateByIdAndBuyerId(bt.status(TaskStatus.WAIT_PAY));
            }

            // 第五步后更新任务状态为待发货
            if (BuyerTaskStepType.ORDER_AND_PAY == type) {
            	Task task = Task.findById(mapper.selectById(bt.id).taskId);
				if (task.sysExpress) {//平台发货
					bt.status = TaskStatus.WAIT_SEND_GOODS;
				} else {
					bt.status = TaskStatus.EXPRESS_PRINT;//商家自己发货
				}
                bt.orderId = stepVo.orderNo.trim();
                bt.paidFee = new BigDecimal(stepVo.realPaidFee).multiply(BigDecimal.valueOf(100)).longValue();
                bt.messageId = buyerTask.messageId;
                mapper.updateByIdAndBuyerId(bt);

                // 更新该条订单留言使用次数
                if (buyerTask.messageId != null) {
                    TaskOrderMessageMapper tomMapper = ss.getMapper(TaskOrderMessageMapper.class);
                    tomMapper.updateUsedNumById(buyerTask.messageId);
                }
            }

            ss.commit();
        } finally {
            ss.close();
        }
    
    }

    /**
     * 
     * 获取任务步骤信息.
     *
     * @param buyerTaskId
     * @return
     * @since  0.1
     * @author youblade
     * @created 2014年8月23日 下午3:28:30
     */
    public static List<BuyerTaskStep> findByTaskId(long buyerTaskId) {
        List<BuyerTaskStep> list = null;
        SqlSession ss = SessionFactory.getSqlSession();
        try {
            BuyerTaskStepMapper mapper = ss.getMapper(BuyerTaskStepMapper.class);
            list = mapper.slelectByBuyerTaskId(buyerTaskId);
        } finally {
            ss.close();
        }
        
        for (BuyerTaskStep step : list) {
            if (StringUtils.isNotBlank(step.content)) {
                step.stepVo = JsonUtil.toBean(step.content, BuyerTaskStepVo.class);
            }
        }
        return list;
    }
    
    /**
     * 
     * 获取“平台返款”的买手任务的进度记录
     *
     * @param buyerTaskId
     * @return
     * @since  v1.7
     * @author youblade
     * @created 2014年12月4日 下午5:02:14
     */
    public static List<BuyerTaskStep> findByTaskIdForSysRefund(long buyerTaskId) {
        List<BuyerTaskStep> list = null;
        SqlSession ss = SessionFactory.getSqlSession();
        boolean rest=false;//判断是否是淘宝、天猫订单类型的任务
        try {
            BuyerTaskStepMapper mapper = ss.getMapper(BuyerTaskStepMapper.class);
            list = mapper.slelectByBuyerTaskId(buyerTaskId);
            BuyerTask buyerTask=BuyerTask.findById(buyerTaskId);
            if(buyerTask!=null){
            	rest=Task.isTaoBaoAndTmall(buyerTask.taskId);
            }
        } finally {
            ss.close();
        }
        
        // 将全部进度步骤全部放入列表中，包括未实际做的
        Map<BuyerTaskStepType, BuyerTaskStep> map = Maps.newLinkedHashMap();
        for (BuyerTaskStepType type : BuyerTaskStepType.values()) {
        	if(rest&&(type==BuyerTaskStepType.CHOOSE_ITEM||type==BuyerTaskStepType.VIEW_AND_INQUIRY)){//不是淘宝、天猫订单类型的任务，跳过2、3步
        		continue;
        	}
            map.put(type, BuyerTaskStep.valueOf(type));
        }
        
        for (BuyerTaskStep step : list) {
            if (StringUtils.isNotBlank(step.content)) {
                step.stepVo = JsonUtil.toBean(step.content, BuyerTaskStepVo.class);
            }
            map.put(step.type, step);
        }
        return Lists.newArrayList(map.values());
    }
    
    /**
     * 
     * 获取买手做任务的上个步骤.
     *
     * @param buyerTaskId
     * @return
     * @since  v0.1.8
     * @author youblade
     * @created 2014年10月20日 下午4:01:10
     */
    public static BuyerTaskStep findBuyerLastStep(long buyerTaskId,long buyerId) {
        SqlSession ss = SessionFactory.getSqlSession();
        try {
            BuyerTaskStepMapper mapper = ss.getMapper(BuyerTaskStepMapper.class);
            return mapper.selectByTaskIdAndStepBuyerId(buyerTaskId,buyerId);
        } finally {
            ss.close();
        }
    }

    public static BuyerTaskStep newInstance(long buyerTaskId, long buyerId) {
        BuyerTaskStep sts = new BuyerTaskStep();
        sts.buyerTaskId = buyerTaskId;
        sts.buyerId = buyerId;
        return sts;
    }
    
    public static BuyerTaskStep instance(long id) {
        BuyerTaskStep sts = new BuyerTaskStep();
        sts.id = id;
        return sts;
    }
    
    public static BuyerTaskStep valueOf(BuyerTaskStepType type) {
        BuyerTaskStep sts = new BuyerTaskStep();
        return sts.type(type);
    }
    
    public BuyerTaskStep type(BuyerTaskStepType type) {
        this.type = type;
        this.no = type.getOrder();
        return this;
    }
    
    public BuyerTaskStep content(BuyerTaskStepVo vo) {
        if (vo != null) {
            this.stepVo = vo;
            this.content = JsonUtil.toJson(vo);
        }
        return this;
    }

    public BuyerTaskStep valid(boolean isValid) {
        this.isValid = isValid;
        return this;
    }
    
    public BuyerTaskStep createTime(Date time) {
        this.createTime = time;
        return this;
    }
    
    public BuyerTaskStep modifyTime(Date time) {
        this.modifyTime = time;
        return this;
    }
}
