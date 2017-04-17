#include "ThostFtdcUserApiStruct.h"
#include "ThostFtdcTraderApi.h"
#include "TdHolder.h"
#include "GatewayManager.h"
#include "Config.h"
#include <iostream>
#include <sstream>
using namespace std;

//链接成功自动登录
void CThostFtdcTraderSpi::OnFrontConnected(){
	cout << "Td连接成功" << endl;
	cout << "请求登陆\n";
	CThostFtdcReqUserLoginField * loginField = new CThostFtdcReqUserLoginField();
	strcpy_s(loginField->BrokerID, TD_BROKER_ID);
	strcpy_s(loginField->UserID, INVESTOR_ID);
	strcpy_s(loginField->Password, INVESTOR_ID);
	TdHolder::getInstance()->tdApi->ReqUserLogin(loginField, 0);
}

///登录请求响应
void CThostFtdcTraderSpi::OnRspUserLogin(CThostFtdcRspUserLoginField *pRspUserLogin,
	CThostFtdcRspInfoField *pRspInfo, int nRequestID, bool bIsLast){
	TdHolder* holder = TdHolder::getInstance();
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
	CThostFtdcQrySettlementInfoConfirmField *isConfirm = new CThostFtdcQrySettlementInfoConfirmField();
	strcpy(isConfirm->BrokerID, TD_BROKER_ID);
	strcpy(isConfirm->InvestorID, INVESTOR_ID);
	holder->tdApi->ReqQrySettlementInfoConfirm(isConfirm, 0);
}

//请求查询结算信息确认响应
void CThostFtdcTraderSpi::OnRspQrySettlementInfoConfirm(CThostFtdcSettlementInfoConfirmField *pSettlementInfoConfirm,
	CThostFtdcRspInfoField *pRspInfo, int nRequestID, bool bIsLast){
	if (pRspInfo == nullptr || pRspInfo->ErrorID == 0){
		TdHolder * holder = TdHolder::getInstance();
		cout << pSettlementInfoConfirm->ConfirmDate << endl;
		cout << pSettlementInfoConfirm->ConfirmTime << endl;
		string lastConfirmDate = pSettlementInfoConfirm->ConfirmDate;
		if (lastConfirmDate != TdHolder::getInstance()->tradingDate){
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
void CThostFtdcTraderSpi::OnRspQrySettlementInfo(CThostFtdcSettlementInfoField *pSettlementInfo,
	CThostFtdcRspInfoField *pRspInfo, int nRequestID, bool bIsLast){
	cout <<"settle content: "<<endl<< pSettlementInfo->Content << endl;
	TdHolder * holder = TdHolder::getInstance();
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
void CThostFtdcTraderSpi::OnRspSettlementInfoConfirm(CThostFtdcSettlementInfoConfirmField *pSettlementInfoConfirm,
	CThostFtdcRspInfoField *pRspInfo, int nRequestID, bool bIsLast){
	cout << endl << "OnRspSettlementInfoConfirm, ID: " << nRequestID << endl;
	TdHolder *holder = TdHolder::getInstance();
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
void CThostFtdcTraderSpi::OnRspQryTradingAccount(CThostFtdcTradingAccountField *pTradingAccount,
	CThostFtdcRspInfoField *pRspInfo, int nRequestID, bool bIsLast){
	cout << "hey!\n";
	if (pRspInfo == nullptr || pRspInfo->ErrorID == 0){
		cout << "nRequestID: " << nRequestID << endl;
		cout << "可用资金" << pTradingAccount->Available << endl;
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