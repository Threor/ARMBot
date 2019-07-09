package de.arm.bot.model;

public class Player {

	private int x;
	private int y;
	
	private int id;

	public Player(int x, int y, int id) {
		this.x = x;
		this.y = y;
		this.id = id;
	}

	/**
	 * @return x
	 */
	public int getX() {
		return x;
	}

	/**
	 * @param x das zu setzende Objekt x
	 */
	public void setX(int x) {
		this.x = x;
	}

	/**
	 * @return y
	 */
	public int getY() {
		return y;
	}

	/**
	 * @param y das zu setzende Objekt y
	 */
	public void setY(int y) {
		this.y = y;
	}

	/**
	 * @return id
	 */
	public int getId() {
		return id;
	}

	/**
	 * @param id das zu setzende Objekt id
	 */
	public void setId(int id) {
		this.id = id;
	}
	
	
}
