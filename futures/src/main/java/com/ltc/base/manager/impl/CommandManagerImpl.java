package com.ltc.base.manager.impl;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.ltc.base.gateway.CommandAdapter;
import com.ltc.base.manager.CommandManager;
import com.ltc.base.manager.Strategy;
import com.ltc.base.vo.CommandVO;
import com.ltc.base.vo.ContractVO;

public class CommandManagerImpl implements CommandManager {

	private static final int DEFAULT_THREAD_COUNT = 20;
	private static Logger logger = LoggerFactory.getLogger(CommandManagerImpl.class);
	
	private ExecutorService threadPool;
	private CommandAdapter commandAdapter;
	
	public void setCommandAdapter(CommandAdapter commandAdapter) {
		this.commandAdapter = commandAdapter;
	}

	protected ExecutorService getThreadPool(){
		if(this.threadPool == null){
			threadPool = Executors.newFixedThreadPool(DEFAULT_THREAD_COUNT);
		}
		return threadPool;
	}
	
	@Override
	public void executeCommand(final ContractVO contract, final CommandVO command, final Strategy callbackStrategy) {
		this.getThreadPool().execute(new Runnable(){

			@Override
			public void run() {
				try{
					commandAdapter.executeCommand(contract, command);
					callbackStrategy.onCommand(contract, command);
				} catch (Exception e){
					logger.error(e.getMessage(), e);
				}
			}
			
		});
	}

}
