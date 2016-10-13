package com.aton.util;

import java.io.IOException;
import java.util.Collections;
import java.util.Map;

import org.apache.commons.collections.map.HashedMap;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import sun.net.www.http.HttpClient;

import com.aton.Constant;

/**
 * 
 * 
 * 短信网关
 * 
 * @author tr0j4n
 * @since v0.3.5
 * @created 2013-11-2 下午1:44:04
 */
public class SmsUtil111 {

	private static final Logger log = LoggerFactory.getLogger(SmsUtil111.class);
	// 发送参数
	private static Map<String, String> paramTemplate = new HashedMap();

	static {
		paramTemplate.put("Uid", "wwnice");
		paramTemplate.put("Key", "b76054eaae8df79e8938");
		// 只读
		paramTemplate = Collections.unmodifiableMap(paramTemplate);
	}
	/**
	 * 
	 * TODO 发送短信
	 *
	 * @param phoneNumber  电话号码
	 * @param messageContent 短信内容
	 * @return
	 * @since  v2.0
	 * @author fufei
	 * @created 2015年1月26日 下午4:46:15
	 */
	public static boolean send(String phoneNumber, String messageContent) {
		Map<String, String> params = new HashedMap();
		params.putAll(paramTemplate);
		params.put("smsMob", phoneNumber);
		params.put("smsText", messageContent);
		String queryParamData = StringUtils.EMPTY;
		for (String key : params.keySet()) {
			queryParamData = queryParamData + key + "=" + params.get(key) + "&";
		}

		String retText = StringUtils.EMPTY;
		try {
			String postUrl = "http://gbk.sms.webchinese.cn";
			retText = WebUtils._doPost(postUrl, "application/x-www-form-urlencoded", queryParamData.getBytes(Constant.GBK), 5000, 2000);
			if (Integer.parseInt(retText) <= 0) {
				log.error("短信发送失败，返回:{}", retText);
				return false;
			}
		} catch (Exception e) {
			log.error("短信发送出现错误，返回:" + retText + "  错误信息:" + e.getMessage(), e);
			return false;
		}
		return true;
	}

}
