package de.arm.bot.info;

public enum Direction {

	NORTH,
	EAST,
	SOUTH,
	WEST;
	
	@Override
	public String toString() {
		return name().toLowerCase();
	}
}
