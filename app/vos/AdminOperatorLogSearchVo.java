package vos;

import java.util.Date;

import models.AdminOperatorLog.LogType;
import models.AdminUser.AdminStatus;
import models.AdminUser.AdminType;
import models.User.UserStatus;
import models.User.UserType;

/**
 * 
 * 用于查询管理员操作记录查询类
 * 
 * @author Mark Xu
 * @since  v2.0
 * @created 2015-1-15 下午5:57:48
 */
public class AdminOperatorLogSearchVo extends Page{
	public long id;
    public String adminAccount;
    public LogType logType;
    public String message;
    public Date operatorTimeStart;
    public Date operatorTimeEnd;
    public static AdminOperatorLogSearchVo newInstance(){
        AdminOperatorLogSearchVo vo = new AdminOperatorLogSearchVo();
        return vo;
    }
    
    public AdminOperatorLogSearchVo logType(LogType type){
        this.logType = type;
        return this;
    }
}
