package com.ltc.base.vo;

public class FullCommandVO {
	private CommandVO command;
	private String contractKey;
	public String getContractKey() {
		return contractKey;
	}
	public void setContractKey(String contractKey) {
		this.contractKey = contractKey;
	}
	public CommandVO getCommand() {
		return command;
	}
	public void setCommand(CommandVO command) {
		this.command = command;
	}
}
