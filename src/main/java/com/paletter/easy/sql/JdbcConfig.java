package com.paletter.easy.sql;

import java.io.InputStream;
import java.util.Enumeration;
import java.util.Properties;

import org.apache.log4j.Logger;

public class JdbcConfig {

	private static Logger log = Logger.getLogger(JdbcConfig.class);
	
	private static String drive;
	private static String url;
	private static String user;
	private static String pwd;
	
	private JdbcConfig() {}
	
	public static void loadJdbcProperties() {
		try {

			InputStream jdbcStream = Thread.currentThread().getContextClassLoader().getResourceAsStream("jdbc.properties");
			if (jdbcStream == null || jdbcStream.available() <= 0) {
				log.info("Load jdbc file fail. jdbc file not exist.");
				return;
			}
			
			Properties pro = new Properties();
			pro.load(jdbcStream);
			Enumeration<Object> es = pro.keys();
			while (es.hasMoreElements()) {
				String key = (String) es.nextElement();
				String value = (String) pro.get(key);
				
				if ("drive".equals(key)) drive = value;
				if ("url".equals(key)) url = value;
				if ("username".equals(key)) user = value;
				if ("password".equals(key)) pwd = value;
			}
		} catch (Exception e) {
			log.error("Load jdbc file error", e);
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
