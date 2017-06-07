package com.ltc.base.gateway.ctp.impl;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.function.BiConsumer;
import java.util.function.Function;
import java.util.stream.Collectors;

import javax.jms.Connection;
import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.MessageConsumer;
import javax.jms.MessageListener;
import javax.jms.Queue;
import javax.jms.Session;
import javax.jms.Topic;

import org.apache.activemq.command.ActiveMQQueue;
import org.apache.activemq.command.ActiveMQTopic;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.jms.core.MessageCreator;

import com.ltc.base.gateway.ctp.CtpManager;
import com.ltc.base.manager.impl.ContractHolderImpl;
import com.ltc.base.vo.BarVO;
import com.ltc.base.vo.CommandVO;
import com.ltc.base.vo.ContractVO;
import com.ltc.strategy.tortoise.utils.StrategyUtils;

public class CtpManagerImpl implements CtpManager {

	private static final Logger logger = LoggerFactory.getLogger(CtpManagerImpl.class);
	
	private static final String SUBSCRIB = "SUB";
	private static final String UNSUBSCRIB = "DRP";
	private static final String SEPERATOR = ",";
	
	private JmsTemplate jmsTemplate;
	private String defaultReplyQueue;
	private String mdCommandQueue;
	private String mdDataTopic;
	private String tdCommandQueue;
	private String tdDataTopic;
	private String tdErrorTopic;
	
	public void setTdDataTopic(String tdDataTopic) {
		this.tdDataTopic = tdDataTopic;
	}

	public void setTdErrorTopic(String tdErrorTopic) {
		this.tdErrorTopic = tdErrorTopic;
	}

	public void setTdCommandQueue(String tdCommandQueue) {
		this.tdCommandQueue = tdCommandQueue;
	}

	public void setMdDataTopic(String mdDataTopic) {
		this.mdDataTopic = mdDataTopic;
	}

	public void setMdCommandQueue(String mdCommandQueue) {
		this.mdCommandQueue = mdCommandQueue;
	}

	public void setDefaultReplyQueue(String defaultReplyQueue) {
		this.defaultReplyQueue = defaultReplyQueue;
	}

	public void setJmsTemplate(JmsTemplate jmsTemplate) {
		this.jmsTemplate = jmsTemplate;
	}

	private Map<String, Queue> fireAndForgetQueueMap = new HashMap<String, Queue>();
	private Map<String, Topic> topicMap = new HashMap<String, Topic>();


	private Queue createFireAndForgetQueue(String queueName) {
		if (fireAndForgetQueueMap.containsKey(queueName)) {
			return fireAndForgetQueueMap.get(queueName);
		}
		Queue q = new ActiveMQQueue(queueName);
		fireAndForgetQueueMap.put(queueName, q);
		return q;
	}
	
	private Topic createTopic(String topicName) {
		if (topicMap.containsKey(topicName)) {
			return topicMap.get(topicName);
		}
		Topic t = new ActiveMQTopic(topicName);
		topicMap.put(topicName, t);
		return t;
	}

	private MessageCreator buildMessageCreator(final String text, final String replyQueue, final Properties properties) {
		return new MessageCreator() {

			@Override
			public Message createMessage(Session session) throws JMSException {
				final Message message = session.createTextMessage(text);
				if(properties != null && !properties.isEmpty()){
					properties.forEach(new BiConsumer<Object, Object>() {
						@Override
						public void accept(Object k, Object v) {
							try {
								message.setStringProperty(k.toString(), v.toString());
							} catch (JMSException e) {
								logger.error(e.getMessage(), e);
							}
						}
					});
				}
				message.setJMSReplyTo(new ActiveMQQueue(replyQueue));
				return message;
			}
		};
	}
	
	private Connection createConnection() throws JMSException{
		return this.jmsTemplate.getConnectionFactory().createConnection();
	}
	
	private Session createSession(Connection connection) throws JMSException{
		return connection.createSession(false, Session.AUTO_ACKNOWLEDGE);
	}
	
	private MessageCreator buildMessageCreator(String text, Properties properties) {
		return this.buildMessageCreator(text, this.defaultReplyQueue, properties);
	}

	@Override
	public void sendToQueue(String message, String queueName, Properties properties) {
		Queue destination = this.createFireAndForgetQueue(queueName);
		this.jmsTemplate.send(destination, this.buildMessageCreator(message, properties));
	}

	@Override
	public void sendToTopic(String message, String topicName, Properties properties) {
		Topic topic = this.createTopic(topicName);
		this.jmsTemplate.send(topic, this.buildMessageCreator(message, properties));
	}

	@Override
	public void registerQueueListener(MessageListener listener, String queueName, String messageSelector) throws JMSException {
		Connection connection = this.createConnection();
		Session session = this.createSession(connection);
		MessageConsumer consumer = null;
		if(StringUtils.isNotBlank(messageSelector)){
			consumer = session.createConsumer(this.createFireAndForgetQueue(queueName), messageSelector);
		} else {
			consumer = session.createConsumer(this.createFireAndForgetQueue(queueName));
		}
		consumer.setMessageListener(listener);
		connection.start();
	}

	@Override
	public void registerTopicListener(MessageListener listener, String topicName, String messageSelector) throws JMSException {
		Connection connection = this.createConnection();
		Session session = this.createSession(connection);
		MessageConsumer consumer = null;
		if(StringUtils.isNotBlank(messageSelector)){
			consumer = session.createConsumer(this.createTopic(topicName), messageSelector);
		} else {
			consumer = session.createConsumer(this.createTopic(topicName));
		}
        consumer.setMessageListener(listener);
        connection.start();
	}
	
	@Override
	public void registContracts(List<ContractVO> contractList) {
		StringBuffer command = new StringBuffer(SUBSCRIB+" ");
		command.append(contractList.stream().map(new Function<ContractVO, String>(){
			@Override
			public String apply(ContractVO c) {
				return c.getCtpKey();
			}}).collect(Collectors.joining(SEPERATOR)));
		sendToQueue(command.toString(), this.mdCommandQueue, null);
	}

	@Override
	public void registerMarketListener(MessageListener listener) {
		while(true){
			try {
				this.registerTopicListener(listener, this.mdDataTopic, null);
				break;
			} catch (JMSException e) {
				logger.warn("register market listener failed, retrying in 30 secs", e);
				try {
					Thread.sleep(30*1000);
				} catch (InterruptedException e1) {
					logger.error(e1.getMessage(), e1);
				}
			}
		}
	}

	@Override
	public void sendTradeCommand(ContractVO contract, CommandVO command) {
		String priceStyle = command.getPriceStyle();
		BigDecimal price = command.getPrice();
		if(StringUtils.equals(priceStyle, CommandVO.MARKET)){
			priceStyle = CommandVO.LIMIT;
			BarVO currentBar = ContractHolderImpl.getInstance().getContractByKey(contract.getKey()).getCurrentBar();
			if(currentBar != null){
				if(StringUtils.equals(command.getInstruction(), CommandVO.OPEN_LONG) || StringUtils.equals(command.getInstruction(), CommandVO.CLOSE_SHORT)){
					price = new BigDecimal(currentBar.getTopPrice());
					price = StrategyUtils.trimPrice(price, true);
				} else {
					price = new BigDecimal(currentBar.getBottomPrice());
					price = StrategyUtils.trimPrice(price, false);
				}
			}
		}
		String commandLine = String.join(SEPERATOR, command.getInstruction(), 
				String.valueOf(command.getHandPerUnit()*command.getUnits()), contract.getCtpKey(), 
				price.toString(), priceStyle);
		this.sendToQueue(commandLine, this.tdCommandQueue, null);
	}

	@Override
	public void registerCommandListener(MessageListener succCommandListener, MessageListener errCommandListener) {
		while(true){
			try {
				this.registerTopicListener(succCommandListener, this.tdDataTopic, null);
				break;
			} catch (JMSException e) {
				logger.warn("register command listener failed, retrying in 30 secs", e);
				try {
					Thread.sleep(30*1000);
				} catch (InterruptedException e1) {
					logger.error(e1.getMessage(), e1);
				}
			}
		}
		while(true){
			try {
				this.registerTopicListener(errCommandListener, this.tdErrorTopic, null);
				break;
			} catch (JMSException e) {
				logger.warn("register error command listener failed, retrying in 30 secs", e);
				try {
					Thread.sleep(30*1000);
				} catch (InterruptedException e1) {
					logger.error(e1.getMessage(), e1);
				}
			}
		}
		
	}

}
