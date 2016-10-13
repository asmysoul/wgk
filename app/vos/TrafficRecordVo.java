package vos;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import org.joda.time.DateTime;

import com.aton.util.StringUtils;

import enums.Platform;
import enums.Platform2;
import enums.Platform3;
import enums.TaskType;
import enums.TaskType2;
import enums.TaskType3;
import models.Task;
import models.Task2;
import models.Task3;
import models.TrafficRecord;
import models.TrafficRecord.ShopType;
import models.TrafficRecord.TrafficStatus;

public class TrafficRecordVo extends Page{
	public long id;
	public String kwd;// 关键词
	public String nid;// 宝贝Id
	public String shopType;
	public String type;
	public Integer times;//计划点击数
	public Integer path1;//淘宝搜索路径占比(3路径占比之和为100)
	public Integer path2;//淘宝搜天猫路径占比(C店商品不可设置)
	public Integer path3;//天猫搜索路径占比(C店商品不可设置)
	public Integer sleepTime;//宝贝页停留时间
	public Integer clickStart;//每日开始时间(如8点)
	public Integer clickEnd;//每日结束时间(如24点)
	public String beginTime;//开始日期(如2014-09-23)
	public String endTime;//截止日期(如2014-09-23)
	public long kid;//关键词ID
	public String taskId;
	public String status;//状态
	public Integer returnTimes;//实际点击次数
	public Date createTime;
	public Date modifyTime;
	public long userId;
	public String userNick;//用户昵称
	
	public List<ListKwds> listKeyWords;
	
	public class ListKwds{
	    public int clickTimes;
	    public String keywords;
	}
	
	
	public static TrafficRecordVo newInstance(){
		return new TrafficRecordVo();
	}
	/**
	 * 
	 * 初始化流量信息
	 *
	 * @param recordVo
	 * @return
	 * @since  v2.7
	 * @author fufei
	 * @created 2015年3月7日 下午2:40:47
	 */
	public static TrafficRecordVo init(Task tasks, long flowNum, String word) {
		TrafficRecordVo trafficRecord = TrafficRecordVo.newInstance();
		SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
		trafficRecord.kwd = StringUtils.trim(word);
		trafficRecord.nid = tasks.itemId;
		if (tasks.platform == Platform.TAOBAO || tasks.platform == Platform.TMALL){
		    if(tasks.type==TaskType.SUBWAY)
		        trafficRecord.type = TrafficRecord.ShopType.TBAD.toString();
		    else
		        trafficRecord.type = TrafficRecord.ShopType.TAOBAOPC.toString();
		}
		if (tasks.platform == Platform.JD)
			trafficRecord.type = TrafficRecord.ShopType.JDPC.toString();
		if (tasks.platform == Platform.TAOBAO)
			trafficRecord.shopType = "c";
		if (tasks.platform == Platform.TMALL)
			trafficRecord.shopType = "b";
		trafficRecord.times = Integer.parseInt(String.valueOf(flowNum));
		trafficRecord.path1 = 100;
		trafficRecord.path2 = 0;
		trafficRecord.path3 = 0;
		trafficRecord.sleepTime = 100;
		trafficRecord.clickStart = DateTime.now().toDate().getHours();
		trafficRecord.clickEnd = 23;
		trafficRecord.beginTime = format.format(DateTime.now().toDate());
		trafficRecord.endTime = format.format(DateTime.now().toDate());
		if(DateTime.now().toDate().getHours()>=22){
            trafficRecord.clickStart = 8;
            trafficRecord.beginTime = format.format(DateTime.now().plusDays(1).toDate());
            trafficRecord.endTime = format.format(DateTime.now().plusDays(1).toDate());
        }
		trafficRecord.taskId = String.valueOf(tasks.id);
		trafficRecord.userId = tasks.sellerId;
		return trafficRecord;
	}
	
	
	
	/**
	 * 
	 * 初始化流量信息
	 *
	 * @param recordVo
	 * @return
	 * @since  v2.7
	 * @author fufei
	 * @created 2015年3月7日 下午2:40:47
	 */
	public static TrafficRecordVo init(Task3 tasks, long flowNum, String word) {
		TrafficRecordVo trafficRecord = TrafficRecordVo.newInstance();
		SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
		trafficRecord.kwd = StringUtils.trim(word);
		trafficRecord.nid = tasks.itemId;
		if (tasks.platform == Platform3.TAOBAO || tasks.platform == Platform3.TMALL){
		    if(tasks.type==TaskType3.SUBWAY)
		        trafficRecord.type = TrafficRecord.ShopType.TBAD.toString();
		    else
		        trafficRecord.type = TrafficRecord.ShopType.TAOBAOPC.toString();
		}
		if (tasks.platform == Platform3.TAOBAO)
			trafficRecord.shopType = "c";
		if (tasks.platform == Platform3.TMALL)
			trafficRecord.shopType = "b";
		trafficRecord.times = Integer.parseInt(String.valueOf(flowNum));
		trafficRecord.path1 = 100;
		trafficRecord.path2 = 0;
		trafficRecord.path3 = 0;
		trafficRecord.sleepTime = 100;
		trafficRecord.clickStart = DateTime.now().toDate().getHours();
		trafficRecord.clickEnd = 23;
		trafficRecord.beginTime = format.format(DateTime.now().toDate());
		trafficRecord.endTime = format.format(DateTime.now().toDate());
		if(DateTime.now().toDate().getHours()>=22){
            trafficRecord.clickStart = 8;
            trafficRecord.beginTime = format.format(DateTime.now().plusDays(1).toDate());
            trafficRecord.endTime = format.format(DateTime.now().plusDays(1).toDate());
        }
		trafficRecord.taskId = String.valueOf(tasks.id);
		trafficRecord.userId = tasks.sellerId;
		return trafficRecord;
	}
	
	
	
	
	
	/**
	 * 
	 * 初始化流量信息
	 *
	 * @param recordVo
	 * @return
	 * @since  v2.7
	 * @author fufei
	 * @created 2015年3月7日 下午2:40:47
	 */
	public static TrafficRecordVo init2(Task2 tasks, long flowNum, String word) {
		TrafficRecordVo trafficRecord = TrafficRecordVo.newInstance();
		SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
		trafficRecord.kwd = StringUtils.trim(word);
		trafficRecord.nid = tasks.itemId;
		if (tasks.platform == Platform2.WEIXIN ){
		    if(tasks.type==TaskType2.QUNFA)
		        trafficRecord.type = TrafficRecord.ShopType.TBAD.toString();
		    else
		        trafficRecord.type = TrafficRecord.ShopType.TAOBAOPC.toString();
		}
		if (tasks.platform == Platform2.WEIBO)
			trafficRecord.type = TrafficRecord.ShopType.JDPC.toString();
		if (tasks.platform == Platform2.WEIXIN)
			trafficRecord.shopType = "c";
		if (tasks.platform == Platform2.QQ)
			trafficRecord.shopType = "b";
		trafficRecord.times = Integer.parseInt(String.valueOf(flowNum));
		trafficRecord.path1 = 100;
		trafficRecord.path2 = 0;
		trafficRecord.path3 = 0;
		trafficRecord.sleepTime = 100;
		trafficRecord.clickStart = DateTime.now().toDate().getHours();
		trafficRecord.clickEnd = 23;
		trafficRecord.beginTime = format.format(DateTime.now().toDate());
		trafficRecord.endTime = format.format(DateTime.now().toDate());
		if(DateTime.now().toDate().getHours()>=22){
            trafficRecord.clickStart = 8;
            trafficRecord.beginTime = format.format(DateTime.now().plusDays(1).toDate());
            trafficRecord.endTime = format.format(DateTime.now().plusDays(1).toDate());
        }
		trafficRecord.taskId = String.valueOf(tasks.id);
		trafficRecord.userId = tasks.sellerId;
		return trafficRecord;
	}
	
	
	
	/**
	 * 
	 * 初始化流量信息
	 *
	 * @param recordVo
	 * @return
	 * @since  v2.7
	 * @author fufei
	 * @created 2015年3月7日 下午2:40:47
	 */
	public static TrafficRecordVo init3(Task3 tasks, long flowNum, String word) {
		TrafficRecordVo trafficRecord = TrafficRecordVo.newInstance();
		SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
		trafficRecord.kwd = StringUtils.trim(word);
		trafficRecord.nid = tasks.itemId;
		if (tasks.platform == Platform3.TAOBAO || tasks.platform == Platform3.TMALL){
		    if(tasks.type==TaskType3.SUBWAY)
		        trafficRecord.type = TrafficRecord.ShopType.TBAD.toString();
		    else
		        trafficRecord.type = TrafficRecord.ShopType.TAOBAOPC.toString();
		}
		if (tasks.platform == Platform3.TAOBAO)
			trafficRecord.shopType = "c";
		if (tasks.platform == Platform3.TMALL)
			trafficRecord.shopType = "b";
		trafficRecord.times = Integer.parseInt(String.valueOf(flowNum));
		trafficRecord.path1 = 100;
		trafficRecord.path2 = 0;
		trafficRecord.path3 = 0;
		trafficRecord.sleepTime = 100;
		trafficRecord.clickStart = DateTime.now().toDate().getHours();
		trafficRecord.clickEnd = 23;
		trafficRecord.beginTime = format.format(DateTime.now().toDate());
		trafficRecord.endTime = format.format(DateTime.now().toDate());
		if(DateTime.now().toDate().getHours()>=22){
            trafficRecord.clickStart = 8;
            trafficRecord.beginTime = format.format(DateTime.now().plusDays(1).toDate());
            trafficRecord.endTime = format.format(DateTime.now().plusDays(1).toDate());
        }
		trafficRecord.taskId = String.valueOf(tasks.id);
		trafficRecord.userId = tasks.sellerId;
		return trafficRecord;
	}
}
