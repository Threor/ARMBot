package de.arm.bot.model;

/**
 * A class representing the player or the bot walking through the maze
 * @author Team ARM
 *
 */
public class Player {

	/**
	 * The x-coordinate the bot currently stands on
	 */
	private int x;
	/**
	 * The y-coordinate the bot currently stands on
	 */
	private int y;
	
	/**
	 * The id of the Player as given by the engine
	 */
	private int id;

	/**
	 * The defaultly used constructor for the class Player
	 * @param x The starting x-coordinate of the bot
	 * @param y The starting y-coordinate of the bot
	 * @param id The given id of the bot
	 */
	public Player(int x, int y, int id) {
		this.x = x;
		this.y = y;
		this.id = id;
	}

	/** Getter for the attribute x
	 * @return The current x-coordinate of the bot
	 */
	public int getX() {
		return x;
	}

	/** Setter for the attribute x
	 * @param x The new x-coordinate of the bot
	 */
	public void setX(int x) {
		this.x = x;
	}

	/** Getter for the attribute y
	 * @return The current y-position of the bot
	 */
	public int getY() {
		return y;
	}

	/** Setter for the attribute y
	 * @param y The new y-coordinate of the bot
	 */
	public void setY(int y) {
		this.y = y;
	}

	/** Getter for the attribute id
	 * @return id The current id of the bot
	 */
	public int getId() {
		return id;
	}
}
