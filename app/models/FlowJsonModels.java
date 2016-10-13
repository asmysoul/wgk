package models;

import javax.sound.sampled.AudioFormat.Encoding;

import org.bouncycastle.util.encoders.UrlBase64;
import org.bouncycastle.util.encoders.UrlBase64Encoder;

/**
 * 
 * 
 * 解析流量返回json字符串model
 * 
 * @author fufei
 * @since  v2.0
 * @created 2015年1月17日 上午11:01:05
 */
public class FlowJsonModels {

	public String status;
	public FlowJsonData data;
	public class FlowJsonData{
		public String kwd;
		public String nid;
		public String id;
	}
}
