package vos;

import models.AdminUser.AdminStatus;
import models.AdminUser.AdminType;
import models.User.UserStatus;
import models.User.UserType;

/**
 * 
 * TODO Comment.
 * 
 * @author Mark Xu
 * @since  v2.0
 * @created 2015-1-15 下午5:57:48
 */
public class AdminSearchVo extends Page{
	public long id;
    public String name;
    public AdminType type;
    public String qq;
    public String email;
    public String mobile;
    public AdminStatus status;
    
    public static AdminSearchVo newInstance(){
        AdminSearchVo vo = new AdminSearchVo();
        return vo;
    }
    
    public AdminSearchVo type(AdminType type){
        this.type = type;
        return this;
    }
}
