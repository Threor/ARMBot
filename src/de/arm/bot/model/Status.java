package de.arm.bot.model;

public enum Status {

	WALL(true),
	DEAD(true),
	FLOOR(false),
	FINISH(false),
	FORM(false),
	NOT_DISCOVERED(false),
	VISITED(false);
	
	private boolean dead;
	
	private Status(boolean dead) {
		this.dead=dead;
	}
	
	public boolean isDead() {
		return dead;
	}

}
