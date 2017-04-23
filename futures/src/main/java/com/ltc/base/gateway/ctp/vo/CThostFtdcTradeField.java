package com.ltc.base.gateway.ctp.vo;

public class CThostFtdcTradeField {
	///经纪公司代码
	public String BrokerID;
	///投资者代码
	public String InvestorID;
	///合约代码
	public String InstrumentID;
	///报单引用
	public String OrderRef;
	///用户代码
	public String UserID;
	///交易所代码
	public String ExchangeID;
	///成交编号
	public String TradeID;
	///买卖方向
	public String Direction;
	///报单编号
	public String OrderSysID;
	///会员代码
	public String ParticipantID;
	///客户代码
	public String ClientID;
	///交易角色
	public String TradingRole;
	///合约在交易所的代码
	public String ExchangeInstID;
	///开平标志
	public String OffsetFlag;
	///投机套保标志
	public String HedgeFlag;
	///价格
	public float Price;
	///数量
	public int Volume;
	///成交时期
	public String TradeDate;
	///成交时间
	public String TradeTime;
	///成交类型
	public String TradeType;
	///成交价来源
	public String PriceSource;
	///交易所交易员代码
	public String TraderID;
	///本地报单编号
	public String OrderLocalID;
	///结算会员编号
	public String ClearingPartID;
	///业务单元
	public String BusinessUnit;
	///序号
	public int SequenceNo;
	///交易日
	public String TradingDay;
	///结算编号
	public int SettlementID;
	///经纪公司报单编号
	public int BrokerOrderSeq;
	///成交来源
	public String TradeSource;
}