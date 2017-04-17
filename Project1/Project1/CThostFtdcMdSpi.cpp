#include "ThostFtdcUserApiStruct.h"
#include "ThostFtdcMdApi.h"
#include "MdHolder.h"
#include "GatewayManager.h"
#include "Config.h"
#include <sstream>
#include <iostream>
using namespace std;

///once front connected, print message only;
void CThostFtdcMdSpi::OnFrontConnected(){
	std::cout << "front end connected!\n";
}

///once front dis-connected, print the reason only, system will auto-reconnect;
void CThostFtdcMdSpi::OnFrontDisconnected(int nReason){
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

void CThostFtdcMdSpi::OnHeartBeatWarning(int nTimeLapse){
	std::cout << "heart beat warning, time lapse:" << nTimeLapse << endl;
}

void CThostFtdcMdSpi::OnRspUserLogin(CThostFtdcRspUserLoginField *pRspUserLogin, CThostFtdcRspInfoField *pRspInfo, int nRequestID, bool bIsLast){
	std::cout << "user login response\n CZCE Time:\t" << pRspUserLogin->CZCETime << endl;
	std::cout << "DCE Time:\t" << pRspUserLogin->DCETime << endl;
	std::cout << "FFEX Time:\t" << pRspUserLogin->FFEXTime << endl;
	std::cout << "INE Time:\t" << pRspUserLogin->INETime << endl;
	std::cout << "SHFE Time:\t" << pRspUserLogin->SHFETime << endl;
}

void CThostFtdcMdSpi::OnRspUserLogout(CThostFtdcUserLogoutField *pUserLogout, CThostFtdcRspInfoField *pRspInfo, int nRequestID, bool bIsLast){
	std::cout << "user logout" << endl;
}

void CThostFtdcMdSpi::OnRspError(CThostFtdcRspInfoField *pRspInfo, int nRequestID, bool bIsLast){
	std::cout << "response error, ID:" << pRspInfo->ErrorID << "  message: " << pRspInfo->ErrorMsg << endl;
}

void CThostFtdcMdSpi::OnRspSubMarketData(CThostFtdcSpecificInstrumentField *pSpecificInstrument, CThostFtdcRspInfoField *pRspInfo, int nRequestID, bool bIsLast){
	std::cout << "market data reach\n instrument:\t" << pSpecificInstrument->InstrumentID << endl;
}

void CThostFtdcMdSpi::OnRtnDepthMarketData(CThostFtdcDepthMarketDataField *pDepthMarketData) {
	string message = "{TradingDay:" + string(pDepthMarketData->TradingDay) + ",InstrumentID:" + string(pDepthMarketData->InstrumentID) + ",ExchangeID:" + string(pDepthMarketData->ExchangeID) +
		",ExchangeInstID:" + string(pDepthMarketData->ExchangeInstID) + ",LastPrice:" + stringify(pDepthMarketData->LastPrice) +
		",PreSettlementPrice:" + stringify(pDepthMarketData->PreSettlementPrice) + ",PreClosePrice:" + stringify(pDepthMarketData->PreClosePrice) +
		",PreOpenInterest:" + stringify(pDepthMarketData->PreOpenInterest) + ",OpenPrice:" + stringify(pDepthMarketData->OpenPrice) +
		",HighestPrice:" + stringify(pDepthMarketData->HighestPrice) + ",LowestPrice:" + stringify(pDepthMarketData->LowestPrice) +
		",Volume:" + stringify(pDepthMarketData->Volume) + ",Turnover:" + stringify(pDepthMarketData->Turnover) + ",OpenInterest:" + stringify(pDepthMarketData->OpenInterest) +
		",ClosePrice:" + stringify(pDepthMarketData->ClosePrice) + ",SettlementPrice:" + stringify(pDepthMarketData->SettlementPrice) +
		",UpperLimitPrice:" + stringify(pDepthMarketData->UpperLimitPrice) + ",LowerLimitPrice:" + stringify(pDepthMarketData->LowerLimitPrice) +
		",PreDelta:" + stringify(pDepthMarketData->PreDelta) + ",CurrDelta:" + stringify(pDepthMarketData->CurrDelta) + ",UpdateTime:" + string(pDepthMarketData->UpdateTime) +
		",UpdateMillisec:" + stringify(pDepthMarketData->UpdateMillisec) + ",BidPrice1:" + stringify(pDepthMarketData->BidPrice1) + ",BidVolume1:" + stringify(pDepthMarketData->BidVolume1) +
		",AskPrice1:" + stringify(pDepthMarketData->AskPrice1) + ",AskVolume1:" + stringify(pDepthMarketData->AskVolume1) + ",BidPrice2:" + stringify(pDepthMarketData->BidPrice2) +
		",BidVolume2:" + stringify(pDepthMarketData->BidVolume2) + ",AskPrice2:" + stringify(pDepthMarketData->AskPrice2) + ",AskVolume2:" + stringify(pDepthMarketData->AskVolume2) +
		",BidPrice3:" + stringify(pDepthMarketData->BidPrice3) + ",BidVolume3:" + stringify(pDepthMarketData->BidVolume3) + ",AskPrice3:" + stringify(pDepthMarketData->AskPrice3) +
		",AskVolume3:" + stringify(pDepthMarketData->AskVolume3) + ",BidPrice4:" + stringify(pDepthMarketData->BidPrice4) + ",BidVolume4:" + stringify(pDepthMarketData->BidVolume4) +
		",AskPrice4:" + stringify(pDepthMarketData->AskPrice4) + ",AskVolume4:" + stringify(pDepthMarketData->AskVolume4) + ",BidPrice5:" + stringify(pDepthMarketData->BidPrice5) +
		",BidVolume5:" + stringify(pDepthMarketData->BidVolume5) + ",AskPrice5:" + stringify(pDepthMarketData->AskPrice5) + ",AskVolume5:" + stringify(pDepthMarketData->AskVolume5) +
		",AveragePrice:" + stringify(pDepthMarketData->AveragePrice) + ",ActionDay:" + string(pDepthMarketData->ActionDay) + "}";
	GatewayManager* gm = GatewayManager::getInstance();
	gm->sendTextMessage(message, TOPIC_MD);
}

string stringify(double x)
{
	ostringstream o;
	o << x;
	return o.str();
}