package com.ltc.base.manager.impl;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.MessageListener;
import javax.jms.TextMessage;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.ltc.base.gateway.ContractAdapter;
import com.ltc.base.gateway.ctp.CtpManager;
import com.ltc.base.gateway.ctp.vo.CThostFtdcDepthMarketDataField;
import com.ltc.base.helpers.BaseConstant;
import com.ltc.base.helpers.BaseUtils;
import com.ltc.base.manager.ContractHolder;
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
	private CtpManager ctpManager;
	private ContractHolder contractHolder;

	public void setContractHolder(ContractHolder contractHolder) {
		this.contractHolder = contractHolder;
	}

	public void setCtpManager(CtpManager ctpManager) {
		this.ctpManager = ctpManager;
	}

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
						if(cb != null){
							logger.debug("[MarketAdapterManagerImpl] setting current bar of contract: "+c.getKey());
							c.setCurrentBar(cb);
						}
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

	@Override
	public ContractVO getTopVolContract(ContractVO currentContract) throws ParseException {
		SimpleDateFormat sdf = new SimpleDateFormat(BaseConstant.PRID_FORMAT);
		Date d = sdf.parse(currentContract.getPrid());
		Calendar c = Calendar.getInstance();
		c.setTime(d);
		List<ContractVO> contractList = new ArrayList<ContractVO>();
		ContractVO cc = getContractOnline(currentContract.getContractMeta(), currentContract.getPrid());
		currentContract.setCurrentBar(cc.getCurrentBar());
		for(int i = 0 ; i<=5 ; i++){
			c.add(Calendar.MONTH, 1);
			ContractVO nmc = getContractOnline(currentContract.getContractMeta(), sdf.format(c.getTime()));
			contractList.add(nmc);
		}
		ContractVO topc = cc;
		if(CollectionUtils.isNotEmpty(contractList)){
			for(ContractVO cvo : contractList){
				if(cvo.getCurrentBar() != null && topc.getCurrentBar() != null){
					if(cvo.getCurrentBar().getVolume() > topc.getCurrentBar().getVolume()){
						topc = cvo;
					}
				}
			}
		}
		return topc;
	}

	private ContractVO getContractOnline(com.ltc.base.vo.ContractMetaVO contractMeta, String prid) {
		ContractVO c = new ContractVO();
		c.setContractMeta(contractMeta);
		c.setPrid(prid);
		BarVO bar = contractAdapter.getCurrentBar(c);
		c.setCurrentBar(bar);
		return c;
	}

	@Override
	public void registContracts(List<ContractVO> contractList) {
		this.ctpManager.registContracts(contractList);
	}

	@Override
	public void initContractListener() {
		MessageListener listener = new MessageListener() {
			@Override
			public void onMessage(Message message) {
				if(message instanceof TextMessage){
					try {
						String json = ((TextMessage) message).getText();
						CThostFtdcDepthMarketDataField deepMd = BaseUtils.json2Obj(json, CThostFtdcDepthMarketDataField.class);
						if(deepMd != null && StringUtils.isNotBlank(deepMd.InstrumentID)){
							BarVO bar = deepMd.toBar();
							ContractVO contract = contractHolder.getContractByKey(deepMd.InstrumentID.toUpperCase());
							bar.setContract(contract);
							contract.setCurrentBar(bar);
						}
					} catch (JMSException e) {
						logger.error(e.getMessage(), e);
					}
					
				}
			}
		};
		this.ctpManager.registerMarketListener(listener);
	}
	
}
