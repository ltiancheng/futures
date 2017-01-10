package com.ltc.strategy.tortoise.manager.impl;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.springframework.util.CollectionUtils;

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
		List<ContractVO> untrackedContracts = new ArrayList<ContractVO>();
		Collections.copy(untrackedContracts, contractList);
		List<String> contractKeyList = new ArrayList<String>();
		for(PositionVO p: this.getPortfolio().getPositionList()){
			contractKeyList.add(p.getContract().getKey());
		}
		Iterator<ContractVO> iter = untrackedContracts.iterator();
		while(iter.hasNext()){
			ContractVO c = iter.next();
			if(contractKeyList.contains(c.getKey())){
				iter.remove();
			}
		}
		return untrackedContracts;
	}

	@Override
	public void addPositions(List<ContractVO> untrackedContracts) {
		if(!CollectionUtils.isEmpty(untrackedContracts)){
			for(ContractVO c: untrackedContracts){
				PositionVO p = new PositionVO();
				p.setContract(c);
				p.setStatus(PositionVO.ACTIVE);
				this.getPortfolio().getPositionList().add(p);
			}
		}
	}

	@Override
	public PositionVO getPositionByContract(ContractVO contract) {
		List<PositionVO> positions = this.getPortfolio().getPositionList();
		for(PositionVO p: positions){
			if(StringUtils.equals(p.getContract().getKey(), contract.getKey())){
				return p;
			}
		}
		return null;
	}

}
