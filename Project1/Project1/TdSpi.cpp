#include "TdSpi.h"
#include <iostream>
#include <string>
#include <cstring>
#include <chrono>
#include <thread>
using namespace std;

//实盘账号
//const string USER_ID = "83601689";
//const string PASS = "270338";
//const string BROKER = "7090";
string tradingDate;

//模拟账号
const string USER_ID = "00000030";
const string PASS = "123456";
const string BROKER = "1035";

//构造函数
TdSpi::TdSpi(CThostFtdcTraderApi *tdapi){
	this->tdapi = tdapi;
}

//当客户端与交易后台建立起通信连接时（还未登录前），该方法被调用。
void TdSpi::OnFrontConnected(){
	cout << "Td连接成功" << endl;
	cout << "请求登陆\n";
	loginField = new CThostFtdcReqUserLoginField();
	strcpy_s(loginField->BrokerID, BROKER.c_str());
	strcpy_s(loginField->UserID, USER_ID.c_str());
	strcpy_s(loginField->Password, PASS.c_str());
	tdapi->ReqUserLogin(loginField, 0);
}

///登录请求响应
void TdSpi::OnRspUserLogin(CThostFtdcRspUserLoginField *pRspUserLogin,
	CThostFtdcRspInfoField *pRspInfo, int nRequestID, bool bIsLast){
	cout << "登录请求回调OnRspUserLogin" << endl;
	cout << pRspInfo->ErrorID << " " << pRspInfo->ErrorMsg << endl;
	cout << "前置编号:" << pRspUserLogin->FrontID << endl
		<< "会话编号" << pRspUserLogin->SessionID << endl
		<< "最大报单引用:" << pRspUserLogin->MaxOrderRef << endl
		<< "上期所时间：" << pRspUserLogin->SHFETime << endl
		<< "大商所时间：" << pRspUserLogin->DCETime << endl
		<< "郑商所时间：" << pRspUserLogin->CZCETime << endl
		<< "中金所时间：" << pRspUserLogin->FFEXTime << endl
		<< "交易日：" << tdapi->GetTradingDay() << endl;
	tradingDate = tdapi->GetTradingDay();//设置交易日期
	cout << "--------------------------------------------" << endl << endl;

	CThostFtdcQryTradingAccountField *account = new CThostFtdcQryTradingAccountField();
	strcpy_s(account->BrokerID, BROKER.c_str());
	strcpy_s(account->InvestorID, USER_ID.c_str());
	tdapi->ReqQryTradingAccount(account, 999);

	//查询是否已经做了确认
	//CThostFtdcQrySettlementInfoConfirmField *isConfirm = new CThostFtdcQrySettlementInfoConfirmField();
	//strcpy(isConfirm->BrokerID, BROKER.c_str());
	//strcpy(isConfirm->InvestorID, USER_ID.c_str());
	//tdapi->ReqQrySettlementInfoConfirm(isConfirm, 0);
}

//请求查询结算信息确认响应
void TdSpi::OnRspQrySettlementInfoConfirm(CThostFtdcSettlementInfoConfirmField *pSettlementInfoConfirm,
	CThostFtdcRspInfoField *pRspInfo, int nRequestID, bool bIsLast){
	if (pRspInfo == nullptr || pRspInfo->ErrorID == 0){
		cout << pSettlementInfoConfirm->ConfirmDate << endl;
		cout << pSettlementInfoConfirm->ConfirmTime << endl;
		string lastConfirmDate = pSettlementInfoConfirm->ConfirmDate;
		if (lastConfirmDate != tradingDate){
			//今天还没确定,第一次发送交易指令前，查询投资者结算结果
			CThostFtdcQrySettlementInfoField *a = new CThostFtdcQrySettlementInfoField();
			strcpy_s(a->BrokerID, BROKER.c_str());
			strcpy_s(a->InvestorID, USER_ID.c_str());
			strcpy_s(a->TradingDay, lastConfirmDate.c_str());

			std::chrono::milliseconds sleepDuration(1 * 1000);
			std::this_thread::sleep_for(sleepDuration);
			tdapi->ReqQrySettlementInfo(a, 1);
		}else{
			//今天已经确认
			CThostFtdcQryTradingAccountField *account = new CThostFtdcQryTradingAccountField();
			strcpy_s(account->BrokerID, BROKER.c_str());
			strcpy_s(account->InvestorID, USER_ID.c_str());
			tdapi->ReqQryTradingAccount(account, 999);
		}
	}
}

//请求查询投资者结算结果响应
void TdSpi::OnRspQrySettlementInfo(CThostFtdcSettlementInfoField *pSettlementInfo,
	CThostFtdcRspInfoField *pRspInfo, int nRequestID, bool bIsLast){
	cout << pSettlementInfo->Content << endl;

	if (bIsLast == true){
		//确认投资者结算结果
		CThostFtdcSettlementInfoConfirmField *a = new CThostFtdcSettlementInfoConfirmField();
		strcpy_s(a->BrokerID, BROKER.c_str());
		strcpy_s(a->InvestorID, USER_ID.c_str());
		int result = tdapi->ReqSettlementInfoConfirm(a, 2);
		cout << "result:" << result << endl;
	}
}


//投资者结算结果确认响应
void TdSpi::OnRspSettlementInfoConfirm(CThostFtdcSettlementInfoConfirmField *pSettlementInfoConfirm,
	CThostFtdcRspInfoField *pRspInfo, int nRequestID, bool bIsLast){
	cout << endl << "OnRspSettlementInfoConfirm, ID: " << nRequestID << endl;
	if (pRspInfo != nullptr){
		cout << pRspInfo->ErrorID << ends << pRspInfo->ErrorMsg << endl;
	}
	cout << "经纪公司代码:" << pSettlementInfoConfirm->BrokerID << endl
		<< "用户账号:" << pSettlementInfoConfirm->InvestorID << endl
		<< "确定日期：" << pSettlementInfoConfirm->ConfirmDate << endl
		<< "确定时间：" << pSettlementInfoConfirm->ConfirmTime << endl;

	CThostFtdcQryTradingAccountField *account = new CThostFtdcQryTradingAccountField();
	strcpy_s(account->BrokerID, BROKER.c_str());
	strcpy_s(account->InvestorID, USER_ID.c_str());
	tdapi->ReqQryTradingAccount(account, 999);

	//查询请求查询成交
	//std::chrono::milliseconds sleepDuration(5 * 1000);
	//std::this_thread::sleep_for(sleepDuration);
	//CThostFtdcQryTradeField *a = new CThostFtdcQryTradeField();
	//strcpy(a->BrokerID, BROKER.c_str());
	//strcpy(a->InvestorID, USER_ID.c_str());
	//strcpy(a->InstrumentID, "cu1409");
	//strcpy(a->TradeTimeStart, "20140101");
	//strcpy(a->TradeTimeEnd, "20140720");
	//tdapi->ReqQryTrade(a, 10);

	//请求查询投资者持仓明细
	//std::chrono::milliseconds sleepDuration(1 * 1000);
	//std::this_thread::sleep_for(sleepDuration);
	//CThostFtdcQryInvestorPositionField *a = new CThostFtdcQryInvestorPositionField();
	//strcpy(a->BrokerID, BROKER.c_str());
	//strcpy(a->InvestorID, USER_ID.c_str());
	//strcpy(a->InstrumentID, "");
	//int result = tdapi->ReqQryInvestorPosition(a, 10);
	//cout << result << endl;

	////休息两秒再发
	//std::chrono::milliseconds sleepDuration(1*1000);
	//std::this_thread::sleep_for(sleepDuration);
	//cout << "X.X" << endl;
	//int result=tdapi->ReqQryInvestorPosition(a, 3);
	//cout << "result:" << result << endl;
}

//请求查询投资者持仓响应
void TdSpi::OnRspQryInvestorPosition(CThostFtdcInvestorPositionField *pInvestorPosition,
	CThostFtdcRspInfoField *pRspInfo, int nRequestID, bool bIsLast){
	cout << "OnRspQryInvestorPosition  ID: " << nRequestID << endl;
	cout << "错误代码：" << pRspInfo->ErrorID << "错误信息:" << pRspInfo->ErrorMsg;
	cout << "持仓多空方向:" << pInvestorPosition->PosiDirection << endl;
	if (bIsLast){
		cout << "last\n";
	}
}

///请求查询成交响应
void TdSpi::OnRspQryTrade(CThostFtdcTradeField *pTrade,
	CThostFtdcRspInfoField *pRspInfo, int nRequestID, bool bIsLast){
	if (pRspInfo == nullptr || pRspInfo->ErrorID == 0){
		cout << pTrade->BrokerID << endl
			<< pTrade->BrokerOrderSeq << endl;
	}
}

///登出请求响应
void TdSpi::OnRspUserLogout(CThostFtdcUserLogoutField *pUserLogout,
	CThostFtdcRspInfoField *pRspInfo, int nRequestID, bool bIsLast){
}

///用户口令更新请求响应
void TdSpi::OnRspUserPasswordUpdate(CThostFtdcUserPasswordUpdateField *pUserPasswordUpdate,
	CThostFtdcRspInfoField *pRspInfo, int nRequestID, bool bIsLast){
	cout << "回调用户口令更新请求响应OnRspUserPasswordUpdate" << endl;
	if (pRspInfo->ErrorID == 0){
		cout << "更改成功 " << endl
			<< "旧密码为:" << pUserPasswordUpdate->OldPassword << endl
			<< "新密码为:" << pUserPasswordUpdate->NewPassword << endl;
	}
	else{
		cout << pRspInfo->ErrorID << ends << pRspInfo->ErrorMsg << endl;
	}
}

///请求查询行情响应
void TdSpi::OnRspQryDepthMarketData(CThostFtdcDepthMarketDataField *pDepthMarketData,
	CThostFtdcRspInfoField *pRspInfo, int nRequestID, bool bIsLast){
	cout << "OnRspQryDepthMarketData" << endl;
	cout << nRequestID << endl;
	if (pDepthMarketData != nullptr){
		cout << "-----------------行情数据--------------------" << endl;
		cout << "交易日:" << pDepthMarketData->TradingDay << endl
			<< "合约代码:" << pDepthMarketData->InstrumentID << endl
			<< "最新价:" << pDepthMarketData->LastPrice << endl
			<< "最高价:" << pDepthMarketData->HighestPrice << endl
			<< "最低价:" << pDepthMarketData->LowestPrice << endl;
		cout << "-----------------行情数据--------------------" << endl;
	}
}

//查询资金帐户响应
void TdSpi::OnRspQryTradingAccount(CThostFtdcTradingAccountField *pTradingAccount,
	CThostFtdcRspInfoField *pRspInfo, int nRequestID, bool bIsLast){
	cout << "hey!\n";
	if (pRspInfo == nullptr || pRspInfo->ErrorID == 0){
		cout << "nRequestID: " << nRequestID << endl;
		cout << "可用资金" << pTradingAccount->Available << endl;
	}
}