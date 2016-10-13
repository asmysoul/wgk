package com.aton.util;

import java.util.concurrent.TimeUnit;

import org.joda.time.DateTime;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import play.libs.Time;

import com.aton.config.AppMode;
import com.aton.config.AppMode.Mode;
import com.aton.config.CacheType;
import com.qiniu.api.auth.digest.Mac;
import com.qiniu.api.config.Config;
import com.qiniu.api.rs.PutPolicy;

public class QnCloudUtil {
    
    public static final Logger log = LoggerFactory.getLogger(QnCloudUtil.class);

    static {
        Config.ACCESS_KEY = com.aton.config.Config.QN_ACCESS_KEY;
        Config.SECRET_KEY = com.aton.config.Config.QN_SECRET_KEY;
    }

    /**
     * 
     * 七牛图片空间.
     * 
     * @author youblade
     * @since  0.1
     * @created 2014年10月1日 下午2:05:43
     */
    public enum QnFileBucket {
        //@formatter:off
        /** 正式空间：任务相关图片 */
        ONLINE_PUBLIC("jzniu-public"), 
        /** 正式空间：公告相关图片 */
        ONLINE_NOTICE_PUBLIC("jzniu-notice-public"), 
        DEV("jzniu-dev"), 
        TEST("jzniu-test");
        //@formatter:on

        public String code;

        private QnFileBucket(String code) {
            this.code = code;
        }
    }
    
    /**
     * 
     * 生成默认的上传token.
     *
     * @return
     * @since  0.1
     * @author youblade
     * @created 2014年8月22日 下午5:28:51
     */
    public static String generateUploadToken() {
        if(AppMode.get().mode == Mode.TEST){
            return generateUploadToken(QnFileBucket.TEST);
        }
        if(AppMode.get().mode == Mode.DEV){
            return generateUploadToken(QnFileBucket.DEV);
        }
        return generateUploadToken(QnFileBucket.ONLINE_PUBLIC);
    }
    
    public static String generateUploadToken(QnFileBucket bucket) {
        /*
         * uptoken的过期时间为：其在本系统中的缓存过期时间+3分钟（避免时间误差）
         */
        int seconds = Time.parseDuration(CacheType.FILE_UPTOKEN.expiredTime);
        long millis = DateTime.now().plusSeconds(seconds).plusMinutes(3).getMillis();
        
        PutPolicy putPolicy = new PutPolicy(bucket.code);
        putPolicy.expires = TimeUnit.MILLISECONDS.toSeconds(millis);
        
        putPolicy.insertOnly = 1;
        //        putPolicy.returnBody
        //TODO 自定义上传文件名
        //        putPolicy.saveKey
        
        // 最大文件限制：2MB
        putPolicy.fsizeLimit = 2 * 1024 * 1024;
        // 只允许上传图片类型
        putPolicy.mimeLimit = "image/*";
        try {
            return putPolicy.token(new Mac(Config.ACCESS_KEY, Config.SECRET_KEY));
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        }
        return StringUtils.EMPTY;
    }
}
