package com.ltc.base.vo;

import java.math.BigDecimal;

import org.apache.commons.lang.StringUtils;

public class ConditionVO {
	
	public static final String PRICE_TYPE="P";
	public static final String VOL_TYPE="V";
	
	private BigDecimal triggerValue; 
	private String type;
	private boolean aboveCondition;

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
