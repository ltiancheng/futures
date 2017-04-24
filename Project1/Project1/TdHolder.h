#ifndef TDHOLDER_H
#define TDHOLDER_H

#include "ThostFtdcTraderApi.h"
#include "MyTdSpi.h"
#include "ThostFtdcUserApiStruct.h"
#include "Config.h"
#include <string>
using namespace std;

class TdHolder {
public:
	CThostFtdcTraderApi * tdApi;
	MyTdSpi* tdSpi;
	char* serverUrl = TD_SERVER_URL;
	char* brokerId = TD_BROKER_ID;
	char* investorId = INVESTOR_ID;
	char* investorPwd = INVESTOR_PWD;
	string tradingDate;
	void initHolder();
	void destroyHolder();
	void startTdThread();
	static TdHolder& getInstance(){
		if (instance == nullptr){
			instance = new TdHolder();
			instance->initHolder();
		}
		return *instance;
	}
	TdHolder(const TdHolder&);
	TdHolder();
	TdHolder& operator=(const TdHolder&);
private:
	static TdHolder* instance;
};
#endif