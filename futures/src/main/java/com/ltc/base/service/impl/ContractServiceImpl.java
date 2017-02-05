package com.ltc.base.service.impl;

import java.util.List;

import com.ltc.base.dao.ContractDAO;
import com.ltc.base.helpers.BaseConstant;
import com.ltc.base.service.ContractService;
import com.ltc.base.vo.ContractMetaVO;
import com.ltc.base.vo.ContractVO;

public class ContractServiceImpl implements ContractService {
	
	private ContractDAO contractDAO;

	public void setContractDAO(ContractDAO contractDAO) {
		this.contractDAO = contractDAO;
	}

	@Override
	public List<ContractVO> getActiveContractList() {
		return contractDAO.getActiveContractList();
	}

	@Override
	public List<ContractMetaVO> getContractMetaList() {
		return contractDAO.getContractMetaList();
	}

	@Override
	public ContractVO getNextMainContract(ContractVO c) {
		return contractDAO.getNextMainContract(c);
	}

	@Override
	public void saveNextMainContract(ContractVO nmc) {
		nmc.setStatus(BaseConstant.NEXT_MAIN);
		contractDAO.saveContract(nmc);
	}

	@Override
	public void mainSwitch(ContractVO c, ContractVO nmc) {
		c.setStatus(BaseConstant.DEACTIVE);
		nmc.setStatus(BaseConstant.ACTIVE);
//		contractDAO.saveContract(c);
//		contractDAO.saveContract(nmc);
		contractDAO.mainSwitch(c, nmc);
	}

}
