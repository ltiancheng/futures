package com.ltc.base.service;

import java.util.List;

import com.ltc.base.vo.ContractMetaVO;
import com.ltc.base.vo.ContractVO;

public interface ContractService {

	List<ContractVO> getActiveContractList();
	List<ContractMetaVO> getContractMetaList();
	ContractVO getNextMainContract(ContractVO c);
	void saveNextMainContract(ContractVO nmc);
	void mainSwitch(ContractVO c, ContractVO nmc);
	List<ContractVO> getNextMainContractList();
}
