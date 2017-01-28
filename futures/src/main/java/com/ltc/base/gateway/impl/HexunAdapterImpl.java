package com.ltc.base.gateway.impl;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.ArrayUtils;
import org.joda.time.LocalTime;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.ltc.base.gateway.ContractAdapter;
import com.ltc.base.gateway.vo.HexunBarVO;
import com.ltc.base.helpers.ContractConfig;
import com.ltc.base.helpers.Converter;
import com.ltc.base.manager.TimeManager;
import com.ltc.base.vo.BarVO;
import com.ltc.base.vo.ContractVO;

public class HexunAdapterImpl implements ContractAdapter {
	
	private static final String FORMAT_STR = "yyyyMMddHHmmss";
	private TimeManager timeManager;
	
	public void setTimeManager(TimeManager timeManager) {
		this.timeManager = timeManager;
	}

	private static Logger logger = LoggerFactory.getLogger(HexunAdapterImpl.class);

	@Override
	public BarVO getCurrentBar(ContractVO c) {
		int[] prices = this.getContractInfoArray(c, true);
		BarVO bar = new BarVO();
		Calendar barDate = Calendar.getInstance();
		LocalTime current = LocalTime.fromCalendarFields(barDate);
		if(current.isAfter(this.timeManager.getBarOpenTime())){
			barDate.add(Calendar.DATE, 1);
			bar.setBarDate(barDate.getTime());
		} else {
			bar.setBarDate(barDate.getTime());
		}
		if(prices.length >= 6){
			int priceWeight = prices[4];
			bar.setClosePrice((float)prices[0]/priceWeight);
			bar.setOpenPrice((float)prices[1]/priceWeight);
			bar.setContract(c);
			bar.setHighPrice((float)prices[2]/priceWeight);
			bar.setLowPrice((float)prices[3]/priceWeight);
			bar.setVolume(prices[5]);
			return bar;
		} else {
			return null;
		}
	}

	@Override
	public List<BarVO> getBarHist(ContractVO c, int barSize) {
		List<HexunBarVO> hexunBarList = this.getHXBarHist(c);
		List<BarVO> barList = new LinkedList<BarVO>();
		if(CollectionUtils.isNotEmpty(hexunBarList)){
			for(HexunBarVO hxBar : hexunBarList){
				 BarVO b = Converter.fromHxBar2BarVO(hxBar);
				 b.setContract(c);
				 barList.add(b);
			}
		}
		return barList;
	}
	
	//GET http://webftcn.hermes.hexun.com/shf/kline?code=SHFE3RB1705&start=20170130210000&number=-1000&type=5
	//GET http://webftcn.hermes.hexun.com/shf/kline?code=DCEA1705&start=20170130210000&number=-1000&type=5
	private List<HexunBarVO> getHXBarHist(ContractVO contract){
		BufferedReader br = null;
		try{
			String prefix = ContractConfig.getPrefix(contract.getContractMeta().getSymbol().toUpperCase());
			String urlStr = null;
			Calendar now = Calendar.getInstance();
			now.add(Calendar.WEEK_OF_YEAR, 1);
			SimpleDateFormat sdf = new SimpleDateFormat(FORMAT_STR);
			String startDate = sdf.format(now.getTime());
			if("CFFEX".equals(prefix)){
				urlStr = "http://webcffex.hermes.hexun.com/cffex/kline?code="+prefix+
						contract.getKey()+"&start="+startDate+"&number=-1000&type=5";
			} else {
				urlStr = "http://webftcn.hermes.hexun.com/shf/kline?code="+prefix+
						contract.getKey()+"&start="+startDate+"&number=-1000&type=5";
			}
			URL site = new URL(urlStr);
			URLConnection connection = site.openConnection();
			br = new BufferedReader(new InputStreamReader(connection.getInputStream()));
			String line = br.readLine();
			if(line != null){
				String[] arr = line.substring(line.lastIndexOf("[[[")+1, line.indexOf("]]")).split("],[");
				int priceWeight = Integer.valueOf(line.substring(line.lastIndexOf(",")+1, line.lastIndexOf("]")));
				List<HexunBarVO> hxBarList = new LinkedList<HexunBarVO>();
				for(String content : arr){
					HexunBarVO hxBar = this.convertStr2HXBar(content, priceWeight);
					hxBarList.add(hxBar);
				}
				Collections.reverse(hxBarList);
				removeFirstBarIfNeeded(hxBarList);
				return hxBarList;
				/*if(arr != null && (arr.length==4 || arr.length==5)){
					int[] priceArray = new int[arr.length];
					for(int i = 0 ; i<arr.length ; i++){
						priceArray[i] = Integer.valueOf(arr[i]);
					}
					return priceArray;
				} else {
					return null;
				}*/
			} else {
				return null;
			}
		} catch (Exception e){
			logger.error("return empty bar hist, due to getting bar hist error of "+contract.getKey(), e);
			return null;
		} finally {
			if(br != null){
				try {
					br.close();
				} catch (IOException e) {}
			}
		}
	}
	
	//if first one a future date, remove first one
	//else if currently it's before 15:00, remove first one
	private void removeFirstBarIfNeeded(List<HexunBarVO> hxBarList) {
		if(CollectionUtils.isNotEmpty(hxBarList)){
			HexunBarVO lastBar = hxBarList.get(0);
			Date lastDate = lastBar.getDate();
			Date currentDate = new Date();
			LocalTime currentTime = LocalTime.fromDateFields(currentDate);
			if(lastDate.after(currentDate) || currentTime.isBefore(timeManager.getBarCloseTime())){
				hxBarList.remove(0);
			}
		}
	}

	private HexunBarVO convertStr2HXBar(String content, int priceWeight) throws ParseException {
		HexunBarVO hxBar = new HexunBarVO();
		String[] arr = content.split(",");
		SimpleDateFormat sdf = new SimpleDateFormat(FORMAT_STR);
		if(ArrayUtils.getLength(arr) != HexunBarVO.STR_SIZE){
			logger.error("get wrong he xun bar info: "+content);
			return hxBar;
		} else {
			hxBar.setDate(sdf.parse(arr[0]));
			hxBar.setLastClose((float)Integer.valueOf(arr[1])/priceWeight);
			hxBar.setOpen((float)Integer.valueOf(arr[2])/priceWeight);
			hxBar.setClose((float)Integer.valueOf(arr[3])/priceWeight);
			hxBar.setHigh((float)Integer.valueOf(arr[4])/priceWeight);
			hxBar.setLow((float)Integer.valueOf(arr[5])/priceWeight);
			hxBar.setVolume(Long.valueOf(arr[6]));
			hxBar.setAmount(Long.valueOf(arr[7]));
			return hxBar;
		}
	}

	public static void main(String []args) throws ParseException{
	}
	
	//GET "http://webftcn.hermes.hexun.com/shf/quotelist?code=DCEA1601&column=Price,Open,High,Low,PriceWeight"  ({"Data":[[[3959,3975,3946,1]]]});
	//GET http://webcffex.hermes.hexun.com/cffex/quotelist?code=CFFEXTF1512&column=Price,Open,High,Low,PriceWeight
	public int[] getContractInfoArray(ContractVO contract, boolean withVolumn) {
		BufferedReader br = null;
		try{
			String prefix = ContractConfig.getPrefix(contract.getContractMeta().getSymbol().toUpperCase());
			String urlStr = null;
			if("CFFEX".equals(prefix)){
				urlStr = "http://webcffex.hermes.hexun.com/cffex/quotelist?code="+prefix+
						contract.getKey()+"&column=Price,Open,High,Low,PriceWeight"+(withVolumn ? ",Volume" : "");
			} else {
				urlStr = "http://webftcn.hermes.hexun.com/shf/quotelist?code="+prefix+
						contract.getKey()+"&column=Price,Open,High,Low,PriceWeight"+(withVolumn ? ",Volume" : "");
			}
			URL site = new URL(urlStr);
			URLConnection connection = site.openConnection();
			br = new BufferedReader(new InputStreamReader(connection.getInputStream()));
			String line = br.readLine();
			if(line != null){
				String[] arr = line.substring(line.lastIndexOf("[")+1, line.indexOf("]")).split(",");
				if(arr != null && (arr.length==4 || arr.length==5)){
					int[] priceArray = new int[arr.length];
					for(int i = 0 ; i<arr.length ; i++){
						priceArray[i] = Integer.valueOf(arr[i]);
					}
					return priceArray;
				} else {
					return null;
				}
			} else {
				return null;
			}
		} catch (Exception e){
			logger.error("get contract info error of "+contract.getKey(), e);
			return null;
		} finally {
			if(br != null){
				try {
					br.close();
				} catch (IOException e) {}
			}
		}
	}

}
