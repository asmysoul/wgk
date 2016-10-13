package vos;

import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.Date;

import models.UserWithdrawRecord.WithdrawStatus;

import com.aton.util.StringUtils;

import enums.Device;
import enums.Platform;
import enums.TaskStatus;
import enums.TaskType;
import enums.pay.KQpayPlatform;
import enums.pay.PayPlatform;

public class BuyerDepositVo {

    
	/** 买家任务ID */
	public Long id;

	/** 主任务ID */
	public String taskId;
	
	/** 提现账户信息  */
	public String fundUserName;
	public String fundOpeningBank;
	public String fundNo;
	public long applyAmount;
	public String parseApplyAmount;
	public String buyerAccountNick;
	public String buyerNick;
	
	public String shopNick;
	
	public String province;
	
	public String city;
	
	public String memo;
	
	public String address;
	
	public KQpayPlatform type;

	public  WithdrawStatus status;
	
	public String tradeNo;
    /** 申请提款时间 */
    public Date applyTime;
    public String parseApplyTime;
	

    public String getBuyerTaskNo() {
        return this.taskId + "-" + this.id;
    }
    
    public void parseApplyAmount(long applyAmount){        
        this.parseApplyAmount = new BigDecimal(applyAmount).movePointLeft(2).toString();
    }
    public void parseApplyTime(Date applyTime){
        this.parseApplyTime = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(applyTime);
    }
   
}
	
    