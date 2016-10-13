package vos;

import java.text.MessageFormat;
 import java.text.SimpleDateFormat;
import java.util.Date;

import com.aton.util.StringUtils;

import enums.TaskStatus;

/**
 * 
 * 
 * 发包裹vo
 * 
 * @author fufei
 * @since  v3.4
 * @created 2015年4月29日 下午1:44:39
 */
public class FaBaoGuoVo {

	/** 买手任务id */
	public long id;

	/** 订单号 */
	public String order_sn;
	
	/**
	 * 发件人信息
	 */
    public String send_addr;
    public String send_name;//发货人
    public String send_tel;
	

	/*
	 * 收货信息
	 */
	public String receive_addr;
	public String receive_name;//收货人
	public String receive_tel;
	
	public String goods_name;
	public String comment;//备注
	public String expressNo;//快递单号
	public Date send_time;
	public String sendTimeStr;
	public String weight;//重量
	public String net_no;//览件网点
	public String batch_num;//批次号
	
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
        this.sendTimeStr=new SimpleDateFormat("yyyy-MM-dd hh:mm:ss").format(this.send_time);
        if(this.goods_name==null){
            this.goods_name="";
        }
        if(this.net_no==null)
            this.net_no="";
        if(this.comment==null)
            this.comment="";
    }
    
    public static FaBaoGuoVo newInstance() {
        return new FaBaoGuoVo();
    }
}
