package com.paletter.easy.sql.utils;

import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;

public class LogUtil {

	private static final Logger logger = LogManager.getLogger(LogUtil.class);
	
	public static Integer LEVLE = 1;
	
	public static void print(String str) {
//		System.out.println(str);
		logger.info(str);
	}
	
	public static void error(String str) {
		logger.error(str);
	}
	
	public static void error(String str, Throwable e) {
		logger.error(str, e);
	}
	
	public static void printDebug(String str) {
		if (LEVLE <= 1) {
			System.out.println(str);
		}
	}
	
	public static void printDebug2(String str) {
		if (LEVLE <= 2) {
			System.out.println(str);
		}
	}
}
