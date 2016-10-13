package vos;

import enums.Device;
import enums.ExpressType;
import enums.Platform;
import enums.TaskStatus;

/**
 * 
 * 
 * 快递统计
 * 
 * @author fufei
 * @since  v3.4
 * @created 2015年5月6日 上午11:33:46
 */
public class ExpressCountVo extends Page{
    public int ydkd;
    public int kjkd;
    public int sellerKd;
    public int fabaoguo;
    public String takeTime;
    
    //查询条件
    public ExpressType expressType;
    public String sellerNick;
    public String startTime;
    public String endTime;
    public static ExpressCountVo newInstance() {
        return new ExpressCountVo();
    }
   
}
