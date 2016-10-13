package vos;

import enums.Device;
import enums.Platform;
import enums.Platform2;
import enums.TaskStatus;

/**
 * 
 * 封装任务统计信息：主要用于“我的任务”页面中显示待处理任务数目.
 * 
 */
public class TaskCountVo2 {

    public TaskStatus status;
    
    public Long buyerAccountId;
    public String buyerAccountNick;
    public int count;
    
    /** 主任务的总单数*/
    public int totalOrderNum;
    
    public Platform2 platform;
    public Device device;
    
	public TaskCountVo2() {
	}

	public TaskCountVo2(Platform2 platform, TaskStatus status) {
		this.platform = platform;
		this.status = status;
	}
}
