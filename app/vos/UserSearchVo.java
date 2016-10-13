package vos;

import java.util.Date;

import models.User.UserStatus;
import models.User.UserType;
import models.User.VipStatus;


public class UserSearchVo extends Page{
    public Long id;
    public String nick;
    public String email;
    public String qq;
    public String mobile;
    public String adminName;
    public UserType type;
    public UserStatus status;
    public VipStatus vipStatus;
    public Date registTimeStart;
    public Date registTimeEnd;
    
    /**
     * 后台-邀请注册管理页面：邀请人昵称
     */
    public String inviteNick;
    public Date memberOpenTimeStart;
    public Date memberOpenTimeEnd;
    
    public static UserSearchVo newInstance(){
        UserSearchVo vo = new UserSearchVo();
        return vo;
    }
    
    public UserSearchVo type(UserType type){
        this.type = type;
        return this;
    }
    
    public UserSearchVo vipStatus(VipStatus vipStatus){
        this.vipStatus = vipStatus;
        return this;
    }
}