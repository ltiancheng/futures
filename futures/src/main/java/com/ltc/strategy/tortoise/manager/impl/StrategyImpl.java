package com.ltc.strategy.tortoise.manager.impl;

import com.ltc.base.manager.Strategy;
import com.ltc.base.vo.CommandVO;
import com.ltc.base.vo.ContractVO;
import com.ltc.base.vo.RuleVO;
import com.ltc.strategy.tortoise.manager.PortfolioHolder;

public class StrategyImpl implements Strategy {

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
