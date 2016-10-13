package models.mappers;

import java.util.Date;

import models.MemberChargeRecord;
import models.SellerPledgeRecord;
import models.SellerPledgeRecord.PledgeAction;
import models.User;
import models.UserIngotRecord;

import org.apache.ibatis.session.SqlSession;
import org.joda.time.LocalDate;
import org.joda.time.Months;
import org.junit.Test;

import com.aton.config.BizConstants;
import com.aton.db.SessionFactory;
import com.aton.test.DBHelper;
import com.aton.test.UnitTest;


/**
 * TODO Comment.
 * 
 * @author youblade
 * @since  0.1
 * @created 2014年8月25日 下午7:24:56
 */
public class MemberChargeRecordTest extends UnitTest{

    @Test
    public void test_simpleCreate() {
        DBHelper.truncate(MemberChargeRecord.TABLE_NAME);
        
        int totalMonths = 10;
        for (int i = 1; i <= totalMonths; i++) {
            MemberChargeRecord r = new MemberChargeRecord();
            r.userId = 0;
            r.month = 1;
            r.simpleCreate();
        }
        // Check
        MemberChargeRecord record = SessionFactory.getSqlSession().getMapper(MemberChargeRecordMapper.class).selectLastByUserId(0);
        assertNotNull(record);
        assertEquals(LocalDate.now().plusMonths(totalMonths).toDate(), record.endTime);
    }
    
    /**
     * Test method for {@link models.MemberChargeRecord#create(boolean, boolean)}.
     */
    @Test
    public void test_create() {

        User user = User.findByNick("seller");
        int memberMonth = 6;
        long memberPayFee = BizConstants.SELLER_MEMBER_FEE.get(memberMonth);

        MemberChargeRecord r = MemberChargeRecord.newInstance(user.id, memberMonth).userType(user.type).amount(memberPayFee);

        /*
         * 仅用押金支付
         */
        DBHelper.truncate(SellerPledgeRecord.TABLE_NAME, MemberChargeRecord.TABLE_NAME);
        // 1、押金不足
        // 1.1 押金为0
        assertFalse(r.create(false, true));

        SqlSession ss = SessionFactory.getSqlSession();
        MemberChargeRecordMapper mapper = ss.getMapper(MemberChargeRecordMapper.class);
        assertNull(mapper.selectLastByUserId(user.id));
        // 1.2 押金不为0
        // 造一条押金充值记录，金额为memberPayFee-100
        SellerPledgeRecord.newInstance(user.id, null).action(PledgeAction.RECHARGE, memberPayFee - 100).create();
        assertFalse(r.create(false, true));
        assertNull(mapper.selectLastByUserId(user.id));

        // 2、押金充足
        // 造一条押金充值记录，金额为memberPayFee
        SellerPledgeRecord.newInstance(user.id, null).action(PledgeAction.RECHARGE, memberPayFee).create();

        assertTrue(r.create(false, true));
        ss.commit();
        MemberChargeRecord chargeRecord = mapper.selectLastByUserId(user.id);
        assertNotNull(chargeRecord);
        assertEquals(memberPayFee, chargeRecord.amount);
        assertEquals(memberMonth, chargeRecord.month);
        assertEquals(memberMonth, mongthsBetween(chargeRecord.startTime, chargeRecord.endTime));

        /*
         * 仅用金币支付
         */
        DBHelper.truncate(UserIngotRecord.TABLE_NAME, MemberChargeRecord.TABLE_NAME);
        // 1、金币不足
        // 1.1 金币为0
        assertFalse(r.create(true, false));
        ss.commit();
        assertNull(mapper.selectLastByUserId(user.id));
        // 1.2 金币不为0
        // 造一条金币充值记录，金额为memberPayFee-100
        UserIngotRecord.newInstance(user.id, null).plus(memberPayFee - 100).simpleCreate();
        assertFalse(r.create(true, false));
//        ss.commit();
        chargeRecord = mapper.selectLastByUserId(user.id);
        assertNull(chargeRecord);

        // 2、金币充足
        // 造一条金币充值记录，金额为memberPayFee
        UserIngotRecord.newInstance(user.id, null).plus(memberPayFee).simpleCreate();
        assertTrue(r.create(true, false));

        ss.commit();
        chargeRecord = mapper.selectLastByUserId(user.id);
        assertEquals(memberPayFee, chargeRecord.amount);
        assertEquals(memberMonth, chargeRecord.month);
        assertEquals(memberMonth, mongthsBetween(chargeRecord.startTime, chargeRecord.endTime));

        /*
         * 押金、金币混合支付
         */
        DBHelper.truncate(UserIngotRecord.TABLE_NAME, SellerPledgeRecord.TABLE_NAME,MemberChargeRecord.TABLE_NAME);
        // 1.押金不足
        // 1.1 押金为0
        // 1.2 押金不为0
        // 2.金币不足
        // 2.1 金币为0
        // 2.2 金币不为0
        
        // 3.押金、金币均不足
        // 3.1 押金为0
        // 3.1 金币为0
        
        // 4.押金、金币都足够
        
        // 造一条金币充值记录，金额为memberPayFee
        UserIngotRecord.newInstance(user.id, null).plus(memberPayFee).simpleCreate();
        assertTrue(r.create(true, false));

        ss.commit();
        chargeRecord = mapper.selectLastByUserId(user.id);
        assertEquals(memberPayFee, chargeRecord.amount);
        assertEquals(memberMonth, chargeRecord.month);
        assertEquals(memberMonth, mongthsBetween(chargeRecord.startTime, chargeRecord.endTime));
    }

    public int mongthsBetween(Date a, Date b) {
        return Months.monthsBetween(new LocalDate(a), new LocalDate(b)).getMonths();
    }
}
