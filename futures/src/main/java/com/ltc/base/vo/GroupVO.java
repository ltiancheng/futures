package com.ltc.base.vo;

import java.io.Serializable;
import java.util.List;

public class GroupVO implements Serializable {

	private static final long serialVersionUID = 2085264585740351880L;
	
	private int id;
	private String desc;
	private List<ContractMetaVO> contractMetas;
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	public String getDesc() {
		return desc;
	}
	public void setDesc(String desc) {
		this.desc = desc;
	}
	public List<ContractMetaVO> getContractMetas() {
		return contractMetas;
	}
	public void setContractMetas(List<ContractMetaVO> contractMetas) {
		this.contractMetas = contractMetas;
	}
	
}
