package controllers;

import java.util.Map;

import models.BuyerAccount;
import models.BuyerTask;
import models.Task;

import org.junit.Test;

import play.mvc.Http.Response;
import vos.BuyerAccountSearchVo;
import vos.BuyerAccountSearchVo.ExamineStatus;

import com.aton.test.BaseAppTest;
import com.aton.test.DBHelper;
import com.aton.util.MixHelper;
import com.google.common.collect.Maps;

public class AdminTest extends BaseAppTest{

	@Test
	public void test_listBuyerAccount() {
		Response rp = GET("/admin/buyerAccount/list?vo.pageNo=1&vo.pageSize=5");
		MixHelper.print(rp.out.toString());
		assertIsOk(rp);
	}
	
	@Test
	public void test_buyerAudit(){
		Map<String, String> param =  Maps.newHashMap();
		param.put("ba.id", "7");
		param.put("ba.status", "EXAMINED");
		Response rp = POST("/admin/buyerAccount/exmine",param);
		assertIsOk(rp);
	}
	
	@Test
	public void test_listOrders(){
		Response rp = GET("/admin/express/listOrders?vo.pageSize=10&vo.pageNo=1");
		MixHelper.print(rp.out.toString());
		assertIsOk(rp);
	}
	
	@Test
	public void test_exportOrders(){
	    DBHelper.truncate(Task.TABLE_NAME,BuyerTask.TABLE_NAME);
	    // 无数据
	    Response response = POST("/admin/express/exportOrders?vo.exportNo=10");
	    MixHelper.print(response.out.toString());
	    assertIsOk(response);
	    
	    // 构造一条待发货记录
	    Task.newInstance().create();
//	    BuyerTask.newInstance().taskId(taskId).status(TaskStatus.WAIT_SEND_GOODS).simpleCreate();
	    response = POST("/admin/express/exportOrders?vo.exportNo=10");
	    MixHelper.print(response.out.toString());
	    assertIsOk(response);
	}

	@Test
    public void test_examineBuyerAccount() {
        DBHelper.truncate(BuyerAccount.TABLE_NAME);

        BuyerAccount ba = new BuyerAccount();
        ba.nick = "129";
        ba.mobile = "";
        ba.userId = 1L;
        ba.status = ExamineStatus.WAIT_EXAMINE;
        ba.save();

        BuyerAccount buyerAccount = BuyerAccount.findList(BuyerAccountSearchVo.newInstance().userId(ba.userId)).get(0);

        ExamineStatus status = ExamineStatus.NOT_PASS;
        String url = "/admin/buyerAccount/exmine?ba.id={}&ba.status={}&ba.memo={}";
        Response response = POST(MixHelper.format(url, buyerAccount.id, status));
        assertResultIsOk(response);

        buyerAccount = BuyerAccount.findList(BuyerAccountSearchVo.newInstance().userId(ba.userId)).get(0);
        assertEquals(status, buyerAccount.status);

        DBHelper.truncate(BuyerAccount.TABLE_NAME);
    }
	
}
