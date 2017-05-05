#include "MyMdSpi.h"
#include "ThostFtdcUserApiStruct.h"
#include "ThostFtdcMdApi.h"
#include "MdHolder.h"
#include "GatewayManager.h"
#include "Config.h"
#include <sstream>
#include <iostream>
using namespace std;


MyMdSpi::MyMdSpi()
{
}


MyMdSpi::~MyMdSpi()
{
}

MyMdSpi::MyMdSpi(CThostFtdcMdApi* api){
	this->mdApi = api;
}

///once front connected, print message only;
void MyMdSpi::OnFrontConnected(){
	std::cout << "front end connected!\n";
	CThostFtdcReqUserLoginField* loginField = new CThostFtdcReqUserLoginField();
	strcpy(loginField->BrokerID, MD_BROKER_ID);
	strcpy(loginField->UserID, INVESTOR_ID);
	strcpy(loginField->Password, INVESTOR_PWD);
	this->mdApi->ReqUserLogin(loginField, 1000);
}

///once front dis-connected, print the reason only, system will auto-reconnect;
void MyMdSpi::OnFrontDisconnected(int nReason){
	std::cout << "front end dis-connected! reason: \n";
	switch (nReason){
	case 0x1001:{
					std::cout << "0x1001 ÍøÂç¶ÁÊ§°Ü" << endl;
					break;
	}
	case 0x1002:{
					std::cout << "0x1002 ÍøÂçÐ´Ê§°Ü" << endl;
					break;
	}
	case 0x2001:{
					std::cout << "0x2001 ½ÓÊÕÐÄÌø³¬Ê±" << endl;
					break;
	}
	case 0x2002: {
					 std::cout << "0x2002 ·¢ËÍÐÄÌøÊ§°Ü" << endl;
					 break;
	}
	case 0x2003:{
					std::cout << "0x2003 ÊÕµ½´íÎó±¨ÎÄ" << endl;
					break;
	}
	default:
		std::cout << "unknow reason: " << nReason << endl;
	}
}

void MyMdSpi::OnHeartBeatWarning(int nTimeLapse){
	std::cout << "heart beat warning, time lapse:" << nTimeLapse << endl;
}

void MyMdSpi::OnRspUserLogin(CThostFtdcRspUserLoginField *pRspUserLogin, CThostFtdcRspInfoField *pRspInfo, int nRequestID, bool bIsLast){
	std::cout << "md user login response\n CZCE Time:\t" << pRspUserLogin->CZCETime << endl;
	std::cout << "DCE Time:\t" << pRspUserLogin->DCETime << endl;
	std::cout << "FFEX Time:\t" << pRspUserLogin->FFEXTime << endl;
	std::cout << "INE Time:\t" << pRspUserLogin->INETime << endl;
	std::cout << "SHFE Time:\t" << pRspUserLogin->SHFETime << endl;
	if (this->lastCount > 0){
		//regist last contracts:
		cout << "recovering from a disconnection, re-subscribing market data" << endl;
		this->mdApi->SubscribeMarketData(this->lastContractArray, this->lastCount);
	}
}

void MyMdSpi::OnRspUserLogout(CThostFtdcUserLogoutField *pUserLogout, CThostFtdcRspInfoField *pRspInfo, int nRequestID, bool bIsLast){
	std::cout << "user logout" << endl;
}

void MyMdSpi::OnRspError(CThostFtdcRspInfoField *pRspInfo, int nRequestID, bool bIsLast){
	std::cout << "response error, ID:" << pRspInfo->ErrorID << "  message: " << pRspInfo->ErrorMsg << endl;
}

void MyMdSpi::OnRspSubMarketData(CThostFtdcSpecificInstrumentField *pSpecificInstrument, CThostFtdcRspInfoField *pRspInfo, int nRequestID, bool bIsLast){
	//std::cout << "market data subscribed\n instrument:\t" << pSpecificInstrument->InstrumentID << endl;
}

void MyMdSpi::OnRtnDepthMarketData(CThostFtdcDepthMarketDataField *pDepthMarketData) {
	//std::cout << "deep market data reach\n instrument:\t" << pDepthMarketData->InstrumentID << endl;
	string message = "{TradingDay:" + stringify(pDepthMarketData->TradingDay) + ",InstrumentID:" + stringify(pDepthMarketData->InstrumentID) + ",ExchangeID:" + stringify(pDepthMarketData->ExchangeID) +
		",ExchangeInstID:" + stringify(pDepthMarketData->ExchangeInstID) + ",LastPrice:" + stringify(pDepthMarketData->LastPrice) +
		",PreSettlementPrice:" + stringify(pDepthMarketData->PreSettlementPrice) + ",PreClosePrice:" + stringify(pDepthMarketData->PreClosePrice) +
		",PreOpenInterest:" + stringify(pDepthMarketData->PreOpenInterest) + ",OpenPrice:" + stringify(pDepthMarketData->OpenPrice) +
		",HighestPrice:" + stringify(pDepthMarketData->HighestPrice) + ",LowestPrice:" + stringify(pDepthMarketData->LowestPrice) +
		",Volume:" + stringify(pDepthMarketData->Volume) + ",Turnover:" + stringify(pDepthMarketData->Turnover) + ",OpenInterest:" + stringify(pDepthMarketData->OpenInterest) +
		",ClosePrice:" + stringify(pDepthMarketData->ClosePrice) + ",SettlementPrice:" + stringify(pDepthMarketData->SettlementPrice) +
		",UpperLimitPrice:" + stringify(pDepthMarketData->UpperLimitPrice) + ",LowerLimitPrice:" + stringify(pDepthMarketData->LowerLimitPrice) +
		",PreDelta:" + stringify(pDepthMarketData->PreDelta) + ",CurrDelta:" + stringify(pDepthMarketData->CurrDelta) + ",UpdateTime:" + stringify(pDepthMarketData->UpdateTime) +
		",UpdateMillisec:" + stringify(pDepthMarketData->UpdateMillisec) + ",BidPrice1:" + stringify(pDepthMarketData->BidPrice1) + ",BidVolume1:" + stringify(pDepthMarketData->BidVolume1) +
		",AskPrice1:" + stringify(pDepthMarketData->AskPrice1) + ",AskVolume1:" + stringify(pDepthMarketData->AskVolume1) + ",BidPrice2:" + stringify(pDepthMarketData->BidPrice2) +
		",BidVolume2:" + stringify(pDepthMarketData->BidVolume2) + ",AskPrice2:" + stringify(pDepthMarketData->AskPrice2) + ",AskVolume2:" + stringify(pDepthMarketData->AskVolume2) +
		",BidPrice3:" + stringify(pDepthMarketData->BidPrice3) + ",BidVolume3:" + stringify(pDepthMarketData->BidVolume3) + ",AskPrice3:" + stringify(pDepthMarketData->AskPrice3) +
		",AskVolume3:" + stringify(pDepthMarketData->AskVolume3) + ",BidPrice4:" + stringify(pDepthMarketData->BidPrice4) + ",BidVolume4:" + stringify(pDepthMarketData->BidVolume4) +
		",AskPrice4:" + stringify(pDepthMarketData->AskPrice4) + ",AskVolume4:" + stringify(pDepthMarketData->AskVolume4) + ",BidPrice5:" + stringify(pDepthMarketData->BidPrice5) +
		",BidVolume5:" + stringify(pDepthMarketData->BidVolume5) + ",AskPrice5:" + stringify(pDepthMarketData->AskPrice5) + ",AskVolume5:" + stringify(pDepthMarketData->AskVolume5) +
		",AveragePrice:" + stringify(pDepthMarketData->AveragePrice) + ",ActionDay:" + stringify(pDepthMarketData->ActionDay) + "}";
	GatewayManager* gm = &GatewayManager::getInstance();
	gm->sendTextMessage(message, TOPIC_MD, true);
}

string MyMdSpi::stringify(double x)
{
	std::ostringstream o;
	o << fixed << x;
	string result = o.str();
	if (result.length() == 0){
		return "0";
	}
	else {
		return result;
	}
}

string MyMdSpi::stringify(char x)
{
	if (0 == x){
		return "\"\"";
	}
	else {
		return "\"" + string(1, x) + "\"";
	}
}

string MyMdSpi::stringify(int x)
{
	std::ostringstream o;
	o << fixed << x;
	string result = o.str();
	if (result.length() == 0){
		return "0";
	}
	else {
		return result;
	}
}

string MyMdSpi::stringify(char* x)
{
	return "\"" + string(x) + "\"";
}