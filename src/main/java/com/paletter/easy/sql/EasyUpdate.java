package com.paletter.easy.sql;

import java.lang.reflect.Method;
import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.paletter.easy.sql.utils.DateUtils;

public class EasyUpdate {

	public static void update(Object object, String table, String whereCondition) {
		update(EasyConnection.getConn(), object, table, whereCondition);
	}
	
	public static void update(Connection conn, Object object, String table, String whereCondition) {
		
		PreparedStatement stat = null;
		
		try {
			
			StringBuilder sql = new StringBuilder();
			sql.append("update " + table + " ");
			
			StringBuilder cols = new StringBuilder();
			
			List<Object> valList = new ArrayList<Object>();
			
			for (Method m : object.getClass().getDeclaredMethods()) {
				if (m.getName().startsWith("get")) {
					
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
				if (val instanceof Integer) {
					stat.setInt(index ++, (int) val);
				} else if (val instanceof String) {
					stat.setString(index ++, val.toString());
				} else if (val instanceof Double) {
					stat.setDouble(index ++, (double) val);
				} else if (val instanceof Float) {
					stat.setFloat(index ++, (float) val);
				} else if (val instanceof Long) {
					stat.setLong(index ++, (long) val);
				} else if (val instanceof BigDecimal) {
					stat.setBigDecimal(index ++, (BigDecimal) val);
				} else if (val instanceof Date) {
					stat.setString(index ++, DateUtils.format((Date) val, "yyyy-MM-dd hh:mm:ss"));
				}
			}
			
			stat.executeUpdate();
			
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (stat != null) {
				try {
					stat.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
		}
	}
}
