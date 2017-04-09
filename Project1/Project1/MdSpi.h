#ifndef MDSPI_H
#define MDSPI_H
#include "ThostFtdcMdApi.h"

#include <map>
#include <string>
using std::map;
using std::string;

class MdSpi :public CThostFtdcMdSpi{
public:
	MdSpi(CThostFtdcMdApi *mdapi);
	//��������ʱ����
	void OnFrontConnected();
	///��¼������Ӧ
	void OnRspUserLogin(CThostFtdcRspUserLoginField *pRspUserLogin, CThostFtdcRspInfoField *pRspInfo,
		int nRequestID, bool bIsLast);
	///�ǳ�������Ӧ
	void OnRspUserLogout(CThostFtdcUserLogoutField *pUserLogout, CThostFtdcRspInfoField *pRspInfo,
		int nRequestID, bool bIsLast);
	///��������Ӧ��
	void OnRspSubMarketData(CThostFtdcSpecificInstrumentField *pSpecificInstrument, CThostFtdcRspInfoField *pRspInfo,
		int nRequestID, bool bIsLast);
	///ȡ����������Ӧ��
	void OnRspUnSubMarketData(CThostFtdcSpecificInstrumentField *pSpecificInstrument, CThostFtdcRspInfoField *pRspInfo, 
		int nRequestID, bool bIsLast);
	///�������֪ͨ
	void OnRtnDepthMarketData(CThostFtdcDepthMarketDataField *pDepthMarketData);
private:
	CThostFtdcMdApi *mdapi;
	CThostFtdcReqUserLoginField *loginField;
	int loginRequestID;
};

#endif