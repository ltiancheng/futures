package com.ltc.base.gateway.ctp.vo;

public class CThostFtdcInputOrderField {
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
	 ///报单价格条件
	 public String OrderPriceType;
	 ///买卖方向
	 public String Direction;
	 ///组合开平标志
	 public String CombOffsetFlag;
	 ///组合投机套保标志
	 public String CombHedgeFlag;
	 ///价格
	 public float LimitPrice;
	 ///数量
	 public int VolumeTotalOriginal;
	 ///有效期类型
	 public String TimeCondition;
	 ///GTD日期
	 public String GTDDate;
	 ///成交量类型
	 public String VolumeCondition;
	 ///最小成交量
	 public int MinVolume;
	 ///触发条件
	 public String ContingentCondition;
	 ///止损价
	 public float StopPrice;
	 ///强平原因
	 public String ForceCloseReason;
	 ///自动挂起标志
	 public int IsAutoSuspend;
	 ///业务单元
	 public String BusinessUnit;
	 ///请求编号
	 public int RequestID;
	 ///用户强评标志
	 public int UserForceClose;
	 ///互换单标志
	 public int IsSwapOrder;
	 ///交易所代码
	 public String ExchangeID;
	 ///投资单元代码
	 public String InvestUnitID;
	 ///资金账号
	 public String AccountID;
	 ///币种代码
	 public String CurrencyID;
	 ///交易编码
	 public String ClientID;
	 ///IP地址
	 public String IPAddress;
	 ///Mac地址
	 public String MacAddress;
}
