package com.ltc.base.manager;

import java.text.ParseException;
import java.util.List;

import com.ltc.base.vo.ContractVO;

public interface MarketAdapterManager {

	void updateCurrentBarInfo(ContractVO c);

	ContractVO getTopVolContract(ContractVO currentContract) throws ParseException;

	void registContracts(List<ContractVO> contractList);

	void initContractListener();
	
}
