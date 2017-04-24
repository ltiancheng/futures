#ifndef MDCOMMANDER_H
#define MDCOMMANDER_H

#include "ThostFtdcMdApi.h"
#include "ThostFtdcUserApiStruct.h"
#include "MdHolder.h"
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

using namespace cms;
using namespace decaf::lang;
using namespace decaf::util::concurrent;

///��queue��ȡ�й�MD������(Ŀǰֻ�ж���/�˶���Լ����)������ִ��
class MdCommander : public MessageListener{
public:
	MdCommander(MdHolder * mdHolder, GatewayManager * gatewayManager, const std::string& destStr, bool useTopic = false);
	virtual ~MdCommander();
	void close();
	void registerSelf();
	///�յ���������ʽΪ��SUB/DRP M1705,MA1705,AL1709
	virtual void onMessage(const Message* message);
private:
	MdHolder * mdHolder;
	GatewayManager * gatewayManager;
	bool useTopic;
	std::string destStr;
	void cleanup();
	char ** getContractArray(string& contracts, const char& seperator, int& count);
};

#endif