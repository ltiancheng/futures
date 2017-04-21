package com.ltc.base.manager;

import java.util.Date;
import java.util.List;

import org.joda.time.LocalTime;

public interface TimeManager {

	void waitTillNextRound() throws InterruptedException;

	boolean needRefreshBeforeOpen(Date lastRefreshTime);

	int getSleepMillis();
	
	LocalTime getBarOpenTime();
	
	LocalTime getBarCloseTime();

	void waitTillNextWorkingDay(LocalTime runTime) throws InterruptedException;

	void waitTillNextWorkingDay(List<LocalTime> runTimes) throws InterruptedException;

}
