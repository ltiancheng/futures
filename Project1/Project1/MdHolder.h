#ifndef MDHOLDER_H
#define MDHOLDER_H

#include "ThostFtdcMdApi.h"
#include "ThostFtdcUserApiStruct.h"
#include <string>

using std::string;

class MdHolder {
public:
	CThostFtdcMdApi * mdApi;
	CThostFtdcMdSpi * mdSpi;
	char* serverUrl = "tcp://180.168.146.187:10031";
	char* brokerId = "9999";
	void initHolder();
	void destroyHolder();
	void startMdThread();
};

MdHolder * mdHolder = nullptr;
#endif