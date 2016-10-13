package vos;

import java.text.MessageFormat;
 import java.text.SimpleDateFormat;
import java.util.Date;

import com.aton.util.StringUtils;

import enums.TaskStatus;

/**
 * 
 * 
 * 订单快递单Vo
 * 
 * @author moloch
 * @since v0.1
 * @created 2014-8-13 下午3:24:09
 */
public class OrderExpress {

	/** 买手任务id */
	public long id;

	/** 主任务id */
	public long taskId;

	/** 订单号 */
	public String orderId;
	
	/** 商家旺旺ID */
	public String tbSellerNick;
	
	/**
	 * 发件人信息
	 */
    public String sellerStreet;
    public String sellerAddress;
    public String shipper;//发货人
    public String sellerMobile;
	

	/*
	 * 收货信息
	 */
	public String state;
	public String city;
	public String region;
	public String address;
	public String consignee;//收货人
	public String mobile;
	
	public String number;
	public String fullAddress;//收件人地址
	public String shipperfullAddress;//发贱人地址
	public String expressNo;//快递单号
	public Date modifyTime;
	public String modifyTimeStr;
	public float weight;//重量
	public String branch;//览件网点
	
	
	public TaskStatus status;
	
	/**
	 * 
	 * 合并编号，地址字段
	 *
	 * @since  v0.1
	 * @author moloch
	 * @created 2014-8-14 下午2:29:58
	 */
    public void resolve() {
        this.number = this.taskId + "-" + this.id;
        this.fullAddress = MessageFormat.format("{0}{1}{2}{3} ", this.state, this.city,
            StringUtils.trimToEmpty(this.region), this.address);
        this.shipperfullAddress=StringUtils.replace(this.sellerAddress, "-", "")+this.sellerStreet;
        if(this.modifyTime!=null)
        this.modifyTimeStr=new SimpleDateFormat("yyyy-MM-dd hh:mm:ss").format(this.modifyTime);
        
    }
}
