package com.ltc.base.utils;

import java.text.ParseException;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.joda.time.LocalTime;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.ltc.base.manager.ContractHolder;
import com.ltc.base.manager.MarketAdapterManager;
import com.ltc.base.manager.Strategy;
import com.ltc.base.manager.TimeManager;
import com.ltc.base.service.ContractService;
import com.ltc.base.vo.ContractVO;


public class MainSwitcher extends BaseStartupItem implements Runnable {
	
	private static Logger logger = LoggerFactory.getLogger(MainSwitcher.class);
	
	private TimeManager timeManager;
	private LocalTime runTime;
	private Strategy strategy;
	private ContractHolder contractHolder;
	private ContractService contractService;
	private MarketAdapterManager marketAdapterManager;
	private float volThreshold;
	
	public float getVolThreshold() {
		return volThreshold;
	}

	public void setVolThreshold(float volThreshold) {
		this.volThreshold = volThreshold;
	}

	public void setContractService(ContractService contractService) {
		this.contractService = contractService;
	}

	public void setMarketAdapterManager(MarketAdapterManager marketAdapterManager) {
		this.marketAdapterManager = marketAdapterManager;
	}

	public void setContractHolder(ContractHolder contractHolder) {
		this.contractHolder = contractHolder;
	}

	public void setStrategy(Strategy strategy) {
		this.strategy = strategy;
	}

	public void setTimeManager(TimeManager timeManager) {
		this.timeManager = timeManager;
	}
	
	public void setRunTime(LocalTime runTime){
		this.runTime = runTime;
	}

	@Override
	public void execute() {
		new Thread(this).start();
	}

	//1. do one time checking every week(Wednesday) after close;
	//2. run once every working day after close. detail time to be set.
	@Override
	public void run() {
		logger.info("[MainSwitcher] started");
		while(true){
			try {
				timeManager.waitTillNextWorkingDay(this.runTime);
			} catch (InterruptedException e) {
				logger.error("sleep interrupted!", e);
			}
			doMainScan();
			doMainSwitch();
			doMainForceSwitch();
		}
	}

	private void doMainForceSwitch() {
		strategy.startForceSwitch();
	}

	private void doMainSwitch() {
		List<ContractVO> contractList = this.contractHolder.getActiveContractList();
		for(ContractVO c: contractList){
			ContractVO nmc = this.contractService.getNextMainContract(c);
			if(nmc != null){
				strategy.mainSwitch(c, nmc);
			}
		}
	}

	private void doMainScan() {
		List<ContractVO> contractList = this.contractHolder.getActiveContractList();
		for(ContractVO c: contractList){
			ContractVO nmc = this.contractService.getNextMainContract(c);
			if(nmc == null){
				try {
					nmc = this.marketAdapterManager.getTopVolContract(c);
				} catch (ParseException e) {
					logger.error("[MainSwitcher] error parse of " + c.getKey(), e);
					continue;
				}
				nmc.setContractMeta(c.getContractMeta());
				if(!StringUtils.equals(nmc.getKey(), c.getKey()) && nmc.getCurrentBar()!= null && c.getCurrentBar()!= null){
					if(nmc.getCurrentBar().getVolume() > 0 && c.getCurrentBar().getVolume() > 0 &&
						(double)nmc.getCurrentBar().getVolume()/c.getCurrentBar().getVolume() >= volThreshold){
						this.contractService.saveNextMainContract(nmc);
					}
				}
			}
		}
	}

}
