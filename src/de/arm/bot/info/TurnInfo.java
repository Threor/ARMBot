package de.arm.bot.info;

import de.arm.bot.model.PrimitiveStatus;
import de.arm.bot.model.Status;

import java.util.Map;

/**
 * A wrapper class that holds all information given on each turn of the game.
 * These information will be used to calculate the next turn based on the freshly gotten information and the result of the last turn
 *
 * @author Team ARM
 */
public class TurnInfo {

    /**
     * The result of the last performed action
     */
    private final ActionResult lastActionResult;

    /**
     * A map of the status nearby the current cell.
     * A direction of null indicates the current cell
     */
    private final Map<Direction, Status> cellStatus;

    /**
     * Constructor for this class that initializes all fields
     *
     * @param lastActionResult The result of the last action
     * @param cellStatus       The status of nearby cells and the current cell
     */
    public TurnInfo(ActionResult lastActionResult, Map<Direction, Status> cellStatus) {
        this.lastActionResult = lastActionResult;
        this.cellStatus = cellStatus;
    }

    /**
     * Getter for the attribute cellStatus
     *
     * @return The map of the status nearby the current cell.
     */
    public Map<Direction, Status> getCellStatus() {
        return cellStatus;
    }

    /**
     * Finds and gets the status of the cell at the given direction
     *
     * @param direction The direction the cell is on
     * @return The status of the cell
     */
    public Status getCellStatus(Direction direction) {
        return cellStatus.get(direction);
    }

    /**
     * Getter for the attribute lastActionResult
     *
     * @return The result of the last action
     */
    public ActionResult getLastActionResult() {
        return lastActionResult;
    }

    /**
     * Checks if the cellStatus contains at least one entry with the given Status as value
     *
     * @param status The PrimitiveStatus to check for
     * @return True if the cellStatus contains one or more entries with the given PrimitiveStatus as value
     */
    public boolean hasCell(PrimitiveStatus status) {
        return cellStatus.containsValue(new Status(status));
    }

    @Override
    public String toString() {
        return String.format("TurnInfo [lastActionResult=%s, cellStatus=%s]", lastActionResult, cellStatus);
    }

}
