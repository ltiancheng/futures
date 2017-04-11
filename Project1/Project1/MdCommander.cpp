#include "MdCommander.h"
#include "MdHolder.h"
#include "ThostFtdcUserApiStruct.h"
#include "ThostFtdcMdApi.h"
#include <iostream>
using namespace std;

MdCommander::MdCommander(MdHolder * mdHolder){
	this->mdHolder = mdHolder;
}

void MdCommander::start(void * para){
	while (true){

	}
}