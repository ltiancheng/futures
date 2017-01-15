package com.ltc.strategy.tortoise.utils;

import com.ltc.base.utils.BaseStartupItem;

public class StrategyRunner extends BaseStartupItem implements Runnable {

	@Override
	public void execute() {
		new Thread(this).start();
	}

	@Override
	public void run() {
		//currently seems no need to do anything...
	}

}
