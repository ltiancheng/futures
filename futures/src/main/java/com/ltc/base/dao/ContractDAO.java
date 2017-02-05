package com.ltc.base.dao;

import java.util.List;

import com.ltc.base.vo.ContractMetaVO;
import com.ltc.base.vo.ContractVO;

public interface ContractDAO {

	List<ContractVO> getActiveContractList();

	List<ContractMetaVO> getContractMetaList();

	ContractVO getNextMainContract(ContractVO c);

	void saveContract(ContractVO nmc);

	void mainSwitch(ContractVO c, ContractVO nmc);

}
