package com.ltc.base.manager.impl;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.springframework.util.CollectionUtils;

import com.ltc.base.gateway.ContractAdapter;
import com.ltc.base.manager.ContractHolder;
import com.ltc.base.manager.TimeManager;
import com.ltc.base.service.ContractService;
import com.ltc.base.vo.BarVO;
import com.ltc.base.vo.ContractVO;

public class ContractHolderImpl implements ContractHolder {
	
	private static final int DEFAULT_BAR_SIZE = 20;
	private ContractService contractService;
	private TimeManager timeManager;
	private List<ContractVO> activeContractList;
	private Date activeContractRefreshTime;
	private ContractAdapter contractAdapter;
	private Map<String, List<BarVO>> barHistMap = new HashMap<String, List<BarVO>>();
	private Date barHistRefreshTime;

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
	
	private boolean needRefreshBarHist() {
		return this.timeManager.needRefreshBeforeOpen(this.barHistRefreshTime);
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
	public List<BarVO> getBarHist(ContractVO c, int barSize) {
		Map<String, List<BarVO>> barHists = this.getBarHistMap();
		List<BarVO> barList = barHists.get(c.getKey());
		if(CollectionUtils.isEmpty(barList)){
			barList = this.contractAdapter.getBarHist(c, barSize);
			barHists.put(c.getKey(), barList);
		}
		return barList;
	}

	private Map<String, List<BarVO>> getBarHistMap() {
		if(CollectionUtils.isEmpty(this.barHistMap) || needRefreshBarHist()){
			List<ContractVO> contractList = this.getActiveContractList();
			for(ContractVO c : contractList){
				String key = c.getKey();
				List<BarVO> barHist = this.contractAdapter.getBarHist(c, DEFAULT_BAR_SIZE);
				this.barHistMap.put(key, barHist);
			}
			this.barHistRefreshTime = new Date();
			return this.barHistMap;
		} else {
			return this.barHistMap;
		}
	}
	
	
}
