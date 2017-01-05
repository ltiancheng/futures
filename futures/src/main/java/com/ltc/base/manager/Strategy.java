package com.ltc.base.manager;

import com.ltc.base.vo.RuleVO;

public interface Strategy {

	void saveStatus();

	void initRules();

	void ruleTriggered(RuleVO rule);

}
