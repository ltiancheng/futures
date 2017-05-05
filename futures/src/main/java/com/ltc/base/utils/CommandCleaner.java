package com.ltc.base.utils;

import java.util.List;

import org.joda.time.LocalTime;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.ltc.base.manager.Strategy;
import com.ltc.base.manager.TimeManager;


public class CommandCleaner extends BaseStartupItem implements Runnable {
	
	private static Logger logger = LoggerFactory.getLogger(CommandCleaner.class);
	private static int MINUTE_GAP = 3;
	
	private TimeManager timeManager;
	private List<LocalTime> runTimes;
	private Strategy strategy;

	public void setRunTimes(List<LocalTime> runTimes) {
		this.runTimes = runTimes;
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

	//run to clear the out standing commands for each portfolio;
	@Override
	public void run() {
		logger.info("[CommandCleaner] started");
		while(true){
			try {
				timeManager.waitTillNextWorkingDay(runTimes);
				strategy.clearOutstandingCommands(MINUTE_GAP);
			} catch (InterruptedException e) {
				logger.error("sleep interrupted!", e);
			}
		}
	}

}
