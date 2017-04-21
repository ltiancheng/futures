package com.ltc.base.gateway.ctp.helper;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class CtpHelper {
	private static Logger logger = LoggerFactory.getLogger(CtpHelper.class);
	
	public static String DAY_FORMAT="yyyyMMdd";
	public static String TIME_FORMAT="HH:mm:ss"; 
	
	public static Date parseDate(String dateStr){
		return parseDate(dateStr, DAY_FORMAT+TIME_FORMAT);
	}
	
	public static Date parseDate(String dateStr, String format) {
		SimpleDateFormat sdf = new SimpleDateFormat(format);
		try {
			return sdf.parse(dateStr);
		} catch (ParseException e) {
			logger.error(e.getMessage(), e);
			return null;
		}
	}

}
