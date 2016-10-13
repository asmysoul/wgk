package controllers;

import java.util.List;

import models.TrafficRecord;
import models.AdminUser.AdminType;
import models.TrafficRecord.TrafficStatus;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import play.data.validation.Max;
import play.data.validation.Min;
import play.data.validation.Required;
import play.libs.Codec;
import play.mvc.With;
import vos.FlowTimesVo;
import vos.Page;
import vos.TrafficRecordVo;

import com.aton.base.BaseController;
import com.aton.base.secure.Secure;
import com.aton.util.CollectionUtils;
import com.aton.util.NumberUtils;
import com.aton.util.StringUtils;


/**
 * 
 * 流量任务接口
 * 
 * @author fufei
 * @since  v2.9
 * @created 2015年4月8日 上午11:34:27
 */
@With(Secure.class)
public class Flow extends BaseController{
    private static final Logger log = LoggerFactory
        .getLogger(Flow.class);

    public static void login() {
        renderArgs.put("msg", flash.get("msg"));
        render();
    }
    
    public static void doLogin(@Required String usr, @Required String pass,
            @Max(30) int duration) {
        if (validation.hasErrors()) {
            flash.put("msg", "用户名或密码不正确！");
            login();
        }
    
        // 快递打印登录
        if ("flow".equals(usr.trim()) && "flow".equals(pass.trim())) {
            String code = StringUtils.replace(Codec.UUID(), "-", "");
            int num1 = NumberUtils.getRandomBetween(0, 8);
            int num2 = 8 - num1;
            code = code.substring(0, 1) + num1 + code.substring(1) + num2;
    
            // 默认免登录有效时间为1天
            String durationDays = Math.max(1, duration) + "d";
            response.setCookie(Secure.FLAG_FLOW_AUTH, code, durationDays);
            //待处理
            flowRecord();
        }
        log.error("Try login failed:user={},pass={}", usr, pass);
        login();
    }
    
    /**
     * 
     * 流量记录页面
     * 
     * @since v2.0
     * @author fufei
     * @created 2015年1月14日 下午2:42:34
     */
    public static void flowRecord() {
        render();
    }
    /**
     * 
     * 处理中
     *
     * @since  v2.9
     * @author fufei
     * @created 2015年4月9日 上午11:52:02
     */
    public static void flowProcessRecord() {
        render();
    }
    /**
     * 
     *统计
     *
     * @since  v2.9
     * @author fufei
     * @created 2015年4月9日 上午11:56:31
     */
    public static void clickCount() {
        render();
    }
    
    /**
     * 
     * 已完成流量记录页面
     * 
     * @since v2.0
     * @author fufei
     * @created 2015年1月14日 下午2:42:34
     */
    public static void flowFinishedRecord() {
        render();
    }


    /**
     * 
     * 分页获取流量数据
     * 
     * @since v2.0
     * @author fufei
     * @created 2015年1月14日 下午2:00:05
     */
    public static void listFlowRecord(@Required TrafficRecordVo vo) {
        handleWrongInput(false);
        if (vo != null) {
            validation.required("vo.pageNo", vo.pageNo);
            validation.required("vo.pageSize", vo.pageSize);
        }
        vo.type="TAOBAOMOBILE";
        Page<TrafficRecord> page = TrafficRecord.listTrafficRecord(vo);
        renderPageJson(page.items, page.totalCount);
    }
    /**
     * 
     * 确认点击完成或处理中
     *
     * @since  v2.9
     * @author fufei
     * @created 2015年4月8日 下午3:00:07
     */
    public static void editFlowFinished(@Required @Min(1)long id,@Required String status) {
        handleWrongInput(false);
        validation.required("id", id);
        TrafficRecordVo vo=TrafficRecordVo.newInstance();
        vo.id=id;
        List<TrafficRecord> records=TrafficRecord.listTrafficRecord(vo).items;
        if(CollectionUtils.isEmpty(records)){
            renderFailedJson(-2, "该流量记录不存在！");
        }
        TrafficRecordVo record=TrafficRecordVo.newInstance();
        record.id=id;
        if("FINISHED".equals(StringUtils.trim(status))){
            record.returnTimes=records.get(0).times;
        }else {
            record.returnTimes=0;
        }
        record.status=status;
        TrafficRecord.updateTraffic(record);
        renderSuccessJson();
    }
    
    /**
     * 
     *不能 点击（可能没找到）
     *
     * @since  v2.9
     * @author fufei
     * @created 2015年4月8日 下午3:00:07
     */
    public static void editNotFlowFinished(@Required @Min(1)long id,@Required String status) {
        handleWrongInput(false);
        validation.required("id", id);
        TrafficRecordVo vo=TrafficRecordVo.newInstance();
        vo.id=id;
        List<TrafficRecord> records=TrafficRecord.listTrafficRecord(vo).items;
        if(CollectionUtils.isEmpty(records)){
            renderFailedJson(-2, "该流量记录不存在！");
        }
        TrafficRecordVo record=TrafficRecordVo.newInstance();
        record.id=id;
        record.returnTimes=0;
        record.status=status;
        TrafficRecord.updateTraffic(record);
        renderSuccessJson();
    }
    /**
     * 
     * 批量确认完成流量任务
     *
     * @param ids
     * @since  v3.2
     * @author fufei
     * @created 2015年4月9日 上午10:36:53
     */
    public static void batchFlowFinished(@Required String ids,@Required String status) {
        handleWrongInput(false);
        if(StringUtils.isNotEmpty(ids)){
            StringBuffer sb=new StringBuffer(ids);
            sb.append("0");
            if("FINISHED".equals(status)){
             List<TrafficRecordVo> list= TrafficRecord.batchFindFlowFinished(sb.toString());
             for (TrafficRecordVo vo : list) {
                vo.returnTimes=vo.times;
                vo.status=status;
                TrafficRecord.updateTraffic(vo);
            }
            }else {
            TrafficRecord.batchFlowFinished(status,sb.toString());
            }
        }
        renderSuccessJson();
    }
    
    /**
     * 
     * 统计每天点击次数
     *
     * @param ids
     * @since  v2.9
     * @author fufei
     * @created 2015年4月9日 上午10:36:53
     */
    public static void totalTimesCount(@Required FlowTimesVo vo) {
        handleWrongInput(false);
        Page<FlowTimesVo> page=TrafficRecord.totalTrafficCount(vo);
        renderPageJson(page.items, page.totalCount);
    }
}
