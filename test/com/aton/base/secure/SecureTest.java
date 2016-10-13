package com.aton.base.secure;

import org.junit.Test;

import play.mvc.Http;
import play.mvc.Http.Cookie;
import play.mvc.Http.Header;
import play.mvc.Http.Request;
import play.mvc.Http.Response;
import play.mvc.Scope.Session;

import com.aton.test.BaseAppTest;
import com.aton.util.MixHelper;

public class SecureTest extends BaseAppTest {

	@Test
	public void test() {
		
		/*
		 *  本地模拟普通用户登录
		 */
		// 未登录时无法访问受限页面（个人中心）
		Response response = GET("/user");
		assertStatus(Http.StatusCode.FOUND, response);
		
		// 模拟登录
		response = GET("/admin/getcode");
		assertResultIsOk(response);
		
		// 登录后可正常访问受限页面（个人中心）
		response = GET("/user");
		assertIsOk(response);
		
		/*
		 *  TODO 线上模拟普通用户登录
		 */
//		Request request = Request.createRequest(null, "GET", "/", "", null, null, null, null, false, 80, "192.168.1.1", false, null, null);
//		response = GET(request,"/admin");
		
		/*
		 *  管理员（客服）登录
		 */
		// 未登录时无法访问受限页面（管理界面首页）
		response = GET("/admin");
		assertIsOk(response);
		assertEquals("FUCK", response.out.toString());
		
		// 管理员登录
		response = POST("/admin/doLogin?usr=rltadmin&pass=@rlt.com");
		// 登录成功跳转到首页
		assertStatus(Http.StatusCode.FOUND, response);
		Header header = response.headers.get("Location");
		assertEquals("/admin",header.value());
		
		response = GET("/admin");
		assertIsOk(response);
		assertFalse("FUCK".equals(response.out.toString()));
		
		// TODO 普通用户登录
	}

}
