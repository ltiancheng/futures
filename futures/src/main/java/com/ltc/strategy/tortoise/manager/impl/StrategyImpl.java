package com.ltc.strategy.tortoise.manager.impl;

import com.ltc.base.manager.Strategy;
import com.ltc.base.vo.CommandVO;
import com.ltc.base.vo.ContractVO;
import com.ltc.base.vo.RuleVO;
import com.ltc.strategy.tortoise.manager.PortfolioHolder;

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
	
	public void setPortfolioHolder(PortfolioHolder portfolioHolder) {
		this.portfolioHolder = portfolioHolder;
	}

	@Override
	public void saveStatus() {
		portfolioHolder.saveCurrentStatus();
	}

	@Override
	public void initRules() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void ruleTriggered(RuleVO rule) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onCommand(ContractVO contract, CommandVO command) {
		// TODO Auto-generated method stub

	}

}
