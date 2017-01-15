package com.ltc.base.gateway;

import java.util.List;

import com.ltc.base.vo.BarVO;
import com.ltc.base.vo.ContractVO;

public interface ContractAdapter {

	BarVO getCurrentBar(ContractVO c);

	List<BarVO> getBarHist(String key, int barSize);

}
