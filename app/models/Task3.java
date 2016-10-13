package models;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

import javax.persistence.Transient;

import jobs.TaskKeeper;
import models.SellerPledgeRecord.PledgeAction;
import models.TrafficRecord.TrafficStatus;
import models.mappers.BuyerTaskMapper3;
import models.mappers.SellerPledgeRecordMapper;
import models.mappers.TaskExamineLogMapper;
import models.mappers.TaskItemSearchPlanMapper3;
import models.mappers.TaskMapper;
import models.mappers.TaskMapper3;
import models.mappers.TaskOrderMessageMapper3;
import models.mappers.TrafficRecordMapper;
import models.mappers.UserFlowRecordMapper;
import models.mappers.UserIngotRecordMapper;
import net.sf.oval.constraint.NotNull;

import org.apache.commons.lang.BooleanUtils;
import org.apache.commons.lang.Validate;
import org.apache.ibatis.session.SqlSession;
import org.joda.time.DateTime;
import org.joda.time.LocalDate;
import org.joda.time.Minutes;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import play.cache.Cache;
import vos.BuyerTakeTaskVo;
import vos.CancelTaskVo;
import vos.CancelTaskVo3;
import vos.FaBaoGuoVo;
import vos.Page;
import vos.SearchSystemCountVo;
import vos.SearchTakenCount;
import vos.SellerPutTimeVo;
import vos.SellerTaskVo3;
import vos.TaskCountVo3;
import vos.TaskSearchVo3;
import vos.TrafficRecordVo;

import com.aton.config.BizConstants;
import com.aton.config.CacheType;
import com.aton.config.ReturnCode;
import com.aton.db.SessionFactory;
import com.aton.util.CacheUtil;
import com.aton.util.CollectionUtils;
import com.aton.util.DateUtils;
import com.aton.util.MixHelper;
import com.aton.util.NumberUtils;
import com.aton.util.Pandora;
import com.aton.util.StringUtils;
import com.aton.util.UploadImgUtil;
import com.aton.util.UrlUtil;
import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.Lists;
import com.google.common.collect.Multimap;

import enums.CollectionType;
import enums.Device;
import enums.ExpressType;
import enums.Platform;
import enums.Platform3;
import enums.Sign;
import enums.TaskListType;
import enums.TaskStatus;
import enums.TaskType;
import enums.TaskType3;

/**
 * 
 * 用户分【卖家】【买手】两种类型.
 * 
 * @author youblade
 * @since 0.1
 * @created 2014年7月9日 下午3:32:58
 */
public class Task3 {

	private static final Logger log = LoggerFactory.getLogger(Task3.class);
	private static final Logger cacheLog = LoggerFactory.getLogger("cache");
	
	public static final String TABLE_NAME = "task3";

	public Long id;
	public Platform3 platform;
	@Transient
    public String collectionTypeStr;
	@Transient
    public int genderIngot;
	 @Transient
	    public int buyerLocationIngot;
	/**
	 * 任务类型：订单、聚划算...
	 */
	public TaskType3 type;
	@Transient
	public String typeStr;
	@Transient
	public boolean isJHS;
	
	public String amoy;
	
	 /**
     * 配置收藏加购
     */
    public CollectionType collectionType;
	
	
	public Long shopId;
	
	
	
	/**
	 * 店铺名字：店铺的名字可能会修改，必须记录；同时方便查询和展示
	 */
	public String shopName;
	public String itemId;
	public String itemTitle;
	public String itemUrl;
	public String itemProps;
	public long itemPrice;
	public Long itemDisplayPrice;
	/**
	 * 每单购买件数
	 */
	public int itemBuyNum;
	public String itemPicUrl;
	public String itemPic;
	/**
	 * 直通车商品图片
	 */
	public String itemSubwayPicUrl;
	public String itemSubwayPic;
	
	public Long itemSearchMinPrice;
	public Long itemSearchMaxPrice;
	public String itemSearchLocation;
	
	public List<TaskItemSearchPlan3> searchPlans;

	
	  /**
     * 老杨新增 配置性别
     */
    @Transient
    public  boolean isgender;
    public String genderConfig;
    
    /**
     * 老杨新增 配置购买位置
     */
    @Transient
    public  boolean isLocation;
    public String buyerLocationConfig;
    
    
	
	/**
	 * 附加宝贝
	 */
	public String additionalItemTitle;
	public String additionalItemUrl;
	public String additionalItemProps;
	public Long additionalItemPrice;
	public Integer additionalItemBuyNum;
	public String additionalItemSearchKeyword;
	public String additionalItemPicUrl;
	public String additionalItemPic;
	
	/**
	 * 是否包邮
	 */
	public Boolean isFreeShipping;
	public String orderMessages;
	/**
	 * 任务总数（刷单数）
	 */
	public int totalOrderNum;
	
	/** PC端任务数 */
	public Integer pcOrderNum;
	/** 移动端任务数 */
	public Integer mobileOrderNum;
	public String goodCommentWords;
	
	/** PC端已接手任务数 */
	public Integer pcTakenCount;
	/** 移动端已接手任务数 */
	public Integer mobileTakenCount;
	//是否是平台发快递
	public Integer expressIngot;
	//快递重量
	public Double expressWeight;
	
	
	//二维码
	public String qrcode;
	
	/**
	 * 经验值（展示用）
	 */
	@Transient
	public int experience;
	
	/**
	 * 加赏佣金（全部付给买手）：X个金币
	 * 【注意】精确到元
	 */
	public Integer extraRewardIngot;
	
	//--------------------------------------------------
	/**
	 * 刷单服务每单的费用（单位精确到分）：<br>
	 * 1、根据商品价格按特定公式计算所得金额，其中10%为平台分成，90%为买手佣金<br>
	 * 2、单位精确到分<br>
	 * 【注意】移动端订单的佣金为上述费用90% +0.5 金币
	 */
	public Long baseOrderIngot;
	
	/** 
	 * 增值服务费用总计，包括以下项目：
	 * 1、多关键词搜索（按每单计价）
	 * 2、加速任务完成+
	 * 3、加赏佣金（按每单计价）+
	 * 4、优先审核任务+
	 * 5、设置任务发布间隔+
	 * 6、延长买手购物周期（按每单计价）+
	 * 7、好评关键词（按每单计价）
	 * 8、快速返款给买手
	 */
    public Long vasIngot;
    
	/**
     * 费用总计：刷单服务费用 + 增值服务费用
     * 精确到分
     */
    public Long totalIngot;
	
	//--------------------------------------------------
	/**
	 * 每单押金：商品单价*购买件数+（附加商品单价*购买件数）+ 退款保证金（前两者总和的5%） + 非包邮商品运费（押金10元）
	 * 精确到分
	 */
	public Long pledge;
	/**
	 * 本次任务押金：每单押金*任务单数，任务完成后【平台】会退还给【商家】
	 * 精确到分
	 */
	public Long totalPledge;
	/**
	 * 平台操作卖家押金，直接返款给买手
	 */
	public Boolean sysRefund;
	/**
	 * 延时发布 时间间隔，单位为min
	 */
	public Integer delaySpan;
	/**
	 * 加速完成任务的佣金（平台推荐的任务）
	 */
	public Integer speedTaskIngot;
	/**
	 * 是否使用优先审单服务：发布的任务会优先审核
	 */
	public Boolean speedExamine;
	
	/**
	 * 任务发布定时器的时间间隔：单位为min
	 */
	public Integer publishTimerInterval;
	/**
	 * 任务发布定时器的值（每次发多少单）
	 */
	public Integer publishTimerValue;
	/**
	 * 限制购买周期
	 */
	public Integer buyTimeInterval;
	
	public int finishedCount;
	public TaskStatus status;
	public Long sellerId;
	public String sellerNick;
	
    /**
     * 该任务是否已付款：撤销任务后要更新该值
     */
    public Boolean isPaid;
    
	/**
	 * 任务发布时间（同时也是审核通过的时间）
	 */
	public Date publishTime;
	    
	/**
	 * 上个批次的任务发布时间（仅针对定时任务）
	 */
	public Date lastBatchPublishTime;
//	public Date lastBatchPublishNo;
	
	public Date examineTime;
	public Date createTime;
	public Date modifyTime;
	public Boolean sysExpress;//是否平台快递
	
	public String qq;//联系qq号码

    /*
     * =============================
     * 仅显示用，非持久化字段
     */
    @Transient
    public List<String> itemPropsList;
    @Transient
    public List<String> orderMessagesList;
    public List<TaskOrderMessage> messages;
    
    public transient String publishTimeStr;
    public transient String createTimeStr;
    /**
     * 任务编号：任务ID的字符串
     */
    @Transient
    public String idStr;
    
    
    /** PC端任务佣金: 90% */
    @Transient
    public float rewardIngot;
    /** 移动端任务佣金: */
    @Transient
    public float mobieRewardIngot;
    
    @Transient
    public String statusTitle;
    @Transient
    public String platformTitle;
    //标记改任务可不可以接    true(可以接)
    @Transient
    public boolean tags;
    @Transient
    public String reason;
    //任务列表初始买号标记
    @Transient
    public long buyerAccountId;
	/**
     * 有效（非撤销）的“PC端”买手任务数
     */
    @Transient
    public int pcTaskCount;
    /**
     * 有效（非撤销）的“移动端”买手任务数
     */
    @Transient
    public int mobileTaskCount;
    
    /**
     * 配置收藏加购任务要求
     */
    public String collectionRequest;
    
    
    public String taskRequest;
    
    /* 该大任务对应的商家的 */
    public String sellerAdminName;
    /**
     * 任务图文评论的图片
     */
    public String goodCommentImg;
    
    /**
     * 快递类型
     */
    public ExpressType expressType;
    
    @Transient
    public String expressTypeStr;
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
	@Override
	public String toString() {
		return "Task3 [id=" + id + ", platform=" + platform
				+ ", collectionTypeStr=" + collectionTypeStr + ", genderIngot="
				+ genderIngot + ", buyerLocationIngot=" + buyerLocationIngot
				+ ", type=" + type + ", typeStr=" + typeStr + ", isJHS="
				+ isJHS + ", amoy=" + amoy + ", collectionType="
				+ collectionType + ", shopId=" + shopId + ", shopName="
				+ shopName + ", itemId=" + itemId + ", itemTitle=" + itemTitle
				+ ", itemUrl=" + itemUrl + ", itemProps=" + itemProps
				+ ", itemPrice=" + itemPrice + ", itemDisplayPrice="
				+ itemDisplayPrice + ", itemBuyNum=" + itemBuyNum
				+ ", itemPicUrl=" + itemPicUrl + ", itemPic=" + itemPic
				+ ", itemSubwayPicUrl=" + itemSubwayPicUrl + ", itemSubwayPic="
				+ itemSubwayPic + ", itemSearchMinPrice=" + itemSearchMinPrice
				+ ", itemSearchMaxPrice=" + itemSearchMaxPrice
				+ ", itemSearchLocation=" + itemSearchLocation
				+ ", searchPlans=" + searchPlans + ", isgender=" + isgender
				+ ", genderConfig=" + genderConfig + ", isLocation="
				+ isLocation + ", buyerLocationConfig=" + buyerLocationConfig
				+ ", additionalItemTitle=" + additionalItemTitle
				+ ", additionalItemUrl=" + additionalItemUrl
				+ ", additionalItemProps=" + additionalItemProps
				+ ", additionalItemPrice=" + additionalItemPrice
				+ ", additionalItemBuyNum=" + additionalItemBuyNum
				+ ", additionalItemSearchKeyword="
				+ additionalItemSearchKeyword + ", additionalItemPicUrl="
				+ additionalItemPicUrl + ", additionalItemPic="
				+ additionalItemPic + ", isFreeShipping=" + isFreeShipping
				+ ", orderMessages=" + orderMessages + ", totalOrderNum="
				+ totalOrderNum + ", pcOrderNum=" + pcOrderNum
				+ ", mobileOrderNum=" + mobileOrderNum + ", goodCommentWords="
				+ goodCommentWords + ", pcTakenCount=" + pcTakenCount
				+ ", mobileTakenCount=" + mobileTakenCount + ", expressIngot="
				+ expressIngot + ", expressWeight=" + expressWeight
				+ ", qrcode=" + qrcode + ", experience=" + experience
				+ ", extraRewardIngot=" + extraRewardIngot
				+ ", baseOrderIngot=" + baseOrderIngot + ", vasIngot="
				+ vasIngot + ", totalIngot=" + totalIngot + ", pledge="
				+ pledge + ", totalPledge=" + totalPledge + ", sysRefund="
				+ sysRefund + ", delaySpan=" + delaySpan + ", speedTaskIngot="
				+ speedTaskIngot + ", speedExamine=" + speedExamine
				+ ", publishTimerInterval=" + publishTimerInterval
				+ ", publishTimerValue=" + publishTimerValue
				+ ", buyTimeInterval=" + buyTimeInterval + ", finishedCount="
				+ finishedCount + ", status=" + status + ", sellerId="
				+ sellerId + ", sellerNick=" + sellerNick + ", isPaid="
				+ isPaid + ", publishTime=" + publishTime
				+ ", lastBatchPublishTime=" + lastBatchPublishTime
				+ ", examineTime=" + examineTime + ", createTime=" + createTime
				+ ", modifyTime=" + modifyTime + ", sysExpress=" + sysExpress
				+ ", qq=" + qq + ", itemPropsList=" + itemPropsList
				+ ", orderMessagesList=" + orderMessagesList + ", messages="
				+ messages + ", idStr=" + idStr + ", rewardIngot="
				+ rewardIngot + ", mobieRewardIngot=" + mobieRewardIngot
				+ ", statusTitle=" + statusTitle + ", platformTitle="
				+ platformTitle + ", tags=" + tags + ", reason=" + reason
				+ ", buyerAccountId=" + buyerAccountId + ", pcTaskCount="
				+ pcTaskCount + ", mobileTaskCount=" + mobileTaskCount
				+ ", collectionRequest=" + collectionRequest + ", taskRequest="
				+ taskRequest + ", sellerAdminName=" + sellerAdminName
				+ ", goodCommentImg=" + goodCommentImg + ", expressType="
				+ expressType + ", expressTypeStr=" + expressTypeStr + "]";
	}


	/**
	 * 
	 * 保存任务.
	 *
	 * @since  v0.6.8
	 * @author youblade
	 * @created 2014年7月21日 下午2:01:20
	 */
public void save() {
    	
    	this.sysExpress=false;
    	
        boolean isNew = false;
        if (this.id == null) {
            isNew = true;
            this.id = Pandora.getInstance().nextId();
        }
        
        SqlSession ss = SessionFactory.getSqlSessionForBatch();
        try {
            /*
             * 处理关键词方案
             */
            TaskItemSearchPlanMapper3 planMapper = ss.getMapper(TaskItemSearchPlanMapper3.class);
            TaskOrderMessageMapper3 messageMapper = ss.getMapper(TaskOrderMessageMapper3.class);
            //若保存的是旧任务，则先删掉其之前的搜索方案
			if (!isNew) {
				planMapper.deleteByTaskId(this.id);

				// 删除之前的订单留言
				messageMapper.deleteByTaskId(this.id);
			}

            // 保存新的搜索关键词方案
            Date now = DateTime.now().toDate();
			if (CollectionUtils.isNotEmpty(searchPlans)) {
				for (TaskItemSearchPlan3 plan : searchPlans) {
					// 跳过关键词为空的记录
					if (plan == null || StringUtils.isBlank(plan.word)) {
						continue;
					}
					plan.taskId = this.id;
					plan.takenNum = 0;
					plan.createTime = now;
					plan.modifyTime = now;
					planMapper.insert(plan);
				}
			}
            
			/*
			 * 保存订单留言
			 */
			
			TaskOrderMessage3 tom = TaskOrderMessage3.newInstance(this.id);
			tom.usedNum = 0;
			for (String message : Lists.newArrayList(this.orderMessages.split(StringUtils.COMMA))) {
				tom.message = message;
				tom.createTime = DateTime.now().toDate();
				messageMapper.insert(tom);
			}
			
			
            /*
			 * 保存任务
			 */
			TaskMapper3 mapper = ss.getMapper(TaskMapper3.class);
			// 记录商品ID
			// 计算并设置任务押金及费用等
			this.totalOrderNum = this.pcOrderNum + this.mobileOrderNum;
			calculateAndSetTaskPledgeAndIngot();
			
			

			// 上传直通车图片
			uploadAndSetSubwayItemPic(mapper, isNew);
			if(StringUtils.isNotEmpty(this.goodCommentImg))
			this.goodCommentImg=StringUtils.substring(goodCommentImg, 0, goodCommentImg.lastIndexOf(","));
			this.modifyTime = now;
			if (isNew) {
				this.status = TaskStatus.WAIT_EDIT;
				this.pcTakenCount = 0;
				this.mobileTakenCount = 0;
				this.isPaid = false;
				this.createTime = now;
				mapper.insert(this);
			} else {
				mapper.updateById(this);
			}
            ss.commit();
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            ss.rollback();
            throw new RuntimeException("任务保存失败~~");
        } finally {
            ss.close();
        }
    }


    /**
     * 
     * 更新
     *
     * @since  v3.4
     * @author fufei
     * @created 2015年5月8日 下午4:41:51
     */
    public void update() {
        SqlSession ss = SessionFactory.getSqlSessionForBatch();
        try {
            TaskMapper3 mapper = ss.getMapper(TaskMapper3.class);
            mapper.updateById(this);
            ss.commit();
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            ss.rollback();
            throw new RuntimeException("任务更新失败~~");
        } finally {
            ss.close();
        }
    }
	/**
	 * 
	 * 获取ItemId
	 *
	 * @param platform
	 * @param url
	 * @return
	 * @since  v2.0
	 * @author fufei
	 * @created 2015年2月7日 下午2:42:53
	 */
	public static String getItemIdByPlatform(Platform3 platform, String url) {
		String itemId = "";
		switch (platform) {
		case TAOBAO:
			itemId = UrlUtil.getParam("id", url);
			break;
		case TMALL:
			itemId = UrlUtil.getParam("id", url);
			break;
		default:
			break;
		}
		return itemId;
	}
	
    /*
     * 上传直通车图片到七牛
     */
	private void uploadAndSetSubwayItemPic(TaskMapper3 mapper,boolean isNew) throws IOException {
        if (this.type != TaskType3.SUBWAY) {
            return;
        }
        
        // 修改旧任务时检查是否有改动
        if (!isNew) {
            String oldSubwayPicUrl = mapper.selectFieldsById("id,item_subway_pic_url", id).itemSubwayPicUrl;
            if (this.itemSubwayPicUrl.equals(oldSubwayPicUrl)) {
                return;
            }
        }

       this.itemSubwayPic=StringUtils.substring(this.itemSubwayPicUrl, 0, this.itemSubwayPicUrl.lastIndexOf(","));
      
    }
    
   
    
    /**
     * 计算一单任务的押金：商品价格*购买件数+5%的退款保证金
     */
    public long calculateItemPledge(){
        long buyerTaskPaidFee = this.itemPrice * this.itemBuyNum;
        return new BigDecimal(buyerTaskPaidFee).multiply(BigDecimal.valueOf(BizConstants.TASK_INSURANCE_RATE))
        	.setScale(0, BigDecimal.ROUND_HALF_UP).add(BigDecimal.valueOf(buyerTaskPaidFee)).longValue();
    }
    
    /**
     * 计算该任务的“平台返款”服务费：
     */
    public long calculateSysRefundFee(int totalOrderNum) {
        // 先对刷单金额*0.6%计算出的结果进行高位取舍后，再乘以整数部分，保证每次运算【基础金额】不变
        return new BigDecimal(itemPrice * itemBuyNum).multiply(BigDecimal.valueOf(BizConstants.TASK_SYS_REFUND))
            .setScale(0, BigDecimal.ROUND_UP).longValue()* totalOrderNum
            ;
    }
  
    /**
     * 撤销子任务的“平台返款”服务费：刷单金额(商品价格*购买件数)*0.6%
     */
    public long calculateSysRefundBuyerTaskFee() {
        // 先对刷单金额*0.6%计算出的结果进行高位取舍后，再乘以整数部分，保证每次运算【基础金额】不变
        return new BigDecimal(itemPrice * itemBuyNum).multiply(BigDecimal.valueOf(BizConstants.TASK_SYS_REFUND))
            .setScale(0, BigDecimal.ROUND_HALF_UP).longValue()
            ;
    }
    
    /*
     * 计算并设置本次任务所需押金及金币
     */
    public void calculateAndSetTaskPledgeAndIngot() {
    	/*
         * 任务押金：<br>
         * 购买商品的垫付资金 + 退款保证金（前两者总和的5%）+非包邮商品的邮费押金
         */
        // TODO 附加商品暂未计入
//        this.pledge = calculateItemPledge();
//        if (BooleanUtils.isFalse(this.isFreeShipping)) {
//            this.pledge += (BizConstants.TASK_EXPRESS_PLEDGE * 100);
//        }
//        // 押金总计
//        this.totalPledge = this.pledge * (this.pcOrderNum + this.mobileOrderNum);

        
        /*
         * 刷单服务费用（PC与mobile订单需要分开计算）
         */
        // 按照特定公式计算出的每单刷单基础费用
        //this.baseOrderIngot = computeFeePerOrder(this.itemPrice * this.itemBuyNum,this.platform,this.type);
        long mobileOrderIngot = BizConstants.TASK_MOBILE_INGOTS*100;      //手机价格
        //0.8
        double serviceFee = (BizConstants.TASK_PC_INGOTS*100) * this.pcOrderNum + mobileOrderIngot * this.mobileOrderNum;
        
        //平台服务费
        serviceFee += (BizConstants.TASK_SERVICE_INGOTS*100);
        // 多关键词搜索(不再收费)
//        serviceFee += countExtraSearchPlan() * (BizConstants.TASK_SEARCH_PLAN_INGOT * 100);
        
        /*
         * 增值服务费用
         */
        this.pledge=NumberUtils.LONG_ZERO;
        this.totalPledge=NumberUtils.LONG_ZERO;
        this.baseOrderIngot= NumberUtils.LONG_ZERO;
        if(this.pcOrderNum>0){
        	 this.baseOrderIngot= (long)BizConstants.TASK_PC_INGOTS;
        }
        if(this.mobileOrderNum>0){
       	 this.baseOrderIngot= (long)BizConstants.TASK_MOBILE_INGOTS;
        }
        this.vasIngot = NumberUtils.LONG_ZERO;
//        // 加速任务完成、加赏任务佣金、
//        if (NumberUtils.isGreaterThanZero(this.speedTaskIngot)) {
//            this.vasIngot += (this.speedTaskIngot * 100);
//        }
        if (NumberUtils.isGreaterThanZero(this.extraRewardIngot)) {
            this.vasIngot += (this.extraRewardIngot * 100) * totalOrderNum;
        }
        //////
//        if (StringUtils.isNotBlank(this.genderConfig)) {
//        	 this.vasIngot += (this.genderIngot * 100) * totalOrderNum;
//        }
//        
//        if (StringUtils.isNotBlank(this.buyerLocationConfig)) {
//        	this.vasIngot += (this.buyerLocationIngot * 100) * totalOrderNum;
//        }
        System.out.println("===this.genderConfig======"+this.genderConfig);
        if(StringUtils.isNotBlank(this.genderConfig)){
        	this.vasIngot += (long)(BizConstants.TASK_GNDER_INGOT * 100) * totalOrderNum;
        }
        System.out.println("===this.buyerLocationConfig======"+this.buyerLocationConfig);
        if(StringUtils.isNotBlank(this.buyerLocationConfig)){
        	this.vasIngot += (long)(BizConstants.TASK_BUYERLOCATION_INGOT * 100) * totalOrderNum;
        }
//        if(this.collectionType!=null){
//        	this.vasIngot += (BizConstants.TASK_COLLECTION_INGOT * 100) * totalOrderNum;
//        }
        ////
        
        
        
        
        // 延长买手购买同一商品的周期：1金币/月
//        if (NumberUtils.isGreaterThanZero(this.buyTimeInterval)) {
//            this.vasIngot += (this.buyTimeInterval * 100) * totalOrderNum;
//        }
        //快速返款给买手
//        if(BooleanUtils.isTrue(this.sysRefund)){
//            this.vasIngot += this.calculateSysRefundFee(totalOrderNum); 
//        }
        
        // 优先审核
//        if (BooleanUtils.isTrue(this.speedExamine)) {
//            this.vasIngot += (BizConstants.TASK_SPEED_EXAMINE_INGOT * 100);
//        }
        // 定时发布
        if (NumberUtils.isGreaterThanZero(this.publishTimerInterval)) {
            this.vasIngot += (BizConstants.TASK_TIMING_PUBLISH_INGOT * 100);
        }
        //优质评论
//        if(StringUtils.isNotEmpty(this.goodCommentWords)){
//            this.vasIngot += (BizConstants.TASK_GOOD_COMM_KWD_INGOT * 100);
//        }
//        if(StringUtils.isNotEmpty(this.goodCommentImg)){
//            this.vasIngot += (BizConstants.TASK_GOOD_COMM_PIC_INGOT * 100);
//        }
        // 总费用：刷单费用（服务费+快递费） + 增值服务
        
        this.totalIngot = (long) (serviceFee + this.vasIngot);
        
        
        System.out.println("-----------totalIngot------"+this.totalIngot);
        System.out.println("-----------totalIngot------"+this.totalIngot);
        System.out.println("-----------totalIngot------"+this.totalIngot);
        System.out.println("-----------totalIngot------"+this.totalIngot);
        System.out.println("-----------totalIngot------"+this.totalIngot);
        
        System.out.println("-----------totalIngot------"+this.totalIngot);
        System.out.println("-----------totalIngot------"+this.totalIngot);
        
        
    }
    
    
    
	/**
	 * 
	 * 使用账户押金和金币来支付.
	 * 
	 * @param user
	 * @since 0.1
	 * @author youblade
	 * @created 2014年9月10日 下午4:50:42
	 */
    public void pay(long payFee, boolean useIngot, boolean usePledge) {
        Validate.notNull(this.id);
        Validate.notNull(this.sellerId);
        Validate.notNull(this.totalPledge);
        Validate.notNull(this.totalIngot);
       // Validate.isTrue(payFee == this.totalIngot + this.totalPledge);
        Validate.notNull(this.sysRefund);

        SqlSession ss = SessionFactory.getSqlSessionWithoutAutoCommit();
        try {
            // 取出上一条金币记录
            UserIngotRecordMapper ingotMapper = ss.getMapper(UserIngotRecordMapper.class);
            UserIngotRecord lastIngot = ingotMapper.selectLastRecord(this.sellerId);
            // 取出上一条押金记录
            SellerPledgeRecordMapper pledgeMapper = ss.getMapper(SellerPledgeRecordMapper.class);
            SellerPledgeRecord lastPledge = pledgeMapper.selectLastRecord(this.sellerId);

            // 混合押金、金币两种方式支付
            if (useIngot && usePledge) {
                payWithMultipleWay(ss, lastPledge, lastIngot);
            }else{
                // 仅使用押金、金币、网银其中一种特定方式进行支付
                payWithCertainWay(useIngot, usePledge, ss, lastIngot, lastPledge);
            }
            if (CollectionUtils.isNotEmpty(this.searchPlans)) {
				long totalFlow = 0;
				for (TaskItemSearchPlan3 plan : this.searchPlans) {
					// 跳过关键词为空的记录
					if (plan == null || StringUtils.isBlank(plan.word)) {
						continue;
					}
					totalFlow += plan.flowNum;
				}
				UserFlowRecordMapper userFlowRecordMapper = ss.getMapper(UserFlowRecordMapper.class);
				UserFlowRecord flowRecord = userFlowRecordMapper.selectLastRecord(this.sellerId);
				String memo = "发布任务【" + String.valueOf(this.id) + "】花费" + totalFlow + "个流量";
				flowRecord = UserFlowRecord.newInstance(this.sellerId, flowRecord).minus(totalFlow).memo(memo).createTime(DateTime.now().toDate());
				if(totalFlow>0)
				userFlowRecordMapper.insert(flowRecord);
				User.findByIdWichCache(this.sellerId).flow(flowRecord.balance).updateCache();
			}
            deducteAndApply(ss);
            ss.commit();
        } finally {
            ss.close();
        }
    }

    /*
     * 使用特定的某一种方式进行支付
     */
    private void payWithCertainWay(boolean useIngot, boolean usePledge, SqlSession ss, UserIngotRecord lastIngot,
        SellerPledgeRecord lastPledge) {

        // 使用非平台内部提供（押金、金币）的其他支付方式（网银、财付通等）
        boolean useOther = !useIngot && !usePledge;
        UserIngotRecordMapper ingotMapper = ss.getMapper(UserIngotRecordMapper.class);
        SellerPledgeRecordMapper pledgeMapper = ss.getMapper(SellerPledgeRecordMapper.class);

        /*
         * 充值押金：使用网银、金币支付时，需要先充值账户押金
         */
        Date now = DateTime.now().toDate();
        if (useOther || useIngot) {
            String memo = "发布任务[" + String.valueOf(this.id) + "]充值账户押金";

            // 网银支付时全部金额都充值为押金，金币支付时仅充值要冻结的部分押金
            long pledgeAmount = this.totalIngot + this.totalPledge;
            if (useIngot) {
                pledgeAmount = this.totalPledge;
                // 扣除金币
                lastIngot = UserIngotRecord.newInstance(this.sellerId, lastIngot).taskId(this.id).minus(pledgeAmount)
                    .memo(memo).createTime(now);
                ingotMapper.insert(lastIngot);
            }

            // 充值为押金
            lastPledge = SellerPledgeRecord.newInstance(this.sellerId, lastPledge)
                .action(PledgeAction.RECHARGE, pledgeAmount).taskId(this.id).memo(memo).createTime(now);
            pledgeMapper.insert(lastPledge);
        }

        /*
         * 兑换金币：使用网银、押金支付时，需要先取一部分押金兑换为本次任务所需支付的金币
         */
        if (useOther || usePledge) {
            // 扣除押金
            String memo = "发布任务[" + String.valueOf(this.id) + "]使用账户押金兑换金币";
            lastPledge = SellerPledgeRecord.newInstance(this.sellerId, lastPledge)
                .action(PledgeAction.EXCHANGE_INGOT, this.totalIngot).taskId(this.id).memo(memo).createTime(now);
            pledgeMapper.insert(lastPledge);

            // 兑换成金币
            lastIngot = UserIngotRecord.newInstance(this.sellerId, lastIngot).taskId(this.id).plus(this.totalIngot)
                .memo(memo).createTime(now);
            ingotMapper.insert(lastIngot);
        }
    }
    
    
    /*
     *  混合方式支付：<br>
     *  1、押金、金币都足够 <br>
     *  2、押金不足，金币足够 <br>
     *  3、押金足够，金币不足<br>
     */
    private void payWithMultipleWay(SqlSession ss, SellerPledgeRecord lastPledge, UserIngotRecord lastIngot) {
        Date now = DateTime.now().toDate();
        UserIngotRecordMapper ingotMapper = ss.getMapper(UserIngotRecordMapper.class);
        SellerPledgeRecordMapper pledgeMapper = ss.getMapper(SellerPledgeRecordMapper.class);

        /*
         * 押金不足以支付时，使用金币充值押金补足
         */
        if (lastPledge.balance < this.totalPledge) {
            // 计算差额，从金币中扣除
            String memo = "发布任务[" + String.valueOf(this.id) + "]充值账户押金";
            long amount = this.totalPledge - lastPledge.balance;
            lastIngot = UserIngotRecord.newInstance(this.sellerId, lastIngot).taskId(this.id).minus(amount).memo(memo)
                .createTime(now);
            ingotMapper.insert(lastIngot);

            // 将扣除的金币充值为押金
            lastPledge = SellerPledgeRecord.newInstance(this.sellerId, lastPledge)
                .action(PledgeAction.RECHARGE, amount).taskId(this.id).memo(memo).createTime(now);
            pledgeMapper.insert(lastPledge);
        }

        /*
         * 金币不足时，使用押金兑换成金币补足
         */
        if (lastIngot.balance < this.totalIngot) {
            // 计算差额，从押金记录中扣除
            String memo = "发布任务[" + String.valueOf(this.id) + "]使用账户押金兑换金币";
            long amount = this.totalIngot - lastIngot.balance;
            lastPledge = SellerPledgeRecord.newInstance(this.sellerId, lastPledge)
                .action(PledgeAction.EXCHANGE_INGOT, amount).taskId(this.id).memo(memo).createTime(now);
            pledgeMapper.insert(lastPledge);

            // 将扣除的押金充值为金币
            lastIngot = UserIngotRecord.newInstance(this.sellerId, lastIngot).taskId(this.id).plus(amount).memo(memo)
                .createTime(now);
            ingotMapper.insert(lastIngot);
        }
    }

    /*
     * 扣除任务所需支付费用，并更新数据
     */
    private void deducteAndApply(SqlSession ss) {
        Date now = DateTime.now().toDate();
        
        // 冻结押金
        SellerPledgeRecordMapper pledgeMapper = ss.getMapper(SellerPledgeRecordMapper.class);
        SellerPledgeRecord lastPledge = pledgeMapper.selectLastRecord(this.sellerId);
        
        // 设置冻结类型：卖家手动返款与平台返款不同
        PledgeAction action = PledgeAction.LOCK;
        if(BooleanUtils.isTrue(this.sysRefund)){
            action = PledgeAction.LOCK_SYS_REFUND;
        }
        String memo = "发布任务[" + String.valueOf(this.id) + "]冻结押金";
        lastPledge = SellerPledgeRecord.newInstance(this.sellerId, lastPledge).action(action, this.totalPledge)
            .taskId(this.id).memo(memo).createTime(now);
        pledgeMapper.insert(lastPledge);

        // 扣除金币
        UserIngotRecordMapper ingotMapper = ss.getMapper(UserIngotRecordMapper.class);
        UserIngotRecord lastIngot = ingotMapper.selectLastRecord(this.sellerId);
        
        memo = "支付任务[" + String.valueOf(this.id) + "]扣除佣金";
        lastIngot = UserIngotRecord.newInstance(this.sellerId, lastIngot).taskId(this.id).minus(this.totalIngot)
            .memo(memo).createTime(now);
        ingotMapper.insert(lastIngot);

        // 更新任务状态
        TaskMapper3 taskMapper = ss.getMapper(TaskMapper3.class);
        if(this.delaySpan==null||this.delaySpan==0){
        	Task3 t = Task3.instance(this.id).setSellerId(this.sellerId).setStatus(TaskStatus.PUBLISHED);
            t.isPaid = true;
            t.modifyTime = now;
            t.examineTime=now;
            t.publishTime=now;
            taskMapper.updateById(t);
        }
        else{
        	Task3 t = Task3.instance(this.id).setSellerId(this.sellerId).setStatus(TaskStatus.WAIT_PUBLISH);
            t.isPaid = true;
            t.modifyTime = now;
            t.examineTime=now;
            t.publishTime=now;
            taskMapper.updateById(t);
        }
        /**
		 * 自动审核任务
		 */
		TaskItemSearchPlanMapper3 searchPlanMapper=ss.getMapper(TaskItemSearchPlanMapper3.class);
		List<TaskItemSearchPlan3> searchPlan=searchPlanMapper.getOneTaskPlan(this.id);
		//Task task = Task.findById(this.id);
		if(CollectionUtils.isNotEmpty(searchPlan)){
			long totalFlow=0;
			for (TaskItemSearchPlan3 plan : searchPlan) {
				totalFlow+=plan.flowNum;
			}
			//任务审核部通过退回发任务花费的流量
			if(totalFlow>0&&this.status == TaskStatus.NOT_PASS){
				 UserFlowRecordMapper userFlowRecordMapper=ss.getMapper(UserFlowRecordMapper.class);
				 UserFlowRecord flowRecord=userFlowRecordMapper.selectLastRecord(this.sellerId);
				 memo="任务【"+this.id+"】审核不通过，退还发布任务花费的"+totalFlow+"个流量";
				 flowRecord=UserFlowRecord.newInstance(this.sellerId, flowRecord).plus(totalFlow).memo(memo).createTime(DateTime.now().toDate());
				 userFlowRecordMapper.insert(flowRecord);
				 User.findByIdWichCache(this.sellerId).flow(flowRecord.balance).updateCache();
			}
		}
		//加入流量任务
		if (this.status == TaskStatus.PUBLISHED || this.status == TaskStatus.WAIT_PUBLISH) {
		   
			if (CollectionUtils.isNotEmpty(searchPlan)) {
			    this.log.info("flow into taskId={}", id, this.id);
				// 流量目前仅支出淘宝、天猫和京东 pc、直通车
				if (this.platform == Platform3.TAOBAO || this.platform == Platform3.TMALL)
					for (TaskItemSearchPlan3 plan : searchPlan) {
						if (StringUtils.trimToNull(plan.word) == null) {
							continue;
						}
						if (plan.flowNum > 0) {
							TrafficRecordMapper recordMapper = ss.getMapper(TrafficRecordMapper.class);
							TrafficRecordVo trafficRecord = TrafficRecordVo.init(this, plan.flowNum, plan.word);
							trafficRecord.createTime = DateTime.now().toDate();
							trafficRecord.status = TrafficStatus.WAIT.toString();
							trafficRecord.returnTimes = 0;
							
							recordMapper.insert(trafficRecord);
						}
					}
			}
		}

        // 更新user缓存
        User.findByIdWichCache(this.sellerId).pledge(lastPledge.balance).ingot(lastIngot.balance).updateCache();
    }
    
    
    /**
     * 
     * 单表更新.
     *
     * @since  0.1
     * @author youblade
     * @created 2014年9月24日 下午5:22:06
     */
	public void simpleUpdate() {
	    Validate.notNull(this.id);
	    SqlSession ss = SessionFactory.getSqlSession();
	    try {
	        TaskMapper3 mapper = ss.getMapper(TaskMapper3.class);
	        this.modifyTime = DateTime.now().toDate();
	        mapper.updateById(this);
	    } finally {
	        ss.close();
	    }
	}
	
	/**
	 * 
     * 获取卖家任务（接手前的任务）.
     * 
     * 【注意】需要检查是否是当前登录用户的任务
     *
     * @param id
     * @return
     * @since  0.1
     * @author youblade
     * @created 2014年8月15日 下午12:08:33
     */
    public static Task3 findById(long id) {
        SqlSession ss = SessionFactory.getSqlSession();
        try {
            TaskMapper3 mapper = ss.getMapper(TaskMapper3.class);
            Task3 task = mapper.selectById(id);
            if (task == null) {
                return task;
            }
            
            //显示用
            task.resolveId();
            return task;
        } finally {
            ss.close();
        }
    }
    
    /**
	 * 
	 * 判断是否不属于天猫、淘宝订单或直通车的任务
	 *
	 * @param taskId
	 * @return true 不属于天猫、淘宝订单或直通车的任务 false 属于天猫、淘宝订单或直通车的任务
	 * @since  v2.0
	 * @author fufei
	 * @created 2015年2月2日 下午4:28:26
	 */
    public static boolean isTaoBaoAndTmall(long taskId) {
		Task3 task=Task3.findById(taskId);
		if((task.platform==Platform3.TAOBAO||task.platform==Platform3.TMALL)&&task.type!=TaskType3.JHS){
			return false;
		}
			return true;
	}

    /**
     * 
     * 找到押金冻结状态的商家返款任务
     *
     * @param id
     * @return
     * @since  v1.9.6
     * @author playlaugh
     * @created 2014年12月27日 下午2:32:07
     */
    public static List<Task3> findlockedPledgeTask(long sellerId) {
        SqlSession ss = SessionFactory.getSqlSession();
        try {
            TaskMapper3 mapper = ss.getMapper(TaskMapper3.class);
            return mapper.selectlockedPledgeTask(sellerId);
        } finally {
            ss.close();
        }
    }
    
    /**
     * 
     * 按照ID查询Task特定属性.
     *
     * @param fields
     * @param id
     * @return
     * @since  0.1
     * @author youblade
     * @created 2014年8月24日 上午11:18:45
     */
    public static Task3 findById(String fields, long id) {
        SqlSession ss = SessionFactory.getSqlSession();
        try {
            TaskMapper3 mapper = ss.getMapper(TaskMapper3.class);
            return mapper.selectFieldsById(fields, id);
        } finally {
            ss.close();
        }
    }

	/**
	 * 
	 * TODO 按照筛选条件查询任务.
	 *
	 * @param vo
	 * @return
	 * @since  0.1
	 * @author youblade
	 * @created 2014年7月25日 下午7:19:16
	 */
	@NotNull
		public static Page<Task3> findBySearchVo(TaskSearchVo3 vo) {
			    
			    // 注册时间在一周内的视为“新商家”
		        vo.registTime = LocalDate.now().minusWeeks(1).toDate();
		        vo.modifyTimeEnd=LocalDate.now().minusDays(1).toDate();
				SqlSession ss = SessionFactory.getSqlSession();
				try {
					TaskMapper3 mapper = ss.getMapper(TaskMapper3.class);
					int count=mapper.selectListCount(vo);
					
					
					
					if (count <= 0) {
		                return Page.EMPTY;
		            }
					Page<Task3> page = Page.newInstance(vo.pageNo, vo.pageSize, count);
					List<Task3> tasks = mapper.selectList(vo);
					for(Task3 task: tasks){
						User seller = User.findByIdWichCache(task.sellerId);
					    task.experience = task.getListType(seller.registTime).experience;
					    task.typeStr=task.type.title;
					    if(StringUtils.isNotEmpty(task.genderConfig)){
					    	task.isgender=true;
					    }
					    if(StringUtils.isNotEmpty(task.buyerLocationConfig)){
					    	task.isLocation=true;
					    }
					}
					page.items = tasks;
					return page;
				} finally {
					ss.close();
				}
			}
	
	/**
	 * 
	 * 卖家任务
	 * 
	 * @param svo
	 * @return
	 * @since v0.1
	 * @author moloch
	 * @created 2014-8-8 下午4:05:08
	 */
	@NotNull
	public static Page<SellerTaskVo3> findVoByPage(TaskSearchVo3 svo) {
		SqlSession ss = SessionFactory.getSqlSession();
		try {
			TaskMapper3 mapper = ss.getMapper(TaskMapper3.class);
			int totalCount = mapper.countForSeller(svo);
			if (totalCount <= 0) {
				return Page.EMPTY;
			}

			Page<SellerTaskVo3> page = Page.newInstance(svo.pageNo, svo.pageSize, totalCount);
			List<SellerTaskVo3> vos =  mapper.selectVoListForSeller(svo);
			
			// 若获取条件有状态审核不通过，给不通过原因设值
            for (SellerTaskVo3 vo : vos) {
                vo.performingCount = vo.pcTakenCount + vo.mobileTakenCount - vo.finishedCount;
                vo.notTakenCount = vo.totalOrderNum - (vo.pcTakenCount + vo.mobileTakenCount);
                if (svo.status == TaskStatus.NOT_PASS && MixHelper.isNotEmpty(vo.examineLogs)) {
                    vo.examineLog = SellerTaskVo3.getLastLog(vo);
                }
                // 返回任务状态的标题文字到前端
                vo.displayTaskStatusTitle();
            }
			page.items = vos;
			return page;
		} finally {
			ss.close();
		}
	}
	
	/**
	 * 
	 * 查询正在进行中的卖家任务
	 *
	 * @param svo
	 * @return
	 * @since  v2.7
	 * @author fufei
	 * @created 2015年3月10日 下午5:13:33
	 */
	@NotNull
	public static Page<SellerTaskVo3> findProcessTaskByPage(TaskSearchVo3 svo) {
		SqlSession ss = SessionFactory.getSqlSession();
		try {
			TaskMapper3 mapper = ss.getMapper(TaskMapper3.class);
			int totalCount = mapper.countProcessTaskSeller(svo);
			if (totalCount <= 0) {
				return Page.EMPTY;
			}

			Page<SellerTaskVo3> page = Page.newInstance(svo.pageNo, svo.pageSize, totalCount);
			List<SellerTaskVo3> vos =  mapper.selectProcessTaskSeller(svo);
			
			// 若获取条件有状态审核不通过，给不通过原因设值
            for (SellerTaskVo3 vo : vos) {
                vo.performingCount = vo.pcTakenCount + vo.mobileTakenCount - vo.finishedCount;
                vo.notTakenCount = vo.totalOrderNum - (vo.pcTakenCount + vo.mobileTakenCount);
                if (svo.status == TaskStatus.NOT_PASS && MixHelper.isNotEmpty(vo.examineLogs)) {
                    vo.examineLog = SellerTaskVo3.getLastLog(vo);
                }
                // 返回任务状态的标题文字到前端
                vo.displayTaskStatusTitle();
            }
			page.items = vos;
			return page;
		} finally {
			ss.close();
		}
	}
	
	/**
	 * 
	 * 【后台管理】查询任务信息.
	 *
	 * @param vo
	 * @return
	 * @since  v0.2.4
	 * @author youblade
	 * @created 2014年11月1日 下午5:37:45
	 */
	@NotNull
	public static Page<Task3> findByPage(TaskSearchVo3 vo) {
	    Validate.notNull(vo.fields);
	    
	    // 过滤数据
	    vo.sellerNick = StringUtils.trimToNull(vo.sellerNick);
        vo.shopName = StringUtils.trimToNull(vo.shopName);
        
	    SqlSession ss = SessionFactory.getSqlSession();
	    try {
	        TaskMapper3 mapper = ss.getMapper(TaskMapper3.class);
	        int totalCount = mapper.countSimple(vo);
	        if (totalCount <= 0) {
	            return Page.EMPTY;
	        }
	        
	        Page<Task3> page = Page.newInstance(vo.pageNo, vo.pageSize, totalCount);
	        page.items = mapper.selectSimple(vo);
	        for (Task3 t : page.items) {
                t.resolveId();
            }
	        return page;
	    } finally {
	        ss.close();
	    }
	}
	
	/**
	 * 
	 * 获取待审核主任务
	 * 
	 * @param vo
	 * @return
	 * @since v0.1
	 * @author moloch
	 * @created 2014-8-22 上午11:06:40
	 */
	public static Page<SellerTaskVo3> findExamineVoByPage(TaskSearchVo3 vo) {
		SqlSession ss = SessionFactory.getSqlSession();
		try {
			TaskMapper3 mapper = ss.getMapper(TaskMapper3.class);
			int totalCount = mapper.countForStatus(vo);
			if (totalCount <= 0) {
				return Page.EMPTY;
			}

			Page<SellerTaskVo3> page = Page.newInstance(vo.pageNo, vo.pageSize, totalCount);
			page.items = mapper.selectVoForStatus(vo);
			for (SellerTaskVo3 v : page.items) {
                v.displayTaskStatusTitle();
            }
			return page;
		} finally {
			ss.close();
		}
	}
	
    /**
     * 
     * 获取带处理的任务统计信息.
     * 1、审核中的任务：包含待审核、审核中、审核不通过三种状态
     * 2、按平台维度统计的待发货、待退款的任务数
     *
     * @param sellerId
     * @return
     * @since  0.1
     * @author youblade
     * @created 2014年8月20日 下午3:04:36
     */
    public static Multimap<String, TaskCountVo3> findWaitingTaskCountInfo(long sellerId) {
        List<TaskCountVo3> list = Lists.newArrayList();
        SqlSession ss = SessionFactory.getSqlSession();
        try {
            TaskMapper3 mapper = ss.getMapper(TaskMapper3.class);
            list.addAll(mapper.countByExamineStatusAndSellerId(sellerId));

            BuyerTaskMapper3 btMapper = ss.getMapper(BuyerTaskMapper3.class);
            list.addAll(btMapper.countWaitingTasksBySellerId(sellerId));
        } finally {
            ss.close();
        }

        Multimap<String, TaskCountVo3> map = ArrayListMultimap.create();
        for (TaskCountVo3 vo : list) {
            if (TaskStatus.EXAMINE_STATUS_LIST.contains(vo.status)) {
                map.put(BizConstants.EXAMINE_STATUS_LIST, vo);
                continue;
            }
            map.put(vo.status.toString(), vo);
        }
        return map;
    }
    
    /**
     * 
     * 获取卖家待编辑的任务.
     *
     * @param sellerId
     * @return
     * @since  0.1
     * @author youblade
     * @created 2014年8月27日 下午2:29:11
     */
    public static Task3 findTodoTask(long sellerId) {
        SqlSession ss = SessionFactory.getSqlSession();
        try {
            TaskMapper3 mapper = ss.getMapper(TaskMapper3.class);
            return mapper.selectWaitEdit(sellerId);
        } finally {
            ss.close();
        }
    }

    /**
     * 
     * 计算每单任务所收取的服务费用（其中90%要作为买手佣金）.
     *
     * @param price
     * @return  :以分为单位的金额数值
     * @since  0.1
     * @author youblade
     * @created 2014年8月29日 上午11:47:14
     */
        // 转换金额单位：从分到元
    public static long computeFeePerOrder(long itemPrice,Platform3 platform) {
        BigDecimal decimal = BigDecimal.valueOf(itemPrice).movePointLeft(2);
        // 以浮点数计算
        float result = computeCommission(decimal.floatValue(),platform);
        // 转换金额单位：从元到分
        return BigDecimal.valueOf(result).movePointRight(2).longValue();
    }
    /**
     * 
     * TODO 天猫、淘宝起步6块钱，其他平台起步5块钱
     *
     * @param price
     * @param platform
     * @return
     * @since  v2.0
     * @author fufei
     * @created 2015年2月4日 下午2:38:08
     */
    public static float computeCommission(float price,Platform3 platform) {
    	int taskStartIngot=BizConstants.TASK_STARTING_INGOT;
		if (platform != Platform3.TAOBAO && platform != Platform3.TMALL) {
			taskStartIngot=BizConstants.TASK_OTHER_PLATFORM_STARTING_INGOT;
		}
		 // 当价格精确到元时，算法逻辑如下
        if (price < 100.01f) {
            return taskStartIngot;
        }
        return (float) (Math.ceil((price - 100.f) / 150.f) + taskStartIngot);
    }

    /**
     * 
     * 计算接手任务时所能获取到的最终佣金（包含加赏部分）.
     *
     * @param device
     * @return
     * @since  0.1
     * @author youblade
     * @created 2014年9月9日 下午4:00:47
     */
    public long calculateRewardIngot(Device device,Task3 task3) {
        Validate.notNull(device);
        Validate.notNull(baseOrderIngot);
        Validate.notNull(extraRewardIngot);

    /* // 服务费用的90%作为买手任务佣金
        long pcReward = BigDecimal.valueOf(this.baseOrderIngot * BizConstants.TASK_REWARD_BUYER_RATE).longValue();
        // 加赏佣金的80%作为买手任务佣金
        long extReward = 0;
        if (NumberUtils.isGreaterThanZero(extraRewardIngot)) {
            extReward = BigDecimal.valueOf(extraRewardIngot * 100 * BizConstants.TASK_EXT_REWARD_BUYER_RATE)
                .longValue();
        }
        if (device == Device.PC) {
            return pcReward + extReward;
        }
       this.totalIngot/100)-0.2)/this.totalOrderNum
        // 移动端任务要再加上额外的金币
        return (long) (pcReward + BizConstants.TASK_MOBILE_INGOT * 100) + extReward;*/
        //long reward=(long) (((this.totalIngot/100)/100-0.2)/this.totalOrderNum);
        System.out.println("=====task3.id=========="+task3.id);
        SqlSession ss = SessionFactory.getSqlSession();
			TaskMapper3 mapper = ss.getMapper(TaskMapper3.class);
			Task3 task=mapper.selectById(task3.id);
			System.out.println("--------"+task.toString());
			System.out.println("--------"+task.toString());
			System.out.println("--------"+task.toString());
			System.out.println("--------"+task.toString());
			System.out.println("--------"+task.toString());
			System.out.println("======(((task.totalIngot/100)/100-0.2)/task.totalOrderNum========="+(((task.totalIngot)-20)/task.totalOrderNum));
			long reward= (((task.totalIngot)-20)/task.totalOrderNum);
			System.out.println("======reward========="+reward/100);
			
        return reward;
        
       // return (long) ( ((this.totalIngot/100)/100-0.2)/this.totalOrderNum );
       
       
    }
    
    /**
     * 
     * 不允许编辑
     *
     * @return
     * @since  v1.9.9.2
     * @author youblade
     * @created 2015年1月7日 下午5:41:45
     */
    public boolean notAllowEdit() {
        return this.status != TaskStatus.WAIT_EDIT && this.status != TaskStatus.NOT_PASS;
    }
    
    /**
     * 
     *  是否不可修改：已完成、已 撤销.
     *
     * @return
     * @since  0.1  
     * @author youblade
     * @created 2014年8月28日 下午6:09:21
     */
    public boolean isUnmodifiable() {
        return this.status == TaskStatus.FINISHED || this.status == TaskStatus.CANCLED;
    }
    
    /**
     * 
     * 是否“非包邮商品”.
     *
     * @return
     * @since  v0.2.5
     * @author youblade
     * @created 2014年11月6日 下午3:31:14
     */
    public boolean notFreeShipping() {
        Validate.notNull(this.isFreeShipping);
        return BooleanUtils.isFalse(this.isFreeShipping);
    }
    
    /**
     *  获取额外的搜索关键词方案数量（默认淘宝有一个，不计入）
     */
    public int countExtraSearchPlan() {
        // 至少有一个方案
        Validate.notEmpty(searchPlans);
        
        // 天猫搜索方案数量
        int countTmall = 0;
        for (TaskItemSearchPlan3 plan : searchPlans) {
            if (plan.inTmall) {
                countTmall++;
            }
        }
        // 淘宝搜索方案，默认有一个免费的；天猫全部收费
        int countTaobao = searchPlans.size() - countTmall - 1;
        return countTmall + Math.max(0, countTaobao);
    }
    
	
    /**
     * 
     * 【用于测试】创建记录.
     *
     * @since  0.1
     * @author youblade
     * @created 2014年9月1日 下午5:57:58
     */
    public Task3 create() {
        this.id = Pandora.getInstance().nextId();
        SqlSession ss = SessionFactory.getSqlSession();
        try {
            TaskMapper3 mapper = ss.getMapper(TaskMapper3.class);
            mapper.insert(this);
        } finally {
            ss.close();
        }
        return this;
    }
    
	/**
	 * 
	 * 获得未接手的所有子任务数量
	 * 
	 * @return
	 * @since v0.1
	 * @author moloch
	 * @created 2014-9-3 下午3:37:24
	 */
	public static Long countTaskByStatus() {
		SqlSession ss = SessionFactory.getSqlSession();
		try {
			TaskMapper3 mapper = ss.getMapper(TaskMapper3.class);
			Integer countPc = mapper.pcPublishAndNotTokenOver();
			Integer countMobile = mapper.mobilePublishAndNotTokenOver();
			return (countPc == null ? 0 : countPc.longValue()) + (countMobile == null ? 0 : countMobile.longValue());
		} finally {
			ss.close();
		}
	}
	
	/**
	 * 
	 * 获取未领取的买手任务总数
	 *
	 * @param sign
	 * @param num
	 * @since  v1.9.9.1
	 * @author youblade
	 * @created 2015年1月5日 上午10:14:14
	 */
    public static Long fetchBuyerTasksNotTakenNum() {
        String key = CacheType.TASK_NUM.getKey();
        Long count = CacheUtil.get(key);
        if (count != null && count > 0) {
            cacheLog.debug("Cache hit TASK_NUM={}", count);
            return count;
        }

        count = Task3.countTaskByStatus();
        cacheLog.debug("Get from db and set cache TASK_NUM={}", count);
        CacheUtil.set(key, count, CacheType.TASK_NUM.expiredTime);
        return count;
    }
	
	/**
	 * 
	 * 更新“任务列表”中显示的可接手任务数量
	 *
	 * @param num
	 * @param sign
	 * @since  0.1
	 * @author youblade
	 * @created 2014年10月7日 下午7:38:08
	 */
    public static void updateTaskCount(Sign sign, int num) {
        String key = CacheType.TASK_NUM.getKey();
        Long countTasks = CacheUtil.get(key);
        if (countTasks == null) {
            countTasks = fetchBuyerTasksNotTakenNum();
        }

        if (Sign.MINUS == sign) {
            Cache.decr(key, num);
        } else if (Sign.PLUS == sign) {
            Cache.incr(key, num);
        }
    }
    
	/**
	 * 
	 * 获取任务列表类型.
	 *
	 * @return
	 * @since  0.1
	 * @author youblade
	 * @created 2014年9月9日 下午5:49:23
	 */
    public TaskListType getListType(Date registTime) {
        // 付费【推荐任务】优先级最高（经验值最高）
        if(NumberUtils.isGreaterThanZero(this.speedTaskIngot)){
            return TaskListType.SYS_RECOMMEND;
        }
        
        // 新用户注册一周内所发布的任务视为【新商家】任务
        LocalDate flagDate = new LocalDate(registTime).plusWeeks(1);
        if(this.createTime.before(flagDate.toDate())){
            return TaskListType.NEW_SHOP;
        }
        
        // 【加赏任务】
        if(NumberUtils.isGreaterThanZero(this.extraRewardIngot)){
            return TaskListType.EXTRA_REWARD;
        }
        
        // 其余的为【常规任务】
        return TaskListType.COMMON;
    }
    
    
	/**
	 * 
	 * 审核任务
	 * 
	 * @since v0.1
	 * @author moloch
	 * @created 2014-9-18 上午10:56:05
	 */
	public void examine(String memo) {
		SqlSession ss = SessionFactory.getSqlSessionWithoutAutoCommit();
		try {
			TaskMapper3 mapper = ss.getMapper(TaskMapper3.class);

			// 更新任务记录
			if(this.status == TaskStatus.NOT_PASS){
			    this.isPaid = false;
			}
			mapper.updateById(this);

			// 保存审核记录
			TaskExamineLog log = new TaskExamineLog();
			log.taskId = this.id;
			TaskExamineLogMapper examineMapper = ss.getMapper(TaskExamineLogMapper.class);
			if (this.status == TaskStatus.NOT_PASS) {
				log = examineMapper.selectByTaskId(this.id);
				log.isPass = false;
				log.memo = memo;
				
				// 为审核不通过的任务退款
                mapper.selectFieldsById("id,seller_id,total_ingot,total_pledge,sys_refund", id).refundForNotPass(ss);
			}
			if (this.status == TaskStatus.PUBLISHED || this.status == TaskStatus.WAIT_PUBLISH) {
				log = examineMapper.selectByTaskId(this.id);
				log.isPass = true;
			}
			log.save();
			//更新流量记录
			TaskItemSearchPlanMapper3 searchPlanMapper=ss.getMapper(TaskItemSearchPlanMapper3.class);
			List<TaskItemSearchPlan3> searchPlan=searchPlanMapper.getOneTaskPlan(this.id);
			Task3 task = Task3.findById(this.id);
			if(CollectionUtils.isNotEmpty(searchPlan)){
				long totalFlow=0;
				for (TaskItemSearchPlan3 plan : searchPlan) {
					totalFlow+=plan.flowNum;
				}
				//任务审核部通过退回发任务花费的流量
				if(totalFlow>0&&this.status == TaskStatus.NOT_PASS){
					 UserFlowRecordMapper userFlowRecordMapper=ss.getMapper(UserFlowRecordMapper.class);
					 UserFlowRecord flowRecord=userFlowRecordMapper.selectLastRecord(task.sellerId);
					 memo="任务【"+this.id+"】审核不通过，退还发布任务花费的"+totalFlow+"个流量";
					 flowRecord=UserFlowRecord.newInstance(task.sellerId, flowRecord).plus(totalFlow).memo(memo).createTime(DateTime.now().toDate());
					 userFlowRecordMapper.insert(flowRecord);
					 User.findByIdWichCache(task.sellerId).flow(flowRecord.balance).updateCache();
				}
			}
			//加入流量任务
			if (this.status == TaskStatus.PUBLISHED || this.status == TaskStatus.WAIT_PUBLISH) {
			   
				if (CollectionUtils.isNotEmpty(searchPlan)) {
				    this.log.info("flow into taskId={}", id, this.id);
					// 流量目前仅支出淘宝、天猫和京东 pc、直通车
					if (task.platform == Platform3.TAOBAO || task.platform == Platform3.TMALL )
						for (TaskItemSearchPlan3 plan : searchPlan) {
							if (StringUtils.trimToNull(plan.word) == null) {
								continue;
							}
							if (plan.flowNum > 0) {
								TrafficRecordMapper recordMapper = ss.getMapper(TrafficRecordMapper.class);
								TrafficRecordVo trafficRecord = TrafficRecordVo.init3(task, plan.flowNum, plan.word);
								trafficRecord.createTime = DateTime.now().toDate();
								trafficRecord.status = TrafficStatus.WAIT.toString();
								trafficRecord.returnTimes = 0;
								
								recordMapper.insert(trafficRecord);
							}
						}
				}
			}
			ss.commit();
			// 更新“任务列表(X)”中的任务数量
            if (this.status == TaskStatus.PUBLISHED) {
                Task3.updateTaskCount(Sign.PLUS, task.totalOrderNum);
            }
		} finally {
			ss.close();
		}
	}
	
	/**
	 * 
	 * 给审核未通过的任务退款 押金退押金，金币退金币
	 * 
	 * @since v0.1
	 * @author moloch
	 * @created 2014-9-23 下午4:40:59
	 */
    private void refundForNotPass(SqlSession ss) {
        Validate.notNull(this.sysRefund, "Task.sysRefund must not be null");

        // 退还金币
        UserIngotRecordMapper ingotMapper = ss.getMapper(UserIngotRecordMapper.class);
        UserIngotRecord lastIngot = ingotMapper.selectLastRecord(this.sellerId);
        
        String memo = "任务[" + this.id + "]审核不通过，退还支付佣金";
        Date now = DateTime.now().toDate();
        UserIngotRecord ingotRecord = UserIngotRecord.newInstance(this.sellerId, lastIngot).taskId(this.id)
            .plus(this.totalIngot).createTime(now).memo(memo);
        ingotMapper.insert(ingotRecord);

        
        // 退还任务押金
        SellerPledgeRecordMapper pledgeMapper = ss.getMapper(SellerPledgeRecordMapper.class);
        SellerPledgeRecord lastPledge = pledgeMapper.selectLastRecord(this.sellerId);
        
        memo = "任务[" + this.id + "]审核不通过，解冻押金";
        PledgeAction action = PledgeAction.UNLOCK;
        if(BooleanUtils.isTrue(this.sysRefund)){
            action = PledgeAction.UNLOCK_SYS_REFUND;
        }
        SellerPledgeRecord pledgeRecord = SellerPledgeRecord.newInstance(sellerId, lastPledge).taskId(this.id)
            .action(action, this.totalPledge).createTime(now).memo(memo);
        pledgeMapper.insert(pledgeRecord);

        // 更新user缓存
        User.findByIdWichCache(this.sellerId).ingot(ingotRecord.balance).pledge(pledgeRecord.balance).updateCache();
    }

	/**
	 * 
	 * 撤销未接单任务
	 * 
	 * @param seller
	 * @since v0.1
	 * @author moloch
	 * @created 2014-9-20 下午5:31:07
	 */
    public static void cancledAndRefund(long id) {
        // 获取任务退款金额
        CancelTaskVo3 cancledInfo = findCancelInfo(id);
        // 若任务全部被接单
        
        Task3 t = Task3.findById(id);
        User seller = User.findById(t.sellerId);
        Date now = DateTime.now().toDate();
       
        SqlSession ss = SessionFactory.getSqlSessionWithoutAutoCommit();
        try {
            if (cancledInfo != null) {
                
                log.info("Cancel task={} UNLOCK pledge={}", id, cancledInfo.cancledPledge);
                
                /*
                 * 解冻任务押金
                 */
                SellerPledgeRecordMapper pledgeMapper = ss.getMapper(SellerPledgeRecordMapper.class);
                SellerPledgeRecord lastRecord = pledgeMapper.selectLastRecord(seller.id);
                
                // 平台返款任务使用特定的冻结Action
                PledgeAction action = PledgeAction.UNLOCK;
                if(BooleanUtils.isTrue(cancledInfo.isSysRefund)){
                    action = PledgeAction.UNLOCK_SYS_REFUND;
                }
                
                SellerPledgeRecord record = SellerPledgeRecord.newInstance(seller.id, lastRecord).taskId(id)
                    .action(action, cancledInfo.cancledPledge).createTime(now).memo("撤销任务[" + id + "]解冻押金");
                pledgeMapper.insert(record);

                /*
                 * 退还金币
                 */
                log.info("Cancel task={} Return ingot={}", id, cancledInfo.cancledIngot);
                UserIngotRecordMapper ingotMapper = ss.getMapper(UserIngotRecordMapper.class);
                UserIngotRecord lastIngot = ingotMapper.selectLastRecord(seller.id);
                UserIngotRecord ingotRecord = UserIngotRecord.newInstance(seller.id, lastIngot).taskId(id)
                    .plus(cancledInfo.cancledIngot).createTime(now).memo("撤销任务[" + id + "]，退还任务佣金");
                ingotMapper.insert(ingotRecord);
                
                // 更新“任务列表(X)”中的任务数量
                Task3.updateTaskCount(Sign.MINUS, cancledInfo.cancledNum);
                // 更新user缓存
                User.findByIdWichCache(seller.id).pledge(record.balance).ingot(ingotRecord.balance).updateCache();
            }
            // 更新任务状态为取消
            Task3 ta = Task3.instance(id).setSellerId(seller.id).setStatus(TaskStatus.CANCLED).modifyTime(now);
            ss.getMapper(TaskMapper3.class).updateById(ta);
            ss.commit();
        } catch (Exception e) {
            ss.rollback();
            log.error(e.getMessage(), e);
            throw new RuntimeException("撤销任务失败！");
        } finally {
            ss.close();
        }
    }
	
    
    
	/**
	 * 
	 * 获取任务撤销数据 不能与 cancledAndRefund 方法中相同逻辑提取公用方法，因为cancledAndRefund
	 * 里面操作需要在一个事务中
	 * 
	 * @param id
	 * @return
	 * @since v0.1
	 * @author moloch
	 * @created 2014-9-22 下午3:27:55
	 */
	public static CancelTaskVo3 findCancelInfo(long id) {
		SqlSession ss = SessionFactory.getSqlSession();
		try {
			// 取出已接单数量
			TaskMapper3 mapper = ss.getMapper(TaskMapper3.class);
            Task3 t = mapper.selectById(id);
            
            // 是否已全部被领取
            if (t.pcTakenCount + t.mobileTakenCount >= t.totalOrderNum) {
                log.info("the task {} is taken over, countTakenNum is {}", id );
                return null;
            }

            // 若该任务一单也未领取过，则返还全部金额
            if (t.pcTakenCount <= 0 && t.mobileTakenCount <= 0) {
                CancelTaskVo3 vo =  CancelTaskVo3.newInstance(t.totalOrderNum, t.totalPledge, t.totalIngot);
                vo.isSysRefund = BooleanUtils.toBoolean(t.sysRefund);
                return vo;
            }
            
            CancelTaskVo3 vo = new CancelTaskVo3();
            vo.cancledNum = t.totalOrderNum - t.pcTakenCount - t.mobileTakenCount;
            
            long oldTaskPledge = t.pledge;
            long oldTaskTotalPledge = t.totalPledge;
            long oldtaskTotalIngot = t.totalIngot;
			
			/*
			 * 任务垫付的押金、支付的金币仅与商品价格关联，与数量无关，
			 * 故此处将已完成的子任务当做来自另一个刷单数不同的父任务来计算其费用，
			 * 则总费用减去该子任务的费用即为撤销这批未接单子任务所要退还的费用
			 */
			t.pcOrderNum = t.pcTakenCount;
			t.mobileOrderNum = t.mobileTakenCount;
			t.totalOrderNum = t.pcOrderNum + t.mobileOrderNum;
			t.calculateAndSetTaskPledgeAndIngot();
			
			vo.cancledPledge = oldTaskTotalPledge - t.totalPledge;
			vo.cancledIngot = oldtaskTotalIngot - t.totalIngot;
			
			// 再次核对押金
            //if (vo.cancledPledge != vo.cancledNum * oldTaskPledge) {
            //    log.error("撤销任务退款金额计算有误");
            //    throw new RuntimeException("操作失败");
            //}

            // 设置“平台返款”标记
            vo.isSysRefund = BooleanUtils.toBoolean(t.sysRefund);
			return vo;
		} finally {
			ss.close();
		}
	}
    
    public boolean isNotBelongTo(User seller) {
        if (this.sellerId == null) {
            return false;
        }
        return this.sellerId.longValue() != seller.id;
    }
    
    /**
     * 
     * 任务已接手完.
     * 
     * @return
     * @since 0.1
     * @author youblade
     * @created 2014年10月12日 下午12:08:16
     */
    public boolean isTakenOver(Device device) {
        Validate.notNull(device);
        Validate.notNull(this.id);
        Validate.notNull(this.pcOrderNum);
        Validate.notNull(this.pcTakenCount);
        Validate.notNull(this.mobileOrderNum);
        Validate.notNull(this.mobileTakenCount);
        Validate.notNull(this.publishTimerInterval);
        Validate.notNull(this.publishTimerValue);

        // 检查对应终端的领取数目
        if (device == Device.MOBILE) {
            // 不存在MOBILE订单时，视为任务已全部领取
            if (mobileOrderNum <= 0) {
                return true;
            }
            if (mobileTakenCount >= mobileOrderNum) {
                return true;
            }
        }
        if (device == Device.PC) {
            // 不存在PC订单时，视为任务已全部领取
            if (pcOrderNum <= 0) {
                return true;
            }
            if (pcTakenCount >= pcOrderNum) {
                return true;
            }
        }
        
        TaskSearchVo3 svo = TaskSearchVo3.newInstance().taskId(this.id).statusExclude(TaskStatus.CANCLED);
        SqlSession ss = SessionFactory.getSqlSession();
        try {
            BuyerTaskMapper3 mapper = ss.getMapper(BuyerTaskMapper3.class);
            int pcTaskCount = mapper.count(svo.device(Device.PC));
            int mobileTaskCount = mapper.count(svo.device(Device.MOBILE));
            if ((this.pcOrderNum > 0 && pcTaskCount >= this.pcOrderNum)
                && (this.mobileOrderNum > 0 && mobileTaskCount >= this.mobileOrderNum)) {
                log.error("Task={} pcTakenCount={} and mobileTakenCount={} has taken over", new Object[] { this.id,
                        this.pcTakenCount, this.mobileTakenCount, this.totalOrderNum });
                return true;
            }
        } finally {
            ss.close();
        }
        
        return false;
    }

    /**
     * 是否定时任务
     */
    public boolean checkIsTimerTask() {
        Validate.notNull(this.publishTimerInterval, "publishTimerInterval must not be null");
        return this.publishTimerInterval > 0;
    }
    
    /**
     * 【定时任务】本批次发布一组子任务的是否已被领取完.
     */
    public boolean checkIsCurrentGroupTasksTakenOver() {
        int totalTakenCount = this.mobileTakenCount + this.pcTakenCount;
        return (totalTakenCount % this.publishTimerValue == 0);
        
    }
    
    /**
     * 
     * 上个批次的一组任务放出的时间距离当前时间是否已达到设定的时间间隔.
     *
     * @return
     * @since  0.1
     * @author youblade
     * @created 2014年10月12日 下午1:18:25
     */
	public boolean isLastBatchPublishTimeOverdue() {
		// 任务分为是否是延迟发布的任务
		if (this.delaySpan != null) {
			if (this.delaySpan == 0) {
				// 上次发布时间为空表明其为新加入定时任务池中的任务，需要立即发布
				if (this.lastBatchPublishTime == null) {
					return true;
				}
				// 判断【上个批次发布时间】距离【当前时间】是否已达到设定的间隔时间
				DateTime lastBatchPublish = new DateTime(this.lastBatchPublishTime);
				DateTime now = DateTime.now();
				int mins = Minutes.minutesBetween(lastBatchPublish, now).getMinutes();
				TaskKeeper.log.debug("mins={} between {} and {}",
						new Object[] { mins, lastBatchPublish.toString(DateUtils.YYYY_MM_DD_HH_MM_SS), now.toString(DateUtils.YYYY_MM_DD_HH_MM_SS) });
				return mins >= this.publishTimerInterval;
			} else if (this.publishTimerInterval != 0 && this.delaySpan != 0) {
				// 既是定时任务，也是延时任务
				DateTime publish = new DateTime(this.publishTime);
				DateTime now = DateTime.now();
				int mins = Minutes.minutesBetween(publish, now).getMinutes();
				// 判断【预计发布时间】距离【当前时间】是否已达到设定的间隔时间
				if (this.lastBatchPublishTime == null) {
					return mins >= this.delaySpan;
				}else{
					DateTime lastBatchPublish = new DateTime(this.lastBatchPublishTime);
					mins = Minutes.minutesBetween(lastBatchPublish, now).getMinutes();
				}
				//// 判断【上个批次发布时间】距离【当前时间】是否已达到设定的间隔时间
				return mins >= this.publishTimerInterval;
			} else if (this.publishTimerInterval == 0 && this.delaySpan != 0) {
				// 延时任务，非定时任务
				DateTime publish = new DateTime(this.publishTime);
				DateTime now = DateTime.now();
				int mins = Minutes.minutesBetween(publish, now).getMinutes();
				return mins >= this.delaySpan;
			}
		}
		return false;
	}
	/**
	 * 
	 * 后台系统统计
	 *
	 * @return
	 * @since  v2.9
	 * @author fufei
	 * @created 2015年3月21日 上午11:13:42
	 */
	  public static SearchSystemCountVo findSysCountVo() {
	        SqlSession ss = SessionFactory.getSqlSession();
	        try {
	            TaskMapper3 mapper = ss.getMapper(TaskMapper3.class);
	            return mapper.findSysCountVo();
	        } finally {
	            ss.close();
	        }
	        
	    }

	  /**
	   * 
	   * 查询新商家放单数
	   *
	   * @param publishTime
	   * @return
	   * @since  v2.9
	   * @author fufei
	   * @created 2015年3月23日 下午3:09:37
	   */
	  public static List<Task3> findSellerPublishCount(String publishTime) {
          SqlSession ss = SessionFactory.getSqlSession();
          try {
              TaskMapper3 mapper = ss.getMapper(TaskMapper3.class);
              return mapper.findSellerPublishCount(publishTime);
          } finally {
              ss.close();
          }
          
      }
	  
	
      /**
       * 
       * 查询所有商家放单数
       *
       * @param publishTime
       * @return
       * @since  v2.9
       * @author fufei
       * @created 2015年3月23日 下午3:09:37
       */
      public static Page<SearchTakenCount> findSellerPublishToday(String publishTime,SearchTakenCount vo) {
          SqlSession ss = SessionFactory.getSqlSession();
          try {
              TaskMapper3 mapper = ss.getMapper(TaskMapper3.class);
              Integer totalCount = mapper.findSellerPublishTodayCount(publishTime);
              if (totalCount==null||totalCount <= 0) {
                  return Page.EMPTY;
              }

              Page<SearchTakenCount> page = Page.newInstance(vo.pageNo, vo.pageSize, totalCount);
              page.items=mapper.findSellerPublishToday(publishTime,vo.startIndex,vo.pageSize);
              return page;
          } finally {
              ss.close();
          }
          
      }
      /**
       * 
       * 查询所有买手接单数
       *
       * @param publishTime
       * @return
       * @since  v2.9
       * @author fufei
       * @created 2015年3月23日 下午3:09:37
       */
      public static Page<SearchTakenCount> findBuyerTakenToday(String publishTime,SearchTakenCount vo) {
          SqlSession ss = SessionFactory.getSqlSession();
          try {
              TaskMapper3 mapper = ss.getMapper(TaskMapper3.class);
              Integer totalCount = mapper.findBuyerTakenTodayCount(publishTime);
              if (totalCount==null||totalCount <= 0) {
                  return Page.EMPTY;
              }
              Page<SearchTakenCount> page = Page.newInstance(vo.pageNo, vo.pageSize, totalCount);
              page.items=mapper.findBuyerTakenToday(publishTime,vo.startIndex,vo.pageSize);
              return page;
          } finally {
              ss.close();
          }
          
      }
      /**
  	 * 
  	 * 商家放单统计
  	 *
  	 * @param vo
  	 * @return
  	 * @since  v2.0
  	 * @author Mark Xu
  	 * @created 2015-3-24 下午4:41:18
  	 */
  	public static Page<SellerPutTimeVo> findBySellerPutTimeVo(SellerPutTimeVo vo) {
  		SqlSession ss = SessionFactory.getSqlSession();
  		TaskMapper3 mapper = ss.getMapper(TaskMapper3.class);
  		try {
  			int totalCount = mapper.findBySellerPutTimeVoCount(vo);
  			Page<SellerPutTimeVo> page = Page.newInstance(vo.pageNo, vo.pageSize, totalCount);
  			if(page.totalCount<=0) {
  				return Page.EMPTY;
  			}
  			page.items = mapper.findBySellerPutTimeVo(vo);
  			return page;
  		}finally {
  			ss.close();
  		}
  	}

	/**
	 * 买手接任务记录
	 *
	 * @param vo
	 * @return
	 * @since  v2.0
	 * @author Mark Xu
	 * @created 2015-5-8 下午2:11:05
	 */
	public static Page<BuyerTakeTaskVo> findByBuyerTakeTaskVo(BuyerTakeTaskVo vo) {
		SqlSession ss = SessionFactory.getSqlSession();
  		TaskMapper3 mapper = ss.getMapper(TaskMapper3.class);
  		try {
  			int totalCount = mapper.findByBuyerTakeTaskVoCount(vo);
  			Page<BuyerTakeTaskVo> page = Page.newInstance(vo.pageNo, vo.pageSize, totalCount);
  			if(page.totalCount<=0) {
  				return Page.EMPTY;
  			}
  			page.items = mapper.findByBuyerTakeTaskVo(vo);
  			return page;
  		}finally {
  			ss.close();
  		}
	}
	
	
	/**
	 * 
	 * 通过ItemId去查找网页
	 *
	 * @param itemId
	 * @return
	 * @since  v2.0
	 * @author Mark Xu
	 * @created 2015-4-10 下午2:54:03
	 */
	public static Task3 findByItemId(String itemId) {
		SqlSession ss = SessionFactory.getSqlSession();
		try {
			TaskMapper3 mapper = ss.getMapper(TaskMapper3.class);
			return mapper.findByItemId(itemId);
		}finally {
			ss.close();
		}
	}

	/**
	 * 修改大任务的商家任务要求
	 *
	 * @param taskRequest
	 * @param id
	 * @since  v2.0
	 * @author Mark Xu
	 * @created 2015-4-30 上午10:28:24
	 */
	public static void updateTaskRequest(String taskRequest, long id) {
		SqlSession ss = SessionFactory.getSqlSession();
		try {
			TaskMapper3 mapper = ss.getMapper(TaskMapper3.class);
			mapper.updateTaskRequest(taskRequest, id);
		}finally {
			ss.close();
		}
	}
	/**
	 * 
	 *修改快递类型临时接口
	 *
	 * @param taskRequest
	 * @param id
	 * @since  v3.4
	 * @author fufei
	 * @created 2015年5月6日 下午5:48:46
	 */
	   public static void updateTaskExpressType() {
	        SqlSession ss = SessionFactory.getSqlSession();
	        try {
	            TaskMapper3 mapper = ss.getMapper(TaskMapper3.class);
	            List<Task3> tasks=mapper.findTaskExpressType();
	            List<Long> ids = Lists.newArrayList();
	            for (Task3 task : tasks) {
                    ids.add(task.id);
                }
	            mapper.updateExpressType(ids);
	        }finally {
	            ss.close();
	        }
	    }
    /**
     * 
     * 将ID解析为前端可展示的字符串.
     *
     * @since  v0.2.4
     * @author youblade
     * @created 2014年11月1日 下午6:00:57
     */
    public Task3 resolveId() {
        if (this.id != null) {
            this.idStr = String.valueOf(this.id);
        }
        if(this.expressType!=null){
            this.expressTypeStr=this.expressType.title;
        }
        if(this.collectionType!=null){
            this.collectionTypeStr=this.collectionType.title;
        }
        return this;
    }
    
    public static Task3 newInstance() {
        Task3 task = new Task3();
        return task;
    }
    
    public static Task3 instance(long id) {
        Task3 task = new Task3();
        task.id = id;
        return task;
    }
    
    public static Task3 instanceForTest(long id) {
        Task3 task = instance(id);
        task.pcOrderNum = 0;
        task.mobileOrderNum = 0;
        task.pcTakenCount = 0;
        task.mobileTakenCount = 0;
        task.extraRewardIngot = 0;
        task.speedTaskIngot = 0;
        task.speedExamine = false;
        task.pledge = 0L;
        task.totalPledge = 0L;
        task.baseOrderIngot = 0L;
        task.totalIngot = 0L;
        task.publishTimerInterval = 0;
        task.publishTimerValue = 0;
        return task;
    }
    
	public Task3 setSellerId(long sellerId) {
	    this.sellerId = sellerId;
	    return this;
	}
	
    public Task3 setStatus(TaskStatus status) {
        this.status = status;
        return this;
    }

    public Task3 setPublishTimerInterval(int publishTimerInterval) {
        this.publishTimerInterval = publishTimerInterval;
        return this;
    }

    public Task3 setLastBatchPublishTime(Date lastBatchPublishTime) {
        this.lastBatchPublishTime = lastBatchPublishTime;
        return this;
    }
    
    public Task3 speedTaskIngot(int speedTaskIngot) {
        this.speedTaskIngot = speedTaskIngot;
        return this;
    }
    
    public Task3 extraRewardIngot(int extraRewardIngot) {
        this.extraRewardIngot = extraRewardIngot;
        return this;
    }

    public Task3 modifyTime(Date modifyTime){
        this.modifyTime = modifyTime;
        return this;
    }
    
    public Task3 shopId(long shopId){
        this.shopId = shopId;
        return this;
    }
    
    public Task3 shopName(String shopName) {
        this.shopName = shopName;
        return this;
    }

    public Task3 sellerNick(String sellerNick) {
        this.sellerNick = sellerNick;
        return this;
    }
    
    public Task3 platform(Platform3 p) {
        this.platform = p;
        return this;
    }
    
    public Task3 orderNum(Device d, int orderNum) {
        Validate.notNull(d);
        if (d == Device.PC) {
            this.pcOrderNum = orderNum;
        } else {
            this.mobileOrderNum = orderNum;
        }
        return this;
    }
    
    public Task3 itemUrl(String itemUrl) {
        this.itemUrl = itemUrl;
        return this;
    }
    
    public Task3 status(TaskStatus status) {
        this.status = status;
        return this;
    }
    
    @NotNull
	public static List<Task3> findALLList(TaskSearchVo3 vo) {

		// 注册时间在一周内的视为“新商家”
		vo.registTime = LocalDate.now().minusWeeks(1).toDate();
		vo.modifyTimeEnd = LocalDate.now().minusDays(1).toDate();
		SqlSession ss = SessionFactory.getSqlSession();
		try {
			TaskMapper3 mapper = ss.getMapper(TaskMapper3.class);
			List<Task3> tasks = mapper.selectAllList(vo);
			
			return tasks;

		} finally {
			ss.close();
		}
	}

    

}
