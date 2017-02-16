package com.ltc.strategy.tortoise.manager.impl;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

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
		try{
			refreshCurrentEquity(portfolioHolder.getPortfolio());
		} catch (Exception e){
			logger.warn("refreshPortoflio failed!", e);
		}
		portfolioHolder.saveCurrentStatus();
		logger.info("[StrategyImpl] current portfolio: "+portfolioHolder.getPortfolio().toString());
	}
	
	//refresh stop loss equity
	public void refreshStopLossEquity(){
		PortfolioVO portfolio = portfolioHolder.getPortfolio();
		Set<PositionVO> positionSet = portfolio.getPositionSet();
		double stopLossEquity = portfolio.getCash();
		Map<String, List<RuleVO>> ruleMap = ruleHolder.getRuleMap();
		for(PositionVO p: positionSet){
			if(StringUtils.isNotBlank(p.getDirection())){
				//fresh close equity
				List<RuleVO> rules = ruleMap.get(p.getContract().getKey());
				float closePrice = 0;
				for(RuleVO r : rules){
					String instruction = r.getCommand().getInstruction();
					if(StringUtils.equals(CommandVO.CLOSE_LONG, instruction) || StringUtils.equals(CommandVO.CLOSE_SHORT, instruction)){
						closePrice = r.getCommand().getPrice().floatValue();
						break;
					}
				}
				if(closePrice > 0){
					double cashAmount = (closePrice - p.getAveragePrice()) * p.getUnitCount() * 
							p.getHandPerUnit() * p.getContract().getContractMeta().getPointValue();
					if(StringUtils.equals(p.getDirection(), PositionVO.LONG)){
						stopLossEquity = stopLossEquity + cashAmount;
					} else {
						stopLossEquity = stopLossEquity - cashAmount;
					}
				}
			}
		}
		portfolio.setStopLossEquity(stopLossEquity);
	}

	//refresh current equity
	private void refreshCurrentEquity(PortfolioVO portfolio) {
		Set<PositionVO> positionSet = portfolio.getPositionSet();
		double currentEquity = portfolio.getCash();
		for(PositionVO p: positionSet){
			if(StringUtils.isNotBlank(p.getDirection())){
				//fresh current equity
				BarVO bar = contractHolder.getContractByKey(p.getContract().getKey()).getCurrentBar();
				if(bar != null){
					float currentPrice = bar.getClosePrice();
					if(currentPrice > 0){
						double cashAmount = (currentPrice - p.getAveragePrice()) * p.getUnitCount() * 
								p.getHandPerUnit() * p.getContract().getContractMeta().getPointValue();
						if(StringUtils.equals(p.getDirection(), PositionVO.LONG)){
							currentEquity = currentEquity + cashAmount;
						} else {
							currentEquity = currentEquity - cashAmount;
						}
					}
				} else {
					logger.warn("current bar is null: "+p.getContract().getKey());
				}
				
			}
		}
		portfolio.setCurrentEquity(currentEquity);
	}

	@Override
	public void initRules() {
		List<ContractVO> contractList = contractHolder.getActiveContractList();
		ruleHolder.clearRule();
		List<ContractVO> untrackedContracts = portfolioHolder.getUntrackedContracts(contractList);
		portfolioHolder.addPositions(untrackedContracts);
		PortfolioVO portfolio = portfolioHolder.getPortfolio();
		Set<PositionVO> positions = portfolio.getPositionSet();
		for(PositionVO p : positions){
			List<RuleVO> rules = generateRulesOnContract(p, portfolio);
			for(RuleVO r : rules){
				ruleHolder.addRule(p.getContract().getKey(), r);
			}
		}
		logger.info("[StrategyImpl] portfolio initiated: "+portfolio.toString());
	}

	private List<RuleVO> generateRulesOnContract(PositionVO p, PortfolioVO portfolio) {
		ContractVO contract = contractHolder.getContractByKey(p.getContract().getKey());
		if(contract.getCurrentBar() == null){
			logger.warn("[StrategyImpl] return empty rule list due to the null current bar of "+contract.getKey());
			return new ArrayList<RuleVO>();
		}
		List<BarVO> barList = contractHolder.getBarHist(contract, OPEN_BAR_SIZE);
		StrategyPricePointVO spp = StrategyUtils.getPricePoint(barList);
		List<RuleVO> ruleList = new ArrayList<RuleVO>();
		if(!StrategyUtils.isFullPortfolio(portfolio)){
			ruleList.addAll(generateOpenRules(p, spp));
		}
		ruleList.addAll(generateCloseRules(p, spp));
		return ruleList;
	}
	
	@Override
	public List<RuleVO> generateRulesOnContract(ContractVO contract) {
		try{
			PortfolioVO portfolio = this.portfolioHolder.getPortfolio();
			PositionVO p = this.portfolioHolder.getPositionByContract(contract);
			return this.generateRulesOnContract(p, portfolio);
		} catch (Exception e){
			logger.error("[StrategyImpl] exception during generating rules on contracts ", e);
			return new ArrayList<RuleVO>();
		}
	}

	private List<RuleVO> generateCloseRules(PositionVO p, StrategyPricePointVO spp) {
		List<RuleVO> ruleList = new ArrayList<RuleVO>();
		BarVO currentBar = contractHolder.getContractByKey(p.getContract().getKey()).getCurrentBar();
		if(p.getUnitCount() == 0){
			return ruleList;
		} else if(StringUtils.equals(p.getDirection(), PositionVO.LONG)) {
			double clp = spp.getCloseLongPoint();
			double slp = p.getLastInPrice() - p.getContract().getContractMeta().getAtr() * 2;
			double stp = slp;
			if(currentBar == null){
				logger.warn("[StrategyImpl]current bar is null of "+p.getContract().getKey());
			} else {
				if((currentBar.getClosePrice() - p.getLastInPrice()) >= (p.getContract().getContractMeta().getAtr() * 2)){
					stp = Math.max(clp, slp);
				}
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
			if(currentBar == null){
				logger.warn("[StrategyImpl]current bar is null of "+p.getContract().getKey());
			} else {
				if((p.getLastInPrice() - currentBar.getClosePrice()) >= (p.getContract().getContractMeta().getAtr() * 2)){
					stp = Math.min(csp, slp);
				}
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
			if(p.getHandPerUnit() != 0){
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
		float dealPrice=0;
		if(command.getDealPrice() == null || command.getDealPrice().equals(0)){
			dealPrice = command.getPrice().floatValue();
		} else {
			dealPrice = command.getDealPrice().floatValue();
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

	@Override
	public void mainSwitch(ContractVO c, ContractVO nmc) {
		PositionVO position = this.portfolioHolder.getPositionByContract(c);
		if(StringUtils.isEmpty(position.getDirection())){
			position.setContract(nmc);
			this.portfolioHolder.saveCurrentStatus();
			contractHolder.mainSwitch(c, nmc);
		}
	}

}
