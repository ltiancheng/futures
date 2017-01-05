package com.ltc.base.manager;

import com.ltc.base.vo.CommandVO;
import com.ltc.base.vo.ContractVO;
import com.ltc.base.vo.RuleVO;

public interface Strategy {

	void saveStatus();

	void initRules();

	void ruleTriggered(RuleVO rule);

	void onCommand(ContractVO contract, CommandVO command);

}
