#include "ThostFtdcMdApi.h"
#include "ThostFtdcTraderApi.h"
#include "MdHolder.h"
#include <iostream>
#include "process.h"
#include "Windows.h"

#pragma comment(lib,"thostmduserapi.lib")
#pragma comment(lib,"thosttraderapi.lib")

MdHolder * mdHolder = nullptr;

void startMd(void *para){
	mdHolder->initHolder();
	mdHolder->startMdThread();
}

int main(int argc, char *argv[]){

	HANDLE hThread;
	mdHolder = new MdHolder();
	hThread = (HANDLE)_beginthread(startMd, 0, NULL);
	WaitForSingleObject(hThread, INFINITE);
	std::cout << "Error: Thread ends\n";
	return 0;
}
