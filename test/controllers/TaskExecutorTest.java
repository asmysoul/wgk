package controllers;

import java.util.List;
import java.util.Map;

import models.BuyerTask;
import models.Task;
import models.User;
import models.User.UserType;

import org.junit.Test;

import play.mvc.Http.Request;
import play.mvc.Http.Response;
import play.mvc.Scope.Session;
import vos.TaskSearchVo;

import com.aton.config.BizConstants;
import com.aton.config.ReturnCode;
import com.aton.test.BaseAppTest;
import com.aton.test.DBHelper;
import com.aton.util.MixHelper;
import com.aton.util.NumberUtils;
import com.aton.util.StringUtils;
import com.google.common.collect.Maps;

import enums.BuyerTaskStepType;
import enums.TaskStatus;

/**
 * TODO Comment.
 * 
 * @author youblade
 * @since 0.1
 * @created 2014年8月21日 下午6:53:47
 */
public class TaskExecutorTest extends BaseAppTest {

    /**
     * Test method for
     * {@link controllers.TaskExecutor#saveStep(enums.BuyerTaskStepType, vos.BuyerTaskStepVo)}.
     */
    @Test
    public void testSaveStep() {

        getAuthcode(UserType.BUYER, "buyer");

        String parmas = MixHelper.format("?type={}&vo.itemUrls={}", BuyerTaskStepType.CHOOSE_ITEM, "http://sdfdsfs.etett.com");
        Response response = POST("/buyer/task/saveStep" + parmas);
        assertResultIsOk(response);
    }
    
    @Test
    public void test_saveStep_check() {
        String floatNumberStr = "19.80";
        assertFalse(StringUtils.isNumeric(floatNumberStr));
        assertFalse(NumberUtils.isDigits(floatNumberStr));
        
        assertTrue(NumberUtils.isNumber(floatNumberStr));
        assertTrue(NumberUtils.isNumber("19"));
    }
    
    @Test
    public void test_checkItem() throws Throwable {
        DBHelper.truncate(Task.TABLE_NAME,BuyerTask.TABLE_NAME);
        
        User buyer = User.findByNick("buyer");
        getAuthcode(UserType.BUYER, buyer.nick);
        
        /*
         *  错误的商品链接：无ID
         */
        String url = "http://sdfdsfs.etett.com";
        Response response = GET("/buyer/task/perform/checkItem?itemUrl=" + url);
        assertReturnCode(ReturnCode.FAIL, response);
        
        /*
         * 正确的商品链接
         */
        url = "http://item.taobao.com/item.html?id=37173999168";
        
        // 构造一个待支付的买手任务
        Task t = Task.instanceForTest(1).itemUrl(url).create();
        BuyerTask bt = BuyerTask.newInstance().taskId(t.id).buyerId(buyer.id).status(TaskStatus.WAIT_PAY);
        bt.simpleCreate();
        
        // 设置当前正在做的任务
        setBuyerTask(bt.id);
        
        response = GET("/buyer/task/perform/checkItem?itemUrl=" + url);
        assertResultIsOk(response);
    }
    
    @Test
    public void test_confirmRecvGoods() {
        User buyer = User.findByNick("buyer");
        getAuthcode(UserType.BUYER, buyer.nick);

        BuyerTask buyerTask = BuyerTask.newInstance().buyerId(buyer.id).status(TaskStatus.WAIT_CONFIRM).simpleCreate();
        long taskId = buyerTask.id;

        String url = "/buyer/task/confirmRecvGoods";
        Response response = POST(MixHelper.format(url, taskId, ""));
        assertReturnCode(ReturnCode.WRONG_INPUT, response);
        
        url = "/buyer/task/confirmRecvGoods?id={}";
        response = POST(MixHelper.format(url, taskId, ""));
        assertReturnCode(ReturnCode.WRONG_INPUT, response);
        
        url = "/buyer/task/confirmRecvGoods?id={}&vo.picUrls={}";
        response = POST(MixHelper.format(url, taskId, ""));
        assertReturnCode(ReturnCode.WRONG_INPUT, response);

        response = POST(MixHelper.format(url, taskId, "sdfdsf"));
        assertReturnCode(ReturnCode.WRONG_INPUT, response);

        response = POST(MixHelper.format(url, taskId, "http://sdfdsfsdf"));
        assertReturnCode(ReturnCode.WRONG_INPUT, response);
        
        response = POST(MixHelper.format(url, taskId, "http://sdfdsfsdf.com"));
        assertResultIsOk(response);
        // 请求成功后会修改状态，重置一下
        buyerTask.status(TaskStatus.WAIT_CONFIRM).save();
        
        response = POST(MixHelper.format(url, taskId, "http://sdfdsfsdf.com/sfsdfs"));
        assertResultIsOk(response);
        buyerTask.status(TaskStatus.WAIT_CONFIRM).save();
    }

}
