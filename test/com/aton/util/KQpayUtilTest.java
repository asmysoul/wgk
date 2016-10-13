package com.aton.util;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import org.joda.time.DateTime;
import org.junit.Test;

import play.libs.WS;

import com.aton.test.UnitTest;

import enums.pay.KQpayPlatform;

public class KQpayUtilTest extends UnitTest {

    @Test
    public void testGetPaymentUrl() throws InterruptedException, ExecutionException, TimeoutException {

        String url = KQpayUtil.getPaymentUrl(KQpayPlatform.ABC, "0000", 1);

        MixHelper.print("");
        MixHelper.print("");
        MixHelper.print("");
        MixHelper.print("");
        MixHelper.print("");
        MixHelper.print("");
        String result = WS.url(url).getAsync().get(1, TimeUnit.SECONDS).getString();
        MixHelper.print(result);

    }

    public String appendParam(String returns, String paramId, String paramValue) {
        if (returns != "") {
            if (paramValue != "") {
                returns += "&" + paramId + "=" + paramValue;
            }
        } else {
            if (paramValue != "") {
                returns = paramId + "=" + paramValue;
            }
        }
        return returns;
    }

    @Test
    public void test() {
        String signMsgVal = "";
        signMsgVal = appendParam(signMsgVal, "inputCharset", "1");
        signMsgVal = appendParam(signMsgVal, "pageUrl", "1");
        signMsgVal = appendParam(signMsgVal, "bgUrl", "http://219.233.173.50:8801/RMBPORT/receive.jsp");
        signMsgVal = appendParam(signMsgVal, "version", "v2.0");
        signMsgVal = appendParam(signMsgVal, "language", "1");
        signMsgVal = appendParam(signMsgVal, "signType", "4");
        signMsgVal = appendParam(signMsgVal, "merchantAcctId", "1002404770501");
        signMsgVal = appendParam(signMsgVal, "payerName", "");
        signMsgVal = appendParam(signMsgVal, "payerContactType", "");
        signMsgVal = appendParam(signMsgVal, "payerContact", "");
        signMsgVal = appendParam(signMsgVal, "orderId", "111221321323");
        signMsgVal = appendParam(signMsgVal, "orderAmount", "1");
        signMsgVal = appendParam(signMsgVal, "orderTime", DateTime.now().toString("yyyyMMddHHmmss"));
        signMsgVal = appendParam(signMsgVal, "productName", "");
        signMsgVal = appendParam(signMsgVal, "productNum", "");
        signMsgVal = appendParam(signMsgVal, "productId", "");
        signMsgVal = appendParam(signMsgVal, "productDesc", "");
        signMsgVal = appendParam(signMsgVal, "ext1", "");
        signMsgVal = appendParam(signMsgVal, "ext2", "");
        signMsgVal = appendParam(signMsgVal, "payType", "10-2");
        signMsgVal = appendParam(signMsgVal, "bankId", KQpayPlatform.ABC.code);
        signMsgVal = appendParam(signMsgVal, "redoFlag", "");
        signMsgVal = appendParam(signMsgVal, "pid", "");
        MixHelper.print("---------------------------------------->");
        MixHelper.print(signMsgVal);
        MixHelper.print(KQpayUtil.signMsg(signMsgVal));
    }

}
