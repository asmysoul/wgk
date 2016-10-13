package jobs;

import java.util.List;

import models.User;
import models.User.UserStatus;
import models.mappers.UserMapper;

import org.apache.ibatis.session.SqlSession;
import org.joda.time.DateTime;
import org.joda.time.LocalDate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import play.cache.Cache;
import play.jobs.On;
import vos.UserSearchVo;

import com.aton.config.CacheType;
import com.aton.db.SessionFactory;
import com.aton.job.BaseJob;

/**
 * 
 * 会员资格检查：是否过期.
 * 
 * @author youblade
 * @since  0.1
 * @created 2014年9月19日 上午12:48:14
 */
@On("0 0 0 * * ?")
public class MembershipCheck extends BaseJob {

    private static final Logger log = LoggerFactory.getLogger(MembershipCheck.class);

    /**
     * 
     * 直接将过期的用户状态置为过期
     * 
     * @see play.jobs.Job#doJob()
     * @since  0.1
     * @author youblade
     * @created 2014年9月19日 上午1:01:59
     */
    public void doJob() throws Exception {
        DateTime now = DateTime.now();
        SqlSession ss = SessionFactory.getSqlSession();
        try {
            UserMapper mapper = ss.getMapper(UserMapper.class);
        	List<User> users = mapper.selectForUpdateUserStatus(now.toDate(), now.toLocalDate().toDate());
        	mapper.updateOverdue(now.toDate(), now.toLocalDate().toDate());
        	//更新用户缓存（会员过期）
            for(User user:users) {
            	Cache.safeDelete(CacheType.USER_INFO.getKey(user.id));
            }
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        } finally {
            ss.close();
        }
    }

}
