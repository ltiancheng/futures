package com.ltc.base.vo;

import java.io.Serializable;

public class ContractVO implements Serializable {
	private static final long serialVersionUID = 1L;
	
	private ContractMetaVO contractMeta;
	private BarVO currentBar;
	private long id;
	private String prid;
	private String status;
	
	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
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
}
