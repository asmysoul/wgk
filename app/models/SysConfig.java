package models;

import java.util.Date;
import java.util.List;

import models.mappers.NoticeMapper;
import models.mappers.SysConfigMapper;
import net.sf.cglib.core.Local;

import org.apache.ibatis.session.SqlSession;
import org.joda.time.DateTime;

import com.aton.config.CacheType;
import com.aton.config.Config;
import com.aton.db.SessionFactory;
import com.aton.util.CacheUtil;
import com.aton.util.StringUtils;

/**
 * 
 * 系统配置
 * 
 * @author fufei
 * @since v2.9
 * @created 2015年3月26日 上午11:31:59
 */
public class SysConfig {

    public static final String TABLE_NAME = "sys_config";
    public long id;
    public SysConfigKey key;
    public String value;
    public String record;
    public Date modifyTime;

    public enum SysConfigKey {
        BUYER_TASK_MONTH_COUNT,
        BUYER_TASK_WEEK_COUNT,
        BUYER_TASK_DAY_COUNT, 
        BUYER_ACOUNT_AND_ITEM_TIME, 
        BUYER_ACOUNT_AND_SHOP_TIME, 
        BUYER_AND_SHOP_TIME,
        BUYER_AND_SELLER_TIME
    }

    /**
     * 
     * 查询
     * 
     * @param configKey
     * @return
     * @since v2.9
     * @author fufei
     * @created 2015年3月26日 下午2:21:51
     */
    public static SysConfig findById(long id) {
        SqlSession ss = SessionFactory.getSqlSession();
        try {
            SysConfigMapper mapper = ss.getMapper(SysConfigMapper.class);
            return mapper.findById(id);
        } finally {
            ss.close();
        }
    }

    /**
     * 
     * 查询
     * 
     * @param configKey
     * @return
     * @since v2.9
     * @author fufei
     * @created 2015年3月26日 下午2:21:51
     */
    public static List<SysConfig> findByAll() {
        SqlSession ss = SessionFactory.getSqlSession();
        try {
            SysConfigMapper mapper = ss.getMapper(SysConfigMapper.class);
            return mapper.findAll();
        } finally {
            ss.close();
        }
    }

    /**
     * 
     * 修改
     * 
     * @param configKey
     * @return
     * @since v2.9
     * @author fufei
     * @created 2015年3月26日 下午2:21:51
     */
    public void update() {
        SqlSession ss = SessionFactory.getSqlSession();
        try {
            SysConfigMapper mapper = ss.getMapper(SysConfigMapper.class);
            this.modifyTime = DateTime.now().toDate();
            mapper.update(this);
        } finally {
            ss.close();
        }
    }

    /**
     * 
     * 按key查询
     * 
     * @param configKey
     * @return
     * @since v2.9
     * @author fufei
     * @created 2015年3月26日 下午2:21:51
     */
    public static SysConfig findByKey(SysConfigKey key) {
        SqlSession ss = SessionFactory.getSqlSession();
        try {
            SysConfigMapper mapper = ss.getMapper(SysConfigMapper.class);
            return mapper.findByKey(key);
        } finally {
            ss.close();
        }
    }

    /**
     * 
     * 
     * 初始化缓存
     * 
     * @param configKey
     * @return
     * @since v2.9
     * @author fufei
     * @created 2015年3月26日 下午2:21:51
     */
    public static void initCache() {
        SqlSession ss = SessionFactory.getSqlSession();
        try {
            SysConfigMapper mapper = ss.getMapper(SysConfigMapper.class);
            List<SysConfig> configs = mapper.findAll();
            for (SysConfig sysConfig : configs) {
                CacheUtil.set(CacheType.SYS_CONFIG.getKey(sysConfig.key), sysConfig.value,
                    CacheType.SYS_CONFIG.expiredTime);
            }
        } finally {
            ss.close();
        }
    }

    /**
     * 
     * 获取配置值
     * 
     * @return
     * @since v2.9
     * @author fufei
     * @created 2015年3月27日 下午3:07:31
     */
    public static String getConfigValue(SysConfigKey key) {
        String value = CacheUtil.get(CacheType.SYS_CONFIG.getKey(key));
        if (StringUtils.isEmpty(value)) {
            value = SysConfig.findByKey(key).value;
        }
        return value;
    }
}
