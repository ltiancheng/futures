#ifndef MDCOMMANDER_H
#define MDCOMMANDER_H

#include "ThostFtdcMdApi.h"
#include "ThostFtdcUserApiStruct.h"
#include "MdHolder.h"

///��queue��ȡ�й�MD������(Ŀǰֻ�ж��ĺ�Լ����)������ִ��
class MdCommander{
public:
	MdCommander(MdHolder * mdHolder);
	void start(void *para);
private:
	MdHolder * mdHolder;
};

#endif