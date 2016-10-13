package models.marketing;

import java.util.Date;
import java.util.List;

import models.marketing.Activity.ActivityBizType;
import models.marketing.Activity.ActivityStatus;

import org.junit.Test;

import com.aton.test.UnitTest;

public class ActivityTest  extends UnitTest{

 
 
	@Test
	public void test_findByPage() {
		List<Activity> page = Activity.selectList();
		assertEquals(page.size(), 1);
	}
	 
	
	@Test
	public void test_save() {
		
		Activity a = new Activity();
		a.bannerPic="pic";
		a.createTime=new Date();
		a.endTime = new Date();
		a.modifyTime=new Date();
		a.startTime=new Date();
		a.pageUrl="pagelink";
		a.status=ActivityStatus.INVALID;
		a.title="title";
		a.bizType=ActivityBizType.INVITE_REG;
		a.save();
		
	}
}
