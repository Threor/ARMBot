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
	
	public Direction getOpposite() {
		switch(this) {
			case NORTH:return SOUTH;
			case EAST:return WEST;
			case SOUTH:return NORTH;
			case WEST:return EAST;
			default:return null;
		}
	}
}
