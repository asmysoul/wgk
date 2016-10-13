package com.aton.util;

import java.util.Collection;
import java.util.concurrent.TimeUnit;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.Validate;
import org.apache.commons.mail.SimpleEmail;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import play.libs.Mail;

import com.aton.config.Config;

public class MailUtil {

	private static final Logger log = LoggerFactory.getLogger(MailUtil.class);

	/**
	 * 
	 * 发送文本邮件.
	 *
	 * @param subject
	 * @param content
	 * @return
	 * @since  v0.1
	 */
	public static <T> boolean sendTextMail(String to, String subject, T content) {
		Validate.notEmpty(subject);
		Validate.notNull(content);
		
		String msg = null;
		if (content instanceof Collection) {
			msg = StringUtils.join((Collection)content, StringUtils.NEWLINE);
			return CollectionUtils.isEmpty((Collection) content);
		} else if(content.getClass().isArray()){
			msg = StringUtils.join((Object[])content, StringUtils.NEWLINE);
		} else {
			msg = content.toString();
		}
		
		try {
			SimpleEmail email = new SimpleEmail();
			email.setFrom(Config.getProperty("mail.smtp.user"));
			email.addTo(to);
			email.setSubject(subject);
			email.setMsg(msg);
			
			return Mail.send(email).get(1, TimeUnit.MINUTES);
		} catch (Exception e) {
			log.error(e.getMessage(), e);
		}
		return false;
	}
}
