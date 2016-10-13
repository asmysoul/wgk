package models.mappers;

import java.util.List;
import java.util.Map;

import models.Notice;
import models.Notice.Role;

import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import vos.NoticeSearchVo;

public interface NoticeMapper {
	
	List<Notice> selectList(NoticeSearchVo vo);
    int count(NoticeSearchVo vo);
	
	@Select("select * from " + Notice.TABLE_NAME + " where id=#{id}")
	Notice selectById(Long id);
	
	@Insert("insert into " + Notice.TABLE_NAME + "(type,title,url,content,is_display,role,sort_num,admin_id,create_time,top_time) "
			+ "values(#{type},#{title},#{url},#{content},#{isDisplay},#{role},#{sortNum},#{adminId},#{createTime},#{topTime})")
	void insert(Notice notice);
	
	Integer updateById(Notice notice);
	
	@Deprecated
	@Delete("delete from "+ Notice.TABLE_NAME + " where id=#{id}")
	void deleteById(@Param("id") long id);
	
	List<Notice> selectDisplayList(@Param("num") int num,@Param("role") Role role);

	@Select("select * from " + Notice.TABLE_NAME + " order by top_time desc limit " + 1)
	Notice selectTopList();

}
