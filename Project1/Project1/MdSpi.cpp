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
	cout << "�������ϣ������¼" << endl;
	loginField = new CThostFtdcReqUserLoginField();
	strcpy_s(loginField->BrokerID, BROKER_ID.c_str());
	strcpy_s(loginField->UserID, NULL_STR.c_str());
	strcpy_s(loginField->Password, NULL_STR.c_str());
	mdapi->ReqUserLogin(loginField, loginRequestID);
}

void MdSpi::OnRspUserLogin(CThostFtdcRspUserLoginField *pRspUserLogin, CThostFtdcRspInfoField *pRspInfo,
	int nRequestID, bool bIsLast){
	cout << "��½�ɹ�\n";
	cout << "�����գ�" << mdapi->GetTradingDay() << endl;
	if (pRspInfo->ErrorID == 0){
		cout << "����ĵ�½�ɹ�," << "����IDΪ" << loginRequestID << endl;
		/***************************************************************/
		cout << "���Զ�������" << endl;
		char *instrumentID[] = { "M1709" };	//����һ����Լ��������Ϊ1
		mdapi->SubscribeMarketData(instrumentID, 1);
	}
}

void MdSpi::OnRspUserLogout(CThostFtdcUserLogoutField *pUserLogout, CThostFtdcRspInfoField *pRspInfo,
	int nRequestID, bool bIsLast){

}

//��������Ӧ��
void MdSpi::OnRspSubMarketData(CThostFtdcSpecificInstrumentField *pSpecificInstrument, CThostFtdcRspInfoField *pRspInfo,
	int nRequestID, bool bIsLast){
	cout << "��������Ӧ��" << endl;
	cout << "��Լ����:" << pSpecificInstrument->InstrumentID << endl;
	cout << "Ӧ����Ϣ:" << pRspInfo->ErrorID << " " << pRspInfo->ErrorMsg << endl;
	char *instrumentID[] = { "M1709" };
	mdapi->SubscribeForQuoteRsp(instrumentID, 1);
}

//ȡ����������Ӧ��
void MdSpi::OnRspUnSubMarketData(CThostFtdcSpecificInstrumentField *pSpecificInstrument, CThostFtdcRspInfoField *pRspInfo,
	int nRequestID, bool bIsLast){

}

void MdSpi::OnRtnDepthMarketData(CThostFtdcDepthMarketDataField *pDepthMarketData){
	cout << "===========================================" << endl;
	cout << "�������" << endl;
	cout << " ������:" << pDepthMarketData->TradingDay << endl
		<< "��Լ����:" << pDepthMarketData->InstrumentID << endl
		<< "���¼�:" << pDepthMarketData->LastPrice << endl
		//<< "�ϴν����:" << pDepthMarketData->PreSettlementPrice << endl
		//<< "������:" << pDepthMarketData->PreClosePrice << endl
		//<< "����:" << pDepthMarketData->Volume << endl
		//<< "��ֲ���:" << pDepthMarketData->PreOpenInterest << endl
		<< "����޸�ʱ��" << pDepthMarketData->UpdateTime << endl
		<< "����޸ĺ���" << pDepthMarketData->UpdateMillisec << endl;
	//<< "�����һ��" << pDepthMarketData->BidPrice1 << endl
	//<< "������һ:" << pDepthMarketData->BidVolume1 << endl
	//<< "������һ:" << pDepthMarketData->AskPrice1 << endl
	//<< "������һ:" << pDepthMarketData->AskVolume1 << endl
	//<< "�����̼�:" << pDepthMarketData->ClosePrice << endl
	//<< "���վ���:" << pDepthMarketData->AveragePrice << endl
	//<< "���ν���۸�:" << pDepthMarketData->SettlementPrice << endl
	//<< "�ɽ����:" << pDepthMarketData->Turnover << endl
	//<< "�ֲ���:" << pDepthMarketData->OpenInterest << endl;
}