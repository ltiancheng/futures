package com.ltc.base.manager;

import java.util.List;

import com.ltc.base.vo.CommandVO;
import com.ltc.base.vo.ContractVO;
import com.ltc.base.vo.RuleVO;

public interface Strategy {

	void saveStatus();

	void initRules();

	void ruleTriggered(RuleVO rule);

	void onCommand(ContractVO contract, CommandVO command);
	
	List<RuleVO> generateRulesOnContract(ContractVO contract);

	void mainSwitch(ContractVO c, ContractVO nmc);
	
	void refreshStopLossEquity();
}
