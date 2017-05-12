package com.ltc.base.gateway.ctp.vo;

import com.fasterxml.jackson.annotation.JsonRawValue;
import com.ltc.base.gateway.ctp.helper.CtpHelper;
import com.ltc.base.helpers.BaseUtils;
import com.ltc.base.vo.BarVO;

public class CThostFtdcDepthMarketDataField {
	/// ������
	public String TradingDay;
	/// ��Լ����
	public String InstrumentID;
	/// ����������
	public String ExchangeID;
	/// ��Լ�ڽ������Ĵ���
	public String ExchangeInstID;
	/// ���¼�
	public float LastPrice;
	/// �ϴν����
	public float PreSettlementPrice;
	/// ������
	public float PreClosePrice;
	/// ��ֲ���
	public float PreOpenInterest;
	/// ����
	public float OpenPrice;
	/// ��߼�
	public float HighestPrice;
	/// ��ͼ�
	public float LowestPrice;
	/// ����
	public int Volume;
	/// �ɽ����
	public double Turnover;
	/// �ֲ���
	public double OpenInterest;
	/// ������
	public float ClosePrice;
	/// ���ν����
	public float SettlementPrice;
	/// ��ͣ���
	public float UpperLimitPrice;
	/// ��ͣ���
	public float LowerLimitPrice;
	/// ����ʵ��
	public double PreDelta;
	/// ����ʵ��
	public double CurrDelta;
	/// ����޸�ʱ��
	@JsonRawValue
	public String UpdateTime;
	/// ����޸ĺ���
	public int UpdateMillisec;
	/// �����һ
	public float BidPrice1;
	/// ������һ
	public int BidVolume1;
	/// ������һ
	public float AskPrice1;
	/// ������һ
	public int AskVolume1;
	/// ����۶�
	public float BidPrice2;
	/// ��������
	public int BidVolume2;
	/// �����۶�
	public float AskPrice2;
	/// ��������
	public int AskVolume2;
	/// �������
	public float BidPrice3;
	/// ��������
	public int BidVolume3;
	/// ��������
	public float AskPrice3;
	/// ��������
	public int AskVolume3;
	/// �������
	public float BidPrice4;
	/// ��������
	public int BidVolume4;
	/// ��������
	public float AskPrice4;
	/// ��������
	public int AskVolume4;
	/// �������
	public float BidPrice5;
	/// ��������
	public int BidVolume5;
	/// ��������
	public float AskPrice5;
	/// ��������
	public int AskVolume5;
	/// ���վ���
	public float AveragePrice;
	/// ҵ������
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
