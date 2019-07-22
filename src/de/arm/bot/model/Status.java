package de.arm.bot.model;

/**
 * An enumeration of all status a cell in the maze can have
 *
 * @author Team ARM
 * @see de.arm.bot.model.Cell
 */
public enum Status {

    /**
     * A value representing a wall in the maze
     */
    WALL(true),
    /**
     * A value representing a dead cell (used by the DCA)
     */
    DEAD(true),
    /**
     * A value representing a floor cell in the maze
     */
    FLOOR(false),
    /**
     * A value for the player's finish cell in the maze
     */
    FINISH(false),
    /**
     * A value for one of the player's form cells
     */
    FORM(false),
    /**
     * A value representing a cell that has not been discovered yet
     */
    NOT_DISCOVERED(false),
    /**
     * A value representing an already visited cell
     */
    VISITED(false);

    /**
     * Indicates, whether a cell is dead (should not be walked on) or not
     */
    private boolean dead;
    /**
     * Used for adding additional info (like the form id) to a cell status
     */
    private Integer additionalInfo;

    /**
     * The defaultly used constructor for the enumeration
     *
     * @param dead Indicates wether the cell is dead or not
     */
    Status(boolean dead) {
        this.dead = dead;
    }

    /**
     * Getter for the attribute dead of the status
     *
     * @return True if the cell is dead and False if it is not
     */
    public boolean isDead() {
        return dead;
    }

    /**
     * Getter for the attribute additionalInfo
     *
     * @return The additionalInfo of the status
     */
    public Integer getAdditionalInfo() {
        return additionalInfo;
    }

    /**
     * Setter for the attribute additionalInfo
     *
     * @param additionalInfo The new additionalInfo of the status
     */
    public void setAdditionalInfo(Integer additionalInfo) {
        this.additionalInfo = additionalInfo;
    }


}
