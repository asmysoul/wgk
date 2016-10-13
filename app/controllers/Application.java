package controllers;

import models.User.UserType;
import models.mappers.BuyerTaskMapper;
import models.mappers.TaskMapper;
import models.mappers.UserMapper;

import org.apache.ibatis.session.SqlSession;
import org.joda.time.LocalDate;

import vos.TaskSearchVo;
import vos.UserSearchVo;

import com.aton.base.BaseController;
import com.aton.config.BizConstants;
import com.aton.config.CacheType;
import com.aton.db.SessionFactory;
import com.aton.util.CacheUtil;

/**
 * 
 * 处理公开页面（不需要登录即可访问）.
 * 
 * @author youblade
 * @since  0.1
 * @created 2014年7月15日 上午10:29:54
 */
public class Application extends BaseController {

    /**
     * 
     * 进入注册页面.<br>
     * 
     * 注册分两步：填写注册信息，发送验证邮件->用户验证邮箱，激活账号
     * 注册页面
     *
     * @since  0.1
     * @author youblade
     * @created 2014年7月12日 下午5:59:33
     */
    public static void regist(Long id) {
        // 设置注册邀请人ID
        if (id != null) {
            renderArgs.put("inviteUserId", id);
            session.put("inviteUserId", id);
        }
        
        renderArgs.put(UserAuthentication.FIELD_USER_ID, flash.get(UserAuthentication.FIELD_USER_ID));
        renderArgs.put(UserAuthentication.FIELD_EMAIL, flash.get(UserAuthentication.FIELD_EMAIL));
        render();
    }
    
    /**
     * 
     * TODO 进入登录页面.
     *
     * @since  0.1
     * @author youblade
     * @created 2014年7月12日 下午5:20:43
     */
    public static void login() {
        renderArgs.put(BizConstants.MSG, flash.get(BizConstants.MSG));
        render();
    }
    
	/**
	 * 
	 * 找回密码.
	 * 
	 * @since 0.1
	 * @author youblade
	 * @created 2014年7月14日 下午6:35:58
	 */
	public static void findPass() {
		// 设置
		renderArgs.put(BizConstants.MSG, flash.get(BizConstants.MSG));
		renderArgs.put(UserAuthentication.FIELD_USER_ID, flash.get(UserAuthentication.FIELD_USER_ID));
		renderArgs.put(UserAuthentication.FIELD_EMAIL, flash.get(UserAuthentication.FIELD_EMAIL));
		render();
	}
    
    /**
     * 
     * TODO 首页.
     *
     * @since  0.1
     * @author youblade
     * @created 2014年7月12日 下午6:00:19
     */
    public static void index() {
        render();
    }
    
    static class IndexDataVo{
        public int totalOrderNum;
        public int totalBuyerNum;
        public long totalRewardIngot;
        public int todayTaskTakenCount;
        public int yesterdayTaskTakenCount;
    }
    
    enum IndexDataType{
        order,
        buyer,
        reward,
        todayTaskNum,
        yesterdayTasNum
    }
    
    /**
     * 
     * 获取首页数据.
     *
     * @since  0.1
     * @author youblade
     * @created 2014年10月15日 下午3:10:25
     */
    public static void fetIndexData() {
        
        String key = CacheType.INDEX_DATA.getKey();
        IndexDataVo vo = CacheUtil.get(key, IndexDataVo.class);
        if (vo != null) {
            renderJson(vo);
        }
        
        vo = new IndexDataVo();
        SqlSession ss = SessionFactory.getSqlSession();
        try {
            // 累计订单数
            vo.totalOrderNum = ss.getMapper(TaskMapper.class).sumTotalOrderNum();
            // 累计注册买手数
            vo.totalBuyerNum = ss.getMapper(UserMapper.class).count(UserSearchVo.newInstance().type(UserType.BUYER));
            // 累计发放佣金
            BuyerTaskMapper mapper = ss.getMapper(BuyerTaskMapper.class);
            vo.totalRewardIngot = mapper.sumRewardIngot();

            // 今日买手接单总数
            TaskSearchVo svo = TaskSearchVo.newInstance().date(LocalDate.now());
            vo.todayTaskTakenCount = mapper.count(svo);
            // 昨日买手接单总数
            vo.yesterdayTaskTakenCount = mapper.count(svo.date(LocalDate.now().minusDays(1)));
        } catch (Exception e) {
            // TODO: handle exception
        } finally {
            ss.close();
        }
        renderJson(vo);
    }
}