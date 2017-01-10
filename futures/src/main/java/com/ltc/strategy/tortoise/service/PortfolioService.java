package com.ltc.strategy.tortoise.service;

import com.ltc.strategy.tortoise.vo.PortfolioVO;

public interface PortfolioService {

	PortfolioVO getPortfolio();

	void savePortfolio(PortfolioVO portfolio);

}
