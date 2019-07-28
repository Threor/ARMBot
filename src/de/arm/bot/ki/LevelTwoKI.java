package de.arm.bot.ki;

import de.arm.bot.info.Action;
import de.arm.bot.info.Command;
import de.arm.bot.info.Direction;
import de.arm.bot.info.TurnInfo;
import de.arm.bot.io.Output;
import de.arm.bot.model.Cell;
import de.arm.bot.model.Maze;
import de.arm.bot.model.PrimitiveStatus;
import de.arm.bot.model.Status;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import static de.arm.bot.model.PrimitiveStatus.FINISH;
import static de.arm.bot.model.PrimitiveStatus.FORM;

/**
 * An implementation of the KI used for level 2. Inherits all functionality of level 1
 *
 * @author Team ARM
 */
public class LevelTwoKI extends LevelOneKI {

    /**
     * The number of forms to be found. A finish cell contains the number of forms.
     */
    protected int formCount;

    /**
     * The number of forms that have been found.
     */
    protected int foundForms;

    /**
     * The finish cell
     */
    protected Cell finish;

    /**
     * A map for found form cells mapped (formId - cell)
     */
    protected final Map<Integer, Cell> formCells;

    /**
     * Indicates whether a take was performed in the last turn
     */
    private boolean performedTake;

    /**
     * Default constructor for the KI, initializes all fields and sets the current maze
     *
     * @param maze The maze the KI should work om
     */
    public LevelTwoKI(Maze maze) {
        super(maze);
        this.formCount = -1;
        this.formCells = new HashMap<>();
    }

    /**
     * Calculates the next move the bot should take.
     *
     * @param turnInfo The information of the current Turn as given by the game
     * @return The calculated Action
     */
    @Override
    public Action calculateMove(TurnInfo turnInfo) {
        //Found all forms and on FINISH
        if (turnInfo.getCellStatus(null).getStatus() == FINISH && foundForms == formCount)
            return new Action(Command.FINISH);
        //Found all previous forms and on FORM
        if (turnInfo.getCellStatus(null).getStatus() == FORM && turnInfo.getCellStatus(null).getAdditionalInfo() == (foundForms) + 1) {
            performedTake = true;
            pathToTake.clear();
            return new Action(Command.TAKE);
        }
        //Found all forms
        if (onFinishWay()) return navigateToCell(finish);
        //Found all previous forms
        if (formCells.containsKey(foundForms + 1)) return navigateToCell(formCells.get(foundForms + 1));
        //When new forms were found, then go to them first
        return getGOAction();
    }

    /**
     * Indicates whether the bot is on the way towards the finish cell
     *
     * @return True, if the bot is on the specified way
     */
    protected boolean onFinishWay() {
        return foundForms == formCount && finish != null;
    }

    /**
     * Processes the given TurnInfo and updates the information
     *
     * @param turnInfo The information of the current Turn as given by the game
     * @return True if the TurnInfo could be processed successfully
     */
    @Override
    protected boolean processTurnInfo(TurnInfo turnInfo) {
        if (!super.standardProcess(turnInfo)) return false;
        processLevelTwo(turnInfo);
        return true;
    }

    /**
     * The standard Level 2 procedure fo processing information.
     * Saves found finish and form cells
     *
     * @param turnInfo The given TurnInfo
     */
    protected void processLevelTwo(TurnInfo turnInfo) {
        //The bot took something
        if (performedTake) {
            performedTake = false;
            //The Action was successful
            if (turnInfo.getLastActionResult().isOk()&&!(turnInfo.getCellStatus(null).getStatus()==FORM)) {
                foundForms++;
            }
        }
        //The finish cell is nearby
        if (turnInfo.hasCell(FINISH)) {
            //Find the Entry
            Entry<Direction, Status> entry = turnInfo.getCellStatus().entrySet().stream().filter(e -> e.getValue().getStatus() == PrimitiveStatus.FINISH).findAny().orElse(null);
            if (entry == null) {
                Output.logDebug("After finding FINISH in TurnInfo, unable to get FINISH from TurnInfo!\n This should not happen!\n If it does, then you are cursed");
            } else {
                //Remember the form count and the fnish cell
                this.formCount = entry.getValue().getAdditionalInfo();
                this.finish = maze.getCurrentCell().getNeighbour(entry.getKey());
            }
        }
        //One or more form cells are nearby
        if (turnInfo.hasCell(FORM)) {
            //Filter all form cells and remember each one
            turnInfo.getCellStatus().entrySet().stream()
                    .filter(e -> e.getValue().getStatus() == FORM)
                    .forEach(e -> formCells.put(e.getValue().getAdditionalInfo(),e.getKey()==null?maze.getCurrentCell():maze.getCurrentCell().getNeighbour(e.getKey())));
        }
    }

    /**
     * Performs a big flood, but remembers all ways to all found finish and form cells
     */
    @Override
    protected void bigFlood() {
        List<Cell> toExclude = new ArrayList<>();
        for (Cell c : formCells.values()) {
            if (c == null) continue;
            toExclude.addAll(aStar(maze.getCurrentCell(), c));
        }
        if (finish != null) toExclude.addAll(aStar(maze.getCurrentCell(), finish));
        maze.performBigFlood(toExclude);
    }
}
