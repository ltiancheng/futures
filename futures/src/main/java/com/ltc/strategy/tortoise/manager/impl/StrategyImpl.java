package com.ltc.strategy.tortoise.manager.impl;

import java.math.BigDecimal;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.ltc.base.helpers.BaseConstant;
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
	private static Logger portfolioLogger = LoggerFactory.getLogger("Portfolio");
	private static Logger commandLogger = LoggerFactory.getLogger("COMMAND");
	
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
	private int forceSwitchDate;
	private int days2Urge;

	public int getDays2Urge() {
		return days2Urge;
	}

	public void setDays2Urge(int days2Urge) {
		this.days2Urge = days2Urge;
	}

	public int getForceSwitchDate() {
		return forceSwitchDate;
	}

	public void setForceSwitchDate(int forceSwitchDate) {
		this.forceSwitchDate = forceSwitchDate;
	}

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
		portfolioLogger.info("[StrategyImpl] current portfolio: "+portfolioHolder.getPortfolio().toString());
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
				if(CollectionUtils.isEmpty(rules)){
					rules = this.generateRulesOnContract(p.getContract());
					ruleMap.put(p.getContract().getKey(), rules);
				}
				if(!CollectionUtils.isEmpty(rules)){
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
						portfolioLogger.info("Position: "+p);
						if(StringUtils.equals(p.getDirection(), PositionVO.LONG)){
							currentEquity = currentEquity + cashAmount;
							portfolioLogger.info("profit: " + Double.toString(cashAmount));
						} else {
							currentEquity = currentEquity - cashAmount;
							portfolioLogger.info("profit: " + Double.toString(-cashAmount));
						}
						portfolioLogger.info("================================================================"); 
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
				ruleHolder.addRule(r.getContract().getKey(), r);
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
		if(isTime2CloseOld(p)){
			List<RuleVO> closeRules = closeOldPostion(p);
			return closeRules;
		}
		if(isTime2OpenNew(p)){
			// update contracts
			ContractVO nextMainContract = contractHolder.getNextMainContract(p.getContract().getContractMeta().getSymbol());
			// update prices accordingly
			BarVO currentNmBar = nextMainContract.getCurrentBar();
			if(currentNmBar == null){
				currentNmBar = this.contractHolder.getBarFromGw(nextMainContract);
				nextMainContract.setCurrentBar(currentNmBar);
			}
//			p.setContract(nextMainContract);
			// issue rules to open new position.
			return openNewPosition(p, nextMainContract, currentNmBar);
		}
		List<BarVO> barList = contractHolder.getBarHist(contract, OPEN_BAR_SIZE);
		StrategyPricePointVO spp = StrategyUtils.getPricePoint(barList, p.getLastInDate());
		List<RuleVO> ruleList = new ArrayList<RuleVO>();
		if(!StrategyUtils.isFullPortfolio(portfolio)){
			ruleList.addAll(generateOpenRules(p, spp));
		}
		ruleList.addAll(generateCloseRules(p, spp));
		return ruleList;
	}
	
	private boolean isTime2OpenNew(PositionVO p) {
		if(StringUtils.equals(p.getStatus(), PositionVO.REFRESH)){
			return true;
		}
		return false;
	}
	
	private boolean isTime2CloseOld(PositionVO p) {
		if(StringUtils.equals(p.getStatus(), PositionVO.EXPIRE)){
			return true;
		}
		return false;
	}

	private List<RuleVO> openNewPosition(PositionVO p, ContractVO newContract, BarVO currentNmBar) {
		RuleVO rule = new RuleVO();
		rule.setCondition(ConditionVO.TRUE_CONDITION);
		rule.setContract(newContract);
		CommandVO command = new CommandVO();
		command.setHandPerUnit(p.getHandPerUnit());
		command.setPrice(new BigDecimal(currentNmBar.getClosePrice()));
		command.setPriceStyle(CommandVO.MARKET);
		command.setUnits(p.getUnitCount());
		if(StringUtils.equals(p.getDirection(), PositionVO.LONG)){
			command.setInstruction(CommandVO.OPEN_LONG);
		} else {
			command.setInstruction(CommandVO.OPEN_SHORT);
		}
		rule.setCommand(command);
		return Arrays.asList(new RuleVO[]{rule});
	}

	private List<RuleVO> closeOldPostion(PositionVO p) {
		RuleVO rule = new RuleVO();
		rule.setCondition(ConditionVO.TRUE_CONDITION);
		rule.setContract(p.getContract());
		CommandVO command = new CommandVO();
		command.setHandPerUnit(p.getHandPerUnit());
		if(StringUtils.equals(p.getDirection(), PositionVO.LONG)){
			command.setInstruction(CommandVO.CLOSE_LONG);
		} else {
			command.setInstruction(CommandVO.CLOSE_SHORT);
		}
		BarVO currentBar = contractHolder.getContractByKey(p.getContract().getKey()).getCurrentBar();
		if(currentBar == null){
			currentBar = contractHolder.getBarFromGw(p.getContract());
		}
		command.setPrice(new BigDecimal(currentBar.getClosePrice()));
		command.setPriceStyle(CommandVO.MARKET);
		command.setUnits(p.getUnitCount());
		rule.setCommand(command);
		return Arrays.asList(new RuleVO[]{rule});
	}

	private boolean isPositionOld(PositionVO p) {
		if(StringUtils.isBlank(p.getDirection()) || !StringUtils.equals(p.getStatus(), PositionVO.ACTIVE) 
				|| this.contractHolder.getNextMainContract(p.getContract().getContractMeta().getSymbol()) == null){
			return false;
		}
		SimpleDateFormat sdf = new SimpleDateFormat(BaseConstant.PRID_FORMAT);
		try {
			Date expireDate = sdf.parse(p.getContract().getPrid());
			Calendar c = Calendar.getInstance();
			c.setTime(expireDate);
			c.set(Calendar.DATE, 1);
			c.add(Calendar.DATE, -forceSwitchDate);
			Calendar now = Calendar.getInstance();
			if(c.before(now)){
				return true;
			}
		} catch (ParseException e) {
			logger.error("parse error of prid on contract: "+p.getContract().getKey());
		}
		return false;
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
		float atr = StrategyUtils.getAtr(p);
		if(p.getUnitCount() == 0){
			return ruleList;
		} else if(StringUtils.equals(p.getDirection(), PositionVO.LONG)) {
			double clp = spp.getCloseLongPoint();
			double slp = p.getLastInPrice() - atr * 2;
			if(spp.getPassedBarsSinceLastIn() >= days2Urge){
				slp += atr * 2;
			}
			double stp = slp;
			/*double topSlp = this.getTopStopLossPrice(p);
			if(topSlp > 0){
				stp = Math.max(slp, topSlp);
			}*/
			if(currentBar == null){
				logger.warn("[StrategyImpl]current bar is null of "+p.getContract().getKey());
			} else if(currentBar.getClosePrice() >= atr * 2 + p.getLastInPrice()) {
				if((currentBar.getClosePrice() - p.getLastInPrice()) >= (atr * 2)){
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
			double slp = p.getLastInPrice() + atr * 2;
			if(spp.getPassedBarsSinceLastIn() >= days2Urge){
				slp = slp - atr * 2;
			}
			double stp = slp;
			/*double topSlp = this.getTopStopLossPrice(p);
			if(topSlp > 0){
				stp = Math.min(slp, topSlp);
			}*/
			if(currentBar == null){
				logger.warn("[StrategyImpl]current bar is null of "+p.getContract().getKey());
			} else if(currentBar.getClosePrice() <= p.getLastInPrice() - atr * 2) {
				if((p.getLastInPrice() - currentBar.getClosePrice()) >= (atr * 2)){
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

	private float getTopStopLossPrice(PositionVO p) {
		if(p.getLastInPrice() <= 0.1 || p.getTopPrice() <= 0.1){
			logger.warn("[StrategyImpl] position uncorrect price: "+p.getContract().getKey()+" last in price: "
						+p.getLastInPrice()+"; top price: "+p.getTopPrice());
			return -1;
		}
		float priceGap = Math.abs(p.getTopPrice() - p.getLastInPrice());
		float atr = StrategyUtils.getAtr(p);
		int adjustAtr = (int) (Math.floor(priceGap/(atr*2)));
		if(adjustAtr >= 1){
			if(StringUtils.isBlank(p.getDirection())){
				logger.error("[StrategyImpl] position direction is blank of : " + p.getContract().getKey());
				return -1;
			} else if(StringUtils.equals(p.getDirection(), PositionVO.LONG)){
				return p.getLastInPrice() - 2 * atr + adjustAtr * atr;
			} else {
				return p.getLastInPrice() + 2 * atr - adjustAtr * atr;
			}
		} else {
			return -1;
		}
	}
	
	private List<RuleVO> generateOpenRules(PositionVO p, StrategyPricePointVO spp) {
		List<RuleVO> ruleList = new ArrayList<RuleVO>();
		float atr = StrategyUtils.getAtr(p);
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
				condition.setTriggerValue(new BigDecimal(p.getLastInPrice() + atr));
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
				condition.setTriggerValue(new BigDecimal(p.getLastInPrice() - atr));
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

	//move below logic to command finished function;
	@Override
	public void ruleTriggered(RuleVO rule) {
		rule.setTriggered(true);
		rule.setTriggerTime(new Date());
	}

	private void updatePosition(PositionVO position, PortfolioVO portfolio, RuleVO rule) {
		CommandVO command = rule.getCommand();
		if(StringUtils.equals(position.getStatus(), PositionVO.EXPIRE)){
			//Update position only update the status when force switch
			position.setStatus(PositionVO.REFRESH);
			this.portfolioHolder.saveCurrentStatus();
		} else if(StringUtils.equals(position.getStatus(), PositionVO.REFRESH)) {
			//Update position only update the status when force switch
			ContractVO nmContract = contractHolder.getContractByKey(rule.getContract().getKey());
			ContractVO oldContract = contractHolder.getContractByKey(position.getContract().getKey());
			BarVO nmBar = this.getBarFromContract(nmContract);
			BarVO oldBar = this.getBarFromContract(oldContract);
			float priceGap = nmBar.getClosePrice() - oldBar.getClosePrice();
			position.setAveragePrice(position.getAveragePrice()+priceGap);
			position.setLastInPrice(position.getLastInPrice()+priceGap);
			position.setTopPrice(0);
			position.setStatus(PositionVO.ACTIVE);
			this.doMainSwitch(position, position.getContract(), rule.getContract());
		} else if(StringUtils.isEmpty(position.getDirection())){
			if(CommandVO.OPEN_LONG.equals(command.getInstruction())){
				position.setDirection(PositionVO.LONG);
			} else if(CommandVO.OPEN_SHORT.equals(command.getInstruction())){
				position.setDirection(PositionVO.SHORT);
			}
			position.setUnitCount(command.getUnits());
			position.setHandPerUnit(command.getHandPerUnit());
//			if(command.isDone()){
			position.setLastInPrice(command.getDealPrice().floatValue());
			position.setLastInDate(new Date());
			position.setAtr(position.getContract().getContractMeta().getAtr());
//			} else {
//				position.setLastInPrice(command.getPrice().floatValue());
//			}
			position.setAveragePrice(position.getLastInPrice());
		} else if(inSameDirection(position.getDirection(), command.getInstruction())){
			if(position.getHandPerUnit() == command.getHandPerUnit()){
//				if(command.isDone()){
				position.setLastInPrice(command.getDealPrice().floatValue());
				position.setLastInDate(new Date());
//				} else {
//					position.setLastInPrice(command.getPrice().floatValue());
//				}
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
					position.setTopPrice(0);
					position.setLastInDate(null);
					position.setAtr(null);
				} else {
					logger.error("behaviour not expected during SL, position.unitCount= "+position.getUnitCount());
				}
			} else {
				logger.error("different hand per unit, position.handPerUnit="+position.getHandPerUnit()
					+", command.handPerUnit="+command.getHandPerUnit()); 
			}
		}
	}

	private BarVO getBarFromContract(ContractVO c) {
		if(c.getCurrentBar() == null){
			c.setCurrentBar(this.contractHolder.getBarFromGw(c));
		}
		return c.getCurrentBar();
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
	public void onCommand(String contractKey, CommandVO command) {
		PortfolioVO portfolio = portfolioHolder.getPortfolio();
		ContractVO contract = contractHolder.getContractByKey(contractKey);
		PositionVO position = portfolioHolder.getPositionByContract(contract);
		if(position == null && StringUtils.equals(contract.getStatus(), BaseConstant.NEXT_MAIN)){
			position = portfolioHolder.getPositionByContractMeta(contract.getContractMeta());
		}
		RuleVO rule = new RuleVO();
		rule.setCommand(command);
		rule.setContract(contract);
		this.updatePosition(position, portfolio, rule);
		ruleHolder.clearContractRule(contract.getKey());
		List<RuleVO> rules = this.generateRulesOnContract(position, portfolio);
		for(RuleVO r: rules){
			ruleHolder.addRule(r.getContract().getKey(), r);
		}
		commandLogger.info("command finished: "+command.getInstruction()+" "
				+command.getHandPerUnit()*command.getUnits()+" "+contract.getKey()+" at "+command.getDealPrice());
	}

	@Override
	public void mainSwitch(ContractVO c, ContractVO nmc) {
		PositionVO position = this.portfolioHolder.getPositionByContract(c);
		if(StringUtils.isEmpty(position.getDirection())){
			this.doMainSwitch(position, c, nmc);
		}
	}
	
	private void doMainSwitch(PositionVO position, ContractVO oldC, ContractVO nmc){
		position.setContract(nmc);
		this.portfolioHolder.saveCurrentStatus();
		contractHolder.mainSwitch(oldC, nmc);
	}

	//update the highest/lowest close price daily
	@Override
	public void updateTopPrice() {
		PortfolioVO portfolio = portfolioHolder.getPortfolio();
		Set<PositionVO> positionSet = portfolio.getPositionSet();
		for(PositionVO p: positionSet){
			updatePositionTopPrice(p);
		}
	}
	
	@Override
	public void onPositionChance2Run(PositionVO position){
		updatePositionTopPrice(position);
	}
	
	private void updatePositionTopPrice(PositionVO p){
		if(StringUtils.isNotBlank(p.getDirection())){
			//fresh close equity
			BarVO currentBar =contractHolder.getContractByKey(p.getContract().getKey()).getCurrentBar();
			if(currentBar == null){
				logger.warn("[StrategyImpl] current bar of " + p.getContract().getKey()+" is null, refecthing.");
				currentBar = this.contractHolder.getBarFromGw(p.getContract());
			}
			float closePrice = currentBar.getClosePrice();
			if(closePrice > 0){
				if(p.getTopPrice() <= 0.1){
					p.setTopPrice(closePrice);
				} else if(StringUtils.equals(p.getDirection(), PositionVO.LONG)){
					if(closePrice > p.getTopPrice()){
						p.setTopPrice(closePrice);
					}
				} else {
					if(closePrice < p.getTopPrice()){
						p.setTopPrice(closePrice);
					}
				}
			}
		}
	}

	@Override
	public void onCommandFailed(String contractKey, CommandVO command) {
		List<RuleVO> rules = ruleHolder.getRuleMap().get(contractKey);
		if(CollectionUtils.isNotEmpty(rules)){
			rules.forEach(r -> {
				if(r.isTriggered()){
					r.setTriggered(false);
				}
			});
		} else {
			logger.warn("rule is empty when command failed: {}", contractKey);
		}
	}

	@Override
	public void clearOutstandingCommands(int minuteGap) {
		List<ContractVO> activeContracts = contractHolder.getActiveContractList();
		List<ContractVO> nmContracts = contractHolder.getNextMainContractList();
		for(ContractVO c : activeContracts){
			clearOutstandingCommands(minuteGap, c.getKey());
		}
		for(ContractVO c : nmContracts){
			clearOutstandingCommands(minuteGap, c.getKey());
		}
		
	}

	private void clearOutstandingCommands(int minuteGap, String key) {
		List<RuleVO> rules = ruleHolder.getRuleMap().get(key);
		if(CollectionUtils.isNotEmpty(rules)){
			for(RuleVO r : rules){
				if(r.isTriggered()){
					if((System.currentTimeMillis()-r.getTriggerTime().getTime())/(60*1000) >= minuteGap){
						logger.info("setting rule of {} to old rule, to be cleared soon", key);
						r.setOld(true);
					}
				}
			}
		}
	}

	@Override
	public void startForceSwitch() {
		Set<PositionVO> positions = portfolioHolder.getPortfolio().getPositionSet();
		for(PositionVO p: positions){
			if(this.isPositionOld(p)){
				// update position status.
				// issue rules to close old position.
				p.setStatus(PositionVO.EXPIRE);
				this.ruleHolder.clearContractRule(p.getContract().getKey());
				this.portfolioHolder.saveCurrentStatus();
			}
		}
	}

}
