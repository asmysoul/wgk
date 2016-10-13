package com.aton.job;

import org.h2.engine.User;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import play.exceptions.JavaExecutionException;
import play.jobs.Job;

import com.aton.config.AppMode;

/**
 * Job基类，当应用设置的mode中关闭 了Job开关，则继承此类的定时Job不会启动.
 * 
 * @author youblade
 */
public class BaseJob<V> extends Job<V> {
	
	private static final Logger log = LoggerFactory.getLogger(BaseJob.class);
	
	/** 是否手工触发任务的标记  */
	protected boolean isManualTrigger;
	
	@Override
	public void before() {
		if(AppMode.get().disableJob && !isManualTrigger){
    		log.debug("----job skipped------");
    		throw new JobDisabledException();
    	}
		super.before();
	}

    @Override
    public void onException(Throwable e) {
    	if(e instanceof JobDisabledException){
    		// Ignored
    		return;
    	}

    	// 用户的sessionKey失效后，就将其登录状态置为“无效”
        if(e instanceof JavaExecutionException){
            Throwable cause = e.getCause();
//            if(cause != null && cause instanceof SessionInvalidException){
//                SessionInvalidException exception = (SessionInvalidException)cause;
//                User.setUserInvalid(exception.session);
//                return;
//            }
        }
    	super.onException(e);
    }
}
