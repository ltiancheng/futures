package com.ltc.base.vo;

import java.io.Serializable;

import org.apache.commons.lang.StringUtils;

public class ContractVO implements Serializable {
	@Override
	public String toString() {
		return contractMeta.getSymbol()+prid;
	}

	private static final long serialVersionUID = 1L;
	
	private ContractMetaVO contractMeta;
	private BarVO currentBar;
	private String prid;
	private String status;

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public String getPrid() {
		return prid;
	}

	public void setPrid(String prid) {
		this.prid = prid;
	}

	public BarVO getCurrentBar() {
		return currentBar;
	}

	public void setCurrentBar(BarVO currentBar) {
		this.currentBar = currentBar;
	}

	public ContractMetaVO getContractMeta() {
		return contractMeta;
	}

	public void setContractMeta(ContractMetaVO contractMeta) {
		this.contractMeta = contractMeta;
	}

	public String getKey() {
		return (this.contractMeta.getSymbol() + this.getPrid());
	}
	
	@Override
	public boolean equals(Object c) {
		if(c instanceof ContractVO){
			return StringUtils.equals(this.getContractMeta().getSymbol(), ((ContractVO)c).getContractMeta().getSymbol()) 
					&& StringUtils.equals(this.prid, ((ContractVO)c).getPrid());
		} else {
			return false;
		}
	}
	@Override
	public int hashCode() {
		if(this.getContractMeta().getSymbol() == null && this.prid == null){
			return 0;
		} else {
			return (this.getContractMeta().getSymbol()+this.prid).hashCode();
		}
	}
}
