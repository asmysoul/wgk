package models.mappers;

import java.util.Date;
import java.util.List;

import models.BuyerTask;

import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.ResultType;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

import vos.BuyerDepositVo;
import vos.BuyerTaskSearchVo;
import vos.BuyerTaskVo;
import vos.ExpressCountVo;
import vos.FaBaoGuoVo;
import vos.OrderExpress;
import vos.Page;
import vos.PersonalInfoVo;
import vos.SellerBalanceVo;
import vos.TakeTaskCountVo;
import vos.TaskCountVo;
import vos.TaskSearchVo;
import domain.UserMoneyStats;
import enums.Device;
import enums.Platform;
import enums.TaskStatus;

public interface BuyerTaskMapper {

    Long countExecute(BuyerTaskSearchVo vo);
    
    //@formatter:off
    @Select("select t.device,count(1) count from buyer_task t"
        + " where t.status = 'FINISHED' and t.task_id = #{taskId}"
        + " group by t.device ; ")
    List<TaskCountVo> countFinished(long taskId);

    @Select("select "
        +"   bt.status,"
        +"   t.platform,"
        +"   count(1) count"
        +" from "
        +"   buyer_task bt "
        +"   join task t on t.id = bt.task_id "
        +" where t.seller_id = #{sellerId} "
        +"   and ("
        +"     bt.status = 'EXPRESS_PRINT' "
        +"     or bt.status = 'WAIT_REFUND'"
        +"     or bt.status = 'WAIT_SEND_GOODS'"
        +"   ) "
        +"  group by bt.status,t.platform ")
    List<TaskCountVo> countWaitingTasksBySellerId(@Param("sellerId") long sellerId);

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
        +"   buyer_task bt "
        +"   join task t on t.id = bt.task_id "
        +" where bt.buyer_id = #{buyerId} "
        +"   and ("
        +"     bt.status = 'WAIT_PAY' "
        +"     or bt.status = 'RECIEVED' "
        +"     or bt.status = 'WAIT_CONFIRM' "
        +"     or bt.status = 'REFUNDING'"
        +"   )"
        +" group by bt.status,t.platform ")
    List<TaskCountVo> countWaitingTasksByBuyerId(@Param("buyerId") long buyerId);
    
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
        +"   buyer_task bt,"
        +"   task t "
        +" where bt.task_id = t.id "
        +"   and bt.status != #{status} "
        +"   and bt.buyer_id = #{buyerId} "
        +"   and t.shop_id = #{shopId} "
        +" order by bt.id desc "
        +" limit 1")
    //@formatter:on
    BuyerTask selectOneByUserAndShopIdAndStatus(@Param("status") TaskStatus status, @Param("buyerId") long buyerId,
        @Param("shopId") long shopId);

    Long hasTask(BuyerTaskSearchVo vo);


    int count(TaskSearchVo vo);
    List<BuyerTaskVo> selectList(TaskSearchVo vo);
    List<BuyerTaskVo> selectListForSellerRefund(TaskSearchVo vo);
    int sysRefundCount(TaskSearchVo vo);
    List<BuyerTaskVo> selectListForSysRefund(TaskSearchVo vo);

    int countForTaskDetail(TaskSearchVo vo);
    List<BuyerTask> selectListForTaskDetail(TaskSearchVo vo);
    
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
    List<BuyerTaskVo> selectListForBuyerDepositWithdraw(TaskSearchVo vo);
    
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
    List<BuyerTask> selectListForTaskDetailExportCsv(@Param("taskId") long taskId, @Param("isSysRefundTask") boolean isSysRefundTask);

    Integer selectOrdersCount(TaskSearchVo vo);
    List<OrderExpress> selectOrders(TaskSearchVo vo);
    
    
    Integer selectYDOrderCount(TaskSearchVo vo);
    List<OrderExpress> selectYDOrders(TaskSearchVo vo);
    
    @Select("select count(*) from buyer_task bt join buyer_account ba on bt.buyer_account_id = ba.id join task t on t.id = bt.task_id join shop s on s.id = t.shop_id  and bt.status = #{status} AND t.express_type='KJKD'")		
    Integer selectOrdersCountByStatus(@Param("status")TaskStatus status);
    
    List<OrderExpress> selectOrdersByIds(@Param("status")TaskStatus status,@Param("ids") List<Long> ids);

    void insert(BuyerTask bt);

    /** 更新任务 by ID */
    Integer update(BuyerTask bt);
    
    Integer updateExpressNoById(BuyerTask bt);

    void batchUpdateStatus(@Param("ids") List<Long> ids, @Param("status") TaskStatus status, @Param("modifyTime") Date modifyTime);
    void batchUpdateStatusSafty(BuyerTask bt);

    //@formatter:off
    @Select("select "
        +"  bt.status,t.platform,t.item_id,t.item_url "
        +"from "
        +"  buyer_task bt "
        +"  join task t on t.id = bt.task_id "
        +"where bt.id = #{id} "
        +"  and bt.buyer_id = #{buyerId} "
        +" limit 1")
    //@formatter:on
    BuyerTaskVo selectByIdAndBuyerId(@Param("id") long id, @Param("buyerId") long buyerId);

    BuyerTaskVo selectVoByIdForPerform(@Param("id") long id, @Param("buyerId") long buyerId);

    BuyerTaskVo selectByIdForViewDetail(@Param("id") long id);

    BuyerTask selectById(long id);

    BuyerTaskVo selectByIdForTaskType(@Param("id") long id);

    void updateById(BuyerTask buyerTask);

    void updateByIdAndBuyerId(BuyerTask buyerTask);

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
    List<BuyerTaskVo> selectListForBuyerConfirm(TaskSearchVo vo);

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
        +"   buyer_task bt "
        +"   left join "
        +"   buyer_task_step s  "
        +"   on bt.id = s.buyer_task_id "
        +" where bt.buyer_id = #{buyerId} "
        +"   and (bt.status = 'WAIT_PAY' or bt.status = 'RECIEVED')"
        +" order by s.no desc "
        +" limit 1")
    BuyerTask selectLastWaitPayAndStepByBuyerId(long buyerId);

    @Select("select "
        +"   count(1)"
        +" from"
        +"   buyer_task bt "
        +" where bt.buyer_account_id = #{buyerAccountId} "
        +"   and bt.take_time >= #{start} "
        +"   and bt.take_time < #{end} and bt.status!='CANCLED'")
    //@formatter:on
    int countByBuyerAccountIdAndTime(@Param("buyerAccountId") long buyerAccountId, @Param("start") Date start, @Param("end") Date end);

    @Select("SELECT COUNT(1) COUNT," +
            " t.total_order_num " +
            "FROM buyer_task bt " +
            "LEFT JOIN task t ON bt.task_id = t.id " +
            "WHERE t.id =#{taskId} AND bt.STATUS = 'FINISHED'")
    TaskCountVo countForTaskFinish(@Param("taskId") long taskId);

    /**
     *  查询单表
     */
    List<BuyerTask> select(TaskSearchVo vo);

    /**
     *  查询是否已接手过该任务
     */
    @Select("select id from " + BuyerTask.TABLE_NAME
        + " where buyer_account_id = #{buyerAccountId} "
        + " and task_id =#{taskId}"
        + " and status != #{status}"
        + " limit 1")
    BuyerTask selectForCheckTaking(TaskSearchVo vo);

    @Deprecated
    @Select("select count(1) from buyer_task where task_id = #{id} and device = #{device} and status != 'CANCLED'")
    int countForSellerTask(@Param("id") long id, @Param("device") Device device);

    @Select("select id from buyer_task where status in ('RECIEVED','WAIT_PAY') and take_time <= #{date}")
    List<BuyerTask> selectForCancel(@Param("date") Date date);
    
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

    @Select("select sum(reward_ingot) from "+BuyerTask.TABLE_NAME)
    long sumRewardIngot();
   
    @Select("select "
            +"   bt.id,"
            +"   bt.take_time "
            +" from"
            +"   buyer_task bt "
            +" where bt.buyer_id = #{buyerId} "
            +"   and bt.status != #{status} "
            +"   and bt.seller_id = #{sellerId} "
            +"   and bt.take_time >= #{start} "
            +"   and bt.take_time <= #{end} "
            +" order by bt.take_time desc")
        List<BuyerTaskVo> selectByBuyerIdAndSellerIdAndPeriod(@Param("buyerId") Long buyerId,
            @Param("sellerId") long sellerId, @Param("start") Date start, @Param("end") Date end,
            @Param("status") TaskStatus status);
    
    @Select("select "
            +"   bt.id,"
            +"   t.item_id,"
            +"   bt.take_time "
            +" from"
            +"   buyer_task bt "
            +"   left join"
            +"   task t "
            +"   on t.id = bt.task_id "
            +" where bt.buyer_id = #{buyerId} "
            +"   and bt.status != #{status} "
            +"   and t.shop_id = #{shopId} "
            +"   and bt.take_time >= #{start} "
            +"   and bt.take_time <= #{end} "
            +" order by bt.take_time desc")
        List<BuyerTaskVo> selectByBuyerIdAndShopIdAndPeriod(@Param("buyerId") Long buyerId,
            @Param("shopId") long shopId, @Param("start") Date start, @Param("end") Date end,
            @Param("status") TaskStatus status);
    
    @Select("select "
            +"   bt.id,"
            +"   t.item_id,"
            +"   bt.take_time "
            +" from"
            +"   buyer_task bt "
            +"   left join"
            +"   task t "
            +"   on t.id = bt.task_id "
            +" where bt.buyer_account_id = #{buyerAccountId} "
            +"   and bt.status != #{status} "
            +"   and t.shop_id = #{shopId} "
            +"   and bt.take_time >= #{start} "
            +"   and bt.take_time <= #{end} "
            +" order by bt.take_time desc")
        List<BuyerTaskVo> selectByBuyerAccountIdAndShopIdAndPeriod(@Param("buyerAccountId") Long buyerAccountId,
            @Param("shopId") long shopId, @Param("start") Date start, @Param("end") Date end,
            @Param("status") TaskStatus status);
    
    @Select("select "
            +"   bt.id,"
            +"   t.item_id,"
            +"   bt.take_time "
            +" from"
            +"   buyer_task bt "
            +"   left join"
            +"   task t "
            +"   on t.id = bt.task_id "
            +" where bt.buyer_account_id = #{buyerAccountId} "
            +"   and bt.status != #{status} "
            +"   and t.item_id = #{itemId} "
            +"   and bt.take_time >= #{start} "
            +"   and bt.take_time <= #{end} "
            +" order by bt.take_time desc")
        List<BuyerTaskVo> selectByBuyerAccountIdAndItemIdAndPeriod(@Param("buyerAccountId") Long buyerAccountId,
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
    int countSysRefundByUserIdAndStatus(TaskSearchVo vo);

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
    List<BuyerTaskVo> selectListForAdmin(TaskSearchVo vo);

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
        + "     buyer_task bt "
        + " left join "
        + "     task t "
        + " on bt.task_id = t.id"
        + " where "
        + " t.item_id = #{itemId}"
        + " and bt.status != 'CANCLED'"
        + " and bt.buyer_id = #{buyerId}"
        + " order by take_time desc")
    List<BuyerTask> selectBuyerTasksByItemId(@Param("buyerId") long buyerId,@Param("itemId")String itemId);
    /**
     * 
     * 每日接单数统计
     *
     * @return
     * @since  v2.4
     * @author fufei
     * @created 2015年2月5日 上午10:44:56
     */
    @Select("SELECT DATE_FORMAT(t.take_time, '%Y-%m-%d') takeTime, COUNT(*) count,count(distinct buyer_id)takenBuyerCount FROM buyer_task t WHERE t.status != 'CANCLED' GROUP BY DATE_FORMAT(t.take_time, '%Y-%m-%d') DESC limit #{startIndex}, #{pageSize}")
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
    @Select("SELECT COUNT(*) FROM buyer_task t WHERE t.status != 'CANCLED' GROUP BY DATE_FORMAT(t.take_time, '%Y-%m-%d') DESC")
    List<TakeTaskCountVo> findTakeTaskTotalCount();
    
    List<SellerBalanceVo> exportBuyerTaskBalance(SellerBalanceVo balanceVo);
    List<SellerBalanceVo> findBuyerTaskBalance(SellerBalanceVo balanceVo);
    int findBuyerTaskBalanceCount(SellerBalanceVo balanceVo);
    
    List<SellerBalanceVo> findSellerTaskBalance(SellerBalanceVo balanceVo);
    List<SellerBalanceVo> exportSellerTaskBalance(SellerBalanceVo balanceVo);
    int findSellerTaskBalanceCount(SellerBalanceVo balanceVo);
    
    @Select("SELECT COUNT(*) FROM buyer_task t WHERE t.buyer_account_id =#{buyerAccountId} and t.status='FINISHED'")
	Integer findByBuyerAccount(@Param("buyerAccountId")long buyerAccountId);

    @Select("SELECT * FROM " + BuyerTask.TABLE_NAME + " WHERE task_id=#{taskId}")
	BuyerTask selectByTaskId(@Param("taskId")long taskId);

    /**
     * 根据订单查询
     *
     * @param orderId
     * @return
     * @since  v3.4
     * @author fufei
     * @created 2015年4月23日 下午3:18:09
     */
    @Select("SELECT * FROM buyer_task WHERE order_id=#{orderId}")
    BuyerTask findByOrderId(@Param("orderId")String orderId);

    /**发包裹
     *
     * @param vo
     * @return
     * @since  v3.4
     * @author fufei
     * @created 2015年4月29日 下午5:21:55
     */
    int findFaBaoGuoOrdersCount(TaskSearchVo vo);

    /**
     * 发包裹
     *
     * @param vo
     * @return
     * @since  v3.4
     * @author fufei
     * @created 2015年4月29日 下午5:22:01
     */
    List<FaBaoGuoVo> findFaBaoGuoOrders(TaskSearchVo vo);
    
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
    Integer modifyYDKD(BuyerTask bt);
    @Select("SELECT DATE_FORMAT(take_time, '%Y-%m-%d') takeTime FROM buyer_task WHERE DATE_FORMAT(take_time, '%Y-%m-%d')  GROUP BY DATE_FORMAT(take_time, '%Y-%m-%d') ORDER BY takeTime desc limit #{startIndex}, #{pageSize}")
    List<ExpressCountVo> findExpressList(@Param("startIndex")int startIndex, @Param("pageSize")int pageSize);
    
    @Select("SELECT COUNT(1) FROM (SELECT count(1) FROM buyer_task WHERE DATE_FORMAT(take_time, '%Y-%m-%d')  GROUP BY DATE_FORMAT(take_time, '%Y-%m-%d')) as express_count")
    Integer findExpressListCount();
    
    Integer findExpressCount(ExpressCountVo vo); 
    Integer findFaBaoGuoCount(ExpressCountVo vo);
}

