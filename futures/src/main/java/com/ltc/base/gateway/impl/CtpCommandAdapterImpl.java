package com.ltc.base.gateway.impl;

import javax.jms.MessageListener;

import com.ltc.base.gateway.CommandAdapter;
import com.ltc.base.gateway.ctp.CtpManager;
import com.ltc.base.vo.CommandVO;
import com.ltc.base.vo.ContractVO;
import com.ltc.base.vo.FullCommandVO;

public class CtpCommandAdapterImpl implements CommandAdapter {

	private CtpManager ctpManager;
	
	public void setCtpManager(CtpManager ctpManager) {
		this.ctpManager = ctpManager;
	}

	@Override
	public void executeCommand(ContractVO contract, CommandVO command) {
		ctpManager.sendTradeCommand(contract, command);
	}

	@Override
	public void initCommandListener(MessageListener succCommandListener, MessageListener errCommandListener) {
		ctpManager.registerCommandListener(succCommandListener, errCommandListener);
	}

	@Override
	public FullCommandVO parseToFullCommand(String textStr) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public FullCommandVO parseToFailedFullCommand(String textStr) {
		// TODO Auto-generated method stub
		return null;
	}

}
