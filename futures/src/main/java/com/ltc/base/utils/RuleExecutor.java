package com.ltc.base.utils;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.CollectionUtils;

import com.ltc.base.manager.CommandManager;
import com.ltc.base.manager.ContractHolder;
import com.ltc.base.manager.RuleHolder;
import com.ltc.base.manager.Strategy;
import com.ltc.base.manager.TimeManager;
import com.ltc.base.vo.ConditionVO;
import com.ltc.base.vo.ContractVO;
import com.ltc.base.vo.RuleVO;


public class RuleExecutor extends BaseStartupItem implements Runnable {
	
	private static Logger logger = LoggerFactory.getLogger(RuleExecutor.class);
	
	private RuleHolder ruleHolder;
	private TimeManager timeManager;
	private Strategy strategy;
	private ContractHolder contractHolder;
	private CommandManager commandManager;
	
	public void setCommandManager(CommandManager commandManager) {
		this.commandManager = commandManager;
	}

	public void setContractHolder(ContractHolder contractHolder) {
		this.contractHolder = contractHolder;
	}

	public void setStrategy(Strategy strategy) {
		this.strategy = strategy;
	}

	public void setRuleHolder(RuleHolder ruleHolder) {
		this.ruleHolder = ruleHolder;
	}

	public void setTimeManager(TimeManager timeManager) {
		this.timeManager = timeManager;
	}

	@Override
	public void execute() {
		new Thread(this).start();
	}

	//1. monitor and check the rules at every round, execute the rule that meets the requirement.
	//2. once some rule get executed, notify the strategy to do the needful jobs, and delete the rule in rule pool.
	//3. rule pool is HashMap of ContractKey to rule list. 
	@Override
	public void run() {
		logger.info("[RuleExecutor] started");
		while(true){
			try {
				timeManager.waitTillNextRound();
				Map<String, List<RuleVO>> ruleMap = ruleHolder.getRuleMap();
				List<RuleVO> triggeredRules = new ArrayList<RuleVO>();
				if(!CollectionUtils.isEmpty(ruleMap)){
					for(Map.Entry<String, List<RuleVO>> entry: ruleMap.entrySet()){
						String contractKey = entry.getKey();
						ContractVO contract = this.contractHolder.getContractByKey(contractKey);
						if(contract != null){
							List<RuleVO> rules = entry.getValue();
							if(CollectionUtils.isEmpty(rules)){
								rules.addAll(strategy.generateRulesOnContract(contract));
							} else {
								for(RuleVO rule : rules){
									if(meetCondition(rule.getCondition(), contract)){
										commandManager.executeCommand(contract, rule.getCommand(), strategy);
										triggeredRules.add(rule);
										break;
									}
								}
							}
						}
					}
					for(RuleVO rule: triggeredRules){
						strategy.ruleTriggered(rule);
					}
				} else {
					this.strategy.initRules();
				}
			} catch (Exception e) {
				logger.error("error caught", e);
			}
		}
	}

	private boolean meetCondition(ConditionVO condition, ContractVO contract) {
		return condition.meet(contract);
	}

}
