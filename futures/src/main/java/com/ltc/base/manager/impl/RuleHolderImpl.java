package com.ltc.base.manager.impl;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.ltc.base.manager.RuleHolder;
import com.ltc.base.manager.TimeManager;
import com.ltc.base.vo.RuleVO;

public class RuleHolderImpl implements RuleHolder {

	private static Logger logger = LoggerFactory.getLogger(RuleHolderImpl.class);
	
	private Map<String, List<RuleVO>> ruleMap = new HashMap<String, List<RuleVO>>();
	private TimeManager timeManager;
	private Date ruleRefreshTime;
	
	public void setTimeManager(TimeManager timeManager) {
		this.timeManager = timeManager;
	}

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
		logger.info("[RuleHolderImpl rule map refreshed: ]\\r\\n"+rm);
	}
	
	public static void main(String[] args){
		Map<String, List<String>> mapTest = new HashMap<String, List<String>>();
		List<String> strList1 = new ArrayList<String>();
		strList1.add("string1");
		strList1.add("string2");
		mapTest.put("key1", strList1);
		System.out.println(mapTest);
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
