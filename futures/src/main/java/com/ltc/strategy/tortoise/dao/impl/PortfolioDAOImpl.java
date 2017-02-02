package com.ltc.strategy.tortoise.dao.impl;

import org.hibernate.criterion.Restrictions;
import org.springframework.orm.hibernate3.support.HibernateDaoSupport;

import com.ltc.strategy.tortoise.dao.PortfolioDAO;
import com.ltc.strategy.tortoise.vo.PortfolioVO;

public class PortfolioDAOImpl extends HibernateDaoSupport implements PortfolioDAO {
	
	private String portfolioCode;

	public String getPortfolioCode() {
		return portfolioCode;
	}

	public void setPortfolioCode(String portfolioCode) {
		this.portfolioCode = portfolioCode;
	}

	@Override
	public PortfolioVO getPortfolio() {
		return (PortfolioVO) this.getSession()
				.createCriteria(PortfolioVO.class).add(Restrictions.eq("code", portfolioCode)).uniqueResult();
	}

	@Override
	public void save(PortfolioVO portfolio) {
		this.getSession().saveOrUpdate(portfolio);
	}

}
