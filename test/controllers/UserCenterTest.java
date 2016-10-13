package controllers;

import java.util.List;
import java.util.Map;

import models.BuyerAccount;
import models.MemberChargeRecord;
import models.Shop;
import models.User;
import models.User.UserType;
import models.UserIngotRecord;
import models.mappers.BuyerAccountMapper;

import org.joda.time.Days;
import org.joda.time.LocalDate;
import org.junit.Test;

import play.mvc.Http.Response;
import vos.BuyerAccountSearchVo;
import vos.BuyerAccountSearchVo.ExamineStatus;

import com.aton.config.ReturnCode;
import com.aton.db.SessionFactory;
import com.aton.test.BaseAppTest;
import com.aton.test.DBHelper;
import com.google.common.collect.Maps;

import enums.Platform;

public class UserCenterTest extends BaseAppTest {

    @Test
    public void test_listShops() {
        getAuthcode(UserType.SELLER,"");
        for (Platform platform : Platform.values()) {
            Response response = GET("/user/shop/" + platform);
            assertIsOk(response);
        }
        
        getAuthcode(UserType.SELLER,"0001");
        for (Platform platform : Platform.values()) {
            Response response = GET("/user/shop/" + platform);
            assertIsOk(response);
        }
    }

    @Test
    public void test_addShop() {
        // 清理数据
        DBHelper.truncate(Shop.TABLE_NAME);
        
        // 模拟用户登录
        getAuthcode(UserType.SELLER,"");
        
        for (Platform platform : Platform.values()) {
            Map<String, String> parmas = Maps.newHashMap();
            parmas.put("shop.url", "http://tsgf.com");
            parmas.put("shop.address", "http://tsgf.com");
            parmas.put("shop.platform", platform.toString());
            if (platform == Platform.TAOBAO || platform == Platform.TMALL) {
                parmas.put("shop.nick", "wangwangid");
            } else {
                parmas.put("shop.name", "XX旗舰店");
            }

            Response response = POST("/user/shop/add", parmas);
            assertResultIsOk(response);
        }
    }
    
    @Test
    public void test_memberCharge() {
        // 清理数据
        DBHelper.truncate(MemberChargeRecord.TABLE_NAME,UserIngotRecord.TABLE_NAME);

        // 模拟【商家】用户登录
        _testRecharge("seller", 6);

        /*
         *  测试校验
         */
        Response response = POST("/user/member/charge?month=0");
        assertReturnCode(ReturnCode.WRONG_INPUT, response);

        /*
         *  6个月
         */
        // 模拟【买手】用户登录
        _testRecharge("buyer", 6);
        
        
    }

    private void _testRecharge(String nick, int month) {
        
        User user = User.findByNick(nick);
        assertNotNull(user);
        getAuthcode(user.type, user.nick);

        // 充值
        long amount = MemberChargeRecord.getRechargeAmount(user.type, month);
        UserIngotRecord.newInstance(user.id, null).plus(amount).memo("购买金币").buy();
        // 取充值后的金额
        user = User.findByIdWichCache(user.id);
        
        Response response = POST("/user/member/charge?useIngot=true&month=" + month);
        assertResultIsOk(response);

        //TODO 校验账户余额
        User cachedUser = User.findByIdWichCache(user.id);
        assertEquals(user.ingot - amount, cachedUser.ingot, 0);
        //        MemberChargeRecord r = MemberChargeRecord.find
    }

    @Test
    public void test_member(){
        LocalDate today = LocalDate.now();
        int days = Days.daysBetween(today, today.plusDays(1)).getDays();
        assertEquals(1, days);
    }
    
    @Test
    public void test_saveBuyerAccount(){
        DBHelper.truncate(BuyerAccount.TABLE_NAME);
        
        User buyer = User.findByNick("buyer");
        getAuthcode(buyer.type, buyer.nick);
        
        String url = "/buyer/account/add";
        /*
         * Add new account
         */
        // no param
        Map<String, String> params = Maps.newHashMap();
        Response response = POST(url, params);
        assertReturnCode(ReturnCode.WRONG_INPUT, response);
        
        String nick = "buyerAccountNick";
        String mobile = "18922838897";
        String consignee = "consignee";
        String state = "state";
        String city = "city";
        String region = "region";
        String address = "address";
        
        // 正确定参数
        params.put("account.platform", Platform.TAOBAO.toString());
        params.put("account.nick", nick);
        params.put("account.mobile", mobile);
        params.put("account.consignee", consignee);
        params.put("account.state", state);
        params.put("account.city", city);
        params.put("account.address", address);
        response = POST(url, params);
        assertResultIsOk(response);
        
        // 重复的昵称
        params.put("account.nick", nick);
        response = POST(url, params);
        assertReturnCode(ReturnCode.OP_BIZ_LIMIT, response);
        // 重复的手机号
        params.put("account.mobile", mobile);
        response = POST(url, params);
        assertReturnCode(ReturnCode.OP_BIZ_LIMIT, response);
        // 重复的收货人姓名
        params.put("account.consignee", consignee);
        response = POST(url, params);
        assertReturnCode(ReturnCode.OP_BIZ_LIMIT, response);
        // 重复的收货地址
        params.put("account.state", state);
        params.put("account.city", city);
        params.put("account.region", region);
        params.put("account.address", address);
        response = POST(url, params);
        assertReturnCode(ReturnCode.OP_BIZ_LIMIT, response);
        
        /*
         * Edit old account
         */
        // 构造一个与之前不重复的新买号
        BuyerAccount ba = new BuyerAccount();
        ba.nick = "129";
        ba.mobile = "";
        ba.userId = buyer.id;
        ba.status = ExamineStatus.NOT_PASS;
        ba.save();
        
        // 查出首次添加的“待审核”的买号，进行修改
        List<BuyerAccount> list = BuyerAccount.findList(BuyerAccountSearchVo.newInstance().userId(buyer.id)
            .status(ExamineStatus.WAIT_EXAMINE));
        BuyerAccount account = list.get(0);
        
        //正确的参数：
        params.put("account.id", String.valueOf(account.id));
        response = POST(url, params);
        assertResultIsOk(response);
    }
}
