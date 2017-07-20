package com.ltc.base.utils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.ltc.base.manager.Strategy;
import com.ltc.base.manager.TimeManager;


public class FuturePersistance extends BaseStartupItem implements Runnable {

	private static Logger logger = LoggerFactory.getLogger(FuturePersistance.class);
	
	private TimeManager timeManager;
	private Strategy strategy;
	private int gapMinutes;
	
	public void setGapMinutes(int gapMinutes) {
		this.gapMinutes = gapMinutes;
	}

	public void setStrategy(Strategy strategy) {
		this.strategy = strategy;
	}

	public void setTimeManager(TimeManager timeManager) {
		this.timeManager = timeManager;
	}

	@Override
	public void execute() {
		new Thread(this).start();
	}

	//save the strategy status every {3} minutes;
	@Override
	public void run() {
		logger.info("[FuturePersistance] started");
		while(true){
			try {
				timeManager.waitTillNextRound();
				Thread.sleep(this.gapMinutes*60*1000-timeManager.getSleepMillis());
				this.strategy.saveStatus();
			} catch (Exception e) {
				logger.error("error caught", e);
			}
		}
	}

}
