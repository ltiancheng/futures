#ifndef TDCOMMANDER_H
#define TDCOMMANDER_H

#include "ThostFtdcTraderApi.h"
#include "ThostFtdcUserApiStruct.h"
#include "TdHolder.h"
#include "GatewayManager.h"
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
#include <stdlib.h>
#include <stdio.h>
#include <iostream>
#include <memory>

///从queue读取有关Trade的命令(目前只有提交订单)，并且执行
class TdCommander : public MessageListener{
public:
	TdCommander(TdHolder * tdHolder, GatewayManager * gatewayManager, const std::string& destStr, bool useTopic = false);
	virtual ~TdCommander();
	void close();
	void registerSelf();
	///命令格式：OS/OL/CS/CL,4,A1709,3715,M/L(开空/开多/平空/平多,合约数量,合约标识,价格,市/限价)
	virtual void onMessage(const Message* message);
private:
	TdHolder * tdHolder;
	GatewayManager * gatewayManager;
	bool useTopic;
	std::string destStr;
	void cleanup();
	char ** getContractArray(string& command, const char& seperator, int& count);
};

#endif