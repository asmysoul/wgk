package models;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.persistence.Transient;

import models.User.UserType;
import models.mappers.BuyerAccountMapper;
import models.mappers.BuyerAccountMapper2;
import models.mappers.BuyerTaskMapper;
import models.mappers.BuyerTaskMapper2;

import org.apache.commons.lang.Validate;
import org.apache.ibatis.session.SqlSession;
import org.joda.time.DateTime;
import org.joda.time.LocalDate;

import vos.BuyerAccountSearchVo;
import vos.BuyerAccountSearchVo.ExamineStatus;
import vos.BuyerAccountSearchVo2;
import vos.BuyerAccountSearchVo2.ExamineStatus2;
import vos.BuyerTaskSearchVo;
import vos.Memo;
import vos.Page;

import com.aton.config.ReturnCode;
import com.aton.db.SessionFactory;
import com.aton.util.JsonUtil;
import com.aton.util.StringUtils;

import domain.TaskStats;
import enums.Platform;
import enums.Platform2;

/**
 * 
 * 买手在各电商平台的用户账号（作为买家身份的账号）.
 * 
 * @author youblade
 * @since  0.1
 * @created 2014年8月1日 上午10:32:15
 */
public class BuyerAccount2 {
    
    public static final String TABLE_NAME = "buyer_account2";
    
    public long id;
    public long userId;
    public String nick;
    public Platform2 platform;
    public String memo;
    
    /**
     * 收货人
     */
    public String consignee;
    public String address;
    public String state;
    public String city;
    public String region;
    public String mobile;
    /**
     * 排序数字
     */
    public long orderNumber;
    /**
     * 审核状态
     */
    public ExamineStatus2 status;
    
    /**
     * 今日已接手的任务数
     */
    @Transient
    public int receivedTaskCount;

    public Date createTime;
    public Date modifyTime;
    
    /**是否有任务未完成*/
    public boolean hasTaskExecuting;
    
    /**是否已接手任务*/
    public boolean hasTask;
    
    /**展示时用*/
    @Transient
    public String userNick;
    
    /**
     * 审核不通过的原因
     */
    @Transient
    public List<Memo> memos;
    /*总接单数**/
    @Transient
    public int totalTakenNum;
    @Transient
    public int weekTakenNum;
    @Transient
    public int mouthTakenNum;
    
    /**
     * 
     * TODO 获取买号.
     *
     * @param vo
     * @return
     * @since  0.1
     * @author youblade
     * @created 2014年8月1日 上午11:49:01
     */
    public static List<BuyerAccount2> findList(BuyerAccountSearchVo2 vo) {
        SqlSession ss = SessionFactory.getSqlSession();
        try{
        	BuyerAccountMapper2 mapper = ss.getMapper(BuyerAccountMapper2.class);
        	BuyerTaskMapper2 taskMapper = ss.getMapper(BuyerTaskMapper2.class);
        	List<BuyerAccount2> list = mapper.selectByUserIdAndPlatform(vo.userId, vo.platform);
        	
        	BuyerTaskSearchVo taskVo = new BuyerTaskSearchVo();
        	taskVo.buyerId = vo.userId;
        	for(BuyerAccount2 ba: list){
        	    
        	    // 设置“审核不通过”的原因
        	    if(StringUtils.isNotBlank(ba.memo)){
        	        ba.memos = JsonUtil.toList(ba.memo, Memo.class);
        	    }
        	    
        	    // 设置标记“是否存在未完成的任务”，用于前端判断是否可修改
        		taskVo.accountId = ba.id;
        		if(taskMapper.countExecute(taskVo) != null){
        			ba.hasTaskExecuting = true;
        		}else{
        			ba.hasTaskExecuting = false;
        		}
        	}
        	return list;
        }finally{
        	ss.close();
        }
    }

    /**
     * 
     * TODO 获取用于接手任务的买号.
     *
     * @param userId
     * @return
     * @since  0.1
     * @author youblade
     * @created 2014年8月1日 下午12:07:33
     */
    public static List<BuyerAccount2>  findForTakingTask(BuyerAccountSearchVo2 vo) {
    	System.out.println("--------------------------");
    	System.out.println("--------------------------");
    	System.out.println("--------------------------");
    	System.out.println("--------------------------");
    	System.out.println("--------------------------");
    	System.out.println("--------------------------");
        SqlSession ss = SessionFactory.getSqlSession();
        try{
            BuyerAccountMapper2 mapper = ss.getMapper(BuyerAccountMapper2.class);
            
            //TODO 过滤出符合条件的买号
            return mapper.selectBySearch(vo);
        }finally{
            ss.close();
        }
    }
    
	/**
	 * 
	 * 按页获取买号列表
	 * 
	 * @param vo
	 * @return
	 * @since v0.1
	 * @author moloch
	 * @created 2014-8-12 下午5:21:28
	 */
	public static Page<BuyerAccount2> findForPage(BuyerAccountSearchVo2 vo) {
		SqlSession ss = SessionFactory.getSqlSession();
		try {
			BuyerAccountMapper2 baMapper = ss.getMapper(BuyerAccountMapper2.class);
			int count = baMapper.count(vo);
			
			
			
			
			if (count <= 0) {
				return Page.EMPTY;
			}
			Page<BuyerAccount2> page = Page.newInstance(vo.pageNo, vo.pageSize, count);
			page.items = baMapper.selectBySearch(vo);
			for (BuyerAccount2 ba : page.items) {
				if(StringUtils.isNotBlank(ba.memo)){
        	        ba.memos = JsonUtil.toList(ba.memo, Memo.class);
        	    }
				TaskStats taskStats = TaskStats.findForBuyerUntilNow(ba.id);
				ba.weekTakenNum=taskStats.weekCount;
				ba.mouthTakenNum=taskStats.monthCount;
				ba.totalTakenNum=BuyerTask2.findByBuyerAccount(ba.id);
			}
			return page;
		} finally {
			ss.close();
		}
	}

    /**
     * 
     * 保存买号.
     *
     * @since  0.1
     * @author youblade
     * @created 2014年8月1日 下午1:06:46
     */
    public void save() {
        SqlSession ss = SessionFactory.getSqlSession();
        try {
            BuyerAccountMapper2 mapper = ss.getMapper(BuyerAccountMapper2.class);
            this.modifyTime = DateTime.now().toDate();
            if (this.id > 0) {
                // 将无关信息过滤为null
                this.nick = StringUtils.trimToNull(this.nick);
                this.mobile = StringUtils.trimToNull(this.mobile);
                this.consignee = StringUtils.trimToNull(this.consignee);
                this.state = StringUtils.trimToNull(this.state);
                this.city = StringUtils.trimToNull(this.city);
                // 三级行政区域有可能为空，将非空的修改为空值时不能将其设置为null，否则无法构造SQL条件
                this.region = StringUtils.trimToEmpty(this.region);
                this.address = StringUtils.trimToNull(this.address);
                mapper.updateById(this);
                return;
            }
            
            this.createTime = this.modifyTime;
            mapper.insert(this);
        } finally {
            ss.close();
        }
    }
    
    /**
     * 
     * 更新买号状态：后台管理审核买号.
     *
     * @since  v1.9.9.1
     * @author youblade
     * @created 2015年1月4日 下午3:07:04
     */
    public void updateStatus() {
        Date now = DateTime.now().toDate();

        SqlSession ss = SessionFactory.getSqlSession();
        try {
            BuyerAccountMapper2 mapper = ss.getMapper(BuyerAccountMapper2.class);

            // 保存“审核不通过”时的理由
            if (this.status == ExamineStatus2.NOT_PASS) {
                Validate.notNull(this.memo);
                
                // 构造memo
                Memo memo = new Memo();
                memo.content = this.memo;
                memo.dateTime = now;

                // 取出原来memo信息
                BuyerAccount2 bacount = mapper.selectById(id);

                // 添加拒绝理由记录
                List<Memo> memos = StringUtils.isBlank(bacount.memo) ? new ArrayList<Memo>() : JsonUtil.toList(
                    bacount.memo, Memo.class);
                memos.add(memo);
                this.memo = JsonUtil.toJson(memos);
            }

            this.modifyTime = now;
            mapper.updateById(this);
        } finally {
            ss.close();
        }
    }
    
    /**
     * 
     * 根据id取买号数据
     *
     * @param id
     * @return
     * @since  v0.1
     * @author moloch
     * @created 2014-8-15 下午4:25:29
     */
    public static BuyerAccount2 findById(Long id){
       	SqlSession ss = SessionFactory.getSqlSession();
        try{
        	BuyerAccountMapper2 mapper = ss.getMapper(BuyerAccountMapper2.class);
        	return mapper.selectById(id);
        }finally{
        	ss.close();
        }
    }

    /**
     * 
     * 检查买号是否属于某个买手用户.
     *
     * @param user
     * @return
     * @since  0.1
     * @author youblade
     * @created 2014年8月23日 上午10:53:09
     */
    public boolean belongTo(User user) {
        if (user == null || user.type != UserType.BUYER) {
            return false;
        }
        return user.id == this.userId;
    }
    
    /**
     * 
     * 校验买号是否有效（已审核、属于当前登录用户）.
     *
     * @param account
     * @param loginUser
     * @return
     * @since  v2.0
     * @author youblade
     * @created 2015年1月12日 下午3:55:36
     */
    public static boolean validate(BuyerAccount2 account,User loginUser) {
        if (account == null) {
            return false;
        }
        return account.status == ExamineStatus2.EXAMINED && account.belongTo(loginUser);
    }
    
    public String toJson(){
        return JsonUtil.toJson(this);
    }

	/**
	 * 此买号是否归此用户所有
	 *
	 * @param id2
	 * @return
	 * @since  v2.0
	 * @author Mark Xu
	 * @created 2015-4-22 下午2:20:39
	 */
	public static boolean includeAccount(long accountId, long userId) {
		if(userId==0) {
			return false;
		}
		SqlSession ss = SessionFactory.getSqlSession();
		try {
			BuyerAccountMapper2 mapper = ss.getMapper(BuyerAccountMapper2.class);
			if(mapper.findCountByAccountId(accountId).userId==userId) {
				return true;
			}
			return false;
		}finally {
			ss.close();
		}
	}

	/**
	 * 更改排序数
	 *
	 * @param id2
	 * @param orderNumber2
	 * @since  v2.0
	 * @author Mark Xu
	 * @created 2015-4-22 下午3:36:04
	 */
	public static void modifyOrderNumber(long id, long orderNumber) {
		SqlSession ss = SessionFactory.getSqlSession();
		try {
			BuyerAccountMapper2 mapper = ss.getMapper(BuyerAccountMapper2.class);
			mapper.updateOrderNumber(id, orderNumber);
		}finally {
			ss.close();
		}
		
	}
}
