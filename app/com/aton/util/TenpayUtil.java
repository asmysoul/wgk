package com.aton.util;

import java.util.Map;
import java.util.TreeMap;
import java.util.concurrent.TimeUnit;

import models.TenpayTradeLog;
import models.TenpayTradeLog.TradeType;

import org.joda.time.LocalDate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import play.libs.Codec;
import play.libs.URLs;
import play.libs.WS;
import play.libs.WS.HttpResponse;

import com.aton.Constant;
import com.aton.config.AppMode;
import com.aton.config.Config;
import com.google.common.collect.Maps;

import enums.TenpayPlatform;

/**
 * 
 * 财付通支付工具类.
 * 
 * @author youblade
 * @since 0.1
 * @created 2014年9月10日 下午7:13:04
 */
@Deprecated
public class TenpayUtil {

    public static final Logger tenpayLog = LoggerFactory.getLogger(TenpayTradeLog.class);

    static final Pandora PANDORA_INSTANCE = Pandora.newInstance(1, 1);
    
    /**
     * 
     * 生成财付通支付网关所需参数：外部商户交易号.
     * 
     * @return
     * @since 0.1
     * @author youblade
     * @created 2014年9月17日 下午5:50:25
     */
    public static String createOutTradeNo() {
        // yyyyMMdd + 19位唯一序列号
        return LocalDate.now().toString(DateUtils.YYYYMMDD) + String.valueOf(PANDORA_INSTANCE.nextId());
    }
    
    /**
     * 
     * 获取财付通付款页面URL.
     *
     * @param platform
     * @param outTradeNo
     * @param itemMemo
     * @param totalPayFee
     * @param clientIp
     * @return
     * @since  0.1
     * @author youblade
     * @created 2014年9月15日 下午1:35:15
     */
    public static String getTenpayPageUrl(TenpayPlatform platform, String outTradeNo, String itemMemo, long totalPayFee,
        String clientIp) {
        Map<String, String> params = Maps.newHashMap();
        params.put("bank_type", platform.code);
        params.put("body", itemMemo);
        params.put("out_trade_no", outTradeNo);
        params.put("total_fee", String.valueOf(totalPayFee));
        
        params.put("input_charset", Constant.UTF_8);
        params.put("partner", Config.TENPAY_PARTNER);
        // 支付成功后返回我的任务页面
        params.put("return_url", Config.APP_URL + "api/tenpay/result/" + TradeType.TASK);
        params.put("notify_url", Config.APP_URL + "api/tenpay/notify");
        params.put("spbill_create_ip", clientIp);
        
        // 根据参数生成签名
        String sign = TenpayUtil.createSign(params);

        // 使用所有参数构造请求URL
        params.put("sign", sign);
        String url = TenpayUtil.getUrl(params, Config.TENPAY_URL);
        return url;
    }
    
    /**
     * 
     * 按照财付通要求的规则创建签名.
     * 
     * @param params
     * @return
     * @since 0.1
     * @author youblade
     * @created 2014年9月10日 下午7:12:43
     */
    public static String createSign(Map<String, String> params) {
        Map<String, Object> parameters = new TreeMap<String, Object>(params);
        StringBuffer sb = new StringBuffer();
        for (String k : parameters.keySet()) {
            String v = (String) parameters.get(k);
            if (!"sign".equals(k) && null != v && !"".equals(v)) {
                sb.append(k + "=" + v + "&");
            }
        }
        // 密钥为最后一个参数
        sb.append("key=" + Config.TENPAY_KEY);
        return Codec.hexMD5(sb.toString()).toUpperCase();
    }

    /**
     * 
     * 检查请求参数中签名.
     *
     * @param params
     * @return
     * @since  0.1
     * @author youblade
     * @created 2014年9月15日 下午5:20:41
     */
    public static boolean isTenpaySign(Map<String, String> params) {
        String sign = createSign(params);
        String tenpaySign = params.get("sign").toUpperCase();
        return StringUtils.equals(sign, tenpaySign);
    }

    /**
     * 
     * 获取请求参数.
     * 
     * @param params
     * @param url
     * @return
     * @since 0.1
     * @author youblade
     * @created 2014年9月10日 下午8:00:26
     */
    public static String getUrl(Map<String, String> params, String url) {
        StringBuffer sb = new StringBuffer();
        for (String k : params.keySet()) {
            String v = (String) params.get(k);
            // 默认使用UTF-8进行编码
            sb.append(k + "=" + URLs.encodePart(v) + "&");
        }

        String reqPars = sb.substring(0, sb.lastIndexOf("&"));
        return url + "?" + reqPars;
    }
    
    /**
     * 
     * 验证notify_id：确保收到的支付消息是合法的.
     *
     * @param notify_id
     * @return
     * @since  0.1
     * @author youblade
     * @created 2014年9月12日 下午2:56:58
     */
    public static boolean verifyNotifyId(String notify_id) {
        Map<String, String> params = Maps.newHashMap();
        params.put("input_charset", Constant.UTF_8);
        params.put("notify_id", notify_id);
        params.put("partner", Config.TENPAY_PARTNER);

        // 根据参数生成签名
        String sign = TenpayUtil.createSign(params);

        // 使用所有参数构造请求URL
        params.put("sign", sign);
        String url = TenpayUtil.getUrl(params, Config.TENPAY_VERIFY_URL);

        // 开发时模拟tenpay平台接口
        if (AppMode.get().mockTenpay) {
            url = Config.APP_URL + "mock/tenpay/verifyNofifyId";
        }

        // 若发生异常则重试，最多5次
        for (int i = 1; i <= 5; i++) {
            String responseXml = "";
            try {
                HttpResponse response = WS.url(url).postAsync().get(10, TimeUnit.SECONDS);
                tenpayLog.debug("Verify Return Status====> {}", response.getStatusText());

                //TODO 确认验证成功后更新交易记录
                Document xml = response.getXml(Constant.UTF_8);
                Element rootNode = (Element) xml.getFirstChild();

                responseXml = rootNode.getTextContent();

                // 验证成功
                String retcode = rootNode.getElementsByTagName("retcode").item(0).getTextContent();
                if ("0".equals(retcode)) {
                    return true;
                }
                //通知ID超时
                if ("88222005".equals(retcode)) {
                    tenpayLog.error("notify_id experied===>{}", responseXml);
                }
                
                // 验证失败
                return false;
            } catch (Exception e) {
                tenpayLog.error(e.getMessage(), e);
                tenpayLog.error("===>{}", responseXml);
            }

            // 暂停1s再重试
            MixHelper.pause(TimeUnit.SECONDS, 1);
        }
        
        return false;
    }
}
