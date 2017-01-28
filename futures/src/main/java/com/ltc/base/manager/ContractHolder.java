package com.ltc.base.manager;

import java.util.List;

import com.ltc.base.vo.BarVO;
import com.ltc.base.vo.ContractVO;

public interface ContractHolder {

	List<ContractVO> getActiveContractList();

	ContractVO getContractByKey(String contractKey);

	List<BarVO> getBarHist(ContractVO c, int openBarSize);

}
