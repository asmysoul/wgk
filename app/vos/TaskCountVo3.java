package vos;

import enums.Device;
import enums.Platform;
import enums.Platform3;
import enums.TaskStatus;

/**
 * 
 * 封装任务统计信息：主要用于“我的任务”页面中显示待处理任务数目.
 * 
 * @author youblade
 * @since  0.1
 * @created 2014年8月20日 上午11:36:26
 */
public class TaskCountVo3 {

    public TaskStatus status;
    
    public Long buyerAccountId;
    public String buyerAccountNick;
    public int count;
    
    /** 主任务的总单数*/
    public int totalOrderNum;
    
    public Platform3 platform;
    public Device device;
    
	public TaskCountVo3() {
	}

	public TaskCountVo3(Platform3 platform, TaskStatus status) {
		this.platform = platform;
		this.status = status;
	}
}
