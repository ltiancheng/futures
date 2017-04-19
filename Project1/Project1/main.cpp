#include "ThostFtdcMdApi.h"
#include "ThostFtdcTraderApi.h"
#include "MdHolder.h"
#include "MdCommander.h"
#include "TdHolder.h"
#include "TdCommander.h"
#include "GatewayManager.h"
#include <iostream>
#include "process.h"
#include "Windows.h"

#pragma comment(lib,"thostmduserapi.lib")
#pragma comment(lib,"thosttraderapi.lib")

MdHolder * mdHolder = nullptr;
TdHolder * tdHolder = nullptr;
GatewayManager * gatewayManager = nullptr;
MdCommander * mdCommander = nullptr;
TdCommander * tdCommander = nullptr;

void startMd(void *para){
	mdHolder->initHolder();
	mdHolder->startMdThread();
}

void startTd(void *para){
	tdHolder->initHolder();
	tdHolder->startTdThread();
}

int main(int argc, char *argv[]){
	///start holders/apis;
	HANDLE mdThread;
	HANDLE tdThread;
	mdHolder = new MdHolder();
	tdHolder = TdHolder::getInstance();
	mdThread = (HANDLE)_beginthread(startMd, 0, NULL);
	tdThread = (HANDLE)_beginthread(startTd, 0, NULL);
	///start command listeners;
	gatewayManager = GatewayManager::getInstance();

	mdCommander = new MdCommander(mdHolder, gatewayManager, QUEUE_MD_COMMAND);
	mdCommander->registerSelf();
	tdCommander = new TdCommander(tdHolder, gatewayManager, QUEUE_TD_COMMAND);
	tdCommander->registerSelf();

	WaitForSingleObject(mdThread, INFINITE);
	std::cout << "Error: MD Thread ends\n";
	return 0;
}
