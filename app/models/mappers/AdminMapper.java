package models.mappers;

import java.util.List;

import models.AdminUser;
import models.User;

import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Select;

import vos.AdminSearchVo;
import vos.UserSearchVo;

/**
 * 
 * Admin类的Mybatis映射类
 * 
 * @author Mark Xu
 * @since  v2.0
 * @created 2015-1-14 上午10:34:21
 */
public interface AdminMapper {
	
	@Select("select * from " + AdminUser.TABLE_NAME + " where name=#{name}")
	AdminUser selectByName(String name);
	
	@Select("select * from " + AdminUser.TABLE_NAME + " where id=#{id}")
	AdminUser selectById(long id);
	
	int count(AdminSearchVo vo);
	
	List<AdminUser> selectListForAdmin(AdminSearchVo vo);
	
	void updateByAdmin(AdminUser admin);

	@Insert("insert into "
			+ AdminUser.TABLE_NAME
			+ " (name,salt,password,type,qq,email,mobile,status,message) "
			+ "values(#{name},#{salt},#{password},#{type},#{qq},#{email},#{mobile},#{status},#{message})")
	void insertByAdmin(AdminUser admin);

}
