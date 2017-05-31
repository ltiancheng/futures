package com.ltc.strategy.tortoise.utils;

import java.util.Date;
import java.util.List;
import java.util.Set;
import java.util.concurrent.TimeUnit;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.time.DateUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.ltc.base.vo.BarVO;
import com.ltc.strategy.tortoise.manager.impl.StrategyImpl;
import com.ltc.strategy.tortoise.vo.PortfolioVO;
import com.ltc.strategy.tortoise.vo.PositionVO;
import com.ltc.strategy.tortoise.vo.StrategyPricePointVO;

public class StrategyUtils {
	
	private static Logger logger = LoggerFactory.getLogger(StrategyUtils.class);
	private static boolean loggedFullRate = false;

	public static StrategyPricePointVO getPricePoint(List<BarVO> barList, Date lastIn) {
		if(barList.size() < StrategyImpl.OPEN_BAR_SIZE){
			logger.error("wrong bar list size: " + barList.size());
		}
		float openLongPoint = barList.get(0).getHighPrice();
		float openShortPoint = barList.get(0).getLowPrice();
		float closeLongPoint = barList.get(0).getLowPrice();
		float closeShortPoint = barList.get(0).getHighPrice();
		int fetchSize = Math.min(StrategyImpl.OPEN_BAR_SIZE, barList.size());
		for(int i = 0 ; i<fetchSize ; i++){
			if(i < StrategyImpl.CLOSE_BAR_SIZE){
				closeLongPoint = Math.min(closeLongPoint, barList.get(i).getLowPrice());
				closeShortPoint = Math.max(closeShortPoint, barList.get(i).getHighPrice());
			}
			openLongPoint = Math.max(openLongPoint, barList.get(i).getHighPrice());
			openShortPoint = Math.min(openShortPoint, barList.get(i).getLowPrice());
		}
		
		StrategyPricePointVO ssp = new StrategyPricePointVO();
		ssp.setCloseLongPoint(closeLongPoint);
		ssp.setCloseShortPoint(closeShortPoint);
		ssp.setOpenLongPoint(openLongPoint);
		ssp.setOpenShortPoint(openShortPoint);
		ssp.setPassedBarsSinceLastIn(getPassedBars(barList, lastIn));
		return ssp;
	}

	private static int getPassedBars(List<BarVO> barList, Date lastIn) {
		if(CollectionUtils.isEmpty(barList) || lastIn == null){
			return 0;
		}
		int passedBars = barList.size();
		for(int i = 0 ; i<barList.size() ; i++){
			BarVO b = barList.get(i);
			if(b.getBarDate() == null || DateUtils.isSameDay(b.getBarDate(), lastIn) || lastIn.after(b.getBarDate())){
				passedBars = i;
				break;
			}
		}
		long interval = System.currentTimeMillis() - lastIn.getTime();
		int natureDays = (int) TimeUnit.DAYS.convert(interval, TimeUnit.MILLISECONDS);
		if(natureDays < passedBars){
			return natureDays;
		} else {
			return passedBars;
		}
	}

	public static boolean isFullPortfolio(PortfolioVO portfolio) {
		Set<PositionVO> pSet = portfolio.getPositionSet();
		double totalLoss = 0;
		for(PositionVO p : pSet){
			totalLoss += p.getHandPerUnit()*p.getUnitCount()*getAtr(p)
				*p.getContract().getContractMeta().getPointValue();
		}
		float fullRate = (float) (totalLoss / portfolio.getCash());
		if(!loggedFullRate){
			logger.info("[StrategyUtils] current full rate is: {}", fullRate);
			loggedFullRate  = true;
		}
		return fullRate >= 0.1;
	}
	
	public static Float getAtr(PositionVO p){
		Float atr = p.getContract().getContractMeta().getAtr();
		if(p.getAtr() != null && p.getAtr() > 0){
			atr = p.getAtr();
		}
		return atr;
	}

	public static void updateHandPerUnit(PositionVO p, PortfolioVO portfolioVO) {
		int hand = (int) Math.floor(portfolioVO.getCash() * 0.5/100/(2 * p.getContract().getContractMeta().getAtr() * 
				p.getContract().getContractMeta().getPointValue()));
		p.setHandPerUnit(hand);
	}

	public static void resetPosition(PositionVO position) {
		position.setUnitCount(0);
		position.setDirection("");
		position.setLastInPrice(0);
		position.setAveragePrice(0);
		position.setTopPrice(0);
		position.setLastInDate(null);
		position.setAtr(null);
	}

}
