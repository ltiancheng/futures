package com.ltc.strategy.tortoise.vo;

import java.io.Serializable;

import com.ltc.base.manager.impl.ContractHolderImpl;
import com.ltc.base.vo.BarVO;
import com.ltc.base.vo.ContractVO;

public class PositionVO implements Serializable {
	@Override
	public String toString() {
		ContractVO c = ContractHolderImpl.getInstance().getContractByKey(contract.getKey());
		BarVO bar = null;
		if(c != null){
			bar = c.getCurrentBar();
		}
		return contract.getKey()+" "+(direction==null?"E":direction)+" "+unitCount+" * "+handPerUnit+" lastInPrice: "+
				lastInPrice+" averagePrice: "+averagePrice+" price: "+bar;
	}
	private static final long serialVersionUID = 1L;
	
	public static final String LONG = "L";
	public static final String SHORT = "S";
	public static final String ACTIVE = "A";
	public static final String EXPIRE = "E";
	public static final String REFRESH = "R";
	
	private ContractVO contract;
	private String direction = "";		//"L" for Long; "S" for Short
	private int handPerUnit;
	private int unitCount;
	private float lastInPrice;
	private float averagePrice;
	private float topPrice;
	private String status;
	private PortfolioVO portfolio;
	private long id;
	
	public float getTopPrice() {
		return topPrice;
	}
	public void setTopPrice(float topPrice) {
		this.topPrice = topPrice;
	}
	public long getId() {
		return id;
	}
	public void setId(long id) {
		this.id = id;
	}
	public PortfolioVO getPortfolio() {
		return portfolio;
	}
	public void setPortfolio(PortfolioVO portfolio) {
		this.portfolio = portfolio;
	}
	public float getAveragePrice() {
		return averagePrice;
	}
	public void setAveragePrice(float averagePrice) {
		this.averagePrice = averagePrice;
	}
	public String getStatus() {
		return status;
	}
	public void setStatus(String status) {
		this.status = status;
	}
	public ContractVO getContract() {
		return contract;
	}
	public void setContract(ContractVO contract) {
		this.contract = contract;
	}
	public String getDirection() {
		return direction;
	}
	public void setDirection(String direction) {
		this.direction = direction;
	}
	public int getHandPerUnit() {
		return handPerUnit;
	}
	public void setHandPerUnit(int handPerUnit) {
		this.handPerUnit = handPerUnit;
	}
	public int getUnitCount() {
		return unitCount;
	}
	public void setUnitCount(int unitCount) {
		this.unitCount = unitCount;
	}
	public float getLastInPrice() {
		return lastInPrice;
	}
	public void setLastInPrice(float lastInPrice) {
		this.lastInPrice = lastInPrice;
	}
	
}
