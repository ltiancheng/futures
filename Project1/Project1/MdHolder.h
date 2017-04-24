#ifndef MDHOLDER_H
#define MDHOLDER_H

#include "ThostFtdcMdApi.h"
#include "ThostFtdcUserApiStruct.h"
#include "Config.h"
#include "MyMdSpi.h"
#include <string>

class MdHolder {
public:
	CThostFtdcMdApi * mdApi;
	MyMdSpi * mdSpi;
	char* serverUrl = MD_SERVER_URL;
	char* brokerId = MD_BROKER_ID;
	void initHolder();
	void destroyHolder();
	void startMdThread();
};
#endif