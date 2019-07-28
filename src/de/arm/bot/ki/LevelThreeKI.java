package de.arm.bot.ki;

import de.arm.bot.info.Action;
import de.arm.bot.info.Command;
import de.arm.bot.info.TurnInfo;
import de.arm.bot.model.Maze;

import static de.arm.bot.model.PrimitiveStatus.FORM;

/**
 * An implementation of the KI used for level 3. Inherits all functionality of level 2
 *
 * @author Team ARM
 */
public class LevelThreeKI extends LevelTwoKI {

    /**
     * The last action the bot took. This is used, because this action could fail if the bot is currently talking.
     * If this happens, the action will be repeated
     */
    protected Action lastAction;

    /**
     * Default constructor for the KI, initializes all fields and sets the current maze
     *
     * @param maze The maze the KI should work on
     */
    public LevelThreeKI(Maze maze) {
        super(maze);
    }

    /**
     * Processes the given TurnInfo and splices the information into the maze.
     * If the bot was talking during the last action, the last action will be repeated.
     * Afterwards calculates the next action and returns it
     *
     * @param turnInfo The information of the current Turn as given by the game
     * @return The generated Next action
     */
    @Override
    public Action generateNextTurn(TurnInfo turnInfo) {
        //Last Action failed
        if (!processTurnInfo(turnInfo)) {
            //Bot was talking
            if (turnInfo.getLastActionResult().getMessage().equalsIgnoreCase("talking")) {
                //Only repeat a take action if the bot is still standing on the form
                if (!(lastAction.getCommand() == Command.TAKE && !(turnInfo.getCellStatus(null).getStatus() == FORM)))
                    return lastAction;
            }
        }
        lastAction = calculateMove(turnInfo);
        return lastAction;
    }
}
