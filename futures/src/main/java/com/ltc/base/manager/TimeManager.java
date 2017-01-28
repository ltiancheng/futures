package com.ltc.base.manager;

import java.util.Date;

import org.joda.time.LocalTime;

public interface TimeManager {

	void waitTillNextRound() throws InterruptedException;

	boolean needRefreshBeforeOpen(Date lastRefreshTime);

	int getSleepMillis();
	
	LocalTime getBarOpenTime();
	
	LocalTime getBarCloseTime();

}
