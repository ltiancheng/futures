package com.ltc.base.gateway.ctp.vo;

import com.fasterxml.jackson.annotation.JsonRawValue;
import com.ltc.base.gateway.ctp.helper.CtpHelper;
import com.ltc.base.helpers.BaseUtils;
import com.ltc.base.vo.BarVO;

public class CThostFtdcDepthMarketDataField {
	/// 交易日
	public String TradingDay;
	/// 合约代码
	public String InstrumentID;
	/// 交易所代码
	public String ExchangeID;
	/// 合约在交易所的代码
	public String ExchangeInstID;
	/// 最新价
	public float LastPrice;
	/// 上次结算价
	public float PreSettlementPrice;
	/// 昨收盘
	public float PreClosePrice;
	/// 昨持仓量
	public float PreOpenInterest;
	/// 今开盘
	public float OpenPrice;
	/// 最高价
	public float HighestPrice;
	/// 最低价
	public float LowestPrice;
	/// 数量
	public int Volume;
	/// 成交金额
	public double Turnover;
	/// 持仓量
	public double OpenInterest;
	/// 今收盘
	public float ClosePrice;
	/// 本次结算价
	public float SettlementPrice;
	/// 涨停板价
	public float UpperLimitPrice;
	/// 跌停板价
	public float LowerLimitPrice;
	/// 昨虚实度
	public double PreDelta;
	/// 今虚实度
	public double CurrDelta;
	/// 最后修改时间
	@JsonRawValue
	public String UpdateTime;
	/// 最后修改毫秒
	public int UpdateMillisec;
	/// 申买价一
	public float BidPrice1;
	/// 申买量一
	public int BidVolume1;
	/// 申卖价一
	public float AskPrice1;
	/// 申卖量一
	public int AskVolume1;
	/// 申买价二
	public float BidPrice2;
	/// 申买量二
	public int BidVolume2;
	/// 申卖价二
	public float AskPrice2;
	/// 申卖量二
	public int AskVolume2;
	/// 申买价三
	public float BidPrice3;
	/// 申买量三
	public int BidVolume3;
	/// 申卖价三
	public float AskPrice3;
	/// 申卖量三
	public int AskVolume3;
	/// 申买价四
	public float BidPrice4;
	/// 申买量四
	public int BidVolume4;
	/// 申卖价四
	public float AskPrice4;
	/// 申卖量四
	public int AskVolume4;
	/// 申买价五
	public float BidPrice5;
	/// 申买量五
	public int BidVolume5;
	/// 申卖价五
	public float AskPrice5;
	/// 申卖量五
	public int AskVolume5;
	/// 当日均价
	public float AveragePrice;
	/// 业务日期
	public String ActionDay;
	public BarVO toBar() {
		BarVO bar = new BarVO();
		bar.setClosePrice(BaseUtils.getTrueValue(LastPrice, 0));
		bar.setAmount((long) BaseUtils.getTrueValue(Turnover, 0));
		bar.setBarDate(CtpHelper.parseDateTime(TradingDay, UpdateTime));
		bar.setHighPrice(BaseUtils.getTrueValue(HighestPrice, bar.getClosePrice()));
		bar.setLowPrice(BaseUtils.getTrueValue(LowestPrice, bar.getClosePrice()));
		bar.setOpenPrice(BaseUtils.getTrueValue(OpenPrice, bar.getClosePrice()));
		bar.setVolume((long) BaseUtils.getTrueValue(Volume, 0));
		bar.setTopPrice(BaseUtils.getTrueValue(UpperLimitPrice, bar.getClosePrice()));
		bar.setBottomPrice(BaseUtils.getTrueValue(LowerLimitPrice, bar.getClosePrice()));
		return bar;
	}
}
