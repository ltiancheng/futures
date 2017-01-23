package com.ltc.strategy.tortoise.dao;

import com.ltc.strategy.tortoise.vo.PortfolioVO;

public interface PortfolioDAO {

	PortfolioVO getPortfolio();

	void save(PortfolioVO portfolio);

}
