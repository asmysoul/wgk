package domain;

import java.util.Date;
import java.util.List;

import models.BuyerTask;
import models.SellerConfig;
import models.Shop;
import models.SysConfig;
import models.SysConfig.SysConfigKey;
import models.Task;
import models.User;
import models.mappers.BuyerTaskMapper;
import models.mappers.BuyerTaskMapper2;

import org.apache.commons.lang.Validate;
import org.apache.ibatis.session.SqlSession;
import org.joda.time.DateTime;
import org.joda.time.LocalDate;

import vos.BuyerTaskVo;
import vos.BuyerTaskVo2;

import com.aton.config.BizConstants;
import com.aton.config.CacheType;
import com.aton.config.Config;
import com.aton.db.SessionFactory;
import com.aton.util.CacheUtil;
import com.aton.util.StringUtils;

import enums.Sign;
import enums.TaskStatus;

/**
 * 
 * 用户任务统计.
 * 1、商家发布的任务数
 * 2、买手接手的任务数
 * 
 * @author youblade
 * @since 0.1
 * @created 2014年8月24日 下午5:02:41
 */
public class TaskStats {

    public long id;
    public long userId;
    public int dayCount;
    public int weekCount;
    public int monthCount;
    public Date logDate;

    /**
     * 
     * 获取截止到当前时间，买手本日、本周、本月接手的任务个数统计.
     *
     * @param buyerAccountId
     * @return
     * @since  0.1
     * @author youblade
     * @created 2014年8月24日 下午5:48:24
     */
    public static TaskStats findForBuyerUntilNow(long buyerAccountId) {
        
        String key = CacheType.BUYER_TASK_STATS.getKey(buyerAccountId);
        TaskStats stats = CacheUtil.get(key, TaskStats.class);
        if (stats != null) {
            return stats;
        }
            
        LocalDate today = LocalDate.now();
        SqlSession ss = SessionFactory.getSqlSession();
        try {
            BuyerTaskMapper mapper = ss.getMapper(BuyerTaskMapper.class);
            stats = new TaskStats();
            // 统计今日接手任务数
            Date tomorrow = today.plusDays(1).toDate();
            stats.dayCount = mapper.countByBuyerAccountIdAndTime(buyerAccountId, today.toDate(), tomorrow);

            // 统计本周接手任务数
            LocalDate weekStart = today.withDayOfWeek(1);
            Date weekEnd = weekStart.plusWeeks(1).toDate();
            stats.weekCount = mapper.countByBuyerAccountIdAndTime(buyerAccountId, weekStart.toDate(), weekEnd);

            // 统计本月接手任务数
            LocalDate monthStart = today.withDayOfMonth(1);
            Date monthEnd = monthStart.plusMonths(1).toDate();
            stats.monthCount = mapper.countByBuyerAccountIdAndTime(buyerAccountId, monthStart.toDate(), monthEnd);
        } finally {
            ss.close();
        }
        
        CacheUtil.setJson(key, stats, CacheType.BUYER_TASK_STATS.expiredTime);
        return stats;
    }
    
    /**
     * 
     * 查询【买手】接手某个【店铺】的最近一次任务记录.
     *
     * @param buyerId
     * @param shopId
     * @return
     * @since  0.1
     * @author youblade
     * @created 2014年10月10日 下午3:32:07
     */
    public static BuyerTask findLastTakenRecord(long buyerId,long shopId) {
        SqlSession ss = SessionFactory.getSqlSession();
        try {
            BuyerTaskMapper mapper = ss.getMapper(BuyerTaskMapper.class);
            return mapper.selectOneByUserAndShopIdAndStatus(TaskStatus.CANCLED, buyerId, shopId);
        } finally {
            ss.close();
        }
    }
    
    /**
     *  
     * 账号和商家之间的限制
     *
     * @param buyerId
     * @param shopId
     * @return
     * @since  v2.0
     * @author fufei
     * @created 2015年1月13日 下午3:30:58
     */
    public static List<BuyerTaskVo> findBuyerIdAndSellerId(long buyerId,long sellerId){
        Validate.notNull(buyerId);
        Validate.notNull(sellerId);
        
        SqlSession ss = SessionFactory.getSqlSession();
        try {
            BuyerTaskMapper mapper = ss.getMapper(BuyerTaskMapper.class);
            DateTime end = DateTime.now();
            //从缓存中查找用户信息（关联商家配置信息），如果接任务配置存在，则使用商家配置的信息判断是否可接任务
            User seller = User.findByIdWichCache(sellerId);
            String days = "";
            int day = 0;
            if(seller.buyerAndSellerTime==0) {
            	days=SysConfig.getConfigValue(SysConfigKey.BUYER_AND_SELLER_TIME);
            	if(StringUtils.isEmpty(days)){
            		days=Config.BUYER_AND_SELLER_TIME;
            	}
            	day = Integer.parseInt(days);
            }else {
            	day = seller.buyerAndSellerTime;
            }
            	DateTime start = end.minusDays(day);
            	return mapper.selectByBuyerIdAndSellerIdAndPeriod(buyerId, sellerId, start.toDate(),
            			end.toDate(), TaskStatus.CANCLED);	
        } finally {
            ss.close();
        }
    }
    
    /**
     *  
     * 账号和店铺之间的限制
     *
     * @param buyerId
     * @param shopId
     * @return
     * @since  v2.0
     * @author fufei
     * @created 2015年1月13日 下午3:30:58
     */
    public static List<BuyerTaskVo> findBuyerIdAndShopId(long buyerId,long shopId){
        Validate.notNull(buyerId);
        Validate.notNull(shopId);
        
        SqlSession ss = SessionFactory.getSqlSession();
        try {
            BuyerTaskMapper mapper = ss.getMapper(BuyerTaskMapper.class);
            DateTime end = DateTime.now();
            //从缓存中查找用户信息（关联商家配置信息），如果接任务配置存在，则使用商家配置的信息判断是否可接任务
            User seller = User.findByIdWichCache(Shop.selectById(shopId).sellerId);
            String days = "";
            int day = 0;
            if(seller.buyerAndShopTime==0) {
            	days=SysConfig.getConfigValue(SysConfigKey.BUYER_AND_SHOP_TIME);
            	if(StringUtils.isEmpty(days)){
            		days=Config.BUYER_AND_SHOP_TIME;
            	}
            	day = Integer.parseInt(days);
            }else {
            	day = seller.buyerAndShopTime;
            }
            DateTime start = end.minusDays(day);
            return mapper.selectByBuyerIdAndShopIdAndPeriod(buyerId, shopId, start.toDate(),
                end.toDate(), TaskStatus.CANCLED);            
        } finally {
            ss.close();
        }
    }
    
    /**
     * 
     * 买号和店铺之间的限制
     * 1.仅获取“非撤销”任务
     * 2.按接手时间排序，最新的在最前面
     *
     * @param 旺旺id
     * @param 店铺id
     * @return
     * @since  v2.0
     * @author fufei
     * @created 2015年1月13日 下午3:04:53
     */
     public static List<BuyerTaskVo> findBuyerAccountIdAndShopId(Long buyerAccountId, Long shopId) {
         Validate.notNull(buyerAccountId);
         Validate.notNull(shopId);
         
         SqlSession ss = SessionFactory.getSqlSession();
         try {
             BuyerTaskMapper mapper = ss.getMapper(BuyerTaskMapper.class);
             DateTime end = DateTime.now();
             //从缓存中查找用户信息（关联商家配置信息），如果接任务配置存在，则使用商家配置的信息判断是否可接任务
             User seller = User.findByIdWichCache(Shop.selectById(shopId).sellerId);
             String days = "";
             int day = 0;
             if(seller.buyerAcountAndShopTime==0) {
             	days=SysConfig.getConfigValue(SysConfigKey.BUYER_ACOUNT_AND_SHOP_TIME);
             	if(StringUtils.isEmpty(days)){
             		days=Config.BUYER_ACOUNT_AND_SHOP_TIME;
             	}
             	day = Integer.parseInt(days);
             }else {
            	day = seller.buyerAcountAndShopTime;
             }
             DateTime start = end.minusDays(day);
             return mapper.selectByBuyerAccountIdAndShopIdAndPeriod(buyerAccountId, shopId, start.toDate(),
                 end.toDate(), TaskStatus.CANCLED);
         } finally {
             ss.close();
         }
     }
    
     
     
     
     
     public static List<BuyerTaskVo2> findBuyerAccountIdAndShopId2(Long buyerAccountId, Long shopId) {
         Validate.notNull(buyerAccountId);
         Validate.notNull(shopId);
         
         SqlSession ss = SessionFactory.getSqlSession();
         try {
             BuyerTaskMapper2 mapper = ss.getMapper(BuyerTaskMapper2.class);
             DateTime end = DateTime.now();
             //从缓存中查找用户信息（关联商家配置信息），如果接任务配置存在，则使用商家配置的信息判断是否可接任务
             User seller = User.findByIdWichCache(Shop.selectById(shopId).sellerId);
             String days = "";
             int day = 0;
             if(seller.buyerAcountAndShopTime==0) {
             	days=SysConfig.getConfigValue(SysConfigKey.BUYER_ACOUNT_AND_SHOP_TIME);
             	if(StringUtils.isEmpty(days)){
             		days=Config.BUYER_ACOUNT_AND_SHOP_TIME;
             	}
             	day = Integer.parseInt(days);
             }else {
            	day = seller.buyerAcountAndShopTime;
             }
             DateTime start = end.minusDays(day);
             return mapper.selectByBuyerAccountIdAndShopIdAndPeriod(buyerAccountId, shopId, start.toDate(),
                 end.toDate(), TaskStatus.CANCLED);
         } finally {
             ss.close();
         }
     }
     
     
     
     
     
     
    /**
     * 
     * 买号和商品之间的限制
     *
     * @param buyerAcountId
     * @param itemId
     * @return
     * @since  v2.0
     * @author fufei
     * @created 2015年1月13日 下午4:20:39
     */
    public static List<BuyerTaskVo> selectByBuyerAccountIdAndItemIdAndPeriod(long buyerAcountId, String itemId) {
    	 SqlSession ss = SessionFactory.getSqlSession();
         try {
             BuyerTaskMapper mapper = ss.getMapper(BuyerTaskMapper.class);
             DateTime end = DateTime.now();
             //从缓存中查找用户信息（关联商家配置信息），如果接任务配置存在，则使用商家配置的信息判断是否可接任务
             User seller = User.findByIdWichCache(Task.findByItemId(itemId).sellerId);
             String days = "";
             int day = 0;
             if(seller.buyerAcountAndItemTime==0) {
                 days=SysConfig.getConfigValue(SysConfigKey.BUYER_ACOUNT_AND_ITEM_TIME);
                 if(StringUtils.isEmpty(days)){
                     days=Config.BUYER_ACOUNT_AND_ITEM_TIME;
                 };
                 day = Integer.parseInt(days);
             }else {
            	 day = seller.buyerAcountAndItemTime;
             }
             DateTime start = end.minusDays(day);
             return mapper.selectByBuyerAccountIdAndItemIdAndPeriod(buyerAcountId, itemId, start.toDate(),
                 end.toDate(), TaskStatus.CANCLED);
         } finally {
             ss.close();
         }
    }
   
   
    /**
     * 
     *  【买号】领取的任务数+1并更新其统计缓存.
     *
     * @param buyerAccountId
     * @since  v1.6
     * @author youblade
     * @created 2014年11月27日 上午10:33:40
     */
    public static void updateTaskTakenCount(Long buyerAccountId, Sign sign) {
        String key = CacheType.BUYER_TASK_STATS.getKey(buyerAccountId);
        TaskStats stats = CacheUtil.get(key, TaskStats.class);
        if (stats == null) {
            return;
        }
        
        if (sign == Sign.PLUS) {
            stats.dayCount++;
            stats.weekCount++;
            stats.monthCount++;
        } else {
            stats.dayCount--;
            stats.weekCount--;
            stats.monthCount--;
        }
        CacheUtil.setJson(key, stats, CacheType.BUYER_TASK_STATS.expiredTime);
    }
    
    /**
     * 
     *  查出某个买手对于某个任务所有子任务
     *
     * @param buyerId
     * @param taskId
     * @return
     * @since  v1.6
     * @author playlaugh
     * @created 2014年12月22日 下午5:28:09
     */

    public static List<BuyerTask> findBuyerTasksByItemId(long buyerId, String itemId) {
        SqlSession ss = SessionFactory.getSqlSession();
        try {
            BuyerTaskMapper mapper = ss.getMapper(BuyerTaskMapper.class);
            return mapper.selectBuyerTasksByItemId(buyerId, itemId);
        } finally {
            ss.close();
        }
    }
}
