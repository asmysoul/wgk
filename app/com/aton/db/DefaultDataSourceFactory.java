package com.aton.db;

import java.util.Properties;

import javax.sql.DataSource;

import org.apache.ibatis.datasource.DataSourceFactory;

import play.db.DB;

/**
 * 供MyBatis构造DataSource使用的默认DataSourceFactory.<br>
 * (直接使用Play自带的C3P0 DataSource实现)<br>
 * 
 * @author youblade
 * @since v0.1
 */
public class DefaultDataSourceFactory implements DataSourceFactory {

	public void setProperties(Properties properties) {
	}

	public DataSource getDataSource() {
		return DB.getDataSource();
	}
}
