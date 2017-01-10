package com.ltc.strategy.tortoise.manager.impl;

import java.util.List;

import com.ltc.base.vo.ContractVO;
import com.ltc.strategy.tortoise.manager.PortfolioHolder;
import com.ltc.strategy.tortoise.service.PortfolioService;
import com.ltc.strategy.tortoise.vo.PortfolioVO;
import com.ltc.strategy.tortoise.vo.PositionVO;

public class PortfolioHolderImpl implements PortfolioHolder {

	private PortfolioVO portfolio;
	private PortfolioService portfolioService;
	
	public PortfolioService getPortfolioService() {
		return portfolioService;
	}

	public void setPortfolioService(PortfolioService portfolioService) {
		this.portfolioService = portfolioService;
	}

	@Override
	public void saveCurrentStatus() {
		this.portfolioService.savePortfolio(portfolio);
	}

	@Override
	public PortfolioVO getPortfolio() {
		if(portfolio == null){
			portfolio = portfolioService.getPortfolio();
		}
		return portfolio;
	}

	@Override
	public List<ContractVO> getUntrackedContracts(List<ContractVO> contractList) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void addPositions(List<ContractVO> untrackedContracts) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public PositionVO getPositionByContract(ContractVO contract) {
		// TODO Auto-generated method stub
		return null;
	}

}
