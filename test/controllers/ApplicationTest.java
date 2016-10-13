package controllers;

import static org.junit.Assert.*;

import org.junit.Test;

import com.aton.test.BaseAppTest;

import play.mvc.Http.Response;
import play.test.FunctionalTest;


public class ApplicationTest extends BaseAppTest {

    @Test
    public void test_fetIndexData() {
        
        Response response = GET("/index/data");
        assertResultIsOk(response);
        
    }

}
