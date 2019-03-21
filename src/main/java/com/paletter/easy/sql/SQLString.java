package com.paletter.easy.sql;

import java.util.ArrayList;
import java.util.List;

public class SQLString {

	private String sql;
	private Object[] params;
	
	public SQLString(String sql, Object[] params) {
		
		if (sql.contains("$")) {
			
			List<Object> paramList = new ArrayList<>();
			for (Object obj : params) paramList.add(obj);
			
			int paramIndex = 0;
			for (int i = 0; i < sql.length(); i ++) {
				char c = sql.charAt(i);
				if (c == '?') paramIndex ++;
				if (c == '$') {
					sql = sql.replaceFirst("\\$", paramList.get(paramIndex).toString());
					paramList.remove(paramIndex);
				}
			}
			
			params = paramList.toArray();
		}
		
		this.sql = sql;
		this.params = params;
	}
	
	public Object[] getParams() {
		return params;
	}
	
	public String getSql() {
		return sql;
	}
}
