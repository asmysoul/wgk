package com.aton.util;

import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.text.MessageFormat;

import play.libs.WS;

import com.aton.config.AppMode;
import com.aton.config.AppMode.Mode;
import com.aton.util.QnCloudUtil.QnFileBucket;
import com.qiniu.api.io.PutRet;
import com.qiniu.api.resumableio.ResumeableIoApi;

/**
 * 
 * 
 * 上传图片到七牛
 * 
 * @author moloch
 * @since v0.1
 * @created 2014-8-28 下午3:54:01
 */
public class UploadImgUtil {

	

	/**
	 * 
	 * 通过图片原url上传至七牛并返回七牛上该图片地址
	 * 
	 * @param url
	 * @return
	 * @throws IOException
	 * @since v0.1
	 * @author moloch
	 * @created 2014-8-28 下午4:02:30
	 */
	public static String uploadImg(String url) throws IOException {

		// 获取图片源数据流
		InputStream inputStream = WS.url(url).get().getStream();
		
		// 图片地址url头部
		String DOMAIN = "http://jzniu-{0}.jzniu.cn/";

		// 获取upToken
		String uptoken = QnCloudUtil.generateUploadToken();
        if (AppMode.get().mode == Mode.TEST) {
            DOMAIN = MessageFormat.format(DOMAIN, "test");
        } else if (AppMode.get().mode == Mode.DEV) {
            DOMAIN = MessageFormat.format(DOMAIN, "dev");
        } else {
            DOMAIN = MessageFormat.format(DOMAIN, "public");
        }
		
		// 上传
		PutRet ret = ResumeableIoApi.put(inputStream, uptoken, null, null);
		inputStream.close();
		
		return DOMAIN + ret.getKey();
	}
}
