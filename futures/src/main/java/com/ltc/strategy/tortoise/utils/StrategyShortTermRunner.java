package com.ltc.strategy.tortoise.utils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.ltc.base.manager.Strategy;
import com.ltc.base.manager.TimeManager;
import com.ltc.base.utils.BaseStartupItem;

public class StrategyShortTermRunner extends BaseStartupItem implements Runnable {

	private static Logger logger = LoggerFactory.getLogger(StrategyShortTermRunner.class);
	
	private TimeManager timeManager;
	private Strategy strategy;
	private int minutesGap;

	public void setMinutesGap(int minutesGap) {
		this.minutesGap = minutesGap;
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

	@Override
	public void run() {
		logger.info("[StrategyShortTermRunner] started");
		while(true){
			try {
				timeManager.waitTillNextRound();
				Thread.sleep(minutesGap * 60 * 1000);
				this.strategy.updateTopPrice();
			} catch (InterruptedException e) {
				logger.error("sleep interrupted!", e);
			}
		}
	}

}
