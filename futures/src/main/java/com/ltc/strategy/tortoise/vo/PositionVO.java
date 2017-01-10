package com.ltc.strategy.tortoise.vo;

import com.ltc.base.vo.ContractVO;

public class PositionVO {
	public static final String LONG = "L";
	public static final String SHORT = "S";
	public static final String ACTIVE = "A";
	public static final String EXPIRE = "E";
	
	private ContractVO contract;
	private String direction;		//"L" for Long; "S" for Short
	private int handPerUnit;
	private int unitCount;
	private float lastInPrice;
	private String status;
	
	public String getStatus() {
		return status;
	}
	public void setStatus(String status) {
		this.status = status;
	}
	public ContractVO getContract() {
		return contract;
	}
	public void setContract(ContractVO contract) {
		this.contract = contract;
	}
	public String getDirection() {
		return direction;
	}
	public void setDirection(String direction) {
		this.direction = direction;
	}
	public int getHandPerUnit() {
		return handPerUnit;
	}
	public void setHandPerUnit(int handPerUnit) {
		this.handPerUnit = handPerUnit;
	}
	public int getUnitCount() {
		return unitCount;
	}
	public void setUnitCount(int unitCount) {
		this.unitCount = unitCount;
	}
	public float getLastInPrice() {
		return lastInPrice;
	}
	public void setLastInPrice(float lastInPrice) {
		this.lastInPrice = lastInPrice;
	}
	
}
