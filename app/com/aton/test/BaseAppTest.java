package com.aton.test;

import models.User.UserType;

import org.apache.commons.lang.Validate;

import play.mvc.Http.Response;
import play.test.FunctionalTest;

import com.alibaba.fastjson.JSON;
import com.aton.config.ReturnCode;
import com.aton.util.JsonUtil;
import com.aton.util.MixHelper;
import com.aton.vo.AjaxResult;

public class BaseAppTest extends FunctionalTest {

	/**
	 * 为用户生成可通过登录验证的authcode.
	 *
	 * @param nick
	 * @return
	 * @author youblade
	 */
    public void getAuthcode(UserType type, String nick) {
        Validate.notNull(type);
        GET("/admin/getcode/" + type.toString() + "?usr=admin&pass=admin123&nick=" + nick);
    }

	public enum Method {
		GET, POST
	}

	/**
	 * Asserts app return status code.<br>
	 * 
	 *	注意，同时包含了以下验证语句：
	 * 	assertIsOk(response);
	 * 
	 * @param code	:biz ReturnCode,not HttpStatus code
	 * @param response
	 */
	public static void assertReturnCode(int code, Response response) {
		assertIsOk(response);

		String json = response.out.toString();

		AjaxResult result = JsonUtil.toBean(json, AjaxResult.class);
		assertEquals(code, result.code);
	}

	/**
	 * 验证返回的数据结果中code是否为200.<br>
	 * 
	 * 	注意，同时包含了以下验证语句：
	 * 	assertIsOk(response);
	 * 
	 * @param response
	 * @author youblade
	 */
	public static void assertResultIsOk(Response response) {
		assertReturnCode(ReturnCode.OK, response);
	}

	/**
	 * 验证返回的结果中数据集是否为空.
	 * 
	 * 注意，同时包含了以下验证语句：
	 * 	assertIsOk(response);
	 * 	assertResultIsOk(response);
	 * 
	 * @param response
	 * @author youblade
	 */
	public static void assertResultsNotEmpty(Response response) {
		assertResultIsOk(response);
		
		AjaxResult result = JsonUtil.toBean(response.out.toString(), AjaxResult.class);
		assertTrue(MixHelper.isNotEmpty(result.results));
	}
	
	/**
	 * 
	 * 验证返回的结果中有多少条数据.<br>
	 * 
	 * 注意，同时包含了以下验证语句：
	 * 	assertIsOk(response);
	 * 	assertResultIsOk(response);
	 *
	 * @param response
	 * @param expected
	 * @since  v0.7.5
	 * @author youblade
	 * @created 2014年6月20日 下午7:21:03
	 */
	public static void assertResultsDataCount(Response response, int expected) {
		assertIsOk(response);
		assertResultIsOk(response);
		
		int count = JSON.parseObject(response.out.toString()).getJSONArray("results").size();
		assertEquals(expected , count);
	}
	
	/**
	 * 
	 * 设置买手当前正在做的任务.
	 *
	 * @param buyerTaskId
	 * @since  0.1
	 * @author youblade
	 * @created 2014年10月11日 下午7:34:45
	 */
    public void setBuyerTask(long buyerTaskId) {
        GET("/admin/setBuyerTask/" + buyerTaskId);
    }
}
