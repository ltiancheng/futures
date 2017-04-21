package com.ltc.base.utils;

import java.util.List;

import org.joda.time.LocalTime;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.CollectionUtils;

import com.ltc.base.manager.ContractHolder;
import com.ltc.base.manager.MarketAdapterManager;
import com.ltc.base.manager.TimeManager;
import com.ltc.base.vo.ContractVO;


public class FutureMonitor extends BaseStartupItem implements Runnable {

	/*private ContractService contractService;
	
	public void setContractService(ContractService contractService) {
		this.contractService = contractService;
	}*/
	
	private static Logger logger = LoggerFactory.getLogger(FutureMonitor.class);
	
	private ContractHolder contractHolder;
	private TimeManager timeManager;
	private MarketAdapterManager marketAdapterManager;
	private List<LocalTime> runTimes;
	
	public void setRunTimes(List<LocalTime> runTimes) {
		this.runTimes = runTimes;
	}

	public void setTimeManager(TimeManager timeManager) {
		this.timeManager = timeManager;
	}

	public void setContractHolder(ContractHolder contractHolder) {
		this.contractHolder = contractHolder;
	}

	public void setMarketAdapterManager(MarketAdapterManager marketAdaptorManager) {
		this.marketAdapterManager = marketAdaptorManager;
	}

	@Override
	public void execute() {
		new Thread(this).start();
	}

//		1. at the start of every bar, pickup the contract list that need to monitor, 
//	 		reset the contract list of the contract holder. -- delegate to contract holder.
//	 	2. during every round (sleep 0.5 seconds), use a thread pool to refresh the current bar list.
	@Override
	public void run() {
		logger.info("[FutureMonitor] started");
		this.marketAdapterManager.initContractListener();
		while(true){
			try {
				List<ContractVO> contractList = contractHolder.getActiveContractList();
				List<ContractVO> nmContracts = contractHolder.getNextMainContractList();
				if(!CollectionUtils.isEmpty(contractList)){
					//regist contract market listener.
					this.marketAdapterManager.registContracts(contractList);
				} else {
					logger.error("contract list is empty!");
				}
				if(!CollectionUtils.isEmpty(nmContracts)){
					this.marketAdapterManager.registContracts(nmContracts);
				}
				timeManager.waitTillNextWorkingDay(runTimes);
			} catch (Exception e) {
				logger.error("error caught", e);
			}
		}
	}

}
