package controllers;

import models.Notice;
import models.User;
import models.User.UserType;
import play.data.validation.Min;
import play.data.validation.Required;
import play.mvc.With;
import vos.NoticeSearchVo;
import vos.Page;

import com.aton.base.BaseController;
import com.aton.base.secure.Secure;
import com.google.common.collect.Lists;

@With(Secure.class)
public class NoticeCenter extends BaseController {

    /**
     * 
     * TODO 首页->公告列表页面.
     *
     * @since  0.1
     * @author youblade
     * @created 2014年10月14日 下午3:59:21
     */
    public static void list() {
        render();
    }
    
    /**
     * 
     * 查看公告详情.
     *
     * @since  0.1
     * @author youblade
     * @created 2014年10月14日 下午3:59:27
     */
    public static void view(@Required @Min(1) long id) {
        handleWrongInput(false);

        Notice notice = Notice.findById(id);
        // 检查用户是否有权限查看，管理员访问时不进行检查
        if (!request.url.startsWith("/admin") && notice.role != UserType.ALL && notice.role != getCurrentUser().type) {
            notFound();
        }

        render(notice);
    }
    
    /**
     * 
     * 公告 列表页面->分页获取列表数据.
     *
     * @param s
     * @since  0.1
     * @author youblade
     * @created 2014年10月14日 下午3:59:35
     */
    public static void listNotices(@Required NoticeSearchVo s) {
        handleWrongInput(false);

        s.pageSize = 20;
        User user = getCurrentUser();
        s.roles = Lists.newArrayList(UserType.ALL, user.type);
        //用户页面只能看到允许显示的公告
        s.isDisplay = true;

        Page<Notice> p = Notice.findByPage(s);
        renderPageJson(p.items, p.totalCount);
    }
}
