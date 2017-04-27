#include "ThostFtdcMdApi.h"
#include "ThostFtdcUserApiStruct.h"
#include "TdHolder.h"
#include <iostream>

TdHolder* TdHolder::instance = nullptr;
void TdHolder::initHolder(){
	if (this->tdApi != nullptr){
		this->destroyHolder();
	}
	tdApi = CThostFtdcTraderApi::CreateFtdcTraderApi("./tdfiles");
	tdSpi = new MyTdSpi();
	tdApi->RegisterSpi(tdSpi);
	tdApi->SubscribePublicTopic(THOST_TERT_RESTART);
	tdApi->SubscribePrivateTopic(THOST_TERT_QUICK);
	tdApi->RegisterFront(serverUrl);
	std::cout << "init tdApi\n";
	tdApi->Init();
}

TdHolder::TdHolder(){}
TdHolder::TdHolder(const TdHolder& holder){}

void TdHolder::destroyHolder(){
	if (this->tdApi != nullptr){
		this->tdApi->Release();
	}
}

void TdHolder::startTdThread(){
	std::cout << "starting td api join thread\n";
	this->tdApi->Join();
}