package models.mappers;

import java.util.List;

import models.AdminOperatorLog;
import models.Notice;
import models.Notice.Role;

import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import vos.AdminOperatorLogSearchVo;
import vos.NoticeSearchVo;

public interface AdminOperatorLogMapper {
	
	
	@Insert("insert into " + AdminOperatorLog.TABLE_NAME + "(admin_account,log_type,message,operator_time) "
			+ "values(#{adminAccount},#{logType},#{message},#{operatorTime})")
	void insert(AdminOperatorLog operatorLog);

	int count(AdminOperatorLogSearchVo vo);
	
	List<AdminOperatorLog> selectListForAdminOperatorLog(AdminOperatorLogSearchVo vo);
}
