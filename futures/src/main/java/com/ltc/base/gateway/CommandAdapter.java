package com.ltc.base.gateway;

import com.ltc.base.vo.CommandVO;
import com.ltc.base.vo.ContractVO;

public interface CommandAdapter {

	void executeCommand(ContractVO contract, CommandVO command);

}
