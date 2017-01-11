package com.ltc.base.vo;

public class ContractMetaVO {
	private long id;
	private String description;
	private String symbol;
	private int handValue;
	private float levelRate;	//levelRate is default to 10%
	private int pointValue;
	private float atr;	//ATR 250
	
	public float getAtr() {
		return atr;
	}
	public void setAtr(float atr) {
		this.atr = atr;
	}
	public long getId() {
		return id;
	}
	public void setId(long id) {
		this.id = id;
	}
	public String getDescription() {
		return description;
	}
	public void setDescription(String description) {
		this.description = description;
	}
	public String getSymbol() {
		return symbol;
	}
	public void setSymbol(String symbol) {
		this.symbol = symbol;
	}
	public int getHandValue() {
		return handValue;
	}
	public void setHandValue(int handValue) {
		this.handValue = handValue;
	}
	public float getLevelRate() {
		return levelRate;
	}
	public void setLevelRate(float levelRate) {
		this.levelRate = levelRate;
	}
	public int getPointValue() {
		return pointValue;
	}
	public void setPointValue(int pointValue) {
		this.pointValue = pointValue;
	}
}
