package com.ltc.strategy.tortoise.utils;

import java.util.List;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.ltc.base.vo.BarVO;
import com.ltc.strategy.tortoise.manager.impl.StrategyImpl;
import com.ltc.strategy.tortoise.vo.PortfolioVO;
import com.ltc.strategy.tortoise.vo.PositionVO;
import com.ltc.strategy.tortoise.vo.StrategyPricePointVO;

public class StrategyUtils {
	
	private static Logger logger = LoggerFactory.getLogger(StrategyUtils.class);

	public static StrategyPricePointVO getPricePoint(List<BarVO> barList) {
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
		return ssp;
	}

	public static boolean isFullPortfolio(PortfolioVO portfolio) {
		Set<PositionVO> pSet = portfolio.getPositionSet();
		double totalLoss = 0;
		for(PositionVO p : pSet){
			totalLoss += p.getHandPerUnit()*p.getUnitCount()*p.getContract().getContractMeta().getAtr()
				*p.getContract().getContractMeta().getPointValue();
		}
		return totalLoss / portfolio.getCash() >= 0.1;
	}

	public static void updateHandPerUnit(PositionVO p, PortfolioVO portfolioVO) {
		int hand = (int) Math.floor(portfolioVO.getCash() * 0.5/100/(2 * p.getContract().getContractMeta().getAtr() * 
				p.getContract().getContractMeta().getPointValue()));
		p.setHandPerUnit(hand);
	}

}
