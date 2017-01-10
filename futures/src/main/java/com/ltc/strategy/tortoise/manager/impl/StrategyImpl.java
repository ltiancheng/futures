package com.ltc.strategy.tortoise.manager.impl;

import java.util.List;

import com.ltc.base.manager.ContractHolder;
import com.ltc.base.manager.RuleHolder;
import com.ltc.base.manager.Strategy;
import com.ltc.base.vo.CommandVO;
import com.ltc.base.vo.ContractVO;
import com.ltc.base.vo.RuleVO;
import com.ltc.strategy.tortoise.manager.PortfolioHolder;
import com.ltc.strategy.tortoise.vo.PortfolioVO;
import com.ltc.strategy.tortoise.vo.PositionVO;

public class StrategyImpl implements Strategy {

	/**
	 * Strategy Content:
	 * 1. ATR是主力连续合约250天的ATR值。由人工更新：(1月1日，4月1日，7月1日，10月1日)各更新一次
	 * 2. 交易标的为所有活跃的主力合约，是否活跃由人工标记，每年1月1日更新一次
	 * 3. 20日突破进场，10日反向突破离场
	 * 6. 行情移动1 ATR就加仓，加仓仅限一次
	 * 7. 当所有的头寸向不利方向移动1 ATR时，如果产生的损失超过10%，那么此时视为满仓，不再增加任何头寸
	 * 8. 开仓时，设置2 ATR为止损位，加仓一次以后，止损位置相应的前移1 ATR。
	 *    如果10日反向高低点是盈利的，那么以10日反向高低点为止盈位
	 * 9. 头寸大小计算公式：头寸规模=止损权益的0.5%/(2ATR*整点价值)
	 * 10.(optional)自动切换主力合约 
	 */
	
	private PortfolioHolder portfolioHolder;
	private RuleHolder ruleHolder;
	private ContractHolder contractHolder;
	
	public void setContractHolder(ContractHolder contractHolder) {
		this.contractHolder = contractHolder;
	}

	public void setRuleHolder(RuleHolder ruleHolder) {
		this.ruleHolder = ruleHolder;
	}

	public void setPortfolioHolder(PortfolioHolder portfolioHolder) {
		this.portfolioHolder = portfolioHolder;
	}

	@Override
	public void saveStatus() {
		portfolioHolder.saveCurrentStatus();
	}

	@Override
	public void initRules() {
		List<ContractVO> contractList = contractHolder.getActiveContractList();
		ruleHolder.clearRule();
		List<ContractVO> untrackedContracts = portfolioHolder.getUntrackedContracts(contractList);
		portfolioHolder.addPositions(untrackedContracts);
		PortfolioVO portfolio = portfolioHolder.getPortfolio();
		List<PositionVO> positions = portfolio.getPositionList();
		for(PositionVO p : positions){
			List<RuleVO> rules = generateRulesOnContract(p, portfolio);
			for(RuleVO r : rules){
				ruleHolder.addRule(p.getContract().getKey(), r);
			}
		}
	}

	private List<RuleVO> generateRulesOnContract(PositionVO p, PortfolioVO portfolio) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void ruleTriggered(RuleVO rule) {
		PortfolioVO portfolio = portfolioHolder.getPortfolio();
		PositionVO position = portfolioHolder.getPositionByContract(rule.getContract());
		this.updatePosition(position, portfolio, rule.getCommand());
		ruleHolder.clearContractRule(rule.getContract().getKey());
		List<RuleVO> rules = this.generateRulesOnContract(position, portfolio);
		for(RuleVO r: rules){
			ruleHolder.addRule(position.getContract().getKey(), r);
		}
	}

	private void updatePosition(PositionVO position, PortfolioVO portfolio, CommandVO command) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onCommand(ContractVO contract, CommandVO command) {
		// do nothing, it's done on rule triggered;
	}

}
