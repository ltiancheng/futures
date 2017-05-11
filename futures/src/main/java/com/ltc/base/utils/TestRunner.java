package com.ltc.base.utils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.ltc.base.manager.Strategy;


public class TestRunner extends BaseStartupItem implements Runnable {
	
	private static Logger logger = LoggerFactory.getLogger(TestRunner.class);
	
	private Strategy strategy;
	

	public void setStrategy(Strategy strategy) {
		this.strategy = strategy;
	}

	@Override
	public void execute() {
		new Thread(this).start();
	}

	//1. do one time checking every week(Wednesday) after close;
	//2. run once every working day after close. detail time to be set.
	@Override
	public void run() {
		logger.info("[TestRunner] started");
		try {
			Thread.sleep(20000);
		} catch (InterruptedException e) {
			logger.error("sleep interrupted!", e);
		}
		doMainForceSwitch();
	}

	private void doMainForceSwitch() {
		strategy.startForceSwitch();
	}

}
