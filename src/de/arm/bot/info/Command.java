package de.arm.bot.info;

public enum Command {

	GO,
	TAKE,
	KICK,
	POSITION,
	FINISH;
	
	@Override
	public String toString() {
		return name().toLowerCase();
	}
}
