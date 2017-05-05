package com.ltc.base.vo;

import java.io.Serializable;
import java.util.Date;

public class BarVO implements Serializable {
	@Override
	public String toString() {
		return String.valueOf(closePrice);
	}
	private static final long serialVersionUID = 1L;
	
	private ContractVO contract;
	private long id;
	private float closePrice;
	private float openPrice;
	private float highPrice;
	private float lowPrice;
	private float topPrice;
	private float bottomPrice;
	private long volume;	//成交量
	private Date barDate;
	private long amount;	//成交额
	
	public float getTopPrice() {
		return topPrice;
	}
	public void setTopPrice(float topPrice) {
		this.topPrice = topPrice;
	}
	public float getBottomPrice() {
		return bottomPrice;
	}
	public void setBottomPrice(float bottomPrice) {
		this.bottomPrice = bottomPrice;
	}
	public long getAmount() {
		return amount;
	}
	public void setAmount(long amount) {
		this.amount = amount;
	}
	public ContractVO getContract() {
		return contract;
	}
	public void setContract(ContractVO contract) {
		this.contract = contract;
	}
	public long getId() {
		return id;
	}
	public void setId(long id) {
		this.id = id;
	}
	public float getClosePrice() {
		return closePrice;
	}
	public void setClosePrice(float closePrice) {
		this.closePrice = closePrice;
	}
	public float getOpenPrice() {
		return openPrice;
	}
	public void setOpenPrice(float openPrice) {
		this.openPrice = openPrice;
	}
	public float getHighPrice() {
		return highPrice;
	}
	public void setHighPrice(float highPrice) {
		this.highPrice = highPrice;
	}
	public float getLowPrice() {
		return lowPrice;
	}
	public void setLowPrice(float lowPrice) {
		this.lowPrice = lowPrice;
	}
	public long getVolume() {
		return volume;
	}
	public void setVolume(long volume) {
		this.volume = volume;
	}
	public Date getBarDate() {
		return barDate;
	}
	public void setBarDate(Date barDate) {
		this.barDate = barDate;
	}
}
