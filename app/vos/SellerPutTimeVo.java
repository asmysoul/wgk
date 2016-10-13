package vos;

import java.util.Date;

import models.AdminOperatorLog.LogType;
import models.AdminUser.AdminStatus;
import models.AdminUser.AdminType;
import models.User.UserStatus;
import models.User.UserType;

/**
 * 
 * 
 * 管理员查询商家放单
 * 
 * @author Mark Xu
 * @since  v2.0
 * @created 2015-3-24 下午1:43:49
 */
public class SellerPutTimeVo extends Page{
	public long id;
    public String nick;
    public String name;
    public String logType;
    //接任务时间
    public Date taskTakeTimeStart;
    public Date taskTakeTimeEnd;
    public String sellerNick;
    public String adminName;
    //商家注册时间
    public Date sellerRegTimeStart;
    public Date sellerRegTimeEnd;
	/* 排序类型 */
    public String type;
    /* 商家注册时间 */
    public Date registTime;
    /* 是否为导出操作 */
    public boolean isExportOperator;
    
	/* 放单总数 */
    public int cc;
    /* 最后放单时间 */
    public Date maxTime;
    
	public String registTimeStr;
	public String maxTimeStr;
	public String ccStr;
    public Date getRegistTime() {
		return registTime;
	}
	public void setRegistTime(Date registTime) {
		this.registTime = registTime;
	}
	public Date getTaskTakeTimeStart() {
		return taskTakeTimeStart;
	}
	public Date getSellerRegTimeStart() {
		return sellerRegTimeStart;
	}
	public void setSellerRegTimeStart(Date sellerRegTimeStart) {
		this.sellerRegTimeStart = sellerRegTimeStart;
	}
	public Date getSellerRegTimeEnd() {
		return sellerRegTimeEnd;
	}
	public void setSellerRegTimeEnd(Date sellerRegTimeEnd) {
		this.sellerRegTimeEnd = sellerRegTimeEnd;
	}
	public void setTaskTakeTimeStart(Date taskTakeTimeStart) {
		this.taskTakeTimeStart = taskTakeTimeStart;
	}
	public Date getTaskTakeTimeEnd() {
		return taskTakeTimeEnd;
	}
	public void setTaskTakeTimeEnd(Date taskTakeTimeEnd) {
		this.taskTakeTimeEnd = taskTakeTimeEnd;
	}
	public String getSellerNick() {
		return sellerNick;
	}
	public void setSellerNick(String sellerNick) {
		this.sellerNick = sellerNick;
	}
	public String getAdminName() {
		return adminName;
	}
	public void setAdminName(String adminName) {
		this.adminName = adminName;
	}
    public int getCc() {
		return cc;
	}
	public void setCc(int cc) {
		this.cc = cc;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getLogType() {
		return logType;
	}
	public void setLogType(String logType) {
		this.logType = logType;
	}
	public String getType() {
		return type;
	}
	public void setType(String type) {
		this.type = type;
	}
	public long getId() {
		return id;
	}
	public void setId(long id) {
		this.id = id;
	}
	public String getNick() {
		return nick;
	}
	public void setNick(String nick) {
		this.nick = nick;
	}
	public Date getMaxTime() {
		return maxTime;
	}
	public void setMaxTime(Date maxTime) {
		this.maxTime = maxTime;
	}
    
}
