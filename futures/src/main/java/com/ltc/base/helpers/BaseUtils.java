package com.ltc.base.helpers;

import java.io.IOException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;

public class BaseUtils {

	private static Logger logger = LoggerFactory.getLogger(BaseUtils.class);
	
	private static ObjectMapper objMapper;
	
	public static ObjectMapper getObjMapper(){
		if(objMapper == null){
			objMapper = new ObjectMapper().configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
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

}
