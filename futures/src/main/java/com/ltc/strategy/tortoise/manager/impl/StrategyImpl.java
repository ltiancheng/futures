package com.ltc.strategy.tortoise.manager.impl;

import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.slf4j.LoggerFactory;

import com.ltc.base.manager.ContractHolder;
import com.ltc.base.manager.RuleHolder;
import com.ltc.base.manager.Strategy;
import com.ltc.base.vo.CommandVO;
import com.ltc.base.vo.ContractVO;
import com.ltc.base.vo.RuleVO;
import com.ltc.strategy.tortoise.manager.PortfolioHolder;
import com.ltc.strategy.tortoise.vo.PortfolioVO;
import com.ltc.strategy.tortoise.vo.PositionVO;

import org.slf4j.Logger;

public class StrategyImpl implements Strategy {

	private static Logger logger = LoggerFactory.getLogger(StrategyImpl.class);
	
	/**
	 * Strategy Content:
	 * 1. ATR������������Լ250���ATRֵ�����˹����£�(1��1�գ�4��1�գ�7��1�գ�10��1��)������һ��
	 * 2. ���ױ��Ϊ���л�Ծ��������Լ���Ƿ��Ծ���˹���ǣ�ÿ��1��1�ո���һ��
	 * 3. 20��ͻ�ƽ�����10�շ���ͻ���볡
	 * 6. �����ƶ�1 ATR�ͼӲ֣��Ӳֽ���һ��
	 * 7. �����е�ͷ�����������ƶ�1 ATRʱ�������������ʧ����10%����ô��ʱ��Ϊ���֣����������κ�ͷ��
	 * 8. ����ʱ������2 ATRΪֹ��λ���Ӳ�һ���Ժ�ֹ��λ����Ӧ��ǰ��1 ATR��
	 *    ���10�շ���ߵ͵���ӯ���ģ���ô��10�շ���ߵ͵�Ϊֹӯλ
	 * 9. ͷ���С���㹫ʽ��ͷ���ģ=ֹ��Ȩ���0.5%/(2ATR*�����ֵ)
	 * 10.(optional)�Զ��л�������Լ 
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
		//TODO: Update Cash info;
		if(StringUtils.isEmpty(position.getDirection())){
			if(CommandVO.OPEN_LONG.equals(command.getInstruction())){
				position.setDirection(PositionVO.LONG);
			} else if(CommandVO.OPEN_SHORT.equals(command.getInstruction())){
				position.setDirection(PositionVO.SHORT);
			}
			position.setUnitCount(command.getUnits());
			position.setHandPerUnit(command.getHandPerUnit());
			if(command.isDone()){
				position.setLastInPrice(command.getDealPrice().floatValue());
			}
		} else if(inSameDirection(position.getDirection(), command.getInstruction())){
			if(position.getHandPerUnit() == command.getHandPerUnit()){
				position.setUnitCount(position.getUnitCount()+command.getUnits());
				if(command.isDone()){
					position.setLastInPrice(command.getDealPrice().floatValue());
				}
			} else {
				logger.error("different hand per unit, position.handPerUnit="+position.getHandPerUnit()
					+", command.handPerUnit="+command.getHandPerUnit()); 
			}
		} else {
			if(position.getHandPerUnit() == command.getHandPerUnit()){
				position.setUnitCount(position.getUnitCount()-command.getUnits());
				if(position.getUnitCount() == 0){
					position.setDirection("");
					position.setLastInPrice(0);
				}
			} else {
				logger.error("different hand per unit, position.handPerUnit="+position.getHandPerUnit()
					+", command.handPerUnit="+command.getHandPerUnit()); 
			}
		}
	}

	private boolean inSameDirection(String direction, String instruction) {
		if(StringUtils.equals(direction, PositionVO.LONG) && StringUtils.equals(instruction, CommandVO.OPEN_LONG)){
			return true;
		}
		if(StringUtils.equals(direction, PositionVO.SHORT) && StringUtils.equals(instruction, CommandVO.OPEN_SHORT)){
			return true;
		}
		return false;
	}

	@Override
	public void onCommand(ContractVO contract, CommandVO command) {
		PositionVO position = portfolioHolder.getPositionByContract(contract);
		if(StringUtils.equals(command.getInstruction(), CommandVO.OPEN_LONG) 
				|| StringUtils.equals(command.getInstruction(), CommandVO.OPEN_SHORT)){
			if(command.isDone()){
				position.setLastInPrice(command.getDealPrice().floatValue());
			}
		}
	}

}
