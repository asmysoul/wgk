package models.mappers;

import java.util.Date;
import java.util.List;

import models.AdminUser;
import models.BuyerTask;
import models.Task;
import models.Task3;
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
import vos.SellerTaskVo3;
import vos.TaskCountVo;
import vos.TaskCountVo3;
import vos.TaskSearchVo;
import vos.TaskSearchVo3;

public interface TaskMapper3 {

    /**
     * 查询“任务列表”显示所需数据.
     */
    List<Task3> selectList(TaskSearchVo3 vo);
    List<Task3> selectAllList(TaskSearchVo3 vo);
    Integer selectListCount(TaskSearchVo3 vo);
    
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
    List<Task3> selectListFromTaskPool(TaskSearchVo3 vo);
    
    /**
     * 查询单表数据.
     */
    int countSimple(TaskSearchVo3 vo);
    List<Task3> selectSimple(TaskSearchVo3 vo);

    void insert(Task3 task);
    
	int count(TaskSearchVo3 vo);
	
	int countForSeller(TaskSearchVo3 vo);
	
	int countProcessTaskSeller(TaskSearchVo3 vo);
	
	List<SellerTaskVo3> selectProcessTaskSeller(TaskSearchVo3 vo);
	/**
	 * 
	 * 卖家“我的浏览单”-平台审核中的浏览单.
	 */
	//@formatter:off
	@Select(" select "
            +"   t.status,"
            +"   count(1) count"
            +" from"
            +"   task3 t "
            +" where t.seller_id = #{sellerId} "
            +"   and ("
            +"     t.status = 'WAIT_EXAMINE' "
            +"     or t.status = 'EXAMINING' "
            +"     or t.status = 'NOT_PASS'"
            +"   ) "
            +" group by t.status ")
	//@formatter:on
	List<TaskCountVo3> countByExamineStatusAndSellerId(long sellerId);

	/*
	 * 查看卖家的任务列表
	 */
	List<SellerTaskVo3> selectVoListForSeller(TaskSearchVo3 vo);
	
	/*
	 * 获取待审核任务列表
	 */
	int countForStatus(TaskSearchVo3 vo);
	List<SellerTaskVo3> selectVoForStatus(TaskSearchVo3 vo);

    Task3 selectById(long id);

    void updateById(Task3 task);

    /*
     * 查询单表中特定的列
     */
    @Select("select ${fields} from task3 where id=#{id}")
    Task3 selectFieldsById(@Param("fields") String fields, @Param("id") long id);
    
    @Select("select id from task3 where seller_id=#{serllerId} and status='WAIT_EDIT' limit 1")
    Task3 selectWaitEdit(@Param("serllerId") long serllerId);
    
    /*
     * 统计可接手任务数量
     */
    Integer mobilePublishAndNotTokenOver();
    Integer pcPublishAndNotTokenOver();
    
    Integer mobilePublishAndNotTokenOverWithPlatform(String platform);
    Integer pcPublishAndNotTokenOverWithPlatform(String platform);
    
    Integer mobilePublishAndNotTokenOverWithDevice(@Param("platform") String platform, @Param("device") String device);
    Integer pcPublishAndNotTokenOverWithDevice(@Param("platform") String platform, @Param("device") String device);
    
    @Select("select sum(total_order_num) from task3")
    int sumTotalOrderNum();

	//押金冻结状态的商家返款任务
    @Select("select t.pledge,t.total_order_num,t.finished_count from task3 t "
        + "where seller_id = #{sellerId} "
        + "and (status = 'PUBLISHED' or status ='WAIT_PUBLISH' or status = 'WAIT_EXAMINE' or status='EXAMINING') "
        + "and sys_refund = false")
    List<Task3> selectlockedPledgeTask(@Param("sellerId") long sellerId);
    
    List<Task3> selectForFinance(@Param("nick") String sellerNick, @Param("timeStart") Date timeStart, @Param("timeEnd") Date timeEnd);
    
    SearchSystemCountVo findSysCountVo();
    
    List<Task3> findSellerPublishCount(@Param("createTime")String createTime);
    
    @Select("SELECT seller_nick nick,SUM(total_order_num) orderNum FROM task3 WHERE DATE_FORMAT(publish_time, '%Y-%m-%d')=#{createTime} AND `status`!='CANCLED' GROUP BY seller_id ORDER BY orderNum desc limit #{startIndex}, #{pageSize}")
    List<SearchTakenCount> findSellerPublishToday(@Param("createTime")String createTime,@Param("startIndex")int startIndex,@Param("pageSize")int pageSize);
    @Select("SELECT u.nick nick,COUNT(b.id) orderNum FROM buyer_task3 b LEFT JOIN `user` u ON u.id=b.buyer_id WHERE DATE_FORMAT(b.take_time, '%Y-%m-%d')=#{createTime} AND b.`status`!='CANCLED' GROUP BY b.buyer_id ORDER BY orderNum desc limit #{startIndex}, #{pageSize}")
    List<SearchTakenCount> findBuyerTakenToday(@Param("createTime")String createTime,@Param("startIndex")int startIndex,@Param("pageSize")int pageSize);
    
    @Select("SELECT count(DISTINCT seller_id) FROM task3 WHERE DATE_FORMAT(publish_time, '%Y-%m-%d')=#{createTime} AND `status`!='CANCLED'")
  
    Integer findSellerPublishTodayCount(@Param("createTime")String createTime);
    
    @Select("SELECT count(DISTINCT buyer_id) FROM buyer_task3 b LEFT JOIN `user` u ON u.id=b.buyer_id WHERE DATE_FORMAT(b.take_time, '%Y-%m-%d')=#{createTime} AND b.`status`!='CANCLED'  ")
    Integer findBuyerTakenTodayCount(@Param("createTime")String createTime);
  
    @Select("select * from " + Task3.TABLE_NAME + " where item_id = #{itemId} limit 1")
	Task3 findByItemId(@Param("itemId") String itemId);
    
    @Update("update " + Task3.TABLE_NAME + " set task_request=#{taskRequest} where id=#{id}")
	void updateTaskRequest(@Param("taskRequest") String taskRequest,@Param("id") long id);

    /**
     * 修改快递类型临时接口
     *
     * @since  v2.9
     * @author fufei
     * @created 2015年5月6日 下午5:49:44
     */
    @Select("SELECT * FROM task3 WHERE sys_express='false'")
    List<Task3> findTaskExpressType();
	
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
