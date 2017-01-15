package com.ltc.strategy.tortoise.manager.impl;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.ltc.base.manager.ContractHolder;
import com.ltc.base.manager.RuleHolder;
import com.ltc.base.manager.Strategy;
import com.ltc.base.vo.BarVO;
import com.ltc.base.vo.CommandVO;
import com.ltc.base.vo.ConditionVO;
import com.ltc.base.vo.ContractVO;
import com.ltc.base.vo.RuleVO;
import com.ltc.strategy.tortoise.manager.PortfolioHolder;
import com.ltc.strategy.tortoise.utils.StrategyUtils;
import com.ltc.strategy.tortoise.vo.PortfolioVO;
import com.ltc.strategy.tortoise.vo.PositionVO;
import com.ltc.strategy.tortoise.vo.StrategyPricePointVO;

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
	
	public static final int OPEN_BAR_SIZE = 20;
	public static final int CLOSE_BAR_SIZE = 10;
	
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
		ContractVO contract = p.getContract();
		List<BarVO> barList = contractHolder.getBarHist(contract.getKey(), OPEN_BAR_SIZE);
		StrategyPricePointVO spp = StrategyUtils.getPricePoint(barList);
		List<RuleVO> ruleList = new ArrayList<RuleVO>();
		if(!StrategyUtils.isFullPortfolio(portfolio)){
			ruleList.addAll(generateOpenRules(p, spp));
		}
		ruleList.addAll(generateCloseRules(p, spp));
		return ruleList;
	}

	private List<RuleVO> generateCloseRules(PositionVO p, StrategyPricePointVO spp) {
		List<RuleVO> ruleList = new ArrayList<RuleVO>();
		if(p.getUnitCount() == 0){
			return ruleList;
		} else if(StringUtils.equals(p.getDirection(), PositionVO.LONG)) {
			double clp = spp.getCloseLongPoint();
			double slp = p.getLastInPrice() - p.getContract().getContractMeta().getAtr() * 2;
			double stp = slp;
			if((p.getContract().getCurrentBar().getClosePrice() - p.getLastInPrice()) >= (p.getContract().getContractMeta().getAtr() * 2)){
				stp = Math.max(clp, slp);
			}
			ConditionVO condition = new ConditionVO();
			condition.setAboveCondition(false);
			condition.setTriggerValue(new BigDecimal(stp));
			condition.setType(ConditionVO.PRICE_TYPE);
			CommandVO command = new CommandVO();
			command.setHandPerUnit(p.getHandPerUnit());
			command.setInstruction(CommandVO.CLOSE_LONG);
			command.setPrice(new BigDecimal(stp));
			command.setPriceStyle(CommandVO.MARKET);
			command.setUnits(p.getUnitCount());
			RuleVO rule = new RuleVO();
			rule.setCommand(command);
			rule.setCondition(condition);
			rule.setContract(p.getContract());
			ruleList.add(rule);
		} else if(StringUtils.equals(p.getDirection(), PositionVO.SHORT)){
			double csp = spp.getCloseShortPoint();
			double slp = p.getLastInPrice() + p.getContract().getContractMeta().getAtr() * 2;
			double stp = slp;
			if((p.getLastInPrice() - p.getContract().getCurrentBar().getClosePrice()) >= (p.getContract().getContractMeta().getAtr() * 2)){
				stp = Math.min(csp, slp);
			}
			ConditionVO condition = new ConditionVO();
			condition.setAboveCondition(true);
			condition.setTriggerValue(new BigDecimal(stp));
			condition.setType(ConditionVO.PRICE_TYPE);
			CommandVO command = new CommandVO();
			command.setHandPerUnit(p.getHandPerUnit());
			command.setInstruction(CommandVO.CLOSE_SHORT);
			command.setPrice(new BigDecimal(stp));
			command.setPriceStyle(CommandVO.MARKET);
			command.setUnits(p.getUnitCount());
			RuleVO rule = new RuleVO();
			rule.setCommand(command);
			rule.setCondition(condition);
			rule.setContract(p.getContract());
			ruleList.add(rule);
		} else {
			logger.error("error position direction: "+p.getDirection());
		}
		return ruleList;
	}

	private List<RuleVO> generateOpenRules(PositionVO p, StrategyPricePointVO spp) {
		List<RuleVO> ruleList = new ArrayList<RuleVO>();
		if(p.getUnitCount() == 0){
			StrategyUtils.updateHandPerUnit(p, portfolioHolder.getPortfolio());
			//open long rule;
			{
				ConditionVO condition = new ConditionVO();
				condition.setAboveCondition(true);
				condition.setTriggerValue(new BigDecimal(spp.getOpenLongPoint()));
				condition.setType(ConditionVO.PRICE_TYPE);
				CommandVO command = new CommandVO();
				command.setHandPerUnit(p.getHandPerUnit());
				command.setInstruction(CommandVO.OPEN_LONG);
				command.setPrice(new BigDecimal(spp.getOpenLongPoint()));
				command.setPriceStyle(CommandVO.MARKET);
				command.setUnits(1);
				RuleVO rule = new RuleVO();
				rule.setCommand(command);
				rule.setCondition(condition);
				rule.setContract(p.getContract());
				ruleList.add(rule);
			}
			//open short rule;
			{
				ConditionVO condition = new ConditionVO();
				condition.setAboveCondition(false);
				condition.setTriggerValue(new BigDecimal(spp.getOpenShortPoint()));
				condition.setType(ConditionVO.PRICE_TYPE);
				CommandVO command = new CommandVO();
				command.setHandPerUnit(p.getHandPerUnit());
				command.setInstruction(CommandVO.OPEN_SHORT);
				command.setPrice(new BigDecimal(spp.getOpenShortPoint()));
				command.setPriceStyle(CommandVO.MARKET);
				command.setUnits(1);
				RuleVO rule = new RuleVO();
				rule.setCommand(command);
				rule.setCondition(condition);
				rule.setContract(p.getContract());
				ruleList.add(rule);
			}
		} else if(p.getUnitCount() == 1){
			if(StringUtils.equals(p.getDirection(), PositionVO.LONG)){
				ConditionVO condition = new ConditionVO();
				condition.setAboveCondition(true);
				condition.setTriggerValue(new BigDecimal(p.getLastInPrice() + p.getContract().getContractMeta().getAtr()));
				condition.setType(ConditionVO.PRICE_TYPE);
				CommandVO command = new CommandVO();
				command.setHandPerUnit(p.getHandPerUnit());
				command.setInstruction(CommandVO.OPEN_LONG);
				command.setPrice(condition.getTriggerValue());
				command.setPriceStyle(CommandVO.MARKET);
				command.setUnits(1);
				RuleVO rule = new RuleVO();
				rule.setCommand(command);
				rule.setCondition(condition);
				rule.setContract(p.getContract());
				ruleList.add(rule);
			} else if(StringUtils.equals(p.getDirection(), PositionVO.SHORT)){
				ConditionVO condition = new ConditionVO();
				condition.setAboveCondition(false);
				condition.setTriggerValue(new BigDecimal(p.getLastInPrice() - p.getContract().getContractMeta().getAtr()));
				condition.setType(ConditionVO.PRICE_TYPE);
				CommandVO command = new CommandVO();
				command.setHandPerUnit(p.getHandPerUnit());
				command.setInstruction(CommandVO.OPEN_SHORT);
				command.setPrice(condition.getTriggerValue());
				command.setPriceStyle(CommandVO.MARKET);
				command.setUnits(1);
				RuleVO rule = new RuleVO();
				rule.setCommand(command);
				rule.setCondition(condition);
				rule.setContract(p.getContract());
				ruleList.add(rule);
			} else {
				logger.error("error position direction: "+p.getDirection());
			}
		}
		return ruleList;
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
			} else {
				position.setLastInPrice(command.getPrice().floatValue());
			}
			position.setAveragePrice(position.getLastInPrice());
		} else if(inSameDirection(position.getDirection(), command.getInstruction())){
			if(position.getHandPerUnit() == command.getHandPerUnit()){
				if(command.isDone()){
					position.setLastInPrice(command.getDealPrice().floatValue());
				} else {
					position.setLastInPrice(command.getPrice().floatValue());
				}
				position.setAveragePrice((position.getLastInPrice()*command.getUnits()+
						position.getAveragePrice()*position.getUnitCount())/(position.getUnitCount()+command.getUnits()));
				position.setUnitCount(position.getUnitCount()+command.getUnits());
			} else {
				logger.error("different hand per unit, position.handPerUnit="+position.getHandPerUnit()
					+", command.handPerUnit="+command.getHandPerUnit()); 
			}
		} else {
			if(position.getHandPerUnit() == command.getHandPerUnit()){
				position.setUnitCount(position.getUnitCount()-command.getUnits());
				if(position.getUnitCount() == 0){
					//fresh total cash(including margin)
					this.updateCashWhenEmpty(portfolio, position, command);
					
					position.setDirection("");
					position.setLastInPrice(0);
					position.setAveragePrice(0);
				} else {
					logger.error("behaviour not expected during SL, position.unitCount= "+position.getUnitCount());
				}
			} else {
				logger.error("different hand per unit, position.handPerUnit="+position.getHandPerUnit()
					+", command.handPerUnit="+command.getHandPerUnit()); 
			}
		}
	}

	private void updateCashWhenEmpty(PortfolioVO portfolio, PositionVO position, CommandVO command) {
		float dealPrice = command.getDealPrice().floatValue();
		if(command.getDealPrice() == null || command.getDealPrice().equals(0)){
			dealPrice = command.getPrice().floatValue();
		}
		double cashAmount = (dealPrice - position.getAveragePrice()) * command.getUnits() * 
				command.getHandPerUnit() * position.getContract().getContractMeta().getPointValue();
		if(StringUtils.equals(position.getDirection(), PositionVO.LONG)){
			portfolio.setCash(portfolio.getCash() + cashAmount);
		} else {
			portfolio.setCash(portfolio.getCash() - cashAmount);
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
		//do nothing.
	}

}
