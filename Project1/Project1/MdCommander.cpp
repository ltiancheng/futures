#include "MdCommander.h"
#include "MdHolder.h"
#include "ThostFtdcUserApiStruct.h"
#include "ThostFtdcMdApi.h"
#include <iostream>
#include "Config.h"
#include <sstream>
#include <cms/MessageListener.h>
using namespace std;

MdCommander::MdCommander(MdHolder * mdHolder, GatewayManager * gatewayManager, const std::string& destStr, bool useTopic){
	this->mdHolder = mdHolder;
	this->useTopic = useTopic;
	this->destStr = destStr;
	this->gatewayManager = gatewayManager;
}

MdCommander::~MdCommander(){
	cleanup();
}

void MdCommander::close(){
	this->cleanup();
}

void MdCommander::registerSelf(){
	try {
		gatewayManager->registerListener(this, this->destStr, this->useTopic);
	} catch (CMSException& e) {
		e.printStackTrace();
	}
}

void MdCommander::onMessage(const Message* message) {
	try {
		const TextMessage* textMessage = dynamic_cast<const TextMessage*> (message);
		string text = "";

		if (textMessage != NULL) {
			text = textMessage->getText();
		} else {
			text = "NOT A TEXTMESSAGE!";
		}
		printf("Contract Command Received: %s\n", text.c_str());
		if (text.find(COMMAND_SUBSCRIBE) == 0){
			string contracts = text.substr(strlen(COMMAND_SUBSCRIBE) + 1);
			int count = 0;
			char ** contractArray = getContractArray(contracts, SEPERATER, count);
			this->mdHolder->mdApi->SubscribeMarketData(contractArray, count);
			delete contractArray;
		}
		else if (text.find(COMMAND_UNSUBSCRIBE) == 0){
			string contracts = text.substr(strlen(COMMAND_UNSUBSCRIBE) + 1);
			int count = 0;
			char ** contractArray = getContractArray(contracts, SEPERATER, count);
			this->mdHolder->mdApi->UnSubscribeMarketData(contractArray, count);
			delete contractArray;
		}
		else {
			printf("un-recornized command\n");
		}

	} catch (CMSException& e) {
		e.printStackTrace();
	}
}

char ** MdCommander::getContractArray(string& contracts, const char& seperator, int& count){
	count = std::count(contracts.begin(), contracts.end(), seperator) + 1;
	char ** contractArray = new char*[count];
	std::istringstream ssin(contracts);
	string contract;
	int i = 0;
	while (getline(ssin, contract, seperator)){
		contractArray[i] = new char[contract.length() + 1];
		strcpy(contractArray[i], contract.c_str());
		i++;
	}
	return contractArray;
}

void MdCommander::cleanup(){
	
}