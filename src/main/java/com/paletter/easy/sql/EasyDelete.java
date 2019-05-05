package com.paletter.easy.sql;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

import com.paletter.easy.sql.utils.LogUtil;
import com.paletter.easy.sql.utils.SQLUtils;

public class EasyDelete {

	public static void delete(String table, String whereCondition, Object... params) {
		delete(EasyConnection.getConn(), table, whereCondition, params);
	}

	public static void delete(Connection conn, String table, String whereCondition, Object... params) {
		
		PreparedStatement stat = null;
		
		try {
			
			StringBuilder sql = new StringBuilder();
			sql.append("delete from " + table + " ");
			if (whereCondition != null && !whereCondition.isEmpty()) {
				sql.append("where ");
				sql.append(whereCondition);
			}
			
			stat = conn.prepareStatement(sql.toString());

			if (params != null) {
				int index = 1;
				for (Object val : params)
					SQLUtils.addParamWithNull(val, stat, index ++);
			}
			
			stat.executeUpdate();
			
		} catch (Exception e) {
			
			LogUtil.error("EasyDelete.delete error", e);
			throw new EasySqlException(e);
			
		} finally {
			if (stat != null) {
				try {
					stat.close();
				} catch (SQLException e) {
					LogUtil.error("EasyDelete.delete close statement error", e);
				}
			}
		}
	}
}
