package vos;

import java.util.Date;

import javax.persistence.Transient;

import com.aton.util.DateUtils;
import com.aton.util.StringUtils;

import enums.Device;
import enums.ExpressType;
import enums.Platform2;
import enums.TaskStatus;
import enums.TaskType;
import enums.TaskType2;
import enums.pay.PayPlatform;

public class BuyerTaskVo2 {

    /** 买家任务编号：父任务ID-子任务ID */
    public String buyerTaskNo;
    
	/** 买家任务ID */
	public Long id;

	/** 主任务ID */
	public Long taskId;
	public Device device;
	public Platform2 platform;
	public Long shopId;
	public String shopName;
	public String itemId;
	public String itemTitle;
	public String itemPicUrl;
	public String itemPic;
	public String additionalItemTitle;
	public String additionalItemPicUrl;
	public String additionalItemUrl;
	public boolean sysExpress;
	/** 平台返款标记 **/
	public boolean sysRefund;

	public Long buyerAccountId;
	public String buyerAccountNick;
	public String orderId;
	public String expressCompany;
	public String goodCommentWords;
	public String expressNo;
	public String transNo;
	public Long sellerId;
	public String sellerNick;
	public String buyerNick;
	//========以下为买手任务列表展示============
	/** 垫付资金 **/
	public Long paidFee;
	/** 佣金 **/
	public Long rewardIngot;
	@Deprecated
	public TaskStatus status;
	public String statusTitle;
	
	//========以下为买手做任务时展示所需============
	/** 垫付资金 **/
	public Date takeTime;
	public String word;
	public String skus;
	public long searchPlanId;
	public Long itemSearchMinPrice;
	public Long itemSearchMaxPrice;
	public String itemSearchLocation;
	public Long itemPrice;
	public Integer itemBuyNum;
	public boolean isFreeShipping;
	public String orderMessages;
	public String itemUrl;
	public TaskType2 taskType;
	public String itemSubwayPic;
	
	//========以下为查看买手任务详情时所需============
	public Long buyerId;
	public Date finishTime;
	
	//========以下为卖家待退款任务列表展示用==========
	public String refundAccountUser;
	public PayPlatform refundAccountType;
	public String refundAccountNo;
	public String taskIdStr;
	public String consignee;
	
	//=========以下为获得买手任务列表所需============
	public String sellerAdminName;
	public String buyerAdminName;
	
	//=========以下为获得主任务类型所需==============
	public int recommendIngot;
	public int extraRewardIngot;
	public Date registTime;
	
	public String qq;
	
	/**
	 * 买手确认收货的时间
	 */
	public Date modifyTime;
	public String taskRequest;
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
		return "BuyerTaskVo2 [buyerTaskNo=" + buyerTaskNo + ", id=" + id
				+ ", taskId=" + taskId + ", device=" + device + ", platform="
				+ platform + ", shopId=" + shopId + ", shopName=" + shopName
				+ ", itemId=" + itemId + ", itemTitle=" + itemTitle
				+ ", itemPicUrl=" + itemPicUrl + ", itemPic=" + itemPic
				+ ", additionalItemTitle=" + additionalItemTitle
				+ ", additionalItemPicUrl=" + additionalItemPicUrl
				+ ", additionalItemUrl=" + additionalItemUrl + ", sysExpress="
				+ sysExpress + ", sysRefund=" + sysRefund + ", buyerAccountId="
				+ buyerAccountId + ", buyerAccountNick=" + buyerAccountNick
				+ ", orderId=" + orderId + ", expressCompany=" + expressCompany
				+ ", goodCommentWords=" + goodCommentWords + ", expressNo="
				+ expressNo + ", transNo=" + transNo + ", sellerId=" + sellerId
				+ ", sellerNick=" + sellerNick + ", buyerNick=" + buyerNick
				+ ", paidFee=" + paidFee + ", rewardIngot=" + rewardIngot
				+ ", status=" + status + ", statusTitle=" + statusTitle
				+ ", takeTime=" + takeTime + ", word=" + word + ", skus="
				+ skus + ", searchPlanId=" + searchPlanId
				+ ", itemSearchMinPrice=" + itemSearchMinPrice
				+ ", itemSearchMaxPrice=" + itemSearchMaxPrice
				+ ", itemSearchLocation=" + itemSearchLocation + ", itemPrice="
				+ itemPrice + ", itemBuyNum=" + itemBuyNum
				+ ", isFreeShipping=" + isFreeShipping + ", orderMessages="
				+ orderMessages + ", itemUrl=" + itemUrl + ", taskType="
				+ taskType + ", itemSubwayPic=" + itemSubwayPic + ", buyerId="
				+ buyerId + ", finishTime=" + finishTime
				+ ", refundAccountUser=" + refundAccountUser
				+ ", refundAccountType=" + refundAccountType
				+ ", refundAccountNo=" + refundAccountNo + ", taskIdStr="
				+ taskIdStr + ", consignee=" + consignee + ", sellerAdminName="
				+ sellerAdminName + ", buyerAdminName=" + buyerAdminName
				+ ", recommendIngot=" + recommendIngot + ", extraRewardIngot="
				+ extraRewardIngot + ", registTime=" + registTime + ", qq="
				+ qq + ", modifyTime=" + modifyTime + ", taskRequest="
				+ taskRequest + ", goodCommentImg=" + goodCommentImg
				+ ", expressType=" + expressType + ", expressTypeStr="
				+ expressTypeStr + "]";
	}

	public void value() {
        this.taskIdStr = String.valueOf(this.taskId);
        if (status != null) {
            this.statusTitle = this.status.title;
        }
        if(this.expressType!=null){
            this.expressTypeStr=this.expressType.title;
        }
    }
    
    public String formatModifyTime() {
        if (modifyTime != null) {
            return DateUtils.formatToStr(modifyTime);
        }
        return null;
    }
    
    public String getBuyerTaskNo() {
        return this.taskId + "-" + this.id;
    }
	
    public void subShopNamePrefix() {
        if (this.shopName.length() > 3) {
            this.shopName = StringUtils.substring(this.shopName, 0, 3);
        }
    }
}
