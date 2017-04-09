#include "ThostFtdcUserApiStruct.h"
#include "ThostFtdcMdApi.h"
#include "MdHolder.h"
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

void OnHeartBeatWarning(int nTimeLapse){
	std::cout << "heart beat warning, time lapse:" << nTimeLapse << endl;
}

void OnRspUserLogin(CThostFtdcRspUserLoginField *pRspUserLogin, CThostFtdcRspInfoField *pRspInfo, int nRequestID, bool bIsLast){
	std::cout << "user login response\n CZCE Time:\t" << pRspUserLogin->CZCETime << endl;
	std::cout << "DCE Time:\t" << pRspUserLogin->DCETime << endl;
	std::cout << "FFEX Time:\t" << pRspUserLogin->FFEXTime << endl;
	std::cout << "INE Time:\t" << pRspUserLogin->INETime << endl;
	std::cout << "SHFE Time:\t" << pRspUserLogin->SHFETime << endl;
}

void OnRspUserLogout(CThostFtdcUserLogoutField *pUserLogout, CThostFtdcRspInfoField *pRspInfo, int nRequestID, bool bIsLast){
	std::cout << "user logout" << endl;
}

void OnRspError(CThostFtdcRspInfoField *pRspInfo, int nRequestID, bool bIsLast){
	std::cout << "response error, ID:" << pRspInfo->ErrorID << "  message: " << pRspInfo->ErrorMsg << endl;
}

void OnRspSubMarketData(CThostFtdcSpecificInstrumentField *pSpecificInstrument, CThostFtdcRspInfoField *pRspInfo, int nRequestID, bool bIsLast){
	std::cout << "market data reach\n instrument:\t" << pSpecificInstrument->InstrumentID << endl;
}