package models.mappers;

import java.util.List;

import models.Shop;
import models.SysConfig;
import models.SysConfig.SysConfigKey;

import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;


/**
 * 
 * 系统配置
 * 
 * @author fufei
 * @since  v2.9
 * @created 2015年3月26日 下午1:24:59
 */
public interface SysConfigMapper {
    @Insert("insert into " + SysConfig.TABLE_NAME + "(key,value,record,modify_time) values(#{key},#{value},#{record},#{modifyTime})")
    void insert(SysConfig config);
    @Select("select * from "+ SysConfig.TABLE_NAME + " where id=#{id}")
    SysConfig findById(long id);
    
    @Select("select * from "+ SysConfig.TABLE_NAME + " where `key`=#{keys}")
    SysConfig findByKey(@Param("keys")SysConfigKey key);
    
    @Select("select * from "+ SysConfig.TABLE_NAME)
    List<SysConfig> findAll();
    
    @Update("update "+ SysConfig.TABLE_NAME + " set value=#{value} ,record=#{record},modify_time=#{modifyTime} where id=#{id}")
    int update(SysConfig config);
    
}
