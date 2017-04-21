package com.ltc.base.gateway;

import javax.jms.MessageListener;

import com.ltc.base.vo.CommandVO;
import com.ltc.base.vo.ContractVO;
import com.ltc.base.vo.FullCommandVO;

public interface CommandAdapter {

	void executeCommand(ContractVO contract, CommandVO command);

	void initCommandListener(MessageListener succCommandListener, MessageListener errCommandListener);

	FullCommandVO parseToFullCommand(String textStr);

	FullCommandVO parseToFailedFullCommand(String textStr);

}
