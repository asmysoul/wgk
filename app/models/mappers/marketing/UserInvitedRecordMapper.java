package models.mappers.marketing;

import java.util.List;

import models.User;
import models.marketing.Activity;
import models.marketing.TaskRewardLog;
import models.marketing.UserInvitedRecord;

import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import vos.InviteStatistics;
import vos.RewardCountVo;
import vos.UserSearchVo;

/**
 * 邀请记录DAO
 * @author zhushan
 * @since  v2.0
 * @created 2014-12-20 上午11:18:54
 */
public interface UserInvitedRecordMapper {
	
	void insert(UserInvitedRecord uir);
    
    void update(UserInvitedRecord uir);

    /**
     * 统计邀请的买手
     * @author 刘志远<yuanzhi-2@163.com>
     * @since 2016年6月26日
     * @param value
     * @return
     */
    @Select("select count(1) buyerCount from " + UserInvitedRecord.TABLE_NAME + " uir left join user u on u.id = uir.user_id where invite_user_id=#{value} and u.type='BUYER'")
    Long inviteBuyerStatistics(@Param("value") Object value);
    
    /**
     * 统计邀请的商家
     * @author 刘志远<yuanzhi-2@163.com>
     * @since 2016年6月26日
     * @param value
     * @return
     */
    @Select("select count(1) buyerCount from " + UserInvitedRecord.TABLE_NAME + " uir left join user u on u.id = uir.user_id where invite_user_id=#{value} and u.type='SELLER'")
    Long inviteSellerStatistics(@Param("value") Object value);
    
    /**
     * 统计返款
     * @author 刘志远<yuanzhi-2@163.com>
     * @since 2016年6月26日
     * @param value
     * @return
     */
    @Select("select sum(reward_ingot) from " + UserInvitedRecord.TABLE_NAME + " where invite_user_id=#{value}")
    Long rewardStatistics(@Param("value") Object value);
    
	/**
	 * 查询注册邀请活动记录
	 * @param field 条件名字
	 * @param value	条件值
	 * @return 活动
	 * @since  v2.0
	 * @author zhushan
	 * @created 2014-12-20 上午11:25:12
	 */
    @Select("SELECT * from  " + UserInvitedRecord.TABLE_NAME + " where ${field}=#{value}")
    UserInvitedRecord selectByField(@Param("field") String field, @Param("value") Object value);

    
    
    
    int count(UserSearchVo vo);
    List<UserInvitedRecord> selectList(UserSearchVo vo);
    @Select("SELECT * from " + UserInvitedRecord.TABLE_NAME + " where user_id = #{userId}")
    UserInvitedRecord selectByInvitedUserId(@Param("userId") long userId);

	
    @Select("SELECT count(*) FROM " +User.TABLE_NAME)
	int findRewardCount(RewardCountVo vo);

	/**
	 * 邀请奖励列表
	 *
	 * @param vo
	 * @return
	 * @since  v2.0
	 * @author Mark Xu
	 * @created 2015-3-27 下午3:43:31
	 */
    @Select("SELECT u.nick nick, u1.userCount userCount, u2.buyerTaskCount buyerTaskCount, u2.rewardCount rewardCount FROM " +User.TABLE_NAME + 
    		" u LEFT JOIN (SELECT uu.invite_user_id, COUNT(uu.invite_user_id) AS userCount FROM " +UserInvitedRecord.TABLE_NAME + 
    		" uu GROUP BY invite_user_id) u1 ON u.id = u1.invite_user_id LEFT JOIN (SELECT uuu.invite_user_id, COUNT(uuu.invite_user_id) AS buyerTaskCount, SUM(uuu.reward_ingot) AS rewardCount FROM "
    		+ TaskRewardLog.TABLE_NAME + " AS uuu GROUP BY invite_user_id) u2 ON u.id = u2.invite_user_id ORDER BY u1.userCount DESC, u2.rewardCount DESC limit #{startIndex}, #{pageSize}")
	List<User> findRewardCountByVo(RewardCountVo vo);
    
    /**
     * 邀请奖励列表
     *
     * @param vo
     * @return
     * @since  v2.0
     * @author Mark Xu
     * @created 2015-3-27 下午3:43:31
     */
    @Select("SELECT u.nick nick, u1.userCount userCount, u2.buyerTaskCount buyerTaskCount, u2.rewardCount rewardCount FROM " +User.TABLE_NAME + 
            " u LEFT JOIN (SELECT uu.invite_user_id, COUNT(uu.invite_user_id) AS userCount FROM " +UserInvitedRecord.TABLE_NAME + 
            " uu GROUP BY invite_user_id) u1 ON u.id = u1.invite_user_id LEFT JOIN (SELECT uuu.invite_user_id, COUNT(uuu.invite_user_id) AS buyerTaskCount, SUM(uuu.reward_ingot) AS rewardCount FROM "
            + TaskRewardLog.TABLE_NAME + " AS uuu GROUP BY invite_user_id) u2 ON u.id = u2.invite_user_id ORDER BY u2.rewardCount DESC, u1.userCount DESC limit #{startIndex}, #{pageSize}")
    List<User> findRewardCountByVoTotal(RewardCountVo vo);
}

