package com.ltc.strategy.tortoise.service.impl;

import com.ltc.strategy.tortoise.dao.PortfolioDAO;
import com.ltc.strategy.tortoise.service.PortfolioService;
import com.ltc.strategy.tortoise.vo.PortfolioVO;

public class PortfolioServiceImpl implements PortfolioService {
	
	private PortfolioDAO portfolioDAO;
	
	public void setPortfolioDAO(PortfolioDAO portfolioDAO) {
		this.portfolioDAO = portfolioDAO;
	}

	@Override
	public PortfolioVO getPortfolio() {
		return this.portfolioDAO.getPortfolio();
	}

	@Override
	public void savePortfolio(PortfolioVO portfolio) {
		this.portfolioDAO.save(portfolio);
	}

}
