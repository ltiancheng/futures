#include "MdSpi.h"
#include "ThostFtdcUserApiStruct.h"
#include <iostream>
#include <cstring>
#include <string>
using namespace std;

const string BROKER_ID = "9999";
const string NULL_STR = "";

MdSpi::MdSpi(CThostFtdcMdApi *mdapi){
	this->mdapi = mdapi;
	loginRequestID = 10;
}

void MdSpi::OnFrontConnected(){
	cout << "已连接上，请求登录" << endl;
	loginField = new CThostFtdcReqUserLoginField();
	strcpy_s(loginField->BrokerID, BROKER_ID.c_str());
	strcpy_s(loginField->UserID, NULL_STR.c_str());
	strcpy_s(loginField->Password, NULL_STR.c_str());
	mdapi->ReqUserLogin(loginField, loginRequestID);
}

void MdSpi::OnRspUserLogin(CThostFtdcRspUserLoginField *pRspUserLogin, CThostFtdcRspInfoField *pRspInfo,
	int nRequestID, bool bIsLast){
	cout << "登陆成功\n";
	cout << "交易日：" << mdapi->GetTradingDay() << endl;
	if (pRspInfo->ErrorID == 0){
		cout << "请求的登陆成功," << "请求ID为" << loginRequestID << endl;
		/***************************************************************/
		cout << "尝试订阅行情" << endl;
		char *instrumentID[] = { "M1709" };	//订阅一个合约所以数量为1
		mdapi->SubscribeMarketData(instrumentID, 1);
	}
}

void MdSpi::OnRspUserLogout(CThostFtdcUserLogoutField *pUserLogout, CThostFtdcRspInfoField *pRspInfo,
	int nRequestID, bool bIsLast){

}

//订阅行情应答
void MdSpi::OnRspSubMarketData(CThostFtdcSpecificInstrumentField *pSpecificInstrument, CThostFtdcRspInfoField *pRspInfo,
	int nRequestID, bool bIsLast){
	cout << "订阅行情应答" << endl;
	cout << "合约代码:" << pSpecificInstrument->InstrumentID << endl;
	cout << "应答信息:" << pRspInfo->ErrorID << " " << pRspInfo->ErrorMsg << endl;
	char *instrumentID[] = { "M1709" };
	mdapi->SubscribeForQuoteRsp(instrumentID, 1);
}

//取消订阅行情应答
void MdSpi::OnRspUnSubMarketData(CThostFtdcSpecificInstrumentField *pSpecificInstrument, CThostFtdcRspInfoField *pRspInfo,
	int nRequestID, bool bIsLast){

}

void MdSpi::OnRtnDepthMarketData(CThostFtdcDepthMarketDataField *pDepthMarketData){
	cout << "===========================================" << endl;
	cout << "深度行情" << endl;
	cout << " 交易日:" << pDepthMarketData->TradingDay << endl
		<< "合约代码:" << pDepthMarketData->InstrumentID << endl
		<< "最新价:" << pDepthMarketData->LastPrice << endl
		//<< "上次结算价:" << pDepthMarketData->PreSettlementPrice << endl
		//<< "昨收盘:" << pDepthMarketData->PreClosePrice << endl
		//<< "数量:" << pDepthMarketData->Volume << endl
		//<< "昨持仓量:" << pDepthMarketData->PreOpenInterest << endl
		<< "最后修改时间" << pDepthMarketData->UpdateTime << endl
		<< "最后修改毫秒" << pDepthMarketData->UpdateMillisec << endl;
	//<< "申买价一：" << pDepthMarketData->BidPrice1 << endl
	//<< "申买量一:" << pDepthMarketData->BidVolume1 << endl
	//<< "申卖价一:" << pDepthMarketData->AskPrice1 << endl
	//<< "申卖量一:" << pDepthMarketData->AskVolume1 << endl
	//<< "今收盘价:" << pDepthMarketData->ClosePrice << endl
	//<< "当日均价:" << pDepthMarketData->AveragePrice << endl
	//<< "本次结算价格:" << pDepthMarketData->SettlementPrice << endl
	//<< "成交金额:" << pDepthMarketData->Turnover << endl
	//<< "持仓量:" << pDepthMarketData->OpenInterest << endl;
}