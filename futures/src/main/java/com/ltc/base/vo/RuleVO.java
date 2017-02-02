package com.ltc.base.vo;

public class RuleVO {

	@Override
	public String toString() {
		return this.condition.toString()+"/ THEN: "+command.toString();
	}

	private ConditionVO condition;
	private CommandVO command;
	private ContractVO contract;

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
