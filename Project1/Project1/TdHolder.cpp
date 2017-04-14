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
	tdApi->RegisterFront(serverUrl);
	tdApi->Init();
	std::cout << "init tdapi\n";
}

void TdHolder::destroyHolder(){
	if (this->tdApi != nullptr){
		this->tdApi->Release();
	}
}

void TdHolder::startMdThread(){
	std::cout << "starting md api join thread\n";
	this->mdApi->Join();
}