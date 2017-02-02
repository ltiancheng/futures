package com.ltc.base.vo;

import java.io.Serializable;

import org.apache.commons.lang.StringUtils;

public class ContractID implements Serializable {
	@Override
	public boolean equals(Object c) {
		if(c instanceof ContractID){
			return StringUtils.equals(this.symbol, ((ContractID)c).symbol) 
					&& StringUtils.equals(this.prid, ((ContractID)c).prid);
		} else if(c instanceof ContractVO){
			return StringUtils.equals(this.symbol, ((ContractVO)c).getContractMeta().getSymbol()) 
					&& StringUtils.equals(this.prid, ((ContractVO)c).getPrid());
		} else {
			return false;
		}
	}
	@Override
	public int hashCode() {
		if(this.symbol == null && this.prid == null){
			return 0;
		} else {
			return (this.symbol+this.prid).hashCode();
		}
	}
	public ContractID(String prid, String symbol) {
		super();
		this.prid = prid;
		this.symbol = symbol;
	}
	public ContractID(){}
	private String prid;
	private String symbol;
	public String getPrid() {
		return prid;
	}
	public void setPrid(String prid) {
		this.prid = prid;
	}
	public String getSymbol() {
		return symbol;
	}
	public void setSymbol(String symbol) {
		this.symbol = symbol;
	}
	
	
}
