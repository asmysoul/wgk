package models.marketing;

import java.text.DecimalFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.persistence.Transient;

import models.User;
import models.User.UserType;
import models.mappers.marketing.UserInvitedRecordMapper;

import org.apache.ibatis.session.SqlSession;
import org.joda.time.DateTime;

import vos.InviteStatistics;
import vos.Page;
import vos.RewardCountVo;
import vos.UserSearchVo;

import com.aton.db.SessionFactory;
import com.aton.util.StringUtils;

/**
 * 
 * 被邀请注册的用户记录.
 * 
 * @author youblade
 * @since  v2.0
 * @created 2014年12月19日 下午3:15:16
 */
public class UserInvitedRecord {

    public static final String TABLE_NAME = "user_invited_record";

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
    
    public String status;
    /**
     * 邀请人昵称
     */
    public String inviteNick;
    
    public UserType type;
    
    /**
     * 被邀请人首次开通会员的时间
     */
    public Date memberOpenTime;
     /**
      *  邀请人奖励的金币
     */
    public Long rewardIngot;
    
    public Date createTime;
    public Date modifyTime;
    
    @Transient
    public String qq;
    /**
     * 被邀请人注册时间
     */
    @Transient
    public Date registTime;
    
    /**
     * 
     */
    public static UserInvitedRecord findMyInvitedRecord(Long invitedId)
    {
    	SqlSession ss = SessionFactory.getSqlSession();
        try {
        	UserInvitedRecordMapper mapper = ss.getMapper(UserInvitedRecordMapper.class);
        	return mapper.selectByField("user_id", invitedId);
        } finally {
            ss.close();
        }
    }
    
    /**
     * 后台管理--邀请注册管理
     */
    public static Page<UserInvitedRecord> findByPageForAdmin(UserSearchVo vo) {
        
       vo.inviteNick = StringUtils.trimToNull(vo.inviteNick);
       vo.nick = StringUtils.trimToNull(vo.nick);
      
            
        SqlSession ss = SessionFactory.getSqlSession();
        try {
        	UserInvitedRecordMapper mapper = ss.getMapper(UserInvitedRecordMapper.class);
            int totalCount = mapper.count(vo);
            if (totalCount <= 0) {
                return Page.EMPTY;
            }

            Page<UserInvitedRecord> page = Page.newInstance(vo.pageNo, vo.pageSize, totalCount);
            page.items = mapper.selectList(vo);
            return page;
        } finally {
            ss.close();
        }
    }
    
    public static Page<UserInvitedRecord> findByPage(UserSearchVo vo) {
            
        SqlSession ss = SessionFactory.getSqlSession();
        try {
        	UserInvitedRecordMapper mapper = ss.getMapper(UserInvitedRecordMapper.class);
            int totalCount = mapper.count(vo);
            if (totalCount <= 0) {
                return Page.EMPTY;
            }

            Page<UserInvitedRecord> page = Page.newInstance(vo.pageNo, vo.pageSize, totalCount);
            page.items = mapper.selectList(vo);
            return page;
        } finally {
            ss.close();
        }
    }
    
    
  	public void save() {
  		SqlSession ss = SessionFactory.getSqlSession();
  		try {
  			UserInvitedRecordMapper mapper = ss.getMapper(UserInvitedRecordMapper.class);
  			this.modifyTime = DateTime.now().toDate();
  			this.createTime = this.modifyTime;
  			if (null == id) {
  				mapper.insert(this);
  			} else {
  				mapper.update(this);
  			}
  		} finally {
  			ss.close();
  		}
  	}

    public void updateRecordForMemberOpen(long fee) {
        SqlSession ss = SessionFactory.getSqlSession();
        try {
            UserInvitedRecordMapper mapper = ss.getMapper(UserInvitedRecordMapper.class);
            Date now = DateTime.now().toDate();
            this.memberOpenTime = now;
            this.modifyTime = now;
            this.rewardIngot = fee;
            mapper.update(this);
        } finally {
            ss.close();
        }
        
    }

	/**
	 * 统计邀请奖励
	 *
	 * @return
	 * @since  v2.0
	 * @author Mark Xu
	 * @created 2015-3-27 下午3:16:25
	 */
	public static Page<User> findRewardCountPage(RewardCountVo vo) {
		SqlSession ss = SessionFactory.getSqlSession();
		try{
			UserInvitedRecordMapper mapper = ss.getMapper(UserInvitedRecordMapper.class);
			int totalCount = mapper.findRewardCount(vo);
			if(totalCount<=0) {
				return Page.EMPTY;
			}
			Page<User> page = Page.newInstance(vo.pageNo, vo.pageSize, totalCount);
			if(vo.countType.equals("userCount")) {
			    page.items = mapper.findRewardCountByVo(vo);
			}else {
			    page.items = mapper.findRewardCountByVoTotal(vo);
			}
			return page;
		}finally {
			ss.close();
		}
	}
	
	/**
	 * 查询邀请统计
	 * @author 刘志远<yuanzhi-2@163.com>
	 * @since 2016年6月26日
	 * @param vo
	 * @return
	 */
	public static Map<String, Object> findInviteStatistics(UserSearchVo vo) {
        Map<String, Object> statistics = new HashMap<String, Object>();
        DecimalFormat df = new DecimalFormat("####.00");
        SqlSession ss = SessionFactory.getSqlSession();
        try {
        	UserInvitedRecordMapper mapper = ss.getMapper(UserInvitedRecordMapper.class);
        	Long buyerCount =  mapper.inviteBuyerStatistics(vo.id);
        	Long sellerCount =  mapper.inviteSellerStatistics(vo.id);
        	Long sum =  mapper.rewardStatistics(vo.id);
        	
        	statistics.put("buyerCount", buyerCount==null?0:buyerCount);
        	statistics.put("sellerCount", sellerCount==null?0:sellerCount);
        	statistics.put("sum", sum==null?"0.00":df.format(sum/100D));
        } finally {
            ss.close();
        }
        return statistics;
    }
    
}
