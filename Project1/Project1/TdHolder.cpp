#include "ThostFtdcMdApi.h"
#include "ThostFtdcUserApiStruct.h"
#include "TdHolder.h"
#include <iostream>

void TdHolder::initHolder(){
	if (this->tdApi != nullptr){
		this->destroyHolder();
	}
	tdApi = CThostFtdcTraderApi::CreateFtdcTraderApi("./tdfiles");
	tdSpi = new CThostFtdcTraderSpi();
	tdApi->RegisterSpi(tdSpi);
	tdApi->SubscribePublicTopic(THOST_TERT_RESTART);
	tdApi->SubscribePrivateTopic(THOST_TERT_QUICK);
	tdApi->RegisterFront(serverUrl);
	tdApi->Init();
	std::cout << "init tdApi\n";
}

TdHolder* TdHolder::getInstance(){
	if (instance == nullptr){
		instance = new TdHolder();
		instance->initHolder();
	}
	return instance;
}

void TdHolder::destroyHolder(){
	if (this->tdApi != nullptr){
		this->tdApi->Release();
	}
}

void TdHolder::startTdThread(){
	std::cout << "starting td api join thread\n";
	this->tdApi->Join();
}