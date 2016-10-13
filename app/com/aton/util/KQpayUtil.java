package com.aton.util;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.security.KeyStore;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.Signature;
import java.security.cert.CertificateFactory;
import java.security.cert.X509Certificate;
import java.util.Map;

import org.joda.time.DateTime;
import org.joda.time.LocalDate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import play.data.validation.Required;
import play.libs.URLs;

import com.aton.config.Config;
import com.google.common.base.Strings;
import com.google.common.collect.Maps;

import enums.pay.KQpayPlatform;

/**
 * 
 * 快钱支付工具类.
 * 
 * @author youblade
 * @since v1.0
 * @created 2014年11月13日 下午12:03:59
 */
public class KQpayUtil {

    private static final Logger log = LoggerFactory.getLogger(KQpayUtil.class);
    
    static final Pandora PANDORA_INSTANCE = Pandora.newInstance(1, 1);
    public static String createTradeNo() {
        return LocalDate.now().toString(DateUtils.YYYYMMDD) + String.valueOf(PANDORA_INSTANCE.nextId());
    }

    public static String getPaymentUrl(KQpayPlatform platform, String tradeNo, long totalPayFee) {
        //【注意】 参数必须按文档顺序，空值参数不参与签名
        Map<String, String> params = Maps.newLinkedHashMap();
        params.put("inputCharset", "1");// 1 代表 UTF-8
        params.put("bgUrl", Config.APP_URL + "api/pay/notify");
        params.put("version", "v2.0");
        params.put("language", "1");// 1 代表中文显示
        params.put("signType", "4");// 4 代表 DSA 戒者 RSA 签名方式
        
        params.put("merchantAcctId", Config.KQPAY_MERCHANT_ID);
        params.put("orderId", tradeNo);
        params.put("orderAmount", String.valueOf(totalPayFee));
        params.put("orderTime", DateTime.now().toString("yyyyMMddHHmmss"));

        // 银行直连:payType=10,10-1,10-2,14
        params.put("payType", "10-1");
        params.put("bankId", platform.code);

        // 根据参数生成签名
        String signMsg = createSign(params);
//        MixHelper.print("signMsg--------->"+signMsg);
        
        // 使用所有参数构造请求URL
        params.put("signMsg", signMsg);
        String url = getUrl(params, Config.KQPAY_URL);
//        MixHelper.print("url--------->"+url);
        return url;
    }
    
    /**
     * 
     * 获取请求参数（使用UTF-8对参数值进行编码）.
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
            sb.append(k + "=" + URLs.encodePart(v) + "&");
        }
        return url + "?" + sb.deleteCharAt(sb.lastIndexOf("&"));
    }

    public static String createSign(Map<String, String> params) {
        StringBuffer sb = new StringBuffer();
        for (String k : params.keySet()) {
            String v = (String) params.get(k);
            sb.append(k + "=" + v + "&");
//            MixHelper.print("k={}----->v={}", k, v);
        }
        sb.deleteCharAt(sb.length() - 1);
//        MixHelper.print("signMsg Param----->" + sb.toString());
        return signMsg(sb.toString());
    }
    
    /**
     * 
     * 校验签名
     *
     * @param params
     * @return
     * @since  v1.0
     * @author youblade
     * @created 2014年11月14日 下午1:45:54
     */
    public static boolean verifySign(String merchantAcctId, String version, String language, String signType,
        String payType, String bankId, String orderId, String orderTime, long orderAmount, String bindCard,
        String bindMobile, String dealId, String bankDealId, String dealTime, long payAmount, long fee, String ext1,
        String ext2, String payResult, String errCode, String signMsg) {

        Map<String, Object> params = Maps.newLinkedHashMap();
        params.put("merchantAcctId", merchantAcctId);
        params.put("version", version);
        params.put("language", language);
        params.put("signType", signType);
        params.put("payType", payType);
        params.put("bankId", bankId);
        params.put("orderId", orderId);
        params.put("orderTime", orderTime);
        params.put("orderAmount", orderAmount);
        params.put("bindCard", bindCard);
        params.put("bindMobile", bindMobile);
        params.put("dealId", dealId);
        params.put("bankDealId", bankDealId);
        params.put("dealTime", dealTime);
        params.put("payAmount", payAmount);
        params.put("fee", fee);
        params.put("ext1", ext1);
        params.put("ext2", ext2);
        params.put("payResult", payResult);
        params.put("errCode", errCode);

        StringBuffer sb = new StringBuffer();
        for (String k : params.keySet()) {
            Object obj = params.get(k);
            // 空值不参与生成签名
            if (obj == null) {
                continue;
            }
            String v = String.valueOf(obj);
            if (Strings.isNullOrEmpty(v)) {
                continue;
            }
            sb.append(k + "=" + v + "&");
        }
        sb.deleteCharAt(sb.length() - 1);
        return enCodeByCer(sb.toString(), signMsg);
    }

    public static String signMsg(String signMsg) {
        try {
            // 读取密钥仓库
            BufferedInputStream ksbufin = new BufferedInputStream(new FileInputStream(Config.KQPAY_SECRET_PFX));

            // 密钥仓库
            KeyStore ks = KeyStore.getInstance("PKCS12");
            char[] keyPwd = Config.KQPAY_SECRET_PASS.toCharArray();
            ks.load(ksbufin, keyPwd);

            // 从密钥仓库得到私钥
            PrivateKey priK = (PrivateKey) ks.getKey("test-alias", keyPwd);
            Signature signature = Signature.getInstance("SHA1withRSA");
            signature.initSign(priK);
            signature.update(signMsg.getBytes("utf-8"));
            sun.misc.BASE64Encoder encoder = new sun.misc.BASE64Encoder();
            return encoder.encode(signature.sign());
        } catch (Exception e) {
            log.error(e.getMessage(),e);
        }
        return "";
    }

    public static boolean enCodeByCer(String val, String msg) {
        try {
            FileInputStream inStream = new FileInputStream(Config.KQPAY_SECRET_CER);
            CertificateFactory cf = CertificateFactory.getInstance("X.509");
            X509Certificate cert = (X509Certificate) cf.generateCertificate(inStream);
            // 获得公钥
            PublicKey pk = cert.getPublicKey();
            // 签名
            Signature signature = Signature.getInstance("SHA1withRSA");
            signature.initVerify(pk);
            signature.update(val.getBytes());
            // 解码
            sun.misc.BASE64Decoder decoder = new sun.misc.BASE64Decoder();
            return signature.verify(decoder.decodeBuffer(msg));
        } catch (Exception e) {
            log.error(e.getMessage(),e);
        }
        return false;
    }

}
