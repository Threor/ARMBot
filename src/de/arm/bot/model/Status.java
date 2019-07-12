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
	
	private Integer additionalInfo;
	
	private Status(boolean dead) {
		this.dead=dead;
	}
	
	private Status(boolean dead, int additionalInfo) {
		this.dead=dead;
		this.additionalInfo=additionalInfo;
	}
	
	public boolean isDead() {
		return dead;
	}
	
	public Integer getAdditionalInfo() {
		return additionalInfo;
	}
	
	public void setAdditionalInfo(Integer additionalInfo) {
		this.additionalInfo = additionalInfo;
	}
	
	

}
