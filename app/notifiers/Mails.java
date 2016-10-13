package notifiers;

import java.text.MessageFormat;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.aton.config.Config;
import com.aton.util.MixHelper;

import controllers.UserAuthentication;
import models.User;
import play.mvc.Mailer;

/**
 * 
 * 邮件通知.
 * 
 * @author youblade
 * @since 0.1
 * @created 2014年7月18日 上午11:22:45
 */
public class Mails extends Mailer {

    private static final Logger log = LoggerFactory.getLogger(Mails.class);
    
    private static final String FROM = MessageFormat.format("【兼职牛】 <{0}>", Config.getProperty("mail.smtp.user"));
    
    /**
     * 
     * 发送验证码到用户填写的注册邮箱.
     * 
     * @param user
     * @since 0.1
     * @author youblade
     * @created 2014年7月18日 上午11:23:27
     */
    public static void validRegist(User user) {
        try {
            setSubject("【兼职牛】验证你的Email并激活注册帐号", user.nick);
            setFrom(FROM);
            addRecipient(user.email);
            send(user);
        } catch (Exception e) {
            log.error("User={} send mail to={} failed:{}", user.nick, user.email, e.getMessage());
        }
    }

    /**
     * 
     * 发送用户找回密码的邮件.
     * 
     * @param user
     * @since 0.1
     * @author youblade
     * @created 2014年7月18日 上午11:24:04
     */
    public static void resetPassword(User user) {
        setSubject("【兼职牛】找回你的登录密码");
        setFrom(FROM);
        addRecipient(user.email);
        send(user);
    }
}
