#include "TdCommander.h"
#include "TdHolder.h"
#include "ThostFtdcUserApiStruct.h"
#include "ThostFtdcTraderApi.h"
#include <iostream>
#include "Config.h"
#include <sstream>
using namespace std;

TdCommander::TdCommander(TdHolder * tdHolder, GatewayManager * gatewayManager, const std::string& destStr, bool useTopic){
	this->tdHolder = tdHolder;
	this->useTopic = useTopic;
	this->destStr = destStr;
	this->gatewayManager = gatewayManager;
}

TdCommander::~TdCommander(){
	cleanup();
}

void TdCommander::close(){
	this->cleanup();
}

void TdCommander::registerSelf(){
	try {
		gatewayManager->registerListener(this, this->destStr, this->useTopic);
	}
	catch (CMSException& e) {
		e.printStackTrace();
	}
}

void TdCommander::onMessage(const Message* message) {
	try {
		const TextMessage* textMessage = dynamic_cast<const TextMessage*> (message);
		string text = "";

		if (textMessage != NULL) {
			text = textMessage->getText();
		}
		else {
			text = "NOT A TEXTMESSAGE!";
		}
		printf("Contract Command Received: %s\n", text.c_str());
		int count = 0;
		char ** commandArray = getCommandArray(text, SEPERATER, count);

		CThostFtdcInputOrderField *orderField = new CThostFtdcInputOrderField();
		strcpy(orderField->BrokerID, TD_BROKER_ID);
		strcpy(orderField->InvestorID, INVESTOR_ID);
		strcpy(orderField->InstrumentID, commandArray[2]);
		if (0 == string(MARKET_PRICE).compare(commandArray[4])){
			orderField->OrderPriceType = THOST_FTDC_OPT_AnyPrice;		//�м�
			orderField->LimitPrice = 0;									//�۸�
		}
		else{
			orderField->OrderPriceType = THOST_FTDC_OPT_LimitPrice;		//�޼�
			orderField->LimitPrice = atof(commandArray[3]);
		}
		if (0 == string(OPEN_LONG).compare(commandArray[0]) || 0 == string(CLOSE_SHORT).compare(commandArray[0])){
			orderField->Direction = THOST_FTDC_D_Buy;					//�� 
		}
		else{
			orderField->Direction = THOST_FTDC_D_Sell;					//��
		}
		if (0 == string(OPEN_LONG).compare(commandArray[0]) || 0 == string(OPEN_SHORT).compare(commandArray[0])){
			orderField->CombOffsetFlag[0] = THOST_FTDC_OF_Open;				//����
		}
		else{
			orderField->CombOffsetFlag[0] = THOST_FTDC_OF_Close;		//���Ի�������ƽ��
		}
		orderField->VolumeTotalOriginal = stoi(commandArray[1]);		//����
		//�����ǹ̶����ֶ�
		orderField->CombHedgeFlag[0] = THOST_FTDC_HF_Speculation;		//Ͷ�� 
		orderField->TimeCondition = THOST_FTDC_TC_GFD;				//������Ч '3'
		orderField->VolumeCondition = THOST_FTDC_VC_AV;				//�κ����� '1'
		orderField->MinVolume = 1;
		orderField->ContingentCondition = THOST_FTDC_CC_Immediately;	//��������'1'
		orderField->ForceCloseReason = THOST_FTDC_FCC_NotForceClose;	//��ǿƽ '0'
		orderField->IsAutoSuspend = 0;
		orderField->UserForceClose = 0;

		this->tdHolder->tdApi->ReqOrderInsert(orderField, 1000);

		delete commandArray;
		delete orderField;
	}
	catch (CMSException& e) {
		e.printStackTrace();
	}
}

char ** TdCommander::getCommandArray(string& command, const char& seperator, int& count){
	count = std::count(command.begin(), command.end(), seperator) + 1;
	char ** commandArray = new char*[count];
	std::istringstream ssin(command);
	string cmd;
	int i = 0;
	while (getline(ssin, cmd, seperator)){
		commandArray[i] = new char[cmd.length() + 1];
		strcpy(commandArray[i], cmd.c_str());
		i++;
	}
	return commandArray;
}

void TdCommander::cleanup(){

}