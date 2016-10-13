package vos;

import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

import com.aton.config.BizConstants;
import com.aton.util.NumberUtils;

import enums.Device;
import enums.Platform;
import enums.TaskStatus;
import enums.TaskType;

/**
 * 商家对账
 * 
 * @author fufei
 * @since v2.0
 * @created 2015年2月28日 下午3:14:55
 */
public class SellerBalanceVo extends Page {
	public Long taskId;// 任务Id
	public Date takeTime;// 接单时间
	public String buyerAccountNick;// 买号
	public String orderId;// 订单ID
	public TaskStatus status;// 任务状态
	public Double itemPrice;// 商品价格
	public Integer expressIngot;// 平台快递费
	public String itemId;
	public Boolean sysExpress;// 是否平台快递
	public Boolean sysRefund;// 平台返款
	public Integer extraRewardIngot;// 加赏
	public Integer buyTimeInterval;// 限制购买周期
	public Boolean speedExamine;// 是否优先审核
	public Integer publishTimerInterval;// 定时发布任务
	public int totalOrderNum;// 任务总数
	/** PC端任务数 */
	public Integer pcOrderNum;
	/** 移动端任务数 */
	public Integer mobileOrderNum;
	/** PC端已接手任务数 */
	public Integer pcTakenCount;
	/** 移动端已接手任务数 */
	public Integer mobileTakenCount;
	public int finishedCount;
	/**
	 * 加速完成任务的佣金（平台推荐的任务）
	 */
	public Integer speedTaskIngot;

	public Long totalIngot;
	public Long totalPledge;

	public Platform platform;
	public TaskType type;
	public String shopName;
	public Date publishTime;
	public Long id;// 任务Id
	public Long sellerId;
	public String sellerNick;
	public String taskIdStr;
	// 查询字段
	public Boolean isFreeShipping;// 是否包邮
	public int itemBuyNum;// 购买件数
	public Long paidFee;// 买手付款
	public Long baseOrderIngot;// 刷单服务每单的费用(买手佣金)
	public Device device;// 任务类型

	// 用于显示的字段
	public String statusStr;
	public Integer postage;// 邮费
	public String refundDeposit;// 退款保证金(商品价格*购买数量*0.05)
	public String paidFeeStr;// 买手付款
	public String baseOrderIngotStr;// 刷单服务每单的费用【买手佣金】(移动端每单加0.5元)
	public String sysRefundStr;// 平台返款费用(平台返款:商品价格*0.006)
	public String totalDeposit;// 总开销
	public String speedExamineStr;// 加速审核5元
	public String publishTimerIntervalStr;// 定时发布任务5元
	public String itemPriceStr;
	// 商家任务
	public String publishIngot;// 发布任务总金额(totalIngot+totalPledge)
	public Integer totalTaken;// 接单总数
	public String pcRefundIngot;// pc端退款金额
	public String mobileRefundIngot;// 移动端退款金额
	public String takeTimeStr;
	public String publishTimeStr;
	public String platformStr;
	public String isSysRefundStr;//是否平台返款
	public String isExpressStr;//是否平台快递
	public Integer noPcTackCount;//pc未接手
	public Integer noMobileTackCount;//移动端未接手
	// 增值服务费用
	public String totalextraRewardIngot;
	public Integer totalBuyTimeInterval;
	public String totalExtraIngot;

	public Date beginTakeTime;
	public Date endTakeTime;
	public Date beginPublishTime;
	public Date endPublishTime;

	public static SellerBalanceVo newInstence() {
		return new SellerBalanceVo();
	}

	public SellerBalanceVo sellerId(long sellerId) {
		this.sellerId = sellerId;
		return this;
	}
	//计算邮费
	public SellerBalanceVo postage() {
		if (this.isFreeShipping)
			this.postage = 0;
		else
			this.postage = 10;
		return this;
	}
	//退款保证金
	public SellerBalanceVo refundDeposit() {
		double buyerTaskPaidFee = this.itemPrice * this.itemBuyNum;
		this.refundDeposit=m2(new BigDecimal(buyerTaskPaidFee/100 * BizConstants.TASK_INSURANCE_RATE).setScale(0, BigDecimal.ROUND_HALF_UP));
		return this;
	}
	//买手付款
	public SellerBalanceVo paidFeeStr() {
		if(NumberUtils.isGreaterThanZero(this.paidFee))
		this.paidFeeStr=m2((double)this.paidFee/100);
		return this;
	}
	
	//买手佣金
	public SellerBalanceVo baseOrderIngotStr() {
		if(this.device==Device.PC)
			this.baseOrderIngotStr=m2(this.baseOrderIngot/100);
		else if(this.device==Device.MOBILE)
			this.baseOrderIngotStr=m2(this.baseOrderIngot/100+BizConstants.TASK_MOBILE_INGOT);
		return this;
	}
	//平台返款
	public SellerBalanceVo sysRefundStr() {
		if(this.sysRefund){
			this.sysRefundStr=m2(new BigDecimal(itemPrice/100 * itemBuyNum).multiply(BigDecimal.valueOf(BizConstants.TASK_SYS_REFUND))
		            .setScale(2, BigDecimal.ROUND_HALF_UP));
		}
		else
			this.sysRefundStr="0.00";
		return this;
	}
	//总费用
	public SellerBalanceVo totalDeposit() {
		this.totalDeposit=m2((Double.parseDouble(this.baseOrderIngotStr)+this.expressIngot/100+Double.parseDouble(this.sysRefundStr)+this.extraRewardIngot+convertNumToLong(this.buyTimeInterval)));
		return this;
	}
	/**
	 * 
	 * 子任务计算
	 *
	 * @return
	 * @since  v2.6
	 * @author fufei
	 * @created 2015年3月3日 下午4:27:11
	 */
	public SellerBalanceVo buyerTaskcalculate() {
		this.taskIdStr=String.valueOf(this.taskId);
		this.statusStr=this.status.title;
		this.itemPriceStr=m2(this.itemPrice/100);
		this.buyTimeInterval=this.buyTimeInterval==null?0:this.buyTimeInterval;
		
		return this.postage().refundDeposit().paidFeeStr().baseOrderIngotStr().sysRefundStr().totalDeposit();
	}
	
	public long convertNumToLong(Integer num) {
		if(num==null){
			return 0;
		}
		return num;
	}
	
	public String m2(Object o) {
        return String.format("%.2f",Float.parseFloat(o.toString()));
    }
	//发布时间
	public SellerBalanceVo publishTimeStr() {
		if(this.publishTime!=null)
		this.publishTimeStr=new SimpleDateFormat("yyyy-MM-dd hh:mm:ss").format(this.publishTime);
		return this;
	}
	public SellerBalanceVo publishIngot() {
		this.publishIngot=m2((this.totalIngot+this.totalPledge)/100);
		return this;
	}
	
	public SellerBalanceVo totalTaken() {
		this.totalTaken=this.pcTakenCount+this.mobileTakenCount;
		return this;
	}
	/////////////////////
	public SellerBalanceVo pcRefundIngot() {
		BigDecimal decimal=new BigDecimal(this.itemPrice/100).add(new BigDecimal(this.itemPrice*BizConstants.TASK_INSURANCE_RATE/100)).add(new BigDecimal(this.expressIngot/100)).add(new BigDecimal(this.extraRewardIngot));
		if(this.sysRefund){
			decimal=decimal.add(BigDecimal.valueOf(this.itemPrice/100).multiply(BigDecimal.valueOf(BizConstants.TASK_SYS_REFUND)));
		}
		if(this.buyTimeInterval!=null&&this.buyTimeInterval>0){
			decimal=decimal.add(BigDecimal.valueOf(this.buyTimeInterval));
		}
		this.pcRefundIngot=m2(decimal.multiply(BigDecimal.valueOf(this.pcOrderNum-this.pcTakenCount)).setScale(2, BigDecimal.ROUND_HALF_UP));
		return this;
	}
	
	public SellerBalanceVo mobileRefundIngot() {
		BigDecimal decimal=new BigDecimal(this.itemPrice/100).add(new BigDecimal(this.itemPrice*BizConstants.TASK_INSURANCE_RATE/100)).add(new BigDecimal(this.expressIngot/100)).add(new BigDecimal(this.extraRewardIngot));
		if(this.sysRefund){
			decimal=decimal.add(BigDecimal.valueOf(this.itemPrice/100).multiply(BigDecimal.valueOf(BizConstants.TASK_SYS_REFUND)));
		}
		if(this.buyTimeInterval!=null&&this.buyTimeInterval>0){
			decimal=decimal.add(new BigDecimal(this.buyTimeInterval));
		}
		this.mobileRefundIngot=m2(decimal.multiply(BigDecimal.valueOf(this.mobileOrderNum-this.mobileTakenCount)).setScale(2, BigDecimal.ROUND_HALF_UP));
		return this;
	}
	
	public SellerBalanceVo taskSysRefundStr() {
		this.sysRefundStr=m2(BigDecimal.valueOf(this.itemPrice/100).multiply(BigDecimal.valueOf(BizConstants.TASK_SYS_REFUND)).multiply(BigDecimal.valueOf(this.totalOrderNum)).setScale(2, BigDecimal.ROUND_HALF_UP));
		return this;
	}
	public SellerBalanceVo extraRewardIngot() {
		this.extraRewardIngot=this.extraRewardIngot*this.totalOrderNum;
		return this;
	}
	
	public SellerBalanceVo buyTimeInterval() {
		if(this.buyTimeInterval!=null)
		this.buyTimeInterval=this.buyTimeInterval*this.totalOrderNum;
		else {
			this.buyTimeInterval=0;
		}
		return this;
	}
	public SellerBalanceVo speedExamineStr() {
		if(this.speedExamine){
			this.speedExamineStr="5.00";
		}else {
			this.speedExamineStr="0.00";
		}
		return this;
	}
	public SellerBalanceVo publishTimerIntervalStr() {
		if(this.buyTimeInterval!=null&&this.publishTimerInterval>0){
			this.publishTimerIntervalStr="5.00";
		}else {
			this.publishTimerIntervalStr="0.00";
		}
		return this;
	}
	/**
	 * 
	 * 父任务计算
	 *
	 * @return
	 * @since  v2.6
	 * @author fufei
	 * @created 2015年3月3日 下午4:27:27
	 */
	public SellerBalanceVo taskcalculate() {
		this.taskIdStr=String.valueOf(this.id);
		this.platformStr=this.platform.title;
		this.statusStr=this.status.title;
		this.itemPriceStr=m2(itemPrice/100);
		this.noPcTackCount=this.pcOrderNum-this.pcTakenCount;
		this.noMobileTackCount=this.mobileOrderNum-this.mobileTakenCount;
		this.isSysRefundStr=this.sysRefund!=null&&this.sysRefund==true?"是":"否";
		this.isExpressStr=this.sysExpress!=null&&this.sysExpress==true?"是":"否";
		BigDecimal bigDecimal=new BigDecimal(this.speedTaskIngot);
		if(this.speedExamine){
			bigDecimal=bigDecimal.add(BigDecimal.valueOf(5));
		}
		if(this.publishTimerInterval>0){
			bigDecimal=bigDecimal.add(BigDecimal.valueOf(5));
		}
		this.totalDeposit=m2(bigDecimal.setScale(2, BigDecimal.ROUND_HALF_UP));
		return this.publishTimeStr().publishIngot().totalTaken().pcRefundIngot().mobileRefundIngot().taskSysRefundStr().extraRewardIngot().buyTimeInterval().speedExamineStr().publishTimerIntervalStr();
	}

}
