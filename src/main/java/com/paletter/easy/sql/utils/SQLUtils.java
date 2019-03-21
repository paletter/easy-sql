package com.paletter.easy.sql.utils;

import java.math.BigDecimal;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Date;

import com.paletter.tool.DateUtils;

public class SQLUtils {
	
	public static void addParam(Object val, PreparedStatement stat, Integer index) throws SQLException {
		if (val instanceof Integer) {
			stat.setInt(index, (int) val);
		} else if (val instanceof String) {
			stat.setString(index, val.toString());
		} else if (val instanceof Double) {
			stat.setDouble(index, (double) val);
		} else if (val instanceof Float) {
			stat.setFloat(index, (float) val);
		} else if (val instanceof Long) {
			stat.setLong(index, (long) val);
		} else if (val instanceof BigDecimal) {
			stat.setBigDecimal(index, (BigDecimal) val);
		} else if (val instanceof Date) {
			stat.setString(index, DateUtils.format((Date) val, DateUtils.FORMAT_YMD_HMS));
		}
	}
	
	public static void addParamWithNull(Object val, PreparedStatement stat, Integer index) throws SQLException {
		if (val instanceof Integer) {
			stat.setInt(index, (int) val);
		} else if (val instanceof String) {
			stat.setString(index, val.toString());
		} else if (val instanceof Double) {
			stat.setDouble(index, (double) val);
		} else if (val instanceof Float) {
			stat.setFloat(index, (float) val);
		} else if (val instanceof Long) {
			stat.setLong(index, (long) val);
		} else if (val instanceof BigDecimal) {
			stat.setBigDecimal(index, (BigDecimal) val);
		} else if (val instanceof Date) {
			stat.setString(index, DateUtils.format((Date) val, DateUtils.FORMAT_YMD_HMS));
		} else if (val == null) {
			stat.setObject(index, val);
		}
	}
	
	public static boolean isResultContainColumn(ResultSet rs, String colName) {
		try {
			return rs.findColumn(colName) > 0;
		} catch (Throwable e) {
			return false;
		}
	}
}
