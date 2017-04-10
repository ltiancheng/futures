#include "ThostFtdcMdApi.h"
#include "ThostFtdcTraderApi.h"
#include "MdSpi.h"
#include "TdSpi.h"
#include <iostream>

#pragma comment(lib,"thostmduserapi.lib")
#pragma comment(lib,"thosttraderapi.lib")

int maint(int argc, char *argv[]){

	CThostFtdcMdApi *mdapi = CThostFtdcMdApi::CreateFtdcMdApi("./mdfiles");
	MdSpi *mdspi = new MdSpi(mdapi);
	//ע���¼��������
	mdapi->RegisterSpi(mdspi);
	//��ǰ�û�����
	mdapi->RegisterFront("tcp://180.168.146.187:10031");
	std::cout << "1\n";
	mdapi->Init();
	std::cout << "2\n";
	mdapi->Join();
	std::cout << "3\n";
	mdapi->Release();
	std::cout << "4\n";
	system("pause");

	//auto tdapi = CThostFtdcTraderApi::CreateFtdcTraderApi("./trader_file/");
	//TdSpi *tdspi = new TdSpi(tdapi);
	////ע���¼��������
	//tdapi->RegisterSpi(tdspi);
	////���Ĺ�������˽����
	//tdapi->SubscribePublicTopic(THOST_TERT_RESTART);
	//tdapi->SubscribePrivateTopic(THOST_TERT_RESTART);
	////ע��ǰ�û�
	////tdapi->RegisterFront("tcp://180.169.75.19:41205");	//ʵ��270338
	//tdapi->RegisterFront("tcp://27.17.62.149:40205");	//ģ��
	//std::cout << "1\n";
	////��ǰ�û�����
	//tdapi->Init();
	//std::cout << "2\n";
	//tdapi->Join();
	//std::cout << "3\n";

	return 0;
}
