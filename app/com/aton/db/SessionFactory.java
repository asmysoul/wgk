package com.aton.db;

import java.io.IOException;
import java.io.Reader;
import java.util.Map;
import java.util.Properties;

import org.apache.ibatis.io.Resources;
import org.apache.ibatis.session.ExecutorType;
import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactory;
import org.apache.ibatis.session.SqlSessionFactoryBuilder;

import com.aton.config.Config;
import com.aton.util.StringUtils;
import com.google.common.collect.Maps;

/**
 * This class provides convenient access to SqlSessionFactory. 
 * It will read database settings from play configuration file (application.conf)
 * 
 * @author bftanase@gmail.com
 * 
 * 1.One SqlSessionFactory instance per database 
 * 2.Reuse SqlSessionFactory
 * 3.如果获取到的session未启动autoCommit,则innodb下需要显式调用session.commit()
 * 
 */
public enum SessionFactory {

	INSTANCE("mybatis.config");

	// MyBatis SqlSessionFactory instance for local DB
	private SqlSessionFactory defaultSqlSessionFactory;

	SessionFactory(String configFile) {
		String resource = Config.getProperty(configFile);
		Reader reader = null;
		try {
			reader = Resources.getResourceAsReader(resource);
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
		defaultSqlSessionFactory = new SqlSessionFactoryBuilder().build(reader);
	}

	/**
	 * 
	 * 读取mybatis.config中的配置的DataSource，其中具体的DS配置使用application.conf中的参数
	 * 
	 * @param needBatch
	 * @param autoCommit
	 * @return
	 * @since v0.1
	 * @author youblade
	 */
	private static SqlSession getSqlSession(boolean needBatch, boolean autoCommit) {
		if (needBatch) {
			return INSTANCE.defaultSqlSessionFactory.openSession(ExecutorType.BATCH, false);
		}
		return INSTANCE.defaultSqlSessionFactory.openSession(autoCommit);
	}

	/**
	 * 
	 * 获取用于执行批量处理的SqlSession.
	 * 
	 * @return
	 * @since v0.1
	 * @author youblade
	 */
	public static SqlSession getSqlSessionForBatch() {
		return getSqlSession(true, false);
	}

	/**
	 * 
	 * 获取需要手动控制事务的SqlSession：非自动提交，Innodb下操作完毕后需要显式调用session.commit().
	 * 
	 * @return
	 * @since v0.1
	 * @author youblade
	 */
	public static SqlSession getSqlSessionWithoutAutoCommit() {
		return getSqlSession(false, false);
	}

	/**
	 * 
	 * 获取默认操作的SqlSession：非批量处理、自动提交
	 * 
	 * @return
	 * @since v0.1
	 * @author youblade
	 */
	public static SqlSession getSqlSession() {
		return getSqlSession(false, true);
	}
}
