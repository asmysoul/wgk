package notifiers;

import static org.junit.Assert.*;
import models.User;

import org.junit.Test;

import com.aton.test.UnitTest;


public class MailsTest extends UnitTest {

    @Test
    public void test_validRegist() {
        User user = User.findByNick("0001");
        Mails.validRegist(user);
    }
    
    @Test
    public void test_resetPassword() {
        User user = User.findByNick("0001");
        Mails.resetPassword(user);
    }

}
