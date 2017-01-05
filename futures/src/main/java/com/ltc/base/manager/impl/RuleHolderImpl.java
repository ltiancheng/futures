package com.ltc.base.manager.impl;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.ltc.base.manager.RuleHolder;
import com.ltc.base.manager.TimeManager;
import com.ltc.base.vo.RuleVO;

public class RuleHolderImpl implements RuleHolder {

	private Map<String, List<RuleVO>> ruleMap = new HashMap<String, List<RuleVO>>();
	private TimeManager timeManager;
	private Date ruleRefreshTime;
	
	@Override
	public Map<String, List<RuleVO>> getRuleMap() {
		if(needRefreshRule()){
			this.clearRule();
			this.ruleRefreshTime = new Date();
		}
		return this.ruleMap;
	}
	
	private boolean needRefreshRule() {
		return this.timeManager.needRefreshBeforeOpen(this.ruleRefreshTime);
	}

	@Override
	public void addRule(String contractKey, RuleVO rule) {
		Map<String, List<RuleVO>> rm = this.getRuleMap();
		if(rm.containsKey(contractKey)){
			List<RuleVO> rl = rm.get(contractKey);
			if(rl == null){
				rl = new ArrayList<RuleVO>();
				rm.put(contractKey, rl);
			}
			rl.add(rule);
		} else {
			List<RuleVO> rl = new ArrayList<RuleVO>();
			rl.add(rule);
			rm.put(contractKey, rl);
		}
	}

	@Override
	public void clearRule() {
		this.ruleMap.clear();
	}

	@Override
	public void clearContractRule(String contractKey) {
		Map<String, List<RuleVO>> rm = this.getRuleMap();
		if(rm.containsKey(contractKey)){
			rm.remove(contractKey);
		}
	}

}
