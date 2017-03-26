package com.ltc.base.manager.impl;

import java.util.Calendar;
import java.util.Date;

import org.joda.time.LocalDate;
import org.joda.time.LocalTime;

import com.ltc.base.manager.TimeManager;

public class TimeManagerImpl implements TimeManager {

//	private List<String> holidays;
	private LocalTime barOpenTime;
	private LocalTime barCloseTime;
	private int sleepMillis;
	
	public LocalTime getBarOpenTime() {
		return barOpenTime;
	}

	public void setBarOpenTime(LocalTime barOpenTime) {
		this.barOpenTime = barOpenTime;
	}

	public LocalTime getBarCloseTime() {
		return barCloseTime;
	}

	public void setBarCloseTime(LocalTime barCloseTime) {
		this.barCloseTime = barCloseTime;
	}

	public int getSleepMillis() {
		return sleepMillis;
	}

	public void setSleepMillis(int sleepMillis) {
		this.sleepMillis = sleepMillis;
	}

	//this is for monitor task.
	//if it's opened time, then wait (0.5) seconds.
	//if it's closed, then wait till next open time;
	@Override
	public void waitTillNextRound() throws InterruptedException {
		Calendar now = Calendar.getInstance();
		int dayOfWeek = now.get(Calendar.DAY_OF_WEEK);
		if(dayOfWeek == Calendar.SATURDAY || dayOfWeek == Calendar.SUNDAY){
			waitTillNextMonday(now, dayOfWeek);
		} else {
			LocalTime lt = LocalTime.fromCalendarFields(now);
			if(lt.isBefore(barOpenTime) && lt.isAfter(barCloseTime)){
				// market closed;
				long millis = Math.abs(barOpenTime.minusMinutes(1).getMillisOfDay() - lt.getMillisOfDay());
				Thread.sleep(millis);
			} else {
				// market opening;
				Thread.sleep(this.sleepMillis);
			}
		}
	}

	private void waitTillNextMonday(Calendar now, int dayOfWeek) throws InterruptedException {
		Calendar monday = Calendar.getInstance();
		if(dayOfWeek == Calendar.SATURDAY){
			monday.add(Calendar.DATE, 2);
		} else if(dayOfWeek == Calendar.SUNDAY){
			monday.add(Calendar.DATE, 1);
		}
		monday.set(Calendar.HOUR, 0);
		monday.set(Calendar.MINUTE, 0);
		monday.set(Calendar.SECOND, 0);
		long millis = monday.getTimeInMillis() - now.getTimeInMillis();
		if(millis > 0){
			Thread.sleep(millis);
		}
	}

	@Override
	public boolean needRefreshBeforeOpen(Date lastRefreshTime) {
		Calendar now = Calendar.getInstance();
		int dayOfWeek = now.get(Calendar.DAY_OF_WEEK);
		if(dayOfWeek == Calendar.SATURDAY || dayOfWeek == Calendar.SUNDAY){
			return false;
		}
		LocalTime lt = LocalTime.fromCalendarFields(now);
		if(lt.isBefore(this.barOpenTime) && lt.isAfter(this.barCloseTime)){
			//market is closed;
			if(lastRefreshTime != null && lastRefreshTime.after(new LocalDate().toDate())){
				return false;
			} else {
				return true;
			}
		} else {
			return false;
		}
	}

	//wait on next working day till the time input.
	@Override
	public void waitTillNextWorkingDay(LocalTime runTime) throws InterruptedException {
		Calendar currentDate = Calendar.getInstance();
		Calendar nextRun = Calendar.getInstance();
		nextRun.set(Calendar.HOUR_OF_DAY, runTime.getHourOfDay());
		nextRun.set(Calendar.MINUTE, runTime.getMinuteOfHour());
		nextRun.set(Calendar.SECOND, runTime.getSecondOfMinute());
		
		LocalTime now = LocalTime.fromCalendarFields(currentDate);
		int weekDay = currentDate.get(Calendar.DAY_OF_WEEK);
		if(runTime.isBefore(now)){
			if(weekDay == Calendar.FRIDAY){
				nextRun.add(Calendar.DATE, 3);
			} else if(weekDay == Calendar.SATURDAY) {
				nextRun.add(Calendar.DATE, 2);
			} else {
				nextRun.add(Calendar.DATE, 1);
			}
		} else {
			if(weekDay == Calendar.SATURDAY){
				nextRun.add(Calendar.DATE, 2);
			} else if(weekDay == Calendar.SUNDAY){
				nextRun.add(Calendar.DATE, 1);
			}
		}
		long millis = nextRun.getTimeInMillis() - currentDate.getTimeInMillis();
		Thread.sleep(millis);
	}
	
}
