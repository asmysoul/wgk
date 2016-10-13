package models;

import static org.junit.Assert.*;

import org.junit.Test;

import play.db.DB;
import play.test.UnitTest;


public class UserTest extends UnitTest {

    @Test
    public void test_findByNick() {
        User user = User.findByNick("test");
        assertNull(user);
    }
    
    @Test
    public void test_update(){
    	User user = new User();
    	user.id=2;
    	user.email ="sdjfl@jls.cld";
    	user.qq = "999999999";
    	user.mobile = "1456232323";
    	user.password = "123456789";
    	user.payPassword = "123456789";
    	user.save(false);
    	
    	User u = User.findById(2L);
    	assertTrue(user.qq.equals(u.qq));
    }
    
    @Test
    public void test_updatePass(){
    	User.updatePass(7L, "tangyiran");
    	User user = User.findById(7L);
    	assertTrue(user.validate("tangyiran"));
    }

}
