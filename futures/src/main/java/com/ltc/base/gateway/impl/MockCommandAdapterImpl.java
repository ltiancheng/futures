package com.ltc.base.gateway.impl;

import javax.jms.MessageListener;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.ltc.base.gateway.CommandAdapter;
import com.ltc.base.vo.CommandVO;
import com.ltc.base.vo.ContractVO;
import com.ltc.base.vo.FullCommandVO;

public class MockCommandAdapterImpl implements CommandAdapter {

	private static Logger logger = LoggerFactory.getLogger("COMMAND");
	
	@Override
	public void executeCommand(ContractVO contract, CommandVO command) {
		logger.info("command finished: "+command.getInstruction()+" "
			+command.getHandPerUnit()*command.getUnits()+" "+contract.getKey()+" at "+command.getPrice());
	}

	@Override
	public void initCommandListener(MessageListener succCommandListener, MessageListener errCommandListener) {
		
	}

	@Override
	public FullCommandVO parseToFullCommand(String textStr) {
		return null;
	}

	@Override
	public FullCommandVO parseToFailedFullCommand(String textStr) {
		return null;
	}

}
