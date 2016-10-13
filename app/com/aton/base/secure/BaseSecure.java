package com.aton.base.secure;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import play.mvc.Before;

import com.aton.base.BaseController;
import com.aton.config.AppMode;

/**
 * 
 * 基础安全校验过滤器<br>
 * 
 * 1、CSRF攻击防御.<br>
 * 2、.<br>
 * 
 * @priority = 0
 * @author youblade
 * @since v0.1
 */
public class BaseSecure extends BaseController {

	private static final Logger log = LoggerFactory.getLogger(BaseSecure.class);

	/**
	 * 
	 * 防御CSRF攻击（所有的POST请求必须带上token）
	 *
	 * @since  0.1
	 * @author youblade
	 * @created 2014年8月5日 下午1:41:51
	 */
    @Before(priority = 0)
    public static void checkAuthenticityToken() {
    	
		// 测试脚本跳过校验
		if (AppMode.Mode.TEST == AppMode.get().mode) {
			return;
		}
        if ("POST".equalsIgnoreCase(request.method)) {
            checkAuthenticity();
        }
    }
}
