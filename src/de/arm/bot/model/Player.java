package de.arm.bot.model;

import de.arm.bot.io.Output;

/**
 * A class representing the player or the bot walking through the maze
 *
 * @author Team ARM
 */
public class Player {

    /**
     * The id of the Player as given by the engine
     */
    private final int id;
    /**
     * The x-coordinate the bot currently stands on
     */
    private int x;
    /**
     * The y-coordinate the bot currently stands on
     */
    private int y;
    /**
     * The count of sheets the player holds at the moment
     * Introduced in level 5.
     * Set to -1 so it will be ignored in the levels before.
     */
    private int sheetCount = -1;


    /**
     * The default used constructor for the class Player
     *
     * @param x  The starting x-coordinate of the bot
     * @param y  The starting y-coordinate of the bot
     * @param id The given id of the bot
     */
    public Player(int x, int y, int id) {
        this.x = x;
        this.y = y;
        this.id = id;
    }

    /**
     * The constructor used for level 5 for this player
     *
     * @param x          The starting x-coordinate of the bot
     * @param y          The starting y-coordinate of the bot
     * @param id         The given id of the bot
     * @param sheetCount The count of sheets the bot can hold
     */
    public Player(int x, int y, int id, int sheetCount) {
        this(x, y, id);
        this.sheetCount = sheetCount;
    }

    /**
     * Getter for the attribute x
     *
     * @return The current x-coordinate of the bot
     */
    int getX() {
        return x;
    }

    /**
     * Setter for the attribute x
     *
     * @param x The new x-coordinate of the bot
     */
    void setX(int x) {
        this.x = x;
    }

    /**
     * Getter for the attribute y
     *
     * @return The current y-position of the bot
     */
    int getY() {
        return y;
    }

    /**
     * Setter for the attribute y
     *
     * @param y The new y-coordinate of the bot
     */
    void setY(int y) {
        this.y = y;
    }

    /**
     * Getter for the attribute id
     *
     * @return id The current id of the bot
     */
    public int getId() {
        return id;
    }

    /**
     * Getter for the attribute sheetCount
     *
     * @return The sheetCount
     */
    public int getSheetCount() {
        return sheetCount;
    }

    /**
     * Increments the amount of sheets
     */
    public void addSheet() {
        sheetCount++;
    }

    /**
     * Decrements the amount of sheets after putting one down if the bot has sheets left
     */
    public void removeSheet() {
        if (sheetCount <= 0) {
            Output.logDebug("ERR Trying to remove a sheet while having none!");
            return;
        }
        sheetCount--;
    }
}
