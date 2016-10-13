package models;

import java.util.List;

import org.junit.Test;

import com.aton.test.DBHelper;
import com.aton.test.UnitTest;

import enums.pay.PayPlatform;


public class FundAccountTest extends UnitTest{
    
	@Test
	public void test_findByUserId() {
        DBHelper.truncate(FundAccount.TABLE_NAME);
        
        FundAccount fa = new FundAccount();
        fa.userId = 0L;
        fa.type = PayPlatform.TENPAY;
        fa.save();
        
        List<FundAccount> list = FundAccount.findByUserId(fa.userId);
        FundAccount fundAccount = list.get(0);
        assertNotNull(fundAccount);
        assertNotNull(fundAccount.userId);
        assertEquals(fa.type, fundAccount.type);
    
        DBHelper.truncate(FundAccount.TABLE_NAME);
    
	}
	
	/**
	 * 
	 * 【警告】会清除数据.
	 *
	 * @since  0.1
	 * @author youblade
	 * @created 2014年9月12日 下午6:35:36
	 */
	@Test
	public void test_findByType() {
	    DBHelper.truncate(FundAccount.TABLE_NAME);
	    
	    FundAccount fa = new FundAccount();
	    fa.userId = 0L;
	    fa.type = PayPlatform.TENPAY;
	    fa.save();
	    
	    FundAccount fundAccount = FundAccount.findByType(PayPlatform.TENPAY, fa.userId);
	    assertNotNull(fundAccount);
	    assertNotNull(fundAccount.userId);
	    assertEquals(fa.type, fundAccount.type);
	    
	    DBHelper.truncate(FundAccount.TABLE_NAME);
	}
	
	@Test
	public void test_findDefault() {
	    
	    User buyer = User.findByNick("buyer");
	    FundAccount fundAccount = FundAccount.findDefault(buyer);
	    assertNotNull(fundAccount);
	    
	    User seller = User.findByNick("seller");
	    fundAccount = FundAccount.findDefault(seller);
	    assertNotNull(fundAccount);
	    
	}

}
