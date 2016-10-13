package controllers.admins;

import models.Notice;
import play.data.validation.Required;
import play.mvc.With;
import vos.NoticeSearchVo;
import vos.Page;

import com.aton.base.BaseController;
import com.aton.base.secure.Secure;
import com.aton.config.AppMode;
import com.aton.config.AppMode.Mode;
import com.aton.util.QnCloudUtil;
import com.aton.util.QnCloudUtil.QnFileBucket;

@With(Secure.class)
public class NoticeManage extends BaseController {

	/**
	 * 
	 * 查看列表.
	 * 
	 * @param vo
	 * @since 0.1
	 * @author youblade
	 * @created 2014年9月30日 上午10:15:20
	 */
	public static void list(@Required NoticeSearchVo vo) {
		handleWrongInput(false);

		Page<Notice> page = Notice.findByPage(vo);
		renderPageJson(page.items, page.totalCount);
	}

	/**
	 * 
	 * 编辑旧通知.
	 * 
	 * @param id
	 * @since 0.1
	 * @author youblade
	 * @created 2014年9月30日 上午10:15:38
	 */
	public static void edit(Long id) {
		if (id != null) {
			Notice notice = Notice.findById(id);
			renderArgs.put("notice", notice);
		}
		render("Admin/editNotice.html");
	}

	/**
	 * 
	 * 发布、更新通知.
	 * 
	 * @param n
	 * @since 0.1
	 * @author youblade
	 * @created 2014年9月30日 上午10:15:42
	 */
	public static void save(@Required Notice n) {
		//这里要不要判断下比如标题字数什么的？
		handleWrongInput(false);
		n.save();
		render("Admin/notice.html");
	}

	/**
	 * 
	 * 更新公告是否显示
	 * 
	 * @param n
	 * @since v0.1
	 * @author moloch
	 * @created 2014-10-4 下午3:02:53
	 */
	public static void isDisplay(@Required Notice notice) {
		if (notice != null) {
			validation.required(notice.id);
			validation.required(notice.isDisplay);
		}
		handleWrongInput(false);

		notice.updateDisplay();
		renderSuccessJson();
	}

	/**
	 * 
	 * 更新公告是否显示
	 * 
	 * @param n
	 * @since v0.1
	 * @author moloch
	 * @created 2014-10-4 下午3:02:53
	 */
	public static void topShow(@Required Notice notice) {
		if (notice != null) {
			validation.required(notice.id);
		}
		handleWrongInput(false);
		
		notice.updateTopTime();
		renderSuccessJson();
	}
	
	/**
	 * 
     * 获取公告上传图片token
     *
     * @since  v0.1
     * @author moloch
     * @created 2014-10-4 上午11:40:00
     */
    public static void fetchUploadToken(){
        if(AppMode.get().mode == Mode.DEV){
            renderJson(QnCloudUtil.generateUploadToken());
        }else if(AppMode.get().mode == Mode.ONLINE){
            renderJson(QnCloudUtil.generateUploadToken(QnFileBucket.ONLINE_NOTICE_PUBLIC));
        }
    }
}
