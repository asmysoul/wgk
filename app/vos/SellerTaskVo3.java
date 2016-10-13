package vos;

import java.util.Date;
import java.util.List;

import javax.persistence.Transient;

import models.TaskExamineLog;

import com.aton.util.MixHelper;
import com.aton.util.ReflectionUtil;
import com.google.common.collect.Ordering;

import enums.ExpressType;
import enums.Platform;
import enums.Platform3;
import enums.TaskStatus;

public class SellerTaskVo3{
	public Long id;
    public String taskId;
    public String shopName;
    public TaskStatus status;
    /**
     * 状态的标题文字
     */
    public String statusTitle;
    
    public Platform3 platform;
    public String itemTitle;
    public String itemUrl;
    public String itemPic;
    public String additionalItemTitle;
    public String additionalItemPicUrl;
    public int speedTaskIngot;
    public Boolean speedExamine;
    
    public String name;
    
    /**
     * PC接手的任务数
     */
    public int pcTakenCount;
    
    /**
     * Mobile接手的任务数
     */
    public int mobileTakenCount;
    
    /**
     * 任务总数
     */
    public int totalOrderNum;
    
    /**
     * 是否“平台返款”
     */
    public boolean sysRefund;
    
    /**
     * 进行中的任务数
     */
    public int performingCount;
    /**
     * 待发货的任务数
     */
    public int waitSendGoodsCount;
    /**
     * 待退款的任务数
     */
    public int waitRefundCount;

    /**
     * 未接手任务数
     */
    public int notTakenCount;
    public int finishedCount;
    
    public Date createTime;
    public String sellerNick;
    public Date publishTime;
    public Date examineTime;
    
    public List<TaskExamineLog> examineLogs;
    public TaskExamineLog examineLog;
    public boolean sysExpress;
    public Integer	expressIngot;
    
    /**
     * 快递类型
     */
    public ExpressType expressType;
    
    @Transient
    public String expressTypeStr;
    /**
     * 
     * 取出最新一条审核记录
     *
     * @return
     * @since  v0.1
     * @author moloch
     * @created 2014-9-25 下午1:02:44
     */
	public static TaskExamineLog getLastLog(SellerTaskVo3 vo) {
		// 按照usedNum排序
		final String fieldName = "finishTime";
		Ordering<TaskExamineLog> ordMessage = new Ordering<TaskExamineLog>() {
			public int compare(TaskExamineLog tom1, TaskExamineLog tom2) {
				Object val1 = ReflectionUtil.getFieldValue(tom1, fieldName);
				Object val2 = ReflectionUtil.getFieldValue(tom2, fieldName);
				long d1 = Date.parse(val1.toString());
				long d2 = Date.parse(val2.toString());
				Long offset = d2 - d1;
				if (offset.longValue() > 0) {
					return 1;
				} else {
					return -1;
				}
			}
		};
		vo.examineLogs = ordMessage.sortedCopy(vo.examineLogs);
		if(MixHelper.isEmpty(vo.examineLogs)){
			return null;
		}

		return vo.examineLogs.get(0);
	}
	
    public void displayTaskStatusTitle() {
        if (this.status != null) {
            this.statusTitle = this.status.title;
        }
        if(this.expressType!=null){
            this.expressTypeStr=this.expressType.title;
        }
    }
}
