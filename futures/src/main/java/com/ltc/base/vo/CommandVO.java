package com.ltc.base.vo;

import java.math.BigDecimal;

public class CommandVO {
	@Override
	public String toString() {
		return instruction+" "+handPerUnit+" * "+units+" at "+price.toString();
	}
	public static final String OPEN_LONG = "OL";
	public static final String OPEN_SHORT = "OS";
	public static final String CLOSE_LONG = "CL";
	public static final String CLOSE_SHORT = "CS";
	
	public static final String MARKET = "M";
	public static final String LIMIT = "L";
	
	private String instruction;
	private String priceStyle;
	private BigDecimal price; // only meaningful when price style is LIMIT;
	private int units;
	private int handPerUnit;
	private boolean done;
	private BigDecimal dealPrice;
	
	public int getHandPerUnit() {
		return handPerUnit;
	}
	public void setHandPerUnit(int handPerUnit) {
		this.handPerUnit = handPerUnit;
	}
	public int getUnits() {
		return units;
	}
	public void setUnits(int units) {
		this.units = units;
	}
	public String getInstruction() {
		return instruction;
	}
	public void setInstruction(String instruction) {
		this.instruction = instruction;
	}
	public String getPriceStyle() {
		return priceStyle;
	}
	public void setPriceStyle(String priceStyle) {
		this.priceStyle = priceStyle;
	}
	public BigDecimal getPrice() {
		return price;
	}
	public void setPrice(BigDecimal price) {
		this.price = price;
	}
	public boolean isDone() {
		return done;
	}
	public void setDone(boolean done) {
		this.done = done;
	}
	public BigDecimal getDealPrice() {
		return dealPrice;
	}
	public void setDealPrice(BigDecimal dealPrice) {
		this.dealPrice = dealPrice;
	}
}
