package models.mappers;

import java.util.Date;
import java.util.List;

import models.AdminOperatorLog;
import models.AdminTradeLog;
import models.AdminTradeLog.AdminTradeType;
import models.AdminUser;
import models.Notice;
import models.Notice.Role;
import models.PayTradeLog;
import models.PayTradeLog.FinanceLogVo;
import models.User;

import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import vos.AdminOperatorLogSearchVo;
import vos.NoticeSearchVo;

public interface AdminTradeLogMapper {
	
	
	@Insert("insert into " + AdminTradeLog.TABLE_NAME + "(type,sign,amount,user_id,memo,create_time,admin_id) "
			+ "values(#{type},#{sign},#{amount},#{userId},#{memo},#{createTime},#{adminId})")
	void insert(AdminTradeLog adminTradeType);

	@Select("SELECT l1.dates DATE, l1.amount amount, l2.amount platAmount, l3.amount adminAmount FROM(SELECT d1.dates,SUM(d1.amount) amount FROM(SELECT DATE_FORMAT(modify_time, '%Y-%m-%d') dates,amount FROM " + PayTradeLog.TABLE_NAME +" WHERE result='OK'  UNION ALL SELECT DATE_FORMAT(create_time, '%Y-%m-%d') dates, amount FROM " + AdminTradeLog.TABLE_NAME + " WHERE SIGN = 'PLUS') d1 GROUP BY d1.dates)l1 LEFT JOIN(SELECT DATE_FORMAT(modify_time, '%Y-%m-%d') dates,SUM(amount) amount,TYPE FROM " + PayTradeLog.TABLE_NAME + " WHERE result='OK' GROUP BY dates)l2 ON l1.dates=l2.dates LEFT JOIN(SELECT DATE_FORMAT(create_time, '%Y-%m-%d') dates,SUM(amount) amount,TYPE FROM " + AdminTradeLog.TABLE_NAME + " WHERE SIGN='PLUS' GROUP BY dates)l3 ON l1.dates=l3.dates ORDER BY l1.dates desc limit #{startIndex}, #{pageSize}")
	List<FinanceLogVo> selectListForFinance(FinanceLogVo vo);

	@Select("SELECT COUNT(*) FROM(SELECT d1.dates,SUM(d1.amount) amount FROM(SELECT DATE_FORMAT(modify_time, '%Y-%m-%d') dates,amount FROM " + PayTradeLog.TABLE_NAME +" WHERE result='OK'  UNION ALL SELECT DATE_FORMAT(create_time, '%Y-%m-%d') dates, amount FROM " + AdminTradeLog.TABLE_NAME + " WHERE SIGN = 'PLUS') d1 GROUP BY d1.dates)l1")
	int count(FinanceLogVo vo);
	
	@Select("SELECT count(*) FROM " + AdminTradeLog.TABLE_NAME + " WHERE SIGN = 'PLUS' AND DATE_FORMAT(create_time, '%Y-%m-%d') = #{date}")
	int adminCount(@Param("date")String date);
	
	@Select("SELECT l.*, a.name adminName,u.nick userNick,u.type userType FROM " + AdminTradeLog.TABLE_NAME + " l left join " + AdminUser.TABLE_NAME + " a on a.id=l.admin_id left join " + User.TABLE_NAME + " u on l.user_id=u.id WHERE SIGN = 'PLUS' AND DATE_FORMAT(l.create_time, '%Y-%m-%d') = #{date}")
	List<AdminTradeLog> adminCountList(@Param("date")String date);
}
