package enums;

import java.util.EnumSet;
import java.util.List;
import java.util.Set;

import com.google.common.collect.Lists;
import com.google.common.collect.Sets;


/**
 * 
 * 任务（以及子任务）状态.<br>
 * 
 * 任务记录在卖家、买手、平台三方流转，卖家发布任务后，平台进行审核，审核通过后买手可接手子任务，
 * 买手付款下单后由平台处理订单的快递信息，之后卖家对子任务进行退款，最后任务完成。
 * 
 * 任务状态分类及涉及的流程：
 * 1、卖家：任务创建、发货、退款
 * 2、平台：任务审核、处理子任务的快递单
 * 3、买手：任务接手、下单
 * 
 * @author youblade
 * @since 0.1
 * @created 2014年7月18日 上午10:12:25
 */
public enum TaskStatus {
	//@formatter:off
    /**
     * 【商家】卖家刚创建、尚未支付的任务为此状态，用户可以继续编辑并支付
     */
	WAIT_EDIT("待编辑",true), 
	
	/**
	 * 【商家】卖家选择“网银支付”并确定后为此状态，
	 * 任务处于该状态下则视为用户已进行实际的支付操作，并向第三方支付网关发起支付请求，
	 * 此时不允许再次修改数据（直到支付状态失效）
	 */
	WAITING_PAY("待支付", true),
	
	/**
	 * 【商家、平台】已支付，等待平台进行审核，用户可以编辑
	 */
	WAIT_EXAMINE("待审核", true),
    /**
     * 【商家、平台】平台已开始审核，审核过程尚未结束，用户不可编辑
     */
	EXAMINING("审核中", true),
	/**
	 * 【商家、平台】审核未通过，用户可以编辑
	 */
	NOT_PASS("审核未通过", true),
	
	//*---------------------------------------------------------
	/**
	 * 【商家、平台】审核通过，但设置的定时发布时间未到，在系统任务池中对【买手】不可见
	 * （仅购买了增值服务的定时任务有此状态）
	 */
	WAIT_PUBLISH("待发布", true), 
	
	/**
	 * 【商家、买手】审核通过，可被【买手】接手的任务
	 * （系统任务池中的任务会定时转换到此状态）
	 */
	PUBLISHED("已发布", true), 
    
	//*------------买家任务（BuyerTask）状态开始-----------------
	/**
	 * 【买手】任务已经接手，尚未开始做
	 */
	RECIEVED("已接手，未开始"),
	
	/**
	 * 【买手】任务已经开始做了，还没到做到下单支付的步骤
	 */
	WAIT_PAY("已接手，待付款"), 
	
    /**
     * 【买手、平台】任务已经下单支付
     */
    WAIT_SEND_GOODS("已完成，待审核返佣"),
    
    /**
     * 【平台】等待外部系统打印快递单
     * 1、【导出订单】后的任务即为此状态
     * 2、【导入订单快递号】时更新此状态的任务
     */
    WAIT_EXPRESS_PRINT("待审核111"),
    
    /**
     * 【平台、商家】快递单已在外部系统打印，并导入平台
     * 1、【导入订单快递号】之后的任务即为此状态
     * 2、卖家【确认发货】时处理此状态的任务
     */
    EXPRESS_PRINT("待返佣"),
    
    /**
     * 【商家、买手】等待买手确认收货
     * 1、卖家【确认发货】之后的任务即为此状态
     * 2、买手【确认收货】时处理此状态的任务
     */
    WAIT_CONFIRM("待审核222"),
    
    /**
     * 【买手 、商家】买手已确认收货并给出好评，等待卖家处理
     * 1、买手【确认收货并好评】后即为此状态
     * 2、卖家【确认退款】处理此状态的任务
     * 
     * 【注意！】在“平台返款”型任务场景下应使用状态：WAIT_SELLER_CONFIRM_SYS_REFUND
     */
    WAIT_REFUND("已收货，等待卖家退款"),
    
    /**
     * 【商家、买手】商家确认退款，等待买手核实
     * 1、卖家【确认退款】后即为此状态
     * 2、买手【核实退款】处理此状态的任务
     */
    REFUNDING("卖家退款中"),
    
    //*------------“平台返款”型的买手任务状态-----------------
    /**
     * 【商家、买手】买手已确认收货并给出好评，等待卖家处理
     * 1、买手【确认收货并好评】后即为此状态
     * 2、卖家【核实退款】处理此状态的任务
     */
    WAIT_SELLER_CONFIRM_SYS_REFUND("买手已收货，等待卖家核实平台返款金额"),
    
    
    /**
     * 【买手 、商家】商家核实后修改退款金额，等待买手核实
     * 1、卖家修改退金额，并【确认退款】后即为此状态
     * 2、买手【核实卖家修改后的退款】处理此状态的任务
     */
    WAIT_BUYER_CONFIRM_EDITED_SYS_REFUND("商家修改退款金额，等待买手核实"),
    
    /**
     * 【买手 、商家】商家核实完金额无误并确认退款，等待买手核实
     * 1、卖家未修改金额，直接【确认退款】后即为此状态
     * 2、买手【核实退款】处理此状态的任务
     */
    WAIT_BUYER_CONFIRM_SYS_REFUND("商家确认退款，等待买手核实退款金额"),
    
    /**
     * 【商家、买手】商家选择平台退款后，买手发现金额有误，驳回此次退款
     * 1、买手【驳回】后为此状态
     * 2、卖家【买手驳回订单列表】处理此状态任务
     */
    BUYER_REJECT_SYS_REFUND("买手驳回退款"),
    
    /**
     * 【买手、平台】买手核实金额无误，确认退款
     * 1、买手【核实并确认退款】后为此状态
     * 2、平台【定时检查】处理此状态任务
     */
    WAIT_SYS_REFUND("等待返佣"),
    
    //*------------商家任务、买手任务公用状态-----------------
    /**
     * 【买手、商家】买手核实退款，商家撤销任务
     * 1、买手【核实退款】后即为此状态，若此为最后一个子任务，则父任务也为此状态
     * 2、卖家【撤销未接单任务】后即为此状态
     */
    FINISHED("已完成", true),
    
    /**
     * 【买手、商家、平台】买手、平台撤销未支付任务，商家撤销未接单任务（0个任务接单时）
     */
    CANCLED("已取消", true),
    
    /**
     * 任务被系统冻结
     */
    LOCKED_BY_SYS("已被系统冻结"),
    
  //*------------以下为买手接手做任务的任务步骤的状态----------------
    ITEM_COMPARE("挑选商品"), 
    VIEW_AND_INQUIRY("询盘"),
    ORDER_AND_PAY("下单支付"),
    CONFIRM_AND_COMMENT("买家收货，给出评价"),
    
    /*
     * 任务不存在以下状态
     * 作为买家任务进展列表筛选条件  状态
     * title中是包含的筛选条件
     */
    _RECIEVED("RECIEVED, WAIT_PAY"),
    _WAIT_SEND_GOODS("WAIT_SEND_GOODS, EXPRESS_PRINT, WAIT_EXPRESS_PRINT"),
    _FINISHED("UNFREZZ_PLEGE, FINISHED");
    //@formatter:on

	public String title;
	/**
	 * 是否【商家】任务的状态：
	 * 1、用于在卖家“我的任务”列表标记“状态”筛选条件
	 * 2、用于在管理后台“任务管理”列表标记筛选条件
	 */
	public boolean seller;

	private TaskStatus(String title) {
		this.title = title;
	}
	private TaskStatus(String title, boolean seller) {
	    this.title = title; 
	    this.seller = seller;
	}
	
	/**
	 * 
	 * 取出卖家任务相关的状态（用于查询条件）.
	 *
	 * @return
	 * @since  v1.4
	 * @author youblade
	 * @created 2014年11月22日 上午11:14:37
	 */
	public static List<TaskStatus> listForSellerTask(){
	    List<TaskStatus> list = Lists.newArrayListWithExpectedSize(10);
	    for (TaskStatus s : TaskStatus.values()) {
            if(s.seller){
                list.add(s);
            }
        }
	    return list;
	}
	
	/**
	 * 处于审核流程的任务状态：待审核、审核中、审核未通过
	 */
    public static final Set<TaskStatus> EXAMINE_STATUS_LIST = Sets.newHashSet(TaskStatus.WAIT_EXAMINE,
        TaskStatus.EXAMINING, TaskStatus.NOT_PASS);
    
    /**
     * 卖家任务状态列表：待编辑，等待支付，待审核，审核中，审核不通过，已发布，待发布，已取消，已完成
     */
    public static final EnumSet<TaskStatus> SELLER_STATUS_LIST = EnumSet.of(TaskStatus.WAIT_EDIT, TaskStatus.WAITING_PAY, TaskStatus.WAIT_EXAMINE, TaskStatus.EXAMINING,
    		TaskStatus.NOT_PASS,TaskStatus.PUBLISHED,TaskStatus.WAIT_PUBLISH,TaskStatus.CANCLED,TaskStatus.FINISHED);    
    
    /**
     * 后台管理-买手任务列表：
     */
    //@formatter:off
    public static final List<TaskStatus> ADMIN_BUYER_TASK_STATUS = Lists.newArrayList(
        TaskStatus.RECIEVED,TaskStatus.WAIT_PAY,
        TaskStatus.WAIT_SEND_GOODS,
        TaskStatus.WAIT_EXPRESS_PRINT,TaskStatus.EXPRESS_PRINT,
        TaskStatus.WAIT_CONFIRM,
        TaskStatus.WAIT_REFUND,TaskStatus.REFUNDING,
        
        TaskStatus.WAIT_SELLER_CONFIRM_SYS_REFUND,
        TaskStatus.WAIT_BUYER_CONFIRM_EDITED_SYS_REFUND,
        TaskStatus.WAIT_BUYER_CONFIRM_SYS_REFUND,
        TaskStatus.BUYER_REJECT_SYS_REFUND,
        TaskStatus.WAIT_SYS_REFUND,
        
        TaskStatus.FINISHED,
        TaskStatus.CANCLED);
}
