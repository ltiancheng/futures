package com.ltc.base.helpers;

import com.ltc.base.gateway.vo.HexunBarVO;
import com.ltc.base.vo.BarVO;

public class Converter {

	public static BarVO fromHxBar2BarVO(HexunBarVO hxBar) {
		BarVO bar = new BarVO();
		if(hxBar != null){
			bar.setBarDate(hxBar.getDate());
			bar.setClosePrice(hxBar.getClose());
			bar.setHighPrice(hxBar.getHigh());
			bar.setLowPrice(hxBar.getLow());
			bar.setOpenPrice(hxBar.getOpen());
			bar.setVolume(hxBar.getVolume());
			bar.setAmount(hxBar.getAmount());
		}
		return bar;
	}

}
