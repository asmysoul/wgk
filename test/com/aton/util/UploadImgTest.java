package com.aton.util;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;

import org.junit.Test;

import play.libs.WS;

import com.alibaba.fastjson.JSON;
import com.aton.test.UnitTest;
import com.qiniu.api.io.PutRet;
import com.qiniu.api.resumableio.ResumeableIoApi;

public class UploadImgTest extends UnitTest{
	@Test
	public void testUpload() throws IOException{
		String imgurl = "http://img03.taobaocdn.com/bao/uploaded/i3/TB1oJXQFVXXXXcLXVXXXXXXXXXX_!!0-item_pic.jpg_400x400.jpg";
		
		InputStream inputStream = WS.url(imgurl).get().getStream();
		String uptoken = QnCloudUtil.generateUploadToken();
		
		PutRet ret = ResumeableIoApi.put(inputStream, uptoken, null, null);
		inputStream.close();
		assertNotNull(ret);
		String key = ret.getKey();
		String imagUrl = "http://heatall-public.qiniudn.com/"+key;
		MixHelper.print(imagUrl);
	}
}
