package com.paletter.easy.sql;

import java.lang.reflect.Method;
import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.paletter.easy.sql.utils.DateUtils;

public class EasyQuery {

	public static <T> T queryOne(String sql, Class<T> retrurnClass, Object... params) {
		return queryOne(EasyConnection.getConn(), sql, retrurnClass, params);
	}
	
	@SuppressWarnings("unchecked")
	public static <T> T queryOne(Connection conn, String sql, Class<T> retrurnClass, Object... params) {

		try {
			
			PreparedStatement stat = conn.prepareStatement(sql);
			if (params != null) {
				int paramIndex = 1;
				for (Object o : params) {
					stat.setObject(paramIndex ++, o);
				}
			}
			
			ResultSet rs = stat.executeQuery();
			while (rs.next()) {
				
				if (retrurnClass.equals(Integer.class)) {
					return (T) Integer.valueOf(rs.getInt(1));
				}
				if (retrurnClass.equals(String.class)) {
					if (rs.getString(1) == null) return null;
					return (T) String.valueOf(rs.getString(1));
				}
				if (retrurnClass.equals(BigDecimal.class)) {
					if (rs.getString(1) == null) return null;
					return (T) new BigDecimal(rs.getString(1));
				}

				T data = retrurnClass.newInstance();
				for (Method m : retrurnClass.getMethods()) {
					if (m.getName().startsWith("set")) {
						
						// Replace upper to underline
						Pattern p = Pattern.compile("[A-Z]");
						StringBuilder colName = new StringBuilder(m.getName().substring(3));
						Matcher mat = p.matcher(m.getName().substring(3));
						int i = 0;
						while (mat.find()) {
							colName.replace(mat.start() + i, mat.end() + i, "_" + mat.group().toLowerCase());
							i ++;
						}
						if (colName.charAt(0) == '_') colName.deleteCharAt(0);
						
						// Set column value
						if (rs.findColumn(colName.toString()) > 0 && rs.getObject(colName.toString()) != null) {
							if (m.getParameterTypes()[0].equals(Date.class)) {
								String date = rs.getString(colName.toString());
								m.invoke(data, DateUtils.parse(date, "yyyy-MM-dd HH:mm:ss"));
							} else {
								m.invoke(data, rs.getObject(colName.toString()));
							}
						}
					}
				}

				return data;
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		return null;
	}
	
	public static <T> List<T> queryList(String sql, Class<T> retrurnClass, Object... params) {
		return queryList(EasyConnection.getConn(), sql, retrurnClass, params);
	}
	
	public static <T> List<T> queryList(Connection conn, String sql, Class<T> retrurnClass, Object... params) {
		
		List<T> list = new ArrayList<T>();
		
		try {
			
			PreparedStatement stat = conn.prepareStatement(sql);
			if (params != null) {
				int paramIndex = 1;
				for (Object o : params) {
					stat.setObject(paramIndex ++, o);
				}
			}
			
			ResultSet rs = stat.executeQuery();
			while (rs.next()) {
				
				T data = retrurnClass.newInstance();
				for (Method m : retrurnClass.getMethods()) {
					if (m.getName().startsWith("set")) {
						
						// Replace upper to underline
						Pattern p = Pattern.compile("[A-Z]");
						StringBuilder colName = new StringBuilder(m.getName().substring(3));
						Matcher mat = p.matcher(m.getName().substring(3));
						int i = 0;
						while (mat.find()) {
							colName.replace(mat.start() + i, mat.end() + i, "_" + mat.group().toLowerCase());
							i ++;
						}
						if (colName.charAt(0) == '_') colName.deleteCharAt(0);
						
						// Set column value
						if (rs.findColumn(colName.toString()) > 0 && rs.getObject(colName.toString()) != null) {
							if (m.getParameterTypes()[0].equals(Date.class)) {
								String date = rs.getString(colName.toString());
								m.invoke(data, DateUtils.parse(date, "yyyy-MM-dd HH:mm:ss"));
							} else {
								m.invoke(data, rs.getObject(colName.toString()));
							}
						}
					}
				}
				
				list.add(data);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		return list;
	}
}
