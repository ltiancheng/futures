package com.ltc.base.gateway.impl;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.ltc.base.gateway.CommandAdapter;
import com.ltc.base.vo.CommandVO;
import com.ltc.base.vo.ContractVO;

public class MockCommandAdapterImpl implements CommandAdapter {

	private static Logger logger = LoggerFactory.getLogger("CommandAppender");
	
	@Override
	public void executeCommand(ContractVO contract, CommandVO command) {
		logger.info("command finished: "+command.getInstruction()+" "
			+command.getHandPerUnit()*command.getUnits()+" "+contract.getKey()+" at "+command.getPrice());
	}

}
