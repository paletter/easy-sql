package com.paletter.easy.sql;

import java.sql.Connection;
import java.sql.DriverManager;

import com.paletter.easy.sql.utils.LogUtil;

public class EasyConnection {

	private static Connection conn;
	
	static {
		
		JdbcConfig.loadJdbcProperties();
		
		if (JdbcConfig.isComplete())
			connection(JdbcConfig.getDrive(), JdbcConfig.getUrl(), JdbcConfig.getUser(), JdbcConfig.getPwd());
	}
	
	public static void connection(String drive, String url, String user, String pwd) {
		try {
			Class.forName(drive);
			conn = DriverManager.getConnection(url, user, pwd);
		} catch (Exception e) {
			LogUtil.error("EasyConnection.connection error", e);
		}
	}
	
	public static Connection getConn() {
		return conn;
	}
}
