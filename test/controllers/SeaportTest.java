package controllers;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeoutException;

import models.TenpayTradeLog;

import org.junit.Test;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import play.libs.XML;
import play.mvc.Http.Response;

import com.aton.test.BaseAppTest;
import com.aton.util.MixHelper;


public class SeaportTest extends BaseAppTest{

    @Test
    public void testTenpayNotify() throws InterruptedException, ExecutionException, TimeoutException {
        // test mock api
        Response response = GET("/mock/tenpay/verifyNofifyId");
        assertIsOk(response);
        MixHelper.print("response={}",response.out.toString());
        String xmlString = response.out.toString();
        
        Document xml = XML.getDocument(xmlString);
        Element rootNode = (Element) xml.getFirstChild();
        
        String retcode = rootNode.getElementsByTagName("retcode").item(0).getTextContent();
        MixHelper.print(retcode);
        
        
        // test nofity
        TenpayTradeLog log = TenpayTradeLog.findById("1");
        assertNotNull(log);
        
        String queryString = MixHelper.format("notify_id={}&out_trade_no={}", 1, log.outTradeNo);
        response = GET("/api/tenpay/notify?" + queryString);
        assertIsOk(response);
    }

}
