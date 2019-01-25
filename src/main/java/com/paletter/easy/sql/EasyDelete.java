package com.paletter.easy.sql;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class EasyDelete {

	public static void delete(String table, String whereCondition) {
		delete(EasyConnection.getConn(), table, whereCondition);
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
