 package com.aton.util;


import org.apache.commons.lang.Validate;
import org.joda.time.DateTime;
import org.joda.time.Seconds;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import play.cache.Cache;

import com.aton.config.CacheType;
import com.aton.config.Config;
import com.google.common.base.Strings;
import com.sun.istack.internal.Nullable;

/**
 *
 * 缓存操作的工具类.<br>
 *
 * 1.增加对泛型的支持，避免每次获取都需要强制类型转换<br>
 * 2.增加Logger记录，便于调试，应优先使用该类中的方法<br>
 * 3.增加设置失效时间点为某天00:00:00，使用单位“t”
 * 4.增加了从缓存的JSON中解析出对应类的方法（不使用java的对象序列化）
 *
 * @author youblade
 * @since v0.1
 * @created 2013-9-12 下午3:23:16
 */
public class CacheUtil {

	private static final Logger log = LoggerFactory.getLogger("cache");

	/**
	 *
	 * 封转从缓存的JSON字符串中获取对象<br>
	 *
	 * @param key
	 * @return
	 * @since v0.1
	 * @author youblade
	 * @created 2014年8月24日 下午6:01:56
	 */
	@Nullable
    public static <T> T get(String key, Class clazz) {
        String json = get(key);
        if(Strings.isNullOrEmpty(json)){
            return null;
        }
        return JsonUtil.toBean(json, clazz);
    }
	
	/**
	 * 
	 * 将对象序列化为JSON字符串后放入缓存.
	 *
	 * @param key
	 * @param value
	 * @param expiration
	 * @since  0.1
	 * @author youblade
	 * @created 2014年8月24日 下午6:24:57
	 */
	public static void setJson(String key, Object value, String expiration) {
	    Validate.notNull(value);
	    set(key, JsonUtil.toJson(value), expiration);
	}
	
	/**
	 *
	 * 封转从缓存中获取对象<br>
	 *
	 * @param key
	 * @return
	 * @since v0.1
	 * @author youblade
	 * @created 2013-9-12 下午3:27:30
	 */
	public static <T> T get(String key) {
		key = processKeyForMemcached(key);
		
		log.debug("Get value from cache,key={}", key);
		
		Object obj = Cache.get(key);
		if (obj == null) {
			log.warn("Cache hit missing,key={}", key);
			return null;
		}
		return (T) obj;
	}

	/**
	 * 
	 * 设置缓存，自动匹配过期时间
	 *
	 * @param key
	 * @param value
	 * @since  v0.5.1
	 * @author tr0j4n
	 * @created 2014-1-25 下午7:56:14
	 */
	public static void set(String key, Object value) {
		for (CacheType ct : CacheType.values()) {
			if (key.startsWith(ct.getKey())) {
				set(key, value, ct.expiredTime);
				break;
			}
		}
	}
	
	
	/**
	 *
	 * 设置缓存，若失败，则重试5次.
	 *
	 * @param key
	 * @param value
	 * @param expiration
	 * @since v0.4
	 * @author youblade
	 * @created 2013-12-9 上午12:34:36
	 */
    public static void set(String key, Object value, String expiration) {

        // 目前仅支持到第二天凌晨00:00,
        if (StringUtils.isNotBlank(expiration) && expiration.endsWith("t")) {
            DateTime now = DateTime.now();
            DateTime ldt = now.plusDays(1).toLocalDate().toDateTimeAtStartOfDay();
            int seconds = Seconds.secondsBetween(now, ldt).getSeconds();
            expiration = seconds + "s";
        }

        key = processKeyForMemcached(key);

        log.info("Set value of cache,key={},expiration={}", key, expiration);

        int retryCount = 5;
        boolean isSuccess = Cache.safeSet(key, value, expiration);
        while (!isSuccess && (retryCount--) > 0) {
            isSuccess = Cache.safeSet(key, value, expiration);
            MixHelper.pause(200);
            log.warn("Retry counter {} to set cache key={}", retryCount, key);
        }
        if (!isSuccess) {
            log.error("Set cache failed, key={}", key);
        }
    }

	// 若key中有空格则memcached处理时会认为是非法字符
	public static String processKeyForMemcached(String key) {
		// key中不含空格
		if(!key.contains(StringUtils.SPACE)){
			return key;
		}
		
		// 缓存未使用memcached
		String isMemcached = Config.getProperty("memcached");
		if (StringUtils.isBlank(isMemcached) || "disabled".equals(isMemcached)) {
			return key;
		}
		
		// 移除空格字符
		log.info("Remove whitespace in Key={}", key);
		key = StringUtils.remove(key, StringUtils.SPACE);
		return key;
	}
	
	/**
	 * 
	 * 设置缓存到某个具体的时间点.
	 *
	 * @param key
	 * @param value
	 * @param day
	 * @param time
	 * @since  v0.5.1
	 * @author youblade
	 * @created 2014年1月17日 下午2:29:53
	 */
	//TODO
//	public static void set(String key, Object value, int days, LocalTime time) {
//		
//		long millis = time.getMinuteOfHour() + time.getMillisOfSecond();
//		DateMidnight.now().plusDays(days).plus(millis);
//		
//		String expiration = "";
//		log.info("Set value of cache,key={},expiration={}", key, expiration);
//		
//		Cache.cacheImpl.set(key, value, expiration);
//	}

}
