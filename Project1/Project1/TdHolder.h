#ifndef TDHOLDER_H
#define TDHOLDER_H

#include "ThostFtdcTraderApi.h"
#include "ThostFtdcUserApiStruct.h"
#include "Config.h"
#include <string>

using std::string;

class TdHolder {
public:
	CThostFtdcTraderApi * tdApi;
	CThostFtdcTraderSpi * tdSpi;
	char* serverUrl = TD_SERVER_URL;
	char* brokerId = TD_BROKER_ID;
	char* investorId = INVESTOR_ID;
	char* investorPwd = INVESTOR_PWD;
	string tradingDate;
	void initHolder();
	void destroyHolder();
	void startTdThread();
	static TdHolder* getInstance();
private:
	static TdHolder* instance;
};
#endif