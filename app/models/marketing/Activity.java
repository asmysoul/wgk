package models.marketing;

import java.util.Date;
import java.util.List;

import models.mappers.BuyerAccountMapper;
import models.mappers.marketing.ActivityMapper;

import org.apache.commons.lang.Validate;
import org.apache.ibatis.session.SqlSession;
import org.joda.time.DateTime;

import com.aton.config.CacheType;
import com.aton.db.SessionFactory;
import com.aton.util.CacheUtil;
import com.aton.util.StringUtils;
import com.google.common.base.Strings;

/**
 * 
 * 平台运营活动：邀请注册等.
 * 
 * @author youblade
 * @since v2.0
 * @created 2014年12月17日 下午6:04:43
 */
public class Activity {

    public static final String TABLE_NAME = "activity";

    public Long id;
    public String title;
    
    public InviteRegistActivityVo inviteRegistActivityVo; 
    /**
     * 活动链接：宣传页面、外链论坛公告的url等
     */
    public String pageUrl;
    /**
     * 活动图片
     */
    public String bannerPic;
    
    public enum ActivityBizType {
        INVITE_REG("邀请注册");

        public String title;

        private ActivityBizType(String title) {
            this.title = title;
        }
    }

    public Date startTime;
    public Date endTime;

    public enum ActivityStatus {
        VALID("进行中"), INVALID("已停止");

        public String title;

        private ActivityStatus(String title) {
            this.title = title;
        }
    }

    /**
     * 活动状态：作为代码开关使用，“已停止”的活动不再执行其对应的逻辑
     */
    public ActivityStatus status;

    /**
     * 活动类型标识：与ruleContent字段配合使用，用于定位活动规则的VO
     */
    public ActivityBizType bizType;
    /**
     * 活动规则：各种参数配置等，json格式存储，需要转换为对应的VO进行使用
     */
    public String ruleContent;

    public Date createTime;
    public Date modifyTime;
    
    
    /**
     * TODO Comment
     *
     * @param vo
     * @return
     * @since  v2.0
     * @author zhushan
     * @created 2014-12-20 上午11:39:33
     */
    public static List<Activity> selectList() {
      
        SqlSession ss = SessionFactory.getSqlSession();
        try {
        	ActivityMapper mapper = ss.getMapper(ActivityMapper.class);
           return mapper.selectAll();
        } finally {
            ss.close();
        }
    }
    
	public void save() {
		SqlSession ss = SessionFactory.getSqlSession();
		try {
			ActivityMapper mapper = ss.getMapper(ActivityMapper.class);
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
    
    public  void updateRuleContents(){
    	SqlSession ss = SessionFactory.getSqlSession();
    	try{
    		ActivityMapper mapper = ss.getMapper(ActivityMapper.class);
    		mapper.update(this);
    	}finally{
    		ss.close();
    	}
    }

	public InviteRegistActivityVo getInviteRegistActivityVo() {
		return InviteRegistActivityVo.valueOf(ruleContent);
	}

	/**
	 * 
	 * 查询“邀请好友注册”的活动规则
	 *
	 * @return
	 * @since  v2.0
	 * @author youblade
	 * @created 2014年12月20日 下午4:30:42
	 */
    public static InviteRegistActivityVo findInviteRegistRule() {
        String key = CacheType.ACTIVITY_RULE_.getKey(ActivityBizType.INVITE_REG);
        String ruleContent = CacheUtil.get(key);
        if(StringUtils.isNotBlank(ruleContent)){
            return InviteRegistActivityVo.valueOf(ruleContent);
        }
        
        SqlSession ss = SessionFactory.getSqlSession();
        try {
            ActivityMapper mapper = ss.getMapper(ActivityMapper.class);
            ruleContent = mapper.selectRuleByBizType(ActivityBizType.INVITE_REG);
        } finally {
            ss.close();
        }
        
        if(Strings.isNullOrEmpty(ruleContent)){
            //TODO log
            //throw new RuntimeException("活动规则数据不存在！");
        }
        CacheUtil.set(key, ruleContent, CacheType.ACTIVITY_RULE_.expiredTime);
        return InviteRegistActivityVo.valueOf(ruleContent);
    }

    /**
     * 
     * 该活动是否仍在进行中
     *
     * @return
     * @since  v2.0
     * @author youblade
     * @created 2014年12月20日 下午5:22:50
     */
    public boolean checkIsValid() {
        Validate.notNull(this.status);
        Validate.notNull(this.startTime);
        Validate.notNull(this.endTime);
        
        if (this.status != ActivityStatus.VALID) {
            return false;
        }
        DateTime now = DateTime.now();
        // 【现在】尚未到活动开始时间
        if (now.isBefore(new DateTime(this.startTime))) {
            return false;
        }
        // 【现在】已经过了活动截止时间
        if (now.isAfter(new DateTime(this.endTime))) {
            return false;
        }
        return true;
    }
}
