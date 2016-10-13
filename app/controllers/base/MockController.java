package controllers.base;

import java.util.HashMap;
import java.util.Map;

import models.User;
import models.User.UserStatus;
import models.User.UserType;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import play.data.validation.Min;
import play.data.validation.Required;
import play.mvc.With;

import com.aton.base.BaseController;
import com.aton.base.secure.Secure;
import com.aton.config.AppMode;
import com.aton.config.AppMode.Mode;
import com.aton.config.BizConstants;
import com.aton.config.CacheType;
import com.aton.config.ReturnCode;
import com.aton.util.CacheUtil;
import com.aton.util.MixHelper;

/**
 * 
 * 生成Mock数据：用于开发测试、线上模拟用户登录等.
 * 
 * @author youblade
 * @since v0.1
 */
@With(Secure.class)
public class MockController extends BaseController {

    private static final Logger log = LoggerFactory.getLogger(MockController.class);

    /**
     * 
     * 为特定用户生成认证码（模拟用户登录）
     * 
     * @param nick
     * @since v0.1
     * @author youblade
     */
    public static void getAuthcode(String nick,UserType type) {
        log.info("Input nick={}", nick);

        User u = null;
        
        // 获取指定的登录用户
        if (StringUtils.isNotBlank(nick)) {
            u = User.findByNick(nick);
            if (u == null) {
                renderFailedJson(ReturnCode.INNER_ERROR, "该登录账号不存在");
            }
            u = u.findByIdWichCache(u.id);
            if (u == null) {
                renderFailedJson(ReturnCode.INNER_ERROR, "该登录账号不存在");
            }
        }

        // 非生产环境下，new一个mock用户
        if (u == null && AppMode.get().mode != Mode.ONLINE) {
            u = new User();
            u.id = 0;
            u.nick = "开发测试用户";
            u.status = UserStatus.VALID;
            u.type = type;
        }

        // 生成mock user的authcode并写入cookie
        session.put(Secure.FIELD_AUTH, u.id);
        CacheUtil.setJson(CacheType.USER_INFO.getKey(u.id), u, "1d");
        
        log.info("Mock login for user={}", u.nick);

        // 线上正式运行时仅输出部分字段
        Map<String, Object> map = new HashMap<String, Object>();
        if(AppMode.get().isOnline()){
            map.put("id", u.id);
            map.put("nick", u.nick);
            map.put("type", u.type.title);
            map.put("status", u.status.title);
            renderJson(map);
        }
        
        map.put("sid", session.getId());
        map.put("user", u);
        renderJson(map);
    }
    
    /**
     * 
     * 模拟【财付通】验证通知ID接口返回的XML.
     *
     * @since  0.1
     * @author youblade
     * @created 2014年9月12日 上午10:52:44
     */
    public static void simpleVerifyNotifyId() {
        MixHelper.print("------mock verify notify-----");
        //@formatter:off
        String xml = 
            "<?xml version=\"1.0\" encoding=\"gb2312\" ?>"+
                "<root>"+
                "   <retcode>0</retcode>"+
                "   <retmsg></retmsg>"+
                "   <partner>1900000109</partner>"+
                "　　<status>0</status>"+
                "　　<sign>8DB4A013A8B515349C307F1E448CE836</sign>"+
                "</root>";
        renderXml(xml);
    }
    
    /**
     * 
     * 设置买手当前正在做的任务.
     *
     * @since  0.1
     * @author youblade
     * @created 2014年10月11日 下午6:09:18
     */
    public static void setPerformingBuyerTask(@Required @Min(1) long id){
        handleWrongInput(false);
        session.put(BizConstants.BUYER_TASK_ID, id);
        renderSuccessJson();
    }
}
