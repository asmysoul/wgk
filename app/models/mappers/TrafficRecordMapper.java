package models.mappers;

import java.util.List;

import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

import models.TrafficRecord;
import vos.FlowTimesVo;
import vos.TakeTaskCountVo;
import vos.TrafficRecordVo;

public interface TrafficRecordMapper {
	/**
	 * 
	 * TODO 查询所有流量任务
	 * 
	 * @param vo
	 * @return
	 * @since v2.0
	 * @author fufei
	 * @created 2015年1月15日 下午2:19:05
	 */
	List<TrafficRecord> listTraffics(TrafficRecordVo vo);

	/**
	 * 
	 * 查询流量任务个数
	 * 
	 * @param vo
	 * @return
	 * @since v2.0
	 * @author fufei
	 * @created 2015年1月15日 下午2:19:45
	 */
	Integer listCount(TrafficRecordVo vo);

	/**
	 * 
	 * 更新流量任务
	 * 
	 * @param vo
	 * @return
	 * @since v2.0
	 * @author fufei
	 * @created 2015年1月15日 下午5:13:36
	 */
	void modifyTraffic(TrafficRecordVo vo);

	/**
     * 
     * 更新流量任务
     * 
     * @param vo
     * @return
     * @since v2.0
     * @author fufei
     * @created 2015年1月15日 下午5:13:36
     */
    void updateTraffic(TrafficRecordVo vo);
	/**
	 * 
	 * TODO 增加流量任务
	 * 
	 * @param vo
	 * @return
	 * @since v2.0
	 * @author fufei
	 * @created 2015年1月15日 下午5:52:50
	 */
	@Insert("insert into traffic_record(type,kwd,nid,shop_type,times,path1,path2,path3,sleep_time,click_start,click_end,begin_time,end_time,kid,taskId,`status`,return_times,create_time,modify_time,user_id)"
			+ " VALUES(#{type},#{kwd},#{nid},#{shopType},#{times},#{path1},#{path2},#{path3},#{sleepTime},#{clickStart},#{clickEnd},#{beginTime},#{endTime},#{kid},#{taskId},#{status},#{returnTimes},#{createTime},#{modifyTime},#{userId})")
	void insert(TrafficRecordVo vo);
	/**
	 * 
	 * 按状态查询
	 *
	 * @param status
	 * @return
	 * @since  v2.0
	 * @author fufei
	 * @created 2015年1月17日 下午1:56:43
	 */
	
	List<TrafficRecord> findByStatus(@Param("status")String status,@Param("nowTime")String nowTime);

    @Select("select t.id,t.type,t.kwd,t.nid,t.shop_type"
        + " shopType,t.times,t.path1,t.path2,t.path3,t.sleep_time sleepTime,"
        + "t.click_start clickStart,t.click_end clickEnd,t.begin_time beginTime,"
        + "t.end_time endTime,t.kid,t.taskId,t.status,"
        + "t.return_times,u.nick userNick ,t.user_id userId from traffic_record t "
        + "left join user u on u.id=t.user_id where t.status='WAIT' and t.end_time>=#{nowTime} and t.type!='TAOBAOMOBILE' AND kid<=0;")
    List<TrafficRecord> findWaitByStatus(@Param("nowTime") String nowTime);
	/**
	 * 
	 * 获取当前之前的任务
	 *
	 * @param nowTime
	 * @return
	 * @since  v2.0
	 * @author fufei
	 * @created 2015年1月17日 下午5:43:02
	 */
	List<TrafficRecord> searchByStatus(@Param("nowTime")String nowTime);
	@Select("select * from traffic_record")
	List<TrafficRecord> findAll();
	@Update("update traffic_record set begin_time=#{beginTime} , end_time =#{endTime} where id=#{id}")
	void updateTime(TrafficRecord record);

    /**
     * TODO Comment
     *
     * @param ids
     * @since  v2.9
     * @author fufei
     * @created 2015年4月9日 上午10:40:12
     */
	@Update("update traffic_record set status=#{status},return_times=0  where id in(${ids})")
    void batchFlowFinished(@Param("status")String status,@Param("ids")String ids);
	@Select("SELECT * FROM traffic_record WHERE `status`='FINISHED'  GROUP BY end_time")
	List<TrafficRecord> clickCount();
	@Select("SELECT end_time FROM traffic_record WHERE `status`='FINISHED'  GROUP BY end_time order by end_time DESC limit #{startIndex}, #{pageSize}")
	List<FlowTimesVo> totalCount(@Param("startIndex")int startIndex,@Param("pageSize")int pageSize);
	
	@Select("SELECT SUM(return_times) FROM traffic_record WHERE `status`='FINISHED' AND end_time=#{endTime} AND type=#{type}")
    Integer findFlowCount(@Param("endTime")String endTime,@Param("type")String type);
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
	@Select("SELECT * FROM traffic_record WHERE `status`='FINISHED' AND end_time=#{endTime} AND return_times=0")
	List<TrafficRecord> listRefoundFlow(@Param("endTime")String endTime);

	@Select("SELECT * FROM traffic_record WHERE id in(${ids})")
    List<TrafficRecordVo> batchFindFlowFinished(@Param("ids")String ids);
}
