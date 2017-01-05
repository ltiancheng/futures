package com.ltc.base.utils;

import java.util.List;

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
		while(true){
			try {
				timeManager.waitTillNextRound();
				List<ContractVO> contractList = contractHolder.getActiveContractList();
				if(!CollectionUtils.isEmpty(contractList)){
					for(ContractVO c : contractList){
						//monitor contract and update contract bar info.
						this.marketAdapterManager.updateCurrentBarInfo(c);
					}
				} else {
					logger.error("contract list is empty!");
				}
			} catch (Exception e) {
				logger.error("error caught", e);
			}
		}
	}

}
