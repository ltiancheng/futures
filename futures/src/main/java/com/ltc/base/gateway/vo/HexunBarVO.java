package com.ltc.base.gateway.vo;

import java.util.Date;

public class HexunBarVO {
	public static final int STR_SIZE = 8;
	private Date date;
	private float lastClose;
	private float open;
	private float close;
	private float high;
	private float low;
	private long volume;
	private long amount;
	
	public float getLastClose() {
		return lastClose;
	}
	public void setLastClose(float lastClose) {
		this.lastClose = lastClose;
	}
	public float getOpen() {
		return open;
	}
	public void setOpen(float open) {
		this.open = open;
	}
	public float getClose() {
		return close;
	}
	public void setClose(float close) {
		this.close = close;
	}
	public float getHigh() {
		return high;
	}
	public void setHigh(float high) {
		this.high = high;
	}
	public float getLow() {
		return low;
	}
	public void setLow(float low) {
		this.low = low;
	}
	public long getVolume() {
		return volume;
	}
	public void setVolume(long volume) {
		this.volume = volume;
	}
	public long getAmount() {
		return amount;
	}
	public void setAmount(long amount) {
		this.amount = amount;
	}
	public Date getDate() {
		return date;
	}
	public void setDate(Date date) {
		this.date = date;
	}
	
}
