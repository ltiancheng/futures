package com.ltc.base.gateway.ctp.helper;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.ltc.base.vo.CommandVO;

public class CtpHelper {
	private static Logger logger = LoggerFactory.getLogger(CtpHelper.class);
	
	public static String DAY_FORMAT="yyyyMMdd";
	public static String TIME_FORMAT="HH:mm:ss"; 
	public static String THOST_FTDC_D_Buy = "0";
	public static String THOST_FTDC_D_Sell = "1";
	///开仓
	public static String THOST_FTDC_OF_Open = "0";
	///平仓
	public static String THOST_FTDC_OF_Close = "1";
	///强平
	public static String THOST_FTDC_OF_ForceClose = "2";
	///平今
	public static String THOST_FTDC_OF_CloseToday = "3";
	///平昨
	public static String THOST_FTDC_OF_CloseYesterday = "4";
	///强减
	public static String THOST_FTDC_OF_ForceOff = "5";
	///本地强平
	public static String THOST_FTDC_OF_LocalForceClose = "6";
	
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

	public static String parseInstruction(String offsetFlag, String direction) {
		if(StringUtils.equals(direction, THOST_FTDC_D_Buy)){
			if(StringUtils.equals(offsetFlag, THOST_FTDC_OF_Open)){
				return CommandVO.OPEN_LONG;
			} else {
				return CommandVO.CLOSE_SHORT;
			}
		} else {
			if(StringUtils.equals(offsetFlag, THOST_FTDC_OF_Open)){
				return CommandVO.OPEN_SHORT;
			} else {
				return CommandVO.CLOSE_LONG;
			}
		}
	}

}
