#ifndef GATEWAY_H
#define GATEWAY_H

#include <activemq/library/ActiveMQCPP.h>
#include <decaf/lang/Thread.h>
#include <decaf/lang/Runnable.h>
#include <decaf/util/concurrent/CountDownLatch.h>
#include <decaf/lang/Integer.h>
#include <decaf/lang/Long.h>
#include <decaf/lang/System.h>
#include <activemq/core/ActiveMQConnectionFactory.h>
#include <activemq/util/Config.h>
#include <cms/Connection.h>
#include <cms/Session.h>
#include <cms/TextMessage.h>
#include <cms/BytesMessage.h>
#include <cms/MapMessage.h>
#include <cms/ExceptionListener.h>
#include <cms/MessageListener.h>
#include <map>
#include <iostream>
#include <string>
#include <memory>
#include "Config.h"

using namespace std;
using namespace cms;

///从queue读取有关MD的命令(目前只有订阅/退订合约行情)，并且执行
class GatewayManager : public ExceptionListener{
public:
	GatewayManager(const string& brokerURI, bool sessionTransacted = false, int waitMillis = 30000);
	virtual ~GatewayManager();
	void close();
	virtual void onException(const CMSException& ex AMQCPP_UNUSED);
	void initConnection();
	void registerListener(MessageListener* listener, const string& destStr, bool useTopic = false);
	void sendTextMessage(const string& message, const string& destStr, bool useTopic = false);
	static GatewayManager& getInstance(){
		if (instance == nullptr){
			instance = new GatewayManager(BROKER_URL);
			instance->initConnection();
		}
		return *instance;
	}
	GatewayManager(const GatewayManager&);
	GatewayManager& operator=(const GatewayManager&);
private:
	static GatewayManager* instance;
	Connection* connection;
	Session* session;
	bool sessionTransacted;
	long waitMillis;
	string brokerURI;
	map<string, Destination*> destinationMap;
	map<string, MessageProducer*> producerMap;
	void cleanup();
	Destination* getDestionation(const string& destStr, bool useTopic = false);
	MessageProducer* getProducer(const string& destStr, bool useTopic = false);
};

#endif