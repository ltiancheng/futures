package com.ltc.base.utils;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class StartupManager {

	private static final Logger logger = LoggerFactory.getLogger(StartupManager.class);
	
	private List<StartupItem> startupItems = new ArrayList<StartupItem>();
	
	/**
	 * to execute the startup items 
	 */
	public void run() {
		Iterator<StartupItem> it = startupItems.iterator();
		while (it.hasNext()) {
			StartupItem si = null;
			
			try {
				si = it.next();
				si.execute();
				
			} catch (Exception e) {
				logger.error("Error while executing startupItem [" + si + "]", e);
			}
		}
		
		startupItems = null;
	}

	public void setStartupItems(List<StartupItem> startupItems) {
		this.startupItems = startupItems;
	}
	

}
