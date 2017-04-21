package com.ltc.base.manager;

import com.ltc.base.vo.CommandVO;
import com.ltc.base.vo.ContractVO;

public interface CommandManager {

	void executeCommand(ContractVO contract, CommandVO command, Strategy callbackStrategy);

	void initCommandListener();

}
