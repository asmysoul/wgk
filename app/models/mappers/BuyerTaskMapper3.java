package models.mappers;

import java.util.Date;
import java.util.List;

import models.BuyerTask3;

import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.ResultType;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

import vos.BuyerDepositVo;
import vos.BuyerTaskSearchVo3;
import vos.BuyerTaskVo;
import vos.BuyerTaskVo3;
import vos.ExpressCountVo;
import vos.FaBaoGuoVo;
import vos.OrderExpress;
import vos.Page;
import vos.PersonalInfoVo;
import vos.SellerBalanceVo;
import vos.TakeTaskCountVo;
import vos.TaskCountVo3;
import vos.TaskSearchVo3;
import domain.UserMoneyStats;
import enums.Device;
import enums.Platform;
import enums.TaskStatus;

public interface BuyerTaskMapper3 {

    Long countExecute(BuyerTaskSearchVo3 vo);
    
    //@formatter:off
    @Select("select t.device,count(1) count from buyer_task3 t"
        + " where t.status = 'FINISHED' and t.task_id = #{taskId}"
        + " group by t.device ; ")
    List<TaskCountVo3> countFinished(long taskId);

    @Select("select "
        +"   bt.status,"
        +"   t.platform,"
        +"   count(1) count"
        +" from "
        +"   buyer_task3 bt "
        +"   join task3 t on t.id = bt.task_id "
        +" where t.seller_id = #{sellerId} "
        +"   and ("
        +"     bt.status = 'EXPRESS_PRINT' "
        +"     or bt.status = 'WAIT_REFUND'"
        +"     or bt.status = 'WAIT_SEND_GOODS'"
        +"   ) "
        +"  group by bt.status,t.platform ")
    List<TaskCountVo3> countWaitingTasksBySellerId(@Param("sellerId") long sellerId);

    /**
     *
     * 以状态、平台的维度查询买手待处理任务的统计信息.
     *
     * @param buyerId
     * @return
     * @since  0.1
     * @author youblade
     * @created 2014年8月20日 上午11:48:15
     */
    @Select( " select "
        +"   bt.status,"
        +"   t.platform,"
        +"   count(1) count"
        +" from"
        +"   buyer_task3 bt "
        +"   join task3 t on t.id = bt.task_id "
        +" where bt.buyer_id = #{buyerId} "
        +"   and ("
        +"     bt.status = 'WAIT_PAY' "
        +"     or bt.status = 'RECIEVED' "
        +"     or bt.status = 'WAIT_CONFIRM' "
        +"     or bt.status = 'REFUNDING'"
        +"   )"
        +" group by bt.status,t.platform ")
    List<TaskCountVo3> countWaitingTasksByBuyerId(@Param("buyerId") long buyerId);
    
    /**
     * 
     * 查询【买手】接手某个【店铺】最近一次记录.
     *
     * @param status
     * @param buyerId
     * @param shopId
     * @return
     * @since  0.1
     * @author youblade
     * @created 2014年10月10日 下午3:26:33
     */
    @Select("select "
        +"   bt.id,"
        +"   bt.buyer_account_id,"
        +"   bt.take_time "
        +" from"
        +"   buyer_task3 bt,"
        +"   task3 t "
        +" where bt.task_id = t.id "
        +"   and bt.status != #{status} "
        +"   and bt.buyer_id = #{buyerId} "
        +"   and t.shop_id = #{shopId} "
        +" order by bt.id desc "
        +" limit 1")
    //@formatter:on
    BuyerTask3 selectOneByUserAndShopIdAndStatus(@Param("status") TaskStatus status, @Param("buyerId") long buyerId,
        @Param("shopId") long shopId);

    Long hasTask(BuyerTaskSearchVo3 vo);


    int count(TaskSearchVo3 vo);
    List<BuyerTaskVo3> selectList(TaskSearchVo3 vo);
    List<BuyerTaskVo3> selectListForSellerRefund(TaskSearchVo3 vo);
    int sysRefundCount(TaskSearchVo3 vo);
    List<BuyerTaskVo3> selectListForSysRefund(TaskSearchVo3 vo);

    int countForTaskDetail(TaskSearchVo3 vo);
    List<BuyerTask3> selectListForTaskDetail(TaskSearchVo3 vo);
    
    /**
     * 
     * 获取买手提现本金所对应的任务列表
     *
     * @param vo
     * @return
     * @since  v1.7
     * @author youblade
     * @created 2014年11月29日 下午7:37:45
     */
    List<BuyerTaskVo3> selectListForBuyerDepositWithdraw(TaskSearchVo3 vo);
    
    /**
     * 
     * 卖家任务详细页面导出子任务列表
     *
     * @param vo
     * @return
     * @since  v1.4
     * @author youblade
     * @created 2014年11月20日 下午9:00:06
     */
    List<BuyerTask3> selectListForTaskDetailExportCsv(@Param("taskId") long taskId, @Param("isSysRefundTask") boolean isSysRefundTask);

    Integer selectOrdersCount(TaskSearchVo3 vo);
    List<OrderExpress> selectOrders(TaskSearchVo3 vo);
    
    
    Integer selectYDOrderCount(TaskSearchVo3 vo);
    List<OrderExpress> selectYDOrders(TaskSearchVo3 vo);
    
    @Select("select count(*) from buyer_task3 bt join buyer_account ba on bt.buyer_account_id = ba.id join task t on t.id = bt.task_id join shop s on s.id = t.shop_id  and bt.status = #{status} AND t.express_type='KJKD'")		
    Integer selectOrdersCountByStatus(@Param("status")TaskStatus status);
    
    List<OrderExpress> selectOrdersByIds(@Param("status")TaskStatus status,@Param("ids") List<Long> ids);

    void insert(BuyerTask3 bt);

    /** 更新任务 by ID */
    Integer update(BuyerTask3 bt);
    
    Integer updateExpressNoById(BuyerTask3 bt);

    void batchUpdateStatus(@Param("ids") List<Long> ids, @Param("status") TaskStatus status, @Param("modifyTime") Date modifyTime);
    void batchUpdateStatusSafty(BuyerTask3 bt);

    //@formatter:off
    @Select("select "
        +"  bt.status,t.platform,t.item_id,t.item_url "
        +"from "
        +"  buyer_task3 bt "
        +"  join task3 t on t.id = bt.task_id "
        +"where bt.id = #{id} "
        +"  and bt.buyer_id = #{buyerId} "
        +" limit 1")
    //@formatter:on
    BuyerTaskVo3 selectByIdAndBuyerId(@Param("id") long id, @Param("buyerId") long buyerId);

    BuyerTaskVo3 selectVoByIdForPerform(@Param("id") long id, @Param("buyerId") long buyerId);

    BuyerTaskVo3 selectByIdForViewDetail(@Param("id") long id);

    BuyerTask3 selectById(long id);

    BuyerTaskVo3 selectByIdForTaskType(@Param("id") long id);

    void updateById(BuyerTask3 buyerTask);

    void updateByIdAndBuyerId(BuyerTask3 buyerTask);

    /**
     *
     * 查询买手“待确认收货”的任务.
     *
     * @param buyerId
     * @return
     * @since  0.1
     * @author youblade
     * @created 2014年8月21日 下午1:19:32
     */
    List<BuyerTaskVo3> selectListForBuyerConfirm(TaskSearchVo3 vo);

    /**
     *
     * 查询买手未做完的任务（已开始做，尚未下单支付）.
     *
     * @param buyerId
     * @return
     * @since  0.1
     * @author youblade
     * @created 2014年8月24日 下午3:02:51
     */
    //@formatter:off
    @Select("select "
        +"   bt.id,bt.task_id,"
        +"   s.type lastStep"
        +" from"
        +"   buyer_task3 bt "
        +"   left join "
        +"   buyer_task_step3 s  "
        +"   on bt.id = s.buyer_task_id "
        +" where bt.buyer_id = #{buyerId} "
        +"   and (bt.status = 'WAIT_PAY' or bt.status = 'RECIEVED')"
        +" order by s.no desc "
        +" limit 1")
    BuyerTask3 selectLastWaitPayAndStepByBuyerId(long buyerId);

    
    
    /**
    *
    * 查询买手未做完的任务列表（已开始做，尚未下单支付）.
    */
   //@formatter:off
   @Select("select "
       +"   bt.id,bt.task_id,"
       +"   t.collection_type"
       +" from"
       +"   buyer_task3 bt "
       + "	left join"
       + "	task3 t  "
       + "	on bt.task_id=t.id"
       +" where bt.buyer_id = #{buyerId} "
       +"   and (bt.status = 'WAIT_PAY' or bt.status = 'RECIEVED')"
       +" order by bt.id desc ")
   List<BuyerTaskVo3> selectLastWaitPaysAndStepsByBuyerId(long buyerId);
    
    
    
    @Select("select "
        +"   count(1)"
        +" from"
        +"   buyer_task3 bt "
        +" where bt.buyer_account_id = #{buyerAccountId} "
        +"   and bt.take_time >= #{start} "
        +"   and bt.take_time < #{end} and bt.status!='CANCLED'")
    //@formatter:on
    int countByBuyerAccountIdAndTime(@Param("buyerAccountId") long buyerAccountId, @Param("start") Date start, @Param("end") Date end);

    @Select("SELECT COUNT(1) COUNT," +
            " t.total_order_num " +
            "FROM buyer_task3 bt " +
            "LEFT JOIN task t ON bt.task_id = t.id " +
            "WHERE t.id =#{taskId} AND bt.STATUS = 'FINISHED'")
    TaskCountVo3 countForTaskFinish(@Param("taskId") long taskId);

    /**
     *  查询单表
     */
    List<BuyerTask3> select(TaskSearchVo3 vo);

    /**
     *  查询是否已接手过该任务
     */
    @Select("select id from " + BuyerTask3.TABLE_NAME
        + " where buyer_account_id = #{buyerAccountId} "
        + " and task_id =#{taskId}"
        + " and status != #{status}"
        + " limit 1")
    BuyerTask3 selectForCheckTaking(TaskSearchVo3 vo);

    @Deprecated
    @Select("select count(1) from buyer_task3 where task_id = #{id} and device = #{device} and status != 'CANCLED'")
    int countForSellerTask(@Param("id") long id, @Param("device") Device device);

    @Select("select id from buyer_task3 where status in ('RECIEVED','WAIT_PAY') and take_time <= #{date}")
    List<BuyerTask3> selectForCancel(@Param("date") Date date);
    
    /**
     * 
     * 查询统计【买手】的垫付资金情况.
     *
     * @param buyerId
     * @param statuses
     * @return
     * @since  0.1
     * @author youblade
     * @created 2014年9月30日 下午5:15:24
     */
    PersonalInfoVo selectBuyerPaidFeeInfo(@Param("buyerId") long buyerId, @Param("statusList") List<TaskStatus> statuses);
    
    /**
     * 
     * 查询统计【买手】的未完成任务情况.
     *
     * @param buyerId
     * @param statuses
     * @return
     * @since  0.1
     * @author youblade
     * @created 2014年9月30日 下午4:57:36
     */
    PersonalInfoVo selectBuyerExcutingInfo(@Param("buyerId") long buyerId, @Param("statusList") List<TaskStatus> statuses);

    @Select("select sum(reward_ingot) from "+BuyerTask3.TABLE_NAME)
    long sumRewardIngot();
   
    @Select("select "
            +"   bt.id,"
            +"   bt.take_time "
            +" from"
            +"   buyer_task3 bt "
            +" where bt.buyer_id = #{buyerId} "
            +"   and bt.status != #{status} "
            +"   and bt.seller_id = #{sellerId} "
            +"   and bt.take_time >= #{start} "
            +"   and bt.take_time <= #{end} "
            +" order by bt.take_time desc")
        List<BuyerTaskVo3> selectByBuyerIdAndSellerIdAndPeriod(@Param("buyerId") Long buyerId,
            @Param("sellerId") long sellerId, @Param("start") Date start, @Param("end") Date end,
            @Param("status") TaskStatus status);
    
    @Select("select "
            +"   bt.id,"
            +"   t.item_id,"
            +"   bt.take_time "
            +" from"
            +"   buyer_task3 bt "
            +"   left join"
            +"   task t "
            +"   on t.id = bt.task_id "
            +" where bt.buyer_id = #{buyerId} "
            +"   and bt.status != #{status} "
            +"   and t.shop_id = #{shopId} "
            +"   and bt.take_time >= #{start} "
            +"   and bt.take_time <= #{end} "
            +" order by bt.take_time desc")
        List<BuyerTaskVo3> selectByBuyerIdAndShopIdAndPeriod(@Param("buyerId") Long buyerId,
            @Param("shopId") long shopId, @Param("start") Date start, @Param("end") Date end,
            @Param("status") TaskStatus status);
    
    @Select("select "
            +"   bt.id,"
            +"   t.item_id,"
            +"   bt.take_time "
            +" from"
            +"   buyer_task3 bt "
            +"   left join"
            +"   task t "
            +"   on t.id = bt.task_id "
            +" where bt.buyer_account_id = #{buyerAccountId} "
            +"   and bt.status != #{status} "
            +"   and t.shop_id = #{shopId} "
            +"   and bt.take_time >= #{start} "
            +"   and bt.take_time <= #{end} "
            +" order by bt.take_time desc")
        List<BuyerTaskVo3> selectByBuyerAccountIdAndShopIdAndPeriod(@Param("buyerAccountId") Long buyerAccountId,
            @Param("shopId") long shopId, @Param("start") Date start, @Param("end") Date end,
            @Param("status") TaskStatus status);
    
    @Select("select "
            +"   bt.id,"
            +"   t.item_id,"
            +"   bt.take_time "
            +" from"
            +"   buyer_task3 bt "
            +"   left join"
            +"   task t "
            +"   on t.id = bt.task_id "
            +" where bt.buyer_account_id = #{buyerAccountId} "
            +"   and bt.status != #{status} "
            +"   and t.item_id = #{itemId} "
            +"   and bt.take_time >= #{start} "
            +"   and bt.take_time <= #{end} "
            +" order by bt.take_time desc")
        List<BuyerTaskVo3> selectByBuyerAccountIdAndItemIdAndPeriod(@Param("buyerAccountId") Long buyerAccountId,
            @Param("itemId") String itemId, @Param("start") Date start, @Param("end") Date end,
            @Param("status") TaskStatus status);
    /**
     * 
     * 按【用户】和【状态】统计“待平台返款”类型的子任务数量.
     *
     * @param vo
     * @return
     * @since  v1.7
     * @author youblade
     * @created 2014年11月27日 下午4:04:51
     */
    int countSysRefundByUserIdAndStatus(TaskSearchVo3 vo);

    /**
     * 
     * 统计【买手】当前尚未退款的任务数及垫付金额合计
     *
     * @param buyerId
     * @param statuses
     * @return
     * @since  v1.7
     * @author youblade
     * @created 2014年11月29日 下午4:42:23
     */
    List<UserMoneyStats> sumPaidFeeByBuyerIdAndStatusNotIn(@Param("buyerId") long buyerId, @Param("statusList") List<TaskStatus> statuses);

    /**
     * 
     * 统计【买手】当前未进行“核实退款”的任务数及金额合计
     *
     * @param buyerId
     * @param statuses
     * @return
     * @since  v1.7
     * @author youblade
     * @created 2014年11月29日 下午4:42:37
     */
    List<UserMoneyStats> sumPaidFeeWaitConfirmByBuyerIdAndStatusIn(@Param("buyerId") long buyerId, @Param("statusList") List<TaskStatus> statuses);

    /**
     * 
     * 计算一组任务的总的垫付金额
     *
     * @param userId
     * @param buyerTaskIds
     * @return
     * @since  v1.7
     * @author youblade
     * @created 2014年11月29日 下午9:56:42
     */
    long sumPaidFeeByBuyerIdAndIds(@Param("buyerId") long buyerId, @Param("buyerTaskIds") List<Long> buyerTaskIds);
    List<BuyerTaskVo3> selectListForAdmin(TaskSearchVo3 vo);

    /**
     * 
     * 从数据库中获取所有未返款的【买手本金提现申请】
     *
     * @return
     * @since  v1.6
     * @author playlaugh
     * @created 2014年12月6日 上午10:58:35
     */
    List<BuyerDepositVo> selectUntradeBuyerDeposit();
    /**
     * 
     * 用户佣金记录
     *
     * @return
     * @since  v3.2
     * @author fufei
     * @created 2015年4月14日 下午4:11:08
     */
    List<BuyerDepositVo> selectUntradeBuyerWidthdraw();
    /**
     * 
     *  查出某个买手对于某个任务所有子任务
     *
     * @return
     * @since  v1.9.5
     * @author playlaugh
     * @created 2014年12月22日 下午5:19:51
     */
    @Select("select "
        + "     bt.id,"
        + "     bt.take_time"
        + " from "
        + "     buyer_task3 bt "
        + " left join "
        + "     task t "
        + " on bt.task_id = t.id"
        + " where "
        + " t.item_id = #{itemId}"
        + " and bt.status != 'CANCLED'"
        + " and bt.buyer_id = #{buyerId}"
        + " order by take_time desc")
    List<BuyerTask3> selectBuyerTasksByItemId(@Param("buyerId") long buyerId,@Param("itemId")String itemId);
    /**
     * 
     * 每日接单数统计
     *
     * @return
     * @since  v2.4
     * @author fufei
     * @created 2015年2月5日 上午10:44:56
     */
    @Select("SELECT DATE_FORMAT(t.take_time, '%Y-%m-%d') takeTime, COUNT(*) count,count(distinct buyer_id)takenBuyerCount FROM buyer_task3 t WHERE t.status != 'CANCLED' GROUP BY DATE_FORMAT(t.take_time, '%Y-%m-%d') DESC limit #{startIndex}, #{pageSize}")
    List<TakeTaskCountVo> findTakeTaskCount(TakeTaskCountVo vo);
    
   /**放单的商家数*/
    Integer findExpenseSellerCount(@Param("publishTime")String publishTime);
    Integer findNewBuyerCount(@Param("createTime")String createTime);
    Integer findNewSellerCount(@Param("createTime")String createTime);
    @Select("SELECT count(1) FROM `user` WHERE DATE_FORMAT(create_time,'%Y-%m-%d')=#{createTime} AND type='SELLER'")
    Integer findSellerCount(@Param("createTime")String createTime);
    @Select("SELECT count(1) FROM `user` WHERE DATE_FORMAT(create_time,'%Y-%m-%d')=#{createTime} AND type='BUYER'")
    Integer findBuyerCount(@Param("createTime")String createTime);
    /**
     * 
     * 每日接单数总数统计
     *
     * @return
     * @since  v2.4
     * @author fufei
     * @created 2015年2月5日 上午10:44:56
     */
    @Select("SELECT COUNT(*) FROM buyer_task3 t WHERE t.status != 'CANCLED' GROUP BY DATE_FORMAT(t.take_time, '%Y-%m-%d') DESC")
    List<TakeTaskCountVo> findTakeTaskTotalCount();
    
    List<SellerBalanceVo> exportBuyerTaskBalance(SellerBalanceVo balanceVo);
    List<SellerBalanceVo> findBuyerTaskBalance(SellerBalanceVo balanceVo);
    int findBuyerTaskBalanceCount(SellerBalanceVo balanceVo);
    
    List<SellerBalanceVo> findSellerTaskBalance(SellerBalanceVo balanceVo);
    List<SellerBalanceVo> exportSellerTaskBalance(SellerBalanceVo balanceVo);
    int findSellerTaskBalanceCount(SellerBalanceVo balanceVo);
    
    @Select("SELECT COUNT(*) FROM buyer_task3 t WHERE t.buyer_account_id =#{buyerAccountId} and t.status='FINISHED'")
	Integer findByBuyerAccount(@Param("buyerAccountId")long buyerAccountId);

    @Select("SELECT * FROM " + BuyerTask3.TABLE_NAME + " WHERE task_id=#{taskId}")
	BuyerTask3 selectByTaskId(@Param("taskId")long taskId);

    /**
     * 根据订单查询
     *
     * @param orderId
     * @return
     * @since  v3.4
     * @author fufei
     * @created 2015年4月23日 下午3:18:09
     */
    @Select("SELECT * FROM buyer_task3 WHERE order_id=#{orderId}")
    BuyerTask3 findByOrderId(@Param("orderId")String orderId);

    /**发包裹
     *
     * @param vo
     * @return
     * @since  v3.4
     * @author fufei
     * @created 2015年4月29日 下午5:21:55
     */
    int findFaBaoGuoOrdersCount(TaskSearchVo3 vo);

    /**
     * 发包裹
     *
     * @param vo
     * @return
     * @since  v3.4
     * @author fufei
     * @created 2015年4月29日 下午5:22:01
     */
    List<FaBaoGuoVo> findFaBaoGuoOrders(TaskSearchVo3 vo);
    
    void batchFaBaoGuoModifyStatus(@Param("ids") List<Long> ids, @Param("status") TaskStatus status, @Param("modifyTime") Date modifyTime);
    @Update("UPDATE fabaoguo SET `status`=#{status} WHERE id=#{id}")
    void updateFaBaoGuoById(@Param("id")long id,@Param("status") String status);
    
    @Update("UPDATE fabaoguo SET `express_no`=#{expressNo},`status`=#{status} WHERE id=#{id}")
    void updateFaBaoGuoExpressNoById(@Param("id")long id,@Param("expressNo") String expressNo,@Param("status") String status);
    
    @Select("SELECT * FROM fabaoguo WHERE id=#{id}")
    FaBaoGuoVo findFaBaoGuoById(@Param("id")long id);
    
    @Select("SELECT * FROM fabaoguo WHERE order_sn=#{orderId}")
    FaBaoGuoVo findFaBaoGuoByprderSn(@Param("orderId")String orderId);

    /**
     * 增加订单
     *
     * @param vo
     * @since  v3.4
     * @author fufei
     * @created 2015年4月30日 下午5:30:27
     */
    void inserFabaoguoOrders(FaBaoGuoVo vo);
    
    void batchDeleteFaBaoGuo(@Param("ids") List<Long> ids, @Param("status") TaskStatus status);

    /**
    
     *
     * @param bt
     * @return
     * @since  v2.9
     * @author fufei
     * @created 2015年5月4日 下午4:23:00
     */
    Integer modifyYDKD(BuyerTask3 bt);
    @Select("SELECT DATE_FORMAT(take_time, '%Y-%m-%d') takeTime FROM buyer_task3 WHERE DATE_FORMAT(take_time, '%Y-%m-%d')  GROUP BY DATE_FORMAT(take_time, '%Y-%m-%d') ORDER BY takeTime desc limit #{startIndex}, #{pageSize}")
    List<ExpressCountVo> findExpressList(@Param("startIndex")int startIndex, @Param("pageSize")int pageSize);
    
    @Select("SELECT COUNT(1) FROM (SELECT count(1) FROM buyer_task3 WHERE DATE_FORMAT(take_time, '%Y-%m-%d')  GROUP BY DATE_FORMAT(take_time, '%Y-%m-%d')) as express_count")
    Integer findExpressListCount();
    
    Integer findExpressCount(ExpressCountVo vo); 
    Integer findFaBaoGuoCount(ExpressCountVo vo);
}

