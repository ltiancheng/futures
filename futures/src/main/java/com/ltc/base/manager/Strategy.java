package com.ltc.base.manager;

import java.util.List;

import com.ltc.base.vo.CommandVO;
import com.ltc.base.vo.ContractVO;
import com.ltc.base.vo.RuleVO;
import com.ltc.strategy.tortoise.vo.PositionVO;

public interface Strategy {

	void saveStatus();

	void initRules();

	void ruleTriggered(RuleVO rule);

	void onCommand(String contractKey, CommandVO command);
	
	List<RuleVO> generateRulesOnContract(ContractVO contract);

	void mainSwitch(ContractVO c, ContractVO nmc);
	
	void refreshStopLossEquity();

	void updateTopPrice();

	void onCommandFailed(String contractKey, CommandVO command);

	void onPositionChance2Run(PositionVO position);

	void clearOutstandingCommands(int mINUTE_GAP);

	void startForceSwitch();

	void updateAtr();
}
