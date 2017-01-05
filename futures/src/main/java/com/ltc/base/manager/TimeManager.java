package com.ltc.base.manager;

import java.util.Date;

public interface TimeManager {

	void waitTillNextRound() throws InterruptedException;

	boolean needRefreshBeforeOpen(Date lastRefreshTime);

	int getSleepMillis();

}
