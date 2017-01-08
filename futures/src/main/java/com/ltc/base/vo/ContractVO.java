package com.ltc.base.vo;

public class ContractVO {
	private ContractMetaVO contractMeta;
	private BarVO currentBar;
	private long id;
	private String prid;
	
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
		// TODO Auto-generated method stub
		return null;
	}
}
