#ifndef MDCOMMANDER_H
#define MDCOMMANDER_H

#include "ThostFtdcMdApi.h"
#include "ThostFtdcUserApiStruct.h"
#include "MdHolder.h"

///从queue读取有关MD的命令(目前只有订阅合约行情)，并且执行
class MdCommander{
public:
	MdCommander(MdHolder * mdHolder);
	void start(void *para);
private:
	MdHolder * mdHolder;
};

#endif