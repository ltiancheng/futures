#include "ThostFtdcMdApi.h"
#include "ThostFtdcUserApiStruct.h"
#include "MdHolder.h"
#include <iostream>

#pragma comment(lib,"thostmduserapi.lib")

void MdHolder::initHolder(){
	if (this->mdApi != nullptr){
		this->destroyHolder();
	}
	CThostFtdcMdApi *mdapi = CThostFtdcMdApi::CreateFtdcMdApi("./mdfiles");
	CThostFtdcMdSpi *mdspi = new CThostFtdcMdSpi();
	mdapi->RegisterSpi(mdspi);
	mdapi->RegisterFront(serverUrl);
	mdapi->Init();
	std::cout << "init mdapi\n";
	this->mdApi = mdapi;
	this->mdSpi = mdspi;
}

void MdHolder::destroyHolder(){
	if (this->mdApi != nullptr){
		this->mdApi->Release();
	}
}

void MdHolder::startMdThread(){
	std::cout << "starting md api join thread\n";
	this->mdApi->Join();
}