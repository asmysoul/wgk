package models;

import models.UserWithdrawRecord.WithdrawStatus;

import org.junit.Test;

import vos.Page;

import com.aton.test.UnitTest;
import com.aton.util.MixHelper;

import controllers.MoneyManage.WithdrawSearchVo;


/**
 * TODO Comment.
 * 
 * @author youblade
 * @since  0.1
 * @created 2014年10月16日 下午6:09:53
 */
public class UserWithdrawRecordTest extends UnitTest {

    /**
     * Test method for {@link models.UserWithdrawRecord#listForAdminPage(vos.WithdrawSearchVo)}.
     */
    @Test
    public void test_findByPageForAdmin() {
        
        WithdrawSearchVo vo = WithdrawSearchVo.newInstance().status(WithdrawStatus.WAIT);
        Page<UserWithdrawRecord> page = UserWithdrawRecord.findByPageForAdmin(vo);
        
        assertNotNull(page);
        assertTrue(MixHelper.isNotEmpty(page.items));
    }

}
