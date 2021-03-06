package vos;

import java.util.Date;
import java.util.List;

import org.joda.time.DateTime;
import org.joda.time.LocalDate;

import com.google.common.collect.Lists;

import enums.Device;
import enums.ExpressType;
import enums.Platform;
import enums.Platform3;
import enums.TaskListType;
import enums.TaskStatus;
import enums.TaskType;
import enums.TaskType3;

public class TaskSearchVo3 extends Page {

	/**
	 * 目标查询字段
	 */
	public String fields;

	public enum SearchModule {
		/**
		 * 查询“商家超时未退款”的任务
		 */
		SELLE_REFUND_OVERDUE,
		/**
		 * 商家退款
		 */
		SELLER_REFUND,
		/**
		 * 平台返款-本金提现
		 */
		SYS_REFUND_WITHDRAW,
		/**
		 * 平台退款
		 */
		WAIT_SELLER_CONFIRM_SYS_REFUND, WAIT_BUYER_CONFIRM_EDITED_SYS_REFUND, WAIT_BUYER_CONFIRM_SYS_REFUND, BUYER_REJECT_SYS_REFUND, WAIT_SYS_REFUND, REFUNDING, FINISHED
	}

	public SearchModule module;

	public Platform3 platform;
	public Device device;
	public TaskType3 taskType;
	public TaskListType taskListType;

	public Date registTime;

	public TaskStatus status;
	public Long sellerId;
	/**
	 * 卖家任务（父任务）ID
	 */
	public Long taskId;
	public String itemTitle;// 商品标题

	public Date publishTimeStart;// 发布时间
	public Date publishTimeEnd;

	public String buyerNick;// 买家账号
	/**
	 * 查询超时任务时的类型
	 */
	public String type;
	/**
	 * 买手任务（子任务）ID
	 */
	public Long buyerTaskId;

	/*
	 * 买家任务相关
	 */
	public String orderId;
	public Long shopId;
	public Long buyerAccountId;
	public String buyerAccountNick;
	public Long buyerId;
	public String expressNo;
	public String tenpayNo;
	public Date takeTimeStart;
	public Date takeTimeEnd;
	/** 导出数量 */
	public Integer exportNo;
	
	public String batchNum;//批次号
	//准备打印订单的id
	public String printId;
	 public ExpressType expressType;
	/*
	 * 审核任务
	 */
	public String shopName;
	public String sellerNick;

	// 父任务是否为平台
	public Boolean isSysRefundTask;

	public Date createTimeStart;
	public Date createTimeEnd;

	public Date modifyTimeStart;
	public Date modifyTimeEnd;

	/**
	 * 提现(申请)记录流水号
	 */
	public Long userWithdrawRecordSn;

	/*
	 * 仅用于构造SQL条件
	 */
	public List<TaskStatus> statusList;
	public List<TaskStatus> statusExcluedeList;

	public static TaskSearchVo3 newInstance() {
		return new TaskSearchVo3();
	}

	/**
	 * 
	 * 用于查询指定字段.
	 * 
	 * @param fields
	 * @return
	 * @since v0.2.4
	 * @author youblade
	 * @created 2014年11月1日 下午5:26:19
	 */
	public static TaskSearchVo3 newInstance(String fields) {
		TaskSearchVo3 vo = new TaskSearchVo3();
		vo.fields = fields;
		return vo;
	}

	public TaskSearchVo3 status(TaskStatus status) {
		this.status = status;
		return this;
	}

	public TaskSearchVo3 taskId(Long taskId) {
		this.taskId = taskId;
		return this;
	}

	public TaskSearchVo3 sellerId(long sellerId) {
		this.sellerId = sellerId;
		return this;
	}

	public TaskSearchVo3 buyerId(long buyerId) {
		this.buyerId = buyerId;
		return this;
	}

	public TaskSearchVo3 pageNo(int pageNo) {
		this.pageNo = pageNo;
		return this;
	}

	public TaskSearchVo3 pageSize(int pageSize) {
		this.pageSize = pageSize;
		return this;
	}

	public TaskSearchVo3 date(LocalDate start) {
		this.createTimeStart = start.toDate();
		this.createTimeEnd = start.plusDays(1).toDate();
		return this;
	}

	public TaskSearchVo3 fields(String fields) {
		this.fields = fields;
		return this;
	}

	public TaskSearchVo3 module(SearchModule module) {
		this.module = module;
		return this;
	}

	public TaskSearchVo3 shopName(String shopName) {
		this.shopName = shopName;
		return this;
	}

	public TaskSearchVo3 statusIn(TaskStatus... statuses) {
		this.statusList = Lists.newArrayList(statuses);
		return this;
	}
	
	public TaskSearchVo3 statusExclude(TaskStatus... statuses) {
	    this.statusExcluedeList = Lists.newArrayList(statuses);
	    return this;
	}

	public TaskSearchVo3 modifyTimeEnd(DateTime end) {
		this.modifyTimeEnd = end.toDate();
		return this;
	}

	public TaskSearchVo3 platform(Platform3 platform) {
		this.platform = platform;
		return this;
	}

	public TaskSearchVo3 buyerTaskId(long buyerTaskId) {
		this.buyerTaskId = buyerTaskId;
		return this;
	}

	public TaskSearchVo3 sysRefund(Boolean sysRefund) {
		this.isSysRefundTask = sysRefund;
		return this;
	}

    public TaskSearchVo3 device(Device device) {
        this.device = device;
        return this;
    }
}
