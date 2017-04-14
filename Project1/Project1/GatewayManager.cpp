#include "GatewayManager.h"
#include <iostream>
#include "Config.h"
#include <sstream>
#include <stdlib.h>
#include <stdio.h>
using namespace std;

GatewayManager::GatewayManager(const std::string& brokerURIint, bool sessionTransacted=false, int waitMillis = 30000){
	this->connection = NULL;
	this->session = NULL;
	this->waitMillis = waitMillis;
	this->brokerURI = brokerURI;
	this->sessionTransacted = sessionTransacted;
}

GatewayManager::~GatewayManager(){
	cleanup();
}

void GatewayManager::close(){
	this->cleanup();
}

void GatewayManager::onException(const CMSException& ex AMQCPP_UNUSED) {
	printf("CMS Exception occurred.  Shutting down client.\n");
	ex.printStackTrace();
	exit(1);
}

void GatewayManager::cleanup(){
	if (connection != NULL) {
		try {
			connection->close();
		}
		catch (cms::CMSException& ex) {
			ex.printStackTrace();
		}
	}
	//TODO destroy destination map

	// Destroy resources.
	try {
		delete session;
		session = NULL;
		delete connection;
		connection = NULL;
	}
	catch (CMSException& e) {
		e.printStackTrace();
	}
}

void GatewayManager::initConnection(){
	try{
		// Create a ConnectionFactory
		auto_ptr<ConnectionFactory> connectionFactory(
			ConnectionFactory::createCMSConnectionFactory(brokerURI));

		// Create a Connection
		connection = connectionFactory->createConnection();
		connection->start();
		connection->setExceptionListener(this);

		// Create a Session
		if (this->sessionTransacted == true) {
			session = connection->createSession(Session::SESSION_TRANSACTED);
		}
		else {
			session = connection->createSession(Session::AUTO_ACKNOWLEDGE);
		}
	}
	catch (CMSException& e) {
		e.printStackTrace();
	}
}

GatewayManager* GatewayManager::getInstance(){
	if (instance == nullptr){
		instance = new GatewayManager(BROKER_URL);
		instance->initConnection();
	}
	return instance;
}

void GatewayManager::registerListener(MessageListener* listener, const string& destStr, bool useTopic = false){
	
	// Create the destination (Topic or Queue)
	Destination * destination = this->getDestionation(destStr, useTopic);

	// Create a MessageConsumer from the Session to the Topic or Queue
	MessageConsumer* consumer = session->createConsumer(destination);

	consumer->setMessageListener(listener);
}

Destination* GatewayManager::getDestionation(const string& destStr, bool useTopic = false){
	map<string, Destination*>::iterator dest = this->destinationMap.find(destStr);
	if (dest != destinationMap.end()){
		return dest->second;
	}
	else {
		Destination* destination;
		if (useTopic) {
			destination = session->createTopic(destStr);
		}
		else {
			destination = session->createQueue(destStr);
		}
		destinationMap.insert(pair<string, Destination*>(destStr, destination));
		return destination;
	}
}

MessageProducer* GatewayManager::getProducer(const string& destStr, bool useTopic = false){
	map<string, MessageProducer*>::iterator dest = this->producerMap.find(destStr);
	if (dest != producerMap.end()){
		return dest->second;
	}
	else {
		Destination * dest = this->getDestionation(destStr, useTopic);
		MessageProducer * producer = session->createProducer(dest);
		producer->setDeliveryMode(DeliveryMode::NON_PERSISTENT);
		producerMap.insert(pair<string, MessageProducer*>(destStr, producer));
		return producer;
	}
}

void GatewayManager::sendTextMessage(const string& message, const string& destStr, bool useTopic = false){
	// Create a MessageProducer from the Session to the Topic or Queue
	MessageProducer* producer = this->getProducer(destStr, useTopic);
	std::auto_ptr<TextMessage> jmsMsg(session->createTextMessage(message));
	printf("Sent message %s\n", message);
	producer->send(jmsMsg.get());
}