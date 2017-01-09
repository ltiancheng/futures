package com.ltc.strategy.tortoise.vo;

import com.ltc.base.vo.ContractVO;

public class PositionVO {
	public static final String LONG = "L";
	public static final String SHORT = "S";
	
	private ContractVO contract;
	private String direction;		//"L" for Long; "S" for Short
	private int handPerUnit;
	private int unitCount;
	private float lastInPrice;
	
}
