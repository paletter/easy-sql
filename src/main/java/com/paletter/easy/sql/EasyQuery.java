package com.paletter.easy.sql;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.paletter.easy.sql.utils.LogUtil;
import com.paletter.easy.sql.utils.SQLUtils;
import com.paletter.tool.DateUtils;
import com.paletter.tool.StringUtils;

public class EasyQuery {

	public static <T> T queryOne(String sql, Class<T> retrurnClass, Object... params) {
		return queryOne(EasyConnection.getConn(), sql, retrurnClass, params);
	}
	
	@SuppressWarnings("unchecked")
	public static <T> T queryOne(Connection conn, String sql, Class<T> retrurnClass, Object... params) {

		PreparedStatement stat = null;
		ResultSet rs = null;
		
		try {
			
			SQLString sqlString = new SQLString(sql, params);
			sql = sqlString.getSql();
			params = sqlString.getParams();
			
			stat = conn.prepareStatement(sql);
			if (params != null) {
				int paramIndex = 1;
				for (Object o : params) {
					stat.setObject(paramIndex ++, o);
				}
			}
			
			rs = stat.executeQuery();
			while (rs.next()) {
				
				T data = parseResult(rs, retrurnClass);

				return data;
			}
			
		} catch (Exception e) {
			
			LogUtil.error("EasyQuery.queryOne error", e);
			throw new EasySqlException(e);
			
		} finally {
			try {
				if (rs != null) {
					rs.close();
				}
				
				if (stat != null) {
					stat.close();
				}
			} catch (Exception e2) {
			}
		}
		
		return null;
	}
	
	public static <T> List<T> queryList(String sql, Class<T> retrurnClass, Object... params) {
		return queryList(EasyConnection.getConn(), sql, retrurnClass, params);
	}
	
	public static <T> List<T> queryList(Connection conn, String sql, Class<T> retrurnClass, Object... params) {
		
		List<T> list = new ArrayList<T>();
		
		PreparedStatement stat = null;
		ResultSet rs = null;
		
		try {

			SQLString sqlString = new SQLString(sql, params);
			sql = sqlString.getSql();
			params = sqlString.getParams();
			
			stat = conn.prepareStatement(sql);
			if (params != null) {
				int paramIndex = 1;
				for (Object o : params) {
					stat.setObject(paramIndex ++, o);
				}
			}
			
			rs = stat.executeQuery();
			while (rs.next()) {
				
				T data = parseResult(rs, retrurnClass);
				
				list.add(data);
			}
		} catch (Exception e) {
			
			LogUtil.error("EasyQuery.queryList error", e);
			throw new EasySqlException(e);
			
		} finally {
			try {
				if (rs != null) {
					rs.close();
				}
				
				if (stat != null) {
					stat.close();
				}
			} catch (Exception e2) {
			}
		}
		
		return list;
	}

	@SuppressWarnings("unchecked")
	private static <T> T parseResult(ResultSet rs, Class<T> retrurnClass) throws SQLException, InstantiationException, IllegalAccessException, IllegalArgumentException, InvocationTargetException {

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
				String camelCaseColName = StringUtils.toLowerCaseFirstChar(m.getName().substring(3));
				StringBuilder underlineColName = new StringBuilder(camelCaseColName);
				Matcher mat = p.matcher(m.getName().substring(3));
				int i = 0;
				while (mat.find()) {
					underlineColName.replace(mat.start() + i, mat.end() + i, "_" + mat.group().toLowerCase());
					i ++;
				}
				if (underlineColName.charAt(0) == '_') underlineColName.deleteCharAt(0);
				
				// Set column value
				if (SQLUtils.isResultContainColumn(rs, underlineColName.toString())) {
					
					Object val = rs.getObject(underlineColName.toString());
					if (val != null && m.getParameterTypes()[0].equals(Date.class)) {
						String date = rs.getString(underlineColName.toString());
						m.invoke(data, DateUtils.parse(date, "yyyy-MM-dd HH:mm:ss"));
					} else {
						m.invoke(data, val);
					}
					
				} else if (SQLUtils.isResultContainColumn(rs, camelCaseColName)) {

					Object val = rs.getObject(camelCaseColName);
					if (val != null && m.getParameterTypes()[0].equals(Date.class)) {
						String date = rs.getString(camelCaseColName);
						m.invoke(data, DateUtils.parse(date, "yyyy-MM-dd HH:mm:ss"));
					} else {
						m.invoke(data, val);
					}
					
				}
			}
		}
		
		return data;
	}
}
