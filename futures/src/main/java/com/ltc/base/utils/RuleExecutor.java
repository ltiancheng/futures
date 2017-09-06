package com.ltc.base.utils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.CollectionUtils;

import com.ltc.base.helpers.BaseConstant;
import com.ltc.base.manager.CommandManager;
import com.ltc.base.manager.ContractHolder;
import com.ltc.base.manager.RuleHolder;
import com.ltc.base.manager.Strategy;
import com.ltc.base.manager.TimeManager;
import com.ltc.base.vo.ConditionVO;
import com.ltc.base.vo.ContractVO;
import com.ltc.base.vo.RuleVO;
import com.ltc.strategy.tortoise.manager.PortfolioHolder;
import com.ltc.strategy.tortoise.utils.StrategyUtils;
import com.ltc.strategy.tortoise.vo.PositionVO;


public class RuleExecutor extends BaseStartupItem implements Runnable {
	
	private static Logger logger = LoggerFactory.getLogger(RuleExecutor.class);
	
	private RuleHolder ruleHolder;
	private TimeManager timeManager;
	private Strategy strategy;
	private ContractHolder contractHolder;
	private CommandManager commandManager;
	private PortfolioHolder portfolioHolder;
	
	public void setPortfolioHolder(PortfolioHolder portfolioHolder) {
		this.portfolioHolder = portfolioHolder;
	}

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
				Map<Integer, Integer> firedLongMap = new HashMap<Integer, Integer>();
				Map<Integer, Integer> firedShortMap = new HashMap<Integer, Integer>();
				if(!CollectionUtils.isEmpty(ruleMap)){
					List<String> sortedKeys = StrategyUtils.sortContractKeyByPriority(ruleMap.keySet(), contractHolder.getContractCodePriorityMap());
					for(String contractKey : sortedKeys){
						ContractVO contract = this.contractHolder.getContractByKey(contractKey);
						if(contract != null){
							List<RuleVO> rules = ruleMap.get(contractKey);
							if(CollectionUtils.isEmpty(rules)){
								rules.addAll(strategy.generateRulesOnContract(contract));
							} else if(!hasTriggeredRules(rules)) {
								for(RuleVO rule : rules){
									if(contract.getCurrentBar() == null){
										contract.setCurrentBar(this.contractHolder.getBarFromGw(contract));
									}
									if(meetCondition(rule.getCondition(), contract)){
										PositionVO position = portfolioHolder.getPositionByContractMeta(contract.getContractMeta());
										String dir = StrategyUtils.getOpenDirect(rule.getCommand().getInstruction());
										boolean isCloseInstruct = StrategyUtils.isCloseInstruction(rule.getCommand().getInstruction());
										if(!StringUtils.equals(position.getStatus(), BaseConstant.ACTIVE) 
												|| !StrategyUtils.isFullPortfolioWithFiredCmd(portfolioHolder.getPortfolio(), position, dir, firedLongMap, firedShortMap) 
												|| isCloseInstruct){
											commandManager.executeCommand(contract, rule.getCommand(), strategy);
											triggeredRules.add(rule);
											if(StringUtils.equals(position.getStatus(), BaseConstant.ACTIVE) && !isCloseInstruct){
												if(StringUtils.equals(dir, PositionVO.LONG)){
													updateGroupCount(contract, rule, firedLongMap);
												} else if(StringUtils.equals(dir, PositionVO.SHORT)){
													updateGroupCount(contract, rule, firedShortMap);
												}
											}
											break;
										}
									}
								}
							} else if(hasOldRules(rules)) {
								logger.info("triggered rules of {} is too old, cleaning & regenerating", contractKey);
								rules.clear();
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

	private void updateGroupCount(ContractVO contract, RuleVO rule, Map<Integer, Integer> firedMap) {
		Integer count = firedMap.get(contract.getContractMeta().getGroup().getId());
		if(count == null){
			firedMap.put(contract.getContractMeta().getGroup().getId(), rule.getCommand().getUnits());
		} else {
			firedMap.put(contract.getContractMeta().getGroup().getId(), rule.getCommand().getUnits() + count);
		}
	}

	private boolean hasOldRules(List<RuleVO> rules) {
		for(RuleVO r : rules){
			if(r.isOld()){
				return true;
			}
		}
		return false;
	}

	private boolean hasTriggeredRules(List<RuleVO> rules) {
		for(RuleVO r: rules){
			if(r.isTriggered()){
				return true;
			}
		}
		return false;
	}

	private boolean meetCondition(ConditionVO condition, ContractVO contract) {
		return condition.meet(contract);
	}

}
