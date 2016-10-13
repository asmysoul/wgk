package controllers;

import java.util.List;
import java.util.Map;

import models.Shop;
import models.Task;
import models.User;
import models.User.UserType;

import org.junit.Test;

import play.mvc.Http.Response;
import vos.TaskSearchVo;

import com.aton.config.ReturnCode;
import com.aton.test.BaseAppTest;
import com.aton.test.DBHelper;
import com.aton.util.MixHelper;
import com.aton.util.NumberUtils;
import com.aton.util.TenpayUtil;
import com.aton.vo.Page;
import com.google.common.collect.Maps;

import enums.Platform;
import enums.TaskStatus;
import enums.TaskType;

/**
 * TODO Comment.
 * 
 * @author youblade
 * @since 0.1
 * @created 2014年8月28日 下午6:38:37
 */
public class TaskPublishTest extends BaseAppTest {

    /**
     * Test method for {@link controllers.TaskPublish#speedPerform(models.Task)}.
     */
    @Test
    public void testSpeedPerform() {
        User user = User.findByNick("0001");
        assertNotNull(user);

        getAuthcode(UserType.SELLER, user.nick);

        vos.Page<Task> list = Task.findBySearchVo(TaskSearchVo.newInstance().sellerId(user.id).pageNo(1).pageSize(1)
            .status(TaskStatus.PUBLISHED));
        assertTrue(MixHelper.isNotEmpty(list));
        Task task = list.items.get(0);

        String params = MixHelper.format("t.id={}&t.speedTaskIngot={}&t.extraRewardIngot={}", task.id, 10, 10);
        Response response = POST("/seller/task/speed?" + params);
        assertResultIsOk(response);

        Task taskUpdated = Task.findById(task.id);
        assertNotNull(taskUpdated);
        assertTrue(10.0f == taskUpdated.extraRewardIngot);
        assertEquals(10, taskUpdated.speedTaskIngot.intValue());
        
        // 检查搜索方案是否被更新掉
        if(MixHelper.isNotEmpty(task.searchPlans)){
            assertEquals(task.searchPlans.size(), taskUpdated.searchPlans.size());
        }
    }
    
    /**
     * Test method for {@link controllers.TaskPublish#saveTask(models.Task)}.
     * 【注意】会清除表数据
     */
    @Test
    public void test_saveTask() {
        DBHelper.truncate(Task.TABLE_NAME);

        User user = User.findByNick("0001");
        assertNotNull(user);
        getAuthcode(UserType.SELLER, user.nick);

        /*
         * 新任务
         */
        Task t = Task.newInstance().setSellerId(user.id);

        // 设置必填参数
        t.platform = Platform.TAOBAO;
        t.type = TaskType.ORDER;
        Shop shop = Shop.findByPlatformAndSellerId(Platform.TAOBAO, user.id).get(0);
        assertNotNull(shop);
        t.shopId = shop.id;
        t.shopName = shop.name;
        t.itemUrl = "http://item.taobao.com?id=12345";
        t.itemId = "12345";
        t.itemTitle = "宝贝数第三方";
        t.itemPicUrl = t.itemUrl;
        t.itemPic = t.itemUrl;
        t.orderMessages = "orderMessages";

        // wrong item price
        t.itemPrice = 10;
        t.itemBuyNum = 1;
        t.totalOrderNum = 10;
        t.mobileOrderNum = 1;

        Map<String, String> params = _setParams(t);
        Response response = POST("/task/save", params);
        assertReturnCode(ReturnCode.WRONG_INPUT, response);

        t.itemPrice = 16500L;
        t.itemBuyNum = 1;
        t.totalOrderNum = 10;
        t.mobileOrderNum = 1;
        t.isFreeShipping = false;
        params = _setParams(t);
        response = POST("/task/save?", params);
        assertResultIsOk(response);

        //Check
        vos.Page<Task> list = Task.findBySearchVo(TaskSearchVo.newInstance().sellerId(user.id));
        assertTrue(MixHelper.isNotEmpty(list));
        Task task = Task.findById(list.items.get(0).id);
        
        assertNotNull(task.id);
        assertEquals(t.platform, task.platform);
        assertEquals(t.type, task.type);
        assertEquals(t.shopId, task.shopId);
        assertEquals(t.shopName, task.shopName);
        assertEquals(t.itemUrl, task.itemUrl);
        assertEquals(t.itemTitle, task.itemTitle);
        assertEquals(t.itemPic, task.itemPic);
        assertEquals(t.itemPicUrl, task.itemPicUrl);
        assertEquals(t.orderMessages, task.orderMessages);
        assertEquals(t.itemPrice, task.itemPrice);
        assertEquals(t.itemBuyNum, task.itemBuyNum);
        assertEquals(t.totalOrderNum, task.totalOrderNum);
        assertEquals(t.mobileOrderNum, task.mobileOrderNum);
        assertEquals(t.sellerId, task.sellerId);
        
        assertNotNull(task.isFreeShipping);
        assertTrue(NumberUtils.isGreaterThanZero(task.pledge));
        assertTrue(NumberUtils.isGreaterThanZero(task.totalPledge));
        assertTrue(NumberUtils.isGreaterThanZero(task.baseOrderIngot));
        assertTrue(NumberUtils.isGreaterThanZero(task.totalIngot));
//            assertTrue(NumberUtils.isGreaterThanZero(task.rewardIngot));
        
        assertNotNull(task.createTime);
        assertNotNull(task.modifyTime);
        assertNotNull(task.status);
    }

    private Map<String, String> _setParams(Task t) {
        Map<String, String> params = Maps.newHashMap();
        params.put("task.platform", t.platform.toString());
        params.put("task.type", t.type.toString());
        params.put("task.sellerId", t.sellerId.toString());
        
        params.put("task.shopId", t.shopId.toString());
        params.put("task.shopName", t.shopName);
        params.put("task.itemUrl", t.itemUrl);
        params.put("task.itemTitle", t.itemTitle);
        params.put("task.itemPicUrl", t.itemPicUrl);
        params.put("task.itemPic", t.itemPic);
        params.put("task.isFreeShipping", String.valueOf(t.isFreeShipping));
        params.put("task.orderMessages", t.orderMessages);
        
        params.put("task.itemPrice", String.valueOf(t.itemPrice));
        params.put("task.itemBuyNum", String.valueOf(t.itemBuyNum));
        params.put("task.totalOrderNum", String.valueOf(t.totalOrderNum));
        params.put("task.mobileOrderNum", String.valueOf(t.mobileOrderNum));
        return params;
    }
    
    @Test
    public void test_pay() {
        Map<String, String> params = Maps.newHashMap();
        String sign = TenpayUtil.createSign(params);
        MixHelper.print(sign);

        params.put("body", "商品描述：task");
        params.put("return_url", "http://localhost/api/tenpay/result");
        params.put("notify_url", "http://localhost/api/tenpay/notify");
        params.put("partner", "1900000113");
        params.put("out_trade_no", "123");
        params.put("total_fee", "1");
        params.put("spbill_create_ip", "127.0.0.1");

        params.put("sign", sign);
        Response response = POST("https://gw.tenpay.com/gateway/pay.htm", params);
        assertIsOk(response);
        
        MixHelper.print(response.out.toString());

    }
}