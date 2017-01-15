package com.ltc.strategy.tortoise.vo;

import java.io.Serializable;
import java.util.List;

public class PortfolioVO implements Serializable {
	private static final long serialVersionUID = 1L;
	
	private double cash;
	private double stopLossEquity;
	private List<PositionVO> positionList;
	
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
	public List<PositionVO> getPositionList() {
		return positionList;
	}
	public void setPositionList(List<PositionVO> positionList) {
		this.positionList = positionList;
	}
}
