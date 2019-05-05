package com.paletter.easy.sql;

public class EasySqlException extends RuntimeException {

	private static final long serialVersionUID = 7405456308222267382L;

	public EasySqlException(Throwable e) {
		super();
		this.initCause(e);
	}
}
