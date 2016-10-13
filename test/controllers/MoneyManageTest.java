package controllers;

import java.math.BigDecimal;

import models.SellerPledgeRecord;
import models.SellerPledgeRecord.PledgeAction;
import models.User;
import models.User.UserType;
import models.UserIngotRecord;
import models.UserWithdrawRecord;
import models.UserWithdrawRecord.WithdrawStatus;
import models.mappers.SellerPledgeRecordMapper;
import models.mappers.UserIngotRecordMapper;
import models.mappers.UserWithdrawRecordMapper;

import org.apache.ibatis.session.SqlSession;
import org.junit.Test;

import play.mvc.Http.Response;

import com.aton.config.BizConstants;
import com.aton.config.ReturnCode;
import com.aton.db.SessionFactory;
import com.aton.test.BaseAppTest;
import com.aton.test.DBHelper;
import com.aton.util.MixHelper;
import com.google.common.base.Strings;

import controllers.MoneyManage.MoneyRecordSearchVo;
import controllers.MoneyManage.RecordType;
import enums.Sign;
import enums.pay.TradeType;

/**
 * TODO Comment.
 * 
 * @author youblade
 * @since 0.1
 * @created 2014年8月25日 下午4:49:19
 */
public class MoneyManageTest extends BaseAppTest {

    /**
     * Test method for
     * {@link controllers.MoneyManage#listRecord(controllers.MoneyManage.MoneyRecordSearchVo)}.
     */
    @Test
    public void testListRecord() {
        // 模拟用户登录
        getAuthcode(UserType.SELLER, "");

        Response response = GET("/user/money/record/list");
        assertReturnCode(ReturnCode.WRONG_INPUT, response);

        response = GET("/user/money/record/list?vo.pageNo=1&vo.type=" + RecordType.member);
        assertResultIsOk(response);
    }
    
    @Test
    public void test_withdraw() {
        DBHelper.truncate(UserWithdrawRecord.TABLE_NAME);

        /*
         * 【卖家】提现“押金”
         */
        User user = User.findByNick("seller");
        getAuthcode(user.type, user.nick);
        // 构造押金充值记录
        Response response = POST(MixHelper.format("/admin/recharge?id={}&yuan={}&type={}",user.id,100,TradeType.PLEDGE));
        assertResultIsOk(response);

        response = POST("/user/withdraw/apply");
        assertReturnCode(ReturnCode.WRONG_INPUT, response);

        // 先设置支付密码：更新缓存即可
        if (Strings.isNullOrEmpty(user.payPassword)) {
            user.payPassword = "123";
            user.updateCache();
        }
        response = POST("/user/withdraw/apply?amount=100&payPass=" + user.payPassword);
        assertResultIsOk(response);

        SqlSession ss = SessionFactory.getSqlSession();
        UserWithdrawRecordMapper mapper = ss.getMapper(UserWithdrawRecordMapper.class);
        UserWithdrawRecord record = mapper.selectList(MoneyRecordSearchVo.newInstance().userId(user.id)).get(0);
        assertNotNull(record.userId);
        assertNotNull(record.applyAmount);
        assertNotNull(record.amount);
        assertNotNull(record.applyTime);
        assertNotNull(record.modifyTime);
        assertEquals(WithdrawStatus.WAIT, record.status);
        long amount = record.applyAmount - BigDecimal.valueOf(record.applyAmount)
            .multiply(new BigDecimal(BizConstants.WITHDRAW_PERCENT_SELLER)).longValue();
        assertEquals(amount, record.amount.intValue());
        // 检查押金记录
        SellerPledgeRecord pledgeRecord = ss.getMapper(SellerPledgeRecordMapper.class).selectLastRecordForTest(user.id);
        assertEquals(record.applyAmount.longValue(), (long)pledgeRecord.amount);
        assertEquals(Sign.MINUS, pledgeRecord.sign);
        assertEquals(PledgeAction.WITHDRAW, pledgeRecord.action);
        
        /*
         * 【买手】提现“金币”
         */
        // DBHelper.truncate(UserWithdrawRecord.TABLE_NAME);
        User buyer = User.findByNick("buyer");
        getAuthcode(buyer.type, buyer.nick);

        // 先设置支付密码：更新缓存即可
        if (Strings.isNullOrEmpty(buyer.payPassword)) {
            buyer.payPassword = "123";
            buyer.updateCache();
        }
        response = POST("/user/withdraw/apply?amount=100&payPass=" + buyer.payPassword);
        assertResultIsOk(response);

        record = mapper.selectList(MoneyRecordSearchVo.newInstance().userId(buyer.id)).get(0);
        assertNotNull(record.userId);
        assertNotNull(record.applyAmount);
        assertNotNull(record.amount);
        assertNotNull(record.applyTime);
        assertNotNull(record.modifyTime);
        assertEquals(WithdrawStatus.WAIT, record.status);
        amount = record.applyAmount - BigDecimal.valueOf(record.applyAmount).multiply(new BigDecimal(BizConstants.WITHDRAW_PERCENT_BUYER))
            .longValue();
        assertEquals(amount, record.amount.intValue());
        
        // 检查金币记录
        UserIngotRecord ingotRecord = ss.getMapper(UserIngotRecordMapper.class).selectLastRecordForTest(buyer.id);
        assertEquals(record.applyAmount.longValue(), ingotRecord.amount);
        assertEquals(Sign.MINUS, ingotRecord.sign);
        assertTrue(ingotRecord.memo.contains("提现"));
    }

}
