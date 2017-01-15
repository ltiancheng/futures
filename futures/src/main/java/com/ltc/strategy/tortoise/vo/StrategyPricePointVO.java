package com.ltc.strategy.tortoise.vo;

public class StrategyPricePointVO {
	private float openLongPoint;		//High 20
	private float openShortPoint;		//Low 20
	private float closeLongPoint;		//Low 10
	private float closeShortPoint;		//High 10
	
	public float getOpenLongPoint() {
		return openLongPoint;
	}
	public void setOpenLongPoint(float openLongPoint) {
		this.openLongPoint = openLongPoint;
	}
	public float getOpenShortPoint() {
		return openShortPoint;
	}
	public void setOpenShortPoint(float openShortPoint) {
		this.openShortPoint = openShortPoint;
	}
	public float getCloseLongPoint() {
		return closeLongPoint;
	}
	public void setCloseLongPoint(float closeLongPoint) {
		this.closeLongPoint = closeLongPoint;
	}
	public float getCloseShortPoint() {
		return closeShortPoint;
	}
	public void setCloseShortPoint(float closeShortPoint) {
		this.closeShortPoint = closeShortPoint;
	}
}
