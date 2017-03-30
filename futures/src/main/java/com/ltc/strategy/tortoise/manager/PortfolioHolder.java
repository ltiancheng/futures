package com.ltc.strategy.tortoise.manager;

import java.util.List;

import com.ltc.base.vo.ContractMetaVO;
import com.ltc.base.vo.ContractVO;
import com.ltc.strategy.tortoise.vo.PortfolioVO;
import com.ltc.strategy.tortoise.vo.PositionVO;

public interface PortfolioHolder {

	void saveCurrentStatus();

	PortfolioVO getPortfolio();

	List<ContractVO> getUntrackedContracts(List<ContractVO> contractList);

	void addPositions(List<ContractVO> untrackedContracts);

	PositionVO getPositionByContract(ContractVO contract);

	PositionVO getPositionByContractMeta(ContractMetaVO contractMeta);

}
