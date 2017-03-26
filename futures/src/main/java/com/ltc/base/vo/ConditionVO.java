package com.ltc.base.vo;

import java.math.BigDecimal;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ConditionVO {
	
	public ConditionVO(BigDecimal triggerValue, String type, boolean aboveCondition) {
		super();
		this.triggerValue = triggerValue;
		this.type = type;
		this.aboveCondition = aboveCondition;
	}
	public ConditionVO(){}

	@Override
	public String toString() {
		return "IF "+type+" IS "+(aboveCondition?"above":"lower")+" than "+triggerValue.toString();
	}

	private static Logger logger = LoggerFactory.getLogger(ConditionVO.class);
	
	public static final String PRICE_TYPE="P";
	public static final String VOL_TYPE="V";
	
	private BigDecimal triggerValue; 
	private String type;
	private boolean aboveCondition;
	
	public static final ConditionVO TRUE_CONDITION = new ConditionVO(BigDecimal.ZERO, PRICE_TYPE, true);

	public BigDecimal getTriggerValue() {
		return triggerValue;
	}

	public void setTriggerValue(BigDecimal triggerValue) {
		this.triggerValue = triggerValue;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public boolean isAboveCondition() {
		return aboveCondition;
	}

	public void setAboveCondition(boolean aboveCondition) {
		this.aboveCondition = aboveCondition;
	}

	public boolean meet(ContractVO contract) {
		if(contract.getCurrentBar() == null){
			logger.warn("[ConditionVO] current bar of contract: "+contract.getKey()+" is null!");
			return false;
		}
		if(contract.getCurrentBar().getClosePrice() <=1){
			logger.warn("[ConditionVO] current bar is abnormal: "+contract.getKey());
			return false;
		}
		BigDecimal compareField;
		if(StringUtils.equals(this.getType(), PRICE_TYPE)){
			compareField = new BigDecimal(contract.getCurrentBar().getClosePrice());
		} else if(StringUtils.equals(this.getType(), VOL_TYPE)){
			compareField = new BigDecimal(contract.getCurrentBar().getVolume());
		} else {
			return false;
		}
		if(this.aboveCondition){
			return (compareField.compareTo(this.triggerValue) >= 0);
		} else {
			return (compareField.compareTo(this.triggerValue) <= 0);
		}
	}

}
