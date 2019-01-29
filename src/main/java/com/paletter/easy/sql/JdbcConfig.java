package com.paletter.easy.sql;

import java.io.InputStream;
import java.util.Enumeration;
import java.util.Properties;

public class JdbcConfig {

	private static String drive;
	private static String url;
	private static String user;
	private static String pwd;
	
	private JdbcConfig() {}
	
	public static void loadJdbcProperties() {
		try {

			InputStream jdbcStream = Thread.currentThread().getContextClassLoader().getResourceAsStream("jdbc.properties");
			Properties p = new Properties();
			p.load(jdbcStream);
			Enumeration<Object> es = p.keys();
			while (es.hasMoreElements()) {
				String key = (String) es.nextElement();
				String value = (String) p.get(key);
				
				if ("drive".equals(key)) drive = value;
				if ("url".equals(key)) url = value;
				if ("username".equals(key)) user = value;
				if ("password".equals(key)) pwd = value;
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public static boolean isComplete() {
		return drive != null && url != null && user != null && pwd != null;
	}
	
	public static String getDrive() {
		return drive;
	}

	public static String getUrl() {
		return url;
	}

	public static String getUser() {
		return user;
	}

	public static String getPwd() {
		return pwd;
	}

	public static void setDrive(String drive) {
		JdbcConfig.drive = drive;
	}

	public static void setUrl(String url) {
		JdbcConfig.url = url;
	}

	public static void setUser(String user) {
		JdbcConfig.user = user;
	}

	public static void setPwd(String pwd) {
		JdbcConfig.pwd = pwd;
	}
	
}
