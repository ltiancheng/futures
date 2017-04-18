#ifndef CONFIG_H
#define CONFIG_H

#define COMMAND_SUBSCRIBE "SUB"		//subscribe a contract
#define COMMAND_UNSUBSCRIBE "DRP"	//un-subscribe a contract;
#define SEPERATER ','				//contract/command seperater: A1705,M1705
#define BROKER_URL "tcp://localhost:61618"
#define TOPIC_MD "topic.marketdata"
#define QUEUE_MD_COMMAND "queue.maketdata.cmd"
#define MD_SERVER_URL "tcp://180.168.146.187:10031"
#define MD_BROKER_ID "9999"
#define INVESTOR_ID "089058"
#define INVESTOR_PWD "058089"
#define TD_SERVER_URL "tcp://180.168.146.187:10030"
#define TD_BROKER_ID "9999"
#define TOPIC_TD "topic.tradedata"
#define TOPIC_TD_ERR "topic.trade.err"

#define MARKET_PRICE "M"
#define LIMIT_PRICE "L"
#define OPEN_SHORT "OS"
#define OPEN_LONG "OL"
#define CLOSE_SHORT "CS"
#define CLOSE_LONG "CL"

#endif