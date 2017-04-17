#include "ThostFtdcUserApiStruct.h"
#include "ThostFtdcTraderApi.h"
#include "TdHolder.h"
#include "GatewayManager.h"
#include "Config.h"
#include <iostream>
#include <sstream>
using namespace std;

//���ӳɹ��Զ���¼
void CThostFtdcTraderSpi::OnFrontConnected(){
	cout << "Td���ӳɹ�" << endl;
	cout << "�����½\n";
	CThostFtdcReqUserLoginField * loginField = new CThostFtdcReqUserLoginField();
	strcpy_s(loginField->BrokerID, TD_BROKER_ID);
	strcpy_s(loginField->UserID, INVESTOR_ID);
	strcpy_s(loginField->Password, INVESTOR_ID);
	TdHolder::getInstance()->tdApi->ReqUserLogin(loginField, 0);
}

///��¼������Ӧ
void CThostFtdcTraderSpi::OnRspUserLogin(CThostFtdcRspUserLoginField *pRspUserLogin,
	CThostFtdcRspInfoField *pRspInfo, int nRequestID, bool bIsLast){
	TdHolder* holder = TdHolder::getInstance();
	cout << "��¼����ص�OnRspUserLogin" << endl;
	cout << pRspInfo->ErrorID << " " << pRspInfo->ErrorMsg << endl;
	cout << "ǰ�ñ��:" << pRspUserLogin->FrontID << endl
		<< "�Ự���" << pRspUserLogin->SessionID << endl
		<< "��󱨵�����:" << pRspUserLogin->MaxOrderRef << endl
		<< "������ʱ�䣺" << pRspUserLogin->SHFETime << endl
		<< "������ʱ�䣺" << pRspUserLogin->DCETime << endl
		<< "֣����ʱ�䣺" << pRspUserLogin->CZCETime << endl
		<< "�н���ʱ�䣺" << pRspUserLogin->FFEXTime << endl
		<< "�����գ�" << holder->tdApi->GetTradingDay() << endl;
	holder->tradingDate = holder->tdApi->GetTradingDay();//���ý�������
	cout << "--------------------------------------------" << endl << endl;

	//��ѯ�Ƿ��Ѿ�����ȷ��
	CThostFtdcQrySettlementInfoConfirmField *isConfirm = new CThostFtdcQrySettlementInfoConfirmField();
	strcpy(isConfirm->BrokerID, TD_BROKER_ID);
	strcpy(isConfirm->InvestorID, INVESTOR_ID);
	holder->tdApi->ReqQrySettlementInfoConfirm(isConfirm, 0);
}

//�����ѯ������Ϣȷ����Ӧ
void CThostFtdcTraderSpi::OnRspQrySettlementInfoConfirm(CThostFtdcSettlementInfoConfirmField *pSettlementInfoConfirm,
	CThostFtdcRspInfoField *pRspInfo, int nRequestID, bool bIsLast){
	if (pRspInfo == nullptr || pRspInfo->ErrorID == 0){
		TdHolder * holder = TdHolder::getInstance();
		cout << pSettlementInfoConfirm->ConfirmDate << endl;
		cout << pSettlementInfoConfirm->ConfirmTime << endl;
		string lastConfirmDate = pSettlementInfoConfirm->ConfirmDate;
		if (lastConfirmDate != TdHolder::getInstance()->tradingDate){
			//���컹ûȷ��,��һ�η��ͽ���ָ��ǰ����ѯͶ���߽�����
			CThostFtdcQrySettlementInfoField *a = new CThostFtdcQrySettlementInfoField();
			strcpy_s(a->BrokerID, TD_BROKER_ID);
			strcpy_s(a->InvestorID, INVESTOR_ID);
			strcpy_s(a->TradingDay, lastConfirmDate.c_str());
			holder->tdApi->ReqQrySettlementInfo(a, 1);
		}
		else{
			//�����Ѿ�ȷ��
			CThostFtdcQryTradingAccountField *account = new CThostFtdcQryTradingAccountField();
			strcpy_s(account->BrokerID, TD_BROKER_ID);
			strcpy_s(account->InvestorID, INVESTOR_ID);
			holder->tdApi->ReqQryTradingAccount(account, 999);
		}
	}
}

//�����ѯͶ���߽�������Ӧ
void CThostFtdcTraderSpi::OnRspQrySettlementInfo(CThostFtdcSettlementInfoField *pSettlementInfo,
	CThostFtdcRspInfoField *pRspInfo, int nRequestID, bool bIsLast){
	cout <<"settle content: "<<endl<< pSettlementInfo->Content << endl;
	TdHolder * holder = TdHolder::getInstance();
	if (bIsLast == true){
		//ȷ��Ͷ���߽�����
		CThostFtdcSettlementInfoConfirmField *a = new CThostFtdcSettlementInfoConfirmField();
		strcpy_s(a->BrokerID, TD_BROKER_ID);
		strcpy_s(a->InvestorID, INVESTOR_ID);
		int result = holder->tdApi->ReqSettlementInfoConfirm(a, 2);
		cout << "result:" << result << endl;
	}
}

//Ͷ���߽�����ȷ����Ӧ
void CThostFtdcTraderSpi::OnRspSettlementInfoConfirm(CThostFtdcSettlementInfoConfirmField *pSettlementInfoConfirm,
	CThostFtdcRspInfoField *pRspInfo, int nRequestID, bool bIsLast){
	cout << endl << "OnRspSettlementInfoConfirm, ID: " << nRequestID << endl;
	TdHolder *holder = TdHolder::getInstance();
	if (pRspInfo != nullptr){
		cout << pRspInfo->ErrorID << ends << pRspInfo->ErrorMsg << endl;
	}
	cout << "���͹�˾����:" << pSettlementInfoConfirm->BrokerID << endl
		<< "�û��˺�:" << pSettlementInfoConfirm->InvestorID << endl
		<< "ȷ�����ڣ�" << pSettlementInfoConfirm->ConfirmDate << endl
		<< "ȷ��ʱ�䣺" << pSettlementInfoConfirm->ConfirmTime << endl;

	CThostFtdcQryTradingAccountField *account = new CThostFtdcQryTradingAccountField();
	strcpy_s(account->BrokerID, TD_BROKER_ID);
	strcpy_s(account->InvestorID, INVESTOR_ID);
	holder->tdApi->ReqQryTradingAccount(account, 999);
}

//��ѯ�ʽ��ʻ���Ӧ
void CThostFtdcTraderSpi::OnRspQryTradingAccount(CThostFtdcTradingAccountField *pTradingAccount,
	CThostFtdcRspInfoField *pRspInfo, int nRequestID, bool bIsLast){
	cout << "hey!\n";
	if (pRspInfo == nullptr || pRspInfo->ErrorID == 0){
		cout << "nRequestID: " << nRequestID << endl;
		cout << "�����ʽ�" << pTradingAccount->Available << endl;
	}
}

void CThostFtdcTraderSpi::OnRspOrderInsert(CThostFtdcInputOrderField *pInputOrder, CThostFtdcRspInfoField *pRspInfo, int nRequestID, bool bIsLast){
	sendErrOrder(pInputOrder, pRspInfo);
}

void CThostFtdcTraderSpi::OnErrRtnOrderInsert(CThostFtdcInputOrderField *pInputOrder, CThostFtdcRspInfoField *pRspInfo){
	sendErrOrder(pInputOrder, pRspInfo);
}

void CThostFtdcTraderSpi::OnRtnOrder(CThostFtdcOrderField *pOrder){
	//TODO:
}

void CThostFtdcTraderSpi::OnRtnTrade(CThostFtdcTradeField *pTrade){
	//TODO:
}

void sendErrOrder(CThostFtdcInputOrderField *pInputOrder, CThostFtdcRspInfoField *pRspInfo){
	if (0 != pRspInfo->ErrorID){
		//error during order insert, send msg to queue
		string message = mergeJson("inputOrder", getJsonFromInputOrder(pInputOrder), "rspInfo", getJsonFromRspInfo(pRspInfo));
		GatewayManager* gm = GatewayManager::getInstance();
		gm->sendTextMessage(message, TOPIC_TD);
	}
}

string getJsonFromInputOrder(CThostFtdcInputOrderField *pInputOrder){
	return "{BrokerID:" + string(pInputOrder->BrokerID) + ",InvestorID:" + string(pInputOrder->InvestorID) + ",InstrumentID:" + string(pInputOrder->InstrumentID) +
	",OrderRef:" + string(pInputOrder->OrderRef) + ",UserID:" + string(pInputOrder->UserID) + ",OrderPriceType:" + stringify(pInputOrder->OrderPriceType) +
	",Direction:" + stringify(pInputOrder->Direction) + ",CombOffsetFlag:" + string(pInputOrder->CombOffsetFlag) + ",CombHedgeFlag:" + string(pInputOrder->CombHedgeFlag) +
	",LimitPrice:" + stringify(pInputOrder->LimitPrice) + ",VolumeTotalOriginal:" + stringify(pInputOrder->VolumeTotalOriginal) + ",TimeCondition:" + stringify(pInputOrder->TimeCondition) +
	",GTDDate:" + string(pInputOrder->GTDDate) + ",VolumeCondition:" + stringify(pInputOrder->VolumeCondition) + ",MinVolume:" + stringify(pInputOrder->MinVolume) +
	",ContingentCondition:" + stringify(pInputOrder->ContingentCondition) + ",StopPrice:" + stringify(pInputOrder->StopPrice) + ",ForceCloseReason:" + stringify(pInputOrder->ForceCloseReason) +
	",IsAutoSuspend:" + stringify(pInputOrder->IsAutoSuspend) + ",BusinessUnit:" + string(pInputOrder->BusinessUnit) + ",RequestID:" + stringify(pInputOrder->RequestID) +
	",UserForceClose:" + stringify(pInputOrder->UserForceClose) + ",IsSwapOrder:" + stringify(pInputOrder->IsSwapOrder) + ",ExchangeID:" + string(pInputOrder->ExchangeID) +
	",InvestUnitID:" + string(pInputOrder->InvestUnitID) + ",AccountID:" + string(pInputOrder->AccountID) + ",CurrencyID:" + string(pInputOrder->CurrencyID) +
	",ClientID:" + string(pInputOrder->ClientID) + ",IPAddress:" + string(pInputOrder->IPAddress) + ",MacAddress:" + string(pInputOrder->MacAddress) + "}";
}

string getJsonFromRspInfo(CThostFtdcRspInfoField *pRspInfo){
	return "{ErrorID:" + stringify(pRspInfo->ErrorID) + ",ErrorMsg:" + string(pRspInfo->ErrorMsg)+"}";
}

string mergeJson(string name1, string json1, string name2, string json2){
	return "{" + name1 + ":" + json1 + "," + name2 + ":" + json2 + "}";
}

string stringify(double x)
{
	std::ostringstream o;
	o << x;
	return o.str();
}