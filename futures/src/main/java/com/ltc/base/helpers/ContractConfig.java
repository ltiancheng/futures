package com.ltc.base.helpers;

import java.util.HashMap;
import java.util.Map;

public class ContractConfig {
	private static Map<String, String> PREFIX_MAP = new HashMap<String, String>();
	static {
		//DCE   大连
		PREFIX_MAP.put("A", "DCE");
		PREFIX_MAP.put("BB", "DCE");
		PREFIX_MAP.put("C", "DCE");
		PREFIX_MAP.put("CS", "DCE");
		PREFIX_MAP.put("FB", "DCE");
		PREFIX_MAP.put("I", "DCE");
		PREFIX_MAP.put("J", "DCE");
		PREFIX_MAP.put("JD", "DCE");
		PREFIX_MAP.put("JM", "DCE");
		PREFIX_MAP.put("L", "DCE");
		PREFIX_MAP.put("M", "DCE");
		PREFIX_MAP.put("P", "DCE");
		PREFIX_MAP.put("PP", "DCE");
		PREFIX_MAP.put("V", "DCE");
		PREFIX_MAP.put("Y", "DCE");
		
		//CZCE   郑州
		PREFIX_MAP.put("CF", "CZCE");
		PREFIX_MAP.put("FG", "CZCE");
		PREFIX_MAP.put("MA", "CZCE");
		PREFIX_MAP.put("OI", "CZCE");
		PREFIX_MAP.put("RM", "CZCE");
		PREFIX_MAP.put("RS", "CZCE");
		PREFIX_MAP.put("SF", "CZCE");
		PREFIX_MAP.put("SM", "CZCE");
		PREFIX_MAP.put("SR", "CZCE");
		PREFIX_MAP.put("TA", "CZCE");
		PREFIX_MAP.put("TC", "CZCE");
		PREFIX_MAP.put("WH", "CZCE");
		PREFIX_MAP.put("LR", "CZCE");
		PREFIX_MAP.put("JR", "CZCE");
		PREFIX_MAP.put("RI", "CZCE");
		PREFIX_MAP.put("ZC", "CZCE");
		
		//SHFE    上海
		PREFIX_MAP.put("AG", "SHFE2");
		PREFIX_MAP.put("AL", "SHFE3");
		PREFIX_MAP.put("NI", "SHFE3");
		PREFIX_MAP.put("AU", "SHFE2");
		PREFIX_MAP.put("BU", "SHFE3");
		PREFIX_MAP.put("CU", "SHFE3");
		PREFIX_MAP.put("HC", "SHFE3");
		PREFIX_MAP.put("PB", "SHFE3");
		PREFIX_MAP.put("RB", "SHFE3");
		PREFIX_MAP.put("RU", "SHFE");
		PREFIX_MAP.put("ZN", "SHFE3");
		PREFIX_MAP.put("SN", "SHFE3");
		
		//CFFEX   中金
		PREFIX_MAP.put("IC", "CFFEX");
		PREFIX_MAP.put("IF", "CFFEX");
		PREFIX_MAP.put("IH", "CFFEX");
		PREFIX_MAP.put("T", "CFFEX");
		PREFIX_MAP.put("TF", "CFFEX");
		
	}
	
	public static String getPrefix(String symbol){
		return PREFIX_MAP.get(symbol);
	}
}
