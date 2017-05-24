package com.ltc.strategy.tortoise.utils;

import org.joda.time.LocalTime;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.ltc.base.manager.Strategy;
import com.ltc.base.manager.TimeManager;
import com.ltc.base.utils.BaseStartupItem;

public class StrategyRunner extends BaseStartupItem implements Runnable {

	private static Logger logger = LoggerFactory.getLogger(StrategyRunner.class);
	
	private TimeManager timeManager;
	private LocalTime runTime;
	private Strategy strategy;

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

	@Override
	public void run() {
		logger.info("[StrategyRunner] started");
		while(true){
			try {
				timeManager.waitTillNextWorkingDay(this.runTime);
			} catch (InterruptedException e) {
				logger.error("sleep interrupted!", e);
			}
			this.strategy.refreshStopLossEquity();
			this.strategy.updateTopPrice();
			this.strategy.updateAtr();
			this.strategy.saveStatus();
		}
	}

}
