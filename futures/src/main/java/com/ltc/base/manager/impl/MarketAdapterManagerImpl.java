package com.ltc.base.manager.impl;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.ltc.base.gateway.ContractAdapter;
import com.ltc.base.manager.MarketAdapterManager;
import com.ltc.base.vo.BarVO;
import com.ltc.base.vo.ContractVO;

/**
 * this one is used to update current bar info;
 * @author Guilin
 *
 */
public class MarketAdapterManagerImpl implements MarketAdapterManager {

	private static final int DEFAULT_THREAD_COUNT = 20;
	private static Logger logger = LoggerFactory.getLogger(MarketAdapterManagerImpl.class);
	
	private ExecutorService threadPool;
	private int threadCount;
	private ContractAdapter contractAdapter;
	private Map<String, Lock> contractLockMap;
	
	private Lock getLock(String key){
		if(contractLockMap == null){
			contractLockMap = new HashMap<String, Lock>();
		}
		if(!this.contractLockMap.containsKey(key)){
			Lock l = new ReentrantLock();
			this.contractLockMap.put(key, l);
		}
		return this.contractLockMap.get(key);
	}
	
	public ContractAdapter getContractAdapter() {
		return contractAdapter;
	}

	public void setContractAdapter(ContractAdapter contractAdapter) {
		this.contractAdapter = contractAdapter;
	}

	public void setThreadCount(int threadCount) {
		this.threadCount = threadCount;
	}
	
	protected ExecutorService getThreadPool(){
		if(this.threadPool == null){
			if(this.threadCount == 0){
				this.threadCount = DEFAULT_THREAD_COUNT;
			}
			threadPool = Executors.newFixedThreadPool(this.threadCount);
		}
		return threadPool;
	}

	@Override
	public void updateCurrentBarInfo(final ContractVO c) {
		if(c == null || StringUtils.isBlank(c.getKey())){
			return;
		}
		final Lock lock = this.getLock(c.getKey());
		final ContractAdapter adapter = this.getContractAdapter();
		this.getThreadPool().execute(new Runnable(){
			@Override
			public void run() {
				if(lock.tryLock()){
					try{
						BarVO cb = adapter.getCurrentBar(c);
						c.setCurrentBar(cb);
					} catch (Exception e){
						logger.error("error happens while getting bar of contract "+c.getKey(), e);
					} finally {
						lock.unlock();
					}
				} else {
					logger.warn("locker of contract "+c.getKey()+" is used. aborting this thread");
					//lock is used, do nothing;
				}
			}
		});
	}
	
}
