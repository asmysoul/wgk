package com.aton.util;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.Map;
import java.util.TreeMap;

import models.FlowJsonClickModel;
import models.FlowJsonModels;
import models.TrafficRecord;
import models.TrafficRecord.ShopType;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import play.libs.Codec;

import com.aton.config.Config;
import com.google.gson.Gson;

/**
 * 
 * 
 * 流量任务处理工具类
 * 
 * @author fufei
 * @since v2.0
 * @created 2015年1月16日 下午2:05:26
 */
public class TrafficRecordUtil {
	private static final Logger log = LoggerFactory.getLogger(TrafficRecordUtil.class);

	// public static String FLOW_APPKEY = "test";
	// public static String FLOWSECRET = "a9d11a189099ac9b483ab982e849e939";

	// public static String FLOW_APPKEY = "waguke";
	// public static String FLOWSECRET = "36f987fb3bb3a60b3a1ec64990615f3d";

	/**
	 * 
	 * 获取点击返回点击数
	 * 
	 * @throws IOException
	 * @since v2.0
	 * @author fufei
	 * @created 2015年1月16日 下午7:11:21
	 */
	public static String getclickTimes(String kid, String date) {
		String app_key = Config.FLOW_APP_KEY;// 平台分配的key
		String timestamp = System.currentTimeMillis() / 1000 + "";// 当前时间戳
		Map<String, String> param = new HashMap<String, String>();
		param.put("appkey", app_key);
		param.put("timestamp", timestamp);
		param.put("date", date);
		param.put("id", kid);
		param.put("sign", signFactory(sortMapByKey(param), "statistics/getclicks"));
		try {
			return WebUtils.doGet(getClicksUrl(), param, "UTF-8");
		} catch (IOException e) {
			e.printStackTrace();
		}
		return "";
	}

	/**
	 * 
	 * 添加接口返回字符串转换json
	 * 
	 * @param result
	 * @return
	 * @since v2.0
	 * @author fufei
	 * @created 2015年1月17日 上午11:10:23
	 */
	public static FlowJsonModels formatToJSON(String result) {
		Gson gson = new Gson();
		FlowJsonModels models = gson.fromJson(result, FlowJsonModels.class);
		return models;
	}

	/**
	 * 
	 * 查询点击数接口返回字符串转换json
	 * 
	 * @param result
	 * @return
	 * @since v2.0
	 * @author fufei
	 * @created 2015年1月17日 上午11:10:23
	 */
	public static FlowJsonClickModel formatClickToJSON(String result) {
		Gson gson = new Gson();
		FlowJsonClickModel models = gson.fromJson(result, FlowJsonClickModel.class);
		return models;
	}

	/**
	 * 
	 * 调用接口方法
	 * 
	 * @param record
	 * @param method
	 * @return
	 * @since v2.0
	 * @author fufei
	 * @created 2015年1月16日 下午4:10:15
	 */
	public static String flow(TrafficRecord record, Method method) {
		if (getShopType(record.type.toString()) == null) {
			log.info("add flow shopType id null");
			return "";
		}
		String url = getUrl(getShopType(record.type.toString()), method);
		log.info("url:"+url);
		Map params = getParamsMap(record, method);
		params.put("sign", sign(params, getShopType(record.type.toString()), method));
		try {
			return WebUtils.doGet(url, params, "UTF-8");
		} catch (IOException e) {
		    log.info("url is null ,throw IOException");
			e.printStackTrace();
		}
		
		return "";
	}

	/**
	 * 
	 * 将字符串转换成枚举类型
	 * 
	 * @param shopType
	 * @return
	 * @since v2.0
	 * @author fufei
	 * @created 2015年1月16日 下午4:21:50
	 */
	public static ShopType getShopType(String shopType) {
		if ("TAOBAOMOBILE".equals(shopType)) {
			return ShopType.TAOBAOMOBILE;
		} else if ("TAOBAOPC".equals(shopType)) {
			return ShopType.TAOBAOPC;
		} else if ("JDPC".equals(shopType)) {
			return ShopType.JDPC;
		}else if ("TBAD".equals(shopType)) {
			return ShopType.TBAD;
		}
		return null;
	}

	/**
	 * 
	 * 获取参数集合
	 * 
	 * @param record
	 * @return
	 * @since v2.0
	 * @author fufei
	 * @created 2015年1月16日 下午4:01:12
	 */
	public static Map<String, String> getParamsMap(TrafficRecord record, Method method) {

		String appkey = Config.FLOW_APP_KEY;// 平台分配的key
		String nid = record.nid + "";// 淘宝商品ID
		String kwd = record.kwd;
		String shop_type = record.shopType;// 店铺类型 b或者c
		String begin_time = record.beginTime;
		// 点击开始日期
		String end_time = record.endTime;// 点击结束日期
		String click_end = record.clickEnd + "";// 每天点击停止时间(整点)
		String click_start = record.clickStart + "";// 每天开始点击时间(整点)
		String path1 = record.path1 + "";// 淘宝搜索路径占比, 2路径之和为100
		String path2 = record.path2 + "";
		// 淘宝搜索天猫路径占比, c店填0
		String path3 = record.path3 + "";
		String sleep_time = record.sleepTime + "";// 商品详情页停留时间(秒)
		String times = record.times + "";// 日点击次数
		String kid = record.kid + "";
		String timestamp = System.currentTimeMillis() / 1000 + "";// 当前时间戳

		Map<String, String> map = new HashMap<String, String>();
		map.put("appkey", appkey);
		map.put("nid", nid);
		map.put("kwd", kwd);
		map.put("kid", kid);
		map.put("shop_type", shop_type);
		map.put("begin_time", begin_time);
		map.put("end_time", end_time);
		map.put("click_end", click_end);
		map.put("click_start", click_start);
		map.put("path1", path1);
		map.put("path2", path2);
		map.put("path3", path3);
		map.put("sleep_time", sleep_time);
		map.put("times", times);
		map.put("timestamp", timestamp);
		return sortMapByKey(map);
	}

	/**
	 * 
	 * 生成签名
	 * 
	 * @param map
	 * @param type
	 * @param appsecret
	 * @return
	 * @since v2.0
	 * @author fufei
	 * @created 2015年1月16日 下午3:29:43
	 */
	public static String sign(Map<String, String> map, ShopType type, Method method) {
		return signFactory(map, apiMethod(type, method));
	}

	/**
	 * 
	 * 
	 * 流量接口方法
	 * 
	 * @author fufei
	 * @since v2.0
	 * @created 2015年1月16日 下午3:51:31
	 */
	public enum Method {
		ADD("添加"), MODIFY("修改");
		public String name;

		private Method(String title) {
			this.name = title;
		}
	}

	/**
	 * 
	 * 签名
	 * 
	 * @param map
	 *            参数
	 * @param method
	 *            调用方法
	 * @return
	 * @since v2.0
	 * @author fufei
	 * @throws UnsupportedEncodingException
	 * @created 2015年1月16日 下午3:16:52
	 */
	public static String signFactory(Map<String, String> map, String method) {
		String sign = "";
		for (Map.Entry<String, String> entry : map.entrySet()) {
			sign = sign + entry.getValue();
		}
		sign = Codec.hexMD5(method + sign + Codec.hexMD5(Config.FLOW_APP_SECRET));
		return sign;
	}

	/**
	 * 
	 * 获取调用方法名称
	 * 
	 * @param type
	 * @param method
	 * @return
	 * @since v2.0
	 * @author fufei
	 * @created 2015年1月16日 下午3:41:39
	 */
	public static String apiMethod(ShopType type, Method method) {
		String methodName = "";
		if (type == ShopType.TAOBAOMOBILE) {
			if (method == Method.ADD) {
				methodName = "tbmobi/add";
			} else if (method == Method.MODIFY) {
				methodName = "tbmobi/modify";
			}
		} else if (type == ShopType.TAOBAOPC) {
			if (method == Method.ADD) {
				methodName = "tbpc/add";
			} else if (method == Method.MODIFY) {
				methodName = "tbpc/modify";
			}
		} else if (type == ShopType.JDPC) {
			if (method == Method.ADD) {
				methodName = "jdpc/add";
			} else if (method == Method.MODIFY) {
				methodName = "jdpc/modify";
			}
		}else if (type == ShopType.TBAD) {
			if (method == Method.ADD) {
				methodName = "tbad/add";
			} else if (method == Method.MODIFY) {
				methodName = "tbad/modify";
			}
		}
		return methodName;
	}

	/**
	 * 获取url
	 * 
	 * @param ShopTye
	 * @param type
	 * @return
	 * @since v2.0
	 * @author fufei
	 * @created 2015年1月16日 下午2:57:31
	 */
	public static String getUrl(ShopType type, Method method) {
		String methodName = apiMethod(type, method);
		if (!"".equals(method)) {
			return Config.FLOW_API_URL + methodName;
		}
		return "";
	}

	/**
	 * 获取点击次数url
	 * 
	 * @param ShopTye
	 * @param type
	 * @return
	 * @since v2.0
	 * @author fufei
	 * @created 2015年1月16日 下午2:57:31
	 */
	public static String getClicksUrl() {
		return Config.FLOW_API_URL + "statistics/getclicks";
	}

	/**
	 * 使用 Map按key进行排序
	 * 
	 * @param map
	 * @return
	 */
	public static Map<String, String> sortMapByKey(Map<String, String> map) {
		if (map == null || map.isEmpty()) {
			return null;
		}
		Map<String, String> sortMap = new TreeMap<String, String>(new MapKeyComparatorUtil());
		sortMap.putAll(map);

		return sortMap;
	}

}
