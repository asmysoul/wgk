package models;

import static org.junit.Assert.fail;

import java.util.List;

import org.junit.Test;

import vos.BuyerAccountSearchVo;
import vos.BuyerAccountSearchVo.ExamineStatus;

import com.aton.test.UnitTest;
import com.aton.util.MixHelper;

import enums.Platform;

public class BuyerAccountTest extends UnitTest{

	@Test
	public void test_findList() {
		BuyerAccountSearchVo vo = new BuyerAccountSearchVo();
		vo.platform = Platform.TAOBAO;
		vo.userId = 123L;
		
		List<BuyerAccount> findList = BuyerAccount.findList(vo);
		MixHelper.print(findList.size());
		
		//测试save（）方法
		BuyerAccount ba = new BuyerAccount();
		ba.id = 4L;
		ba.nick = "nihao";
		ba.status = ExamineStatus.WAIT_EXAMINE;
		ba.platform = Platform.TAOBAO;
		ba.address="feijichang";
		ba.userId = 123L;
		ba.save();
		
		List<BuyerAccount> list = BuyerAccount.findList(vo);
		MixHelper.print(list.size());
		assertTrue(list.size() - findList.size() == 1);
		
		
	}

}
