package com.ltc.strategy.tortoise.manager.impl;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import org.apache.commons.lang.StringUtils;
import org.springframework.util.CollectionUtils;

import com.ltc.base.vo.ContractMetaVO;
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
		List<ContractVO> untrackedContracts = new ArrayList<ContractVO>(contractList.size());
		for(ContractVO c: contractList){
			untrackedContracts.add(c);
		}
		List<String> contractKeyList = new ArrayList<String>();
		for(PositionVO p: this.getPortfolio().getPositionSet()){
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
				p.setPortfolio(this.getPortfolio());
				this.getPortfolio().getPositionSet().add(p);
			}
		}
	}

	@Override
	public PositionVO getPositionByContract(ContractVO contract) {
		Set<PositionVO> positions = this.getPortfolio().getPositionSet();
		for(PositionVO p: positions){
			if(StringUtils.equals(p.getContract().getKey(), contract.getKey())){
				return p;
			}
		}
		return null;
	}

	@Override
	public PositionVO getPositionByContractMeta(ContractMetaVO contractMeta) {
		Set<PositionVO> positions = this.getPortfolio().getPositionSet();
		for(PositionVO p: positions){
			if(StringUtils.equals(p.getContract().getContractMeta().getSymbol(), contractMeta.getSymbol())){
				return p;
			}
		}
		return null;
	}

}
