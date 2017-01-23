package com.ltc.strategy.tortoise.vo;

import java.io.Serializable;
import java.util.List;
import java.util.Set;

public class PortfolioVO implements Serializable {
	private static final long serialVersionUID = 1L;
	
	private double cash;
	private double stopLossEquity;
	private Set<PositionVO> positionSet;
	private String code;
	
	public Set<PositionVO> getPositionSet() {
		return positionSet;
	}
	public void setPositionSet(Set<PositionVO> positionSet) {
		this.positionSet = positionSet;
	}
	public String getCode() {
		return code;
	}
	public void setCode(String code) {
		this.code = code;
	}
	public double getCash() {
		return cash;
	}
	public void setCash(double cash) {
		this.cash = cash;
	}
	public double getStopLossEquity() {
		return stopLossEquity;
	}
	public void setStopLossEquity(double stopLossEquity) {
		this.stopLossEquity = stopLossEquity;
	}
	
}
