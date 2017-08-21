package com.ltc.base.manager.impl;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.joda.time.LocalTime;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.CollectionUtils;

import com.ltc.base.gateway.ContractAdapter;
import com.ltc.base.manager.ContractHolder;
import com.ltc.base.manager.TimeManager;
import com.ltc.base.service.ContractService;
import com.ltc.base.vo.BarVO;
import com.ltc.base.vo.ContractMetaVO;
import com.ltc.base.vo.ContractVO;

public class ContractHolderImpl implements ContractHolder {
	
	private static final int DEFAULT_BAR_SIZE = 20;
	private ContractService contractService;
	private TimeManager timeManager;
	private List<ContractVO> activeContractList;
	private List<ContractVO> nextMainContractList = new ArrayList<ContractVO>();
	private Date contractRefreshTime;
	private ContractAdapter contractAdapter;
	private Map<String, List<BarVO>> barHistMap = new HashMap<String, List<BarVO>>();
	private Date barHistRefreshTime;
	private List<LocalTime[]> barHistRefreshInterval;
	private static ContractHolderImpl instance;
	private Map<String, Integer> contractCodePriorityMap;
	
	public List<LocalTime[]> getBarHistRefreshInterval() {
		return barHistRefreshInterval;
	}

	public void setBarHistRefreshInterval(List<LocalTime[]> barHistRefreshInterval) {
		this.barHistRefreshInterval = barHistRefreshInterval;
	}

	public ContractHolderImpl(){
		instance = this;
	}
	
	public static ContractHolder getInstance(){
		return instance;
	}

	public void setNextMainContractList(List<ContractVO> nextMainContractList) {
		this.nextMainContractList = nextMainContractList;
	}

	public Date getBarHistRefreshTime() {
		return barHistRefreshTime;
	}

	public void setBarHistRefreshTime(Date barHistRefreshTime) {
		this.barHistRefreshTime = barHistRefreshTime;
	}
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
		this.refreshContractList();
		return this.activeContractList;
	}
	
	public synchronized void refreshContractList(){
		if(needRefreshContract() || CollectionUtils.isEmpty(this.activeContractList)){
			this.activeContractList = contractService.getActiveContractList();
			this.nextMainContractList = contractService.getNextMainContractList();
			logger.debug("[ContractHolderImpl] get fresh active contract list: "
					+Arrays.toString(this.activeContractList.toArray(new ContractVO[0])));
			logger.debug("[ContractHolderImpl] get fresh next main contract list: "
					+Arrays.toString(this.nextMainContractList.toArray(new ContractVO[0])));
			this.contractRefreshTime = new Date();
		}
	}
	
	@Override
	public List<ContractVO> getNextMainContractList() {
		this.refreshContractList();
		return this.nextMainContractList;
	}

	private boolean needRefreshContract() {
		return this.timeManager.needRefreshBeforeOpen(this.contractRefreshTime);
	}
	
	private boolean needRefreshBarHist() {
		return !this.timeManager.isTimeInIntervals(this.barHistRefreshTime, barHistRefreshInterval);
	}

	@Override
	public ContractVO getContractByKey(String contractKey) {
		List<ContractVO> contracts = this.getActiveContractList();
		for(ContractVO c: contracts){
			if(StringUtils.equals(c.getKey(), contractKey)){
				return c;
			}
		}
		List<ContractVO> nmContracts = this.getNextMainContractList();
		for(ContractVO c: nmContracts){
			if(StringUtils.equals(c.getKey(), contractKey)){
				return c;
			}
		}
		return null;
	}
	
	@Override
	public ContractVO getNextMainContract(String symbol) {
		List<ContractVO> nextMainContracts = this.getNextMainContractList();
		for(ContractVO c : nextMainContracts){
			if(StringUtils.equals(symbol, c.getContractMeta().getSymbol())){
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
	
	@Override
	public BarVO getBarFromGw(ContractVO c){
		return this.contractAdapter.getCurrentBar(c);
	}

	private Map<String, List<BarVO>> getBarHistMap() {
		if(CollectionUtils.isEmpty(this.barHistMap) || needRefreshBarHist()){
			logger.debug("[ContractHolder] refreshing bar hist...");
			List<ContractVO> allContracts = new ArrayList<ContractVO>();
			allContracts.addAll(this.getActiveContractList());
			allContracts.addAll(this.getNextMainContractList());
			for(ContractVO c : allContracts){
				String key = c.getKey();
				List<BarVO> barHist = this.contractAdapter.getBarHist(c, DEFAULT_BAR_SIZE);
				this.barHistMap.put(key, barHist);
				logger.debug("[ContractHolder] getting bar hist of contract: {}, latest bar date is: {}", key, barHist.get(0).getBarDate());
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
		if(this.contractRefreshTime != null){
			cal.setTime(this.contractRefreshTime);
			cal.add(Calendar.WEEK_OF_MONTH, -1);
			this.contractRefreshTime = cal.getTime();
		}
		cal = Calendar.getInstance();
		if(this.barHistRefreshTime != null){
			cal.setTime(this.barHistRefreshTime);
			cal.add(Calendar.WEEK_OF_MONTH, -1);
			this.barHistRefreshTime = cal.getTime();
		}
	}

	@Override
	public void saveContractMeta(ContractMetaVO contractMeta) {
		this.contractService.saveContractMeta(contractMeta);
	}

	@Override
	public Map<String, Integer> getContractCodePriorityMap() {
		if(this.contractCodePriorityMap == null){
			contractCodePriorityMap = new HashMap<String, Integer>();
			List<ContractMetaVO> metas = this.contractService.getContractMetaList();
			for(ContractMetaVO cmv : metas){
				this.contractCodePriorityMap.put(cmv.getSymbol(), cmv.getPriority());
			}
		}
		return this.contractCodePriorityMap;
	}
}
