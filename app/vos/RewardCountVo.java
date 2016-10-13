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
 * 管理员查询邀请总量
 * 
 * @author Mark Xu
 * @since  v2.0
 * @created 2015-3-27 下午3:04:53
 */
public class RewardCountVo extends Page{
	public long id;
	/* 邀请人昵称 */
    public String nick;
    /* 邀请用户数量 */
    public long userCount;
    /* 被邀请人完成任务数  */
    public long buyerTaskCount;
    /* 邀请人获得总奖励 */
    public long rewardCount;
    /* 统计类型1 */
    public String countType;
    /* 统计类型2 */
    public String countType1;
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
	public long getUserCount() {
		return userCount;
	}
	public void setUserCount(long userCount) {
		this.userCount = userCount;
	}
	public long getBuyerTaskCount() {
		return buyerTaskCount;
	}
	public void setBuyerTaskCount(long buyerTaskCount) {
		this.buyerTaskCount = buyerTaskCount;
	}
	public long getRewardCount() {
		return rewardCount;
	}
	public void setRewardCount(long rewardCount) {
		this.rewardCount = rewardCount;
	}
   
}
