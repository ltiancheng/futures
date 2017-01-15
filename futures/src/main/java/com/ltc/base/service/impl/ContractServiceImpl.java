package com.ltc.base.service.impl;

import java.util.List;

import com.ltc.base.dao.ContractDAO;
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

}
