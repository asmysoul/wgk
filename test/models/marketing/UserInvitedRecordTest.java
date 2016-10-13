package models.marketing;

import java.util.Date;

import org.joda.time.DateTime;
import org.junit.Test;

import vos.Page;
import vos.UserSearchVo;

import com.aton.test.UnitTest;

public class UserInvitedRecordTest  extends UnitTest{

 
	@Test
	public void test_findByPage() {
		UserSearchVo vo = new UserSearchVo();
		Page<UserInvitedRecord> page = UserInvitedRecord.findByPage(vo);
	}
	@Test
	public void test_findMyInvitedRecord() {
		UserInvitedRecord record = UserInvitedRecord.findMyInvitedRecord(2L);
		record.rewardIngot=50L;
		record.memberOpenTime=DateTime.now().toDate();
		record.save();
	}
	
}
