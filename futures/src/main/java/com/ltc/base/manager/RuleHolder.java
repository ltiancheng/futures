package com.ltc.base.manager;

import java.util.List;
import java.util.Map;

import com.ltc.base.vo.RuleVO;

public interface RuleHolder {
	
	//clear the rule map when new bar starts.
	Map<String, List<RuleVO>> getRuleMap();
	
	void addRule(String contractKey, RuleVO rule);
	
	void clearRule();
	
	void clearContractRule(String contractKey);

}
