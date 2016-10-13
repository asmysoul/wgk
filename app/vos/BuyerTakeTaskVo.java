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
public class BuyerTakeTaskVo extends Page{
	public long id;
    public String nick;
    public String name;
    public String logType;
    //接任务时间
    public Date takeTaskTimeStart;
    public Date takeTaskTimeEnd;
    public String buyerNick;
    public String adminName;
    //商家注册时间
    public Date buyerCreateTimeStart;
    public Date buyerCreateTimeEnd;
	/* 排序类型 */
    public String type;
    /* 商家注册时间 */
    public Date createTime;
    /* 是否为导出操作 */
    public boolean isExportOperator;
    
	/* 放单总数 */
    public int cc;
    /* 最后放单时间 */
    public Date maxTime;
    
	public String createTimeStr;
	public String maxTimeStr;
	public String ccStr;
    public Date getCreateTime() {
		return createTime;
	}
	public void setCreateTime(Date createTime) {
		this.createTime = createTime;
	}

	public Date getTakeTaskTimeStart() {
		return takeTaskTimeStart;
	}
	public void setTakeTaskTimeStart(Date takeTaskTimeStart) {
		this.takeTaskTimeStart = takeTaskTimeStart;
	}
	public Date getTakeTaskTimeEnd() {
		return takeTaskTimeEnd;
	}
	public void setTakeTaskTimeEnd(Date takeTaskTimeEnd) {
		this.takeTaskTimeEnd = takeTaskTimeEnd;
	}
	public String getBuyerNick() {
		return buyerNick;
	}
	public void setBuyerNick(String buyerNick) {
		this.buyerNick = buyerNick;
	}
	public Date getBuyerCreateTimeStart() {
		return buyerCreateTimeStart;
	}
	public void setBuyerCreateTimeStart(Date buyerCreateTimeStart) {
		this.buyerCreateTimeStart = buyerCreateTimeStart;
	}
	public Date getBuyerCreateTimeEnd() {
		return buyerCreateTimeEnd;
	}
	public void setBuyerCreateTimeEnd(Date buyerCreateTimeEnd) {
		this.buyerCreateTimeEnd = buyerCreateTimeEnd;
	}
	public boolean isExportOperator() {
		return isExportOperator;
	}
	public void setExportOperator(boolean isExportOperator) {
		this.isExportOperator = isExportOperator;
	}
	public String getCreateTimeStr() {
		return createTimeStr;
	}
	public void setCreateTimeStr(String createTimeStr) {
		this.createTimeStr = createTimeStr;
	}
	public String getMaxTimeStr() {
		return maxTimeStr;
	}
	public void setMaxTimeStr(String maxTimeStr) {
		this.maxTimeStr = maxTimeStr;
	}
	public String getCcStr() {
		return ccStr;
	}
	public void setCcStr(String ccStr) {
		this.ccStr = ccStr;
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
