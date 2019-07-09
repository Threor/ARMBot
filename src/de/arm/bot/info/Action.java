package de.arm.bot.info;

public class Action {

	Command command;
	
	String argument;

	public Action(Command command, String argument) {
		this.command = command;
		this.argument = argument;
	}
	
	public Action (Command command) {
		this.command=command;
		this.argument="";
	}

	/**
	 * @return command
	 */
	public Command getCommand() {
		return command;
	}

	/**
	 * @param command das zu setzende Objekt command
	 */
	public void setCommand(Command command) {
		this.command = command;
	}

	/**
	 * @return argument
	 */
	public String getArgument() {
		return argument;
	}

	/**
	 * @param argument das zu setzende Objekt argument
	 */
	public void setArgument(String argument) {
		this.argument = argument;
	}
	
	@Override
	public String toString() {
		return String.format("%s %s", command,argument);
	}
	
}
