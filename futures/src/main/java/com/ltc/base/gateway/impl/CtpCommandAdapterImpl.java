package com.ltc.base.gateway.impl;

import java.math.BigDecimal;

import javax.jms.MessageListener;

import org.apache.commons.lang.StringUtils;

import com.ltc.base.gateway.CommandAdapter;
import com.ltc.base.gateway.ctp.CtpManager;
import com.ltc.base.gateway.ctp.helper.CtpHelper;
import com.ltc.base.gateway.ctp.vo.CThostFtdcTradeField;
import com.ltc.base.gateway.ctp.vo.TradeErrResp;
import com.ltc.base.helpers.BaseUtils;
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
		CThostFtdcTradeField rawClass = BaseUtils.json2Obj(textStr, CThostFtdcTradeField.class);
		if(rawClass != null && rawClass.InstrumentID != null){
			FullCommandVO fcvo = new FullCommandVO();
			fcvo.setContractKey(rawClass.InstrumentID.toUpperCase());
			CommandVO command = new CommandVO();
			command.setDealPrice(new BigDecimal(rawClass.Price));
			command.setDone(true);
			command.setHandPerUnit(rawClass.Volume);
			command.setUnits(1);
			command.setInstruction(CtpHelper.parseInstruction(rawClass.OffsetFlag, rawClass.Direction));
			fcvo.setCommand(command);
			return fcvo;
		} else {
			return null;
		}
	}

	@Override
	public FullCommandVO parseToFailedFullCommand(String textStr) {
		TradeErrResp resp = BaseUtils.json2Obj(textStr, TradeErrResp.class);
		if(resp != null && resp.inputOrder != null && resp.rspInfo != null && StringUtils.isNotBlank(resp.inputOrder.InstrumentID)){
			FullCommandVO fcvo = new FullCommandVO();
			fcvo.setContractKey(resp.inputOrder.InstrumentID.toUpperCase());
			return fcvo;
		} else {
			return null;
		}
	}

}
