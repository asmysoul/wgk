package controllers;

import static org.junit.Assert.*;

import org.junit.Test;

import play.mvc.Http.Response;

import com.aton.config.CacheType;
import com.aton.test.BaseAppTest;
import com.aton.util.CacheUtil;
import com.aton.util.MixHelper;


/**
 * TODO Comment.
 * 
 * @author youblade
 * @since  0.1
 * @created 2014年9月27日 下午2:29:20
 */
public class UserAuthenticationTest extends BaseAppTest {

    /**
     * Test method for {@link controllers.UserAuthentication#sendSmsValidCode(java.lang.String)}.
     */
    @Test
    public void testSendSmsValidCode() {
        String nick = "test";
        String url = MixHelper.format("/regist/sendSms?user.nick={}&user.mobile={}", nick,"18922326039");
        Response response = POST(url);
        assertResultIsOk(response);
        
        String validCode = CacheUtil.get(CacheType.SMS_VALID_CODE.getKey(nick));
        assertTrue(MixHelper.isNotEmpty(validCode));
        assertEquals(6, validCode.length());
    }
    
    /**
     * Test method for {@link controllers.UserAuthentication#checkRegist(models.User, java.lang.String)}.
     */
    @Test
    public void testCheckRegist() {
        int code = 0;
        String url = MixHelper.format("/checkRegist?user.nick={}&smsValidCode={}", "test",code);
        Response response = GET(url);
        assertIsOk(response);
        assertEquals(Boolean.FALSE.toString().toLowerCase(), response.out.toString());
    }


    /**
     * Test method for {@link controllers.UserAuthentication#doRegist(models.User)}.
     */
    @Test
    public void testDoRegist() {
        fail("Not yet implemented");
    }

}
