package vos;

import java.util.Date;
import java.util.List;

import org.joda.time.DateTime;
import org.joda.time.LocalDate;

import com.google.common.collect.Lists;

import enums.Device;
import enums.ExpressType;
import enums.Platform;
import enums.TaskListType;
import enums.TaskStatus;
import enums.TaskType;

public class TaskSearchVo extends Page {

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

	public Platform platform;
	public Device device;
	public TaskType taskType;
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

	
	
	
	public TaskSearchVo() {
		super();
	}

	/*
	 * 仅用于构造SQL条件
	 */
	public List<TaskStatus> statusList;
	public List<TaskStatus> statusExcluedeList;

	public static TaskSearchVo newInstance() {
		return new TaskSearchVo();
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
	public static TaskSearchVo newInstance(String fields) {
		TaskSearchVo vo = new TaskSearchVo();
		vo.fields = fields;
		return vo;
	}

	public TaskSearchVo status(TaskStatus status) {
		this.status = status;
		return this;
	}

	public TaskSearchVo taskId(Long taskId) {
		this.taskId = taskId;
		return this;
	}

	public TaskSearchVo sellerId(long sellerId) {
		this.sellerId = sellerId;
		return this;
	}

	public TaskSearchVo buyerId(long buyerId) {
		this.buyerId = buyerId;
		return this;
	}

	public TaskSearchVo pageNo(int pageNo) {
		this.pageNo = pageNo;
		return this;
	}

	public TaskSearchVo pageSize(int pageSize) {
		this.pageSize = pageSize;
		return this;
	}

	public TaskSearchVo date(LocalDate start) {
		this.createTimeStart = start.toDate();
		this.createTimeEnd = start.plusDays(1).toDate();
		return this;
	}

	public TaskSearchVo fields(String fields) {
		this.fields = fields;
		return this;
	}

	public TaskSearchVo module(SearchModule module) {
		this.module = module;
		return this;
	}

	public TaskSearchVo shopName(String shopName) {
		this.shopName = shopName;
		return this;
	}

	public TaskSearchVo statusIn(TaskStatus... statuses) {
		this.statusList = Lists.newArrayList(statuses);
		return this;
	}
	
	public TaskSearchVo statusExclude(TaskStatus... statuses) {
	    this.statusExcluedeList = Lists.newArrayList(statuses);
	    return this;
	}

	public TaskSearchVo modifyTimeEnd(DateTime end) {
		this.modifyTimeEnd = end.toDate();
		return this;
	}

	public TaskSearchVo platform(Platform platform) {
		this.platform = platform;
		return this;
	}

	public TaskSearchVo buyerTaskId(long buyerTaskId) {
		this.buyerTaskId = buyerTaskId;
		return this;
	}

	public TaskSearchVo sysRefund(Boolean sysRefund) {
		this.isSysRefundTask = sysRefund;
		return this;
	}

    public TaskSearchVo device(Device device) {
        this.device = device;
        return this;
    }

	public TaskSearchVo(String fields, SearchModule module, Platform platform,
			Device device, TaskType taskType, TaskListType taskListType,
			Date registTime, TaskStatus status, Long sellerId, Long taskId,
			String itemTitle, Date publishTimeStart, Date publishTimeEnd,
			String buyerNick, String type, Long buyerTaskId, String orderId,
			Long shopId, Long buyerAccountId, String buyerAccountNick,
			Long buyerId, String expressNo, String tenpayNo,
			Date takeTimeStart, Date takeTimeEnd, Integer exportNo,
			String batchNum, String printId, ExpressType expressType,
			String shopName, String sellerNick, Boolean isSysRefundTask,
			Date createTimeStart, Date createTimeEnd, Date modifyTimeStart,
			Date modifyTimeEnd, Long userWithdrawRecordSn,
			List<TaskStatus> statusList, List<TaskStatus> statusExcluedeList) {
		super();
		this.fields = fields;
		this.module = module;
		this.platform = platform;
		this.device = device;
		this.taskType = taskType;
		this.taskListType = taskListType;
		this.registTime = registTime;
		this.status = status;
		this.sellerId = sellerId;
		this.taskId = taskId;
		this.itemTitle = itemTitle;
		this.publishTimeStart = publishTimeStart;
		this.publishTimeEnd = publishTimeEnd;
		this.buyerNick = buyerNick;
		this.type = type;
		this.buyerTaskId = buyerTaskId;
		this.orderId = orderId;
		this.shopId = shopId;
		this.buyerAccountId = buyerAccountId;
		this.buyerAccountNick = buyerAccountNick;
		this.buyerId = buyerId;
		this.expressNo = expressNo;
		this.tenpayNo = tenpayNo;
		this.takeTimeStart = takeTimeStart;
		this.takeTimeEnd = takeTimeEnd;
		this.exportNo = exportNo;
		this.batchNum = batchNum;
		this.printId = printId;
		this.expressType = expressType;
		this.shopName = shopName;
		this.sellerNick = sellerNick;
		this.isSysRefundTask = isSysRefundTask;
		this.createTimeStart = createTimeStart;
		this.createTimeEnd = createTimeEnd;
		this.modifyTimeStart = modifyTimeStart;
		this.modifyTimeEnd = modifyTimeEnd;
		this.userWithdrawRecordSn = userWithdrawRecordSn;
		this.statusList = statusList;
		this.statusExcluedeList = statusExcluedeList;
	}
    
    
    
    
}
