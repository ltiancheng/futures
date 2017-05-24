package com.ltc.base.helpers;

import java.awt.datatransfer.StringSelection;
import java.io.IOException;
import java.util.Calendar;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.ltc.base.gateway.ctp.vo.CThostFtdcDepthMarketDataField;

public class BaseUtils {

	private static Logger logger = LoggerFactory.getLogger(BaseUtils.class);
	
	private static ObjectMapper objMapper;
	private static String currentYearStr;
	
	public static ObjectMapper getObjMapper(){
		if(objMapper == null){
			objMapper = new ObjectMapper().configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false)
					.configure(DeserializationFeature.FAIL_ON_INVALID_SUBTYPE, false)
					.configure(JsonParser.Feature.ALLOW_UNQUOTED_FIELD_NAMES, true);
		}
		return objMapper;
	}
	
	public static <T> T json2Obj(String jsonStr, Class<T> claz) {
		ObjectMapper om = getObjMapper();
		try {
			return om.readValue(jsonStr, claz);
		} catch (IOException e) {
			logger.error("[BaseUtils] error unmarshalling class:{}, with jsonString: {}", claz.getName(), jsonStr);
			logger.error(e.getMessage(), e);
			return null;
		}
	}

	public static String obj2Json(Object obj) {
		ObjectMapper om = getObjMapper();
		try {
			return om.writeValueAsString(obj);
		} catch (JsonProcessingException e) {
			logger.error("[BaseUtils] error marshalling class:{}", obj.getClass());
			logger.error(e.getMessage(), e);
			return "{}";
		}
	}
	
	public static float getTrueValue(float showValue, float defaultValue) {
		if(showValue > Float.MAX_VALUE){
			return defaultValue;
		}
		return showValue;
	}

	public static double getTrueValue(double showValue, double defaultValue) {
		if(showValue > Double.MAX_VALUE){
			return defaultValue;
		}
		return showValue;
	}

	//if it's from zhengzhou market, set the contract key from SR709 to SR1709
	public static String ctpKey2Key(String instrumentID) {
		String key = instrumentID.toUpperCase();
		if(key.charAt(key.length()-4) >= 'A' && key.charAt(key.length()-4) <= 'Z'){
			return key.substring(0, key.length()-3) + getCurrentYearStr() + key.substring(key.length()-3) ;
		} else {
			return key;
		}
	}

	private static String getCurrentYearStr() {
		if(StringUtils.isBlank(currentYearStr)){
			Calendar c = Calendar.getInstance();
			currentYearStr = String.valueOf(String.valueOf(c.get(Calendar.YEAR)).charAt(2));
		}
		return currentYearStr;
	}

	public static void TrimCalendar(Calendar now) {
		now.set(Calendar.HOUR_OF_DAY, 0);
		now.set(Calendar.MINUTE, 0);
		now.set(Calendar.SECOND, 0);
		now.set(Calendar.MILLISECOND, 0);
	}
	
}
