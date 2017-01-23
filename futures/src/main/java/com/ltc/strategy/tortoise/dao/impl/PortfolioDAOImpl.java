package com.ltc.strategy.tortoise.dao.impl;

import org.springframework.orm.hibernate3.support.HibernateDaoSupport;

import com.ltc.strategy.tortoise.dao.PortfolioDAO;
import com.ltc.strategy.tortoise.vo.PortfolioVO;

public class PortfolioDAOImpl extends HibernateDaoSupport implements PortfolioDAO {

	@Override
	public PortfolioVO getPortfolio() {
		return (PortfolioVO) this.getSession()
				.createCriteria(PortfolioVO.class).uniqueResult();
	}

	@Override
	public void save(PortfolioVO portfolio) {
		this.getSession().save(portfolio);
	}

}
