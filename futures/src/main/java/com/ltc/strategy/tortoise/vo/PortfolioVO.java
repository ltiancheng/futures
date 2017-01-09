package com.ltc.strategy.tortoise.vo;

import java.util.List;

public class PortfolioVO {
	private double initEquity;
	private double stopLossEquity;
	private List<PositionVO> positionList;
	
	public double getInitEquity() {
		return initEquity;
	}
	public void setInitEquity(double initEquity) {
		this.initEquity = initEquity;
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
