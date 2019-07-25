package de.arm.bot.model;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

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
    WALL(false,4),
    /**
     * A value representing a floor cell in the maze
     */
    FLOOR(true,1),
    /**
     * A value for the player's finish cell in the maze
     */
    FINISH(true,1),
    /**
     * A value for one of the player's form cells
     */
    FORM(true,1),
    /**
     * A value for one of the enemies form cells
     */
    ENEMY_FORM(true,1),
    /**
     * A value representing a cell that has been blocked by a sheet
     */
    SHEET(true,1),
    /**
     * A value representing a cell that has not been discovered yet
     */
    NOT_DISCOVERED(false,10);

    /**
     * Indicates, whether a cell is navigable (should be walked on) or not
     */
    private boolean navigable;

    private int cost;
    /**
     * Used for adding additional info (like the form id) to a cell status
     */
    private Integer additionalInfo;

    /**
     * The defaultly used constructor for the enumeration
     *
     * @param navigable Indicates whether the bot can navigate over a cell or not
     */
    Status(boolean navigable, int cost) {
        this.navigable = navigable;
        this.cost=cost;
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

    public boolean isNavigable() {
        return navigable;
    }

    public static List<Status> getNavigableStatus() {
        return Arrays.stream(values())
                .filter(Status::isNavigable)
                .collect(Collectors.toList());
    }

    public int getCost() {
        return cost;
    }
}
