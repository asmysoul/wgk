package models;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import models.FlowJsonClickModel.FlowJsonClicks;
import models.mappers.TrafficRecordMapper;
import models.mappers.UserFlowRecordMapper;
import models.mappers.UserMapper;
import net.sf.ehcache.config.PersistenceConfiguration.Strategy;

import org.apache.ibatis.session.SqlSession;
import org.joda.time.DateTime;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.aton.db.SessionFactory;
import com.aton.util.CollectionUtils;
import com.aton.util.StringUtils;
import com.aton.util.TrafficRecordUtil;
import com.sun.xml.internal.bind.api.AccessorException;

import vos.FlowTimesVo;
import vos.Page;
import vos.TrafficRecordVo;

/**
 * 
 * 
 * 流量任务记录
 * 
 * @author fufei
 * @since v2.0
 * @created 2015年1月15日 上午10:25:35
 */
public class TrafficRecord {
	public static final Logger log = LoggerFactory.getLogger(TrafficRecord.class);
	public long id;
	public String kwd;// 关键词
	public String nid;// 宝贝Id
	public String shopType;
	public ShopType type;

	public enum ShopType {
		TAOBAOPC("淘宝PC"), TAOBAOMOBILE("淘宝移动"), JDPC("京东PC"),TBAD("淘宝直通车");

		public String name;

		private ShopType(String title) {
			this.name = title;
		}
	}

	public Integer times;
	public Integer path1;
	public Integer path2;
	public Integer path3;
	public Integer sleepTime;
	public Integer clickStart;
	public Integer clickEnd;
	public String beginTime;
	public String endTime;
	public long kid;
	public String taskId;
	public String userNick;

	public TrafficStatus status;

	public enum TrafficStatus {
		WAIT("待处理"), PROCESSING("处理中"), FINISHED("已完成");
		public String name;

		private TrafficStatus(String title) {
			this.name = title;
		}
	}

	public Integer returnTimes;
	public Date createTime;
	public Date modifyTime;
	public long userId;

	/**
	 * 
	 * TODO 分页查询所有流量任务
	 * 
	 * @param vo
	 * @return
	 * @since v2.0
	 * @author fufei
	 * @created 2015年1月15日 下午2:19:05
	 */
	public static Page<TrafficRecord> listTrafficRecord(TrafficRecordVo vo) {
		vo.kwd = StringUtils.trimToNull(vo.kwd);
		vo.userNick = StringUtils.trimToNull(vo.userNick);
		vo.status = StringUtils.trimToNull(vo.status);
		vo.beginTime = StringUtils.trimToNull(vo.beginTime);
		vo.endTime = StringUtils.trimToNull(vo.endTime);
		SqlSession ss = SessionFactory.getSqlSession();
		try {
			TrafficRecordMapper mapper = ss.getMapper(TrafficRecordMapper.class);
			int totalCount = mapper.listCount(vo);
			if (totalCount <= 0) {
				return Page.EMPTY;
			}
			List<TrafficRecord> list = mapper.listTraffics(vo);
			
			Page<TrafficRecord> page = Page.newInstance(vo.pageNo, vo.pageSize, totalCount);
			page.items = list;
			return page;

		} finally {
			ss.close();
		}
	}

	/**
	 * 
	 * （保存）更新流量任务
	 * 
	 * @param vo
	 * @since v2.0
	 * @author fufei
	 * @created 2015年1月15日 下午6:12:49
	 */
	public static void modifyTraffic(TrafficRecordVo vo) {
		SqlSession ss = SessionFactory.getSqlSession();
		try {
			TrafficRecordMapper mapper = ss.getMapper(TrafficRecordMapper.class);
			vo.modifyTime = DateTime.now().toDate();
			if (vo.id > 0) {
				TrafficRecordVo recordVo = TrafficRecordVo.newInstance();
				recordVo.id = vo.id;
				if (CollectionUtils.isNotEmpty(mapper.listTraffics(recordVo)))
					mapper.modifyTraffic(vo);// 更新
			} else {
				vo.createTime = DateTime.now().toDate();
				vo.status = TrafficStatus.WAIT.toString();
				vo.returnTimes = 0;
				mapper.insert(vo);
			}
		} finally {
			ss.close();
		}
	}

	/**
	 * 
	 * 按状态查询流量任务
	 * 
	 * @return
	 * @since v2.0
	 * @author fufei
	 * @created 2015年1月17日 下午1:54:45
	 */
	public static List<TrafficRecord> findByStatus(String status,String nowTime) {
		SqlSession ss = SessionFactory.getSqlSession();
		try {
			TrafficRecordMapper mapper = ss.getMapper(TrafficRecordMapper.class);
			return mapper.findByStatus(status,nowTime);
		} finally {
			ss.close();
		}
	}
	
	/**
     * 
     * 按状态查询流量任务
     * 
     * @return
     * @since v2.0
     * @author fufei
     * @created 2015年1月17日 下午1:54:45
     */
    public static List<TrafficRecord> findWaitByStatus(String nowTime) {
        SqlSession ss = SessionFactory.getSqlSession();
        try {
            TrafficRecordMapper mapper = ss.getMapper(TrafficRecordMapper.class);
            return mapper.findWaitByStatus(nowTime);
        } finally {
            ss.close();
        }
    }
    
	
	/**
	 * 
	 * 按状态查询流量任务
	 * 
	 * @return
	 * @since v2.0
	 * @author fufei
	 * @created 2015年1月17日 下午1:54:45
	 */
	public static List<TrafficRecord> searchByStatus(String nowTime) {
		SqlSession ss = SessionFactory.getSqlSession();
		try {
			TrafficRecordMapper mapper = ss.getMapper(TrafficRecordMapper.class);
			return mapper.searchByStatus(nowTime);
		} finally {
			ss.close();
		}
	}
	/**
	 * 
	 * 获取点击次数
	 *
	 * @param trafficRecord
	 * @since  v2.6
	 * @author fufei
	 * @created 2015年2月9日 下午5:12:27
	 */
	public static void getReturnTimes(TrafficRecord trafficRecord) {
		String rest = TrafficRecordUtil.getclickTimes(trafficRecord.kid + "", new SimpleDateFormat("yyyyMMdd").format(new Date()));
		
		if (!"".equals(rest)) {
			FlowJsonClickModel jsonModels = TrafficRecordUtil.formatClickToJSON(rest);
			log.info("result:"+rest);
			if ("success".equals(jsonModels.status)) {
				List<FlowJsonClicks> clicks = jsonModels.data.clicks;
				for (FlowJsonClicks flowJsonClicks : clicks) {
					TrafficRecordVo vo = new TrafficRecordVo();
					vo.id = trafficRecord.id;
					vo.returnTimes = Integer.parseInt(flowJsonClicks.clicks);
					if (trafficRecord.kid == Long.parseLong(flowJsonClicks.kid))
						TrafficRecord.modifyTraffic(vo);
				}
			}
		} else {
			log.info("id为：" + trafficRecord.id + "的任务失败！请重新核对数据,返回数据：" + rest);
		}
	}
	
	public static List<TrafficRecord> getAll() {
		SqlSession ss = SessionFactory.getSqlSession();
		try {
			TrafficRecordMapper mapper = ss.getMapper(TrafficRecordMapper.class);
			return mapper.findAll();
		} finally {
			ss.close();
		}	
	}
	
	public static void updateTime(TrafficRecord record) {
		SqlSession ss = SessionFactory.getSqlSession();
		try {
			TrafficRecordMapper mapper = ss.getMapper(TrafficRecordMapper.class);
			mapper.updateTime(record);
		} finally {
			ss.close();
		}	
	}
	/**
	 * 
	 * 批量完成任务
	 *
	 * @param ids
	 * @since  v2.9
	 * @author fufei
	 * @created 2015年4月9日 上午10:46:55
	 */
	public static void batchFlowFinished(String status,String ids) {
        SqlSession ss = SessionFactory.getSqlSession();
        try {
            TrafficRecordMapper mapper = ss.getMapper(TrafficRecordMapper.class);
            mapper.batchFlowFinished(status,ids);
        } finally {
            ss.close();
        }   
    }
	
	/**
     * 
     * 批量查询任务
     *
     * @param ids
     * @since  v2.9
     * @author fufei
     * @created 2015年4月9日 上午10:46:55
     */
    public static List<TrafficRecordVo> batchFindFlowFinished(String ids) {
        SqlSession ss = SessionFactory.getSqlSession();
        try {
            TrafficRecordMapper mapper = ss.getMapper(TrafficRecordMapper.class);
            return mapper.batchFindFlowFinished(ids);
        } finally {
            ss.close();
        }   
    }
	
	/**
     * 
     * 更新流量任务
     * 
     * @param vo
     * @since v2.0
     * @author fufei
     * @created 2015年1月15日 下午6:12:49
     */
    public static void updateTraffic(TrafficRecordVo vo) {
        SqlSession ss = SessionFactory.getSqlSession();
        try {
            TrafficRecordMapper mapper = ss.getMapper(TrafficRecordMapper.class);
            vo.modifyTime = DateTime.now().toDate();
            TrafficRecordVo recordVo = TrafficRecordVo.newInstance();
            recordVo.id = vo.id;
            if (CollectionUtils.isNotEmpty(mapper.listTraffics(recordVo)))
                mapper.updateTraffic(vo);// 更新
        } finally {
            ss.close();
        }
    }
    
    /**
     * 
     *统计每天完成次数
     * 
     * @param vo
     * @since v2.0
     * @author fufei
     * @created 2015年1月15日 下午6:12:49
     */
    public static Page<FlowTimesVo> totalTrafficCount(FlowTimesVo vo) {
        SqlSession ss = SessionFactory.getSqlSession();
        try {
            TrafficRecordMapper mapper = ss.getMapper(TrafficRecordMapper.class);
            int count=mapper.clickCount().size();
            if (count <= 0) {
                return Page.EMPTY;
            }
            Page<FlowTimesVo> page = Page.newInstance(vo.pageNo, vo.pageSize, count);
            page.items = mapper.totalCount(vo.startIndex,vo.pageSize);
            for (FlowTimesVo flowTimesVo : page.items) {
                Integer mcount=mapper.findFlowCount(flowTimesVo.endTime, "TAOBAOMOBILE");
                Integer pcount=mapper.findFlowCount(flowTimesVo.endTime, "TAOBAOPC");
                flowTimesVo.count=mcount==null?0:mcount;
                flowTimesVo.pcCount=pcount==null?0:pcount;
            }
            return page;
        } finally {
            ss.close();
        }
    }

  /**
   * 
   * 发布流量任务
   *
   * @param vo
   * @since  v3.2
   * @author fufei
   * @created 2015年4月11日 下午3:58:07
   */
    public static void publishTraffic(TrafficRecordVo vo) {
        SqlSession ss = SessionFactory.getSqlSession();
        try {
            UserFlowRecordMapper userFlowRecordMapper=ss.getMapper(UserFlowRecordMapper.class);
            TrafficRecordMapper mapper = ss.getMapper(TrafficRecordMapper.class);
            vo.modifyTime = DateTime.now().toDate();
            vo.createTime = DateTime.now().toDate();
            vo.status = TrafficStatus.WAIT.toString();
            vo.returnTimes = 0;
            vo.path1 = 100;
            vo.path2 = 0;
            vo.path3 = 0;
            vo.sleepTime = 100;
            vo.clickStart = DateTime.now().toDate().getHours();
            vo.clickEnd = 23;
            int totalFlow=vo.times;
            if(StringUtils.contains(vo.type, "MOBILE")){
                totalFlow=vo.times*3;
            }
            UserFlowRecord flowRecord=userFlowRecordMapper.selectLastRecord(vo.userId);
            //流量不足
            if(flowRecord==null||flowRecord.balance<totalFlow){
                throw new RuntimeException("流量不足，发布失败~~");
            }
            mapper.insert(vo);
            String memo="发布流量任务花费"+totalFlow+"个流量";
            flowRecord=UserFlowRecord.newInstance(vo.userId, flowRecord).minus(totalFlow).memo(memo).createTime(DateTime.now().toDate());
            userFlowRecordMapper.insert(flowRecord);
            User.findByIdWichCache(vo.userId).flow(flowRecord.balance).updateCache();
            
        } finally {
            ss.close();
        }
    }
    /**
     * 
     * 判断是否足够
     *
     * @param vo
     * @return
     * @since  v3.4
     * @author fufei
     * @created 2015年5月6日 下午6:56:29
     */
    public static boolean isFullTraffic(TrafficRecordVo vo) {
        SqlSession ss = SessionFactory.getSqlSession();
        try {
            UserFlowRecordMapper userFlowRecordMapper=ss.getMapper(UserFlowRecordMapper.class);
            int totalFlow=vo.times;
            if(StringUtils.contains(vo.type, "MOBILE")){
                totalFlow=vo.times*3;
            }
            UserFlowRecord flowRecord=userFlowRecordMapper.selectLastRecord(vo.userId);
            //流量不足
            if(flowRecord==null||flowRecord.balance<totalFlow){
               return false;
            }
            return true;
        } finally {
            ss.close();
        }
    }
    /**
     * 
     * 查询当天没有点击完成的流量任务
     *
     * @param vo
     * @return
     * @since  v3.2
     * @author fufei
     * @created 2015年4月13日 下午3:25:42
     */
    public static List<TrafficRecord> listRefoundFlow(String endTime) {
        SqlSession ss = SessionFactory.getSqlSession();
        try {
            TrafficRecordMapper mapper = ss.getMapper(TrafficRecordMapper.class);
            return mapper.listRefoundFlow(endTime);
        } finally {
            ss.close();
        } 
    }
	
}
