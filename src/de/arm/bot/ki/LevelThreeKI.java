package de.arm.bot.ki;

import de.arm.bot.info.Action;
import de.arm.bot.info.TurnInfo;
import de.arm.bot.model.Maze;

public class LevelThreeKI extends LevelTwoKI {

    protected Action lastAction;

    /**
     * Default constructor for the KI, initializes all fields and sets the current maze
     *
     * @param maze The maze the KI should work on
     */
    public LevelThreeKI(Maze maze) {
        super(maze);
    }

    @Override
    public Action generateNextTurn(TurnInfo turnInfo) {
        //maze.logCellsSimple();
        if (!processTurnInfo(turnInfo)) {
            if (turnInfo.getLastActionResult().getMessage().equalsIgnoreCase("talking")) return lastAction;
        }
        lastAction = calculateMove(turnInfo);
        return lastAction;
    }
}
