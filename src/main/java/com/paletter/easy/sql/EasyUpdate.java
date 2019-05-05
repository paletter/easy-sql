package com.paletter.easy.sql;

import java.lang.reflect.Method;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.paletter.easy.sql.utils.LogUtil;
import com.paletter.easy.sql.utils.SQLUtils;

public class EasyUpdate {

	public static int update(Object object, String table, String whereCondition, Object... params) {
		return update(EasyConnection.getConn(), object, table, whereCondition, params);
	}
	
	public static int update(Connection conn, Object object, String table, String whereCondition, Object... params) {
		
		PreparedStatement stat = null;
		
		try {
			
			StringBuilder sql = new StringBuilder();
			sql.append("update " + table + " ");
			
			StringBuilder cols = new StringBuilder();
			
			List<Object> valList = new ArrayList<Object>();
			
			for (Method m : object.getClass().getMethods()) {
				if (m.getModifiers() == Method.DECLARED && m.getName().startsWith("get")) {
					
					Object property = m.invoke(object);
					if (property != null) {
						
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
						
						// Set column
						cols.append(",`");
						cols.append(colName.toString());
						cols.append("`=?");
						
						valList.add(property);
					}
				}
			}
			
			if (valList.size() == 0) {
				throw new IllegalArgumentException("Insert values object can not null.");
			}
			
			cols.deleteCharAt(0);
			sql.append("set ");
			sql.append(cols.toString());
			if (whereCondition != null && !whereCondition.isEmpty()) {
				sql.append(" where ");
				sql.append(whereCondition);
			}
			
			stat = conn.prepareStatement(sql.toString());
			
			int index = 1;
			for (Object val : valList) {
				SQLUtils.addParam(val, stat, index ++);
			}
			
			if (params != null) {
				for (Object val : params) {
					SQLUtils.addParamWithNull(val, stat, index ++);
				}
			}
			
			return stat.executeUpdate();
			
		} catch (Exception e) {
			
			LogUtil.error("EasyQuery.update error", e);
			throw new EasySqlException(e);
			
		} finally {
			if (stat != null) {
				try {
					stat.close();
				} catch (SQLException e) {
				}
			}
		}
	}
	
	public static int updateBySql(String sql, Object... params) {
		
		PreparedStatement stat = null;
		
		try {
			
			SQLString sqlString = new SQLString(sql, params);
			sql = sqlString.getSql();
			params = sqlString.getParams();
			
			stat = EasyConnection.getConn().prepareStatement(sql);
			
			int index = 1;
			if (params != null) {
				for (Object val : params) {
					SQLUtils.addParamWithNull(val, stat, index ++);
				}
			}
			
			return stat.executeUpdate();
			
		} catch (Exception e) {
			
			LogUtil.error("EasyUpdate.updateBySql error", e);
			throw new EasySqlException(e);
			
		} finally {
			if (stat != null) {
				try {
					stat.close();
				} catch (SQLException e) {
				}
			}
		}
	}
}
