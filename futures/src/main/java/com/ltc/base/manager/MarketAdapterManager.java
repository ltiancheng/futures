package com.ltc.base.manager;

import java.text.ParseException;

import com.ltc.base.vo.ContractVO;

public interface MarketAdapterManager {

	void updateCurrentBarInfo(ContractVO c);

	ContractVO getTopVolContract(ContractVO currentContract) throws ParseException;
	
}
