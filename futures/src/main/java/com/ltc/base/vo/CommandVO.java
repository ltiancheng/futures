package com.ltc.base.vo;

import java.math.BigDecimal;

public class CommandVO {
	public static final String OPEN_LONG = "OL";
	public static final String OPEN_SHORT = "OS";
	public static final String CLOSE_LONG = "CL";
	public static final String CLOSE_SHORT = "CS";
	
	public static final String MARKET = "M";
	public static final String LIMIT = "L";
	
	private String instruction;
	private String priceStyle;
	private BigDecimal price; // only meaningful when price style is LIMIT;
	private int volumn;
	private boolean done;
	private BigDecimal dealPrice;
	
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
	public int getVolumn() {
		return volumn;
	}
	public void setVolumn(int volumn) {
		this.volumn = volumn;
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
