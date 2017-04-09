#include "TdSpi.h"
#include <iostream>
#include <string>
#include <cstring>
#include <chrono>
#include <thread>
using namespace std;

//ʵ���˺�
//const string USER_ID = "83601689";
//const string PASS = "270338";
//const string BROKER = "7090";
string tradingDate;

//ģ���˺�
const string USER_ID = "00000030";
const string PASS = "123456";
const string BROKER = "1035";

//���캯��
TdSpi::TdSpi(CThostFtdcTraderApi *tdapi){
	this->tdapi = tdapi;
}

//���ͻ����뽻�׺�̨������ͨ������ʱ����δ��¼ǰ�����÷��������á�
void TdSpi::OnFrontConnected(){
	cout << "Td���ӳɹ�" << endl;
	cout << "�����½\n";
	loginField = new CThostFtdcReqUserLoginField();
	strcpy_s(loginField->BrokerID, BROKER.c_str());
	strcpy_s(loginField->UserID, USER_ID.c_str());
	strcpy_s(loginField->Password, PASS.c_str());
	tdapi->ReqUserLogin(loginField, 0);
}

///��¼������Ӧ
void TdSpi::OnRspUserLogin(CThostFtdcRspUserLoginField *pRspUserLogin,
	CThostFtdcRspInfoField *pRspInfo, int nRequestID, bool bIsLast){
	cout << "��¼����ص�OnRspUserLogin" << endl;
	cout << pRspInfo->ErrorID << " " << pRspInfo->ErrorMsg << endl;
	cout << "ǰ�ñ��:" << pRspUserLogin->FrontID << endl
		<< "�Ự���" << pRspUserLogin->SessionID << endl
		<< "��󱨵�����:" << pRspUserLogin->MaxOrderRef << endl
		<< "������ʱ�䣺" << pRspUserLogin->SHFETime << endl
		<< "������ʱ�䣺" << pRspUserLogin->DCETime << endl
		<< "֣����ʱ�䣺" << pRspUserLogin->CZCETime << endl
		<< "�н���ʱ�䣺" << pRspUserLogin->FFEXTime << endl
		<< "�����գ�" << tdapi->GetTradingDay() << endl;
	tradingDate = tdapi->GetTradingDay();//���ý�������
	cout << "--------------------------------------------" << endl << endl;

	CThostFtdcQryTradingAccountField *account = new CThostFtdcQryTradingAccountField();
	strcpy_s(account->BrokerID, BROKER.c_str());
	strcpy_s(account->InvestorID, USER_ID.c_str());
	tdapi->ReqQryTradingAccount(account, 999);

	//��ѯ�Ƿ��Ѿ�����ȷ��
	//CThostFtdcQrySettlementInfoConfirmField *isConfirm = new CThostFtdcQrySettlementInfoConfirmField();
	//strcpy(isConfirm->BrokerID, BROKER.c_str());
	//strcpy(isConfirm->InvestorID, USER_ID.c_str());
	//tdapi->ReqQrySettlementInfoConfirm(isConfirm, 0);
}

//�����ѯ������Ϣȷ����Ӧ
void TdSpi::OnRspQrySettlementInfoConfirm(CThostFtdcSettlementInfoConfirmField *pSettlementInfoConfirm,
	CThostFtdcRspInfoField *pRspInfo, int nRequestID, bool bIsLast){
	if (pRspInfo == nullptr || pRspInfo->ErrorID == 0){
		cout << pSettlementInfoConfirm->ConfirmDate << endl;
		cout << pSettlementInfoConfirm->ConfirmTime << endl;
		string lastConfirmDate = pSettlementInfoConfirm->ConfirmDate;
		if (lastConfirmDate != tradingDate){
			//���컹ûȷ��,��һ�η��ͽ���ָ��ǰ����ѯͶ���߽�����
			CThostFtdcQrySettlementInfoField *a = new CThostFtdcQrySettlementInfoField();
			strcpy_s(a->BrokerID, BROKER.c_str());
			strcpy_s(a->InvestorID, USER_ID.c_str());
			strcpy_s(a->TradingDay, lastConfirmDate.c_str());

			std::chrono::milliseconds sleepDuration(1 * 1000);
			std::this_thread::sleep_for(sleepDuration);
			tdapi->ReqQrySettlementInfo(a, 1);
		}else{
			//�����Ѿ�ȷ��
			CThostFtdcQryTradingAccountField *account = new CThostFtdcQryTradingAccountField();
			strcpy_s(account->BrokerID, BROKER.c_str());
			strcpy_s(account->InvestorID, USER_ID.c_str());
			tdapi->ReqQryTradingAccount(account, 999);
		}
	}
}

//�����ѯͶ���߽�������Ӧ
void TdSpi::OnRspQrySettlementInfo(CThostFtdcSettlementInfoField *pSettlementInfo,
	CThostFtdcRspInfoField *pRspInfo, int nRequestID, bool bIsLast){
	cout << pSettlementInfo->Content << endl;

	if (bIsLast == true){
		//ȷ��Ͷ���߽�����
		CThostFtdcSettlementInfoConfirmField *a = new CThostFtdcSettlementInfoConfirmField();
		strcpy_s(a->BrokerID, BROKER.c_str());
		strcpy_s(a->InvestorID, USER_ID.c_str());
		int result = tdapi->ReqSettlementInfoConfirm(a, 2);
		cout << "result:" << result << endl;
	}
}


//Ͷ���߽�����ȷ����Ӧ
void TdSpi::OnRspSettlementInfoConfirm(CThostFtdcSettlementInfoConfirmField *pSettlementInfoConfirm,
	CThostFtdcRspInfoField *pRspInfo, int nRequestID, bool bIsLast){
	cout << endl << "OnRspSettlementInfoConfirm, ID: " << nRequestID << endl;
	if (pRspInfo != nullptr){
		cout << pRspInfo->ErrorID << ends << pRspInfo->ErrorMsg << endl;
	}
	cout << "���͹�˾����:" << pSettlementInfoConfirm->BrokerID << endl
		<< "�û��˺�:" << pSettlementInfoConfirm->InvestorID << endl
		<< "ȷ�����ڣ�" << pSettlementInfoConfirm->ConfirmDate << endl
		<< "ȷ��ʱ�䣺" << pSettlementInfoConfirm->ConfirmTime << endl;

	CThostFtdcQryTradingAccountField *account = new CThostFtdcQryTradingAccountField();
	strcpy_s(account->BrokerID, BROKER.c_str());
	strcpy_s(account->InvestorID, USER_ID.c_str());
	tdapi->ReqQryTradingAccount(account, 999);

	//��ѯ�����ѯ�ɽ�
	//std::chrono::milliseconds sleepDuration(5 * 1000);
	//std::this_thread::sleep_for(sleepDuration);
	//CThostFtdcQryTradeField *a = new CThostFtdcQryTradeField();
	//strcpy(a->BrokerID, BROKER.c_str());
	//strcpy(a->InvestorID, USER_ID.c_str());
	//strcpy(a->InstrumentID, "cu1409");
	//strcpy(a->TradeTimeStart, "20140101");
	//strcpy(a->TradeTimeEnd, "20140720");
	//tdapi->ReqQryTrade(a, 10);

	//�����ѯͶ���ֲ߳���ϸ
	//std::chrono::milliseconds sleepDuration(1 * 1000);
	//std::this_thread::sleep_for(sleepDuration);
	//CThostFtdcQryInvestorPositionField *a = new CThostFtdcQryInvestorPositionField();
	//strcpy(a->BrokerID, BROKER.c_str());
	//strcpy(a->InvestorID, USER_ID.c_str());
	//strcpy(a->InstrumentID, "");
	//int result = tdapi->ReqQryInvestorPosition(a, 10);
	//cout << result << endl;

	////��Ϣ�����ٷ�
	//std::chrono::milliseconds sleepDuration(1*1000);
	//std::this_thread::sleep_for(sleepDuration);
	//cout << "X.X" << endl;
	//int result=tdapi->ReqQryInvestorPosition(a, 3);
	//cout << "result:" << result << endl;
}

//�����ѯͶ���ֲ߳���Ӧ
void TdSpi::OnRspQryInvestorPosition(CThostFtdcInvestorPositionField *pInvestorPosition,
	CThostFtdcRspInfoField *pRspInfo, int nRequestID, bool bIsLast){
	cout << "OnRspQryInvestorPosition  ID: " << nRequestID << endl;
	cout << "������룺" << pRspInfo->ErrorID << "������Ϣ:" << pRspInfo->ErrorMsg;
	cout << "�ֲֶ�շ���:" << pInvestorPosition->PosiDirection << endl;
	if (bIsLast){
		cout << "last\n";
	}
}

///�����ѯ�ɽ���Ӧ
void TdSpi::OnRspQryTrade(CThostFtdcTradeField *pTrade,
	CThostFtdcRspInfoField *pRspInfo, int nRequestID, bool bIsLast){
	if (pRspInfo == nullptr || pRspInfo->ErrorID == 0){
		cout << pTrade->BrokerID << endl
			<< pTrade->BrokerOrderSeq << endl;
	}
}

///�ǳ�������Ӧ
void TdSpi::OnRspUserLogout(CThostFtdcUserLogoutField *pUserLogout,
	CThostFtdcRspInfoField *pRspInfo, int nRequestID, bool bIsLast){
}

///�û��������������Ӧ
void TdSpi::OnRspUserPasswordUpdate(CThostFtdcUserPasswordUpdateField *pUserPasswordUpdate,
	CThostFtdcRspInfoField *pRspInfo, int nRequestID, bool bIsLast){
	cout << "�ص��û��������������ӦOnRspUserPasswordUpdate" << endl;
	if (pRspInfo->ErrorID == 0){
		cout << "���ĳɹ� " << endl
			<< "������Ϊ:" << pUserPasswordUpdate->OldPassword << endl
			<< "������Ϊ:" << pUserPasswordUpdate->NewPassword << endl;
	}
	else{
		cout << pRspInfo->ErrorID << ends << pRspInfo->ErrorMsg << endl;
	}
}

///�����ѯ������Ӧ
void TdSpi::OnRspQryDepthMarketData(CThostFtdcDepthMarketDataField *pDepthMarketData,
	CThostFtdcRspInfoField *pRspInfo, int nRequestID, bool bIsLast){
	cout << "OnRspQryDepthMarketData" << endl;
	cout << nRequestID << endl;
	if (pDepthMarketData != nullptr){
		cout << "-----------------��������--------------------" << endl;
		cout << "������:" << pDepthMarketData->TradingDay << endl
			<< "��Լ����:" << pDepthMarketData->InstrumentID << endl
			<< "���¼�:" << pDepthMarketData->LastPrice << endl
			<< "��߼�:" << pDepthMarketData->HighestPrice << endl
			<< "��ͼ�:" << pDepthMarketData->LowestPrice << endl;
		cout << "-----------------��������--------------------" << endl;
	}
}

//��ѯ�ʽ��ʻ���Ӧ
void TdSpi::OnRspQryTradingAccount(CThostFtdcTradingAccountField *pTradingAccount,
	CThostFtdcRspInfoField *pRspInfo, int nRequestID, bool bIsLast){
	cout << "hey!\n";
	if (pRspInfo == nullptr || pRspInfo->ErrorID == 0){
		cout << "nRequestID: " << nRequestID << endl;
		cout << "�����ʽ�" << pTradingAccount->Available << endl;
	}
}