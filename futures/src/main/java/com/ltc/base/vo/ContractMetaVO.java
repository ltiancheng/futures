package com.ltc.base.vo;

import java.io.Serializable;
import java.util.Date;

public class ContractMetaVO implements Serializable {
	private static final long serialVersionUID = 1L;
	private long id;
	private String description;
	private String symbol;
	private String ctpSymbol;
//	private int handValue;
	private float leverage = 0.1f;	//levelRate is default to 10%
	private int pointValue;
	private float atr;	//ATR 20
	private Date atrUpdateDate;
	
	public Date getAtrUpdateDate() {
		return atrUpdateDate;
	}
	public void setAtrUpdateDate(Date atrUpdateDate) {
		this.atrUpdateDate = atrUpdateDate;
	}
	public void setCtpSymbol(String ctpSymbol) {
		this.ctpSymbol = ctpSymbol;
	}
	public float getLeverage() {
		return leverage;
	}
	public void setLeverage(float leverage) {
		this.leverage = leverage;
	}
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
/*	public int getHandValue() {
		return handValue;
	}
	public void setHandValue(int handValue) {
		this.handValue = handValue;
	}*/
	public int getPointValue() {
		return pointValue;
	}
	public void setPointValue(int pointValue) {
		this.pointValue = pointValue;
	}
	public String getCtpSymbol() {
		return this.ctpSymbol;
	}
}
