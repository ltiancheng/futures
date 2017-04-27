#include "MyTdSpi.h"
#include "ThostFtdcUserApiStruct.h"
#include "ThostFtdcTraderApi.h"
#include "TdHolder.h"
#include "GatewayManager.h"
#include "Config.h"
#include <iostream>
#include <sstream>
using namespace std;


MyTdSpi::MyTdSpi()
{
}


MyTdSpi::~MyTdSpi()
{
}

//链接成功自动登录
void MyTdSpi::OnFrontConnected(){
	cout << "Td连接成功" << endl;
	cout << "请求登陆\n";
	CThostFtdcReqUserLoginField * loginField = new CThostFtdcReqUserLoginField();
	strcpy_s(loginField->BrokerID, TD_BROKER_ID);
	strcpy_s(loginField->UserID, INVESTOR_ID);
	strcpy_s(loginField->Password, INVESTOR_PWD);
	TdHolder::getInstance().tdApi->ReqUserLogin(loginField, 0);
}

///登录请求响应
void MyTdSpi::OnRspUserLogin(CThostFtdcRspUserLoginField *pRspUserLogin,
	CThostFtdcRspInfoField *pRspInfo, int nRequestID, bool bIsLast){
	TdHolder* holder = &TdHolder::getInstance();
	cout << "登录请求回调OnRspUserLogin" << endl;
	cout << pRspInfo->ErrorID << " " << pRspInfo->ErrorMsg << endl;
	cout << "前置编号:" << pRspUserLogin->FrontID << endl
		<< "会话编号" << pRspUserLogin->SessionID << endl
		<< "最大报单引用:" << pRspUserLogin->MaxOrderRef << endl
		<< "上期所时间：" << pRspUserLogin->SHFETime << endl
		<< "大商所时间：" << pRspUserLogin->DCETime << endl
		<< "郑商所时间：" << pRspUserLogin->CZCETime << endl
		<< "中金所时间：" << pRspUserLogin->FFEXTime << endl
		<< "交易日：" << holder->tdApi->GetTradingDay() << endl;
	holder->tradingDate = holder->tdApi->GetTradingDay();//设置交易日期
	cout << "--------------------------------------------" << endl << endl;

	//查询是否已经做了确认
	CThostFtdcQrySettlementInfoConfirmField isConfirm = {0};
	strcpy(isConfirm.BrokerID, TD_BROKER_ID);
	strcpy(isConfirm.InvestorID, INVESTOR_ID);
	holder->tdApi->ReqQrySettlementInfoConfirm(&isConfirm, 1000);
}

//请求查询结算信息确认响应
void MyTdSpi::OnRspQrySettlementInfoConfirm(CThostFtdcSettlementInfoConfirmField *pSettlementInfoConfirm,
	CThostFtdcRspInfoField *pRspInfo, int nRequestID, bool bIsLast){
	if (pSettlementInfoConfirm != NULL && (pRspInfo == nullptr || pRspInfo->ErrorID == 0)){
		TdHolder * holder = &TdHolder::getInstance();
		cout << pSettlementInfoConfirm->ConfirmDate << endl;
		cout << pSettlementInfoConfirm->ConfirmTime << endl;
		string lastConfirmDate = pSettlementInfoConfirm->ConfirmDate;
		if (lastConfirmDate != TdHolder::getInstance().tradingDate){
			//今天还没确定,第一次发送交易指令前，查询投资者结算结果
			CThostFtdcQrySettlementInfoField *a = new CThostFtdcQrySettlementInfoField();
			strcpy_s(a->BrokerID, TD_BROKER_ID);
			strcpy_s(a->InvestorID, INVESTOR_ID);
			strcpy_s(a->TradingDay, lastConfirmDate.c_str());
			holder->tdApi->ReqQrySettlementInfo(a, 1);
		}
		else{
			//今天已经确认
			CThostFtdcQryTradingAccountField *account = new CThostFtdcQryTradingAccountField();
			strcpy_s(account->BrokerID, TD_BROKER_ID);
			strcpy_s(account->InvestorID, INVESTOR_ID);
			holder->tdApi->ReqQryTradingAccount(account, 999);
		}
	}
}

//请求查询投资者结算结果响应
void MyTdSpi::OnRspQrySettlementInfo(CThostFtdcSettlementInfoField *pSettlementInfo,
	CThostFtdcRspInfoField *pRspInfo, int nRequestID, bool bIsLast){
	cout << "settle content: " << endl << pSettlementInfo->Content << endl;
	TdHolder * holder = &TdHolder::getInstance();
	if (bIsLast == true){
		//确认投资者结算结果
		CThostFtdcSettlementInfoConfirmField *a = new CThostFtdcSettlementInfoConfirmField();
		strcpy_s(a->BrokerID, TD_BROKER_ID);
		strcpy_s(a->InvestorID, INVESTOR_ID);
		int result = holder->tdApi->ReqSettlementInfoConfirm(a, 2);
		cout << "result:" << result << endl;
	}
}

//投资者结算结果确认响应
void MyTdSpi::OnRspSettlementInfoConfirm(CThostFtdcSettlementInfoConfirmField *pSettlementInfoConfirm,
	CThostFtdcRspInfoField *pRspInfo, int nRequestID, bool bIsLast){
	cout << endl << "OnRspSettlementInfoConfirm, ID: " << nRequestID << endl;
	TdHolder *holder = &TdHolder::getInstance();
	if (pRspInfo != nullptr){
		cout << pRspInfo->ErrorID << ends << pRspInfo->ErrorMsg << endl;
	}
	cout << "经纪公司代码:" << pSettlementInfoConfirm->BrokerID << endl
		<< "用户账号:" << pSettlementInfoConfirm->InvestorID << endl
		<< "确定日期：" << pSettlementInfoConfirm->ConfirmDate << endl
		<< "确定时间：" << pSettlementInfoConfirm->ConfirmTime << endl;

	CThostFtdcQryTradingAccountField *account = new CThostFtdcQryTradingAccountField();
	strcpy_s(account->BrokerID, TD_BROKER_ID);
	strcpy_s(account->InvestorID, INVESTOR_ID);
	holder->tdApi->ReqQryTradingAccount(account, 999);
}

//查询资金帐户响应
void MyTdSpi::OnRspQryTradingAccount(CThostFtdcTradingAccountField *pTradingAccount,
	CThostFtdcRspInfoField *pRspInfo, int nRequestID, bool bIsLast){
	cout << "hey!\n";
	if (pRspInfo == nullptr || pRspInfo->ErrorID == 0){
		cout << "nRequestID: " << nRequestID << endl;
		cout << "可用资金" << pTradingAccount->Available << endl;
	}
}

void MyTdSpi::OnRspOrderInsert(CThostFtdcInputOrderField *pInputOrder, CThostFtdcRspInfoField *pRspInfo, int nRequestID, bool bIsLast){
	sendErrOrder(pInputOrder, pRspInfo);
}

void MyTdSpi::OnErrRtnOrderInsert(CThostFtdcInputOrderField *pInputOrder, CThostFtdcRspInfoField *pRspInfo){
	sendErrOrder(pInputOrder, pRspInfo);
}

void MyTdSpi::OnRtnOrder(CThostFtdcOrderField *pOrder){
	//do nothing, use onRtnTrade;
}

void MyTdSpi::OnRtnTrade(CThostFtdcTradeField *pTrade){
	string message = getJsonFromTrade(pTrade);
	GatewayManager* gm = &GatewayManager::getInstance();
	gm->sendTextMessage(message, TOPIC_TD, true);
}

void MyTdSpi::sendErrOrder(CThostFtdcInputOrderField *pInputOrder, CThostFtdcRspInfoField *pRspInfo){
	if (0 != pRspInfo->ErrorID){
		//error during order insert, send msg to queue
		string message = mergeJson("inputOrder", getJsonFromInputOrder(pInputOrder), "rspInfo", getJsonFromRspInfo(pRspInfo));
		GatewayManager* gm = &GatewayManager::getInstance();
		gm->sendTextMessage(message, TOPIC_TD_ERR, true);
	}
}

string MyTdSpi::getJsonFromTrade(CThostFtdcTradeField *pTrade){
	return "{BrokerID:" + stringify(pTrade->BrokerID) + ",InvestorID:" + stringify(pTrade->InvestorID) + ",InstrumentID:" + stringify(pTrade->InstrumentID) +
		",OrderRef:" + stringify(pTrade->OrderRef) + ",UserID:" + stringify(pTrade->UserID) + ",ExchangeID:" + stringify(pTrade->ExchangeID) + ",TradeID:" + stringify(pTrade->TradeID) +
		",Direction:" + stringify(pTrade->Direction) + ",OrderSysID:" + stringify(pTrade->OrderSysID) + ",ParticipantID:" + stringify(pTrade->ParticipantID) +
		",ClientID:" + stringify(pTrade->ClientID) + ",TradingRole:" + stringify(pTrade->TradingRole) + ",ExchangeInstID:" + stringify(pTrade->ExchangeInstID) +
		",OffsetFlag:" + stringify(pTrade->OffsetFlag) + ",HedgeFlag:" + stringify(pTrade->HedgeFlag) + ",Price:" + stringify(pTrade->Price) + ",Volume:" + stringify(pTrade->Volume) +
		",TradeDate:" + stringify(pTrade->TradeDate) + ",TradeTime:" + stringify(pTrade->TradeTime) + ",TradeType:" + stringify(pTrade->TradeType) + ",PriceSource:" + stringify(pTrade->PriceSource) +
		",TraderID:" + stringify(pTrade->TraderID) + ",OrderLocalID:" + stringify(pTrade->OrderLocalID) + ",ClearingPartID:" + stringify(pTrade->ClearingPartID) +
		",BusinessUnit:" + stringify(pTrade->BusinessUnit) + ",SequenceNo:" + stringify(pTrade->SequenceNo) + ",TradingDay:" + stringify(pTrade->TradingDay) +
		",SettlementID:" + stringify(pTrade->SettlementID) + ",BrokerOrderSeq:" + stringify(pTrade->BrokerOrderSeq) + ",TradeSource:" + stringify(pTrade->TradeSource) + "}";
}

string MyTdSpi::getJsonFromInputOrder(CThostFtdcInputOrderField *pInputOrder){
	return "{BrokerID:" + stringify(pInputOrder->BrokerID) + ",InvestorID:" + stringify(pInputOrder->InvestorID) + ",InstrumentID:" + stringify(pInputOrder->InstrumentID) +
		",OrderRef:" + stringify(pInputOrder->OrderRef) + ",UserID:" + stringify(pInputOrder->UserID) + ",OrderPriceType:" + stringify(pInputOrder->OrderPriceType) +
		",Direction:" + stringify(pInputOrder->Direction) + ",CombOffsetFlag:" + stringify(pInputOrder->CombOffsetFlag) + ",CombHedgeFlag:" + stringify(pInputOrder->CombHedgeFlag) +
		",LimitPrice:" + stringify(pInputOrder->LimitPrice) + ",VolumeTotalOriginal:" + stringify(pInputOrder->VolumeTotalOriginal) + ",TimeCondition:" + stringify(pInputOrder->TimeCondition) +
		",GTDDate:" + stringify(pInputOrder->GTDDate) + ",VolumeCondition:" + stringify(pInputOrder->VolumeCondition) + ",MinVolume:" + stringify(pInputOrder->MinVolume) +
		",ContingentCondition:" + stringify(pInputOrder->ContingentCondition) + ",StopPrice:" + stringify(pInputOrder->StopPrice) + ",ForceCloseReason:" + stringify(pInputOrder->ForceCloseReason) +
		",IsAutoSuspend:" + stringify(pInputOrder->IsAutoSuspend) + ",BusinessUnit:" + stringify(pInputOrder->BusinessUnit) + ",RequestID:" + stringify(pInputOrder->RequestID) +
		",UserForceClose:" + stringify(pInputOrder->UserForceClose) + ",IsSwapOrder:" + stringify(pInputOrder->IsSwapOrder) + ",ExchangeID:" + stringify(pInputOrder->ExchangeID) +
		",InvestUnitID:" + stringify(pInputOrder->InvestUnitID) + ",AccountID:" + stringify(pInputOrder->AccountID) + ",CurrencyID:" + stringify(pInputOrder->CurrencyID) +
		",ClientID:" + stringify(pInputOrder->ClientID) + ",IPAddress:" + stringify(pInputOrder->IPAddress) + ",MacAddress:" + stringify(pInputOrder->MacAddress) + "}";
}

string MyTdSpi::getJsonFromRspInfo(CThostFtdcRspInfoField *pRspInfo){
	return "{ErrorID:" + stringify(pRspInfo->ErrorID) + ",ErrorMsg:" + stringify(pRspInfo->ErrorMsg) + "}";
}

string MyTdSpi::mergeJson(string name1, string json1, string name2, string json2){
	return "{" + name1 + ":" + json1 + "," + name2 + ":" + json2 + "}";
}

string MyTdSpi::stringify(double x)
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

string MyTdSpi::stringify(char* x)
{
	return "\""+string(x)+"\"";
}