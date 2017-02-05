package com.ltc.base.manager.impl;

import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.ArrayUtils;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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

	private static final Logger logger = LoggerFactory.getLogger(ContractHolderImpl.class);
	
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
			logger.debug("[ContractHolderImpl] get fresh active contract list: "
					+Arrays.toString(this.activeContractList.toArray(new ContractVO[0])));
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

	@Override
	public void mainSwitch(ContractVO c, ContractVO nmc) {
		this.contractService.mainSwitch(c, nmc);
		Calendar cal = Calendar.getInstance();
		if(this.activeContractRefreshTime != null){
			cal.setTime(this.activeContractRefreshTime);
			cal.add(Calendar.WEEK_OF_MONTH, -1);
			this.activeContractRefreshTime = cal.getTime();
		}
		cal = Calendar.getInstance();
		if(this.barHistRefreshTime != null){
			cal.setTime(this.barHistRefreshTime);
			cal.add(Calendar.WEEK_OF_MONTH, -1);
			this.barHistRefreshTime = cal.getTime();
		}
	}
	
	
}
