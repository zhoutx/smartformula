package com.epri.dlsc.sbs.inter.db;

import java.sql.SQLException;
import java.util.List;

public interface DBCollection {
	/**
	 * 执行sql
	 * @param sql sql语句
	 * @param params 参数
	 * @return
	 */
	public boolean executeSql(String sql, Object[] params) throws SQLException;
	/**
	 * 执行sql
	 * @param sql sql语句
	 * @return
	 */
	public boolean executeSql(String sql) throws SQLException;
	/**
	 * 按批处理执行sql
	 * @param sql sql语句
	 * @param params 参数
	 * @return
	 */
	public boolean batchExecuteSql(String sql, List<Object[]> params) throws SQLException;
	
}
