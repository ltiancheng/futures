package com.ltc.base.vo;

import java.util.Date;

public class RuleVO {

	@Override
	public String toString() {
		return this.condition.toString()+"/ THEN: "+command.toString();
	}

	private ConditionVO condition;
	private CommandVO command;
	private ContractVO contract;
	private boolean triggered = false;
	private Date triggerTime;
	private boolean old = false;

	public boolean isOld() {
		return old;
	}

	public void setOld(boolean old) {
		this.old = old;
	}

	public Date getTriggerTime() {
		return triggerTime;
	}

	public void setTriggerTime(Date triggerTime) {
		this.triggerTime = triggerTime;
	}

	public boolean isTriggered() {
		return triggered;
	}

	public void setTriggered(boolean triggered) {
		this.triggered = triggered;
	}

	public ContractVO getContract() {
		return contract;
	}

	public void setContract(ContractVO contract) {
		this.contract = contract;
	}

	public CommandVO getCommand() {
		return command;
	}

	public void setCommand(CommandVO command) {
		this.command = command;
	}

	public ConditionVO getCondition() {
		return condition;
	}

	public void setCondition(ConditionVO condition) {
		this.condition = condition;
	}
	
}
