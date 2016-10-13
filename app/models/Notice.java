package models;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import models.User.UserType;
import models.mappers.NoticeMapper;

import org.apache.ibatis.session.SqlSession;
import org.joda.time.DateTime;

import vos.NoticeSearchVo;
import vos.Page;

import com.aton.db.SessionFactory;

public class Notice {

	public static final String TABLE_NAME = "notice";

	public Long id;
	public NoticeType type;
	public String title;
	public String url;
	public String content;
	public Date createTime;
	public Date modifyTime;
	public UserType role;
	public Date topTime;

	/** 是否显示 */
	public boolean isDisplay;

	/** 排序标示 */
	public Integer sortNum;
	public Long adminId;

	/**
	 * 公告类型
	 * 
	 * @author moloch
	 * @since v0.1
	 * @created 2014-9-26 下午3:57:36
	 */
	public enum NoticeType {
		DEFAULT, COURSE
	}

	/**
	 * 
	 * 
	 * 查看该公告的人的身份，是卖家还是买家
	 * 
	 * @author tr0j4n
	 * @since v0.1.1
	 * @created 2014-9-30 下午8:21:47
	 */
	public enum Role {
		ALL, SELLER, BUYER
	}

	/**
	 * 
	 * 分页根据搜索条件取公告分页数据
	 * 
	 * @param vo
	 * @return
	 * @since v0.1.1
	 * @author tr0j4n
	 * @created 2014-9-30 下午7:43:53
	 */
	public static Page<Notice> findByPage(NoticeSearchVo vo) {
		SqlSession ss = SessionFactory.getSqlSession();
		try {
			NoticeMapper mapper = ss.getMapper(NoticeMapper.class);
			int totalCount = mapper.count(vo);
			if (totalCount <= 0) {
				return Page.EMPTY;
			}
			Page<Notice> page = Page.newInstance(vo.pageNo, vo.pageSize,
					totalCount);
			page.items = mapper.selectList(vo);

			return page;
		} finally {
			ss.close();
		}
	}

	/**
	 * 
	 * 根据id查找特定的公告
	 * 
	 * @param id
	 * @return
	 * @since v0.1.1
	 * @author tr0j4n
	 * @created 2014-9-30 下午7:40:31
	 */
	public static Notice findById(Long id) {
		SqlSession ss = SessionFactory.getSqlSession();
		try {
			NoticeMapper mapper = ss.getMapper(NoticeMapper.class);
			return mapper.selectById(id);
		} finally {
			ss.close();
		}
	}

	/**
	 * 
	 * 更新某个特定的公告内容
	 * 
	 * @param notice
	 * @since v0.1.1
	 * @author tr0j4n
	 * @created 2014-9-30 下午7:58:28
	 */
    public void save() {
        SqlSession ss = SessionFactory.getSqlSession();
        try {
            NoticeMapper mapper = ss.getMapper(NoticeMapper.class);
            this.modifyTime = DateTime.now().toDate();
            if (this.id != null) {
                mapper.updateById(this);
                return;
            }
            this.isDisplay = false;
            this.sortNum = 0;
            this.createTime = this.modifyTime;
            mapper.insert(this);
        } finally {
            ss.close();
        }
    }
	
    /**
     * 
	 * 修改公告是否显示
	 *
	 * @since  v0.1
	 * @author moloch
	 * @created 2014-10-4 下午3:00:35
	 */
	public void updateDisplay(){
		SqlSession ss = SessionFactory.getSqlSession();
		try {
			NoticeMapper mapper = ss.getMapper(NoticeMapper.class);
			this.modifyTime = DateTime.now().toDate();
			mapper.updateById(this);
		} finally {
			ss.close();
		}
	}
	
	/**
	 * 
	 * 置顶公告
	 *
	 * @since  v2.0
	 * @author Mark Xu
	 * @created 2015-1-22 下午5:34:38
	 */
	public void updateTopTime() {
		SqlSession ss = SessionFactory.getSqlSession();
		try{
			NoticeMapper mapper = ss.getMapper(NoticeMapper.class);
			this.isDisplay = true;
			this.modifyTime = DateTime.now().toDate();
			this.topTime = this.modifyTime;
			mapper.updateById(this);
		}finally {
			ss.close();
		}
	}
	
	/**
	 * 
	 * 个人中心公告
	 * 
	 * @return
	 * @since v0.1
	 * @author moloch
	 * @created 2014-10-5 下午2:57:44
	 */
	public static List<Notice> getUserNotice(int num, Role role) {
		SqlSession ss = SessionFactory.getSqlSession();
		try {
			NoticeMapper nMapper = ss.getMapper(NoticeMapper.class);
			return nMapper.selectDisplayList(num,role);
		} finally {
			ss.close();
		}
	}

	/**
	 * 查找置顶公告
	 *
	 * @since  v2.0
	 * @author Mark Xu
	 * @return 
	 * @created 2015-1-23 上午11:42:57
	 */
	public static Notice findTopNotice() {
		SqlSession ss = SessionFactory.getSqlSession();
		try {
			NoticeMapper mapper = ss.getMapper(NoticeMapper.class);
			return mapper.selectTopList();
		}finally {
			ss.close();
		}
	}

	
}
