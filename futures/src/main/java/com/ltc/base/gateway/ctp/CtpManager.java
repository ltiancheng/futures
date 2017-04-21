package com.ltc.base.gateway.ctp;

import java.util.List;
import java.util.Properties;

import javax.jms.JMSException;
import javax.jms.MessageListener;

import com.ltc.base.vo.CommandVO;
import com.ltc.base.vo.ContractVO;

public interface CtpManager {
	void sendToQueue(String message, String queueName, Properties properties);
	void sendToTopic(String message, String topicName, Properties properties);
	void registerQueueListener(MessageListener listener, String queueName, String messageSelector) throws JMSException;
	void registerTopicListener(MessageListener listener, String topicName, String messageSelector) throws JMSException;
	void registContracts(List<ContractVO> contractList);
	void registerMarketListener(MessageListener listener);
	void sendTradeCommand(ContractVO contract, CommandVO command);
	void registerCommandListener(MessageListener succCommandListener, MessageListener errCommandListener);
}
