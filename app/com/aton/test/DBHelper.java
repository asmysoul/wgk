package com.aton.test;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.Validate;

import com.google.common.base.Strings;

import play.db.DB;

/**
 * 
 * 测试用DB工具类.
 * 
 * @author youblade
 * @since  v0.3.5
 * @created 2013-11-7 下午3:53:23
 */
public class DBHelper {
	
	public static void truncate(String table) {
		if (StringUtils.isBlank(table)) {
			throw new IllegalArgumentException("Input Table name error!");
		}
		String SQL = "truncate table " + table;
		DB.execute(SQL);
	}
	
	public static void truncate(String... tables) {
	    Validate.notEmpty(tables);
	    for (String table : tables) {
	        String SQL = "truncate table " + table;
	        DB.execute(SQL);
        }
	}
	
}
