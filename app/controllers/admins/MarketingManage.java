package controllers.admins;

import java.util.List;

import models.marketing.Activity;
import models.marketing.Activity.ActivityBizType;
import models.marketing.InviteRegistActivityVo;
import models.marketing.TaskRewardLog;
import models.marketing.UserInvitedRecord;
import play.data.validation.Required;
import play.mvc.With;
import vos.Page;
import vos.TaskRewardLogVo;
import vos.UserSearchVo;

import com.aton.base.BaseController;
import com.aton.base.secure.Secure;
import com.aton.util.JsonUtil;
import com.aton.util.StringUtils;

/**
 * 
 * 运营相关的功能：活动管理等.
 * 
 * @author youblade
 * @since v2.0
 * @created 2014年12月20日 上午11:16:25
 */
@With(Secure.class)
public class MarketingManage extends BaseController {

	/**
	 * 
	 * 活动管理->保存活动记录
	 * 
	 * @param a
	 * @since v2.0
	 * @author youblade
	 * @created 2014年12月20日 下午12:23:00
	 */
	public static void saveActivity(@Required Activity a) {
		handleWrongInput(false);
		a.save();
		renderSuccessJson();
	}

	public static void updateRuleContents(@Required InviteRegistActivityVo ruleVo,@Required Long id) {
		handleWrongInput(false);
		Activity a = new Activity();
		a.id=id;
		a.ruleContent=JsonUtil.toJson(ruleVo);
		a.updateRuleContents();
		renderSuccessJson();
	}
	
	/**
	 * 
	 * 活动管理->保存“注册邀请”活动的规则
	 * 
	 * @param type
	 *            :活动记录的业务类型
	 * @param vo
	 *            :活动规则对象
	 * @since v2.0
	 * @author youblade
	 * @created 2014年12月20日 下午2:16:43
	 */
	public static void saveInvateRegActivityRule(
			@Required ActivityBizType type, @Required InviteRegistActivityVo vo) {
		if (vo != null) {
			validation.required(vo.buyerRewardRate);
			validation.required(vo.sellerRewardRate);
		}
		handleWrongInput(false);
		// Activity acitvity = Activity.findByBizType(type);
		renderSuccessJson();
	}

	/**
	 * 
	 * 活动管理->获取记录列表
	 * 
	 * @since v2.0
	 * @author youblade
	 * @created 2014年12月20日 上午11:24:40
	 */
	public static void listActivity() {
		handleWrongInput(false);
		List<Activity> list = Activity.selectList();
		renderPageJson(list, list.size());
	}

	/**
	 * 
	 * 邀请注册管理->获取邀请注册记录列表
	 * 
	 * @param uid
	 * @since v2.0
	 * @author youblade
	 * @created 2014年12月20日 上午11:11:37
	 */
	public static void listInvitedUsers(@Required UserSearchVo vo) {
		handleWrongInput(false);
        vo.nick = StringUtils.trimToNull(vo.nick);
        vo.inviteNick = StringUtils.trimToNull(vo.inviteNick);
		Page<UserInvitedRecord> page = UserInvitedRecord.findByPage(vo);
		renderPageJson(page.items,page.totalCount);
	}
	
	public static void listTaskRewardLog(@Required TaskRewardLogVo vo) {
		handleWrongInput(false);
		vo.userNick = StringUtils.trimToNull(vo.userNick);
		vo.inviteUserNick = StringUtils.trimToNull(vo.inviteUserNick);
		Page<TaskRewardLog> page = TaskRewardLog.findByPage(vo);
		renderPageJson(page.items, page.totalCount);
	}
}
