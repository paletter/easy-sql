package com.paletter.easy.sql;

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

import com.paletter.easy.sql.utils.DateUtils;

public class EasySql {

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
	
	public static void insert(Connection conn, Object object, String table) {
		
		PreparedStatement stat = null;
		
		try {
		
			StringBuilder sql = new StringBuilder();
			sql.append("insert into " + table + " (");
			
			StringBuilder cols = new StringBuilder();
			StringBuilder vals = new StringBuilder();
			
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
						cols.append("`");
						
						// Set value
						vals.append(",?");
						valList.add(property);
					}
				}
			}
			
			if (valList.size() == 0) {
				throw new IllegalArgumentException("Insert values object can not null.");
			}
			
			cols.deleteCharAt(0);
			sql.append(cols.toString());
			vals.deleteCharAt(0);
			sql.append(") values(");
			sql.append(vals.toString());
			sql.append(")");
			
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
				sql.append("where ");
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

	public static void delete(Connection conn, String table, String whereCondition) {
		
		PreparedStatement stat = null;
		
		try {
			
			StringBuilder sql = new StringBuilder();
			sql.append("delete from " + table + " ");
			if (whereCondition != null && !whereCondition.isEmpty()) {
				sql.append("where ");
				sql.append(whereCondition);
			}
			
			stat = conn.prepareStatement(sql.toString());
			
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
