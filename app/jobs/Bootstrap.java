package jobs;

import models.Region;
import models.SysConfig;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import play.jobs.OnApplicationStart;

import com.aton.config.AppMode;
import com.aton.job.BaseJob;

@OnApplicationStart
public class Bootstrap extends BaseJob {

    private static final Logger log = LoggerFactory.getLogger(Bootstrap.class);

    public void doJob() throws Exception {
        log.info("=====App Started=====");
        
        // 初始导入地区数据
        if(AppMode.get().isOnline()){
            Region.init();
        }
        SysConfig.initCache();
    }

}
