package controllers.base;

import org.junit.Test;

import play.mvc.Http.Response;

import com.aton.test.BaseAppTest;


public class MockControllerTest extends BaseAppTest {

    @Test
    public void testGetAuthcode() {
        
        Response response = GET("/admin/getcode");
        assertResultIsOk(response);
    }

}
