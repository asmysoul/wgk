package models.marketing;

import java.math.BigDecimal;
import java.util.Date;

import javax.persistence.Transient;

import models.User.UserType;
import models.mappers.marketing.TaskRewardLogMapper;
import models.mappers.marketing.UserInvitedRecordMapper;

import org.apache.ibatis.session.SqlSession;
import org.joda.time.DateTime;

import vos.Page;
import vos.TaskRewardLogVo;
import vos.UserSearchVo;

import com.aton.db.SessionFactory;
import com.aton.util.StringUtils;

/***
 * 
 * 
 * 被邀请用户做任务奖励邀请用户金币表
 * 
 * @author Mark Xu
 * @since  v2.0
 * @created 2015-3-10 上午10:35:52
 */
public class TaskRewardLog {

    public static final String TABLE_NAME = "task_reward_log";

    public Long id;
    /**
     * 邀请人ID
     */
    public Long inviteUserId;
    /**
     * 被邀请人ID
     */
    public Long userId;
    /**
     * 被邀请人昵称
     */
    public String nick;
    
    /**
     * 邀请人昵称
     */
    public String inviteNick;
    
    public UserType type;
    
    public long taskId;
    /**
     * 被邀请人完成任务的时间
     */
    public Date taskFinishedTime;
     /**
      *  邀请人奖励的金币
      */
    public BigDecimal rewardIngot;
    
    /**
     * 备注消息 
     */
    public String memo;
//    
//    /**
//     * 
//     */
//    public static TaskRewardLog findMyInvitedRecord(Long invitedId)
//    {
//    	SqlSession ss = SessionFactory.getSqlSession();
//        try {
//        	UserInvitedRecordMapper mapper = ss.getMapper(UserInvitedRecordMapper.class);
//        	return mapper.selectByField("user_id", invitedId);
//        } finally {
//            ss.close();
//        }
//    }
//    
//    /**
//     * 后台管理--邀请注册管理
//     */
//    public static Page<TaskRewardLog> findByPageForAdmin(UserSearchVo vo) {
//        
//       vo.inviteNick = StringUtils.trimToNull(vo.inviteNick);
//       vo.nick = StringUtils.trimToNull(vo.nick);
//      
//            
//        SqlSession ss = SessionFactory.getSqlSession();
//        try {
//        	UserInvitedRecordMapper mapper = ss.getMapper(UserInvitedRecordMapper.class);
//            int totalCount = mapper.count(vo);
//            if (totalCount <= 0) {
//                return Page.EMPTY;
//            }
//
//            Page<TaskRewardLog> page = Page.newInstance(vo.pageNo, vo.pageSize, totalCount);
//            page.items = mapper.selectList(vo);
//            return page;
//        } finally {
//            ss.close();
//        }
//    }
//    
//    public static Page<TaskRewardLog> findByPage(UserSearchVo vo) {
//            
//        SqlSession ss = SessionFactory.getSqlSession();
//        try {
//        	UserInvitedRecordMapper mapper = ss.getMapper(UserInvitedRecordMapper.class);
//            int totalCount = mapper.count(vo);
//            if (totalCount <= 0) {
//                return Page.EMPTY;
//            }
//
//            Page<TaskRewardLog> page = Page.newInstance(vo.pageNo, vo.pageSize, totalCount);
//            page.items = mapper.selectList(vo);
//            return page;
//        } finally {
//            ss.close();
//        }
//    }
//    
//    
//  	public void save() {
//  		SqlSession ss = SessionFactory.getSqlSession();
//  		try {
//  			UserInvitedRecordMapper mapper = ss.getMapper(UserInvitedRecordMapper.class);
//  			this.modifyTime = DateTime.now().toDate();
//  			this.createTime = this.modifyTime;
//  			if (null == id) {
//  				mapper.insert(this);
//  			} else {
//  				mapper.update(this);
//  			}
//  		} finally {
//  			ss.close();
//  		}
//  	}
//
//    public void updateRecordForMemberOpen(long fee) {
//        SqlSession ss = SessionFactory.getSqlSession();
//        try {
//            UserInvitedRecordMapper mapper = ss.getMapper(UserInvitedRecordMapper.class);
//            Date now = DateTime.now().toDate();
//            this.memberOpenTime = now;
//            this.modifyTime = now;
//            this.rewardIngot = fee;
//            mapper.update(this);
//        } finally {
//            ss.close();
//        }
//        
//    }

	/**
	 * 插入任务邀请奖励记录
	 *
	 * @param rewardLog
	 * @since  v2.0
	 * @author Mark Xu
	 * @created 2015-3-9 下午5:21:22
	 */
	public void insert(SqlSession ss, TaskRewardLog rewardLog) {
		TaskRewardLogMapper mapper = ss.getMapper(TaskRewardLogMapper.class);
		mapper.insert(rewardLog);
	}

	/**
	 * 查询任务邀请奖励记录
	 *
	 * @param vo
	 * @return
	 * @since  v2.0
	 * @author Mark Xu
	 * @created 2015-3-10 下午5:06:27
	 */
	public static Page<TaskRewardLog> findByPage(TaskRewardLogVo vo) {
		SqlSession ss = SessionFactory.getSqlSession();
		try {
			TaskRewardLogMapper mapper = ss.getMapper(TaskRewardLogMapper.class);
			int totalCount = mapper.count(vo);
			if(totalCount <= 0) {
				return Page.EMPTY;
			}
			
			Page<TaskRewardLog> page= Page.newInstance(vo.pageNo, vo.pageSize, totalCount);
			page.items = mapper.selectList(vo);
			return page;
		}finally {
			ss.close();
		}
	}
	
}
