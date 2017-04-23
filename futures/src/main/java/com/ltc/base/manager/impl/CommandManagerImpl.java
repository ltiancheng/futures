package com.ltc.base.manager.impl;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.MessageListener;
import javax.jms.TextMessage;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.ltc.base.gateway.CommandAdapter;
import com.ltc.base.manager.CommandManager;
import com.ltc.base.manager.Strategy;
import com.ltc.base.vo.CommandVO;
import com.ltc.base.vo.ContractVO;
import com.ltc.base.vo.FullCommandVO;

public class CommandManagerImpl implements CommandManager {

	private static final int DEFAULT_THREAD_COUNT = 20;
	private static Logger logger = LoggerFactory.getLogger(CommandManagerImpl.class);
	
	private ExecutorService threadPool;
	private CommandAdapter commandAdapter;
	private Strategy strategy;
	
	public void setStrategy(Strategy strategy) {
		this.strategy = strategy;
	}

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
	public void initCommandListener(){
		MessageListener succCommandListener = new MessageListener(){

			@Override
			public void onMessage(Message msg) {
				if(msg instanceof TextMessage){
					String textStr;
					try {
						textStr = ((TextMessage) msg).getText();
						FullCommandVO fullCmd= commandAdapter.parseToFullCommand(textStr);
						if(fullCmd != null && StringUtils.isNotBlank(fullCmd.getContractKey())&& fullCmd.getCommand() != null){
							strategy.onCommand(fullCmd.getContractKey(), fullCmd.getCommand());
						}
					} catch (JMSException e) {
						logger.error(e.getMessage(), e);
					}
				}
			}
			
		};
		MessageListener errCommandListener = new MessageListener(){

			@Override
			public void onMessage(Message msg) {
				if(msg instanceof TextMessage){
					String textStr;
					try {
						textStr = ((TextMessage) msg).getText();
						logger.warn("[CommandManagerImpl] command failed: {}", textStr);
						FullCommandVO fullCmd= commandAdapter.parseToFailedFullCommand(textStr);
						if(fullCmd != null && StringUtils.isNotBlank(fullCmd.getContractKey())){
							strategy.onCommandFailed(fullCmd.getContractKey(), fullCmd.getCommand());
						}
					} catch (JMSException e) {
						logger.error(e.getMessage(), e);
					}
				}
			}
			
		};
		commandAdapter.initCommandListener(succCommandListener, errCommandListener);
	}
	
	//send command through queue.
	@Override
	public void executeCommand(final ContractVO contract, final CommandVO command, final Strategy callbackStrategy) {
		this.getThreadPool().execute(new Runnable(){

			@Override
			public void run() {
				try{
					commandAdapter.executeCommand(contract, command);
//					callbackStrategy.onCommand(contract, command);
				} catch (Exception e){
					logger.error(e.getMessage(), e);
				}
			}
			
		});
	}

}
