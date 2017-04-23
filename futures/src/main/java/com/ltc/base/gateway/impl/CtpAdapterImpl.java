package com.ltc.base.gateway.impl;

import java.util.List;

import com.ltc.base.gateway.ContractAdapter;
import com.ltc.base.vo.BarVO;
import com.ltc.base.vo.ContractVO;

public class CtpAdapterImpl implements ContractAdapter {

	@Override
	public BarVO getCurrentBar(ContractVO c) {
		return null;
	}

	@Override
	public List<BarVO> getBarHist(ContractVO c, int barSize) {
		return null;
	}

}
