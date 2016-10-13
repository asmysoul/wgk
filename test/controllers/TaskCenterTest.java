package controllers;

import java.util.List;
import java.util.Map;

import models.BuyerAccount;
import models.BuyerTask;
import models.Task;
import models.User;
import models.User.UserType;

import org.junit.Test;

import play.mvc.Http.Response;
import vos.BuyerAccountSearchVo;
import vos.TaskSearchVo;

import com.aton.test.BaseAppTest;
import com.aton.test.DBHelper;
import com.aton.util.MixHelper;
import com.aton.util.NumberUtils;
import com.aton.util.Pandora;
import com.aton.vo.Page;
import com.google.common.collect.Maps;

import enums.Device;
import enums.Platform;
import enums.TaskListType;
import enums.TaskStatus;
import enums.TaskType;

public class TaskCenterTest extends BaseAppTest {

	
	@Test
	public void testListTasks() {

		StringBuilder sb = new StringBuilder();
		Map<String, Object> maps = Maps.newHashMap();
		maps.put("vo.platform", Platform.TAOBAO);
		maps.put("vo.device", Device.PC);
		maps.put("vo.taskType", TaskType.ORDER);
		maps.put("vo.taskListType", TaskListType.NEW_SHOP);
		for (String key : maps.keySet()) {
			sb.append(key).append("=").append(maps.get(key)).append("&");
		}
		
		Response response = GET("/task/list?" + sb.toString());
		assertResultIsOk(response);
		
	}
	
	@Test
	public void test_listWaitSendGoods(){
		TaskSearchVo vo = TaskSearchVo.newInstance().pageNo(1).pageNo(10).sellerId(3L);
		Response resp = GET("/seller/task/listWaitSendGoods?vo.sellerId=3&vo.platform=TAOBAO");
		assertResultIsOk(resp);
	}
	
	/*
	 * 【注意】会清空数据
	 */
    @Test
    public void test_take() {
        DBHelper.truncate(BuyerTask.TABLE_NAME);

        getAuthcode(UserType.BUYER, "buyer");
        
        long sellerId = User.findByNick("0001").id;
        vos.Page<Task> list = Task.findBySearchVo(TaskSearchVo.newInstance().sellerId(sellerId));
        assertTrue(MixHelper.isNotEmpty(list));

        long buyerId = User.findByNick("buyer").id;
        List<BuyerAccount> buyerAccounts = BuyerAccount.findForTakingTask(BuyerAccountSearchVo.newInstance().userId(
            buyerId));
        assertTrue(MixHelper.isNotEmpty(list));

        Long taskId = list.items.get(0).id;
        BuyerAccount ba = buyerAccounts.get(0);
        Map<String, String> map = Maps.newHashMap();
        map.put("bt.taskId", taskId.toString());
        map.put("bt.device", Device.PC.toString());
        map.put("bt.buyerAccountId", String.valueOf(ba.id));
        map.put("bt.buyerAccountNick", ba.nick);

        Response response = POST("/task/take", map);
        assertResultIsOk(response);

        //Check
        TaskSearchVo svo = TaskSearchVo.newInstance().buyerId(buyerId);
        svo.buyerAccountId = ba.id;
        BuyerTask task = BuyerTask.findList(svo).get(0);
        assertNotNull(task);
        assertTrue(NumberUtils.isGreaterThanZero(task.pledgeIngot));
        assertTrue(NumberUtils.isGreaterThanZero(task.rewardIngot));
        assertTrue(NumberUtils.isGreaterThanZero(task.experience));
    }
    
    @Test
    public void test_sellerTasks() {
        getAuthcode(UserType.SELLER, "seller");
        Response response = GET("/seller/tasks");
        assertIsOk(response);
    }

    @Test
    public void test_confirmRefund() {
        User seller = User.findByNick("seller");
        getAuthcode(seller.type, seller.nick);
        
        BuyerTask buyerTask = BuyerTask.newInstance().status(TaskStatus.WAIT_REFUND).buyerId(0L);
        buyerTask.taskId = Pandora.getInstance().nextId();
        buyerTask.sellerId = seller.id;
        buyerTask.simpleCreate();
        
        String url = MixHelper.format("/seller/task/confirmRefund?id={}&transNo={}",buyerTask.id,"123343432424" );
        Response response = POST(url);
        assertIsOk(response);
        assertResultIsOk(response);
    }
    
}
