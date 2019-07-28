package de.arm.bot.ki;

import de.arm.bot.info.Action;
import de.arm.bot.info.TurnInfo;
import de.arm.bot.model.Cell;
import de.arm.bot.model.Maze;

import java.util.ArrayList;
import java.util.List;

import static de.arm.bot.info.Command.PUT;
import static de.arm.bot.info.Command.TAKE;
import static de.arm.bot.model.PrimitiveStatus.ENEMY_FORM;
import static de.arm.bot.model.PrimitiveStatus.SHEET;

/**
 * An implementation of the KI used for level 5. Inherits all functionality of level 4.
 * Also adds functionality for working with sheets
 *
 * @author Team ARM
 */
public class LevelFiveKI extends LevelFourKI {

    /**
     * A List of all cells the bot has performed a put action on
     */
    private final List<Cell> alreadyPut;
    /**
     * Indicates whether the bot took a sheet
     */
    private boolean took;
    /**
     * Indicates whether the bot put a sheet down
     */
    private boolean put;

    /**
     * Default constructor for the KI, initializes all fields and sets the current maze
     *
     * @param maze The maze the KI should work on
     */
    public LevelFiveKI(Maze maze) {
        super(maze);
        this.alreadyPut = new ArrayList<>();
    }

    /**
     * Calculates the next move the bot should take.
     *
     * @param turnInfo The information of the current Turn as given by the game
     * @return The calculated Action
     */
    @Override
    public Action calculateMove(TurnInfo turnInfo) {
        //Don't take or put something if the bot is going to finish or if the bot has put on this cell before
        if (alreadyPut.contains(maze.getCurrentCell()) || onFinishWay()) return super.calculateMove(turnInfo);
        //Standing on enemy cell
        if (turnInfo.getCellStatus(null).getStatus() == ENEMY_FORM) {
            //Put if the bot has sheets left
            if (maze.getPlayer().getSheetCount() > 0) {
                put = true;
                return new Action(PUT);
            }
        }
        //Standing on a sheet
        if (turnInfo.getCellStatus(null).getStatus() == SHEET) {
            //Let's see what is under it
            took = true;
            return new Action(TAKE);
        }
        return super.calculateMove(turnInfo);
    }

    /**
     * Processes the given TurnInfo and updates the information.
     *
     * @param turnInfo The information of the current Turn as given by the game
     * @return True if the TurnInfo could be processed successfully
     */
    @Override
    protected boolean processTurnInfo(TurnInfo turnInfo) {
        if (turnInfo.getLastActionResult().isOk()) {
            if (took) {
                took = false;
                maze.getPlayer().addSheet();
            }
            if (put) {
                put = false;
                alreadyPut.add(maze.getCurrentCell());
                maze.getPlayer().removeSheet();
            }
        }
        return super.processTurnInfo(turnInfo);
    }

    /**
     * Processes the given TurnInfo and splices the information into the maze.
     *
     * @param turnInfo The information of the current Turn as given by the game
     * @return The generated Next action
     */
    @Override
    public Action generateNextTurn(TurnInfo turnInfo) {
        if (!turnInfo.getLastActionResult().isOk() && turnInfo.getLastActionResult().getMessage().equalsIgnoreCase("taking"))
            return lastAction;
        return super.generateNextTurn(turnInfo);
    }

    /**
     * Performs a big flood and forgets that he performed a put on any cell
     */
    @Override
    protected void bigFlood() {
        alreadyPut.clear();
        super.bigFlood();
    }
}
