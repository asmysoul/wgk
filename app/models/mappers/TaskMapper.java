package models.mappers;

import java.util.Date;
import java.util.List;

import models.AdminUser;
import models.BuyerTask;
import models.Task;
import models.User;

import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

import enums.Platform;
import vos.BuyerTakeTaskVo;
import vos.Page;
import vos.SearchSystemCountVo;
import vos.SearchTakenCount;
import vos.SellerPutTimeVo;
import vos.SellerTaskVo;
import vos.TaskCountVo;
import vos.TaskSearchVo;

public interface TaskMapper {

    /**
     * 查询“任务列表”显示所需数据.
     */
    List<Task> selectList(TaskSearchVo vo);
    List<Task> selectAllList(TaskSearchVo vo);
    Integer selectListCount(TaskSearchVo vo);
    
    /**
     * 
     * 查询任务池中待处理的任务.
     *
     * @param vo
     * @return  :t.id,t.last_batch_publish_time,t.publish_timer_interval
     * @since  0.1
     * @author youblade
     * @created 2014年7月31日 下午7:19:52
     */
    List<Task> selectListFromTaskPool(TaskSearchVo vo);
    
    /**
     * 查询单表数据.
     */
    int countSimple(TaskSearchVo vo);
    List<Task> selectSimple(TaskSearchVo vo);

    void insert(Task task);
    
	int count(TaskSearchVo vo);
	
	int countForSeller(TaskSearchVo vo);
	
	int countProcessTaskSeller(TaskSearchVo vo);
	
	List<SellerTaskVo> selectProcessTaskSeller(TaskSearchVo vo);
	/**
	 * 
	 * 卖家“我的任务”-平台审核中的任务.
	 *
	 * @param sellerId
	 * @return
	 * @since  0.1
	 * @author youblade
	 * @created 2014年8月20日 下午3:24:14
	 */
	//@formatter:off
	@Select(" select "
            +"   t.status,"
            +"   count(1) count"
            +" from"
            +"   task t "
            +" where t.seller_id = #{sellerId} "
            +"   and ("
            +"     t.status = 'WAIT_EXAMINE' "
            +"     or t.status = 'EXAMINING' "
            +"     or t.status = 'NOT_PASS'"
            +"   ) "
            +" group by t.status ")
	//@formatter:on
	List<TaskCountVo> countByExamineStatusAndSellerId(long sellerId);

	/*
	 * 查看卖家的任务列表
	 */
	List<SellerTaskVo> selectVoListForSeller(TaskSearchVo vo);
	
	/*
	 * 获取待审核任务列表
	 */
	int countForStatus(TaskSearchVo vo);
	List<SellerTaskVo> selectVoForStatus(TaskSearchVo vo);

    Task selectById(long id);

    void updateById(Task task);

    /*
     * 查询单表中特定的列
     */
    @Select("select ${fields} from task where id=#{id}")
    Task selectFieldsById(@Param("fields") String fields, @Param("id") long id);
    
    @Select("select id from task where seller_id=#{serllerId} and status='WAIT_EDIT' limit 1")
    Task selectWaitEdit(@Param("serllerId") long serllerId);
    
    /*
     * 统计可接手任务数量
     */
    Integer mobilePublishAndNotTokenOver();
    Integer pcPublishAndNotTokenOver();
    
    Integer mobilePublishAndNotTokenOverWithPlatform(String platform);
    Integer pcPublishAndNotTokenOverWithPlatform(String platform);
    
    Integer mobilePublishAndNotTokenOverWithDevice(@Param("platform") String platform, @Param("device") String device);
    Integer pcPublishAndNotTokenOverWithDevice(@Param("platform") String platform, @Param("device") String device);
    
    @Select("select sum(total_order_num) from task")
    int sumTotalOrderNum();

	//押金冻结状态的商家返款任务
    @Select("select t.pledge,t.total_order_num,t.finished_count from task t "
        + "where seller_id = #{sellerId} "
        + "and (status = 'PUBLISHED' or status ='WAIT_PUBLISH' or status = 'WAIT_EXAMINE' or status='EXAMINING') "
        + "and sys_refund = false")
    List<Task> selectlockedPledgeTask(@Param("sellerId") long sellerId);
    
    List<Task> selectForFinance(@Param("nick") String sellerNick, @Param("timeStart") Date timeStart, @Param("timeEnd") Date timeEnd);
    
    SearchSystemCountVo findSysCountVo();
    
    List<Task> findSellerPublishCount(@Param("createTime")String createTime);
    
    @Select("SELECT seller_nick nick,SUM(total_order_num) orderNum FROM task WHERE DATE_FORMAT(publish_time, '%Y-%m-%d')=#{createTime} AND `status`!='CANCLED' GROUP BY seller_id ORDER BY orderNum desc limit #{startIndex}, #{pageSize}")
    List<SearchTakenCount> findSellerPublishToday(@Param("createTime")String createTime,@Param("startIndex")int startIndex,@Param("pageSize")int pageSize);
    @Select("SELECT u.nick nick,COUNT(b.id) orderNum FROM buyer_task b LEFT JOIN `user` u ON u.id=b.buyer_id WHERE DATE_FORMAT(b.take_time, '%Y-%m-%d')=#{createTime} AND b.`status`!='CANCLED' GROUP BY b.buyer_id ORDER BY orderNum desc limit #{startIndex}, #{pageSize}")
    List<SearchTakenCount> findBuyerTakenToday(@Param("createTime")String createTime,@Param("startIndex")int startIndex,@Param("pageSize")int pageSize);
    
    @Select("SELECT count(DISTINCT seller_id) FROM task WHERE DATE_FORMAT(publish_time, '%Y-%m-%d')=#{createTime} AND `status`!='CANCLED'")
  
    Integer findSellerPublishTodayCount(@Param("createTime")String createTime);
    
    @Select("SELECT count(DISTINCT buyer_id) FROM buyer_task b LEFT JOIN `user` u ON u.id=b.buyer_id WHERE DATE_FORMAT(b.take_time, '%Y-%m-%d')=#{createTime} AND b.`status`!='CANCLED'  ")
    Integer findBuyerTakenTodayCount(@Param("createTime")String createTime);
  
    @Select("select * from " + Task.TABLE_NAME + " where item_id = #{itemId} limit 1")
	Task findByItemId(@Param("itemId") String itemId);
    
    @Update("update " + Task.TABLE_NAME + " set task_request=#{taskRequest} where id=#{id}")
	void updateTaskRequest(@Param("taskRequest") String taskRequest,@Param("id") long id);

    /**
     * 修改快递类型临时接口
     *
     * @since  v2.9
     * @author fufei
     * @created 2015年5月6日 下午5:49:44
     */
    @Select("SELECT * FROM task WHERE sys_express='false'")
    List<Task> findTaskExpressType();
	
    void updateExpressType(@Param("ids")List<Long> ids);
    
    /**
     * 
     * 商家发任务统计
     *
     * @param vo
     * @return
     * @since  v2.0
     * @author Mark Xu
     * @created 2015-5-8 下午2:17:04
     */
	int findBySellerPutTimeVoCount(SellerPutTimeVo vo);
	List<SellerPutTimeVo> findBySellerPutTimeVo(SellerPutTimeVo vo);

	/**
	 * 
	 * 买手接任务统计
	 *
	 * @param vo
	 * @return
	 * @since  v2.0
	 * @author Mark Xu
	 * @created 2015-5-8 下午2:14:45
	 */
	int findByBuyerTakeTaskVoCount(BuyerTakeTaskVo vo);
	List<BuyerTakeTaskVo> findByBuyerTakeTaskVo(BuyerTakeTaskVo vo);
}
