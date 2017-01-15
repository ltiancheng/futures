package com.ltc.base.manager.impl;

import java.util.Date;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.springframework.util.CollectionUtils;

import com.ltc.base.gateway.ContractAdapter;
import com.ltc.base.manager.ContractHolder;
import com.ltc.base.manager.TimeManager;
import com.ltc.base.service.ContractService;
import com.ltc.base.vo.BarVO;
import com.ltc.base.vo.ContractVO;

public class ContractHolderImpl implements ContractHolder {
	
	private ContractService contractService;
	private TimeManager timeManager;
	private List<ContractVO> activeContractList;
	private Date activeContractRefreshTime;
	private ContractAdapter contractAdapter;

	public void setContractAdapter(ContractAdapter contractAdapter) {
		this.contractAdapter = contractAdapter;
	}

	public void setTimeManager(TimeManager timeManager) {
		this.timeManager = timeManager;
	}

	public void setContractService(ContractService contractService) {
		this.contractService = contractService;
	}

	@Override
	public List<ContractVO> getActiveContractList() {
		if(CollectionUtils.isEmpty(activeContractList) || needRefreshActiveContract()){
			this.activeContractList = contractService.getActiveContractList();
			this.activeContractRefreshTime = new Date();
			return this.activeContractList;
		} else {
			return this.activeContractList;
		}
	}

	private boolean needRefreshActiveContract() {
		return this.timeManager.needRefreshBeforeOpen(this.activeContractRefreshTime);
	}

	@Override
	public ContractVO getContractByKey(String contractKey) {
		List<ContractVO> contracts = this.getActiveContractList();
		for(ContractVO c: contracts){
			if(StringUtils.equals(c.getKey(), contractKey)){
				return c;
			}
		}
		return null;
	}

	/**
	 * return bar list, the first one index(0) is the latest bar
	 */
	@Override
	public List<BarVO> getBarHist(String key, int barSize) {
		return this.contractAdapter.getBarHist(key, barSize);
	}
}
