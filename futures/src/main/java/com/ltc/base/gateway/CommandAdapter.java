package com.ltc.base.gateway;

import com.ltc.base.vo.BarVO;
import com.ltc.base.vo.CommandVO;
import com.ltc.base.vo.ContractVO;

public interface CommandAdapter {

	BarVO getCurrentBar(ContractVO c);

	void executeCommand(ContractVO contract, CommandVO command);

}
