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
			fcvo.setContractKey(BaseUtils.ctpKey2Key(rawClass.InstrumentID));
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
	
	public static void main(String[] args){
		CtpCommandAdapterImpl ca = new CtpCommandAdapterImpl();
		String textStr = "{BrokerID:\"9999\",InvestorID:\"089058\",InstrumentID:\"MA709\",OrderRef:\"           1\",UserID:\"089058\",ExchangeID:\"CZCE\",TradeID:\"       13240\",Direction:49.000000,OrderSysID:\"       20111\",ParticipantID:\"9999\",ClientID:\"9999089038\",TradingRole:0.000000,ExchangeInstID:\"MA709\",OffsetFlag:48.000000,HedgeFlag:49.000000,Price:2277.000000,Volume:2.000000,TradeDate:\"20170504\",TradeTime:\"21:36:19\",TradeType:0.000000,PriceSource:0.000000,TraderID:\"9999cae\",OrderLocalID:\"        1616\",ClearingPartID:\"9999\",BusinessUnit:\"\",SequenceNo:2274.000000,TradingDay:\"20170505\",SettlementID:1.000000,BrokerOrderSeq:22571.000000,TradeSource:48.000000}";
		FullCommandVO fm = ca.parseToFullCommand(textStr);
		System.out.println(fm.getContractKey());
	}

	@Override
	public FullCommandVO parseToFailedFullCommand(String textStr) {
		TradeErrResp resp = BaseUtils.json2Obj(textStr, TradeErrResp.class);
		if(resp != null && resp.inputOrder != null && resp.rspInfo != null && StringUtils.isNotBlank(resp.inputOrder.InstrumentID)){
			FullCommandVO fcvo = new FullCommandVO();
			fcvo.setContractKey(BaseUtils.ctpKey2Key(resp.inputOrder.InstrumentID));
			return fcvo;
		} else {
			return null;
		}
	}

}
