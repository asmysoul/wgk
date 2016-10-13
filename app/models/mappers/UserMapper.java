package models.mappers;

import java.util.Date;
import java.util.List;

import models.AdminUser;
import models.BuyerConfig;
import models.BuyerTask;
import models.SellerConfig;
import models.User;

import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.SelectKey;
import org.apache.ibatis.annotations.Update;

import vos.UserSearchVo;

public interface UserMapper {

    @Select("select * from " + User.TABLE_NAME + " where nick=#{nick}")
    User selectByNick(String nick);

    @Select("select u.*, a.name,bc.is_clear_view, sc.buyer_and_seller_time, sc.buyer_and_shop_time, sc.buyer_acount_and_shop_time, sc.buyer_acount_and_item_time from " + User.TABLE_NAME + " u left join " + AdminUser.TABLE_NAME + " a on u.admin_id = a.id left join " + SellerConfig.TABLE_NAME + " sc on u.id = sc.seller_id left join " +BuyerConfig.TABLE_NAME + " bc on bc.buyer_id=u.id where u.id=#{id}")
    User selectById(Long id);
    

    @Insert("insert into "
			+ User.TABLE_NAME
			+ "(id,nick,active_code,password,salt,type,vip_status,qq,email,mobile,status,create_time,modify_time) "
			+ "values(#{id},#{nick},#{activeCode},#{password},#{salt},#{type},#{vipStatus},#{qq},#{email},#{mobile},#{status},#{createTime},#{modifyTime})")
    @SelectKey(before = false, resultType = Long.class, keyProperty = "id", statement = { "select LAST_INSERT_ID() as id" })
	void insert(User user);


    @Select("select * from " + User.TABLE_NAME + " where email=#{mail} limit 1")
	User selectByMail(String mail);

    @Select("select * from " + User.TABLE_NAME + " where active_code=#{activeCode} limit 1")
	User selectByActiveCode(String activeCode);

    @Select("select * from " + User.TABLE_NAME + " where id = (select seller_id from " + BuyerTask.TABLE_NAME + " where task_id = #{taskId})")
    User selectByTaskId(long taskId);
    
    @Update("update " + User.TABLE_NAME + " set active_code=null where id=#{id}")
	void updateActiveCode(long id);
    
    void updateById(User user);
    
    @Update("update " + User.TABLE_NAME 
        + " set status='INVALID',due_time=null,modify_time=#{modifyTime}"
        + " where status='VALID' and due_time<#{dueTime}")
    void updateOverdue(@Param("modifyTime") Date modifyTime, @Param("dueTime") Date dueTime);
    
    @Select("select id from " + User.TABLE_NAME + " where status='VALID' and due_time<#{dueTime}")
    List<User> selectForUpdateUserStatus(@Param("modifyTime") Date modifyTime, @Param("dueTime") Date dueTime);
    
    @Select("select id,email,type,active_code_create_time,status from " + User.TABLE_NAME + " where ${field}=#{value}")
    User selectByField(@Param("field") String field, @Param("value") Object value);

    @Update("update " + User.TABLE_NAME + " set status='VALID',due_time=#{dueTime} where id=#{id}")
   	void delayUserMember(@Param("id") long id, @Param("dueTime") Date dueTime);
    
    int count(UserSearchVo vo);
    List<User> selectListForAdmin(UserSearchVo vo);

    @Update("update " + User.TABLE_NAME 
            + " set admin_id=#{adminId}, docking_message=#{dockingMessage} " +
            " where id=#{id}")
	void updateUser(@Param("adminId")long adminId, @Param("dockingMessage")String dockingMessage, @Param("id")long id);

    List<User> findUserTakenCount(@Param("createTime")String createTime);
    
    
}
